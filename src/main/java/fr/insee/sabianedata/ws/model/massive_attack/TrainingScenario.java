package fr.insee.sabianedata.ws.model.massive_attack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TrainingScenario {

	private List<MassiveCampaign> campaigns;
	private ScenarioType type;
	private String label;

}
