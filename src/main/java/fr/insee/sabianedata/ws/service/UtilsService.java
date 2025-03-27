package fr.insee.sabianedata.ws.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UtilsService {

	public String getRequesterId() {

		return SecurityContextHolder.getContext().getAuthentication().getName();

	}

}
