import { ApplicationConfig, importProvidersFrom, LOCALE_ID, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, RouteReuseStrategy } from '@angular/router';

import { appRoutes } from './app.routes';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { errorInterceptor } from './core/interceptors/errors-interceptor';
import { provideAnimations } from '@angular/platform-browser/animations';
import { loaderInterceptor } from './core/interceptors/loader.interceptor';
import { authInterceptor } from './core/interceptors/auth.interceptor';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { localeProvider } from './core/locale/locale.provider';
import { I18nRouteReuseStrategy } from './core/locale/i18n-route-reuse.strategy';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(appRoutes), provideAnimationsAsync(),
    provideHttpClient(
      withInterceptors([
        loaderInterceptor,
        authInterceptor,
        errorInterceptor
      ])
    ),
    importProvidersFrom(MatProgressBarModule),
    provideAnimations(),
    { provide: LOCALE_ID, useValue: localeProvider }, // Not necessary with the native `Intl` API.
    { provide: RouteReuseStrategy, useClass: I18nRouteReuseStrategy }
  ]
};
