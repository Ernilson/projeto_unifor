import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Curso } from '../model/Curso';
import { BaseCrudService } from './base-crud.service';

@Injectable({ providedIn: 'root' })
export class CursoService extends BaseCrudService<Curso> {
  constructor(http: HttpClient) {
    super(http, 'cursos');
  }
}


