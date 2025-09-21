package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import it.uniroma3.siw.model.Prodotto;
import it.uniroma3.siw.repository.ProdottoRepository;

import java.io.IOException;
import java.util.List;

@Transactional
@Service
public class ProdottoService {

    @Autowired
    private ProdottoRepository prodottoRepository;

    public Iterable<Prodotto> getAllProdotti() {
        return prodottoRepository.findAll();
    }

    public Prodotto findById(Long id) {
        return prodottoRepository.findById(id).orElse(null);
    }

 
    public void salvaProdotto(Prodotto prodotto, MultipartFile immagine) {
        try {
     
            if (immagine != null && !immagine.isEmpty()) {
                prodotto.setImmagine(immagine.getBytes());
            }
            prodottoRepository.save(prodotto);
        } catch (IOException e) {
            throw new RuntimeException("Errore durante il salvataggio dell'immagine", e);
        }
    }

	public void eliminaProdottoById(Long id) {
		prodottoRepository.deleteById(id);
		
	}
	
	public List<Prodotto> getProdottiSimili(Long prodottoId) {
	    //trova il prodotto principale 
	    Prodotto prodotto = this.findById(prodottoId); 
	    
	    //restituisce la sua lista di prodotti simili
	    return prodotto.getProdottiSimili();
	}

}
