import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { TarefaAnalitico } from '../../models/tarefa.model';
import { AbstractApiService } from '../base';

@Injectable({
  providedIn: 'root',
})
export class TarefaAnaliticoService extends AbstractApiService {
  private readonly resourcePath = '/api/tarefa-analitico';

  constructor(http: HttpClient) {
    super(http);
  }

  semana(): Observable<TarefaAnalitico[]> {
    return this.http.get<TarefaAnalitico[]>(this.buildUrl(`${this.resourcePath}/semana`));
  }

  atrasadas(): Observable<TarefaAnalitico[]> {
    return this.http.get<TarefaAnalitico[]>(this.buildUrl(`${this.resourcePath}/atrasadas`));
  }

  vencemHoje(): Observable<TarefaAnalitico[]> {
    return this.http.get<TarefaAnalitico[]>(this.buildUrl(`${this.resourcePath}/vencem-hoje`));
  }
}