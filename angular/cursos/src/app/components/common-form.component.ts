import { Component, Directive, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2'
import { Generic } from '../models/generic';
import { CommonService } from '../services/common.service';

@Directive()
export abstract class CommonFormComponent<E extends Generic, S extends CommonService<E>> 
implements OnInit {

  titulo: string;
  model: E;
  error: any;
  protected redirect: string;
  protected nombreModel: string;

  constructor(protected service: S, 
    protected router:Router,
    protected route: ActivatedRoute) { } //Inyectar para buscar el id del alumno

  ngOnInit() {
    this.route.paramMap.subscribe(params => { //tenemos que suscribirnos porque es reactivo
      const id: number = +params.get('id'); //el + es para el cast
      if(id){
        this.service.ver(id).subscribe(m => { //Para cambiar leyenda de Crear/Editar en la clase common padre
          this.model = m,                     // Si ya existe el ID pone editar
          this.titulo = "Editar " + this.nombreModel; 
        });
      }
    })
  }

  public crear(): void{
    this.service.crear(this.model).subscribe(m => {
      console.log(m);
      Swal.fire(`Nuevo`, `${this.nombreModel} ${m.nombre} creado con éxito`,'success');
      this.router.navigate([this.redirect]); //redirigir al listado de alumnos
    }, err => {
      if(err.status === 400){
        this.error = err.error;
        console.log(this.error);
      }
    });
  }

  public editar(): void{
    this.service.editar(this.model).subscribe(m => {
      console.log(m);
      Swal.fire(`Modificado`, `${this.nombreModel} ${m.nombre} actualizado con éxito`, 'success');
      this.router.navigate([this.redirect]); //redirigir al listado de alumnos
    }, err => {
      if(err.status === 400){
        this.error = err.error;
        console.log(this.error);
      }
    });
  }

}
