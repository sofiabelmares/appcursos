package com.formacionbdi.microservicios.app.usuarios.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.formacionbdi.microservicios.app.usuarios.client.CursoFeignClient;
import com.formacionbdi.microservicios.app.usuarios.models.repository.AlumnoRepository;
import com.formacionbdi.microservicios.commons.alumnos.models.entity.Alumno;
import com.formacionbdi.microservicios.commons.services.CommonServiceImpl;

@Service //Registrar este componente en el contenedor, estereotipo de component, crear componentes de sprint
public class AlumnoServiceImpl extends CommonServiceImpl<Alumno, AlumnoRepository> implements AlumnoService {

	@Autowired
	private CursoFeignClient clientCurso;
	
	@Override
	@Transactional(readOnly = true)
	public List<Alumno> findByNombreOrApellido(String term) {
		return repository.findByNombreOrApellido(term);
	}

	@Override
	@Transactional(readOnly = true)
	public Iterable<Alumno> findAllById(Iterable<Long> ids) {
		return repository.findAllById(ids);
	}

	@Override
	public void eliminarCursoAlumnoPorId(Long id) {
		clientCurso.eliminarCursoAlumnoPorId(id);
	}

	//Implementar el metodo deleteById de common
	@Override
	@Transactional //Anotar porque se está eliminando por id
	public void deleteById(Long id) {
		super.deleteById(id);
		this.eliminarCursoAlumnoPorId(id);
		//El transactional sirve para que se realice todo en la misma transacción, si se cumple todo el commit
		// se borra por completo
	}

	@Override
	@Transactional(readOnly = true)
	public Iterable<Alumno> findAll() {
		return repository.findAllByOrderByIdAsc();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Alumno> findAll(Pageable pageable) {
		return repository.findAllByOrderByIdAsc(pageable);
	}
	
}
