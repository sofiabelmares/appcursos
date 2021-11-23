import { Alumno } from "./alumno";
import { Examen } from "./examen";
import { Generic } from "./generic";

export class Curso implements Generic{
    id: number;
    nombre: string;
    createdAt: string;
    alumnos: Alumno[] = []; //es una lista, de esta forma se inicializa el arreglo vacio
    examenes: Examen[] = [];
}
