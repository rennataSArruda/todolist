export type SelectValue = boolean | number | string;

export interface SelectOption<TValue extends SelectValue = number | string, TRaw = unknown> {
  value: TValue;
  label: string;
  disabled?: boolean;
  raw?: TRaw;
}

export type SelectCompareWith<TValue = unknown, TOption = unknown> = (
  value: TValue | null,
  option: TOption | null,
) => boolean;

export type SelectDisplayWith<TOption = unknown> = (option: TOption | null) => string;
