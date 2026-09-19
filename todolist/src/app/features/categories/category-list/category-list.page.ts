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
import { AppSelect } from '../../../shared/components/form-fields/select/app-select';
import { AppInput } from '../../../shared/components/form-fields/input/app-input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { RouterLink } from '@angular/router';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';
import { ICONS } from '../../../core/fixeds/icons';
import { TarefaCategoria } from '../../../core/models/tarefa-categoria.model';
import { TarefaCategoriaService } from '../../../core/services/tarefa-categorias/tarefa-categoria.service';
import {
  AppTableColumn,
  DataTableComponent,
} from '../../../shared/components/data-table/data-table';

@Component({
  selector: 'app-category-list-page',
  imports: [
    FormsModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    RouterLink,
    DataTableComponent,
    AppSelect,
    AppInput,
  ],
  templateUrl: './category-list.page.html',
  styleUrl: './category-list.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CategoryListPage {
  private readonly categoryService = inject(TarefaCategoriaService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly search$ = new Subject<string>();
  private readonly categoryCell =
    viewChild.required<TemplateRef<{ $implicit: TarefaCategoria }>>('categoryCell');
  private readonly descriptionCell =
    viewChild.required<TemplateRef<{ $implicit: TarefaCategoria }>>('descriptionCell');
  private readonly statusCell =
    viewChild.required<TemplateRef<{ $implicit: TarefaCategoria }>>('statusCell');
  private readonly actionsCell =
    viewChild.required<TemplateRef<{ $implicit: TarefaCategoria }>>('actionsCell');

  protected readonly categories = signal<TarefaCategoria[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly totalElements = signal(0);
  protected readonly page = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly pageSize = 5;
  protected readonly searchTerm = signal('');
  protected readonly searchInputValue = signal('');
  protected readonly status = signal<'all' | 'active' | 'inactive'>('all');
  protected readonly statusOptions: readonly SelectOption<string>[] = [
    { value: 'all', label: 'Todas' },
    { value: 'active', label: 'Ativas' },
    { value: 'inactive', label: 'Inativas' },
  ];
  protected readonly ICONS = ICONS;
  protected readonly columns = computed<readonly AppTableColumn<TarefaCategoria>[]>(() => [
    { key: 'category', label: 'Categoria', template: this.categoryCell() },
    { key: 'description', label: 'Descrição', template: this.descriptionCell() },
    { key: 'status', label: 'Status', template: this.statusCell() },
    { key: 'actions', label: 'Ações', template: this.actionsCell(), align: 'end' },
  ]);

  constructor() {
    this.search$
      .pipe(debounceTime(220), distinctUntilChanged(), takeUntilDestroyed(this.destroyRef))
      .subscribe((term) => {
        this.searchTerm.set(term);
        this.loadCategories(0);
      });
    this.loadCategories();
  }

  protected updateSearch(value: string): void {
    this.searchInputValue.set(value);
    this.search$.next(value);
  }
  protected updateStatus(value: SelectValue | null): void {
    this.status.set(value as 'all' | 'active' | 'inactive');
    this.loadCategories(0);
  }
  protected changePage(nextPage: number): void {
    if (nextPage >= 0 && nextPage < this.totalPages() && nextPage !== this.page())
      this.loadCategories(nextPage);
  }
  protected toggleStatus(category: TarefaCategoria): void {
    if (!category.id) return;
    this.categoryService.bloquear(category.id).subscribe({
      next: () => this.loadCategories(this.page()),
      error: () =>
        this.errorMessage.set('Não foi possível alterar a situação da categoria. Tente novamente.'),
    });
  }

  private loadCategories(page = 0): void {
    this.loading.set(true);
    this.errorMessage.set(null);
    const status = this.status();
    this.categoryService
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
          this.categories.set(result.content);
          this.page.set(result.number);
          this.totalPages.set(result.totalPages);
          this.totalElements.set(result.totalElements);
          this.loading.set(false);
        },
        error: () => {
          this.errorMessage.set('Não foi possível carregar as categorias.');
          this.loading.set(false);
        },
      });
  }
}
