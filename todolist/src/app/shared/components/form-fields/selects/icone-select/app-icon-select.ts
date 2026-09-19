import { Component, Optional, Self, SkipSelf, ViewEncapsulation } from '@angular/core';
import { NgControl } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';

import { AbstractFormField, AppFormValidationDirective } from '../../base';

@Component({
  selector: 'app-icon-select',
  standalone: true,
  imports: [MatIconModule, MatSelectModule],
  templateUrl: './app-icon-select.html',
  styleUrl: './app-icon-select.scss',
  encapsulation: ViewEncapsulation.None,
})
export class AppIconSelect extends AbstractFormField<string> {
  protected readonly icons = [
    'work',
    'school',
    'auto_awesome',
    'wb_sunny',
    'check_circle',
    'favorite',
    'home',
    'fitness_center',
  ];

  constructor(
    @Optional() @Self() ngControl: NgControl | null,
    @Optional() @SkipSelf() formValidation: AppFormValidationDirective | null,
  ) {
    super(ngControl, formValidation);
  }

  get selectedIcon(): string {
    return this.value || this.icons[0];
  }

  iconLabel(icon: string): string {
    return icon.replaceAll('_', ' ');
  }

  handleSelection(icon: string): void {
    this.value = icon;
    this.onChange(icon);
  }
}
