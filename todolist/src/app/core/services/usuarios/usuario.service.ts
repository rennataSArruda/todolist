import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Usuario, UsuarioFilter } from '../../models/usuario.model';
import { BlockableSearchCrudApiService } from '../base';

@Injectable({
  providedIn: 'root',
})
export class UsuarioService extends BlockableSearchCrudApiService<Usuario, UsuarioFilter> {
  constructor(http: HttpClient) {
    super(http, '/api/usuario');
  }
}