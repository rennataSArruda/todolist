import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Papel } from '../../models/fixed/papel.model';
import { TabelaFixaApiService } from '../base';

@Injectable({ providedIn: 'root' })
export class PapelService extends TabelaFixaApiService<Papel> {
  constructor(http: HttpClient) {
    super(http, '/api/fixed/papel');
  }
}
