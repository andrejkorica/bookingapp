<script setup lang="ts">
import type { TabsItem } from '@nuxt/ui'
import { useAuthStore } from '~/stores/auth'

const authStore = useAuthStore()

const isOpen = defineModel<boolean>('open', {
  default: false
})

const selectedTab = defineModel<string>('tab', {
  default: 'profile'
})

const items: TabsItem[] = [
  { label: 'Profile', value: 'profile', icon: 'i-lucide-user' },
  { label: 'Payment Methods', value: 'payments', icon: 'i-lucide-credit-card' },
  { label: 'Booking History', value: 'bookings', icon: 'i-lucide-calendar' }
]

/**
 * local editable form
 */
const form = reactive({
  name: '',
  surname: '',
  email: ''
})

watch(
  () => authStore.user,
  (user) => {
    if (!user) return
    form.name = user.name
    form.surname = user.surname
    form.email = user.email
  },
  { immediate: true }
)

function saveProfile() {
  if (!authStore.user) return

  authStore.user.name = form.name
  authStore.user.surname = form.surname
  authStore.user.email = form.email

  localStorage.setItem('auth_user', JSON.stringify(authStore.user))
}
</script>

<template>
  <UModal
    v-model:open="isOpen"
    :ui="{
      content: 'max-w-3xl w-full p-4'
    }"
  >
    <template #content>
      <div class="space-y-4">

        <!-- TABS -->
        <UTabs
          v-model="selectedTab"
          :items="items"
          variant="link"
          class="w-full"
          :ui="{
            list: 'flex w-full',
            trigger: 'flex-1 flex justify-center'
          }"
        />

        <!-- CONTENT -->
        <div class="p-2 min-h-[300px]">

          <!-- PROFILE -->
          <div v-if="selectedTab === 'profile'" class="space-y-6">

            <h2 class="text-xl font-semibold">Profile</h2>

            <!-- avatar + info -->
            <div class="flex items-center gap-6">
              <img
                src="https://i.pravatar.cc/150"
                class="w-20 h-20 rounded-full border object-cover"
              />

              <div class="text-sm text-slate-600">
                <p>
                  <span class="font-medium">Role:</span>
                  {{ authStore.user?.role || 'user' }}
                </p>
                <p>
                  <span class="font-medium">Email verified:</span>
                  yes
                </p>
              </div>
            </div>

            <!-- FIXED FORM LAYOUT -->
            <div class="space-y-4 max-w-lg">

              <div class="grid grid-cols-1 gap-3">
                <UInput v-model="form.name" placeholder="First name" />
                <UInput v-model="form.surname" placeholder="Last name" />
                <UInput v-model="form.email" placeholder="Email" />
              </div>

              <div class="flex justify-start pt-2">
                <UButton
                  label="Save changes"
                  class="bg-indigo-600 hover:bg-indigo-700 text-white"
                  @click="saveProfile"
                />
              </div>

            </div>

          </div>

          <!-- PAYMENTS -->
          <div v-else-if="selectedTab === 'payments'">
            <h2 class="text-xl font-semibold">Payment Methods</h2>
            <p class="text-slate-600 mt-2">
              Saved payment methods go here.
            </p>
          </div>

          <!-- BOOKINGS -->
          <div v-else>
            <h2 class="text-xl font-semibold">Booking History</h2>
            <p class="text-slate-600 mt-2">
              Your bookings will appear here.
            </p>
          </div>

        </div>

      </div>
    </template>
  </UModal>
</template>