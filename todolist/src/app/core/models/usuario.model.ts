export interface Usuario {
  id?: number;
  username: string;
  name: string;
  ativo?: boolean;
  password?: string;
}

export interface UsuarioFilter {
  username?: string;
  name?: string;
  ativo?: boolean;
}
