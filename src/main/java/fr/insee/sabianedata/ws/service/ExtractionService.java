package fr.insee.sabianedata.ws.service;

import fr.insee.sabianedata.ws.model.pearl.Assignment;
import fr.insee.sabianedata.ws.model.pearl.PearlCampaign;
import fr.insee.sabianedata.ws.model.pearl.PearlSurveyUnit;
import fr.insee.sabianedata.ws.model.queen.NomenclatureDto;
import fr.insee.sabianedata.ws.model.queen.QueenCampaign;
import fr.insee.sabianedata.ws.model.queen.QueenSurveyUnit;
import fr.insee.sabianedata.ws.model.queen.QuestionnaireModelDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExtractionService {
    private final QueenExtractEntities queenExtractEntities;
    private final PearlExtractEntities pearlExtractEntities;

    public QueenCampaign extractQueenCampaign(File queenFodsInput) throws Exception {
        return queenExtractEntities.getQueenCampaignFromFods(queenFodsInput);
    }

    public List<QuestionnaireModelDto> extractQuestionnaires(File queenFodsInput, Path queenFolder) throws Exception {
        return queenExtractEntities
                .getQueenQuestionnaireModelsDtoFromFods(queenFodsInput, queenFolder);
    }

    public List<NomenclatureDto> extractNomenclatures(File queenFodsInput, Path queenFolder) throws Exception {
        return queenExtractEntities
                .getQueenNomenclaturesDtoFromFods(queenFodsInput, queenFolder);
    }

    public List<QueenSurveyUnit> extractQueenSurveyUnits(File queenFodsInput, Path queenFolder) throws Exception {
        return queenExtractEntities.getQueenSurveyUnitsFromFods(queenFodsInput,
                queenFolder);
    }

    public PearlCampaign extractPearlCampaign(File pearlFodsInput) throws Exception {
        return pearlExtractEntities
                .getPearlCampaignFromFods(pearlFodsInput);
    }

    public List<PearlSurveyUnit> extractPearlSurveyUnits(File pearlFodsInput) throws Exception {
        return pearlExtractEntities
                .getPearlSurveyUnitsFromFods(pearlFodsInput);
    }

    public List<Assignment> extractAssignments(File pearlFodsInput) throws Exception {
        return pearlExtractEntities.getAssignementsFromFods(pearlFodsInput);
    }


}
