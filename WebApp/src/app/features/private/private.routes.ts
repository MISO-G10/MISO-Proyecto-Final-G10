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

        { path: 'sellers/create', loadComponent: () => import('./sellers/create-seller/create-seller.component').then(m => m.CreateSellerComponent),
          canActivate: [RoleGuard],
          data: { roles: ['DIRECTOR_VENTAS', 'ADMINISTRADOR'] }
        }, 
        

        { path: 'fabricantes-temp', loadComponent: () => import('./fabricantes-temp/fabricantes-temp.component').then(m => m.FabricantesTempComponent),
          canActivate: [RoleGuard],
          data: { roles: ['ADMINISTRADOR'] }
        },
        { path: 'crear-producto/:manufacturerId', loadComponent: () => import('./productos/crear-producto/crear-producto.component').then(m => m.CrearProductoComponent),
          canActivate: [RoleGuard],
          data: { roles: ['ADMINISTRADOR'] }
        },
        { path: '', redirectTo: 'home', pathMatch: 'full' },
         
      ]
    }
  ];