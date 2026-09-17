import { isPlatformBrowser } from '@angular/common';
import { Injectable, PLATFORM_ID, inject } from '@angular/core';

const ENCRYPTED_VALUE_PREFIX = 'enc:v1:';
const APP_STORAGE_SECRET = 'todolist-client-storage-v1';
const TEXT_ENCODER = new TextEncoder();
const TEXT_DECODER = new TextDecoder();

export type EncryptedStorageTarget = 'local' | 'session';

@Injectable({
  providedIn: 'root',
})
export class EncryptedStorageService {
  private readonly platformId = inject(PLATFORM_ID);
  private readonly keyPromise = this.createKey();

  async getItem(key: string, target: EncryptedStorageTarget = 'local'): Promise<string | null> {
    if (!this.canUseStorage()) {
      return null;
    }

    const storedValue = this.getStorage(target).getItem(key);

    if (!storedValue) {
      return null;
    }

    if (!storedValue.startsWith(ENCRYPTED_VALUE_PREFIX)) {
      await this.setItem(key, storedValue, target);
      return storedValue;
    }

    try {
      return await this.decrypt(storedValue.slice(ENCRYPTED_VALUE_PREFIX.length));
    } catch {
      this.removeItem(key, target);
      return null;
    }
  }

  async setItem(key: string, value: string, target: EncryptedStorageTarget = 'local'): Promise<void> {
    if (!this.canUseStorage()) {
      return;
    }

    const encryptedValue = await this.encrypt(value);
    this.getStorage(target).setItem(key, `${ENCRYPTED_VALUE_PREFIX}${encryptedValue}`);
  }

  removeItem(key: string, target: EncryptedStorageTarget = 'local'): void {
    if (!this.canUseStorage()) {
      return;
    }

    this.getStorage(target).removeItem(key);
  }

  private async encrypt(value: string): Promise<string> {
    const iv = crypto.getRandomValues(new Uint8Array(12));
    const encryptedBytes = await crypto.subtle.encrypt(
      { name: 'AES-GCM', iv },
      await this.keyPromise,
      TEXT_ENCODER.encode(value),
    );

    return this.toBase64(this.joinBytes(iv, new Uint8Array(encryptedBytes)));
  }

  private async decrypt(value: string): Promise<string> {
    const encryptedBytes = this.fromBase64(value);
    const iv = encryptedBytes.slice(0, 12);
    const data = encryptedBytes.slice(12);
    const decryptedBytes = await crypto.subtle.decrypt({ name: 'AES-GCM', iv }, await this.keyPromise, data);

    return TEXT_DECODER.decode(decryptedBytes);
  }

  private async createKey(): Promise<CryptoKey> {
    const digest = await crypto.subtle.digest('SHA-256', TEXT_ENCODER.encode(APP_STORAGE_SECRET));
    return crypto.subtle.importKey('raw', digest, { name: 'AES-GCM' }, false, ['encrypt', 'decrypt']);
  }

  private getStorage(target: EncryptedStorageTarget): Storage {
    return target === 'session' ? window.sessionStorage : window.localStorage;
  }

  private joinBytes(first: Uint8Array, second: Uint8Array): Uint8Array {
    const joined = new Uint8Array(first.length + second.length);
    joined.set(first);
    joined.set(second, first.length);
    return joined;
  }

  private toBase64(bytes: Uint8Array): string {
    let binary = '';
    bytes.forEach((byte) => {
      binary += String.fromCharCode(byte);
    });

    return btoa(binary);
  }

  private fromBase64(value: string): Uint8Array {
    return Uint8Array.from(atob(value), (char) => char.charCodeAt(0));
  }

  private canUseStorage(): boolean {
    return isPlatformBrowser(this.platformId) && typeof window.crypto?.subtle !== 'undefined';
  }
}
