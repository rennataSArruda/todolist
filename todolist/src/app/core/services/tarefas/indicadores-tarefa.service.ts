import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { TarefaCategoriaPrioridadeIndicador, TarefaIndicadorItem } from '../../models/tarefa.model';
import { AbstractApiService } from '../base';

@Injectable({
  providedIn: 'root',
})
export class IndicadoresTarefaService extends AbstractApiService {
  private readonly resourcePath = '/api/indicadores/tarefa';

  constructor(http: HttpClient) {
    super(http);
  }

  totalAtivas(): Observable<number> {
    return this.http.get<number>(this.buildUrl(`${this.resourcePath}/total-ativas`));
  }

  porStatus(): Observable<TarefaIndicadorItem[]> {
    return this.http.get<TarefaIndicadorItem[]>(this.buildUrl(`${this.resourcePath}/por-status`));
  }

  porPrioridade(): Observable<TarefaIndicadorItem[]> {
    return this.http.get<TarefaIndicadorItem[]>(this.buildUrl(`${this.resourcePath}/por-prioridade`));
  }

  porCategoria(): Observable<TarefaIndicadorItem[]> {
    return this.http.get<TarefaIndicadorItem[]>(this.buildUrl(`${this.resourcePath}/por-categoria`));
  }

  porCategoriaPrioridade(): Observable<TarefaCategoriaPrioridadeIndicador[]> {
    return this.http.get<TarefaCategoriaPrioridadeIndicador[]>(
      this.buildUrl(`${this.resourcePath}/por-categoria-prioridade`),
    );
  }
}