'use client'

import type * as React from 'react'
import { QueryClientProvider } from '@tanstack/react-query'

import { ThemeProvider } from '@acme/ui/theme'

import { getQueryClient } from '~/lib/query-client'

export function Providers({ children }: { children: React.ReactNode }) {
  const queryClient = getQueryClient()

  return (
    <ThemeProvider attribute="class" defaultTheme="system" enableSystem>
      <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
    </ThemeProvider>
  )
}
