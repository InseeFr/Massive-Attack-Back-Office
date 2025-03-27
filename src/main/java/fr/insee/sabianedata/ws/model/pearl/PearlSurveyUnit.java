package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "SurveyUnit")
@NoArgsConstructor
@Getter
@Setter
@Slf4j
public class PearlSurveyUnit {

    // will be randomly generated => not extracted from XML source
    private String id;
    @JacksonXmlProperty(localName = "Id")
    private String displayName;
    @JacksonXmlElementWrapper(localName = "Persons")
    private List<Person> persons;
    @JacksonXmlProperty(localName = "Address")
    private AdressDto address;
    @JacksonXmlProperty(localName = "OrganizationUnitId")
    private String organizationUnitId;
    @JacksonXmlProperty(localName = "Priority")
    private boolean priority;
    @JacksonXmlProperty(localName = "Campaign")
    private String campaign;
    @JacksonXmlProperty(localName = "SampleIdentifiers")
    private SampleIdentifiersDto sampleIdentifiers;
    @JacksonXmlProperty(localName = "Comment")
    private String comment;
    private List<CommentDto> comments;
    @JacksonXmlProperty(localName = "Move")
    private Boolean move;
    @JacksonXmlProperty(localName = "ContactOutcome")
    private ContactOutcomeDto contactOutcome;
    @JacksonXmlProperty(localName = "ContactAttempts")
    private List<ContactAttemptDto> contactAttempts = List.of();
    @JacksonXmlProperty(localName = "States")
    private List<SurveyUnitStateDto> states = new ArrayList<>();
    @JacksonXmlProperty(localName = "SurveyUnitIdentification")
    @JsonProperty(value = "identification")
    private IdentificationData identification;
    private String interviewerId;


    public void cleanAttributes() {
        this.states = states == null ? new ArrayList<>() : states;
        this.contactAttempts = contactAttempts == null ? List.of() : contactAttempts;
        this.comments = comment == null ? List.of() : List.of(new CommentDto(CommentType.INTERVIEWER, comment));
    }

    public PearlSurveyUnit(PearlSurveyUnit su) {
        this.id = su.getId();
        this.displayName = su.getDisplayName();
        this.persons = su.getPersons();
        this.address = su.getAddress();
        this.interviewerId= su.getInterviewerId();
        this.organizationUnitId = su.getOrganizationUnitId();
        this.priority = su.isPriority();
        this.campaign = su.getCampaign();
        this.sampleIdentifiers = su.getSampleIdentifiers();
        this.comment = su.getComment();
        this.comments = comment == null ? List.of() : List.of(new CommentDto(CommentType.INTERVIEWER, comment));
        this.contactOutcome = su.getContactOutcome();
        // contactAttempts are added later, for date twisting
        this.contactAttempts = List.of();
        this.states = new ArrayList<>();
        this.identification = su.getIdentification();
        this.move = su.getMove();
    }

    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.warn("Couldn't stringify survey-unit", e);
        }
        return "";
    }
}
