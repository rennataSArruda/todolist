import { Component, Optional, Self, SkipSelf } from '@angular/core';
import { NgControl } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatIconModule } from '@angular/material/icon';
import { Observable } from 'rxjs';

import { Perfil, PerfilFilter } from '../../../../core/models/perfil.model';
import { PerfilService } from '../../../../core/services/perfis/perfil.service';
import { AbstractAutocompleteFormField, AppFormValidationDirective } from '../base';

@Component({
  selector: 'app-profile-autocomplete',
  standalone: true,
  imports: [MatAutocompleteModule, MatIconModule],
  templateUrl: './app-profile-autocomplete.html',
  styleUrl: './app-profile-autocomplete.scss',
})
export class AppProfileAutocomplete extends AbstractAutocompleteFormField<Perfil, Perfil> {
  constructor(
    private readonly perfilService: PerfilService,
    @Optional() @Self() ngControl: NgControl | null,
    @Optional() @SkipSelf() formValidation: AppFormValidationDirective | null,
  ) {
    super(ngControl, formValidation);
  }

  protected searchItems(codigo: string): Observable<Perfil[]> {
    const filter: PerfilFilter = { codigo: codigo || undefined, ativo: true };
    return this.perfilService.search(filter);
  }

  protected getItemValue(item: Perfil): Perfil {
    return item;
  }

  protected getItemLabel(value: Perfil): string {
    return value.codigo;
  }
}
