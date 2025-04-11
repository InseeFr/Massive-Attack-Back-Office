package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@JacksonXmlRootElement(localName = "Person")
@NoArgsConstructor
@Getter
public class Person {

    @Setter
    @JacksonXmlProperty(localName = "FirstName")
    private String firstName;
    @Setter
    @JacksonXmlProperty(localName = "LastName")
    private String lastName;
    @Setter
    @JacksonXmlProperty(localName = "Title")
    private String title;
    @Setter
    @JacksonXmlProperty(localName = "Email")
    private String email;
    @Setter
    @JacksonXmlProperty(localName = "Privileged")
    private boolean privileged;
    @Setter
    @JacksonXmlProperty(localName = "FavoriteEmail")
    private boolean favoriteEmail;
    @JacksonXmlProperty(localName = "BirthDate")
    private String birthdateString;
    @Setter
    private Long birthdate;

    @JacksonXmlElementWrapper(localName = "PhoneNumbers")
    @Setter
    private ArrayList<PhoneNumber> phoneNumbers;

    public void setBirthdateString(String birthdateString) {
        this.birthdateString = birthdateString;
        this.birthdate = Long.parseLong(birthdateString);
    }

}
