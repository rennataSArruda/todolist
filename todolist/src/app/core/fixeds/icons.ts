export const ICONS = {
  DASHBOARD: 'dashboard',
  TASK: 'checklist',
  INDICATORS: 'insert_chart',
  CATEGORY: 'category',
  USER: 'group',
  PROFILE: 'badge',
  SETTINGS: 'settings',
} as const;

export type IconName = typeof ICONS[keyof typeof ICONS];
