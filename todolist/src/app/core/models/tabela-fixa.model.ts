export interface TabelaFixa {
  id: number;
  codigo: string;
  descricao: string;
}

export interface TarefaStatus extends TabelaFixa {
  codigo: 'PENDENTE' | 'EM_ANDAMENTO' | 'CONCLUIDA' | 'CANCELADA';
}

export interface TarefaPrioridade extends TabelaFixa {
  codigo: 'BAIXA' | 'MEDIA' | 'ALTA' | 'URGENTE';
  ordem?: number;
}

export interface Papel extends TabelaFixa {
  codigo: 'USUARIO' | 'TAREFA_CATEGORIA' | 'TAREFA' | 'INDICADORES_TAREFAS';
}
