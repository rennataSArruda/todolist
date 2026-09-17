export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface CurrentUser {
  id: number;
  username: string;
  nome: string;
  perfil?: CurrentUserPerfil;
  authorities: string[];
  root: boolean;
}

export interface CurrentUserPerfil {
  id: number;
  codigo: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword?: string;
}