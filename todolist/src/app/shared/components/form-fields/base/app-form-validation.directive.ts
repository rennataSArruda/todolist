import { Directive, HostListener, OnDestroy, signal } from '@angular/core';
import { FormGroupDirective } from '@angular/forms';

const ERROR_VISIBILITY_DURATION_MS = 4_000;

@Directive({
  selector: 'form[formGroup][appFormValidation]',
  standalone: true,
  exportAs: 'appFormValidation',
})
export class AppFormValidationDirective implements OnDestroy {
  readonly errorsVisible = signal(false);

  private hideErrorsTimeout: ReturnType<typeof setTimeout> | undefined;

  constructor(private readonly formGroupDirective: FormGroupDirective) {}

  @HostListener('submit')
  handleSubmit(): void {
    if (this.formGroupDirective.invalid) {
      this.formGroupDirective.control.markAllAsTouched();
      this.showErrorsTemporarily();
      return;
    }

    this.hideErrors();
  }

  ngOnDestroy(): void {
    this.clearHideErrorsTimeout();
  }

  private showErrorsTemporarily(): void {
    this.clearHideErrorsTimeout();
    this.errorsVisible.set(true);
    this.hideErrorsTimeout = setTimeout(() => this.hideErrors(), ERROR_VISIBILITY_DURATION_MS);
  }

  private hideErrors(): void {
    this.clearHideErrorsTimeout();
    this.errorsVisible.set(false);
  }

  private clearHideErrorsTimeout(): void {
    if (this.hideErrorsTimeout) {
      clearTimeout(this.hideErrorsTimeout);
      this.hideErrorsTimeout = undefined;
    }
  }
}