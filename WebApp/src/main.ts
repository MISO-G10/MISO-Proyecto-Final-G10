/// <reference types="@angular/localize" />

import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { setLocaleData } from './app/core/locale/set-locale-data';
import { localeStorage } from './app/core/locale/locale.storage';

setLocaleData(localeStorage.getLocale()).then(() => {
  bootstrapApplication(AppComponent, appConfig)
    .catch((err) => console.error(err));
});
