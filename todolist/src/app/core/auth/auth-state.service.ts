import { Injectable, inject, signal } from '@angular/core';
import { Observable, from, map, of, switchMap, tap, throwError } from 'rxjs';

import { CurrentUser, LoginRequest, LoginResponse } from '../models/auth.model';
import { AuthService } from '../services/auth/auth.service';
import { CurrentUserService } from '../services/auth/current-user.service';
import { TokenStorageService } from './token-storage.service';

@Injectable({
  providedIn: 'root',
})
export class AuthStateService {
  private readonly authService = inject(AuthService);
  private readonly currentUserService = inject(CurrentUserService);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly currentUserSignal = signal<CurrentUser | null>(null);
  private readonly authenticatedSignal = signal(false);

  readonly currentUser = this.currentUserSignal.asReadonly();
  readonly isAuthenticated = this.authenticatedSignal.asReadonly();

  getAuthorizationHeader(): Promise<string | null> {
    return this.tokenStorage.getAuthorizationHeader();
  }

  async hasValidSession(): Promise<boolean> {
    if (!(await this.tokenStorage.isAuthenticated())) {
      this.authenticatedSignal.set(false);
      return false;
    }

    if (await this.tokenStorage.isAccessTokenExpired()) {
      this.clearSession();
      return false;
    }

    this.authenticatedSignal.set(true);
    return true;
  }

  loadCurrentUser(): Observable<CurrentUser> {
    const currentUser = this.currentUserSignal();

    if (currentUser) {
      return of(currentUser);
    }

    return this.currentUserService.me().pipe(tap((user) => this.setCurrentUser(user)));
  }

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.authService.login(request).pipe(
      switchMap((response) => from(this.saveSession(response)).pipe(map(() => response))),
    );
  }

  refreshSession(): Observable<LoginResponse> {
    return from(this.tokenStorage.getRefreshToken()).pipe(
      switchMap((refreshToken) => {
        if (!refreshToken) {
          this.clearSession();
          return throwError(() => new Error('Refresh token nao encontrado.'));
        }

        return this.authService.refresh({ refreshToken });
      }),
      switchMap((response) => from(this.saveSession(response)).pipe(map(() => response))),
    );
  }

  logout(): Observable<void> {
    return from(this.tokenStorage.getRefreshToken()).pipe(
      switchMap((refreshToken) => {
        if (!refreshToken) {
          this.clearSession();
          return throwError(() => new Error('Refresh token nao encontrado.'));
        }

        return this.authService.logout({ refreshToken });
      }),
      tap(() => this.clearSession()),
    );
  }

  clearSession(): void {
    this.tokenStorage.clear();
    this.authenticatedSignal.set(false);
    this.currentUserSignal.set(null);
  }

  setCurrentUser(user: CurrentUser): void {
    this.currentUserSignal.set(user);
  }

  private async saveSession(response: LoginResponse): Promise<void> {
    await this.tokenStorage.save(response);
    this.authenticatedSignal.set(true);
  }
}