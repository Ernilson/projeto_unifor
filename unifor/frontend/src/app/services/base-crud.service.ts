import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

export abstract class BaseCrudService<T> {
  protected apiUrl: string;

  constructor(
    protected http: HttpClient,
    protected endpoint: string
  ) {
    this.apiUrl = `${environment.apiUrlBase}/api/${endpoint}`;
  }

  listar(): Observable<T[]> {
    return this.http.get<T[]>(this.apiUrl);
  }

  buscarPorId(id: number): Observable<T> {
    return this.http.get<T>(`${this.apiUrl}/${id}`);
  }

  salvar(entidade: T): Observable<T> {
    return this.http.post<T>(this.apiUrl, entidade);
  }

  atualizar(entidade: T & { id?: number }): Observable<T> {
    return this.http.put<T>(`${this.apiUrl}/${entidade.id}`, entidade);
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
