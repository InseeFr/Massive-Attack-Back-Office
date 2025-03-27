package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JacksonXmlRootElement(localName = "Campaign")
public class PearlCampaign {

    @JacksonXmlProperty(localName = "Campaign")
    private String campaign;

    @JacksonXmlProperty(localName = "CampaignLabel")
    private String campaignLabel;

    @JacksonXmlProperty(localName = "Email")
    private String email;

    @JacksonXmlProperty(localName = "IdentificationConfiguration")
    private String identificationConfiguration;

    @JacksonXmlProperty(localName = "ContactAttemptConfiguration")
    private String contactAttemptConfiguration;

    @JacksonXmlProperty(localName = "ContactOutcomeConfiguration")
    private String contactOutcomeConfiguration;

    @JacksonXmlElementWrapper(localName = "Visibilities")
    private List<Visibility> visibilities;

    @JacksonXmlElementWrapper(localName = "Referents")
    private List<Referent> referents;

}
