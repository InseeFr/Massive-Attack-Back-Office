package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import fr.insee.sabianedata.ws.utils.DateParser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JacksonXmlRootElement(localName = "State")
@NoArgsConstructor
@Getter
@Setter
public class SurveyUnitStateDto {

    @JacksonXmlProperty(localName = "Value")
    private String type;
    @JacksonXmlProperty(localName = "Date")
    private String dateString;

    private Long date;

    public SurveyUnitStateDto(SurveyUnitStateDto su, Long reference) throws IllegalArgumentException {
        this.type = su.getType();
        this.date = DateParser.relativeDateParse(su.getDateString(), reference);
    }

}
