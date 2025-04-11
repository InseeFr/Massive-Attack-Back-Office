package fr.insee.sabianedata.ws.controller;

import fr.insee.sabianedata.ws.model.ResponseModel;
import fr.insee.sabianedata.ws.model.massive_attack.OrganisationUnitDto;
import fr.insee.sabianedata.ws.model.massive_attack.TrainingScenario;
import fr.insee.sabianedata.ws.model.pearl.Campaign;
import fr.insee.sabianedata.ws.service.MassiveAttackService;
import fr.insee.sabianedata.ws.service.PearlApiService;
import fr.insee.sabianedata.ws.service.UtilsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Tag(name = "Massive Attack : Post data to Pearl and Queen APIs")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/massive-attack/api")
public class MassiveAttackController {

	private final MassiveAttackService massiveAttackService;
	private final PearlApiService pearlApiService;
	private final UtilsService utilsService;

	@Operation(summary = "Return list of available training courses")
	@GetMapping("training-course-scenario")
	public ResponseEntity<List<TrainingScenario>> getTrainingScenariiTitles() {
		try {
			List<TrainingScenario> scenarii = massiveAttackService.getTrainingScenariosTitles();
			return new ResponseEntity<>(scenarii, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Can't get training scenarii titles");
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(summary = "Create a training course")
	@PostMapping(value = "training-course", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseModel> generateTrainingCourse(@RequestParam(value =
			"campaignId") String scenarioId, @RequestParam(value = "campaignLabel") String scenarioLabel,
																@RequestParam(value = "organisationUnitId") String organisationUnitId, @RequestParam(value = "dateReference") Long dateReference, @RequestParam(value = "interviewers", defaultValue = "") List<String> trainees) {
		String encodedScenarioId = URLEncoder.encode(scenarioId, StandardCharsets.UTF_8);
		String encodedScenarioLabel = URLEncoder.encode(scenarioLabel, StandardCharsets.UTF_8);
		String encodedOrganisationUnitId = URLEncoder.encode(organisationUnitId, StandardCharsets.UTF_8);

		log.info("USER : {} | create scenario {}  -> {}", utilsService.getRequesterId(), encodedScenarioId,
				scenarioLabel);
		ResponseModel result = massiveAttackService.generateTrainingScenario(encodedScenarioId, encodedScenarioLabel,
				encodedOrganisationUnitId, dateReference, trainees);
		return result.isSuccess() ? ResponseEntity.ok().body(result) : ResponseEntity.badRequest().body(result);
	}

	@Operation(summary = "Return user OrganisationalUnit")
	@GetMapping(value = "user/organisationUnit", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OrganisationUnitDto> getUserOrganisationalUnit() {
		log.info("USER : {} | get organization unit ", utilsService.getRequesterId());
		OrganisationUnitDto ou = pearlApiService.getUserOrganizationUnit();
		if (ou == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find user organisationUnit");
		}
		return ResponseEntity.ok().body(ou);

	}

	@Operation(summary = "Delete a campaign")
	@DeleteMapping(path = "campaign/{id}")
	public ResponseEntity<String> deleteCampaignById(
													 @PathVariable(value = "id") String campaignId) {
		log.warn("USER : {} | delete campaign {}", utilsService.getRequesterId(), campaignId);
		return massiveAttackService.deleteCampaign(campaignId);
	}

	@Operation(summary = "Get list of training courses")
	@GetMapping(path = "/training-courses")
	public ResponseEntity<List<Campaign>> getTrainingSessions(@RequestParam(value =
			"admin", defaultValue = "false") boolean admin) {
		List<Campaign> pearlCampaigns = pearlApiService.getCampaigns(admin);

		log.info("USER: {} | get campaigns ", utilsService.getRequesterId());
		return new ResponseEntity<>(pearlCampaigns, HttpStatus.OK);
	}

	@Operation(summary = "Return all OrganisationalUnits")
	@GetMapping(value = "organisation-units", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OrganisationUnitDto>> getAllOrganisationalUnits() {
		log.info("USER : {} | get organization units ", utilsService.getRequesterId());
		List<OrganisationUnitDto> ous = pearlApiService.getAllOrganizationUnits();
		if (ous.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().body(ous);
	}
}
