export interface PermissaoFixa {
  id?: number;
  codigo: string;
  descricao: string;
}

export interface Papel {
  id?: number;
  codigo: string;
  descricao: string;
  permissoes: PermissaoFixa[];
}
