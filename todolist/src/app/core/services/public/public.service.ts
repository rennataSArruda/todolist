import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { AbstractApiService } from '../base';

@Injectable({
  providedIn: 'root',
})
export class PublicService extends AbstractApiService {
  private readonly resourcePath = '/public';

  constructor(http: HttpClient) {
    super(http);
  }

  on(): Observable<string> {
    return this.http.get(this.buildUrl(`${this.resourcePath}/on`), { responseType: 'text' });
  }
}