package fr.insee.sabianedata.ws.model.queen;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JacksonXmlRootElement(localName = "QuestionnaireModels")
@NoArgsConstructor
@Getter
@Setter
public class QuestionnaireModelsList {

    @JacksonXmlProperty(localName = "QuestionnaireModel")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<QuestionnaireModel> questionnaireModels;

}
