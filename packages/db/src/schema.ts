import type z from 'zod'
import { integer, pgTable, text, timestamp, uuid } from 'drizzle-orm/pg-core'
import {
  createInsertSchema,
  createSelectSchema,
  createUpdateSchema,
} from 'drizzle-zod'
import { v7 } from 'uuid'

export const certificates = pgTable('certificates', {
  id: uuid()
    .primaryKey()
    .$default(() => v7()),
  lead_id: integer().notNull(),
  business_name: text().notNull(),
  retrieve_until: timestamp().notNull(),
  url: text().notNull(),
  retrieved_at: timestamp(),
  created_at: timestamp()
    .$defaultFn(() => new Date())
    .notNull(),
  updated_at: timestamp().$onUpdateFn(() => new Date()),
})

export const certificateSelectSchema = createSelectSchema(certificates).extend(
  {},
)
export const certificateInsertSchema = createInsertSchema(certificates).extend(
  {},
)
export const certificateUpdateSchema = createUpdateSchema(certificates).extend(
  {},
)

export type Certificate = z.infer<typeof certificateSelectSchema>
export type InsertCertificate = z.infer<typeof certificateInsertSchema>
export type UpdateCertificate = z.infer<typeof certificateUpdateSchema>
