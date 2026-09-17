import { Component, Input, Optional, Self, SkipSelf } from '@angular/core';
import { NgControl } from '@angular/forms';

import { AbstractFormField, AppFormValidationDirective } from '../base';

@Component({
  selector: 'app-textarea',
  standalone: true,
  templateUrl: './app-textarea.html',
  styleUrl: './app-textarea.scss',
})
export class AppTextarea extends AbstractFormField<string> {
  @Input() rows = 4;
  @Input() maxlength: number | null = null;

  constructor(
    @Optional() @Self() ngControl: NgControl | null,
    @Optional() @SkipSelf() formValidation: AppFormValidationDirective | null,
  ) {
    super(ngControl, formValidation);
  }

  handleInput(event: Event): void {
    const textarea = event.target as HTMLTextAreaElement;

    this.value = textarea.value;
    this.onChange(textarea.value);
  }
}