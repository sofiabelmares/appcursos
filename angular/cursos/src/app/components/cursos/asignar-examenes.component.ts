import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Curso } from 'src/app/models/curso';
import { Examen } from 'src/app/models/examen';
import { CursoService } from 'src/app/services/curso.service';
import { ExamenService } from 'src/app/services/examen.service';
import { map, flatMap } from 'rxjs/operators';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import Swal from 'sweetalert2';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';

@Component({
  selector: 'app-asignar-examenes',
  templateUrl: './asignar-examenes.component.html',
  styleUrls: ['./asignar-examenes.component.css']
})
export class AsignarExamenesComponent implements OnInit {

  curso: Curso;

  autocompleteControl = new FormControl();
  examenesFiltrados: Examen[] = [];

  examenesAsignar: Examen[] = []; //arreglo para los examenes que se van a asignar
  examenes: Examen[] = []; //para actualizar la lista de los examenes del curso

  mostrarColumnas = ['nombre', 'asignatura', 'eliminar'];
  mostrarColumnasExamenes = ['id', 'nombre', 'asignatura', 'eliminar'];

  //paginador
  dataSource: MatTableDataSource<Examen>;
  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  pageSizeOptions = [3, 5, 10, 20, 50];

  tabIndex = 0;

  constructor(private route: ActivatedRoute,
    private router: Router,
    private cursoService: CursoService,
    private examenService: ExamenService) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id: number = +params.get('id'); //con el + se hace cast para tipo number
      this.cursoService.ver(id).subscribe(c => {
        this.curso = c
        this.examenes = this.curso.examenes;
        this.iniciarPaginador();
      }); //obtener id
    });
    this.autocompleteControl.valueChanges.pipe(
      //capturar el texto
      map(valor => typeof valor === 'string' ? valor : valor.nombre), //si se recibe un objeto examen se convierte a string
      flatMap(valor => valor ? this.examenService.filtrarPorNombre(valor) : [])
    ).subscribe(examenes => this.examenesFiltrados = examenes);
  }

  private iniciarPaginador(){
    this.dataSource = new MatTableDataSource<Examen>(this.examenes);
    this.dataSource.paginator = this.paginator;
    this.paginator._intl.itemsPerPageLabel = 'Registros por página';    
  }

  mostrarNombre(examen?: Examen): string {
    return examen ? examen.nombre : '';
  }

  seleccionarExamen(event: MatAutocompleteSelectedEvent): void {
    const examen = event.option.value as Examen;

    if (!this.existe(examen.id)) {
      //para actualizar la tabla de examenes asignados al agregar uno nuevo
      this.examenesAsignar = this.examenesAsignar.concat(examen);

      console.log(this.examenesAsignar);
    } else{
      Swal.fire(
        'Error',
        `El examen \"${examen.nombre}\" ya está asignado a este curso`,
        'error'
      );
    }
    this.autocompleteControl.setValue('');
    event.option.deselect();
    event.option.focus();
  }

  //validar si ya existe el examen en el curso
  private existe(id: number): boolean {
    let existe = false;
    this.examenesAsignar.concat(this.examenes)
      .forEach(e => {
        if (id === e.id) {
          existe = true;
        }
      });
    return existe;
  }

  eliminarDelAsignar(examen: Examen): void{
    this.examenesAsignar = this.examenesAsignar.filter(
      e => examen.id !== e.id
    );
  }

  //asginar examenes
  asignar(): void{
    console.log(this.examenesAsignar);
    this.cursoService.asignarExamenes(this.curso, this.examenesAsignar)
    .subscribe(curso => {
      this.examenes = this.examenes.concat(this.examenesAsignar); //concatenar los del curso y x asignar
      let titulo = this.examenesAsignar.length===1? "Examen asignado": "Exámenes asignados";
      let mensaje = this.examenesAsignar.length===1? `Examen asignado al curso ${curso.nombre}`: `Exámenes asignados al curso ${curso.nombre}`;
      this.iniciarPaginador();
      this.examenesAsignar = [];
      Swal.fire(
        titulo,
        mensaje,
        'success'
      );
      this.tabIndex = 2;
    });
  }

  eliminarExamenDelCurso(examen: Examen): void{
    Swal.fire({
      title: 'Confirmar',
      text: `¿Seguro que desea eliminar ${examen.nombre} del curso ${this.curso.nombre}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.cursoService.eliminarExamen(this.curso, examen)
          .subscribe(curso => {
            //Actualizar lista de alumnos
            this.examenes = this.examenes.filter(e => e.id !== examen.id);
            this.iniciarPaginador();
            Swal.fire(
              'Eliminado',
              `Se ha eliminado ${examen.nombre} del curso ${curso.nombre}`,
              'success'
            );
          });
      }
    });
  }

}
