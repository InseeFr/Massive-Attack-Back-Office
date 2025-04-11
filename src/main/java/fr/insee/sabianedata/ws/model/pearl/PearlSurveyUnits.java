package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@JacksonXmlRootElement(localName = "SurveyUnits")
@NoArgsConstructor
@Getter
@Setter
public class PearlSurveyUnits {

    @JacksonXmlProperty(localName = "SurveyUnit")
    @JacksonXmlElementWrapper(useWrapping = false)
    private ArrayList<PearlSurveyUnit> surveyUnits;


}
