import { defineConfig } from 'orval'

export default defineConfig({
  api: {
    input: {
      target: 'http://localhost:4000/openapi',
    },
    output: {
      target: './packages/client/src/api.ts',
      baseUrl: {
        getBaseUrlFromSpecification: true,
      },
      schemas: './packages/client/src/schemas',
      client: 'react-query',
    },
    hooks: {
      afterAllFilesWrite: 'turbo -F @acme/client format -- --write',
    },
  },
})
