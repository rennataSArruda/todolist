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
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { RouterLink } from '@angular/router';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';

import { ICONS } from '../../../core/fixeds/icons';
import { Usuario } from '../../../core/models/usuario.model';
import { UsuarioService } from '../../../core/services/usuarios/usuario.service';
import { SelectOption, SelectValue } from '../../../shared/components/form-fields/base';
import { AppInput } from '../../../shared/components/form-fields/input/app-input';
import { AppSelect } from '../../../shared/components/form-fields/select/app-select';
import { AppTableColumn, DataTableComponent } from '../../../shared/components/data-table/data-table';

@Component({
  selector: 'app-user-list-page',
  imports: [FormsModule, RouterLink, MatButtonModule, MatIconModule, MatMenuModule, DataTableComponent, AppInput, AppSelect],
  templateUrl: './user-list.page.html',
  styleUrl: './user-list.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserListPage {
  private readonly userService = inject(UsuarioService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly search$ = new Subject<string>();
  private readonly userCell = viewChild.required<TemplateRef<{ $implicit: Usuario }>>('userCell');
  private readonly usernameCell = viewChild.required<TemplateRef<{ $implicit: Usuario }>>('usernameCell');
  private readonly statusCell = viewChild.required<TemplateRef<{ $implicit: Usuario }>>('statusCell');
  private readonly actionsCell = viewChild.required<TemplateRef<{ $implicit: Usuario }>>('actionsCell');

  protected readonly users = signal<Usuario[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly totalElements = signal(0);
  protected readonly page = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly pageSize = 10;
  protected readonly searchTerm = signal('');
  protected readonly searchInputValue = signal('');
  protected readonly status = signal<'all' | 'active' | 'inactive'>('all');
  protected readonly ICONS = ICONS;
  protected readonly statusOptions: readonly SelectOption<string>[] = [
    { value: 'all', label: 'Todos os status' },
    { value: 'active', label: 'Ativos' },
    { value: 'inactive', label: 'Inativos' },
  ];
  protected readonly columns = computed<readonly AppTableColumn<Usuario>[]>(() => [
    { key: 'user', label: 'Usuário', template: this.userCell() },
    { key: 'username', label: 'Nome de usuário', template: this.usernameCell() },
    { key: 'status', label: 'Status', template: this.statusCell() },
    { key: 'actions', label: 'Ações', template: this.actionsCell(), align: 'end' },
  ]);

  constructor() {
    this.search$
      .pipe(debounceTime(220), distinctUntilChanged(), takeUntilDestroyed(this.destroyRef))
      .subscribe((term) => {
        this.searchTerm.set(term);
        this.loadUsers(0);
      });
    this.loadUsers();
  }

  protected updateSearch(value: string): void {
    this.searchInputValue.set(value);
    this.search$.next(value);
  }

  protected updateStatus(value: SelectValue | null): void {
    this.status.set(value as 'all' | 'active' | 'inactive');
    this.loadUsers(0);
  }

  protected changePage(nextPage: number): void {
    if (nextPage >= 0 && nextPage < this.totalPages() && nextPage !== this.page()) {
      this.loadUsers(nextPage);
    }
  }

  protected toggleStatus(user: Usuario): void {
    if (!user.id) return;

    this.userService.bloquear(user.id).subscribe({
      next: () => this.loadUsers(this.page()),
      error: () => this.errorMessage.set('Não foi possível alterar a situação do usuário.'),
    });
  }
  private loadUsers(page = 0): void {
    this.loading.set(true);
    this.errorMessage.set(null);
    const status = this.status();

    this.userService
      .searchPagination({
        filter: {
          quickSearch: this.searchTerm().trim() || undefined,
          ativo: status === 'all' ? undefined : status === 'active',
        },
        page,
        size: this.pageSize,
      })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (result) => {
          this.users.set(result.content);
          this.page.set(result.number);
          this.totalPages.set(result.totalPages);
          this.totalElements.set(result.totalElements);
          this.loading.set(false);
        },
        error: () => {
          this.errorMessage.set('Não foi possível carregar os usuários.');
          this.loading.set(false);
        },
      });
  }
}
