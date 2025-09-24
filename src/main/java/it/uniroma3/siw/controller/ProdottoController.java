package it.uniroma3.siw.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw.model.Commento;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Prodotto;
import it.uniroma3.siw.service.CommentoService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ProdottoService;
import it.uniroma3.siw.service.TipologiaService;
import jakarta.transaction.Transactional;

@Controller
public class ProdottoController {

    @Autowired
    private ProdottoService prodottoService;
    @Autowired
    private CommentoService commentoService;
    @Autowired
    private CredentialsService credentialsService;
    @Autowired
    private TipologiaService tipologiaService;

    @GetMapping("/")
    public String mostraCatalogo(Model model,
                                 @RequestParam(value = "tipologiaId", required = false) Long tipologiaId,
                                 @RequestParam(value = "keyword", required = false) String keyword) {

        List<Prodotto> prodotti;

        if (keyword != null && !keyword.trim().isEmpty()) {
            prodotti = prodottoService.cercaByKeyword(keyword);
        } else if (tipologiaId != null) {
            prodotti = prodottoService.findByTipologiaId(tipologiaId);
        } else {
            prodotti = (List<Prodotto>) prodottoService.getAllProdotti();
        }

        model.addAttribute("prodotti", prodotti);
        model.addAttribute("tipologie", tipologiaService.getAllTipologie());
        model.addAttribute("selectedTipologiaId", tipologiaId);

        return "home";
    }

    @GetMapping("/immagine/{id}")
    @Transactional
    public ResponseEntity<byte[]> getImmagine(@PathVariable Long id) {
        Prodotto prodotto = prodottoService.findById(id);
        if (prodotto != null && prodotto.getImmagine() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(prodotto.getImmagine());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/prodotto/{id}")
    public String schedaProdotto(@PathVariable("id") Long id, Model model) {
        Prodotto prodotto = prodottoService.findById(id);
        if (prodotto == null) {
            return "redirect:/";
        }

        List<Prodotto> prodottiSimili = prodottoService.getProdottiSimili(id);
        if (prodottiSimili == null) {
            prodottiSimili = List.of();
        }

        model.addAttribute("prodotto", prodotto);
        model.addAttribute("prodottiSimili", prodottiSimili);
        model.addAttribute("commenti", commentoService.getCommentiByProdottoId(id));
        model.addAttribute("newCommento", new Commento());

        return "schedaProdotto";
    }

    @PostMapping("/prodotto/{id}/commento")
    public String nuovoCommento(@PathVariable("id") Long id,
                                @RequestParam("titolo") String titolo,
                                @RequestParam("testo") String testo) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
        Prodotto prodotto = prodottoService.findById(id);

        if (prodotto != null && credentials != null) {
            Commento nuovoCommento = new Commento();
            nuovoCommento.setTitolo(titolo);
            nuovoCommento.setTesto(testo);
            nuovoCommento.setAutore(credentials.getUtente());
            nuovoCommento.setData(LocalDateTime.now());
            nuovoCommento.setProdotto(prodotto);

            commentoService.save(nuovoCommento);
        }

        return "redirect:/prodotto/" + id;
    }
}
