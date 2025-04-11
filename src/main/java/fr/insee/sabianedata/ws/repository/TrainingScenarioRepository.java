package fr.insee.sabianedata.ws.repository;

import fr.insee.sabianedata.ws.model.massive_attack.TrainingScenario;

import java.util.List;
import java.util.Optional;

public interface TrainingScenarioRepository {

    Optional<TrainingScenario> getTrainingScenarioById(String trainingScenarioId) ;

    List<TrainingScenario> getTrainingScenarioIdsAndType();

}

