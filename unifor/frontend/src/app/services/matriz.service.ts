import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatrizCurricular } from '../model/MatrizCurricular';
import { BaseCrudService } from './base-crud.service';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class MatrizService extends BaseCrudService<MatrizCurricular> {
  constructor(http: HttpClient) {
    super(http, 'matrizes');
  }

  salvarMatriz(dto: { cursoId: number; semestreId: number; ativa: boolean }): Observable<MatrizCurricular> {
    return this.http.post<MatrizCurricular>(this.apiUrl, dto);
  }

  atualizarMatriz(id: number, dto: { cursoId: number; semestreId: number; ativa: boolean }): Observable<MatrizCurricular> {
    return this.http.put<MatrizCurricular>(`${this.apiUrl}/${id}`, dto);
  }
}

