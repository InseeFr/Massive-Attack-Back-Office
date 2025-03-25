package fr.insee.sabianedata.ws.repository;

import fr.insee.sabianedata.ws.model.massive_attack.TrainingScenario;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;

public interface TrainingScenarioRepository {

    Optional<TrainingScenario> getTrainingScenarioById(String trainingScenarioId) throws NotFoundException;

    List<TrainingScenario> getTrainingScenarioIdsAndType();

}

