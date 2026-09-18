import { AbstractControl, AsyncValidatorFn, ValidationErrors } from '@angular/forms';
import { catchError, map, Observable, of, switchMap, timer } from 'rxjs';

import { FirstAccessAvailabilityType, FirstAccessService } from '../services/auth/first-access.service';

const AVAILABILITY_DEBOUNCE_MS = 400;

export function firstAccessAvailabilityValidator(
  firstAccessService: FirstAccessService,
  type: FirstAccessAvailabilityType,
): AsyncValidatorFn {
  return (control: AbstractControl<string>): Observable<ValidationErrors | null> => {
    const value = control.value.trim();

    if (!value) {
      return of(null);
    }

    return timer(AVAILABILITY_DEBOUNCE_MS).pipe(
      switchMap(() => firstAccessService.checkAvailability({ type, value })),
      map((response) => (response.available ? null : { unavailable: true })),
      catchError(() => of(null)),
    );
  };
}
