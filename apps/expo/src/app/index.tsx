import { View } from 'react-native'
import { SafeAreaView } from 'react-native-safe-area-context'
import { Link, Stack } from 'expo-router'

import { useGetIndex } from '@acme/client/api'

import { Button } from '~/components/ui/button'
import { Text } from '~/components/ui/text'

export default function Index() {
  const query = useGetIndex()

  if (query.isPending) {
    return <Text>Loading...</Text>
  }

  return (
    <SafeAreaView className="bg-background">
      <Stack.Screen options={{ title: 'Home Page' }} />
      <View className="h-full w-full gap-y-2 p-4">
        <Text className="pb-2 text-center text-5xl font-bold text-foreground">
          Test
        </Text>

        {/*<Button onPress={() => scheme.toggleColorScheme()}>*/}
        {/*  <Text>Test</Text>*/}
        {/*</Button>*/}

        <Link href="/post/1" asChild>
          <Button>
            <Text>Post 1</Text>
          </Button>
        </Link>

        {query.data?.data.map((post, index) => (
          <View key={`${index}`}>
            <Text>{post.name}</Text>
          </View>
        ))}
      </View>
    </SafeAreaView>
  )
}
