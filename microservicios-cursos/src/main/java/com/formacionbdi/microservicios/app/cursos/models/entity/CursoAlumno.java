package com.formacionbdi.microservicios.app.cursos.models.entity;

import javax.persistence.Column;
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
@Table(name="cursos_alumnos") //tabla intermedia
public class CursoAlumno {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//nombre de la columna en la base de datos, no es una llave
	@Column(name="alumno_id", unique = true)
	private Long alumnoId;
	
	@JsonIgnoreProperties(value= {"cursoAlumnos"}) //evitar que se forme un loop infinito, termina con la relación hija
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="curso_id") //nombre de la foreign key
	private Curso curso;

	//Getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAlumnoId() {
		return alumnoId;
	}

	public void setAlumnoId(Long alumnoId) {
		this.alumnoId = alumnoId;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}
	
//equals
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof CursoAlumno)) { //Valida que sea un objeto de alumno
			return false;
		}
		
		CursoAlumno c = (CursoAlumno) obj; //Comparamos los ID
		return this.alumnoId != null && this.alumnoId.equals(c.getAlumnoId());
	}	

}
