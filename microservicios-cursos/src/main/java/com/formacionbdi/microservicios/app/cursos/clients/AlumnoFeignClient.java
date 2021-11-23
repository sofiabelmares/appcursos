package com.formacionbdi.microservicios.app.cursos.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.formacionbdi.microservicios.commons.alumnos.models.entity.Alumno;

@FeignClient(name = "microservicio-usuarios") //nombre del microservicio al cual nos comunicaremos
public interface AlumnoFeignClient {
	
	@GetMapping("/alumnos-por-curso") //endpoint al cual nos queremos comunicar
	public Iterable<Alumno> obtenerAlumnosPorCurso(@RequestParam List<Long> ids);
	//es un tipo iterable de alumnos y copiamos el nombre del método exacto del alumno controller

}
