package fr.insee.sabianedata.ws.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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


	private void setupTempFolders() throws IOException {
		tempFolder = Files.createTempDirectory("folder-").toFile();
		tempScenariiFolder = new File(tempFolder, "scenarii");

		if (!tempScenariiFolder.mkdirs()) {
			log.error("Couldn't create temporary scenarii folder.");
			throw new IOException("Failed to create temporary scenarii folder.");
		}

		File scenariosFolder = resourceLoader.getResource("classpath:scenarii").getFile();
		if (!scenariosFolder.exists()) {
			log.error("Scenarii folder not found in classpath.");
			throw new IOException("Scenarii folder not found in classpath.");
		}

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

		log.info("creating scenario from {}", scenarioDirectory.getName());
		File infoFile = new File(scenarioDirectory, "info.json");

		TrainingScenario trainingScenario;
		try (InputStream inputStream = new FileInputStream(infoFile)) {
			trainingScenario = objectMapper.readValue(inputStream, TrainingScenario.class);
		} catch (IOException e) {
			log.warn("Unable to load TrainingScenario from {}", infoFile, e);
			throw new Exception("Unable to load TrainingScenario");
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
			throw new Exception("Error when processing scenario " + scenarioDirectory.getAbsolutePath());
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
			throw new RuntimeException("Campaign extraction failed", e);
		}
	}

	private List<PearlSurveyUnit> extractPearlSurveyUnits(File pearlFodsInput) {
		try {
			return extractionService.extractPearlSurveyUnits(pearlFodsInput);
		} catch (Exception e) {
			log.error("Error with SU extraction in {}", pearlFodsInput.getAbsolutePath());
			throw new RuntimeException("Pearl survey-units extraction failed", e);
		}
	}

	private List<Assignment> extractAssignments(File pearlFodsInput) {
		try {
			return extractionService.extractAssignments(pearlFodsInput);
		} catch (Exception e) {
			throw new RuntimeException("Pearl assignments extraction failed", e);
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
			throw new RuntimeException("Queen Campaign extraction failed", e);
		}
	}

	private List<QueenSurveyUnit> extractQueenSurveyUnits(Path queenFolder, File queenSourceFile) {
		try {
			return extractionService.extractQueenSurveyUnits(queenSourceFile,
					queenFolder);
		} catch (Exception e) {
			throw new RuntimeException("Queen survey-units extraction failed", e);
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
	public List<String> getTrainingScenarioIds() {
		return scenarioMap.keySet().stream().toList();
	}
}

