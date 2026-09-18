import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, NonNullableFormBuilder, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { AuthStateService } from '../../../core/auth/auth-state.service';
import { ApiError } from '../../../core/models/api-error.model';
import { AppFormValidationDirective, AppInput, AppInputPassword } from '../../../shared/components/form-fields';
import {MatIcon} from '@angular/material/icon';
import {ICONS} from '../../../core/fixeds/icons';

@Component({
  selector: 'app-login-page',
  imports: [ReactiveFormsModule, RouterLink, MatButtonModule, AppFormValidationDirective, AppInput, AppInputPassword, MatIcon],
  templateUrl: './login.page.html',
  styleUrl: './login.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginPage {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly router = inject(Router);
  private readonly authState = inject(AuthStateService);
  private readonly afterLoginRoute = '/dashboard';

  protected readonly form = this.formBuilder.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required]],
  });

  protected readonly loading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  protected submit(): void {
    if (this.form.invalid) {
      return;
    }

    this.errorMessage.set(null);
    this.loading.set(true);

    this.authState
      .login(this.form.getRawValue())
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: () => {
          void this.router.navigateByUrl(this.afterLoginRoute);
        },
        error: (error: unknown) => {
          this.errorMessage.set(this.resolveLoginErrorMessage(error));
        },
      });
  }

  private resolveLoginErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      const apiError = error.error as ApiError | null;

      if (error.status === 401 || apiError?.action === 'LOGIN' || apiError?.code === 'AUTH_UNAUTHORIZED') {
        return 'Usuário ou senha inválidos.';
      }
    }

    return 'Não foi possível entrar agora. Tente novamente.';
  }

  protected readonly icons = ICONS;
}

