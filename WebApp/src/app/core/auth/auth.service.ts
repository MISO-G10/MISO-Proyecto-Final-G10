import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { User } from '../../features/auth/login/models/user';
import { environment } from '../../../environment/environment';
import { SnackbarService } from '../../shared/ui/snackbar.service';
interface AuthResponse {
  expireAt: string;
  id: string;
  token: string;
  message: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);
  private readonly apiUrl = environment.apiUrl+'/usuarios';
  private readonly snackbarService = inject(SnackbarService);

  login(username: string, password: string) {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth`, { username, password })
      .subscribe({
        next: (response) => {
          if (response.token) {
            localStorage.setItem('auth', response.token);
            this.getUser();
          }
        }
      });
  }

  getUser() {
    return this.http.get<User>(`${this.apiUrl}/me`).subscribe({
      next: (response) => {
        if (response) {
          localStorage.setItem('user', JSON.stringify(response));
          this.snackbarService.success('Bienvenido', {
            duration: 5000,
            position: { horizontal: 'center', vertical: 'bottom' }
          });

          this.router.navigate(['private/home']);
        }
      },
      error: (error) => {
        this.snackbarService.error(error, {
          duration: 5000,
          position: { horizontal: 'center', vertical: 'bottom' }
        });
      }
    });
  }

  //Hay que mejorar esta funcion, ya que yo podria sencillamente cambiar el token en el localstorage y acceder a la aplicacion sin tener que logearme, se debe llamar al servicio /me
  isAuthenticated(): boolean {
    return !!localStorage.getItem('auth');
  }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/login']);

    this.snackbarService.warning('Sesión cerrada', {
      duration: 5000,
      position: { horizontal: 'center', vertical: 'bottom' }
    });
  }

  getToken(): string | null {
    return localStorage.getItem('auth');
  }

  getCurrentUser(): User | null {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }
}
