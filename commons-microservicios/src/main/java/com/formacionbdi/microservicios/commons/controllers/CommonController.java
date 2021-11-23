package com.formacionbdi.microservicios.commons.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.formacionbdi.microservicios.commons.services.CommonService;

//@CrossOrigin(origins = "http://localhost:4200")
public class CommonController<E, S extends CommonService<E>> {
	
	@Autowired
	protected S service;
	
	@GetMapping //Dar una ruta
	public ResponseEntity<?> listar(){ //Nos permite construir una respuesta, el HTTP Entity
		return ResponseEntity.ok().body(service.findAll());	
	}
	
	@GetMapping("/pagina") //Dar una ruta
	public ResponseEntity<?> listar(Pageable pageable){ //Nos permite construir una respuesta, el HTTP Entity
		return ResponseEntity.ok().body(service.findAll(pageable));	
	}
	
	@GetMapping("/{id}") //Una ruta variable URL que va a ir cambiando
	public ResponseEntity<?> ver(@PathVariable Long id){
		Optional<E> o = service.findById(id); //Buscar el alumno, para eso se usa el optional
		
		if(o.isEmpty()) {
			return ResponseEntity.notFound().build(); //Se construye la respuesta con un contenido vacio 404
		}
		return ResponseEntity.ok(o.get());
	}
	
	@PostMapping
	public ResponseEntity<?> crear(@Valid @RequestBody E entity, BindingResult result){ //Este JSOn se va a poblar en el Alumno, debe coincidir el nombre
		if(result.hasErrors()) {
			return this.validar(result);
		}
		E entityDb = service.save(entity); //Se guarda el alumno
		return ResponseEntity.status(HttpStatus.CREATED).body(entityDb); //no es OK porque no sería 200 sino 201

	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id){
		service.deleteById(id);
		
		return ResponseEntity.noContent().build(); //Tipo 204
	}
	
	//Generar un JSON con los mensajes de error
	protected ResponseEntity<?> validar(BindingResult result){
		Map<String, Object> errores = new HashMap<>();
		result.getFieldErrors().forEach(err -> {
			errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
		});
		
		return ResponseEntity.badRequest().body(errores);
	}
	

}
