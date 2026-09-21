export interface Perfil {
  id?: number;
  codigo: string;
  descricao: string;
  ativo?: boolean;
  permissoes: PerfilPermissao[];
}

export interface PerfilPermissao {
  papelCodigo: string;
  permissaoCodigo: string;
}

export interface PerfilFilter {
  codigo?: string;
  descricao?: string;
  ativo?: boolean;
}
