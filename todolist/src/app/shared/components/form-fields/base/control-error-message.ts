import { AbstractControl, ValidationErrors } from '@angular/forms';

export type ControlErrorMessages = Record<string, string | ((error: unknown) => string)>;

export const DEFAULT_CONTROL_ERROR_MESSAGES: ControlErrorMessages = {
  required: 'Campo obrigatorio.',
  email: 'Informe um e-mail valido.',
  min: (error) => `Valor minimo: ${getErrorValue(error, 'min')}.`,
  max: (error) => `Valor maximo: ${getErrorValue(error, 'max')}.`,
  minlength: (error) => `Minimo de ${getErrorValue(error, 'requiredLength')} caracteres.`,
  maxlength: (error) => `Maximo de ${getErrorValue(error, 'requiredLength')} caracteres.`,
  pattern: 'Formato invalido.',
  matDatepickerParse: 'Data invalida.',
  matDatepickerMin: 'Data menor que a permitida.',
  matDatepickerMax: 'Data maior que a permitida.',
};

export function getControlErrorMessage(
  control: AbstractControl | null | undefined,
  customMessages: ControlErrorMessages = {},
): string | null {
  if (!control?.errors) {
    return null;
  }

  const errors = control.errors;
  const firstErrorKey = Object.keys(errors)[0];

  if (!firstErrorKey) {
    return null;
  }

  const message = customMessages[firstErrorKey] ?? DEFAULT_CONTROL_ERROR_MESSAGES[firstErrorKey];

  if (!message) {
    return 'Campo invalido.';
  }

  return typeof message === 'function' ? message(errors[firstErrorKey]) : message;
}

export function hasControlError(control: AbstractControl | null | undefined): boolean {
  return Boolean(control && control.invalid && (control.dirty || control.touched));
}

function getErrorValue(error: unknown, key: string): string {
  if (!isValidationErrorObject(error)) {
    return '';
  }

  const value = error[key];
  return value === null || value === undefined ? '' : String(value);
}

function isValidationErrorObject(error: unknown): error is ValidationErrors {
  return typeof error === 'object' && error !== null;
}
