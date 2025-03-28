package fr.insee.sabianedata.ws.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class BearerAuthInterceptor implements ClientHttpRequestInterceptor {

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body,
										ClientHttpRequestExecution execution) throws IOException {

		var context = SecurityContextHolder.getContext();
		if (context.getAuthentication() instanceof JwtAuthenticationToken jwtAuth) {
			String token = jwtAuth.getToken().getTokenValue();
			if (StringUtils.hasText(token)) {
				request.getHeaders().setBearerAuth(token);
			}
		}

		return execution.execute(request, body);
	}
}
