// interceptors/auth.interceptor.ts
import { HttpRequest, HttpHandlerFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../auth/auth.service';

export function authInterceptor(request: HttpRequest<unknown>, next: HttpHandlerFn) {
  const authService = inject(AuthService);
  
  // Excluir endpoints
  if (request.url.includes('/auth') || (request.method === 'POST' && request.url=="http://localhost:3000/usuarios")) {
    console.log("excluyento ruta de autenticación",request.url , "metodo", request.method);
    return next(request);
  }

  const token = authService.getToken();
  
  if (token) {
    const authReq = request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`   ,            
      }
    });
    return next(authReq);
  }
  
  return next(request);
}