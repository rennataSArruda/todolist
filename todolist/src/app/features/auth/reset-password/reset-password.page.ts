import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize, map, startWith } from 'rxjs';

import { AuthService } from '../../../core/services/auth/auth.service';
import { AppFormValidationDirective, AppInputPassword } from '../../../shared/components/form-fields';

type PasswordRules = {
  hasMinLength: boolean;
  passwordsMatch: boolean;
};

@Component({
  selector: 'app-reset-password-page',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatIconModule,
    AppFormValidationDirective,
    AppInputPassword,
  ],
  templateUrl: './reset-password.page.html',
  styleUrl: './reset-password.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResetPasswordPage {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  private readonly token = this.route.snapshot.queryParamMap.get('token') ?? '';

  protected readonly form = this.formBuilder.group({
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', [Validators.required]],
  });

  protected readonly loading = signal(false);
  protected readonly successMessage = signal<string | null>(null);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly hasToken = signal(this.token.length > 0);
  protected readonly passwordRules = toSignal(
    this.form.valueChanges.pipe(
      startWith(this.form.getRawValue()),
      map(({ newPassword = '', confirmPassword = '' }) => this.resolvePasswordRules(newPassword, confirmPassword)),
    ),
    { initialValue: this.resolvePasswordRules('', '') },
  );

  protected submit(): void {
    if (!this.hasToken()) {
      this.errorMessage.set('O link de redefinição é inválido ou expirou. Solicite uma nova recuperação de senha.');
      return;
    }

    if (this.form.invalid || !this.passwordRules().passwordsMatch) {
      this.form.markAllAsTouched();
      this.errorMessage.set('Confira a nova senha antes de continuar.');
      return;
    }

    this.successMessage.set(null);
    this.errorMessage.set(null);
    this.loading.set(true);

    this.authService
      .resetPassword({
        token: this.token,
        newPassword: this.form.controls.newPassword.value,
      })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: () => {
          this.successMessage.set('Senha redefinida com sucesso. Você já pode entrar com a nova senha.');
          setTimeout(() => void this.router.navigateByUrl('/login'), 1200);
        },
        error: (error: unknown) => {
          this.errorMessage.set(this.resolveErrorMessage(error));
        },
      });
  }

  private resolvePasswordRules(newPassword: string, confirmPassword: string): PasswordRules {
    return {
      hasMinLength: newPassword.length >= 8,
      passwordsMatch: newPassword.length > 0 && newPassword === confirmPassword,
    };
  }

  private resolveErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse && error.status === 0) {
      return 'Não foi possível conectar ao servidor. Verifique sua conexão e tente novamente.';
    }

    return 'O link de redefinição é inválido ou expirou. Solicite uma nova recuperação de senha.';
  }
}
