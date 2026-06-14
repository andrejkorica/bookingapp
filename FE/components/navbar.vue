<template>
  <header class="border-b border-slate-200 bg-white">
    <nav
      class="container mx-auto px-4 sm:px-6 lg:px-8 flex justify-between items-center h-16"
    >
      <div class="flex items-center space-x-6">
        <NuxtLink
          to="/"
          class="text-2xl font-bold text-indigo-600 transition-all duration-300 hover:text-indigo-700"
        >
          MyBooking
        </NuxtLink>
      </div>

      <div v-if="!authStore.user" class="flex items-center space-x-3">
        <UButton
          label="Sign up"
          variant="soft"
          color="neutral"
          size="sm"
          to="/auth/signup"
        />
        <UButton
          label="Sign in"
          variant="solid"
          size="sm"
          class="bg-indigo-600 hover:bg-indigo-700 text-white font-semibold"
          to="/auth/signin"
        />
      </div>

      <div v-else class="flex items-center space-x-4">
        <span class="text-sm text-slate-600">
          Hi, {{ authStore.user.name }}
        </span>

        <UDropdownMenu
          :items="dropdownItems"
          :content="{ align: 'end', side: 'bottom', sideOffset: 6 }"
          :ui="{ content: 'w-56' }"
        >
          <UButton
            icon="i-lucide-user"
            variant="ghost"
            size="sm"
            class="text-slate-500 hover:text-slate-700 hover:bg-slate-100"
          />
        </UDropdownMenu>
      </div>
    </nav>

    <OptionsModal
      v-model:open="optionsModalOpen"
      v-model:tab="activeTab"
    />
  </header>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useAuthStore } from '~/stores/auth'
import type { DropdownMenuItem } from '@nuxt/ui'
import OptionsModal from '~/components/optionsModal.vue'

const authStore = useAuthStore()

const optionsModalOpen = ref(false)

const activeTab = ref<'profile' | 'seller' | 'bookings'>('profile')

const openModal = (tab: 'profile' | 'seller' | 'bookings') => {
  activeTab.value = tab
  optionsModalOpen.value = true
}

const dropdownItems = computed<DropdownMenuItem[][]>(() => [
  [
    {
      label: authStore.user?.name || 'User',
      type: 'label',
      avatar: { src: 'https://i.pravatar.cc/100' }
    }
  ],
  [
    {
      label: 'Profile',
      icon: 'i-lucide-user',
      onSelect: () => openModal('profile')
    },
    {
      label: 'Seller info',
      icon: 'i-lucide-badge-dollar-sign',
      onSelect: () => openModal('seller')
    },

    ...(authStore.user?.role === 'ADMIN'
      ? [
          {
            label: 'Admin Dashboard',
            icon: 'i-lucide-shield-check',
            onSelect: () => navigateTo('/admin')
          }
        ]
      : authStore.user?.role === 'SELLER'
        ? [
            {
              label: 'Seller Dashboard',
              icon: 'i-lucide-layout-dashboard',
              onSelect: () => navigateTo('/seller')
            }
          ]
        : []),

    {
      label: 'Booking History',
      icon: 'i-lucide-calendar',
      onSelect: () => openModal('bookings')
    }
  ],
  [
    {
      label: 'Log out',
      icon: 'i-lucide-log-out',
      color: 'error',
      onSelect: async () => {
        await authStore.logout()
        await navigateTo('/')
      }
    }
  ]
])
</script>