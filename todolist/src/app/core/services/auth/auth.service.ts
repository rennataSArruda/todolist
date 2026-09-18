import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import {
  ForgotPasswordRequest,
  LoginRequest,
  LoginResponse,
  RefreshTokenRequest,
  ResetPasswordRequest,
} from '../../models/auth.model';
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

  logout(request: RefreshTokenRequest): Observable<void> {
    return this.http.post<void>(this.buildUrl(`${this.resourcePath}/logout`), request);
  }

  forgotPassword(request: ForgotPasswordRequest): Observable<void> {
    return this.http.post<void>(this.buildUrl(`${this.resourcePath}/forgot-password`), request);
  }

  resetPassword(request: ResetPasswordRequest): Observable<void> {
    return this.http.post<void>(this.buildUrl(`${this.resourcePath}/reset-password`), request);
  }
}
