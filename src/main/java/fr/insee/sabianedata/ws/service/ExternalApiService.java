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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalApiService {

    private final PearlApiService pearlApiService;
    private final QueenApiService queenApiService;

    public MassiveCampaign postTrainingCourse(MassiveCampaign trainingCourse, HttpServletRequest request) {

        boolean pearlCampaignSuccess = false;
        boolean pearlSurveyUnitSuccess = false;
        boolean assignementSuccess = false;

        log.info("Trying to post pearl campaign");
        try {
            pearlApiService.postCampaignToApi(request, trainingCourse.getPearlCampaign());
            pearlCampaignSuccess = true;
        } catch (Exception e) {
            log.error("Error during creation campaign : {}", trainingCourse.getId());
            log.error(e.getMessage());
        }

        List<PearlSurveyUnit> pearlSurveyUnitsToPost =
                trainingCourse.getSurveyUnits().stream().map(MassiveSurveyUnit::getPearlSurveyUnit).toList();
        log.info("Trying to post {}  pearl surveyUnits", pearlSurveyUnitsToPost.size());

        try {
            pearlApiService.postUesToApi(request, pearlSurveyUnitsToPost);
            pearlSurveyUnitSuccess = true;
        } catch (Exception e) {
            log.error("Error during creation of surveyUnits");
            log.error(e.getMessage());
        }
        log.info("Trying to post {} assignments", trainingCourse.getAssignments().size());
        try {
            pearlApiService.postAssignmentsToApi(request, trainingCourse.getAssignments());
            assignementSuccess = true;
        } catch (Exception e) {
            log.error("Error during creation of assignments");
            log.error(e.getMessage());
        }
        boolean pearlSuccess = pearlCampaignSuccess && pearlSurveyUnitSuccess && assignementSuccess;
        String pearlMessage = String.format("Campaign : %b, SurveyUnits: %b, Assignements: %b",
                pearlCampaignSuccess, pearlSurveyUnitSuccess, assignementSuccess);
        log.info(pearlMessage);

        // POST queen entities
        long nomenclaturesSuccess;
        long questionnairesSuccess;
        long queenSurveyUnitsSuccess;
        boolean queenCampaignSuccess = false;

        log.info("Trying to post {} nomenclatures", trainingCourse.getQueenCampaign().getNomenclatures().size());
        nomenclaturesSuccess = trainingCourse.getQueenCampaign().getNomenclatures().stream().parallel().filter(n -> {
            try {
                queenApiService.postNomenclaturesToApi(request, n);
                return true;
            } catch (Exception e) {
                log.error("Error during creation of nomenclature : {}", n.getId());
                log.error(e.getMessage());
                return false;
            }
        }).count();

        log.info("Trying to post {} questionnaires", trainingCourse.getQueenCampaign().getQuestionnaireModels().size());
        questionnairesSuccess = trainingCourse.getQueenCampaign().getQuestionnaireModels().stream().parallel().filter(q -> {
            try {
                queenApiService.postQuestionnaireModelToApi(request, q);
                return true;
            } catch (Exception e) {
                log.error("Error during creation of questionnaire : {}",
                        q.getIdQuestionnaireModel());
                log.error(e.getMessage());
                return false;
            }
        }).count();

        log.info("Trying to post campaign");
        try {
            queenApiService.postCampaignToApi(request, trainingCourse.getQueenCampaign());
            queenCampaignSuccess = true;
        } catch (Exception e) {
            log.error("Error during creation of campaignDto : {}", trainingCourse.getId());
            log.error(e.getMessage());
        }
        List<QueenSurveyUnit> queenSurveyUnitsToPost =
                trainingCourse.getSurveyUnits().stream().map(MassiveSurveyUnit::getQueenSurveyUnit).toList();

        log.info("Trying to post {} queen survey-units", queenSurveyUnitsToPost.size());
        queenSurveyUnitsSuccess = queenSurveyUnitsToPost.stream().parallel().filter(su -> {
            try {
                queenApiService.postUeToApi(request, su, trainingCourse.getId());
                return true;
            } catch (Exception e) {
                log.error("Error during creation of surveyUnit : {}", su.getId());
                log.error(e.getMessage());
                return false;
            }
        }).count();

        boolean queenSuccess = queenCampaignSuccess && nomenclaturesSuccess == trainingCourse.getQueenCampaign().getNomenclatures().size()
                && questionnairesSuccess == trainingCourse.getQueenCampaign().getQuestionnaireModels().size()
                && queenSurveyUnitsSuccess == queenSurveyUnitsToPost.size();
        String queenMessage = String.format(
                "Nomenclatures: %d/%d, Questionnaires: %d/%d, SurveyUnits: %d/%d, Campaign: %b",
                nomenclaturesSuccess, trainingCourse.getQueenCampaign().getNomenclatures().size(), questionnairesSuccess,
                trainingCourse.getQueenCampaign().getQuestionnaireModels().size(), queenSurveyUnitsSuccess,
                queenSurveyUnitsToPost.size(), queenCampaignSuccess);

        log.info(queenMessage);

        return pearlSuccess && queenSuccess ? trainingCourse : null;
    }

    public boolean checkUsers(List<String> users, HttpServletRequest request) {

        OrganisationUnitDto ou = pearlApiService.getUserOrganizationUnit(request);
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
                ResponseEntity<String> postResponse = pearlApiService.postUsersToApi(request, userList,
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

    public ResponseEntity<String> deleteCampaign(HttpServletRequest request, String id) {
        List<Campaign> pearlCampaigns = pearlApiService.getCampaigns(request, true);
        if (pearlCampaigns.stream().noneMatch(camp -> camp.getId().equals(id))) {
            log.error("DELETE campaign with id {} resulting in 404 because it does not exists", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ResponseEntity<String> pearlResponse = pearlApiService.deleteCampaign(request, id);
        ResponseEntity<String> queenResponse = queenApiService.deleteCampaign(request, id);
        log.info("DELETE campaign with id {} : pearl={} / queen={}", id,
                pearlResponse.getStatusCode(), queenResponse.getStatusCode());
        return ResponseEntity.ok().build();

    }

    public boolean checkInterviewers(List<String> interviewers, HttpServletRequest request) {
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
                ResponseEntity<String> postResponse = pearlApiService.postInterviewersToApi(request,
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

}
