import baseConfig from '@acme/eslint-config/base'

/** @type {import('typescript-eslint').Config} */
export default [
  ...baseConfig,
  {
    rules: {
      '@typescript-eslint/no-unsafe-return': 'off',
    },
  },
]
