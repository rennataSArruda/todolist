import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { FloatingThemeToggle } from './shared/components/floating-theme-toggle';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, FloatingThemeToggle],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected readonly title = signal('todolist');
}
