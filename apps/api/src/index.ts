import { serve } from '@hono/node-server'
import { apiReference } from '@scalar/hono-api-reference'
import { Hono } from 'hono'
import { describeRoute, openAPISpecs } from 'hono-openapi'
import { resolver } from 'hono-openapi/zod'
import { cors } from 'hono/cors'
import { z } from 'zod'

import { env } from './lib/env'

const app = new Hono()

app.use(cors())

const responseSchema = z.array(
  z.object({
    id: z.number(),
    name: z.string(),
  }),
)

app.get(
  '/',
  describeRoute({
    validateResponse: true,
    description: 'Root route',
    responses: {
      200: {
        description: 'Success',
        content: {
          'application/json': {
            schema: resolver(responseSchema),
          },
        },
      },
    },
  }),
  (c) => {
    return c.json(
      [
        { id: 1, name: 'Alice' },
        { id: 2, name: 'Bob' },
      ],
      200,
    )
  },
)

app.get(
  '/openapi',
  openAPISpecs(app, {
    documentation: {
      info: {
        title: 'Hono API',
        version: '1.0.0',
        description: 'Greeting API',
      },
      servers: [
        {
          url: `http://localhost:${env.APP_PORT}`,
          description: 'Local Server',
        },
      ],
    },
  }),
)

app.get(
  '/docs',
  apiReference({
    theme: 'saturn',
    spec: { url: '/openapi' },
  }),
)

const port = env.APP_PORT
console.log(`Server is running on http://localhost:${port}`)

serve({
  fetch: app.fetch,
  port,
})
