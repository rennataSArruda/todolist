import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  TemplateRef,
  computed,
  inject,
  signal,
  viewChild,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { SelectOption, SelectValue } from '../../../shared/components/form-fields/base';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { RouterLink } from '@angular/router';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';

import { ICONS } from '../../../core/fixeds/icons';
import { Perfil } from '../../../core/models/perfil.model';
import { PerfilService } from '../../../core/services/perfis/perfil.service';
import { AppInput } from '../../../shared/components/form-fields/input/app-input';
import { AppSelect } from '../../../shared/components/form-fields/select/app-select';
import { AppTableColumn, DataTableComponent } from '../../../shared/components/data-table/data-table';

@Component({
  selector: 'app-profile-list-page',
  imports: [FormsModule, MatButtonModule, MatIconModule, MatMenuModule, RouterLink, DataTableComponent, AppInput, AppSelect],
  templateUrl: './profile-list.page.html',
  styleUrl: './profile-list.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProfileListPage {
  private readonly profileService = inject(PerfilService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly search$ = new Subject<string>();
  private readonly profileCell = viewChild.required<TemplateRef<{ $implicit: Perfil }>>('profileCell');
  private readonly descriptionCell = viewChild.required<TemplateRef<{ $implicit: Perfil }>>('descriptionCell');
  private readonly permissionsCell = viewChild.required<TemplateRef<{ $implicit: Perfil }>>('permissionsCell');
  private readonly statusCell = viewChild.required<TemplateRef<{ $implicit: Perfil }>>('statusCell');
  private readonly actionsCell = viewChild.required<TemplateRef<{ $implicit: Perfil }>>('actionsCell');

  protected readonly profiles = signal<Perfil[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly totalElements = signal(0);
  protected readonly page = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly pageSize = 10;
  protected readonly searchTerm = signal('');
  protected readonly searchInputValue = signal('');
  protected readonly status = signal<'all' | 'active' | 'inactive'>('all');
  protected readonly statusOptions: readonly SelectOption<string>[] = [
    { value: 'all', label: 'Todos' },
    { value: 'active', label: 'Ativos' },
    { value: 'inactive', label: 'Inativos' },
  ];
  protected readonly ICONS = ICONS;
  protected readonly columns = computed<readonly AppTableColumn<Perfil>[]>(() => [
    { key: 'profile', label: 'Perfil', template: this.profileCell() },
    { key: 'description', label: 'Descrição', template: this.descriptionCell() },
    { key: 'permissions', label: 'Permissões', template: this.permissionsCell(), align: 'end' },
    { key: 'status', label: 'Status', template: this.statusCell() },
    { key: 'actions', label: 'Ações', template: this.actionsCell(), align: 'end' },
  ]);

  constructor() {
    this.search$.pipe(debounceTime(220), distinctUntilChanged(), takeUntilDestroyed(this.destroyRef)).subscribe((term) => {
      this.searchTerm.set(term);
      this.loadProfiles(0);
    });
    this.loadProfiles();
  }

  protected updateSearch(value: string): void { this.searchInputValue.set(value); this.search$.next(value); }
  protected updateStatus(value: SelectValue | null): void {
    this.status.set(value as 'all' | 'active' | 'inactive');
    this.loadProfiles(0);
  }
  protected changePage(nextPage: number): void {
    if (nextPage >= 0 && nextPage < this.totalPages() && nextPage !== this.page()) this.loadProfiles(nextPage);
  }

  protected toggleStatus(profile: Perfil): void {
    if (!profile.id) return;
    this.profileService.bloquear(profile.id).subscribe({
      next: () => this.loadProfiles(this.page()),
      error: () => this.errorMessage.set('Não foi possível alterar a situação do perfil. Tente novamente.'),
    });
  }

  private loadProfiles(page = 0): void {
    this.loading.set(true);
    this.errorMessage.set(null);
    const searchTerm = this.searchTerm().trim();
    const status = this.status();
    this.profileService.searchPagination({ filter: {
      codigo: searchTerm || undefined,
      descricao: searchTerm || undefined,
      ativo: status === 'all' ? undefined : status === 'active',
    }, page, size: this.pageSize })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (result) => { this.profiles.set(result.content); this.page.set(result.number); this.totalPages.set(result.totalPages); this.totalElements.set(result.totalElements); this.loading.set(false); },
        error: () => { this.errorMessage.set('Não foi possível carregar os perfis.'); this.loading.set(false); },
      });
  }
}

