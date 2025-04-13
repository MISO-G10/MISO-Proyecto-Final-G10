import { clearTranslations, loadTranslations } from '@angular/localize';


import { setDocumentI18nAttributes } from './set-document-i18n-attributes';
import { defaultLocale } from './get-initial-locale';
import { localeStorage } from './locale.storage';
import { Locale } from './locale';

export const setLocaleData = (locale: Locale): Promise<void> => {
  localeStorage.setLocale(locale);
  $localize.locale = locale;

  clearTranslations();

  // It is not necessary to load any translations for the default locale.
  if (locale === defaultLocale) {
    setDocumentI18nAttributes(locale);
    return Promise.resolve();
  }

  return fetch(`/assets/locales/${locale}.json`)
    .then(response => response.json())
    .catch(error => console.error(error))
    .then(response => {
      loadTranslations(response.translations);
      setDocumentI18nAttributes(response.locale);
    });
};
