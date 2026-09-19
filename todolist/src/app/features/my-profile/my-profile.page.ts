import { ChangeDetectionStrategy, Component, computed, effect, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { finalize, map, startWith } from 'rxjs';

import { AuthStateService } from '../../core/auth';
import { CurrentUserService } from '../../core/services/auth/current-user.service';
import { ChangePasswordConfirmationDialog } from '../../shared/components/change-password-confirmation-dialog/change-password-confirmation-dialog';

type PasswordRules = {
  hasMinLength: boolean;
  passwordsMatch: boolean;
};

@Component({
  selector: 'app-my-profile-page',
  imports: [ReactiveFormsModule, MatButtonModule, MatIconModule],
  templateUrl: './my-profile.page.html',
  styleUrl: './my-profile.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MyProfilePage {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly authState = inject(AuthStateService);
  private readonly currentUserService = inject(CurrentUserService);
  private readonly dialog = inject(MatDialog);
  private readonly router = inject(Router);

  protected readonly user = this.authState.currentUser;
  protected readonly initials = computed(() => this.getInitials(this.user()?.nome));
  protected readonly role = computed(() => (this.user()?.root ? 'Administrador' : this.user()?.perfil?.codigo ?? 'Usuário'));
  protected readonly profileLoading = signal(false);
  protected readonly passwordLoading = signal(false);
  protected readonly profileErrorMessage = signal<string | null>(null);
  protected readonly profileSuccessMessage = signal<string | null>(null);
  protected readonly passwordErrorMessage = signal<string | null>(null);
  protected readonly passwordSuccessMessage = signal<string | null>(null);

  protected readonly profileForm = this.formBuilder.group({ name: ['', Validators.required] });
  protected readonly passwordForm = this.formBuilder.group({
    currentPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', Validators.required],
  });
  protected readonly passwordRules = toSignal(
    this.passwordForm.valueChanges.pipe(
      startWith(this.passwordForm.getRawValue()),
      map(({ newPassword = '', confirmPassword = '' }) => this.resolvePasswordRules(newPassword, confirmPassword)),
    ),
    { initialValue: this.resolvePasswordRules('', '') },
  );

  constructor() {
    effect(() => {
      const user = this.user();
      if (user) this.profileForm.controls.name.setValue(user.nome, { emitEvent: false });
    });
  }

  protected submitProfileUpdate(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.profileLoading.set(true);
    this.profileErrorMessage.set(null);
    this.profileSuccessMessage.set(null);
    this.currentUserService
      .updateProfile(this.profileForm.getRawValue())
      .pipe(finalize(() => this.profileLoading.set(false)))
      .subscribe({
        next: (user) => {
          this.authState.setCurrentUser(user);
          this.profileSuccessMessage.set('Nome atualizado com sucesso.');
        },
        error: () => this.profileErrorMessage.set('Não foi possível atualizar o nome. Tente novamente.'),
      });
  }

  protected submitPasswordChange(): void {
    if (this.passwordForm.invalid || !this.passwordRules().passwordsMatch) {
      this.passwordForm.markAllAsTouched();
      return;
    }

    this.dialog
      .open(ChangePasswordConfirmationDialog, {
        ariaLabel: 'Confirmar atualização de senha',
        autoFocus: false,
        width: 'min(100% - 32px, 560px)',
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (confirmed) this.changePassword();
      });
  }

  private changePassword(): void {
    this.passwordLoading.set(true);
    this.passwordErrorMessage.set(null);
    this.passwordSuccessMessage.set(null);
    const { currentPassword, newPassword } = this.passwordForm.getRawValue();
    this.currentUserService.changePassword({ currentPassword, newPassword }).pipe(finalize(() => this.passwordLoading.set(false))).subscribe({
      next: () => {
        this.authState.clearSession();
        void this.router.navigate(['/login'], { queryParams: { passwordUpdated: 'true' } });
      },
      error: () => this.passwordErrorMessage.set('Não foi possível atualizar a senha. Verifique a senha atual e tente novamente.'),
    });
  }

  private getInitials(name?: string): string {
    return name ? name.trim().split(/\s+/).slice(0, 2).map((part) => part.charAt(0)).join('').toUpperCase() : 'U';
  }

  private resolvePasswordRules(newPassword: string, confirmPassword: string): PasswordRules {
    return {
      hasMinLength: newPassword.length >= 8,
      passwordsMatch: newPassword.length > 0 && newPassword === confirmPassword,
    };
  }
}
