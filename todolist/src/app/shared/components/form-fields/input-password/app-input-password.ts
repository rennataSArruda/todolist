import { Component, Optional, Self, SkipSelf } from '@angular/core';
import { NgControl } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';

import { AbstractFormField, AppFormValidationDirective } from '../base';

@Component({
  selector: 'app-input-password',
  standalone: true,
  imports: [MatIconModule],
  templateUrl: './app-input-password.html',
  styleUrl: './app-input-password.scss',
})
export class AppInputPassword extends AbstractFormField<string> {
  showPassword = false;

  constructor(
    @Optional() @Self() ngControl: NgControl | null,
    @Optional() @SkipSelf() formValidation: AppFormValidationDirective | null,
  ) {
    super(ngControl, formValidation);
    this.autocomplete = 'current-password';
  }

  get inputType(): 'password' | 'text' {
    return this.showPassword ? 'text' : 'password';
  }

  get visibilityIcon(): 'visibility' | 'visibility_off' {
    return this.showPassword ? 'visibility_off' : 'visibility';
  }

  get visibilityLabel(): string {
    return this.showPassword ? 'Ocultar senha' : 'Mostrar senha';
  }

  handleInput(event: Event): void {
    const input = event.target as HTMLInputElement;

    this.value = input.value;
    this.onChange(input.value);
  }

  toggleVisibility(): void {
    this.showPassword = !this.showPassword;
  }
}