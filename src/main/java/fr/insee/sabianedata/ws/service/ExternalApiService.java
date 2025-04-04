package fr.insee.sabianedata.ws.service;

import fr.insee.sabianedata.ws.model.massive_attack.MassiveCampaign;
import fr.insee.sabianedata.ws.model.massive_attack.MassiveSurveyUnit;
import fr.insee.sabianedata.ws.model.massive_attack.OrganisationUnitDto;
import fr.insee.sabianedata.ws.model.pearl.Campaign;
import fr.insee.sabianedata.ws.model.pearl.InterviewerDto;
import fr.insee.sabianedata.ws.model.pearl.PearlSurveyUnit;
import fr.insee.sabianedata.ws.model.pearl.UserDto;
import fr.insee.sabianedata.ws.model.queen.QueenSurveyUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalApiService {

	private final PearlApiService pearlApiService;
	private final QueenApiService queenApiService;

	public MassiveCampaign postTrainingCourse(MassiveCampaign trainingCourse) {
		boolean pearlSuccess = postTrainingCourseToManagementApi(trainingCourse);
		boolean queenSuccess = postTrainingCourseToQuestionnaireApi(trainingCourse);
		return pearlSuccess && queenSuccess ? trainingCourse : null;
	}

	private boolean postTrainingCourseToManagementApi(MassiveCampaign trainingCourse) {
		boolean pearlCampaignSuccess = runWithErrorLogging(
				() -> pearlApiService.postCampaignToApi(trainingCourse.getPearlCampaign()),
				String.format("Error during creation campaign : %s", trainingCourse.getId())
		);

		List<PearlSurveyUnit> pearlSurveyUnitsToPost =
				trainingCourse.getSurveyUnits().stream().map(MassiveSurveyUnit::getPearlSurveyUnit).toList();
		boolean pearlSurveyUnitSuccess = runWithErrorLogging(
				() -> pearlApiService.postUesToApi(pearlSurveyUnitsToPost),
				"Error during creation of surveyUnits"
		);

		boolean assignmentSuccess = runWithErrorLogging(
				() -> pearlApiService.postAssignmentsToApi(trainingCourse.getAssignments()),
				"Error during creation of assignments"
		);

		log.info("Campaign: {}, SurveyUnits: {}, Assignments: {}", pearlCampaignSuccess, pearlSurveyUnitSuccess,
				assignmentSuccess);
		return pearlCampaignSuccess && pearlSurveyUnitSuccess && assignmentSuccess;

	}

	private boolean postTrainingCourseToQuestionnaireApi(MassiveCampaign trainingCourse) {
		// extract main thread context for parallel stream usage
		var securityContext = SecurityContextHolder.getContext();


		log.info("Trying to post {} nomenclatures", trainingCourse.getQueenCampaign().getNomenclatures().size());
		long createdNomenclatures = trainingCourse.getQueenCampaign().getNomenclatures().parallelStream()
				.filter(n -> secureParallelCallWithContext(
						() -> queenApiService.postNomenclaturesToApi(n),
						n.getId(),
						"POST nomenclature-%s failed",
						securityContext
				))
				.count();

		log.info("Trying to post {} questionnaires",
				trainingCourse.getQueenCampaign().getQuestionnaireModels().size());
		long createdQuestionnaires = trainingCourse.getQueenCampaign().getQuestionnaireModels().parallelStream()
				.filter(q -> secureParallelCallWithContext(
						() -> queenApiService.postQuestionnaireModelToApi(q),
						q.getIdQuestionnaireModel(),
						"POST questionnaire-%s failed",
						securityContext
				))
				.count();


		log.info("Trying to post campaign");
		boolean queenCampaignSuccess = runWithErrorLogging(
				() -> queenApiService.postCampaignToApi(trainingCourse.getQueenCampaign()),
				String.format("Error during creation campaign : %s", trainingCourse.getId())
		);


		List<QueenSurveyUnit> queenSurveyUnitsToPost =
				trainingCourse.getSurveyUnits().stream().map(MassiveSurveyUnit::getQueenSurveyUnit).toList();

		log.info("Trying to post {} queen survey-units", queenSurveyUnitsToPost.size());
		long createdQueenSurveyUnits = queenSurveyUnitsToPost.parallelStream()
				.filter(su -> secureParallelCallWithContext(
						() -> queenApiService.postUeToApi(su, trainingCourse.getId()),
						su.getId(),
						"POST surveyUnit-%s failed",
						securityContext
				))
				.count();

		log.info("Nomenclatures: {}/{} , Questionnaires: {}/{} , SurveyUnits: {}/{} , Campaign: {}",
				createdNomenclatures,
				trainingCourse.getQueenCampaign().getNomenclatures().size(),
				createdQuestionnaires,
				trainingCourse.getQueenCampaign().getQuestionnaireModels().size(),
				createdQueenSurveyUnits,
				queenSurveyUnitsToPost.size(),
				queenCampaignSuccess
		);


		return queenCampaignSuccess && createdNomenclatures == trainingCourse.getQueenCampaign().getNomenclatures().size()
				&& createdQuestionnaires == trainingCourse.getQueenCampaign().getQuestionnaireModels().size()
				&& createdQueenSurveyUnits == queenSurveyUnitsToPost.size();
	}


	public boolean checkUsers(List<String> users) {

		OrganisationUnitDto ou = pearlApiService.getUserOrganizationUnit();
		if (ou == null) {
			log.warn("Can't get organizationUnit of caller");
			return false;
		}

		UserDto validUser = new UserDto();
		ArrayList<UserDto> userList = new ArrayList<>();
		userList.add(validUser);
		validUser.setFirstName("FirstName");
		validUser.setLastName("LastName");
		return users.stream().map(user -> {
			validUser.setId(user);
			try {
				ResponseEntity<String> postResponse = pearlApiService.postUsersToApi(userList,
						ou.getId());
				log.info("User {} created", user);
				return postResponse;

			} catch (RestClientException e) {
				log.info("User {} already present", user);
				log.debug(e.getMessage());

				return new ResponseEntity<>(HttpStatus.OK);
			}

		}).allMatch(response -> response.getStatusCode().is2xxSuccessful());

	}

	public ResponseEntity<String> deleteCampaign(String id) {
		List<Campaign> pearlCampaigns = pearlApiService.getCampaigns(true);
		if (pearlCampaigns.stream().noneMatch(camp -> camp.getId().equals(id))) {
			log.error("DELETE campaign with id {} resulting in 404 because it does not exists", id);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		ResponseEntity<String> pearlResponse = pearlApiService.deleteCampaign(id);
		ResponseEntity<String> queenResponse = queenApiService.deleteCampaign(id);
		log.info("DELETE campaign with id {} : pearl={} / queen={}", id,
				pearlResponse.getStatusCode(), queenResponse.getStatusCode());
		return ResponseEntity.ok().build();

	}

	public boolean checkInterviewers(List<String> interviewers) {
		InterviewerDto validInterviewer = new InterviewerDto();
		ArrayList<InterviewerDto> interviewerList = new ArrayList<>();
		interviewerList.add(validInterviewer);
		validInterviewer.setFirstName("FirstName");
		validInterviewer.setLastName("LastName");
		validInterviewer.setEmail("firstname.lastname@valid.net");
		validInterviewer.setPhoneNumber("+33000000000");
		validInterviewer.setTitle("MISTER");
		return interviewers.stream().map(inter -> {
			validInterviewer.setId(inter);
			try {
				ResponseEntity<String> postResponse = pearlApiService.postInterviewersToApi(
						interviewerList);
				log.info("Interviewer {} created", inter);
				return postResponse;
			} catch (RestClientException e) {
				log.info("Interviewer {} already present.", inter);
				log.debug(e.getMessage());

				return new ResponseEntity<>(HttpStatus.OK);
			}

		}).allMatch(response -> response.getStatusCode().is2xxSuccessful());

	}

	private boolean secureParallelCallWithContext(
			Runnable action,
			String entityId,
			String errorFormat,
			SecurityContext context
	) {
		SecurityContextHolder.setContext(context);
		try {
			action.run();
			return true;
		} catch (Exception e) {
			String errorMessage = String.format(errorFormat, entityId);
			log.error(errorMessage, e);
			return false;
		}
	}


	private boolean runWithErrorLogging(Runnable action, String errorMessage) {
		try {
			action.run();
			return true;
		} catch (Exception e) {
			log.error(errorMessage);
			log.error(e.getMessage(), e);
			return false;
		}
	}

}
