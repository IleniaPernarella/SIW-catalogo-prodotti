package it.uniroma3.siw.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import it.uniroma3.siw.model.Prodotto;
import it.uniroma3.siw.model.Tipologia;
import it.uniroma3.siw.service.ProdottoService;
import it.uniroma3.siw.service.TipologiaService;
import jakarta.validation.Valid;

@Controller
public class AdminController {

	@Autowired
	private ProdottoService prodottoService;

	@Autowired
	private TipologiaService tipologiaService;

	@GetMapping("/admin/dashboard")
	public String dashboard(Model model) {
		model.addAttribute("prodotti", prodottoService.getAllProdotti());
		return "admin-dashboard";
	}

	@PostMapping("/admin/prodotto/{id}/delete")
	public String eliminaProdoto(@PathVariable Long id) {
		prodottoService.eliminaProdottoById(id);
		return "redirect:/admin/dashboard";
	}

	@GetMapping("/admin/prodotto/new")
	public String nuovoProdottoForm(Model model) {
		model.addAttribute("prodotto", new Prodotto());
		model.addAttribute("tipologie", tipologiaService.getAllTipologie());
		model.addAttribute("allProdotti", prodottoService.getAllProdotti());
		return "admin-formNuovoProdotto";
	}

	 @PostMapping("/admin/prodotto/new")
	    public String salvaNuovoProdotto(@Valid @ModelAttribute("prodotto") Prodotto prodotto, BindingResult bindingResult, Model model,
	                                     @RequestParam(value = "tipologiaId", required = false) Long tipologiaId,
	                                     @RequestParam(value = "nuovaTipologia", required = false) String nuovaTipologia,
	                                     @RequestParam("fileImmagine") MultipartFile immagine,
	                                     @RequestParam(value = "prodottiSimiliIds", required = false) List<Long> prodottiSimiliIds) {

	        
	        if (bindingResult.hasErrors()) {
	            model.addAttribute("tipologie", tipologiaService.getAllTipologie());
	            model.addAttribute("allProdotti", prodottoService.getAllProdotti());
	            return "admin-formNuovoProdotto"; 
	        }

	        // Gestione Tipologia
	        Tipologia tipologia;
	        if (nuovaTipologia != null && !nuovaTipologia.isEmpty()) {
	            tipologia = new Tipologia();
	            tipologia.setNome(nuovaTipologia);
	            tipologiaService.salvaTipologia(tipologia);
	        } else if (tipologiaId != null) {
	            tipologia = tipologiaService.getTipologiaById(tipologiaId);
	        } else {
	            bindingResult.rejectValue("tipologia", "error.prodotto", "È necessario specificare una tipologia.");
	            model.addAttribute("tipologie", tipologiaService.getAllTipologie());
	            model.addAttribute("allProdotti", prodottoService.getAllProdotti());
	            return "admin-formNuovoProdotto";
	        }
	        prodotto.setTipologia(tipologia);

	        // Gestione Prodotti Simili
	        if (prodottiSimiliIds != null) {
	            List<Prodotto> simili = new ArrayList<>();
	            for (Long id : prodottiSimiliIds) {
	                simili.add(prodottoService.findById(id));
	            }
	            prodotto.setProdottiSimili(simili);
	        }

	        prodottoService.salvaProdotto(prodotto, immagine);
	        return "redirect:/admin/dashboard";
	    }

	@GetMapping("/admin/prodotto/{id}/edit")
	public String modificaProdottoForm(@PathVariable Long id, Model model) {
		Prodotto prodotto = prodottoService.findById(id);
		
		List<Prodotto> allAltriProdotti = new ArrayList<>();
        for (Prodotto p : prodottoService.getAllProdotti()) {
            if (!p.getId().equals(id)) {
                allAltriProdotti.add(p);
            }
        }
		
		model.addAttribute("prodotto", prodotto);
		model.addAttribute("tipologie", tipologiaService.getAllTipologie());
		model.addAttribute("allProdotti", allAltriProdotti);
		return "admin-formProdotto";
	}

	@PostMapping("/admin/prodotto/{id}/update")
    public String aggiornaProdotto(@PathVariable Long id, 
                                   @Valid @ModelAttribute("prodotto") Prodotto prodottoFromForm, BindingResult bindingResult, Model model,
                                   @RequestParam(value = "tipologiaId", required = false) Long tipologiaId,
                                   @RequestParam(value = "nuovaTipologia", required = false) String nuovaTipologia,
                                   @RequestParam("fileImmagine") MultipartFile immagine,
                                   @RequestParam(value = "prodottiSimiliIds", required = false) List<Long> prodottiSimiliIds) {
        
        if (bindingResult.hasErrors()) {
            List<Prodotto> allAltriProdotti = new ArrayList<>();
            for (Prodotto p : prodottoService.getAllProdotti()) {
                if (!p.getId().equals(id)) {
                    allAltriProdotti.add(p);
                }
            }
            model.addAttribute("tipologie", tipologiaService.getAllTipologie());
            model.addAttribute("allProdotti", allAltriProdotti);
            return "admin-formProdotto";
        }
        
        // Carica il prodotto esistente e aggiorna i suoi campi
        Prodotto prodottoEsistente = prodottoService.findById(id);
        prodottoEsistente.setNome(prodottoFromForm.getNome());
        prodottoEsistente.setDescrizione(prodottoFromForm.getDescrizione());
        prodottoEsistente.setPrezzo(prodottoFromForm.getPrezzo());
        
        Tipologia tipologia;
        if (nuovaTipologia != null && !nuovaTipologia.isEmpty()) {
            tipologia = new Tipologia();
            tipologia.setNome(nuovaTipologia);
            tipologiaService.salvaTipologia(tipologia);
        } else if (tipologiaId != null) {
            tipologia = tipologiaService.getTipologiaById(tipologiaId);
        } else {
             bindingResult.rejectValue("tipologia", "error.prodotto", "È necessario specificare una tipologia.");
             return "admin-formProdotto";
        }
        prodottoEsistente.setTipologia(tipologia);

        // Gestione Prodotti Simili con esclusione del prodotto corrente
        List<Prodotto> simili = new ArrayList<>();
        if (prodottiSimiliIds != null) {
            for (Long simileId : prodottiSimiliIds) {
                if (!simileId.equals(id)) { 
                    simili.add(prodottoService.findById(simileId));
                }
            }
        }
        prodottoEsistente.setProdottiSimili(simili);

        prodottoService.salvaProdotto(prodottoEsistente, immagine);
        return "redirect:/admin/dashboard";
    }
}
