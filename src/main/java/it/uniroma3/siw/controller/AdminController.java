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
	public String salvaNuovoProdotto(@Valid @ModelAttribute("prodotto") Prodotto prodotto,
			BindingResult bindingResult, Model model,
			@RequestParam(value = "tipologiaId", required = false) Long tipologiaId,
			@RequestParam(value = "nuovaTipologia", required = false) String nuovaTipologia,
			@RequestParam(value = "fileImmagine", required = false) MultipartFile immagine,
			@RequestParam(value = "prodottiSimiliIds", required = false) List<Long> prodottiSimiliIds) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("tipologie", tipologiaService.getAllTipologie());
			model.addAttribute("allProdotti", prodottoService.getAllProdotti());
			return "admin-formNuovoProdotto";
		}


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


		if (immagine != null && !immagine.isEmpty()) {
			try {
				prodotto.setImmagine(immagine.getBytes());
			} catch (Exception e) {
				bindingResult.rejectValue("immagine", "error.prodotto", "Errore durante il caricamento dell'immagine");
				model.addAttribute("tipologie", tipologiaService.getAllTipologie());
				model.addAttribute("allProdotti", prodottoService.getAllProdotti());
				return "admin-formNuovoProdotto";
			}
		}

		if (prodottiSimiliIds != null) {
			for (Long simileId : prodottiSimiliIds) {
				Prodotto simile = prodottoService.findById(simileId);
				prodotto.addProdottoSimile(simile);
			}
		}

		prodottoService.salvaProdotto(prodotto);
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
			@Valid @ModelAttribute("prodotto") Prodotto prodottoFromForm,
			BindingResult bindingResult, Model model,
			@RequestParam(value = "tipologiaId", required = false) Long tipologiaId,
			@RequestParam(value = "nuovaTipologia", required = false) String nuovaTipologia,
			@RequestParam(value = "fileImmagine", required = false) MultipartFile immagine,
			@RequestParam(value = "prodottiSimiliIds", required = false) List<Long> prodottiSimiliIds) {

		if (bindingResult.hasErrors()) {
			List<Prodotto> allAltriProdotti = new ArrayList<>();
			for (Prodotto p : prodottoService.getAllProdotti()) {
				if (!p.getId().equals(id)) allAltriProdotti.add(p);
			}
			model.addAttribute("tipologie", tipologiaService.getAllTipologie());
			model.addAttribute("allProdotti", allAltriProdotti);
			return "admin-formProdotto";
		}

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


		for (Prodotto p : new ArrayList<>(prodottoEsistente.getProdottiSimili())) {
			prodottoEsistente.removeProdottoSimile(p);
		}
		if (prodottiSimiliIds != null) {
			for (Long simileId : prodottiSimiliIds) {
				if (!simileId.equals(id)) {
					Prodotto simile = prodottoService.findById(simileId);
					prodottoEsistente.addProdottoSimile(simile);
				}
			}
		}


		if (immagine != null && !immagine.isEmpty()) {
			try {
				prodottoEsistente.setImmagine(immagine.getBytes());
			} catch (Exception e) {
				bindingResult.rejectValue("immagine", "error.prodotto", "Errore durante il caricamento dell'immagine");
				model.addAttribute("tipologie", tipologiaService.getAllTipologie());
				model.addAttribute("allProdotti", prodottoService.getAllProdotti());
				return "admin-formProdotto";
			}
		}
		// non modifico l'immagine esistente se non viene caricata una nuova immagine

		prodottoService.salvaProdotto(prodottoEsistente);
		return "redirect:/admin/dashboard";
	}

}
