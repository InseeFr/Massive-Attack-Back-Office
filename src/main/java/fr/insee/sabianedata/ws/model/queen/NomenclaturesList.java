package fr.insee.sabianedata.ws.model.queen;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@JacksonXmlRootElement(localName = "Nomenclatures")
@NoArgsConstructor
@Getter
@Setter
public class NomenclaturesList {

    @JacksonXmlProperty(localName = "Nomenclature")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Nomenclature> nomenclatures;

}
