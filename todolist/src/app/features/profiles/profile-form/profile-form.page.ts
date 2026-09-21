import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize, forkJoin } from 'rxjs';

import { ICONS } from '../../../core/fixeds/icons';
import { PERMISSION_COLUMNS } from '../../../core/fixeds/roles';
import { Papel } from '../../../core/models/fixed/papel.model';
import { Perfil, PerfilPermissao } from '../../../core/models/perfil.model';
import { PapelService } from '../../../core/services/papeis/papel.service';
import { PerfilService } from '../../../core/services/perfis/perfil.service';
import { AppFormValidationDirective } from '../../../shared/components/form-fields';
import { AppInput } from '../../../shared/components/form-fields/input/app-input';
import { AppTextarea } from '../../../shared/components/form-fields/textarea/app-textarea';

@Component({
  selector: 'app-profile-form-page',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCheckboxModule,
    MatIconModule,
    RouterLink,
    AppFormValidationDirective,
    AppInput,
    AppTextarea,
  ],
  templateUrl: './profile-form.page.html',
  styleUrl: './profile-form.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProfileFormPage {
  private readonly builder = inject(NonNullableFormBuilder);
  private readonly profileService = inject(PerfilService);
  private readonly papelService = inject(PapelService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly profileId = Number(this.route.snapshot.paramMap.get('id')) || null;
  protected readonly saving = signal(false);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly roles = signal<Papel[]>([]);
  protected readonly ICONS = ICONS;
  protected readonly permissionColumns = PERMISSION_COLUMNS;
  protected readonly form = this.builder.group({
    codigo: ['', [Validators.required, Validators.maxLength(50)]],
    descricao: ['', [Validators.required, Validators.maxLength(150)]],
    permissoes: this.builder.control<PerfilPermissao[]>([], Validators.minLength(1)),
  });

  constructor() {
    const requests = this.profileId
      ? forkJoin({
          roles: this.papelService.loadAll(),
          profile: this.profileService.getById(this.profileId),
        })
      : forkJoin({ roles: this.papelService.loadAll() });

    requests.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (result) => {
        this.roles.set(result.roles);
        if ('profile' in result) this.form.patchValue(result.profile as Perfil);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Não foi possível carregar os dados do perfil.');
        this.loading.set(false);
      },
    });
  }

  protected isPermissionAvailable(role: Papel, permissionCode: string): boolean {
    return role.permissoes.some((permission) => permission.codigo === permissionCode);
  }

  protected isPermissionSelected(roleCode: string, permissionCode: string): boolean {
    return this.form.controls.permissoes.value.some(
      (permission) =>
        permission.papelCodigo === roleCode && permission.permissaoCodigo === permissionCode,
    );
  }

  protected togglePermission(roleCode: string, permissionCode: string, selected: boolean): void {
    const current = this.form.controls.permissoes.value;
    const next = selected
      ? [...current, { papelCodigo: roleCode, permissaoCodigo: permissionCode }]
      : current.filter(
          (permission) =>
            permission.papelCodigo !== roleCode || permission.permissaoCodigo !== permissionCode,
        );

    this.form.controls.permissoes.setValue(next);
    this.form.controls.permissoes.markAsTouched();
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.errorMessage.set(null);
    const profile: Perfil = this.form.getRawValue();
    const request = this.profileId
      ? this.profileService.update(this.profileId, profile)
      : this.profileService.create(profile);

    request.pipe(finalize(() => this.saving.set(false))).subscribe({
      next: () => void this.router.navigate(['/perfis']),
      error: () => this.errorMessage.set('Não foi possível salvar o perfil. Tente novamente.'),
    });
  }
}
