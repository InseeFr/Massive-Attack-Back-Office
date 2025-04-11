package fr.insee.sabianedata.ws.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger configuration
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(value = "feature.swagger.enabled", havingValue = "true")
public class SpringDocConfiguration {

	private final SpringDocSecurityConfiguration securityConfiguration;

	/**
	 * Generate Open API configuration for springdoc
	 *
	 * @param buildProperties properties for build
	 * @return Open API configuration object
	 */
	@Bean
	public OpenAPI generateOpenAPI(BuildProperties buildProperties) {
		OpenAPI openAPI = new OpenAPI().info(
				new Info()
						.title(buildProperties.getName())
						.description(buildProperties.get("description"))
						.version(buildProperties.getVersion()));
		securityConfiguration.addSecurity(openAPI);
		return openAPI;
	}
}