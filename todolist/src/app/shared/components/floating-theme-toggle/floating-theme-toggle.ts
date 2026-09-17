import { isPlatformBrowser } from '@angular/common';
import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  ElementRef,
  PLATFORM_ID,
  ViewChild,
  computed,
  inject,
  signal,
} from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

import { ThemeModeService } from '../../../core/services';
import { EncryptedStorageService } from '../../../core/storage';

type TogglePosition = {
  x: number;
  y: number;
};

type DragState = {
  pointerId: number;
  startClientX: number;
  startClientY: number;
  startX: number;
  startY: number;
  moved: boolean;
};

type ViewportBounds = {
  minX: number;
  minY: number;
  maxX: number;
  maxY: number;
};

@Component({
  selector: 'app-floating-theme-toggle',
  standalone: true,
  imports: [MatIconModule, MatTooltipModule],
  templateUrl: './floating-theme-toggle.html',
  styleUrl: './floating-theme-toggle.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FloatingThemeToggle implements AfterViewInit {
  private static readonly positionStorageKey = 'todolist.theme-toggle-position';
  private static readonly dragThreshold = 6;

  private readonly elementRef = inject<ElementRef<HTMLElement>>(ElementRef);
  private readonly destroyRef = inject(DestroyRef);
  private readonly encryptedStorage = inject(EncryptedStorageService);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly isBrowser = isPlatformBrowser(this.platformId);

  protected readonly themeMode = inject(ThemeModeService);
  protected readonly position = signal<TogglePosition>({ x: 0, y: 0 });
  protected readonly ready = signal(false);
  protected readonly isDragging = signal(false);
  protected readonly title = 'Alternar tema';
  protected readonly ariaLabel = computed(() =>
    this.themeMode.isDarkMode() ? 'Alternar para tema claro' : 'Alternar para tema escuro',
  );

  @ViewChild('toggleButton')
  private toggleButton?: ElementRef<HTMLButtonElement>;

  private dragState: DragState | null = null;
  private suppressNextClick = false;

  ngAfterViewInit(): void {
    if (!this.isBrowser) {
      return;
    }

    void this.initializePosition();

    const handleResize = (): void => {
      this.position.update((position) => this.clampPosition(position));
      void this.persistPosition(this.position());
    };

    window.addEventListener('resize', handleResize, { passive: true });
    this.destroyRef.onDestroy(() => window.removeEventListener('resize', handleResize));
  }

  protected onPointerDown(event: PointerEvent): void {
    if (!this.isBrowser || (event.pointerType === 'mouse' && event.button !== 0)) {
      return;
    }

    const button = this.toggleButton?.nativeElement;

    if (!button) {
      return;
    }

    button.setPointerCapture(event.pointerId);
    this.dragState = {
      pointerId: event.pointerId,
      startClientX: event.clientX,
      startClientY: event.clientY,
      startX: this.position().x,
      startY: this.position().y,
      moved: false,
    };
  }

  protected onPointerMove(event: PointerEvent): void {
    if (!this.dragState || this.dragState.pointerId !== event.pointerId) {
      return;
    }

    const deltaX = event.clientX - this.dragState.startClientX;
    const deltaY = event.clientY - this.dragState.startClientY;
    const hasDragged = Math.hypot(deltaX, deltaY) > FloatingThemeToggle.dragThreshold;

    if (!hasDragged && !this.dragState.moved) {
      return;
    }

    event.preventDefault();
    this.dragState.moved = true;
    this.isDragging.set(true);
    this.position.set(
      this.clampPosition({
        x: this.dragState.startX + deltaX,
        y: this.dragState.startY + deltaY,
      }),
    );
  }

  protected onPointerUp(event: PointerEvent): void {
    if (!this.dragState || this.dragState.pointerId !== event.pointerId) {
      return;
    }

    this.releasePointer(event);

    if (this.dragState.moved) {
      this.suppressNextClick = true;
      const snappedPosition = this.snapToClosestEdge(this.position());
      this.position.set(snappedPosition);
      void this.persistPosition(snappedPosition);
    }

    this.dragState = null;
    this.isDragging.set(false);
  }

  protected onPointerCancel(event: PointerEvent): void {
    if (!this.dragState || this.dragState.pointerId !== event.pointerId) {
      return;
    }

    this.releasePointer(event);
    this.dragState = null;
    this.isDragging.set(false);
  }

  protected toggleTheme(event: MouseEvent): void {
    if (this.suppressNextClick) {
      event.preventDefault();
      event.stopPropagation();
      this.suppressNextClick = false;
      return;
    }

    this.themeMode.toggleMode();
  }

  private async initializePosition(): Promise<void> {
    await this.restorePosition();
    this.ready.set(true);
  }

  private async restorePosition(): Promise<void> {
    const storedPosition = await this.readStoredPosition();
    const initialPosition = storedPosition ?? this.getInitialPosition();

    this.position.set(this.clampPosition(initialPosition));
  }

  private getInitialPosition(): TogglePosition {
    const bounds = this.getViewportBounds();

    return {
      x: bounds.maxX,
      y: bounds.maxY,
    };
  }

  private snapToClosestEdge(position: TogglePosition): TogglePosition {
    const bounds = this.getViewportBounds();
    const distances = [
      { edge: 'left', value: Math.abs(position.x - bounds.minX) },
      { edge: 'right', value: Math.abs(bounds.maxX - position.x) },
      { edge: 'top', value: Math.abs(position.y - bounds.minY) },
      { edge: 'bottom', value: Math.abs(bounds.maxY - position.y) },
    ] as const;
    const closestEdge = distances.reduce((closest, current) =>
      current.value < closest.value ? current : closest,
    ).edge;

    if (closestEdge === 'left') {
      return this.clampPosition({ ...position, x: bounds.minX });
    }

    if (closestEdge === 'right') {
      return this.clampPosition({ ...position, x: bounds.maxX });
    }

    if (closestEdge === 'top') {
      return this.clampPosition({ ...position, y: bounds.minY });
    }

    return this.clampPosition({ ...position, y: bounds.maxY });
  }

  private clampPosition(position: TogglePosition): TogglePosition {
    const bounds = this.getViewportBounds();

    return {
      x: this.clamp(position.x, bounds.minX, bounds.maxX),
      y: this.clamp(position.y, bounds.minY, bounds.maxY),
    };
  }

  private getViewportBounds(): ViewportBounds {
    const button = this.toggleButton?.nativeElement;
    const buttonWidth = button?.offsetWidth ?? 56;
    const buttonHeight = button?.offsetHeight ?? 56;
    const insets = this.getViewportInsets();

    return {
      minX: insets.left,
      minY: insets.top,
      maxX: Math.max(insets.left, window.innerWidth - buttonWidth - insets.right),
      maxY: Math.max(insets.top, window.innerHeight - buttonHeight - insets.bottom),
    };
  }

  private getViewportInsets(): { top: number; right: number; bottom: number; left: number } {
    const styles = getComputedStyle(this.elementRef.nativeElement);
    const baseInset = this.readCssPixelValue(styles, '--theme-toggle-inset');

    return {
      top: baseInset + this.readCssPixelValue(styles, '--theme-toggle-safe-area-top'),
      right: baseInset + this.readCssPixelValue(styles, '--theme-toggle-safe-area-right'),
      bottom: baseInset + this.readCssPixelValue(styles, '--theme-toggle-safe-area-bottom'),
      left: baseInset + this.readCssPixelValue(styles, '--theme-toggle-safe-area-left'),
    };
  }

  private readCssPixelValue(styles: CSSStyleDeclaration, propertyName: string): number {
    const value = Number.parseFloat(styles.getPropertyValue(propertyName));
    return Number.isFinite(value) ? value : 0;
  }

  private async readStoredPosition(): Promise<TogglePosition | null> {
    try {
      const storedPosition = await this.encryptedStorage.getItem(FloatingThemeToggle.positionStorageKey);

      if (!storedPosition) {
        return null;
      }

      const parsedPosition = JSON.parse(storedPosition) as Partial<TogglePosition>;

      if (typeof parsedPosition.x !== 'number' || typeof parsedPosition.y !== 'number') {
        return null;
      }

      return {
        x: parsedPosition.x,
        y: parsedPosition.y,
      };
    } catch {
      return null;
    }
  }

  private async persistPosition(position: TogglePosition): Promise<void> {
    try {
      await this.encryptedStorage.setItem(FloatingThemeToggle.positionStorageKey, JSON.stringify(position));
    } catch {
      // Position persistence is optional; dragging should keep working when storage is unavailable.
    }
  }

  private releasePointer(event: PointerEvent): void {
    const button = this.toggleButton?.nativeElement;

    if (button?.hasPointerCapture(event.pointerId)) {
      button.releasePointerCapture(event.pointerId);
    }
  }

  private clamp(value: number, min: number, max: number): number {
    return Math.min(Math.max(value, min), max);
  }
}
