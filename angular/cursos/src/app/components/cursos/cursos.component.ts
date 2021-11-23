import { Component, OnInit } from '@angular/core';
import { Curso } from 'src/app/models/curso';
import { CursoService } from 'src/app/services/curso.service';
import { CommonListarComponent } from '../common-listar.component';

@Component({
  selector: 'app-cursos',
  templateUrl: './cursos.component.html',
  styleUrls: ['./cursos.component.css']
})
export class CursosComponent extends CommonListarComponent<Curso, CursoService> 
implements OnInit {

  //se pasa el service a la clase padre
  constructor(service: CursoService) { 
    super(service);
    this.titulo = 'Listado de Cursos';
    this.nombreModel = Curso.name;
  }

  //se elimina el onInit porque ya está implementado en el padre
}
