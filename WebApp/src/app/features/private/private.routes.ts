import { Routes } from '@angular/router';
import { AuthGuard } from '../../core/auth/auth.guard';
import { RoleGuard } from '../../core/auth/role.guard';

export const PRIVATE_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./private.component').then(m => m.PrivateComponent),
    canActivate: [AuthGuard],
    children: [
      { path: 'home', loadComponent: () => import('./home/home.component').then(m => m.HomeComponent) },
      {
        path: 'sellers', loadComponent: () => import('./sellers/sellers.component').then(m => m.SellersComponent),
        canActivate: [RoleGuard],
        data: { roles: ['DIRECTOR_VENTAS', 'ADMINISTRADOR'] }
      },
      {
        path: 'sellers/create',
        loadComponent: () => import('./sellers/create-seller/create-seller.component').then(m => m.CreateSellerComponent),
        canActivate: [RoleGuard],
        data: { roles: ['DIRECTOR_VENTAS', 'ADMINISTRADOR'] }
      },
      {
        path: 'sellers/edit', loadComponent: () => import('./sellers/update-seller/update-seller.component').then(m => m.UpdateSellerComponent),
          canActivate: [RoleGuard],
          data: { roles: ['DIRECTOR_VENTAS', 'ADMINISTRADOR'] }
      },
      {
        path: 'sellers/sales-report/:id',
        loadComponent: () => import('./sellers/sales-report/sales-report.component').then(m => m.SalesReportComponent),
        canActivate: [RoleGuard],
        data: { roles: ['DIRECTOR_VENTAS', 'ADMINISTRADOR'] }
      },

      {
        path: 'sales', loadComponent: () => import('./sales/sales.component').then(m => m.SalesComponent),
        canActivate: [RoleGuard],
        data: { roles: ['DIRECTOR_VENTAS', 'ADMINISTRADOR'] }
      },

      {
        path: 'sales/create',
        loadComponent: () => import('./sales/create/create.component').then(m => m.CreateComponent),
        canActivate: [RoleGuard],
        data: { roles: ['DIRECTOR_VENTAS', 'ADMINISTRADOR'] }
      },

      {
        path: 'fabricantes',
        loadComponent: () => import('./fabricantes/fabricantes.component').then(m => m.FabricantesComponent),
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRADOR', 'ENCARGADO_COMPRAS_PROVEEDORES'] }
      },

      {
        path: 'fabricantes/create',
        loadComponent: () => import('./fabricantes/create-fabricante/create-fabricante.component').then(c => c.CreateFabricanteComponent),
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRADOR'] }
      },

      {
        path: 'crear-producto/:fabricanteId',
        loadComponent: () => import('./productos/crear-producto/crear-producto.component').then(m => m.CrearProductoComponent),
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRADOR', 'ENCARGADO_COMPRAS_PROVEEDORES'] }
      },
      {
        path: 'productos/:productoId',
        loadComponent: () => import('./productos/producto-detalle/producto-detalle.component').then(m => m.ProductoDetalleComponent),
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRADOR', 'LOGISTICA'] }
      },

      {
        path: 'productos',
        loadComponent: () => import('./productos/productos.component').then(m => m.ProductosComponent),
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRADOR', 'LOGISTICA'] }
      },
      {
        path: 'routes', 
        loadComponent: () => import('./rutas/rutas.component').then(m => m.RutasComponent),
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRADOR', 'LOGISTICA'] }
      },
      { path: '', redirectTo: 'home', pathMatch: 'full' }
    ]
  }
];
