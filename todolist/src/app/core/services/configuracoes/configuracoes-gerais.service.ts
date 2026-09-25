import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { AtualizarConfiguracoesGeraisRequest, ConfiguracoesGerais } from '../../models/configuracoes-gerais.model';
import { AbstractApiService } from '../base';

@Injectable({ providedIn: 'root' })
export class ConfiguracoesGeraisService extends AbstractApiService {
  constructor(http: HttpClient) { super(http); }

  get(): Observable<ConfiguracoesGerais> {
    return this.http.get<ConfiguracoesGerais>(this.buildUrl('/api/configuracoes-gerais'));
  }

  update(request: AtualizarConfiguracoesGeraisRequest): Observable<ConfiguracoesGerais> {
    return this.http.put<ConfiguracoesGerais>(this.buildUrl('/api/configuracoes-gerais'), request);
  }
}
