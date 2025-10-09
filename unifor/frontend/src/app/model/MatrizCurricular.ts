import { Curso } from "./Curso";
import { Semestre } from "./Semestre";

export class MatrizCurricular {
    id?: number;     
    curso: Curso;
    semestre: Semestre;       
    ativa: boolean;   
  }
  