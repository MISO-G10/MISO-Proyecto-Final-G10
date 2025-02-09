/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: 'class',
  content: ['./src/**/*.{ts,tsx}', './node_modules/@rnr/**/*.{ts,tsx}'],
  presets: [
    require('@acme/tailwind-config/native'),
    require('nativewind/preset'),
  ],
}
