package com.formacionbdi.microservicios.app.cursos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "microservicio-respuestas") // Microservicio con el que nos vamos a comunicar
public interface RespuestaFeignClient {

	// método para indicar a que endpoint nos vamos a comunicar, que
	// parametros vamos a pasar y tipo de retorno
	@GetMapping("/alumno/{alumnoId}/examenes-respondidos") // la ruta tiene que ser la misma a la que nos vamos a
															// comunicar
	public Iterable<Long> obtenerExamenesIdsConRespuestasAlumno(@PathVariable Long alumnoId); // Controlador que
																								// queremos consumir de
																								// respuestas

}
