import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Page, SearchRequest } from '../../models/page.model';
import { CrudApiService } from './crud-api.service';

export abstract class SearchCrudApiService<
  TDto,
  TFilter = Record<string, unknown>,
  TView = TDto,
  TId extends number | string = number,
> extends CrudApiService<TDto, TId> {
  protected constructor(http: HttpClient, resourcePath: string) {
    super(http, resourcePath);
  }

  search(filter: TFilter): Observable<TView[]> {
    return this.http.post<TView[]>(this.buildUrl(`${this.resourcePath}/search`), filter);
  }

  searchPagination(request: SearchRequest<TFilter>): Observable<Page<TView>> {
    return this.http.post<Page<TView>>(this.buildUrl(`${this.resourcePath}/search-pagination`), {
      filter: request.filter,
      page: request.page,
      size: request.size,
    });
  }
}
