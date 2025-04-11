package fr.insee.sabianedata.ws.service;

import fr.insee.sabianedata.ws.model.ResponseModel;
import fr.insee.sabianedata.ws.model.massive_attack.MassiveCampaign;
import fr.insee.sabianedata.ws.model.massive_attack.ScenarioType;
import fr.insee.sabianedata.ws.model.massive_attack.TrainingConfiguration;
import fr.insee.sabianedata.ws.model.massive_attack.TrainingScenario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MassiveAttackService {

	private final ExternalApiService externalApiService;
	private final TrainingCourseService trainingCourseService;
	private final TrainingScenarioService trainingScenarioService;

	private void rollBackOnFail(List<String> ids) {
		log.warn("Roll back : DELETE following campaigns {}", ids);
		ids.forEach(externalApiService::deleteCampaign);
	}

	public ResponseEntity<String> deleteCampaign(String id) {
		String encodedId = URLEncoder.encode(id, StandardCharsets.UTF_8);
		return externalApiService.deleteCampaign(encodedId);
	}

	public List<TrainingScenario> getTrainingScenariosTitles() {
		return trainingScenarioService.getAllTrainingScenarioTitles();
	}

	/**
	 * Main method applying a configuration to a TrainingCourse
	 *
	 * @param scenarioId         scenario Id
	 * @param scenarioLabel      label to be applied
	 * @param organisationUnitId survey-units OUid
	 * @param referenceDate      reference date for opening status of campaign
	 * @param trainees           list of trainees
	 */
	public ResponseModel generateTrainingScenario(String scenarioId, String scenarioLabel,
												  String organisationUnitId,
												  Long referenceDate,
												  List<String> trainees) {

		Optional<TrainingScenario> scenarioOpt = trainingScenarioService.getTrainingScenarioById(scenarioId);
		if (scenarioOpt.isEmpty()) {
			return new ResponseModel(false, String.format("Scenario %s is not present", scenarioId));
		}
		TrainingScenario scenario = scenarioOpt.get();
		ScenarioType scenarioType = scenario.getType();

		if (scenarioType == ScenarioType.INTERVIEWER && !externalApiService.checkInterviewers(trainees)) {
			return new ResponseModel(false, "Error when checking interviewers");
		}
		if (scenarioType == ScenarioType.MANAGER && !externalApiService.checkUsers(trainees)) {
			return new ResponseModel(false, "Error when checking users");
		}

		TrainingConfiguration trainingConfiguration = new TrainingConfiguration(scenarioLabel, organisationUnitId,
				referenceDate, trainees, scenario.getLabel());

		List<MassiveCampaign> trainingCourse = trainingCourseService.generateTrainingCourse(scenario,
				trainingConfiguration);

		if (trainingCourse.contains(null)) {
			rollBackOnFail(trainingCourse.stream().filter(Objects::nonNull).map(MassiveCampaign::getId)
					.toList());
			return new ResponseModel(false, "Error when loading campaigns");
		}

		boolean success = trainingCourse.stream()
				.map(externalApiService::postTrainingCourse).noneMatch(Objects::isNull);

		if (!success) {
			rollBackOnFail(trainingCourse.stream().map(MassiveCampaign::getId)
					.toList());
			return new ResponseModel(false, "Error when posting campaigns");
		}
		return new ResponseModel(true, "Training scenario generated");
	}


}