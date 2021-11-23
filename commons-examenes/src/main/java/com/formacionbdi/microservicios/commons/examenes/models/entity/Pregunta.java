package com.formacionbdi.microservicios.commons.examenes.models.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="preguntas") //Mappeado a una tabla
public class Pregunta {
	
	@Id //anotación de llave
	@GeneratedValue(strategy = GenerationType.IDENTITY) //Generación de ID
	private Long id;
	
	private String texto;

	@JsonIgnoreProperties(value = {"preguntas"}) //evitar loop infinito, ignorar la relación inversa de examen y preguntas
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "examen_id") //Llave foranea
	private Examen examen;

	//Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public Examen getExamen() {
		return examen;
	}

	public void setExamen(Examen examen) {
		this.examen = examen;
	}
	
	//Para saber si existe la pregunta
	@Override
	public boolean equals(Object obj) {

		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof Pregunta)) { //Valida que sea un objeto de Pregunta
			return false;
		}
		
		Pregunta p = (Pregunta) obj; //Comparamos los ID
		return this.id != null && this.id.equals(p.getId());
	}
	
	

}
