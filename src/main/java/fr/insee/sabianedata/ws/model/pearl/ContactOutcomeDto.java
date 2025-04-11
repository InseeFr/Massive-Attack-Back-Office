package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import fr.insee.sabianedata.ws.utils.DateParser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JacksonXmlRootElement(localName = "ContactOutcome")
@NoArgsConstructor
@Getter
@Setter
public class ContactOutcomeDto {

    @JacksonXmlProperty(localName = "Value")
    private String type;
    @JacksonXmlProperty(localName = "AttemptsNumber")
    private String attemptsNumber;
    @JacksonXmlProperty(localName = "Date")
    private String dateString;

    private Long date;
    private int totalNumberOfContactAttempts;

    public ContactOutcomeDto(ContactOutcomeDto co, Long reference) throws IllegalArgumentException {
        this.type = co.getType();
        this.dateString = co.getDateString();
        this.date = DateParser.relativeDateParse(co.getDateString(), reference);
        this.totalNumberOfContactAttempts = Integer.parseInt(co.getAttemptsNumber());
    }

}
