package fr.insee.sabianedata.ws.config.properties;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "application")
public record ApplicationProperties(
        String host,
        @NotEmpty(message = "cors origins must be specified (application.corsOrigins)") List<String> corsOrigins,
        @NotEmpty(message = "Folder where temp files will be created cannot be empty.") String tempFolder,
		@NotEmpty(message = "External management API should be provided.") String managementUrl,
		@NotEmpty(message = "External questionnaire API should be provided.") String questionnaireUrl)
		{
}