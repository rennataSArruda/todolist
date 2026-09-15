import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { AbstractApiService, QueryParams } from './abstract-api.service';

export abstract class CrudApiService<TDto, TId extends number | string = number> extends AbstractApiService {
  protected constructor(
    http: HttpClient,
    protected readonly resourcePath: string,
  ) {
    super(http);
  }

  getAll(): Observable<TDto[]> {
    return this.http.get<TDto[]>(this.buildUrl(this.resourcePath));
  }

  getById(id: TId): Observable<TDto> {
    return this.http.get<TDto>(this.buildUrl(`${this.resourcePath}/${id}`));
  }

  create(dto: TDto): Observable<TDto> {
    return this.http.post<TDto>(this.buildUrl(this.resourcePath), dto);
  }

  update(id: TId, dto: TDto): Observable<TDto> {
    return this.http.put<TDto>(this.buildUrl(`${this.resourcePath}/${id}`), dto);
  }

  exists(params?: QueryParams): Observable<boolean> {
    return this.http.get<boolean>(this.buildUrl(`${this.resourcePath}/exists`), {
      params: this.buildParams(params),
    });
  }
}
