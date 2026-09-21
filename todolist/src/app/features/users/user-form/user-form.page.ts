import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { ICONS } from '../../../core/fixeds/icons';
import { Perfil } from '../../../core/models/perfil.model';
import { Usuario } from '../../../core/models/usuario.model';
import { UsuarioService } from '../../../core/services/usuarios/usuario.service';
import { PerfilService } from '../../../core/services/perfis/perfil.service';
import { FirstAccessService } from '../../../core/services/auth/first-access.service';
import { firstAccessAvailabilityValidator } from '../../../core/validators/first-access-availability.validator';
import { AppFormValidationDirective, AppInput, AppInputPassword, AppProfileAutocomplete } from '../../../shared/components/form-fields';

const USERNAME_PATTERN = /^(?!.*[._-]{2})[a-zA-Z0-9](?:[a-zA-Z0-9._-]*[a-zA-Z0-9])?$/;

@Component({
  selector: 'app-user-form-page',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatIconModule,
    AppFormValidationDirective,
    AppInput,
    AppInputPassword,
    AppProfileAutocomplete,
  ],
  templateUrl: './user-form.page.html',
  styleUrl: './user-form.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserFormPage {
  private readonly builder = inject(NonNullableFormBuilder);
  private readonly userService = inject(UsuarioService);
  private readonly firstAccessService = inject(FirstAccessService);
  private readonly profileService = inject(PerfilService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly userId = Number(this.route.snapshot.paramMap.get('id')) || null;
  protected readonly loading = signal(!!this.userId);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly ICONS = ICONS;
  protected readonly form = this.builder.group({
    name: ['', Validators.required],
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
    perfil: this.builder.control<Perfil | null>(null, Validators.required),
    password: ['', this.userId ? [] : [Validators.required, Validators.minLength(8)]],
  });

  constructor() {
    if (this.userId) {
      this.form.controls.username.clearAsyncValidators();
      this.form.controls.email.clearAsyncValidators();
      this.loadUser();
    }
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { name, username, email, perfil, password } = this.form.getRawValue();
    const user: Usuario = {
      name,
      username,
      email,
      perfilId: perfil!.id,
      ...(!this.userId && password ? { password } : {}),
    };

    this.saving.set(true);
    this.errorMessage.set(null);
    const request = this.userId ? this.userService.update(this.userId, user) : this.userService.create(user);

    request.pipe(finalize(() => this.saving.set(false))).subscribe({
      next: () => void this.router.navigate(['/usuarios']),
      error: () => this.errorMessage.set('Não foi possível salvar o usuário. Verifique os dados e tente novamente.'),
    });
  }

  private loadUser(): void {
    this.userService
      .getById(this.userId!)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (user) => {
          this.form.patchValue({ name: user.name, username: user.username, email: user.email });
          if (!user.perfilId) {
            this.loading.set(false);
            return;
          }

          this.profileService
            .getById(user.perfilId)
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe({
              next: (perfil) => this.form.controls.perfil.setValue(perfil),
              error: () => this.errorMessage.set('Não foi possível carregar o perfil do usuário.'),
              complete: () => this.loading.set(false),
            });
        },
        error: () => {
          this.errorMessage.set('Não foi possível carregar o usuário.');
          this.loading.set(false);
        },
      });
  }
}