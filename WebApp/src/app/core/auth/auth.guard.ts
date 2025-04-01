// auth.guard.ts
import { Injectable, inject } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);
  private authService = inject(AuthService);

  canActivate(): boolean | UrlTree {
    if (!this.authService.isAuthenticated()) {
      this.snackBar.open('Debes iniciar sesión para acceder a esta página', 'Cerrar', { duration: 3000 });
      return this.router.createUrlTree(['/login']);
    }
    
    return true;
  }
}