import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatrizCurricular } from '../model/MatrizCurricular';
import { BaseCrudService } from './base-crud.service';

@Injectable({ providedIn: 'root' })
export class MatrizService extends BaseCrudService<MatrizCurricular> {
  constructor(http: HttpClient) {
    super(http, 'matrizes');
  }
}

