// interceptors/loader.interceptor.ts
import { HttpRequest, HttpHandlerFn, HttpEvent } from '@angular/common/http';
import { inject } from '@angular/core';
import { MatProgressBar } from '@angular/material/progress-bar';
import { Observable, finalize } from 'rxjs';
import { LoadingService } from '../services/loading.services';


export function loaderInterceptor(request: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {
  const loadingService = inject(LoadingService);
  
  // Mostrar loader solo para ciertas peticiones (opcional)
  if (!request.url.includes('/notifications')) {
    loadingService.show();
  }

  return next(request).pipe(
    finalize(() => {
      loadingService.hide();
    })
  );
}