import { Component, Input, Optional, Self, SkipSelf } from '@angular/core';
import { NgControl } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';

import { AbstractFormField, AppFormValidationDirective, SelectOption, SelectValue } from '../base';

@Component({
  selector: 'app-select',
  standalone: true,
  imports: [MatSelectModule],
  templateUrl: './app-select.html',
  styleUrl: './app-select.scss',
})
export class AppSelect extends AbstractFormField<SelectValue> {
  @Input() options: readonly SelectOption[] = [];

  constructor(
    @Optional() @Self() ngControl: NgControl | null,
    @Optional() @SkipSelf() formValidation: AppFormValidationDirective | null,
  ) {
    super(ngControl, formValidation);
  }

  protected handleSelection(value: SelectValue): void {
    this.value = value;
    this.onChange(value);
  }
}
