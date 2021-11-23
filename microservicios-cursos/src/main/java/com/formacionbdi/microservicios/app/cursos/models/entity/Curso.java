package com.formacionbdi.microservicios.app.cursos.models.entity;

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
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.formacionbdi.microservicios.commons.alumnos.models.entity.Alumno;
import com.formacionbdi.microservicios.commons.examenes.models.entity.Examen;

@Entity	
@Table(name="cursos")
public class Curso {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty
	private String nombre;
	
	@Column(name="created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	
	@JsonIgnoreProperties(value= {"curso"}, allowSetters = true)
	@OneToMany(fetch = FetchType.LAZY, mappedBy="curso", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CursoAlumno> cursoAlumnos;
	
	//@OneToMany(fetch = FetchType.LAZY) //Relación uno a muchos
	@Transient //ya que no está mapeado a la tabla, es un atributo que se va a poblar después para la relación
	private List<Alumno> alumnos; //para la relación
	
	@ManyToMany(fetch = FetchType.LAZY)
	private List<Examen> examenes; //Crear la lista de examenes
	
	//Constructor
	public Curso() {
		this.alumnos = new ArrayList<>();
		this.examenes = new ArrayList<>(); //Inicializar la lista en el constructor
		this.cursoAlumnos = new ArrayList<>();
	}
	
	@PrePersist
	public void prePersist() {
		this.createdAt = new Date();
	}
	
	//Getters and Setters
	public List<Alumno> getAlumnos() {
		return alumnos;
	}

	public void setAlumnos(List<Alumno> alumnos) {
		this.alumnos = alumnos;
	}

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
	

	public List<CursoAlumno> getCursoAlumnos() {
		return cursoAlumnos;
	}

	public void setCursoAlumnos(List<CursoAlumno> cursoAlumnos) {
		this.cursoAlumnos = cursoAlumnos;
	}

	public List<Examen> getExamenes() {
		return examenes;
	}

	public void setExamenes(List<Examen> examenes) {
		this.examenes = examenes;
	}
	
	//Agregar un alumno al curso
	public void addAlumno(Alumno alumno) {
		this.alumnos.add(alumno);
	}
	
	//Eliminar un alumno de un curso
	public void removeAlumno(Alumno alumno) {
		this.alumnos.remove(alumno);
	}
	
	public void addExamen(Examen examen) { //Agregar un examen
		this.examenes.add(examen);
	}
	
	public void removeExamen(Examen examen) { //Eliminar un examen
		this.examenes.remove(examen);
	}
	
	public void addCursoAlumno(CursoAlumno cursoAlumno) {
		this.cursoAlumnos.add(cursoAlumno);
	}
	
	public void removeCursoAlumno(CursoAlumno cursoAlumno) {
		this.cursoAlumnos.remove(cursoAlumno);
	}

}
