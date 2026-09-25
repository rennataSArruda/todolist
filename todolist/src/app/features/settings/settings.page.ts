import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { finalize, forkJoin } from 'rxjs';

import { ICONS } from '../../core/fixeds/icons';
import { EmailConfig } from '../../core/models/email-config.model';
import { Perfil } from '../../core/models/perfil.model';
import { ConfiguracoesGeraisService } from '../../core/services/configuracoes/configuracoes-gerais.service';
import { EmailConfigService } from '../../core/services/configuracoes/email-config.service';
import { PerfilService } from '../../core/services/perfis/perfil.service';
import { AppFormValidationDirective, AppInput, AppInputPassword, AppProfileAutocomplete } from '../../shared/components/form-fields';

type SettingsSection = 'gerais' | 'email';

@Component({
  selector: 'app-settings-page',
  imports: [ReactiveFormsModule, MatButtonModule, MatIconModule, MatSlideToggleModule, AppFormValidationDirective, AppInput, AppInputPassword, AppProfileAutocomplete],
  templateUrl: './settings.page.html',
  styleUrl: './settings.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SettingsPage {
  private readonly builder = inject(NonNullableFormBuilder);
  private readonly settingsService = inject(ConfiguracoesGeraisService);
  private readonly emailConfigService = inject(EmailConfigService);
  private readonly profileService = inject(PerfilService);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly ICONS = ICONS;
  protected readonly activeSection = signal<SettingsSection>('gerais');
  protected readonly loading = signal(true);
  protected readonly savingGeneral = signal(false);
  protected readonly savingEmail = signal(false);
  protected readonly generalMessage = signal<string | null>(null);
  protected readonly emailMessage = signal<string | null>(null);
  protected readonly selectedEmailId = signal<number | null>(null);
  protected readonly generalForm = this.builder.group({ perfil: this.builder.control<Perfil | null>(null, Validators.required) });
  protected readonly emailForm = this.builder.group({
    host: ['', [Validators.required, Validators.maxLength(150)]],
    port: [587, [Validators.required, Validators.min(1), Validators.max(65535)]],
    username: ['', Validators.maxLength(150)],
    password: ['', Validators.maxLength(512)],
    fromAddress: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
    fromName: ['', Validators.maxLength(150)],
    auth: [true], startTls: [true], ssl: [false], ativo: [true],
  });

  constructor() {
    this.load();
    this.emailForm.controls.auth.valueChanges.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => this.updatePasswordValidation());
  }

  protected selectSection(section: SettingsSection): void { this.activeSection.set(section); }

  protected saveGeneral(): void {
    if (this.generalForm.invalid) { this.generalForm.markAllAsTouched(); return; }
    this.savingGeneral.set(true);
    this.generalMessage.set(null);
    this.settingsService.update({ perfilPadraoPrimeiroAcessoId: this.generalForm.controls.perfil.value!.id! })
      .pipe(finalize(() => this.savingGeneral.set(false)))
      .subscribe({
        next: () => this.generalMessage.set('Perfil padrão atualizado com sucesso.'),
        error: () => this.generalMessage.set('Não foi possível salvar as configurações gerais.'),
      });
  }

  protected saveEmail(): void {
    if (this.emailForm.invalid) { this.emailForm.markAllAsTouched(); return; }
    this.savingEmail.set(true);
    this.emailMessage.set(null);
    const request: EmailConfig = { ...this.emailForm.getRawValue() };
    const id = this.selectedEmailId();
    const operation = id ? this.emailConfigService.update(id, request) : this.emailConfigService.create(request);
    operation.pipe(finalize(() => this.savingEmail.set(false))).subscribe({
      next: (config) => {
        this.selectedEmailId.set(config.id ?? null);
        this.emailMessage.set('Configuração de e-mail salva com sucesso.');

      },
      error: () => this.emailMessage.set('Não foi possível salvar a configuração de e-mail. Verifique os dados e tente novamente.'),
    });
  }

  private load(): void {
    forkJoin({ general: this.settingsService.get(), emails: this.emailConfigService.getAll() })
      .pipe(takeUntilDestroyed(this.destroyRef), finalize(() => this.loading.set(false)))
      .subscribe({
        next: ({ general, emails }) => {

          if (general.perfilPadraoPrimeiroAcessoId) this.loadDefaultProfile(general.perfilPadraoPrimeiroAcessoId);
          const emailConfig = emails[0];
          if (emailConfig?.id) this.loadEmailConfig(emailConfig.id);
        },
        error: () => this.generalMessage.set('Não foi possível carregar todas as configurações.'),
      });
  }

  private loadEmailConfig(id: number): void {
    this.selectedEmailId.set(id);
    this.emailConfigService.getById(id).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (config) => { this.emailForm.patchValue(config); this.updatePasswordValidation(); },
      error: () => this.emailMessage.set('Não foi possível carregar a configuração de e-mail.'),
    });
  }

  private loadDefaultProfile(profileId: number): void {
    this.profileService.getById(profileId).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (profile) => this.generalForm.controls.perfil.setValue(profile),
      error: () => this.generalMessage.set('Não foi possível carregar o perfil padrão configurado.'),
    });
  }

  private updatePasswordValidation(): void {
    const password = this.emailForm.controls.password;
    const needsPassword = this.emailForm.controls.auth.value && !this.selectedEmailId();
    password.setValidators(needsPassword ? [Validators.required, Validators.maxLength(512)] : [Validators.maxLength(512)]);
    password.updateValueAndValidity({ emitEvent: false });
  }
}
