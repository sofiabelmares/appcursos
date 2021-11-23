package com.formacionbdi.microservicios.commons.alumnos.models.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="alumnos")

public class Alumno {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) //Autonúmerico, también sirve para postgress
	private Long id;
	
	//No es necesario tener que anotar con column ya que se llaman igual en la BD
	@NotEmpty
	private String nombre;
	
	@NotEmpty
	private String apellido;
	
	@NotEmpty
	@Email
	private String email;
	
	//Se especifica el nombre en la BD
	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	
	@Lob //para persistir un large object, es decir un blob
	@JsonIgnore //Para que no aparezca en el json
	private byte[] foto;

	//Para que de forma automatica antes de hacer un insert de este objeto en el motor de BD asigne la fecha
	@PrePersist
	public void prePersist() {
		this.createdAt = new Date();
	}
	
	public Integer getFotoHashCode() {
		//si es null retorna un hashcode, de lo contrario retorna null
		return (this.foto != null) ? this.foto.hashCode() : null; //HashCode genera un identificador único
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

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@Override //Método para el remove alumno de un curso
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof Alumno)) { //Valida que sea un objeto de alumno
			return false;
		}
		
		Alumno a = (Alumno) obj; //Comparamos los ID
		return this.id != null && this.id.equals(a.getId());
	}

	public byte[] getFoto() {
		return foto;
	}

	public void setFoto(byte[] foto) {
		this.foto = foto;
	}

	
}
