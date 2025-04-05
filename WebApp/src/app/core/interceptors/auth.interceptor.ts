// interceptors/auth.interceptor.ts
import { HttpRequest, HttpHandlerFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../auth/auth.service';

export function authInterceptor(request: HttpRequest<unknown>, next: HttpHandlerFn) {
  const authService = inject(AuthService);
  
  // Excluir endpoints de autenticación
  if (request.url.includes('/auth')) {
    return next(request);
  }

  const token = authService.getToken();
  
  if (token) {
    const authReq = request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`        
      }
    });
    return next(authReq);
  }
  
  return next(request);
}