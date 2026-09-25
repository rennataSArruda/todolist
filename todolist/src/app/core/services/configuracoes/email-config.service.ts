import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { EmailConfig } from '../../models/email-config.model';
import { BlockableSearchCrudApiService } from '../base';

@Injectable({ providedIn: 'root' })
export class EmailConfigService extends BlockableSearchCrudApiService<EmailConfig> {
  constructor(http: HttpClient) { super(http, '/api/email-config'); }

  ativar(id: number): Observable<EmailConfig> {
    return this.http.put<EmailConfig>(this.buildUrl(`${this.resourcePath}/${id}/ativar`), null);
  }
}
