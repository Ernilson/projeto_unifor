import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Disciplina } from '../model/Disciplina';
import { BaseCrudService } from './base-crud.service';

@Injectable({ providedIn: 'root' })
export class DisciplinaService extends BaseCrudService<Disciplina> {
  constructor(http: HttpClient) {
    super(http, 'disciplinas');
  }
}
