export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken?: string;
  tokenType?: string;
  expiresIn?: number;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface CurrentUser {
  id: number;
  username: string;
  name: string;
  perfil?: string;
  authorities: string[];
  root: boolean;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword?: string;
}
