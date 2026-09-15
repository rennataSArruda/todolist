export type TarefaStatusCodigo = 'PENDENTE' | 'EM_ANDAMENTO' | 'CONCLUIDA' | 'CANCELADA';
export type TarefaPrioridadeCodigo = 'BAIXA' | 'MEDIA' | 'ALTA' | 'URGENTE';

export interface Tarefa {
  id?: number;
  usuarioId?: number;
  categoriaId?: number;
  statusId?: number;
  prioridadeId?: number;
  titulo: string;
  descricao?: string;
  dataInicio?: string;
  dataFim?: string;
  dataConclusao?: string;
  tempoEstimado?: number;
  tempoGasto?: number;
  posicao?: number;
  ativo?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface TarefaView extends Tarefa {
  usuarioNome?: string;
  categoriaNome?: string;
  categoriaCorHex?: string;
  statusCodigo?: TarefaStatusCodigo;
  statusDescricao?: string;
  prioridadeCodigo?: TarefaPrioridadeCodigo;
  prioridadeDescricao?: string;
}

export interface TarefaFilter {
  usuarioId?: number;
  categoriaId?: number;
  statusId?: number;
  prioridadeId?: number;
  titulo?: string;
  ativo?: boolean;
  dataInicio?: string;
  dataFim?: string;
}

export interface TarefaAnalitico {
  id: number;
  titulo: string;
  descricao?: string;
  dataInicio?: string;
  dataFim?: string;
  status?: string;
  prioridade?: string;
  categoria?: string;
}

export interface TarefaIndicadorItem {
  label: string;
  value: number;
}

export interface TarefaCategoriaPrioridadeIndicador {
  categoria: string;
  prioridade: string;
  total: number;
}
