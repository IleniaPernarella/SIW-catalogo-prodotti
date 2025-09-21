package it.uniroma3.siw.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.repository.UtenteRepository;

@Service
public class CredentialsService {

	@Autowired
	private CredentialsRepository credentialsRepository;
	@Autowired
	private UtenteRepository utenteRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public List<Credentials> findByUsername(String username) {
		List<Credentials> credentialsByUsername = new ArrayList<>();
		
		for(Credentials c : credentialsRepository.findAll()) {
			if(c.getUsername().equals(username))
				credentialsByUsername.add(c);
		}
		return credentialsByUsername;
	}
	
	public void creaAdminCredentials() {
		
		List<Credentials> adminEsistente = findByUsername("admin");
		
		if(adminEsistente.isEmpty()) {
			Utente adminUtente = new Utente();
			adminUtente.setNome("admin");
			adminUtente.setCognome("admin");
			 adminUtente.setEmail("admin@example.com");
			
			utenteRepository.save(adminUtente);
			
			Credentials adminCredentials = new Credentials();
			adminCredentials.setUsername("admin");
			adminCredentials.setPassword(passwordEncoder.encode("admin"));
			adminCredentials.setRole(Credentials.ADMIN_ROLE);
			
			adminCredentials.setUtente(adminUtente);
			adminUtente.setCredentials(adminCredentials);
			
			credentialsRepository.save(adminCredentials);
		}
	}
	
	public Credentials getCredentials(String username) {
     
        List<Credentials> credentialsByUsername = this.findByUsername(username);
        
        if (credentialsByUsername != null && !credentialsByUsername.isEmpty()) {
            return credentialsByUsername.get(0);
        }
        return null;
    }
	
	public void salva(Credentials credentials) {
		credentialsRepository.save(credentials);
	}
}
