package fr.insee.sabianedata.ws.config.security;


import fr.insee.sabianedata.ws.config.properties.OidcProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnProperty(name = "feature.oidc.enabled", havingValue = "true")
@EnableWebSecurity
public class OidcSecurityConfiguration {

	@Bean
	@Order(1)
	@ConditionalOnProperty(name = "feature.swagger.enabled", havingValue = "true")
	protected SecurityFilterChain swaggerSecurityFilterChain(
			HttpSecurity http,
			SpringDocSecurityFilterChain springDocSecurityFilterChain,
			OidcProperties oidcProperties
	) throws Exception {
		String authorizedConnectionHost = oidcProperties.enabled()
				? " " + oidcProperties.authServerHost()
				: "";
		return springDocSecurityFilterChain.buildSecurityFilterChain(http, authorizedConnectionHost);
	}

	@Bean
	@Order(2)
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.cors(Customizer.withDefaults())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers("/", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/healthcheck").permitAll()
						.anyRequest().authenticated()
				)
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
		return http.build();
	}

}
