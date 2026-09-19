import { ICONS, IconName } from '../../../core/fixeds/icons';
import { ROLES, Role } from '../../../core/fixeds/roles';

export interface NavigationItem {
  readonly label: string;
  readonly icon: IconName;
  readonly route?: string;
  readonly permission?: Role;
  readonly rootOnly?: boolean;
}

export const PRIMARY_NAVIGATION_ITEMS: readonly NavigationItem[] = [
  {
    label: 'Dashboard',
    icon: ICONS.INDICATORS,
    route: '/dashboard',
  },
  {
    label: 'Tarefas',
    icon: ICONS.TASK,
    permission: ROLES.TASK_VIEW,
  },
  {
    label: 'Categorias',
    icon: ICONS.CATEGORY,
    permission: ROLES.TASK_CATEGORY_VIEW,
  }
];

export const ADMINISTRATION_NAVIGATION_ITEMS: readonly NavigationItem[] = [
  {
    label: 'Usuários',
    icon: ICONS.USER,
    rootOnly: true,
  },
  {
    label: 'Perfis',
    icon: ICONS.PROFILE,
    rootOnly: true,
  },
  {
    label: 'Configurações',
    icon: ICONS.SETTINGS,
    rootOnly: true,
  },
];
