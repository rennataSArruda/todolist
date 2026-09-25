import { ChangeDetectorRef, DestroyRef, Directive, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, debounceTime, Observable, of, Subject, switchMap } from 'rxjs';

import { NgControl } from '@angular/forms';

import { AbstractFormField } from './abstract-form-field';
import { AppFormValidationDirective } from './app-form-validation.directive';

/**
 * Base para campos de seleção assistida. Cada especialização informa como
 * pesquisar, identificar e apresentar os itens do seu domínio.
 */
@Directive()
export abstract class AbstractAutocompleteFormField<TItem, TValue> extends AbstractFormField<TValue> {
  private readonly destroyRef = inject(DestroyRef);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);
  private readonly searchTerms = new Subject<string>();

  protected items: readonly TItem[] = [];
  protected loading = false;
  protected searchFailed = false;
  protected inputValue = '';

  protected constructor(
    ngControl: NgControl | null,
    formValidation: AppFormValidationDirective | null,
  ) {
    super(ngControl, formValidation);

    this.searchTerms
      .pipe(
        debounceTime(220),
        switchMap((term) => {
          return this.searchItems(term).pipe(
            catchError(() => {
              this.searchFailed = true;
              return of([] as readonly TItem[]);
            }),
          );
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((items) => {
        this.items = items;
        this.loading = false;
        this.changeDetectorRef.markForCheck();
      });
  }

  override writeValue(value: TValue | null): void {
    super.writeValue(value);
    this.inputValue = value === null ? '' : this.getItemLabel(value);
  }

  protected handleInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const term = input.value;

    this.inputValue = term;
    this.items = [];

    if (this.value !== null) {
      this.value = null;
      this.onChange(null);
    }

    this.requestSearch(term.trim());
  }

  protected loadInitialOptions(): void {
    if (!this.disabled && !this.readonly) {
      this.requestSearch(this.inputValue.trim());
    }
  }

  protected selectItem(item: TItem): void {
    const value = this.getItemValue(item);
    this.value = value;
    this.inputValue = this.getItemLabel(value);
    this.items = [];
    this.onChange(value);
    this.markAsTouched();
  }

  private requestSearch(term: string): void {
    this.loading = true;
    this.searchFailed = false;
    this.changeDetectorRef.markForCheck();
    this.searchTerms.next(term);
  }

  protected abstract searchItems(term: string): Observable<readonly TItem[]>;
  protected abstract getItemValue(item: TItem): TValue;
  protected abstract getItemLabel(value: TValue): string;
}