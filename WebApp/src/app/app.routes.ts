import { Routes } from '@angular/router';

export const appRoutes: Routes = [
    { path: 'login', loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent) },
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    { path: 'signin', loadComponent: () => import('./features/auth/signin/signin.component').then(m => m.SigninComponent) },
    { path: 'private', loadChildren: () => import('./features/private/private.routes').then(m => m.PRIVATE_ROUTES) }
  ];