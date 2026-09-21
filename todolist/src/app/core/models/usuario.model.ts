export interface Usuario {
  id?: number;
  username: string;
  email: string;
  name: string;
  ativo?: boolean;
  password?: string;
  perfilId?: number;
}

export interface UsuarioFilter {
  username?: string;
  name?: string;
  quickSearch?: string;
  ativo?: boolean;
}