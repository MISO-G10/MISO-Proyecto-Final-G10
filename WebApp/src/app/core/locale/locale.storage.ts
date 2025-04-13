import { Locale } from './locale';
import { getInitialLocale } from './get-initial-locale';

class LocaleStorage {
  #locale: Locale = getInitialLocale();

  getLocale(): Locale {
    return this.#locale;
  }

  setLocale(locale: Locale) {
    this.#locale = locale;
    globalThis.localStorage.setItem('locale', locale);
  }
}

export const localeStorage = new LocaleStorage();
