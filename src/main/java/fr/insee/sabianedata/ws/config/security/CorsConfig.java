package fr.insee.sabianedata.ws.config.security;

import fr.insee.sabianedata.ws.config.properties.ApplicationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpMethod.*;

@Configuration
public class CorsConfig {

	@Bean
	protected CorsConfigurationSource corsConfigurationSource(ApplicationProperties applicationProperties) {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.setAllowedOrigins(applicationProperties.corsOrigins());
		config.setAllowedHeaders(List.of(AUTHORIZATION, CONTENT_TYPE));
		config.setAllowedMethods(Stream.of(GET, PUT, DELETE, POST, OPTIONS).map(HttpMethod::toString).toList());
		config.addExposedHeader(CONTENT_DISPOSITION);
		config.setMaxAge(3600L);
		source.registerCorsConfiguration("/**", config);
		return source;
	}

}