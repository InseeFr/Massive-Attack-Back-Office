package fr.insee.sabianedata.ws.model.queen;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JacksonXmlRootElement(localName = "SurveyUnits")
@NoArgsConstructor
@Getter
@Setter
public class SurveyUnitsList {

    @JacksonXmlProperty(localName = "SurveyUnit")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<SurveyUnit> surveyUnits;

}
