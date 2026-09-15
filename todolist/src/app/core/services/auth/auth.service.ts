import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { LoginRequest, LoginResponse, RefreshTokenRequest } from '../../models/auth.model';
import { AbstractApiService } from '../base';

@Injectable({
  providedIn: 'root',
})
export class AuthService extends AbstractApiService {
  private readonly resourcePath = '/public/auth';

  constructor(http: HttpClient) {
    super(http);
  }

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.buildUrl(`${this.resourcePath}/login`), request);
  }

  refresh(request: RefreshTokenRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.buildUrl(`${this.resourcePath}/refresh`), request);
  }

  logout(): Observable<void> {
    return this.http.post<void>(this.buildUrl(`${this.resourcePath}/logout`), null);
  }
}