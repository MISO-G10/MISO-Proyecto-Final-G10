'use client'

import { useGetIndex } from '@acme/client/api'

export default function HomePage() {
  const query = useGetIndex()

  if (query.isLoading) {
    return <div>Loading...</div>
  }

  return (
    <main className="container h-screen py-16">
      <div className="flex flex-col items-center justify-center gap-4">
        <h1 className="text-5xl font-extrabold tracking-tight sm:text-[5rem]">
          Miso
        </h1>

        {query.data?.data.map((post, index) => (
          <div key={`${index}`}>
            <h2>{post.name}</h2>
          </div>
        ))}

        <div className="w-full max-w-2xl overflow-y-scroll"></div>
      </div>
    </main>
  )
}
