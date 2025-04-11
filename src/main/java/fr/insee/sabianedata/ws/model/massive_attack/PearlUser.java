package fr.insee.sabianedata.ws.model.massive_attack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class PearlUser {

    private String id;
    private String firstName;
    private String lastName;
    @JsonProperty("organizationUnit")
    private OrganisationUnitDto organisationUnit;


}
