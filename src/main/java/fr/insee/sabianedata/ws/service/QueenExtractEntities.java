package fr.insee.sabianedata.ws.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import fr.insee.sabianedata.ws.model.queen.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueenExtractEntities {

    private final QueenTransformService queenTransformService;

    private final XmlMapper xmlMapper = new XmlMapper();

    public QueenCampaign getQueenCampaignFromXMLFile(File file) throws IOException {
        return xmlMapper.readValue(file, QueenCampaign.class);
    }

    public QueenCampaign getQueenCampaignFromFods(File fods) throws Exception {
        File file = queenTransformService.getQueenCampaign(fods);
        return getQueenCampaignFromXMLFile(file);
    }

    public List<QueenSurveyUnit> getQueenSurveyUnitsFromFods(File fods, Path folderPath) throws Exception {
        File file = queenTransformService.getQueenSurveyUnits(fods);
        SurveyUnitsList surveyUnits = xmlMapper.readValue(file, SurveyUnitsList.class);
        return surveyUnits.getSurveyUnits().stream().map(s -> {
            QueenSurveyUnit suDto = new QueenSurveyUnit(s);
            suDto.extractJsonFromFiles(folderPath);
            return suDto;
        }).toList();
    }

    private List<QuestionnaireModel> getQueenQuestionnaireModelsFromFods(File fods) throws Exception {
        File file = queenTransformService.getQueenQuestionnaires(fods);
        QuestionnaireModelsList questionnaireModels = xmlMapper.readValue(file, QuestionnaireModelsList.class);
        return questionnaireModels != null && questionnaireModels.getQuestionnaireModels() != null ?
                questionnaireModels.getQuestionnaireModels() : List.of();
    }

    public List<QuestionnaireModelDto> getQueenQuestionnaireModelsDtoFromFods(File fods, Path folderPath) throws Exception {
        List<QuestionnaireModel> questionnaireModels = getQueenQuestionnaireModelsFromFods(fods);
        return questionnaireModels.stream().map(q -> new QuestionnaireModelDto(q, folderPath)).toList();
    }

    public List<Nomenclature> getQueenNomenclatureFromFods(File fods) throws Exception {
        ArrayList<Nomenclature> lists = new ArrayList<>();
        File file = queenTransformService.getQueenNomenclatures(fods);
        NomenclaturesList nomenclatures = xmlMapper.readValue(file, NomenclaturesList.class);
        return nomenclatures != null && nomenclatures.getNomenclatures() != null ? nomenclatures.getNomenclatures() :
                lists;
    }

    public List<NomenclatureDto> getQueenNomenclaturesDtoFromFods(File fods, Path folderPath) throws Exception {
        List<Nomenclature> nomenclatures = getQueenNomenclatureFromFods(fods);
        return nomenclatures.stream().map(n -> new NomenclatureDto(n, folderPath)).toList();
    }

}
