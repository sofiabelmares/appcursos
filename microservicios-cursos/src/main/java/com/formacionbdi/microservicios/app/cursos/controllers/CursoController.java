package com.formacionbdi.microservicios.app.cursos.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.formacionbdi.microservicios.app.cursos.models.entity.Curso;
import com.formacionbdi.microservicios.app.cursos.models.entity.CursoAlumno;
import com.formacionbdi.microservicios.app.cursos.services.CursoService;
import com.formacionbdi.microservicios.commons.alumnos.models.entity.Alumno;
import com.formacionbdi.microservicios.commons.controllers.CommonController;
import com.formacionbdi.microservicios.commons.examenes.models.entity.Examen;

@RestController
public class CursoController extends CommonController<Curso, CursoService> {

	@Value("${config.balanceador.test}")
	private String balanceadorTest;

	// Para eliminar
	@DeleteMapping("/eliminar-alumno/{id}")
	public ResponseEntity<?> eliminarCursoAlumnoPorId(@PathVariable Long id) {
		service.eliminarCursoAlumnoPorId(id);

		return ResponseEntity.noContent().build();
	}

	// Método para listar los cursos
	@GetMapping // Dar una ruta
	@Override
	public ResponseEntity<?> listar() { // Nos permite construir una respuesta, el HTTP Entity
		List<Curso> cursos = ((List<Curso>) service.findAll()).stream().map(c -> {
			c.getCursoAlumnos().forEach(ca -> {
				Alumno alumno = new Alumno(); // recorremos cada alumno
				alumno.setId(ca.getAlumnoId()); // obtenemos su id
				c.addAlumno(alumno); // vamos llenando la colección alumnos del curso
			});
			return c; // retornar el curso modificado
		}).collect(Collectors.toList());
		return ResponseEntity.ok().body(cursos);
	}

	@GetMapping("/pagina") // Dar una ruta
	@Override
	public ResponseEntity<?> listar(Pageable pageable) { // Nos permite construir una respuesta, el HTTP Entity
		// Retorna toda la información en una pagina, modificar el contenido de la
		// paginación
		Page<Curso> cursos = service.findAll(pageable).map(curso -> {
			curso.getCursoAlumnos().forEach(ca -> { // Contiene el id para la relación con el ms usuarios
				Alumno alumno = new Alumno(); // creamos y recorremos cada alumno
				alumno.setId(ca.getAlumnoId()); // obtenemos su id
				curso.addAlumno(alumno); // vamos llenando la colección alumnos del curso
			});
			return curso;
		});

		return ResponseEntity.ok().body(cursos); // le pasamos los cursos ya modificados
	}

	@GetMapping("/{id}") // Una ruta variable URL que va a ir cambiando
	@Override
	public ResponseEntity<?> ver(@PathVariable Long id) {
		Optional<Curso> o = service.findById(id); // Buscar el Curso, para eso se usa el optional

		if (o.isEmpty()) {
			return ResponseEntity.notFound().build(); // Se construye la respuesta con un contenido vacio 404
		}

		Curso curso = o.get();

		if (curso.getCursoAlumnos().isEmpty() == false) {
			// A partir de la lista alumnos, usando stream y map se convierte a una lista de
			// long
			List<Long> ids = curso.getCursoAlumnos().stream().map(ca -> ca.getAlumnoId()).collect(Collectors.toList()); // pasarlo
																														// a
																														// un
																														// tipo
																														// list

			// Pasar los alumnos
			List<Alumno> alumnos = (List<Alumno>) service.obtenerAlumnosPorCurso(ids);
			curso.setAlumnos(alumnos);
		}

		return ResponseEntity.ok().body(curso);
	}

	@GetMapping("/balanceador-test")
	public ResponseEntity<?> balanceadorTest() {
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("balanceador", balanceadorTest);
		response.put("cursos", service.findAll());
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editar(@Valid @RequestBody Curso curso, BindingResult result, @PathVariable Long id) {
		if (result.hasErrors()) {
			return this.validar(result);
		}

		Optional<Curso> o = this.service.findById(id);// buscar en la BD
		if (o.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		Curso dbCurso = o.get();
		dbCurso.setNombre(curso.getNombre());
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(dbCurso));
	}

	@PutMapping("/{id}/asignar-alumnos")
	public ResponseEntity<?> asignarAlumnos(@RequestBody List<Alumno> alumnos, @PathVariable Long id) {
		Optional<Curso> o = this.service.findById(id);// buscar en la BD
		if (o.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		Curso dbCurso = o.get();
		alumnos.forEach(a -> {
			CursoAlumno cursoAlumno = new CursoAlumno();
			cursoAlumno.setAlumnoId(a.getId());
			cursoAlumno.setCurso(dbCurso);
			dbCurso.addCursoAlumno(cursoAlumno);
		});
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(dbCurso));
	}

	@PutMapping("/{id}/eliminar-alumno")
	public ResponseEntity<?> eliminarAlumno(@RequestBody Alumno alumno, @PathVariable Long id) {
		Optional<Curso> o = this.service.findById(id);// buscar en la BD
		if (o.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		Curso dbCurso = o.get();
		CursoAlumno cursoAlumno = new CursoAlumno();
		cursoAlumno.setAlumnoId(alumno.getId());
		dbCurso.removeCursoAlumno(cursoAlumno);
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(dbCurso));
	}

	@GetMapping("/alumno/{id}")
	public ResponseEntity<?> buscarAlumnoPorId(@PathVariable Long id) {
		Curso curso = service.findCursoById(id);

		if (curso != null) {
			List<Long> examenesIds = (List<Long>) service.obtenerExamenesIdsConRespuestasAlumno(id);

			if (examenesIds != null && examenesIds.size() > 0) {
				List<Examen> examenes = curso.getExamenes().stream().map(examen -> {
					if (examenesIds.contains(examen.getId())) {
						examen.setRespondido(true);
					}
					return examen;
				}).collect(Collectors.toList());

				curso.setExamenes(examenes);
			}
		}

		return ResponseEntity.ok(curso);
	}

	// Asignar un examen a un curso
	@PutMapping("/{id}/asignar-examenes")
	public ResponseEntity<?> asignarExamenes(@RequestBody List<Examen> examenes, @PathVariable Long id) {
		Optional<Curso> o = this.service.findById(id);// buscar en la BD
		if (o.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		Curso dbCurso = o.get();
		examenes.forEach(e -> {
			dbCurso.addExamen(e);
		});
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(dbCurso));
	}

	@PutMapping("/{id}/eliminar-examen")
	public ResponseEntity<?> eliminarExamen(@RequestBody Examen examen, @PathVariable Long id) {
		Optional<Curso> o = this.service.findById(id);// buscar en la BD
		if (o.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		Curso dbCurso = o.get();
		dbCurso.removeExamen(examen);
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(dbCurso));
	}

}
