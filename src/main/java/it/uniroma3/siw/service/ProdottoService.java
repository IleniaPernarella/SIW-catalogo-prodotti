package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import it.uniroma3.siw.model.Prodotto;
import it.uniroma3.siw.repository.ProdottoRepository;

import java.io.IOException;
import java.util.ArrayList;
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

    public void salvaProdotto(Prodotto prodotto) {
        prodottoRepository.save(prodotto);
    }

    public void eliminaProdottoById(Long id) {
        Prodotto prodotto = findById(id);
        if (prodotto != null) {
            // Rimuove le relazioni bidirezionali prima di eliminare
            for (Prodotto simile : new ArrayList<>(prodotto.getProdottiSimili())) {
                prodotto.removeProdottoSimile(simile);
            }
            prodottoRepository.delete(prodotto);
        }
    }

    public List<Prodotto> getProdottiSimili(Long prodottoId) {
        Prodotto prodotto = this.findById(prodottoId);
        return prodotto != null ? prodotto.getProdottiSimili() : new ArrayList<>();
    }

    public List<Prodotto> findByTipologiaId(Long tipologiaId) {
        List<Prodotto> prodottiFiltrati = new ArrayList<>();
        for (Prodotto p : getAllProdotti()) {
            if (p.getTipologia() != null && p.getTipologia().getId().equals(tipologiaId)) {
                prodottiFiltrati.add(p);
            }
        }
        return prodottiFiltrati;
    }

    public List<Prodotto> cercaByKeyword(String keyword) {
        List<Prodotto> prodottiFiltrati = new ArrayList<>();
        String lowerCaseKeyword = keyword.toLowerCase();
        for (Prodotto p : getAllProdotti()) {
            if (p != null && p.getNome().toLowerCase().contains(lowerCaseKeyword)) {
                prodottiFiltrati.add(p);
            }
        }
        return prodottiFiltrati;
    }
}
