import * as process from 'node:process'
import { drizzle } from 'drizzle-orm/node-postgres'

import * as schema from './schema'

const DB_URL = process.env.DB_URL

if (!DB_URL) {
  throw new Error('DB_URL is required')
}

export const db = drizzle({
  connection: DB_URL,
  schema,
})
