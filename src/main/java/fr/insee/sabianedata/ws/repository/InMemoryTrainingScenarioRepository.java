package fr.insee.sabianedata.ws.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.sabianedata.ws.config.properties.ApplicationProperties;
import fr.insee.sabianedata.ws.controller.exception.TrainingScenarioLoadingException;
import fr.insee.sabianedata.ws.model.massive_attack.MassiveCampaign;
import fr.insee.sabianedata.ws.model.massive_attack.MassiveSurveyUnit;
import fr.insee.sabianedata.ws.model.massive_attack.TrainingScenario;
import fr.insee.sabianedata.ws.model.pearl.Assignment;
import fr.insee.sabianedata.ws.model.pearl.PearlCampaign;
import fr.insee.sabianedata.ws.model.pearl.PearlSurveyUnit;
import fr.insee.sabianedata.ws.model.queen.NomenclatureDto;
import fr.insee.sabianedata.ws.model.queen.QueenCampaign;
import fr.insee.sabianedata.ws.model.queen.QueenSurveyUnit;
import fr.insee.sabianedata.ws.model.queen.QuestionnaireModelDto;
import fr.insee.sabianedata.ws.service.ExtractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryTrainingScenarioRepository implements TrainingScenarioRepository {

	private final ResourceLoader resourceLoader;

	private final Map<String, TrainingScenario> scenarioMap = new HashMap<>();
	private final ExtractionService extractionService;
	private final ApplicationProperties applicationProperties;

	private File tempFolder;
	private File tempScenariiFolder;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@PostConstruct
	public void init() {
		try {
			setupTempFolders();
			loadScenarios();
			log.debug("Init loading finished: {} scenarios loaded.", scenarioMap.size());
		} catch (IOException e) {
			log.error("Critical error during initialization of scenarios. Shutting down.", e);
			throw new IllegalStateException("Initialization failed due to IO error", e);
		} catch (Exception e) {
			log.error("Unexpected error during initialization. Shutting down.", e);
			throw new IllegalStateException("Initialization failed due to an unexpected error", e);
		}
	}

	@PreDestroy
	private void cleanup() {
		boolean result = FileSystemUtils.deleteRecursively(tempFolder);
		log.debug("Clean-up result : {}", result);
	}

	/**
	 * Prepares a temporary folder structure to hold scenarii files by:
	 * <ul>
	 *   <li>Creating a base temp folder if it doesn't exist</li>
	 *   <li>Creating a "scenarii" subfolder within the temp folder</li>
	 *   <li>Verifying that the scenarii folder resides within the temp folder (to prevent symlink or directory traversal attacks)</li>
	 *   <li>Copying scenarii resources from the classpath into the temporary scenarii folder</li>
	 * </ul>
	 *
	 * @throws IOException if any folder cannot be created, is not writable,
	 *                     if a symlink/directory traversal issue is detected,
	 *                     or if classpath scenarii resources are missing
	 */
	private void setupTempFolders() throws IOException {
		tempFolder = new File(applicationProperties.tempFolder());

		// Ensure the base temp folder exists and is writable
		if (!tempFolder.exists() && !tempFolder.mkdir()) {
			throw new IOException("Could not create temp folder: " + tempFolder);
		}
		if (!tempFolder.isDirectory() || !tempFolder.canWrite()) {
			throw new IOException("Configured temp folder is not writable: " + tempFolder);
		}

		// Create the scenarii subfolder inside the temp folder
		tempScenariiFolder = new File(tempFolder, "scenarii");
		if (!tempScenariiFolder.exists() && !tempScenariiFolder.mkdirs()) {
			log.error("Couldn't create temporary scenarii folder.");
			throw new IOException("Failed to create temporary scenarii folder.");
		}

		// Resolve the canonical (real) paths to detect symlink attacks
		Path realBasePath = tempFolder.toPath().toRealPath(); // resolves symlinks in temp folder
		Path scenariiPath = tempScenariiFolder.toPath().toRealPath(); // resolves symlinks in scenarii subfolder

		// Ensure the scenarii folder is truly a subdirectory of the base temp folder
		// This prevents path tricks or symlink-based attacks that could redirect outside of temp
		if (!scenariiPath.startsWith(realBasePath)) {
			throw new IOException("Potential directory traversal or symlink attack.");
		}

		// Load scenarii files from the classpath
		File scenariosFolder = resourceLoader.getResource("classpath:scenarii").getFile();
		if (!scenariosFolder.exists()) {
			log.error("Scenarii folder not found in classpath.");
			throw new IOException("Scenarii folder not found in classpath.");
		}

		// Safely copy classpath scenarii files into the validated temp/scenarii folder
		FileUtils.copyDirectory(scenariosFolder, tempScenariiFolder);
	}

	private void loadScenarios() {
		File[] scenarioFolders = tempScenariiFolder.listFiles();
		checkScenarioFolder(scenarioFolders);

		List<TrainingScenario> scenarios = Arrays.stream(scenarioFolders)
				.map(file -> {
					try {
						return createTrainingScenario(file);
					} catch (Exception e) {
						throw new IllegalStateException(String.format("Couldn't load scenario %s", file.getName()));
					}
				}).toList();

		scenarios.forEach(scenario -> scenarioMap.put(scenario.getLabel(), scenario));
	}

	private void checkScenarioFolder(File[] scenarioFolders) {
		if (scenarioFolders == null || scenarioFolders.length == 0) {
			log.error("No scenarios found in the temporary scenarii folder.");
			throw new IllegalStateException("No scenarios found in the temporary folder.");
		}
	}


	private TrainingScenario createTrainingScenario(File scenarioDirectory) throws Exception {
		if (scenarioDirectory == null) {
			throw new IllegalArgumentException("Scenario directory cannot be null");
		}

		if (!scenarioDirectory.getCanonicalPath().startsWith(tempScenariiFolder.getCanonicalPath())) {
			throw new SecurityException("Scenario directory is outside allowed base path.");
		}

		log.info("creating scenario from {}", scenarioDirectory.getName());
		File infoFile = new File(scenarioDirectory, "info.json");

		TrainingScenario trainingScenario;
		try (InputStream inputStream = new FileInputStream(infoFile)) {
			trainingScenario = objectMapper.readValue(inputStream, TrainingScenario.class);
		} catch (IOException e) {
			log.warn("Unable to load TrainingScenario from {}", infoFile, e);
			throw new TrainingScenarioLoadingException("Unable to load TrainingScenario",e);
		}

		// for each Scenario sub-folder
		try (Stream<Path> paths = Files.list(scenarioDirectory.toPath())) {
			List<File> directories = paths
					.filter(Files::isDirectory)
					.map(Path::toFile)
					.toList();

			List<MassiveCampaign> campaigns = directories.stream().map(
					this::extractMassiveCampaign
			).toList();

			trainingScenario.setCampaigns(campaigns);

		} catch (RuntimeException e) {
			log.warn("Error when processing campaigns", e);
			throw new TrainingScenarioLoadingException("Error when processing scenario " + scenarioDirectory.getAbsolutePath(),e);
		}

		return trainingScenario;

	}

	private MassiveCampaign extractMassiveCampaign(File campaignDirectory) {

		// extract Pearl entities
		File pearlSourceFile = new File(campaignDirectory, "pearl/pearl_campaign.fods");
		PearlCampaign pearlCampaign = extractPearlCampaign(pearlSourceFile);
		List<PearlSurveyUnit> pearlSurveyUnits = extractPearlSurveyUnits(pearlSourceFile);
		List<Assignment> assignments = extractAssignments(pearlSourceFile);

		// extract Queen entities
		Path queenFolder = new File(campaignDirectory, "queen").toPath();
		File queenSourceFile = new File(queenFolder.toFile(), "queen_campaign.fods");
		QueenCampaign queenCampaign = extractQueenCampaign(queenFolder, queenSourceFile);
		List<QueenSurveyUnit> queenSurveyUnits = extractQueenSurveyUnits(queenFolder, queenSourceFile);

		// merge pearl and queen into MassiveSurveyUnits
		List<MassiveSurveyUnit> surveyUnits = mergePearlAndQueenSurveyUnits(pearlSurveyUnits, queenSurveyUnits);

		// wrap pearl and queen campaign together for easier id handling
		return new MassiveCampaign(pearlCampaign, queenCampaign, surveyUnits, assignments);

	}


	private PearlCampaign extractPearlCampaign(File pearlSourceFile) {
		try {
			return extractionService.extractPearlCampaign(pearlSourceFile);
		} catch (Exception e) {
			log.warn("Error when extracting campaign from {}", pearlSourceFile.getAbsolutePath(), e);
			throw new TrainingScenarioLoadingException("Campaign extraction failed", e);
		}
	}

	private List<PearlSurveyUnit> extractPearlSurveyUnits(File pearlFodsInput) {
		try {
			return extractionService.extractPearlSurveyUnits(pearlFodsInput);
		} catch (Exception e) {
			log.error("Error with SU extraction in {}", pearlFodsInput.getAbsolutePath());
			throw new TrainingScenarioLoadingException("Pearl survey-units extraction failed", e);
		}
	}

	private List<Assignment> extractAssignments(File pearlFodsInput) {
		try {
			return extractionService.extractAssignments(pearlFodsInput);
		} catch (Exception e) {
			throw new TrainingScenarioLoadingException("Pearl assignments extraction failed", e);
		}
	}


	private QueenCampaign extractQueenCampaign(Path queenFolder, File queenSourceFile) {
		try {
			QueenCampaign queenCampaign = extractionService.extractQueenCampaign(queenSourceFile);
			List<QuestionnaireModelDto> questionnaireModels = extractionService
					.extractQuestionnaires(queenSourceFile, queenFolder);
			queenCampaign.setQuestionnaireModels(questionnaireModels);
			List<NomenclatureDto> nomenclatures = extractionService
					.extractNomenclatures(queenSourceFile, queenFolder);
			queenCampaign.setNomenclatures(nomenclatures);
			return queenCampaign;
		} catch (Exception e) {
			log.warn("Error when extracting queen campaign from {}", queenFolder.toAbsolutePath());
			throw new TrainingScenarioLoadingException("Queen Campaign extraction failed", e);
		}
	}

	private List<QueenSurveyUnit> extractQueenSurveyUnits(Path queenFolder, File queenSourceFile) {
		try {
			return extractionService.extractQueenSurveyUnits(queenSourceFile,
					queenFolder);
		} catch (Exception e) {
			throw new TrainingScenarioLoadingException("Queen survey-units extraction failed", e);
		}

	}

	private List<MassiveSurveyUnit> mergePearlAndQueenSurveyUnits(List<PearlSurveyUnit> pearlUnits,
																  List<QueenSurveyUnit> queenUnits) {
		// Create a map of QueenSurveyUnit by their id for quick lookup
		Map<String, QueenSurveyUnit> queenUnitMap = queenUnits.stream()
				.collect(Collectors.toMap(QueenSurveyUnit::getId, queenSu -> queenSu));

		// Map each PearlSurveyUnit to a MassiveSurveyUnit by finding the matching QueenSurveyUnit by id
		return pearlUnits.stream()
				.map(pearlSu -> {
					QueenSurveyUnit queenSu = queenUnitMap.get(pearlSu.getDisplayName());
					return new MassiveSurveyUnit(pearlSu.getId(), pearlSu, queenSu); // Create MassiveSurveyUnit
				})
				.toList();

	}


	/////////////////////////////

	@Override
	public Optional<TrainingScenario> getTrainingScenarioById(String trainingScenarioId) {

		if (!scenarioMap.containsKey(trainingScenarioId)) {
			return Optional.empty();
		}
		return Optional.of(scenarioMap.get(trainingScenarioId));
	}

	@Override
	public List<TrainingScenario> getTrainingScenarioIdsAndType() {
		return scenarioMap.values().stream().map(scenario -> new TrainingScenario(null, scenario.getType(),
				scenario.getLabel())).toList();
	}
}

