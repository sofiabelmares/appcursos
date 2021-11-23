package com.formacionbdi.microservicios.app.respuestas.services;

//import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import com.formacionbdi.microservicios.app.respuestas.clients.ExamenFeignClient;
import com.formacionbdi.microservicios.app.respuestas.models.entity.Respuesta;
import com.formacionbdi.microservicios.app.respuestas.models.repository.RespuestaRepository;
//import com.formacionbdi.microservicios.commons.examenes.models.entity.Examen;
//import com.formacionbdi.microservicios.commons.examenes.models.entity.Pregunta;

@Service // para que se puede registrar como componente de spring
public class RespuestaServiceImpl implements RespuestaService {

	@Autowired // Inyectar el repositorio
	private RespuestaRepository repository;

	//@Autowired
	//private ExamenFeignClient examenClient;

	@Override
	public Iterable<Respuesta> saveAll(Iterable<Respuesta> respuestas) {
		return repository.saveAll(respuestas);
	}

	@Override
	public Iterable<Respuesta> findRespuestaByAlumnoByExamen(Long alumnoId, Long examenId) {
		/*Examen examen = examenClient.obtenerExamenPorId(examenId); // Obtener examen por microservicio
		List<Pregunta> preguntas = examen.getPreguntas(); // obtener las preguntas através del examen
		List<Long> preguntaIds = preguntas.stream().map(p -> p.getId()).collect(Collectors.toList()); // Convertirlo
																										// list
		List<Respuesta> respuestas = (List<Respuesta>) repository.findRespuestaByAlumnoByPreguntaIds(alumnoId,
				preguntaIds);
		respuestas = respuestas.stream().map(r -> { // le agregamos el objeto pregunta
			preguntas.forEach(p -> {
				if (p.getId() == r.getPreguntaId()) {
					r.setPregunta(p);
				}
			});
			return r;
		}).collect(Collectors.toList());*/ //ya no es necesario ir a buscar al examen porque lo podemos tener en mongo
		List<Respuesta> respuestas = (List<Respuesta>) repository.findRespuestaByAlumnoByExamen(alumnoId,examenId);
		return respuestas;
	}

	@Override
	public Iterable<Long> findExamenesIdsConRespuestasByAlumno(Long alumnoId) {
		/*List<Respuesta> respuestasAlumno = (List<Respuesta>) repository.findByAlumnoId(alumnoId);
		List<Long> examenIds = Collections.emptyList();
		
		if(respuestasAlumno.size() > 0) {
		List<Long> preguntaIds = respuestasAlumno.stream().map(r -> r.getPreguntaId()).collect(Collectors.toList());
		examenIds = examenClient.obtenerExamenesIdsPorPreguntasIdRespondidas(preguntaIds);
		}*/
		List<Respuesta> respuestasAlumno = (List<Respuesta>) repository.findExamenesIdsConRespuestasByAlumno(alumnoId);
		List<Long> examenIds = respuestasAlumno.stream().map(r -> r.getPregunta().getExamen().getId())
				.distinct()
				.collect(Collectors.toList());
		return examenIds;
	}

	@Override
	public Iterable<Respuesta> findByAlumnoId(Long alumnoId) {
		return repository.findByAlumnoId(alumnoId);
	}

}
