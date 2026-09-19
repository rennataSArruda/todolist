import { isPlatformBrowser } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, PLATFORM_ID, computed, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { AuthStateService } from '../../../core/auth';
import { CurrentUser } from '../../../core/models/auth.model';
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
  private readonly platformId = inject(PLATFORM_ID);

  protected readonly user = this.authState.currentUser;
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