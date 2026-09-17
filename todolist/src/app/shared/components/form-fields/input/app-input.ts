import { Component, Input, Optional, Self, SkipSelf } from '@angular/core';
import { NgControl } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';

import { AbstractFormField, AppFormValidationDirective } from '../base';

export type AppInputType = 'email' | 'number' | 'tel' | 'text' | 'url';

@Component({
  selector: 'app-input',
  standalone: true,
  imports: [MatIconModule],
  templateUrl: './app-input.html',
  styleUrl: './app-input.scss',
})
export class AppInput extends AbstractFormField<string | number> {
  @Input() type: AppInputType = 'text';
  @Input() prefixIcon = '';
  @Input() suffixIcon = '';
  @Input() maxlength: number | null = null;
  @Input() min: number | null = null;
  @Input() max: number | null = null;

  constructor(
    @Optional() @Self() ngControl: NgControl | null,
    @Optional() @SkipSelf() formValidation: AppFormValidationDirective | null,
  ) {
    super(ngControl, formValidation);
  }

  handleInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const nextValue = this.type === 'number' && input.value !== '' ? Number(input.value) : input.value;

    this.value = nextValue;
    this.onChange(nextValue);
  }
}