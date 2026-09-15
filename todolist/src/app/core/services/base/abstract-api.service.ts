import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

export type QueryParams = Record<string, boolean | number | string | null | undefined>;

export abstract class AbstractApiService {
  protected readonly apiUrl = environment.apiUrl;

  protected constructor(protected readonly http: HttpClient) {}

  protected buildUrl(path: string): string {
    return `${this.apiUrl}${path}`;
  }

  protected buildParams(params?: QueryParams): HttpParams {
    let httpParams = new HttpParams();

    if (!params) {
      return httpParams;
    }

    Object.entries(params).forEach(([key, value]) => {
      if (value !== null && value !== undefined) {
        httpParams = httpParams.set(key, String(value));
      }
    });

    return httpParams;
  }
}
