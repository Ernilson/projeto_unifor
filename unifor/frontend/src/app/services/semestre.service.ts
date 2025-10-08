import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Semestre } from '../model/Semestre';
import { BaseCrudService } from './base-crud.service';

@Injectable({ providedIn: 'root' })
export class SemestreService extends BaseCrudService<Semestre> {
  constructor(http: HttpClient) {
    super(http, 'semestres');
  }
}

