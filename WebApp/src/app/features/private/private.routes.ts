import { Routes } from '@angular/router';
import { AuthGuard } from '../../core/auth/auth.guard';

export const PRIVATE_ROUTES: Routes = [
    { 
      path: '', 
      loadComponent: () => import('./private.component').then(m => m.PrivateComponent),
      canActivate:[AuthGuard]
      ,
      children: [
        { path: 'home', loadComponent: () => import('./home/home.component').then(m => m.HomeComponent) },        
        { path: '', redirectTo: 'home', pathMatch: 'full' },
        { path: 'sellers', loadComponent: () => import('./sellers/sellers.component').then(m => m.SellersComponent) }, 
         
      ]
    }
  ];