package it.uniroma3.siw.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import it.uniroma3.siw.model.Commento;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Prodotto;
import it.uniroma3.siw.service.CommentoService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ProdottoService;
import it.uniroma3.siw.service.TipologiaService;



@Controller
public class ProdottoController {
	
	@Autowired
	ProdottoService prodottoService;
	@Autowired
	CommentoService commentoService;
	@Autowired
	CredentialsService credentialsService;
	@Autowired
	TipologiaService tipologiaService;

	@GetMapping("/")
	private String mostraCatalogo(Model model,@RequestParam(value="tipologiaId",required=false)Long tipologiaId,
									@RequestParam(value="keyword",required=false)String keyword) {
		
		List<Prodotto> prodotti;
		
		//trim() rimuove spazi bianchi
        if (keyword != null && !keyword.trim().isEmpty()) {
            prodotti = prodottoService.cercaByKeyword(keyword);
        }
        
        else if (tipologiaId != null) {
            prodotti = prodottoService.findByTipologiaId(tipologiaId);
        } 
        
        else {
            prodotti = (List<Prodotto>) prodottoService.getAllProdotti();
        }
		
		model.addAttribute("prodotti", prodotti);
		model.addAttribute("tipologie",tipologiaService.getAllTipologie());
		model.addAttribute("selectedTipologiaId", tipologiaId);
		
		return "home";
	}
	
	@GetMapping("/immagine/{id}")
	public ResponseEntity<byte[]> getImmagine(@PathVariable Long id) {
	    Prodotto prodotto = prodottoService.findById(id);
	    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(prodotto.getImmagine());
	}
	
	@GetMapping("/prodotto/{id}")
	public String schedaProdotto(Model model,@PathVariable("id") Long id) {
		model.addAttribute("prodotto", prodottoService.findById(id));
		model.addAttribute("commenti",commentoService.getCommentiByProdottoId(id));
		model.addAttribute("prodottiSimili", prodottoService.getProdottiSimili(id));
		
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

	        
	        Commento nuovoCommento = new Commento();
	        nuovoCommento.setTitolo(titolo);
	        nuovoCommento.setTesto(testo);
	        nuovoCommento.setAutore(credentials.getUtente());
	        nuovoCommento.setData(LocalDateTime.now());
	        nuovoCommento.setProdotto(prodotto);
	        
	      
	        commentoService.save(nuovoCommento);

	        
	        return "redirect:/prodotto/" + id;
	    }

}
