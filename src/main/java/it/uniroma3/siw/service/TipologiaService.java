package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Tipologia;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.repository.TipologiaRepository;

@Service
public class TipologiaService {

    private final CredentialsRepository credentialsRepository;

	@Autowired
	TipologiaRepository tipologiaRepository;

    TipologiaService(CredentialsRepository credentialsRepository) {
        this.credentialsRepository = credentialsRepository;
    }
	
	public Iterable<Tipologia> getAllTipologie() {
		return tipologiaRepository.findAll();
	}

	public void salvaTipologia(Tipologia tipologia) {
		tipologiaRepository.save(tipologia);
		
	}

	public Tipologia getTipologiaById(Long tipologiaId) {
		
		return tipologiaRepository.findById(tipologiaId).orElseThrow();
	}
	
	
}
