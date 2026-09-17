import { Directive, Input, Optional, Self, SkipSelf, booleanAttribute } from '@angular/core';
import { AbstractControl, ControlValueAccessor, NgControl } from '@angular/forms';

import { AppFormValidationDirective } from './app-form-validation.directive';
import { ControlErrorMessages, getControlErrorMessage } from './control-error-message';

let nextFormFieldId = 0;

@Directive()
export abstract class AbstractFormField<TValue = string> implements ControlValueAccessor {
  @Input() label = '';
  @Input() placeholder = '';
  @Input() hint = '';
  @Input() ariaLabel = '';
  @Input() autocomplete = 'off';
  @Input() errorMessages: ControlErrorMessages = {};
  @Input({ transform: booleanAttribute }) required = false;
  @Input({ transform: booleanAttribute }) readonly = false;

  value: TValue | null = null;
  disabled = false;

  readonly fieldId = `form-field-${nextFormFieldId++}`;
  readonly hintId = `${this.fieldId}-hint`;
  readonly errorId = `${this.fieldId}-error`;

  protected onChange: (value: TValue | null) => void = () => undefined;
  protected onTouched: () => void = () => undefined;

  protected constructor(
    @Optional() @Self() public readonly ngControl: NgControl | null,
    @Optional() @SkipSelf() private readonly formValidation: AppFormValidationDirective | null,
  ) {
    if (this.ngControl) {
      this.ngControl.valueAccessor = this;
    }
  }

  get control(): AbstractControl | null {
    return this.ngControl?.control ?? null;
  }

  get hasError(): boolean {
    return Boolean(this.control?.invalid && this.formValidation?.errorsVisible());
  }

  get errorMessage(): string | null {
    if (!this.hasError) {
      return null;
    }

    return getControlErrorMessage(this.control, this.errorMessages);
  }

  get describedById(): string | null {
    if (this.hasError) {
      return this.errorId;
    }

    if (this.hint) {
      return this.hintId;
    }

    return null;
  }

  writeValue(value: TValue | null): void {
    this.value = value;
  }

  registerOnChange(fn: (value: TValue | null) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  markAsTouched(): void {
    this.onTouched();
  }
}