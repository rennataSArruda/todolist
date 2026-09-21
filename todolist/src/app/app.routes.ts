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
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./shared/components/authenticated-layout/authenticated-layout').then(
        (module) => module.AuthenticatedLayout,
      ),
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard.page').then((module) => module.DashboardPage),
      },
      {
        path: 'meu-perfil',
        loadComponent: () => import('./features/my-profile/my-profile.page').then((module) => module.MyProfilePage),
      },
      {
        path: 'categorias',
        loadComponent: () =>
          import('./features/categories/category-list/category-list.page').then((module) => module.CategoryListPage),
      },
      {
        path: 'categorias/nova',
        loadComponent: () =>
          import('./features/categories/category-form/category-form.page').then((module) => module.CategoryFormPage),
      },
      {
        path: 'categorias/:id/editar',
        loadComponent: () =>
          import('./features/categories/category-form/category-form.page').then((module) => module.CategoryFormPage),
      },
{
        path: 'usuarios/novo',
        loadComponent: () => import('./features/users/user-form/user-form.page').then((module) => module.UserFormPage),
      },
      {
        path: 'usuarios/:id/editar',
        loadComponent: () => import('./features/users/user-form/user-form.page').then((module) => module.UserFormPage),
      },      {
        path: 'usuarios',
        loadComponent: () => import('./features/users/user-list/user-list.page').then((module) => module.UserListPage),
      },
      {
        path: 'perfis',
        loadComponent: () => import('./features/profiles/profile-list/profile-list.page').then((module) => module.ProfileListPage),
      },
      {
        path: 'perfis/novo',
        loadComponent: () => import('./features/profiles/profile-form/profile-form.page').then((module) => module.ProfileFormPage),
      },
      {
        path: 'perfis/:id/editar',
        loadComponent: () => import('./features/profiles/profile-form/profile-form.page').then((module) => module.ProfileFormPage),
      },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: '**', redirectTo: 'dashboard' },
    ],
  },
];
