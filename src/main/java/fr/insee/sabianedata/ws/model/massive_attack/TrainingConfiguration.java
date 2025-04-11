package fr.insee.sabianedata.ws.model.massive_attack;

import java.util.List;

public record TrainingConfiguration(String campaignLabel, String organisationUnitId, Long referenceDate,
									List<String> trainees, String scenarioLabel) {
}
