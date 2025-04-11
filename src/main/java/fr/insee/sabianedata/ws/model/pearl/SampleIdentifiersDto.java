package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

@JacksonXmlRootElement(localName = "SampleIdentifiers")
@Getter
@Setter
public class SampleIdentifiersDto {

    @JacksonXmlProperty(localName = "Bs")
    private String bs;
    @JacksonXmlProperty(localName = "Ec")
    private String ec;
    @JacksonXmlProperty(localName = "Le")
    private String le;
    @JacksonXmlProperty(localName = "Noi")
    private String noi;
    @JacksonXmlProperty(localName = "Numfa")
    private String numfa;
    @JacksonXmlProperty(localName = "Rges")
    private String rges;
    @JacksonXmlProperty(localName = "Ssech")
    private String ssech;
    @JacksonXmlProperty(localName = "Nolog")
    private String nolog;
    @JacksonXmlProperty(localName = "Nole")
    private String nole;
    @JacksonXmlProperty(localName = "Autre")
    private String autre;
    @JacksonXmlProperty(localName = "Nograp")
    private String nograp;

}
