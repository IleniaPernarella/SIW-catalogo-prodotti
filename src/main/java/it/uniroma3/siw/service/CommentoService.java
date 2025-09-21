package it.uniroma3.siw.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Commento;
import it.uniroma3.siw.repository.CommentoRepository;

@Service
public class CommentoService {

	@Autowired
	private CommentoRepository commentoRepository;
	
	public Iterable<Commento> getAllCommenti(){
		return commentoRepository.findAll();
	}

	public List<Commento> getCommentiByProdottoId(Long id){
		List<Commento> commentoByProdottoId = new ArrayList<>();
		
		for(Commento c : getAllCommenti() ) {
			if(c.getProdotto().getId().equals(id))
				commentoByProdottoId.add(c);
		}
		return commentoByProdottoId;
	}

	public void save(Commento commento) {
		commentoRepository.save(commento);
		
	}
}
