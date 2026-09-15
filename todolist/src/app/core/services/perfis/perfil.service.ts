import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Perfil, PerfilFilter } from '../../models/perfil.model';
import { SearchCrudApiService } from '../base';

@Injectable({
  providedIn: 'root',
})
export class PerfilService extends SearchCrudApiService<Perfil, PerfilFilter> {
  constructor(http: HttpClient) {
    super(http, '/api/perfil');
  }
}