package it.uniroma3.siw.model;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Prodotto {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String nome;
	private Float prezzo;
	@Size(max = 1000, message = "La descrizione non può superare i 1000 caratteri")
    @NotBlank(message = "La descrizione non può essere vuota")
	private String descrizione;
	
	@Lob 
    private byte[] immagine;  
	
	@ManyToOne
	private Tipologia tipologia;
	
	@OneToMany(mappedBy="prodotto", cascade=CascadeType.ALL)
	private List<Commento> commenti;
	
	@ManyToMany
	private List<Prodotto> prodottiSimili;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Float getPrezzo() {
		return prezzo;
	}

	public void setPrezzo(Float prezzo) {
		this.prezzo = prezzo;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public Tipologia getTipologia() {
		return tipologia;
	}

	public void setTipologia(Tipologia tipologia) {
		this.tipologia = tipologia;
	}


	public List<Prodotto> getProdottiSimili() {
		return prodottiSimili;
	}

	public void setProdottiSimili(List<Prodotto> prodottiSimili) {
		this.prodottiSimili = prodottiSimili;
	}

	public byte[] getImmagine() {
		return immagine;
	}

	public void setImmagine(byte[] immagine) {
		this.immagine = immagine;
	}

	public List<Commento> getCommenti() {
		return commenti;
	}

	public void setCommenti(List<Commento> commenti) {
		this.commenti = commenti;
	}
	
	
	
}
