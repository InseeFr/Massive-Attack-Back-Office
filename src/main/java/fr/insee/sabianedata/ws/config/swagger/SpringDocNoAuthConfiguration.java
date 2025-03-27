package fr.insee.sabianedata.ws.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger configuration to use no authentication with OpenAPI
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "feature.oidc.enabled", havingValue = "false")
public class SpringDocNoAuthConfiguration implements SpringDocSecurityConfiguration {

    @Override
    public void addSecurity(OpenAPI openAPI) {
        // nothing to do as no security here
    }
}
