import { Routes } from '@angular/router';
import { AuthGuard } from '../../core/auth/auth.guard';
import { RoleGuard } from '../../core/auth/role.guard';

export const PRIVATE_ROUTES: Routes = [
    { 
      path: '', 
      loadComponent: () => import('./private.component').then(m => m.PrivateComponent),
      canActivate:[AuthGuard]
      ,
      children: [
        { path: 'home', loadComponent: () => import('./home/home.component').then(m => m.HomeComponent) },
        { path: 'sellers', loadComponent: () => import('./sellers/sellers.component').then(m => m.SellersComponent),
          canActivate: [RoleGuard],
          data: { roles: ['DIRECTOR_VENTAS', 'ADMINISTRADOR'] }
        }, 
        
        { path: '', redirectTo: 'home', pathMatch: 'full' },
         
      ]
    }
  ];