import { Component, Input, Optional, Self, SkipSelf } from '@angular/core';
import { NgControl } from '@angular/forms';

import { AbstractFormField, AppFormValidationDirective } from '../base';

@Component({
  selector: 'app-color-picker',
  standalone: true,
  templateUrl: './app-color-picker.html',
  styleUrl: './app-color-picker.scss',
})
export class AppColorPicker extends AbstractFormField<string> {

  constructor(
    @Optional() @Self() ngControl: NgControl | null,
    @Optional() @SkipSelf() formValidation: AppFormValidationDirective | null,
  ) {
    super(ngControl, formValidation);
  }

  selectColor(color: string): void {
    this.value = color;
    this.onChange(color);
    this.markAsTouched();
  }

  handleColorInput(event: Event): void {
    this.selectColor((event.target as HTMLInputElement).value);
  }
}
