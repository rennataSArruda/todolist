import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Tarefa, TarefaFilter, TarefaView } from '../../models/tarefa.model';
import { BlockableSearchCrudApiService } from '../base';

@Injectable({
  providedIn: 'root',
})
export class TarefaService extends BlockableSearchCrudApiService<Tarefa, TarefaFilter, TarefaView> {
  constructor(http: HttpClient) {
    super(http, '/api/tarefa');
  }
}