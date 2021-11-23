package com.formacionbdi.microservicios.commons.examenes.models.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="examenes") //Mappeado a una tabla
public class Examen {
	
	@Id //anotación de llave
	@GeneratedValue(strategy = GenerationType.IDENTITY) //Generación de ID
	private Long id;
	
	@NotEmpty
	@Size(min = 4, max = 30)
	private String nombre;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_at")
	private Date createdAt;
	
	@JsonIgnoreProperties(value = {"examen"}, allowSetters = true) //evitar loop infinito, ignorar la relación inversa de examen y preguntas
	//AllowSetters para asignar mediante un set la relación inversa
	@OneToMany(mappedBy = "examen", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Pregunta> preguntas;
	
	//Muchos examenes a una asignatura
	@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer"}) //Para evitar un posible error
	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	private Asignatura asignaturaPadre;
	
	@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer"})
	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	private Asignatura asignaturaHijo;
	
	@Transient //anotación para indicar que no se va a mapear a la tabla, solo es de la clase de examen
	private boolean respondido;

	//Consturctor para inicializar la lista
	public Examen() {
		this.preguntas = new ArrayList<>();
	}

	//Crear la fecha
	@PrePersist
	public void prePersist() {
		this.createdAt = new Date();
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

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public List<Pregunta> getPreguntas() {
		return preguntas;
	}

	public boolean isRespondido() {
		return respondido;
	}

	public void setRespondido(boolean respondido) {
		this.respondido = respondido;
	}
	

	public Asignatura getAsignaturaPadre() {
		return asignaturaPadre;
	}

	public void setAsignaturaPadre(Asignatura asignaturaPadre) {
		this.asignaturaPadre = asignaturaPadre;
	}

	public Asignatura getAsignaturaHijo() {
		return asignaturaHijo;
	}

	public void setAsignaturaHijo(Asignatura asignaturaHijo) {
		this.asignaturaHijo = asignaturaHijo;
	}

	//Por cada pregunta que se envíe se asigna al examen
	//Se asigna el examen a la pregunta y la pregunta al examen
	public void setPreguntas(List<Pregunta> preguntas) {
		this.preguntas.clear(); //Se debe reiniciar
		preguntas.forEach(this::addPregunta); //o this::addPregunta
	}
	
	//Para establecer la relación inversa
	public void addPregunta(Pregunta pregunta) {
		this.preguntas.add(pregunta);
		pregunta.setExamen(this);
	}
	
	public void removePregunta(Pregunta pregunta) {
		this.preguntas.remove(pregunta);
		pregunta.setExamen(null); //quitar la relacion
	}

	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof Examen)) { //Valida que sea un objeto de alumno
			return false;
		}
		
		Examen e = (Examen) obj; //Comparamos los ID
		return this.id != null && this.id.equals(e.getId());
	}
	
}
