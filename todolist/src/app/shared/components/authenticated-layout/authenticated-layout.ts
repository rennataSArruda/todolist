import { isPlatformBrowser } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, PLATFORM_ID, computed, inject, signal } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { catchError, of } from 'rxjs';

import { AuthStateService } from '../../../core/auth';
import { CurrentUser } from '../../../core/models/auth.model';
import { LogoutConfirmationDialog } from '../logout-confirmation-dialog/logout-confirmation-dialog';
import {
  ADMINISTRATION_NAVIGATION_ITEMS,
  NavigationItem,
  PRIMARY_NAVIGATION_ITEMS,
} from './navigation.config';

@Component({
  selector: 'app-authenticated-layout',
  imports: [MatIconModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './authenticated-layout.html',
  styleUrl: './authenticated-layout.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuthenticatedLayout implements OnInit {
  private readonly authState = inject(AuthStateService);
  private readonly dialog = inject(MatDialog);
  private readonly router = inject(Router);
  private readonly platformId = inject(PLATFORM_ID);

  protected readonly user = this.authState.currentUser;
  protected readonly userMenuOpen = signal(false);
  protected readonly initials = computed(() => this.getInitials(this.user()?.nome));
  protected readonly profile = computed(() => this.user()?.perfil?.codigo ?? 'Usuário');
  protected readonly authoritySet = computed(() => new Set(this.user()?.authorities ?? []));
  protected readonly primaryNavigationItems = computed(() =>
    this.visibleItems(PRIMARY_NAVIGATION_ITEMS, this.user(), this.authoritySet()),
  );
  protected readonly administrationNavigationItems = computed(() =>
    this.visibleItems(ADMINISTRATION_NAVIGATION_ITEMS, this.user(), this.authoritySet()),
  );

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId) || this.user()) return;

    this.authState.loadCurrentUser().subscribe();
  }

  protected toggleUserMenu(): void {
    this.userMenuOpen.update((isOpen) => !isOpen);
  }

  protected requestLogout(): void {
    this.userMenuOpen.set(false);
    this.dialog
      .open(LogoutConfirmationDialog, {
        ariaLabel: 'Confirmar saída da conta',
        autoFocus: false,
        width: 'min(100% - 32px, 560px)',
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (confirmed) this.logout();
      });
  }

  private logout(): void {
    this.authState
      .logout()
      .pipe(
        catchError(() => {
          this.authState.clearSession();
          return of(void 0);
        }),
      )
      .subscribe(() => void this.router.navigateByUrl('/login'));
  }

  private getInitials(name?: string): string {
    return name ? name.trim().split(/\s+/).slice(0, 2).map((part) => part.charAt(0)).join('').toUpperCase() : 'U';
  }

  private visibleItems(
    items: readonly NavigationItem[],
    user: CurrentUser | null,
    authorities: ReadonlySet<string>,
  ): readonly NavigationItem[] {
    return items.filter((item) => {
      if (!user) return !item.permission && !item.rootOnly;
      if (user.root) return true;
      if (item.rootOnly) return false;

      return !item.permission || authorities.has(item.permission);
    });
  }
}