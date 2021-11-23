package com.formacionbdi.microservicios.app.usuarios.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.formacionbdi.microservicios.app.usuarios.services.AlumnoService;
import com.formacionbdi.microservicios.commons.alumnos.models.entity.Alumno;
import com.formacionbdi.microservicios.commons.controllers.CommonController;

//@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = {"Content-Type","Access-Control-Allow-Origin","*"})
@RestController //manejar API restfull
public class AlumnoController extends CommonController<Alumno, AlumnoService>{
	
	@GetMapping("/alumnos-por-curso")
	public ResponseEntity<?> obtenerAlumnosPorCurso(@RequestParam List<Long> ids){ //recibe una lista con los ids
		return ResponseEntity.ok(service.findAllById(ids));
	}
	
	@GetMapping("/uploads/img/{id}")
	public ResponseEntity<?> verFoto(@PathVariable Long id){
		
		Optional<Alumno> o = service.findById(id); // Buscar el alumno
		
		//verifica que alumno no venga vacio o que no tenga foto, entonces manda un Not Found
		if(o.isEmpty() ||  o.get().getFoto() == null) {
			return ResponseEntity.notFound().build(); //Se construye la respuesta con un contenido vacio 404
		}
		
		//crea el recurso de imagen del tipo byte arrany resource y se pasan los bytes
		//como argumento
		Resource imagen = new ByteArrayResource(o.get().getFoto());
		
		
		return ResponseEntity.ok()
				.contentType(MediaType.IMAGE_JPEG)
				.body(imagen);
		
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> editar(@Valid @RequestBody Alumno alumno, BindingResult result, @PathVariable Long id){
		if(result.hasErrors()) {
			return this.validar(result);
		}
		
		Optional<Alumno> o = service.findById(id); // Buscar el alumno
		
		if(o.isEmpty()) {
			return ResponseEntity.notFound().build(); //Se construye la respuesta con un contenido vacio 404
		}
		
		//Se toman los valores que vienen del alumno en el request
		Alumno alumnoDb = o.get();
		alumnoDb.setNombre(alumno.getNombre());
		alumnoDb.setApellido(alumno.getApellido());
		alumnoDb.setEmail(alumno.getEmail());
		
		return ResponseEntity.status(HttpStatus.CREATED).body(service.save(alumnoDb)); //El alumnoDb se tiene que persistir
		
	}	
	
	@GetMapping("/filtrar/{term}")
	public ResponseEntity<?> filtrar(@PathVariable String term){ //Si el arg se llamara diferente habria que agregar el atributo name o value y se tendria que indicar el nombre de la ruta
		return ResponseEntity.ok(service.findByNombreOrApellido(term));
	}

	//método post para agregar nueva foto
	//NOTA: acá no se usa requestbody porque no es un JSON
	@PostMapping("/crear-con-foto")
	public ResponseEntity<?> crearConFoto(@Valid Alumno alumno, BindingResult result, 
			@RequestParam MultipartFile archivo) throws IOException {
		//Si es distinto de vacio se asigna al alumno, se pasan los bytes que se obtienen
		//A través del archivo
		if(!archivo.isEmpty()) {
			alumno.setFoto(archivo.getBytes());
		}
		return super.crear(alumno, result);
	}
	
	//método put para editar foto
	@PutMapping("/editar-con-foto/{id}")
	public ResponseEntity<?> editarConFoto(@Valid Alumno alumno, BindingResult result,
			@PathVariable Long id, @RequestParam MultipartFile archivo) throws IOException{
		if(result.hasErrors()) {
			return this.validar(result);
		}
		
		Optional<Alumno> o = service.findById(id); // Buscar el alumno
		
		if(o.isEmpty()) {
			return ResponseEntity.notFound().build(); //Se construye la respuesta con un contenido vacio 404
		}
		
		//Se toman los valores que vienen del alumno en el request
		Alumno alumnoDb = o.get();
		alumnoDb.setNombre(alumno.getNombre());
		alumnoDb.setApellido(alumno.getApellido());
		alumnoDb.setEmail(alumno.getEmail());
		
		if(!archivo.isEmpty()) {
			alumnoDb.setFoto(archivo.getBytes()); //es alumnodb no alumno, ya que es el que encontró por su id
		}
		
		return ResponseEntity.status(HttpStatus.CREATED).body(service.save(alumnoDb)); //El alumnoDb se tiene que persistir
		
	}	
	
	

}
