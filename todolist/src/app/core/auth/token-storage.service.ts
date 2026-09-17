import { Injectable, inject } from '@angular/core';

import { LoginResponse } from '../models/auth.model';
import { EncryptedStorageService } from '../storage';

interface StoredTokens {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresAt: number;
}

@Injectable({
  providedIn: 'root',
})
export class TokenStorageService {
  private readonly encryptedStorage = inject(EncryptedStorageService);
  private readonly storageKey = 'todolist.auth.tokens';

  async getAccessToken(): Promise<string | null> {
    return (await this.getTokens())?.accessToken ?? null;
  }

  async getRefreshToken(): Promise<string | null> {
    return (await this.getTokens())?.refreshToken ?? null;
  }

  async getTokenType(): Promise<string> {
    return (await this.getTokens())?.tokenType || 'Bearer';
  }

  async getAuthorizationHeader(): Promise<string | null> {
    const tokens = await this.getTokens();

    if (!tokens?.accessToken) {
      return null;
    }

    return `${tokens.tokenType || 'Bearer'} ${tokens.accessToken}`;
  }

  async isAuthenticated(): Promise<boolean> {
    const tokens = await this.getTokens();
    return Boolean(tokens?.accessToken && tokens.refreshToken);
  }

  async isAccessTokenExpired(): Promise<boolean> {
    const expiresAt = (await this.getTokens())?.expiresAt;
    return !expiresAt || Date.now() >= expiresAt;
  }

  async save(response: LoginResponse): Promise<void> {
    this.encryptedStorage.removeItem(this.storageKey, 'local');

    await this.setTokens({
      accessToken: response.accessToken,
      refreshToken: response.refreshToken,
      tokenType: response.tokenType || 'Bearer',
      expiresAt: Date.now() + response.expiresIn * 1000,
    });
  }

  clear(): void {
    this.encryptedStorage.removeItem(this.storageKey, 'session');
    this.encryptedStorage.removeItem(this.storageKey, 'local');
  }

  private async getTokens(): Promise<StoredTokens | null> {
    const rawTokens = await this.getRawTokens();

    if (!rawTokens) {
      return null;
    }

    try {
      return JSON.parse(rawTokens) as StoredTokens;
    } catch {
      this.clear();
      return null;
    }
  }

  private async getRawTokens(): Promise<string | null> {
    const sessionTokens = await this.encryptedStorage.getItem(this.storageKey, 'session');

    if (sessionTokens) {
      return sessionTokens;
    }

    const localTokens = await this.encryptedStorage.getItem(this.storageKey, 'local');
    this.encryptedStorage.removeItem(this.storageKey, 'local');

    if (localTokens) {
      await this.encryptedStorage.setItem(this.storageKey, localTokens, 'session');
    }

    return localTokens;
  }

  private async setTokens(tokens: StoredTokens): Promise<void> {
    await this.encryptedStorage.setItem(this.storageKey, JSON.stringify(tokens), 'session');
  }
}
