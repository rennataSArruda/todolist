export const ICONS = {
  TASK: 'checklist',
  INDICATORS: 'insert_chart',
  CATEGORY: 'category',
} as const;

export type IconName = typeof ICONS[keyof typeof ICONS];
