import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  { path: 'dashboard', renderMode: RenderMode.Client },
  { path: 'meu-perfil', renderMode: RenderMode.Client },
  { path: 'categorias', renderMode: RenderMode.Client },
  { path: 'categorias/nova', renderMode: RenderMode.Client },
  { path: 'categorias/:id/editar', renderMode: RenderMode.Client },
  { path: '**', renderMode: RenderMode.Prerender },
];
