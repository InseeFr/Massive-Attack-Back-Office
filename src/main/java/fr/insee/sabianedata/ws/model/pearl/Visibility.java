package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import fr.insee.sabianedata.ws.utils.DateParser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JacksonXmlRootElement(localName = "Visibility")
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Setter
@Getter
public class Visibility {

    @JacksonXmlProperty(localName = "OrganisationalUnit")
    private String organizationalUnit;

    @JacksonXmlProperty(localName = "CollectionStartDate")
    private String collectionStartDateString;
    @JacksonXmlProperty(localName = "CollectionEndDate")
    private String collectionEndDateString;
    @JacksonXmlProperty(localName = "IdentificationPhaseStartDate")
    private String identificationPhaseStartDateString;
    @JacksonXmlProperty(localName = "InterviewerStartDate")
    private String interviewerStartDateString;
    @JacksonXmlProperty(localName = "ManagementStartDate")
    private String managementStartDateString;
    @JacksonXmlProperty(localName = "EndDate")
    private String endDateString;

    private Long collectionStartDate;
    private Long collectionEndDate;
    private Long identificationPhaseStartDate;
    private Long interviewerStartDate;
    private Long managementStartDate;
    private Long endDate;
    private String mail = "no-mail-yet";
    private String tel = "no-tel-yet";
    private boolean useLetterCommunication = false;

    public Visibility(Visibility visibility) throws IllegalArgumentException {
        this.organizationalUnit = visibility.getOrganizationalUnit();
        this.collectionStartDate = DateParser.fixedDateParse(visibility.getCollectionStartDateString());
        this.collectionEndDate = DateParser.fixedDateParse(visibility.getCollectionEndDateString());
        this.identificationPhaseStartDate = DateParser
                .fixedDateParse(visibility.getIdentificationPhaseStartDateString());
        this.interviewerStartDate = DateParser.fixedDateParse(visibility.getInterviewerStartDateString());
        this.managementStartDate = DateParser.fixedDateParse(visibility.getManagementStartDateString());
        this.endDate = DateParser.fixedDateParse(visibility.getEndDateString());
        this.collectionStartDateString = visibility.getCollectionStartDateString();
        this.collectionEndDateString = visibility.getCollectionEndDateString();
        this.identificationPhaseStartDateString = visibility.getIdentificationPhaseStartDateString();
        this.interviewerStartDateString = visibility.getInterviewerStartDateString();
        this.managementStartDateString = visibility.getManagementStartDateString();
        this.endDateString = visibility.getEndDateString();
        this.mail = visibility.getMail();
        this.tel= visibility.getTel();
        this.useLetterCommunication= visibility.isUseLetterCommunication();
    }

    /**
     * Apply the date modification to the reference date to calculate the final dates.
     *
     * @param referenceDate the reference date
     */
    public void updateDatesWithReferenceDate(Long referenceDate) {
        this.collectionStartDate = DateParser.relativeDateParse(collectionStartDateString,
                referenceDate);
        this.collectionEndDate = DateParser.relativeDateParse(collectionEndDateString, referenceDate);
        this.identificationPhaseStartDate = DateParser
                .relativeDateParse(identificationPhaseStartDateString, referenceDate);
        this.interviewerStartDate = DateParser.relativeDateParse(interviewerStartDateString,
                referenceDate);
        this.managementStartDate = DateParser.relativeDateParse(managementStartDateString,
                referenceDate);
        this.endDate = DateParser.relativeDateParse(endDateString, referenceDate);
    }

}
