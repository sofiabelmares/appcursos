import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute } from '@angular/router';
import { Alumno } from 'src/app/models/alumno';
import { Curso } from 'src/app/models/curso';
import { Examen } from 'src/app/models/examen';
import { Respuesta } from 'src/app/models/respuesta';
import { AlumnoService } from 'src/app/services/alumno.service';
import { CursoService } from 'src/app/services/curso.service';
import { RespuestaService } from 'src/app/services/respuesta.service';
import Swal from 'sweetalert2';
import { ResponderExamenModalComponent } from './responder-examen-modal.component';
import { VerExamenModalComponent } from './ver-examen-modal.component';

@Component({
  selector: 'app-responder-examen',
  templateUrl: './responder-examen.component.html',
  styleUrls: ['./responder-examen.component.css']
})
export class ResponderExamenComponent implements OnInit {

  alumno: Alumno;
  curso: Curso;
  examenes: Examen[] = [];
  mostrarColumnasExamenes = ['id', 'nombre', 'asignatura', 'responder', 'ver', 'preguntas'];

  //paginador
  dataSource: MatTableDataSource<Examen>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator; //static true para poder usar el paginator dentro del oninit
  pageSizeOptions = [3, 5, 10, 20, 50];

  constructor(private route: ActivatedRoute,
    private alumnoService: AlumnoService,
    private cursoService: CursoService,
    private respuestaService: RespuestaService,
    public dialog: MatDialog) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => { //nos suscribimos y obtenemos los parametros a través del route (la ruta activa)
      const id = +params.get('id'); //obtenemos el id
      this.alumnoService.ver(id).subscribe(alumno => { //buscamos el alumno por el id utilizando alumno service
        this.alumno = alumno; //obtenemos el alumno completo
        this.cursoService.obtenerCursoPorAlumnoId(this.alumno).subscribe( //buscamos el curso de dicho alumno, hace un request al backend enviando el ID del alumno para obtener el curso asignado
          curso => { //nos suscribimos, se emite el curso como respuesta de esta petición de forma asíncrona y reactiva
            this.curso = curso; //y lo asignamos
            this.examenes = (curso && curso.examenes) ? curso.examenes : []; //y también se asignan los exámenes
            this.iniciarPaginador()
          }
        )
      });
    });
  }

  private iniciarPaginador(){
    this.dataSource = new MatTableDataSource<Examen>(this.examenes);
    this.dataSource.paginator = this.paginator;
    this.paginator._intl.itemsPerPageLabel = 'Registros por página';    
  }

  public responderExamen(examen: Examen): void{
    const modalRef = this.dialog.open(ResponderExamenModalComponent, {
      width: '750px',
      data: {curso: this.curso, alumno: this.alumno, examen: examen}
    });

    modalRef.afterClosed().subscribe((respuestasMap: Map<number, Respuesta>) => {
      console.log('Modal responder examen ha sido enviado y cerrado');
      console.log(respuestasMap);
      if(respuestasMap){
        const respuestas: Respuesta[] = Array.from(respuestasMap.values()); //el metodo array.from() crea una nueva instancia de Array a partir de un objeto iterable
          this.respuestaService.crear(respuestas).subscribe(rs => {
            examen.respondido = true;
            Swal.fire(
              'Enviadas',
              'Se han enviado las respuestas',
              'success'
            );
            console.log(rs);
          }); 
        }
    });
  }

  verExamen(examen: Examen): void{
    this.respuestaService.obtenerRespuestasPorAlumnoPorExamen(this.alumno, examen)
    .subscribe(rs => {
      const modalRef = this.dialog.open(VerExamenModalComponent, {
        width: '750px',
        data: {curso: this.curso, examen: examen, alumno: this.alumno, respuestas: rs}
      });

      modalRef.afterClosed().subscribe(() => {
        console.log('Modal ver examen cerrado');
      });
    });
  }

}
