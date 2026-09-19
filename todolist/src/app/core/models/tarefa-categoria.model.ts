export interface TarefaCategoria {
  id?: number;
  usuarioId?: number;
  nome: string;
  descricao?: string;
  corHex?: string;
  icone?: string;
  ativo?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface TarefaCategoriaFilter {
  usuarioId?: number;
  nome?: string;
  quickSearch?: string;
  ativo?: boolean;
}
