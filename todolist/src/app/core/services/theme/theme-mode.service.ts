import { DOCUMENT, isPlatformBrowser } from '@angular/common';
import { computed, inject, Injectable, PLATFORM_ID, signal } from '@angular/core';

import { EncryptedStorageService } from '../../storage';

export type ThemeMode = 'light' | 'dark';

@Injectable({
  providedIn: 'root',
})
export class ThemeModeService {
  private static readonly storageKey = 'todolist.theme-mode';

  private readonly document = inject(DOCUMENT);
  private readonly encryptedStorage = inject(EncryptedStorageService);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly isBrowser = isPlatformBrowser(this.platformId);
  private readonly modeSignal = signal<ThemeMode>('light');

  readonly mode = this.modeSignal.asReadonly();
  readonly isDarkMode = computed(() => this.modeSignal() === 'dark');
  readonly isLightMode = computed(() => this.modeSignal() === 'light');

  constructor() {
    this.setMode(this.resolvePreferredMode(), false);
    void this.applyStoredMode();
  }

  setLightMode(): void {
    this.setMode('light');
  }

  setDarkMode(): void {
    this.setMode('dark');
  }

  toggleMode(): ThemeMode {
    const nextMode: ThemeMode = this.isDarkMode() ? 'light' : 'dark';
    this.setMode(nextMode);
    return nextMode;
  }

  setMode(mode: ThemeMode, persist = true): void {
    this.modeSignal.set(mode);
    this.applyMode(mode);

    if (persist) {
      void this.persistMode(mode);
    }
  }

  private async applyStoredMode(): Promise<void> {
    const storedMode = await this.getStoredMode();

    if (storedMode) {
      this.setMode(storedMode, false);
    }
  }

  private resolvePreferredMode(): ThemeMode {
    if (!this.isBrowser) {
      return 'light';
    }

    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  }

  private async getStoredMode(): Promise<ThemeMode | null> {
    try {
      const storedMode = await this.encryptedStorage.getItem(ThemeModeService.storageKey);
      return this.isThemeMode(storedMode) ? storedMode : null;
    } catch {
      return null;
    }
  }

  private async persistMode(mode: ThemeMode): Promise<void> {
    try {
      await this.encryptedStorage.setItem(ThemeModeService.storageKey, mode);
    } catch {
      // localStorage can be unavailable in private contexts; the visual mode still applies.
    }
  }

  private applyMode(mode: ThemeMode): void {
    if (!this.isBrowser) {
      return;
    }

    const rootElement = this.document.documentElement;

    rootElement.classList.remove('theme-light', 'theme-dark');
    rootElement.classList.add(`theme-${mode}`);
    rootElement.dataset['theme'] = mode;
    rootElement.style.colorScheme = mode;
  }

  private isThemeMode(value: string | null): value is ThemeMode {
    return value === 'light' || value === 'dark';
  }
}
