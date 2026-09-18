import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterLink } from '@angular/router';
import { finalize, map, startWith } from 'rxjs';

import { ApiError } from '../../../core/models/api-error.model';
import { FirstAccessService } from '../../../core/services/auth/first-access.service';
import { firstAccessAvailabilityValidator } from '../../../core/validators/first-access-availability.validator';
import { AppFormValidationDirective, AppInput, AppInputPassword } from '../../../shared/components/form-fields';

type PasswordRules = {
  hasMinLength: boolean;
  passwordsMatch: boolean;
};

const USERNAME_PATTERN = /^(?!.*[._-]{2})[a-zA-Z0-9](?:[a-zA-Z0-9._-]*[a-zA-Z0-9])?$/;

@Component({
  selector: 'app-register-page',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatIconModule,
    AppFormValidationDirective,
    AppInput,
    AppInputPassword,
  ],
  templateUrl: './register.page.html',
  styleUrl: './register.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegisterPage {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly firstAccessService = inject(FirstAccessService);
  private readonly router = inject(Router);

  protected readonly form = this.formBuilder.group({
    name: ['', [Validators.required]],
    username: [
      '',
      [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(30),
        Validators.pattern(USERNAME_PATTERN),
      ],
      [firstAccessAvailabilityValidator(this.firstAccessService, 'USERNAME')],
    ],
    email: [
      '',
      [Validators.required, Validators.email],
      [firstAccessAvailabilityValidator(this.firstAccessService, 'EMAIL')],
    ],
    password: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', [Validators.required]],
  });

  protected readonly loading = signal(false);
  protected readonly successMessage = signal<string | null>(null);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly passwordRules = toSignal(
    this.form.valueChanges.pipe(
      startWith(this.form.getRawValue()),
      map(({ password = '', confirmPassword = '' }) =>
        this.resolvePasswordRules(password, confirmPassword),
      ),
    ),
    { initialValue: this.resolvePasswordRules('', '') },
  );

  protected submit(): void {
    if (this.form.invalid || !this.passwordRules().passwordsMatch) {
      this.form.markAllAsTouched();
      this.errorMessage.set('Confira os dados informados antes de criar a conta.');
      return;
    }

    const { name, username, email, password } = this.form.getRawValue();

    this.successMessage.set(null);
    this.errorMessage.set(null);
    this.loading.set(true);

    this.firstAccessService
      .create({ name, username, email, password })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: () => {
          this.successMessage.set('Conta criada com sucesso. Entre com seu usuário e senha para continuar.');
          setTimeout(() => void this.router.navigateByUrl('/login'), 1500);
        },
        error: (error: unknown) => {
          this.errorMessage.set(this.resolveErrorMessage(error));
        },
      });
  }

  private resolvePasswordRules(password: string, confirmPassword: string): PasswordRules {
    return {
      hasMinLength: password.length >= 8,
      passwordsMatch: password.length > 0 && password === confirmPassword,
    };
  }

  private resolveErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 409) {
        return 'O usuário ou e-mail informado já está em uso. Revise os dados e tente novamente.';
      }

      const apiError = error.error as ApiError | null;
      if (apiError?.message) {
        return apiError.message;
      }

      if (error.status === 0) {
        return 'Não foi possível conectar ao servidor. Verifique sua conexão e tente novamente.';
      }
    }

    return 'Não foi possível criar sua conta agora. Tente novamente.';
  }
}
