export interface ConfiguracoesGerais {
  id?: number;
  perfilPadraoPrimeiroAcessoId: number | null;
  createdAt?: string;
  updatedAt?: string;
}

export interface AtualizarConfiguracoesGeraisRequest {
  perfilPadraoPrimeiroAcessoId: number;
}
