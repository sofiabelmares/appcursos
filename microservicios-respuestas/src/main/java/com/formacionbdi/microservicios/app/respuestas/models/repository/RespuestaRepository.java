package com.formacionbdi.microservicios.app.respuestas.models.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.formacionbdi.microservicios.app.respuestas.models.entity.Respuesta;

public interface RespuestaRepository extends MongoRepository<Respuesta, String> {
	
	@Query("{'alumnoId': ?0, 'preguntaId': { $in: ?1}}")
	public Iterable<Respuesta> findRespuestaByAlumnoByPreguntaIds(Long alumnoId, Iterable<Long> preguntaIds);
	
	@Query("{'alumnoId': ?0}")
	public Iterable<Respuesta> findByAlumnoId(Long alumnoId);
	//En estos query no se trae atributos sino todo
	//Acá se usa el fetch porque se retorna todo en la consulta
	//@Query("select r from Respuesta r join fetch r.pregunta p"
	//		+ " join fetch p.examen e where r.alumnoId=?1 and e.id=?2 ")
	//public Iterable<Respuesta> findRespuestaByAlumnoByExamen(Long alumnoId, Long examenId);
	
	//Acá solo retorna el id del examen y no los relacionados, por eso no se usa fetch
	//@Query("select e.id from Respuesta r join r.pregunta p join p.examen e"
	//		+ " where r.alumnoId=?1 group by e.id")
	//public Iterable<Long> findExamenesIdsConRespuestasByAlumno(Long alumnoId);
	
	@Query("{'alumnoId': ?0, 'pregunta.examen.id': ?1}") //pregunta es un atributo de respuesta, y pregunta
														// tiene el examen, y el examen tiene su id examen
	public Iterable<Respuesta> findRespuestaByAlumnoByExamen(Long alumnoId, Long examenId);
	
	@Query(value = "{'alumnoId': ?0}", fields = "{'pregunta.examen.id': 1}") //indicamos que campos queremos que retorne el json
	public Iterable<Respuesta> findExamenesIdsConRespuestasByAlumno(Long alumnoId);
	
}
