import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SearchCrudApiService } from './search-api.service';

export abstract class BlockableSearchCrudApiService<
  TDto,
  TFilter = Record<string, unknown>,
  TView = TDto,
  TId extends number | string = number,
> extends SearchCrudApiService<TDto, TFilter, TView, TId> {
  protected constructor(http: HttpClient, resourcePath: string) {
    super(http, resourcePath);
  }

  bloquear(id: TId): Observable<TDto> {
    return this.http.put<TDto>(this.buildUrl(`${this.resourcePath}/${id}/bloquear`), null);
  }
}
