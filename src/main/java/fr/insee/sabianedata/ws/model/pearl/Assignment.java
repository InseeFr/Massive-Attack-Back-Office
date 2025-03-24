package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@JacksonXmlRootElement(localName = "Assignement")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Slf4j
public class Assignment {

    @JacksonXmlProperty(localName = "SurveyUnitId")
    private String surveyUnitId;

    @JacksonXmlProperty(localName = "InterviewerId")
    private String interviewerId;

    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.warn("Couldn't stringify assignment",e);
        }
        return "";
    }
}
