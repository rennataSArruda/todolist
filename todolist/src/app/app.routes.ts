import { Routes } from '@angular/router';

import { authGuard, loginGuard } from './core/guards';

export const routes: Routes = [
  {
    path: 'login',
    canActivate: [loginGuard],
    loadComponent: () => import('./features/auth/login/login.page').then((module) => module.LoginPage),
  },
  {
    path: 'forgot-password',
    canActivate: [loginGuard],
    loadComponent: () =>
      import('./features/auth/forgot-password/forgot-password.page').then((module) => module.ForgotPasswordPage),
  },
  {
    path: 'reset-password',
    canActivate: [loginGuard],
    loadComponent: () =>
      import('./features/auth/reset-password/reset-password.page').then((module) => module.ResetPasswordPage),
  },
  {
    path: 'register',
    canActivate: [loginGuard],
    loadComponent: () => import('./features/auth/register/register.page').then((module) => module.RegisterPage),
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./features/dashboard/dashboard.page').then((module) => module.DashboardPage),
  },
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },
  {
    path: '**',
    redirectTo: 'dashboard',
  },
];

