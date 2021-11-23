import { Asignatura } from "./asignatura";
import { Generic } from "./generic";
import { Pregunta } from "./pregunta";

export class Examen implements Generic{
    id: number;
    nombre: string;
    createdAt: string;
    preguntas: Pregunta[] = [];
    asignaturaPadre: Asignatura;
    asignaturaHijo: Asignatura;
    respondido: boolean;
}
