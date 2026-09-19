import { ChangeDetectionStrategy, Component, TemplateRef, input, output } from '@angular/core';
import { NgTemplateOutlet } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { ICONS } from '../../../core/fixeds/icons';

export interface AppTableColumn<T> {
  readonly key: string;
  readonly label: string;
  readonly template?: TemplateRef<{ $implicit: T }>;
  readonly align?: 'start' | 'end';
}
@Component({
  selector: 'app-data-table',
  imports: [NgTemplateOutlet, MatIconModule, MatPaginatorModule, MatTableModule],
  templateUrl: './data-table.html',
  styleUrl: './data-table.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DataTableComponent<T> {
  readonly columns = input.required<readonly AppTableColumn<T>[]>();
  readonly data = input.required<readonly T[]>();
  readonly loading = input(false);
  readonly emptyMessage = input('Nenhum registro encontrado.');
  readonly pageIndex = input(0);
  readonly pageSize = input(10);
  readonly totalElements = input(0);
  readonly totalPages = input(0);
  readonly pageChange = output<number>();
  protected readonly ICONS = ICONS;
  protected readonly displayedColumns = () => this.columns().map((column) => column.key);
  protected changePage(event: PageEvent): void {
    if (event.pageIndex !== this.pageIndex()) this.pageChange.emit(event.pageIndex);
  }
}
