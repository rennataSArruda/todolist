import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { TarefaCategoria, TarefaCategoriaFilter } from '../../models/tarefa-categoria.model';
import { BlockableSearchCrudApiService } from '../base';

@Injectable({
  providedIn: 'root',
})
export class TarefaCategoriaService extends BlockableSearchCrudApiService<TarefaCategoria, TarefaCategoriaFilter> {
  constructor(http: HttpClient) {
    super(http, '/api/tarefa-categoria');
  }
}