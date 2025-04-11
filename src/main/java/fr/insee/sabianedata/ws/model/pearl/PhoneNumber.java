package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JacksonXmlRootElement(localName = "PhoneNumber")
@NoArgsConstructor
@Getter
@Setter
public class PhoneNumber {

    @JacksonXmlProperty(localName = "Source")
    private String source;
    @JacksonXmlProperty(localName = "Favorite")
    private boolean favorite;
    @JacksonXmlProperty(localName = "Number")
    private String number;

}
