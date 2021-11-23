package com.formacionbdi.microservicios.commons.examenes.models.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "asignaturas")
public class Asignatura {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nombre;
	
	//muchas asignaturas hijas asociadas a un padre
	@JsonIgnoreProperties(value = {"hijos", "handler", "hibernateLazyInitializer"}) //Para evitar un posible error
	@ManyToOne(fetch = FetchType.LAZY)
	private Asignatura padre; 
	
	//una asignatura puede tener hijos
	@JsonIgnoreProperties(value = {"padre", "handler", "hibernateLazyInitializer"}, allowSetters = true)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "padre", cascade = CascadeType.ALL)
	private List<Asignatura> hijos;
	
	//Cuando hay una relación OneToMany de lista, se tiene que inicializar con un ArrayList
	public Asignatura() {
		this.hijos = new ArrayList<>();
	}
	
	//Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Asignatura getPadre() {
		return padre;
	}

	public void setPadre(Asignatura padre) {
		this.padre = padre;
	}

	public List<Asignatura> getHijos() {
		return hijos;
	}

	public void setHijos(List<Asignatura> hijos) {
		this.hijos = hijos;
	}

	
	
}
