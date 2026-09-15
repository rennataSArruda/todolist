import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Papel, TarefaPrioridade, TarefaStatus } from '../../models/tabela-fixa.model';
import { TabelaFixaApiService } from '../base';

@Injectable({
  providedIn: 'root',
})
export class TarefaStatusService extends TabelaFixaApiService<TarefaStatus> {
  constructor(http: HttpClient) {
    super(http, '/api/fixed/tarefa-status');
  }
}

@Injectable({
  providedIn: 'root',
})
export class TarefaPrioridadeService extends TabelaFixaApiService<TarefaPrioridade> {
  constructor(http: HttpClient) {
    super(http, '/api/fixed/tarefa-prioridade');
  }
}

@Injectable({
  providedIn: 'root',
})
export class PapelService extends TabelaFixaApiService<Papel> {
  constructor(http: HttpClient) {
    super(http, '/api/fixed/papel');
  }
}