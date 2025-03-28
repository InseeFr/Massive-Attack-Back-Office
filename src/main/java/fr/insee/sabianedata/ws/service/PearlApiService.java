package fr.insee.sabianedata.ws.service;

import fr.insee.sabianedata.ws.config.properties.ApplicationProperties;
import fr.insee.sabianedata.ws.model.massive_attack.OrganisationUnitDto;
import fr.insee.sabianedata.ws.model.massive_attack.PearlUser;
import fr.insee.sabianedata.ws.model.pearl.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PearlApiService {

	private final ApplicationProperties applicationProperties;
	private final RestTemplate restTemplate;

	public ResponseEntity<String> postCampaignToApi(PearlCampaign pearlCampaign) {
		log.info("Creating Campaign{}", pearlCampaign.getCampaign());
		final String apiUri = applicationProperties.managementUrl().concat("/api/campaign");
		return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(pearlCampaign),
				String.class);
	}

	public ResponseEntity<String> postUesToApi(List<PearlSurveyUnit> surveyUnits) {
		log.info("Create SurveyUnits ");
		final String apiUri = applicationProperties.managementUrl().concat("/api/survey-units");
		return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(surveyUnits),
				String.class);
	}

	public ResponseEntity<String> postInterviewersToApi(
			List<InterviewerDto> interviewers) {
		log.info("Create interviewers");
		final String apiUri = applicationProperties.managementUrl().concat("/api/interviewers");

		return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(interviewers),
				String.class);
	}

	public ResponseEntity<String> postUsersToApi(List<UserDto> users, String ouId) {
		log.info("Try to create users with id {}", users.stream().map(UserDto::getId).toList());
		final String apiUri = String.join("/", applicationProperties.managementUrl(), "api/organization-unit", ouId, "users");

		return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(users), String.class);
	}

	public ResponseEntity<String> postAssignmentsToApi(List<Assignment> assignments) {
		log.info("Create assignments");
		final String apiUri = applicationProperties.managementUrl().concat("/api/survey-units/interviewers");
		return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(assignments),
				String.class);
	}


	public OrganisationUnitDto getUserOrganizationUnit() {
		final String apiUri = applicationProperties.managementUrl().concat("/api/user");

		try {

			ResponseEntity<PearlUser> userResponse = restTemplate.exchange(apiUri, HttpMethod.GET,
					HttpEntity.EMPTY, PearlUser.class);
			if (userResponse.getStatusCode() == HttpStatus.OK && userResponse.getBody() != null) {
				return userResponse.getBody().getOrganisationUnit();
			}
		} catch (Exception e) {
			log.error("Can't retrieve user organisational-unit", e);
			return null;
		}
		return null;
	}

	public List<Campaign> getCampaigns(boolean admin) {
		final String apiUri = applicationProperties.managementUrl().concat("/api/campaigns");
		final String adminApiUri = applicationProperties.managementUrl().concat("/api/admin/campaigns");
		log.info("Trying to get campaigns list");
		ResponseEntity<Campaign[]> campaignsResponse = restTemplate.exchange(admin ? adminApiUri : apiUri,
				HttpMethod.GET, HttpEntity.EMPTY, Campaign[].class);
		if (campaignsResponse.getStatusCode() == HttpStatus.OK && campaignsResponse.getBody() != null) {
			log.info("API call for campaigns is OK");
			return Arrays.asList(campaignsResponse.getBody());
		}
		log.warn("Can't get Campaigns list");
		return new ArrayList<>();
	}

	public ResponseEntity<String> deleteCampaign(String id) {
		log.info("pearl service : delete");
		final String apiUri = String.join("/", applicationProperties.managementUrl(), "api/campaign", id).concat("?force=true");
		return restTemplate.exchange(apiUri, HttpMethod.DELETE, new HttpEntity<>(id), String.class);
	}

	public boolean healthCheck() {
		final String apiUri = applicationProperties.managementUrl().concat("/api/healthcheck");
		return restTemplate.exchange(apiUri, HttpMethod.GET, HttpEntity.EMPTY, String.class).getStatusCode().equals(HttpStatus.OK);

	}

	public List<OrganisationUnitDto> getAllOrganizationUnits() {
		final String apiUri = applicationProperties.managementUrl().concat("/api/organization-units");
		log.info("Trying to get all organisation units");
		ResponseEntity<OrganisationUnitDto[]> campaignsResponse = restTemplate.exchange(apiUri, HttpMethod.GET,
				HttpEntity.EMPTY, OrganisationUnitDto[].class);
		if (campaignsResponse.getStatusCode() == HttpStatus.OK && campaignsResponse.getBody() != null) {
			log.info("API call for all organisation units is OK");
			return Arrays.asList(campaignsResponse.getBody());
		} else {
			log.warn("Can't get all OUs");
		}
		return new ArrayList<>();
	}

}
