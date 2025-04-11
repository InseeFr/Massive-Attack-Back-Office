package fr.insee.sabianedata.ws.model.queen;

import java.io.File;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.sabianedata.ws.utils.JsonFileToJsonNode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class QueenSurveyUnit extends SurveyUnit {

    public static final String FOLDER = "surveyUnits";

    private JsonNode data;
    private JsonNode comment;
    private JsonNode personalization;
    private JsonNode stateData;

    public QueenSurveyUnit(SurveyUnit surveyUnit) {
        super(surveyUnit.getId(), surveyUnit.getQuestionnaireId(), surveyUnit.getStateDataFile(),
                surveyUnit.getPersonalizationFile(),
                surveyUnit.getDataFile(),
                surveyUnit.getCommentFile());
    }

    public QueenSurveyUnit(QueenSurveyUnit suDto, SurveyUnit su) {
        super(su);
        this.data = suDto.getData();
        this.comment = suDto.getComment();
        this.personalization = suDto.getPersonalization();
        this.stateData = suDto.getStateData();
    }

    public void extractJsonFromFiles(Path folderPath) {
        String finalFolder = folderPath + File.separator + FOLDER;
        File dtodataFile = new File(finalFolder + File.separator + getDataFile());
        File commentFile = new File(finalFolder + File.separator + getCommentFile());
        File personalizationFile = new File(finalFolder + File.separator + getPersonalizationFile());
        // handle previous data structure
        populateStateData(finalFolder);

        setData(JsonFileToJsonNode.getJsonNodeFromFile(dtodataFile));
        setComment(JsonFileToJsonNode.getJsonNodeFromFile(commentFile));
        setPersonalization(JsonFileToJsonNode.getJsonNodeFromFile(personalizationFile));

        // to prevent jsonification to API calls
        setDataFile(null);
        setCommentFile(null);
        setPersonalizationFile(null);
        setStateDataFile(null);
    }

    private void populateStateData(String finalFolder) {
        String sdf = getStateDataFile();
        if (sdf == null || sdf.isEmpty()) {
            setStateData(null);
        } else {
            File stateDataFile = new File(finalFolder + File.separator + getStateDataFile());
            setStateData(JsonFileToJsonNode.getJsonNodeFromFile(stateDataFile));
        }
    }

}
