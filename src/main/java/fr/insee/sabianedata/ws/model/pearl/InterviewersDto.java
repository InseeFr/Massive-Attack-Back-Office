package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@JacksonXmlRootElement(localName = "Interviewers")
@NoArgsConstructor
@Getter
@Setter
public class InterviewersDto {

    @JacksonXmlProperty(localName = "Interviewer")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<InterviewerDto> interviewers;

}
