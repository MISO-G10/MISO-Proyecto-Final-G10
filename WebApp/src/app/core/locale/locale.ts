export const supportedLocales = [
  'en',
  'es'
] as const;

export type Locale = typeof supportedLocales[number];
