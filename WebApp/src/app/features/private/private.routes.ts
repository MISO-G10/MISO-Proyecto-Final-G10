import { Routes } from '@angular/router';

export const PRIVATE_ROUTES: Routes = [
    { 
      path: '', 
      loadComponent: () => import('./private.component').then(m => m.PrivateComponent),
      children: [
        { path: 'home', loadComponent: () => import('./home/home.component').then(m => m.HomeComponent) },        
        { path: '', redirectTo: 'home', pathMatch: 'full' },
        { path: 'sellers', loadComponent: () => import('./sellers/sellers.component').then(m => m.SellersComponent) }, 
              
      ]
    }
  ];