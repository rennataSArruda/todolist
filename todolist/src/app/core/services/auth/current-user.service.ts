import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { ChangePasswordRequest, CurrentUser } from '../../models/auth.model';
import { AbstractApiService } from '../base';

@Injectable({
  providedIn: 'root',
})
export class CurrentUserService extends AbstractApiService {
  private readonly resourcePath = '/api/auth';

  constructor(http: HttpClient) {
    super(http);
  }

  me(): Observable<CurrentUser> {
    return this.http.get<CurrentUser>(this.buildUrl(`${this.resourcePath}/me`));
  }

  changePassword(request: ChangePasswordRequest): Observable<void> {
    return this.http.put<void>(this.buildUrl(`${this.resourcePath}/password`), request);
  }
}