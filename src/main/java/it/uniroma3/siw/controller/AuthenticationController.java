package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UtenteService;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {


	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UtenteService utenteService;



	@GetMapping("/register")
	public String formRegistrazione(Model model) {
	    Utente utente = new Utente();
	    utente.setCredentials(new Credentials());  
	    model.addAttribute("utente", utente);
	    model.addAttribute("credentials", utente.getCredentials());  
	    return "register";
	}


	@PostMapping("/register")
	public String registraUtente(@Valid @ModelAttribute("utente") Utente utente, 
			@Valid @ModelAttribute("credentials") Credentials credentials, 
			@RequestParam("confirmPassword") String confirmPassword, Model model) {

		if(!credentials.getPassword().equals(confirmPassword)) {
			model.addAttribute("error", "Le password non coincidono");
			return "register";
		}

		utenteService.salva(utente);

		credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
		credentials.setRole(Credentials.DEFAULT_ROLE);

		credentials.setUtente(utente);
		utente.setCredentials(credentials);

		credentialsService.salva(credentials);


		return "redirect:/login";
	}

	@GetMapping("/login")  
	public String formLogin(@RequestParam(value="error",required=false) String error,Model model) {
		if(error!=null) {
			model.addAttribute("error", "Credenziali non valide");
		}

		return "login";
	}

}
