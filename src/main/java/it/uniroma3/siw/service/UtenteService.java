package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.repository.UtenteRepository;
import jakarta.validation.Valid;

@Service
public class UtenteService {

	@Autowired
	private UtenteRepository utenteRepository;

	public void salva(Utente utente) {
		utenteRepository.save(utente);
		
	}
	
	public Utente findByEmail(String email) {
        return utenteRepository.findByEmail(email);  
    }
	
	
}
