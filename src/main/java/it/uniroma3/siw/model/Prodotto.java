package it.uniroma3.siw.model;

import java.util.ArrayList;
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

	
	@Column(name = "immagine", columnDefinition = "bytea")
	private byte[] immagine;

	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private Tipologia tipologia;

	@OneToMany(mappedBy="prodotto", cascade=CascadeType.ALL)
	private List<Commento> commenti=new ArrayList<>();

	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(
	    name = "prodotti_simili",
	    joinColumns = @JoinColumn(name = "prodotto_id"),
	    inverseJoinColumns = @JoinColumn(name = "prodotto_simile_id")
	)
	private List<Prodotto> prodottiSimili=new ArrayList<>();
	
	

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

	public byte[] getImmagine() {
		return immagine;
	}

	public void setImmagine(byte[] immagine) {
		this.immagine = immagine;
	}

	public Tipologia getTipologia() {
		return tipologia;
	}

	public void setTipologia(Tipologia tipologia) {
		this.tipologia = tipologia;
	}

	public List<Commento> getCommenti() {
		return commenti;
	}

	public void setCommenti(List<Commento> commenti) {
		this.commenti = commenti;
	}

	public List<Prodotto> getProdottiSimili() {
		return prodottiSimili;
	}

	public void setProdottiSimili(List<Prodotto> prodottiSimili) {
		this.prodottiSimili = prodottiSimili;
	}

	// Metodi helper per la bidirezionalità
	public void addProdottoSimile(Prodotto prodotto) {
		if (!this.prodottiSimili.contains(prodotto)) {
			this.prodottiSimili.add(prodotto);
			prodotto.addProdottoSimile(this); // Assicura la bidirezionalità
		}
	}

	public void removeProdottoSimile(Prodotto prodotto) {
		if (this.prodottiSimili.contains(prodotto)) {
			this.prodottiSimili.remove(prodotto);
			prodotto.removeProdottoSimile(this); // Assicura la bidirezionalità
		}
	}



}
