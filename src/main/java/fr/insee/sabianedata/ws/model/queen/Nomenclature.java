package fr.insee.sabianedata.ws.model.queen;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JacksonXmlRootElement(localName = "Nomenclature")
@NoArgsConstructor
@Getter
@Setter
public class Nomenclature {

    @JacksonXmlProperty(localName = "Id")
    private String id;

    @JacksonXmlProperty(localName = "Label")
    private String label;

    @JacksonXmlProperty(localName = "FileName")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fileName;

    public Nomenclature(String id, String label) {
        this.id = id;
        this.label = label;
    }
}
