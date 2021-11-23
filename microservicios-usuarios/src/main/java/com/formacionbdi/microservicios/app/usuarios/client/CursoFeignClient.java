package com.formacionbdi.microservicios.app.usuarios.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "microservicio-cursos") //donde nos vamos a comunicar del app.prop
public interface CursoFeignClient {
	
	@DeleteMapping("/eliminar-alumno/{id}")
	public void eliminarCursoAlumnoPorId(@PathVariable Long id); //aqui se usa pathvariable porque vamos a mandar el id que variará

}
