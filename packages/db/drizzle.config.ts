import { defineConfig } from 'drizzle-kit'

const DB_URL = process.env.DB_URL

if (!DB_URL) {
  throw new Error('DB_URL is required')
}

export default defineConfig({
  out: './drizzle',
  schema: './src/schema.ts',
  dialect: 'postgresql',
  dbCredentials: {
    url: DB_URL,
  },
})
