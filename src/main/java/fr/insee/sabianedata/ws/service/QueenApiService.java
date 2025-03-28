package fr.insee.sabianedata.ws.service;

import fr.insee.sabianedata.ws.config.properties.ApplicationProperties;
import fr.insee.sabianedata.ws.model.queen.NomenclatureDto;
import fr.insee.sabianedata.ws.model.queen.QueenCampaign;
import fr.insee.sabianedata.ws.model.queen.QueenSurveyUnit;
import fr.insee.sabianedata.ws.model.queen.QuestionnaireModelDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueenApiService {

	private final ApplicationProperties applicationProperties;
	private final RestTemplate restTemplate;

	public ResponseEntity<String> postCampaignToApi(QueenCampaign queenCampaign) {
		log.info("Creating Campaign {}", queenCampaign.getId());
		final String apiUri = String.join("/", applicationProperties.questionnaireUrl(), "api/campaigns");
		return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(queenCampaign),
				String.class);
	}

	public ResponseEntity<String> postUeToApi(QueenSurveyUnit queenSurveyUnit,
											  String idCampaign) {
		log.info("Create SurveyUnit {}", queenSurveyUnit.getId());
		final String apiUri = String.format("%s/api/campaign/%s/survey-unit", applicationProperties.questionnaireUrl()
				, idCampaign);
		return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(queenSurveyUnit),
				String.class);
	}

	public ResponseEntity<String> postNomenclaturesToApi(NomenclatureDto nomenclatureDto) {
		log.info("Create nomenclature {}", nomenclatureDto.getId());
		final String apiUri = String.join("/", applicationProperties.questionnaireUrl(), "api/nomenclature");
		log.info("Calling {}", apiUri);
		return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(nomenclatureDto),
				String.class);
	}

	public ResponseEntity<String> postQuestionnaireModelToApi(
			QuestionnaireModelDto questionnaireModelDto) {
		log.info("Create Questionnaire {}", questionnaireModelDto.getIdQuestionnaireModel());
		final String apiUri = String.join("/", applicationProperties.questionnaireUrl(), "api/questionnaire-models");
		return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(questionnaireModelDto),
				String.class);
	}


	public ResponseEntity<String> deleteCampaign(String id) {

		// new CampaignDto with parameter id to send to pearl APi
		final String apiUri = String.format("%s/api/campaign/%s?force=true", applicationProperties.questionnaireUrl(),
				id);
		return restTemplate.exchange(apiUri, HttpMethod.DELETE, new HttpEntity<>(id), String.class);
	}

	public boolean healthCheck() {
		final String apiUri = String.join("/", applicationProperties.questionnaireUrl(), "api/healthcheck");
		return restTemplate.exchange(apiUri, HttpMethod.GET, HttpEntity.EMPTY, String.class)
				.getStatusCode().equals(HttpStatus.OK);

	}

}
