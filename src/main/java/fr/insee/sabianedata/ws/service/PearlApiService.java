package fr.insee.sabianedata.ws.service;

import fr.insee.sabianedata.ws.model.massive_attack.OrganisationUnitDto;
import fr.insee.sabianedata.ws.model.massive_attack.PearlUser;
import fr.insee.sabianedata.ws.model.pearl.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PearlApiService {

	@Value("${fr.insee.sabianedata.pearl-api.url}")
	private String pearlApiUrl;

	private final RestTemplate restTemplate;

	public ResponseEntity<String> postCampaignToApi(HttpServletRequest request, PearlCampaign pearlCampaign) {
		log.info("Creating Campaign{}", pearlCampaign.getCampaign());
		final String apiUri = pearlApiUrl.concat("/api/campaign");
		HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(pearlCampaign, httpHeaders),
				String.class);
	}

	public ResponseEntity<String> postUesToApi(HttpServletRequest request, List<PearlSurveyUnit> surveyUnits) {
		log.info("Create SurveyUnits ");
		final String apiUri = pearlApiUrl.concat("/api/survey-units");
		HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(surveyUnits, httpHeaders),
				String.class);
	}

	public ResponseEntity<String> postInterviewersToApi(HttpServletRequest request,
														List<InterviewerDto> interviewers) {
		log.info("Create interviewers");
		final String apiUri = pearlApiUrl.concat("/api/interviewers");
		HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(interviewers, httpHeaders),
				String.class);
	}

	public ResponseEntity<String> postUsersToApi(HttpServletRequest request, List<UserDto> users, String ouId) {
		log.info("Try to create users with id {}", users.stream().map(UserDto::getId).toList());
		final String apiUri = String.join("/", pearlApiUrl, "api/organization-unit", ouId, "users");
		HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(users, httpHeaders), String.class);
	}

	public ResponseEntity<String> postAssignmentsToApi(HttpServletRequest request, List<Assignment> assignments) {
		log.info("Create assignements");
		final String apiUri = pearlApiUrl.concat("/api/survey-units/interviewers");
		HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(assignments, httpHeaders),
				String.class);
	}

	private HttpHeaders createSimpleHeadersAuth(HttpServletRequest request) {
		String authTokenHeader = request.getHeader("Authorization");
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
		if (!StringUtils.isBlank(authTokenHeader)) {
			httpHeaders.set("Authorization", authTokenHeader);
		}
		return httpHeaders;
	}

	public OrganisationUnitDto getUserOrganizationUnit(HttpServletRequest request) {
		final String apiUri = pearlApiUrl.concat("/api/user");
		HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		try {

			ResponseEntity<PearlUser> userResponse = restTemplate.exchange(apiUri, HttpMethod.GET,
					new HttpEntity<>(httpHeaders), PearlUser.class);
			if (userResponse.getStatusCode() == HttpStatus.OK && userResponse.getBody() != null) {
				return userResponse.getBody().getOrganisationUnit();
			}
		} catch (Exception e) {
			log.error("Can't retrieve user organisational-unit", e);
			return null;
		}
		return null;
	}

	public List<Campaign> getCampaigns(HttpServletRequest request, boolean admin) {
		final String apiUri = pearlApiUrl.concat("/api/campaigns");
		final String adminApiUri = pearlApiUrl.concat("/api/admin/campaigns");
		HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		log.info("Trying to get campaigns list");
		ResponseEntity<Campaign[]> campaignsResponse = restTemplate.exchange(admin ? adminApiUri : apiUri,
				HttpMethod.GET, new HttpEntity<>(httpHeaders), Campaign[].class);
		if (campaignsResponse.getStatusCode() == HttpStatus.OK && campaignsResponse.getBody() != null) {
			log.info("API call for campaigns is OK");
			return Arrays.asList(campaignsResponse.getBody());
		}
		log.warn("Can't get Campaigns list");
		return new ArrayList<>();
	}

	public ResponseEntity<String> deleteCampaign(HttpServletRequest request, String id) {
		log.info("pearl service : delete");
		final String apiUri = String.join("/", pearlApiUrl, "api/campaign", id).concat("?force=true");
		HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return restTemplate.exchange(apiUri, HttpMethod.DELETE, new HttpEntity<>(id, httpHeaders), String.class);
	}

	public boolean healthCheck(HttpServletRequest request) {
		final String apiUri = pearlApiUrl.concat("/api/healthcheck");
		HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return restTemplate.exchange(apiUri, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class).getStatusCode().equals(HttpStatus.OK);

	}

	public List<OrganisationUnitDto> getAllOrganizationUnits(HttpServletRequest request) {
		final String apiUri = pearlApiUrl.concat("/api/organization-units");
		HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		log.info("Trying to get all organisation units");
		ResponseEntity<OrganisationUnitDto[]> campaignsResponse = restTemplate.exchange(apiUri, HttpMethod.GET,
				new HttpEntity<>(httpHeaders), OrganisationUnitDto[].class);
		if (campaignsResponse.getStatusCode() == HttpStatus.OK && campaignsResponse.getBody() != null) {
			log.info("API call for all organisation units is OK");
			return Arrays.asList(campaignsResponse.getBody());
		} else {
			log.warn("Can't get all OUs");
		}
		return new ArrayList<>();
	}

}
