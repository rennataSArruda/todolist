export type PapelCodigo = 'USUARIO' | 'TAREFA_CATEGORIA' | 'TAREFA' | 'INDICADORES_TAREFAS';

export interface Perfil {
  id?: number;
  codigo: string;
  descricao: string;
  permissoes: PerfilPermissao[];
}

export interface PerfilPermissao {
  id?: number;
  papelCodigo?: PapelCodigo | string;
  papelDescricao?: string;
  visualizar?: boolean;
  criar?: boolean;
  editar?: boolean;
  bloquear?: boolean;
}

export interface PerfilFilter {
  codigo?: string;
  descricao?: string;
}
