import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { AbstractApiService } from '../base';

export type FirstAccessAvailabilityType = 'USERNAME' | 'EMAIL';

export interface FirstAccessRequest {
  name: string;
  username: string;
  email: string;
  password: string;
}

export interface FirstAccessAvailabilityRequest {
  type: FirstAccessAvailabilityType;
  value: string;
}

export interface FirstAccessAvailabilityResponse {
  available: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class FirstAccessService extends AbstractApiService {
  private readonly resourcePath = '/public/auth/first-access';

  constructor(http: HttpClient) {
    super(http);
  }

  create(request: FirstAccessRequest): Observable<void> {
    return this.http.post<void>(this.buildUrl(this.resourcePath), request);
  }

  checkAvailability(request: FirstAccessAvailabilityRequest): Observable<FirstAccessAvailabilityResponse> {
    return this.http.post<FirstAccessAvailabilityResponse>(
      this.buildUrl(`${this.resourcePath}/availability`),
      request,
    );
  }
}
