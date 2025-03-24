package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@JacksonXmlRootElement(localName = "Assignements")
@NoArgsConstructor
@Getter
@Setter
public class Assignements {

    @JacksonXmlProperty(localName = "Assignement")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Assignment> assignments;

}
