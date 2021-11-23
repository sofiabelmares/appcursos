package com.formacionbdi.microservicios.app.examenes.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.formacionbdi.microservicios.app.examenes.services.ExamenService;
import com.formacionbdi.microservicios.commons.controllers.CommonController;
import com.formacionbdi.microservicios.commons.examenes.models.entity.Examen;
import com.formacionbdi.microservicios.commons.examenes.models.entity.Pregunta;

@RestController
public class ExamenController extends CommonController<Examen, ExamenService>{
	
	@GetMapping("/respondidos-por-preguntas")
	public ResponseEntity<?> obtenerExamenesIdsPorPreguntasIdRespondidas(@RequestParam List<Long> preguntaIds){
		return ResponseEntity.ok().body(service.findExamenesIdsConRespuestasByPreguntasIds(preguntaIds));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> editar(@Valid @RequestBody Examen examen, BindingResult result, @PathVariable Long id){
		
		if(result.hasErrors()) {
			return this.validar(result);
		}
		
		Optional<Examen> o = service.findById(id);
		
		if(!o.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		Examen examenDb = o.get();
		examenDb.setNombre(examen.getNombre());
		
		//Validar si existe o no existe la pregunta en el JSON enviado, si no existe se elimina
		/*List<Pregunta> eliminadas = new ArrayList<>();
		
		//Se itera en cada pregunta 
		examenDb.getPreguntas().forEach(pdb ->{
			if(!examen.getPreguntas().contains(pdb)) {
				eliminadas.add(pdb);
			}
		});*/
		
		List<Pregunta> eliminadas = examenDb.getPreguntas() //Se obtiene las preguntas
		.stream() //Stream de Java 8 
		.filter(pdb -> !examen.getPreguntas().contains(pdb)) //Se filtran
		.collect(Collectors.toList());
		
		eliminadas.forEach(examenDb::removePregunta); //Se eliminan
		
		//Se quedan las que están enviando
		examenDb.setPreguntas(examen.getPreguntas());
		examenDb.setAsignaturaHijo(examen.getAsignaturaHijo());
		examenDb.setAsignaturaPadre(examen.getAsignaturaPadre());
		
		return ResponseEntity.status(HttpStatus.CREATED).body(service.save(examenDb));
	}
	
	@GetMapping("/filtrar/{term}")
	public ResponseEntity<?> filtrar(@PathVariable String term){
		return ResponseEntity.ok(service.findByNombre(term));
	}
	
	@GetMapping("/asignaturas")
	public ResponseEntity<?> listarAsignaturas(){
		return ResponseEntity.ok(service.findAllAsignaturas());
	}

}
