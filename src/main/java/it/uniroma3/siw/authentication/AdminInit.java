package it.uniroma3.siw.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.uniroma3.siw.service.CredentialsService;
import jakarta.annotation.PostConstruct;

@Component
public class AdminInit {

	@Autowired 
	CredentialsService credentialsService;
	
	@PostConstruct
    public void init() {
        credentialsService.creaAdminCredentials();
    }
}
