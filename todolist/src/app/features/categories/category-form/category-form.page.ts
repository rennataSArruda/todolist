import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { ICONS } from '../../../core/fixeds/icons';
import { TarefaCategoria } from '../../../core/models/tarefa-categoria.model';
import { TarefaCategoriaService } from '../../../core/services/tarefa-categorias/tarefa-categoria.service';
import { AppFormValidationDirective } from '../../../shared/components/form-fields';
import { AppColorPicker } from '../../../shared/components/form-fields/color-picker/app-color-picker';
import { AppInput } from '../../../shared/components/form-fields/input/app-input';
import { AppTextarea } from '../../../shared/components/form-fields/textarea/app-textarea';
import { AppIconSelect } from '../../../shared/components/form-fields/selects/icone-select/app-icon-select';

@Component({
  selector: 'app-category-form-page',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    RouterLink,
    AppFormValidationDirective,
    AppColorPicker,
    AppIconSelect,
    AppInput,
    AppTextarea,
  ],
  templateUrl: './category-form.page.html',
  styleUrl: './category-form.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CategoryFormPage {
  private readonly builder = inject(NonNullableFormBuilder);
  private readonly service = inject(TarefaCategoriaService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly categoryId = Number(this.route.snapshot.paramMap.get('id')) || null;
  protected readonly saving = signal(false);
  protected readonly loading = signal(!!this.categoryId);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly ICONS = ICONS;
  protected readonly form = this.builder.group({
    nome: ['', Validators.required],
    descricao: [''],
    icone: ['work', Validators.required],
    corHex: ['#695be8', [Validators.required, Validators.pattern(/^#[0-9a-fA-F]{6}$/)]],
    ativo: [true],
  });

  constructor() {
    if (this.categoryId) this.loadCategory();
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.errorMessage.set(null);
    const category: TarefaCategoria = this.form.getRawValue();
    const request = this.categoryId
      ? this.service.update(this.categoryId, category)
      : this.service.create(category);

    request.pipe(finalize(() => this.saving.set(false))).subscribe({
      next: () => void this.router.navigate(['/categorias']),
      error: () => this.errorMessage.set('Não foi possível salvar a categoria. Tente novamente.'),
    });
  }

  private loadCategory(): void {
    this.service
      .getById(this.categoryId!)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (category) => {
          this.form.patchValue(category);
          this.loading.set(false);
        },
        error: () => {
          this.errorMessage.set('Não foi possível carregar a categoria.');
          this.loading.set(false);
        },
      });
  }
}
