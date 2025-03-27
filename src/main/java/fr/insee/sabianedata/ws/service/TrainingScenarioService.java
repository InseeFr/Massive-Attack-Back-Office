package fr.insee.sabianedata.ws.service;

import fr.insee.sabianedata.ws.model.massive_attack.TrainingScenario;
import fr.insee.sabianedata.ws.repository.TrainingScenarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainingScenarioService {

	private final TrainingScenarioRepository trainingScenarioRepository;

	public Optional<TrainingScenario> getTrainingScenarioById(String scenarioId) {
		return trainingScenarioRepository.getTrainingScenarioById(scenarioId);
	}

	public List<TrainingScenario> getAllTrainingScenarioTitles() {
		return trainingScenarioRepository.getTrainingScenarioIdsAndType();
	}
}
