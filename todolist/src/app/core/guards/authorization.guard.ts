import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID, inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';

import { AuthStateService } from '../auth/auth-state.service';
import { CurrentUser } from '../models/auth.model';
import { Role } from '../fixeds/roles';

type AuthorizationRule = (user: CurrentUser) => boolean;

export const permissionGuard = (permission: Role): CanActivateFn =>
  authorizationGuard((user) => user.root || user.authorities.includes(permission));

export const rootGuard: CanActivateFn = authorizationGuard((user) => user.root);

function authorizationGuard(rule: AuthorizationRule): CanActivateFn {
  return async () => {
    const platformId = inject(PLATFORM_ID);
    const authState = inject(AuthStateService);
    const router = inject(Router);

    if (!isPlatformBrowser(platformId)) {
      return true;
    }

    if (!(await authState.hasValidSession())) {
      return router.createUrlTree(['/login']);
    }

    try {
      const user = await firstValueFrom(authState.loadCurrentUser());
      return rule(user) ? true : router.createUrlTree(['/dashboard']);
    } catch {
      authState.clearSession();
      return router.createUrlTree(['/login']);
    }
  };
}
