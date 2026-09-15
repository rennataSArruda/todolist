export type AuthErrorAction = 'TOKEN_REFRESH' | 'LOGIN' | 'FORBIDDEN';

export type AuthErrorCode =
  | 'AUTH_TOKEN_EXPIRED'
  | 'AUTH_TOKEN_INVALID'
  | 'AUTH_TOKEN_INVALIDATED'
  | 'AUTH_SESSION_INVALID'
  | 'AUTH_ACCESS_DENIED'
  | 'AUTH_USER_WITHOUT_PROFILE';

export interface ApiError {
  code?: string | AuthErrorCode;
  message?: string;
  status?: number;
  action?: AuthErrorAction | string;
  timestamp?: string;
  path?: string;
  errors?: ApiFieldError[];
}

export interface ApiFieldError {
  field?: string;
  message: string;
}
