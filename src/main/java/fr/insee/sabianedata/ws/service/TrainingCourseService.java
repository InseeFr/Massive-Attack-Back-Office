package fr.insee.sabianedata.ws.service;

import fr.insee.sabianedata.ws.model.massive_attack.*;
import fr.insee.sabianedata.ws.model.pearl.*;
import fr.insee.sabianedata.ws.model.queen.QueenSurveyUnit;
import fr.insee.sabianedata.ws.model.queen.QuestionnaireModel;
import fr.insee.sabianedata.ws.model.queen.QuestionnaireModelDto;
import fr.insee.sabianedata.ws.model.queen.SurveyUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TrainingCourseService {

	public List<MassiveCampaign> generateTrainingCourse(TrainingScenario scenario,
														TrainingConfiguration configuration) {

		return scenario.getCampaigns().stream().map(camp -> {
			try {
				return prepareTrainingCourse(camp, configuration, scenario);

			} catch (Exception e1) {
				log.error("Couldn't create training course {}", camp.getId(), e1);
				return null;
			}
		}).toList();
	}


	private MassiveCampaign prepareTrainingCourse(MassiveCampaign templateCampaign, TrainingConfiguration configuration,
												  TrainingScenario scenario) {
		// extract configuration
		String organisationUnitId = configuration.organisationUnitId();
		Long referenceDate = configuration.referenceDate();

		MassiveCampaign generatedCampaign = new MassiveCampaign();

		// 1 : update campaigns Label and make unique campaignId -> campaign.id_I/M_OU_date_scenarLabel
		String newCampaignId = String.join("_", templateCampaign.getId(), scenario.getType().toString().substring(0, 1),
				organisationUnitId, referenceDate.toString(), configuration.campaignLabel());
		generatedCampaign.updateCampaignsId(newCampaignId);
		generatedCampaign.updateLabel(configuration.scenarioLabel());

		// 2 : change visibility with OU in configuration
		List<Visibility> newVisibilities = updateVisibilities(templateCampaign, referenceDate, organisationUnitId);
		generatedCampaign.getPearlCampaign().setVisibilities(newVisibilities);


		// 6 Queen : make unique questionnaireId and map oldQuestId to new questModels
		HashMap<String, String> questionnaireIdMapping = new HashMap<>();
		List<QuestionnaireModelDto> newQuestionnaireModels =
				templateCampaign.getQueenCampaign().getQuestionnaireModels().stream().map(qm -> {
					String initQuestionnaireModelId = qm.getIdQuestionnaireModel();
					String newQuestionnaireModelId = String.join("_",
							initQuestionnaireModelId,
							organisationUnitId,
							referenceDate.toString());
					questionnaireIdMapping.put(initQuestionnaireModelId, newQuestionnaireModelId);
					QuestionnaireModelDto clonedQM = qm.deepClone();
					clonedQM.setIdQuestionnaireModel(newQuestionnaireModelId);
					return clonedQM;
				}).toList();

		List<String> newQuestionnaireIds = newQuestionnaireModels.stream()
				.map(QuestionnaireModel::getIdQuestionnaireModel).toList();

		generatedCampaign.getQueenCampaign().setQuestionnaireIds(newQuestionnaireIds);


		// 7 : generate pearl survey-units for interviewers
		// big fancy method dispatching survey-unit to trainees
		List<MassiveSurveyUnit> dispatchedSurveyUnits =
				generateSurveyUnits(templateCampaign.getSurveyUnits(), newCampaignId, configuration,
						templateCampaign.getAssignments(), scenario.getType(), questionnaireIdMapping);
		generatedCampaign.setSurveyUnits(dispatchedSurveyUnits);
		// extract assignments after dispatch
		List<Assignment> distributedAssignments = extractDistributedAssignements(dispatchedSurveyUnits);
		generatedCampaign.setAssignments(distributedAssignments);

		return generatedCampaign;

	}


	private List<Assignment> extractDistributedAssignements(
			List<MassiveSurveyUnit> distributedSurveyUnits
	) {

		return distributedSurveyUnits.stream()
				.map(su -> new Assignment(su.getId(), su.getPearlSurveyUnit().getInterviewerId()))
				.toList();

	}

	/**
	 * Take a SurveyUnit `template`,and return a clone of it updated with other params
	 *
	 * @param surveyUnit         survey-unit to update
	 * @param interviewerId      interviewerId to assign
	 * @param campaignId         new campaignId
	 * @param organisationUnitId new organisationalUnit id
	 * @param referenceDate      reference date modifier
	 * @param newQuestionnaireId new questionnaire id
	 */
	private MassiveSurveyUnit updateSurveyUnit(MassiveSurveyUnit surveyUnit, String interviewerId, String campaignId,
											   String organisationUnitId, Long referenceDate,
											   String newQuestionnaireId) {
		// to keep same id in  pearl and queen APIs
		String newId = UUID.randomUUID().toString();
		PearlSurveyUnit pearlSurveyUnit = updatePearlSurveyUnit(surveyUnit.getPearlSurveyUnit(), newId, interviewerId,
				campaignId, organisationUnitId, referenceDate);
		QueenSurveyUnit queenSurveyUnit = updateQueenSurveyUnit(surveyUnit.getQueenSurveyUnit(), newId,
				newQuestionnaireId);
		return new MassiveSurveyUnit(newId, pearlSurveyUnit, queenSurveyUnit);

	}

	private PearlSurveyUnit updatePearlSurveyUnit(
			PearlSurveyUnit initialSurveyUnit, String newId,
			String interviewerId, String campaignId,
			String organisationUnitId, Long referenceDate) {

		PearlSurveyUnit newSu = new PearlSurveyUnit(
				initialSurveyUnit);
		newSu.setInterviewerId(interviewerId);
		newSu.setCampaign(campaignId);
		newSu.setOrganizationUnitId(organisationUnitId);
		newSu.setId(newId);

		// states
		initialSurveyUnit.getStates()
				.stream()
				.map(state -> new SurveyUnitStateDto(state, referenceDate))
				.forEach(newSu.getStates()::add);

		// contactOutcome
		ContactOutcomeDto newContactOutcomeDto = initialSurveyUnit.getContactOutcome() != null
				? new ContactOutcomeDto(initialSurveyUnit.getContactOutcome(), referenceDate)
				: null;
		newSu.setContactOutcome(newContactOutcomeDto);

		// contactAttempts
		List<ContactAttemptDto> newCAs = Optional.ofNullable(initialSurveyUnit.getContactAttempts())
				.orElse(new ArrayList<>()).stream()
				.map(ca -> new ContactAttemptDto(ca, referenceDate, ca.getMedium()))
				.toList();
		ArrayList<ContactAttemptDto> newContactAttempts = new ArrayList<>(newCAs);
		newSu.setContactAttempts(newContactAttempts);

		return newSu;
	}

	/**
	 * Take a Queen survey-unit and return a clone of it updated with other params
	 *
	 * @param initialSurveyUnit  surveyUnit
	 * @param newId              new surveyUnit Id
	 * @param newQuestionnaireId new questionnaireId
	 * @return the updated clone
	 */
	private QueenSurveyUnit updateQueenSurveyUnit(QueenSurveyUnit initialSurveyUnit, String newId,
												  String newQuestionnaireId) {
		SurveyUnit newSu = new SurveyUnit(newId, newQuestionnaireId, initialSurveyUnit.getStateDataFile());
		return new QueenSurveyUnit(initialSurveyUnit, newSu);
	}


	/**
	 * This is the core method : it takes as input the initial survey-units
	 * and generate a copy for each interviewer
	 * <p>
	 * In Interviewer typed scenario : all Survey-Units are cloned </br>
	 * In Manager typed scenario : Survey-Units keep their initial assigned interviewer
	 *
	 * @param surveyUnits            to use as base
	 * @param campaignId             campaign id
	 * @param configuration          date modification, new organisation-unit id,trainees for the training session
	 * @param assignments            initial assignments (for MANAGER case)
	 * @param type                   MANAGER or INTERVIEWER
	 * @param questionnaireIdMapping map linking initial questionnaire ids to generated ids
	 * @return dispatched new survey-units
	 */
	private List<MassiveSurveyUnit> generateSurveyUnits(
			List<MassiveSurveyUnit> surveyUnits,
			String campaignId,
			TrainingConfiguration configuration,
			List<Assignment> assignments,

			ScenarioType type, HashMap<String, String> questionnaireIdMapping) {
		String organisationUnitId = configuration.organisationUnitId();
		Long referenceDate = configuration.referenceDate();

		return switch (type) {
			// for each trainee => dispatch each TrainingCourse Survey-unit
			case INTERVIEWER -> configuration.trainees().stream()
					.flatMap(interviewerId -> surveyUnits.stream()
							.map(surveyUnit -> {
										String questId = surveyUnit.getQueenSurveyUnit().getQuestionnaireId();
										String newQuestionnaireId = questionnaireIdMapping.get(questId);
										return updateSurveyUnit(surveyUnit,
												interviewerId,
												campaignId,
												organisationUnitId,
												referenceDate,
												newQuestionnaireId);
									}
							)
					)
					.toList();

			case MANAGER -> {
				Map<String, String> assignMap = assignments.stream()
						.collect(Collectors.toMap(Assignment::getSurveyUnitId, Assignment::getInterviewerId));

				yield surveyUnits.stream()
						.map(surveyUnit -> {
							String questId = surveyUnit.getQueenSurveyUnit().getQuestionnaireId();
							String newQuestionnaireId = questionnaireIdMapping.get(questId);

							return updateSurveyUnit(surveyUnit,
									assignMap.get(surveyUnit.getId()),
									campaignId,
									organisationUnitId,
									referenceDate,
									newQuestionnaireId);
						})
						.toList();
			}
		};
	}


	private List<Visibility> updateVisibilities(MassiveCampaign campaign, Long referenceDate, String organisationUnitId) {
		return campaign.getPearlCampaign().getVisibilities().stream()
				.map(Visibility::new)
				.peek(visibility -> {
					visibility.updateDatesWithReferenceDate(referenceDate);
					visibility.setOrganizationalUnit(organisationUnitId);
				}).toList();
	}

}
