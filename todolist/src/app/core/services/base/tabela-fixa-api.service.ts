import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { AbstractApiService } from './abstract-api.service';

export abstract class TabelaFixaApiService<TDto, TId extends number | string = number> extends AbstractApiService {
  protected constructor(
    http: HttpClient,
    protected readonly resourcePath: string,
  ) {
    super(http);
  }

  load(id: TId): Observable<TDto> {
    return this.http.get<TDto>(this.buildUrl(`${this.resourcePath}/load/${id}`));
  }

  loadAll(): Observable<TDto[]> {
    return this.http.get<TDto[]>(this.buildUrl(`${this.resourcePath}/load-all`));
  }
}
