import { ICONS, IconName } from './icons';

export const ROLES = {
  TASK_VIEW: 'TAREFA_VISUALIZAR',
  TASK_CATEGORY_VIEW: 'TAREFA_CATEGORIA_VISUALIZAR',
  TASK_INDICATORS_VIEW: 'INDICADORES_TAREFAS_VISUALIZAR',
} as const;

export type Role = (typeof ROLES)[keyof typeof ROLES];

export interface PermissionColumn {
  readonly codigo: string;
  readonly label: string;
  readonly icon: IconName | 'visibility' | 'delete';
}

export const PERMISSION_COLUMNS: readonly PermissionColumn[] = [
  { codigo: 'BLOQUEAR', label: 'Bloquear', icon: ICONS.BLOCK },
  { codigo: 'VISUALIZAR', label: 'Visualizar', icon: 'visibility' },
  { codigo: 'CRIAR', label: 'Criar', icon: ICONS.ADD },
  { codigo: 'EDITAR', label: 'Editar', icon: ICONS.EDIT },
  { codigo: 'EXCLUIR', label: 'Excluir', icon: 'delete' },
];
