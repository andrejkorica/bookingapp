<script setup lang="ts">
import type { TabsItem } from '@nuxt/ui'
import SellerTab from './options/sellerTab.vue'
import ProfileTab from './options/profileTab.vue'

const isOpen = defineModel<boolean>('open', {
  default: false
})

const selectedTab = defineModel<string>('tab', {
  default: 'profile'
})

const items: TabsItem[] = [
  { label: 'Profile', value: 'profile', icon: 'i-lucide-user' },
  { label: 'Seller Info', value: 'seller', icon: 'i-lucide-store' },
  { label: 'Booking History', value: 'bookings', icon: 'i-lucide-calendar' }
]
</script>

<template>
  <UModal
    v-model:open="isOpen"
    :ui="{
      content: 'max-w-3xl w-full max-h-[90vh] p-4 overflow-hidden'
    }"
    title="Account settings"
    description="Manage your profile, seller information, and booking history."

  >
    <template #content>
      <div class="flex max-h-[calc(90vh-2rem)] flex-col space-y-4">
        <UTabs
          v-model="selectedTab"
          :items="items"
          variant="link"
          class="w-full shrink-0"
          :ui="{
            list: 'flex w-full',
            trigger: 'flex-1 flex justify-center'
          }"
        />

        <div class="min-h-[300px] overflow-y-auto p-2 pr-3">
          <ProfileTab v-if="selectedTab === 'profile'" />
          <SellerTab v-else-if="selectedTab === 'seller'" />
        </div>
      </div>
    </template>
  </UModal>
</template>