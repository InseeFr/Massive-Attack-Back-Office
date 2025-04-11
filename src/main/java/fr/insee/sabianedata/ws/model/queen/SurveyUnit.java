package fr.insee.sabianedata.ws.model.queen;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JacksonXmlRootElement(localName = "SurveyUnit")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SurveyUnit {

    @JacksonXmlProperty(localName = "Id")
    private String id;

    @JacksonXmlProperty(localName = "QuestionnaireId")
    private String questionnaireId;

    @JacksonXmlProperty(localName = "StateDataFile")
    private String stateDataFile;

    @JacksonXmlProperty(localName = "PersonalizationFile")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String personalizationFile;

    @JacksonXmlProperty(localName = "DataFile")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dataFile;

    @JacksonXmlProperty(localName = "CommentFile")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String commentFile;

    public SurveyUnit(String id, String questionnaireId, String stateDataFile) {
        this.id = id;
        this.questionnaireId = questionnaireId;
        this.stateDataFile = stateDataFile;
    }

    public SurveyUnit(SurveyUnit su) {
        this(su.getId(), su.getQuestionnaireId(), su.getStateDataFile(), su.getDataFile(), su.getCommentFile(),
                su.getPersonalizationFile());
    }

}
