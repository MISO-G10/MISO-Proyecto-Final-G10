// role.guard.ts
import { Injectable, inject } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);
  private readonly authService = inject(AuthService);

  canActivate(route: any): boolean | UrlTree {
    const currentUser = this.authService.getCurrentUser();
    
    if (!currentUser) {
      this.snackBar.open('Debes iniciar sesión', 'Cerrar', { duration: 3000 });
      return this.router.createUrlTree(['/login']);
    }

    const requiredRoles = route.data['roles'] as string[];
    
    if (!requiredRoles || requiredRoles.includes(currentUser.rol)) {
      return true;
    }

    this.snackBar.open('No tienes permisos para acceder a esta página', 'Cerrar', { duration: 3000 });
    return this.router.createUrlTree(['/private/home']);
  }
}