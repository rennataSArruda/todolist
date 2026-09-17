import { HttpErrorResponse, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, from, switchMap, throwError } from 'rxjs';

import { AuthStateService } from '../auth/auth-state.service';
import { ApiError } from '../models/api-error.model';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const authState = inject(AuthStateService);

  return from(addAuthorizationHeader(request, authState)).pipe(
    switchMap((authenticatedRequest) =>
      next(authenticatedRequest).pipe(
        catchError((error: unknown) => {
          if (!shouldTryRefresh(error, request)) {
            return throwError(() => error);
          }

          return authState.refreshSession().pipe(
            switchMap(() => from(addAuthorizationHeader(request, authState))),
            switchMap((refreshedRequest) => next(refreshedRequest)),
            catchError((refreshError: unknown) => {
              authState.clearSession();
              return throwError(() => refreshError);
            }),
          );
        }),
      ),
    ),
  );
};

async function addAuthorizationHeader(
  request: HttpRequest<unknown>,
  authState: AuthStateService,
): Promise<HttpRequest<unknown>> {
  const authorizationHeader = await authState.getAuthorizationHeader();

  if (!authorizationHeader || isAuthBootstrapRequest(request)) {
    return request;
  }

  return request.clone({
    setHeaders: {
      Authorization: authorizationHeader,
    },
  });
}

function shouldTryRefresh(error: unknown, request: HttpRequest<unknown>): boolean {
  if (!(error instanceof HttpErrorResponse) || isAuthBootstrapRequest(request)) {
    return false;
  }

  const apiError = error.error as ApiError | undefined;
  return error.status === 401 && apiError?.action === 'TOKEN_REFRESH';
}

function isAuthBootstrapRequest(request: HttpRequest<unknown>): boolean {
  return (
    request.url.includes('/public/auth/login') ||
    request.url.includes('/public/auth/refresh') ||
    request.url.includes('/public/auth/token') ||
    request.url.includes('/public/on')
  );
}
