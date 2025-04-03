package fr.insee.sabianedata.ws.model.queen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@JacksonXmlRootElement(localName = "Campaign")
@NoArgsConstructor
@Getter
@Setter
public class QueenCampaign {

    @JacksonXmlProperty(localName = "Id")
    private String id;

    @JacksonXmlProperty(localName = "Label")
    private String label;

    @JacksonXmlProperty(localName = "questionnaireIds")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<String> questionnaireIds;

    @JsonIgnore
    private List<QuestionnaireModelDto> questionnaireModels;

    @JsonIgnore
    private List<NomenclatureDto> nomenclatures;

    @JacksonXmlProperty(localName = "Metadata")
    private MetadataDto metadata;

    @Override
    public String toString() {
        return "CampaignDto{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", questionnairesId=" + questionnaireIds +
                ", metadata=" + metadata +
                '}';
    }

    public QueenCampaign deepClone(){
        QueenCampaign clone = new QueenCampaign();
        clone.setId(id);
        clone.setLabel(label);
        clone.setQuestionnaireIds(questionnaireIds);
        return clone;
    }

}
