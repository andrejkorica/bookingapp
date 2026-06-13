<template>
  <header class="border-b border-slate-200 bg-white">
    <nav
      class="container mx-auto px-4 sm:px-6 lg:px-8 flex justify-between items-center h-16"
    >
      <NuxtLink
        to="/admin"
        class="text-2xl font-bold text-indigo-600 hover:text-indigo-700"
      >
        Admin Panel
      </NuxtLink>


        <UButton
          label="Sign out"
          icon="i-lucide-log-out"
          color="error"
          variant="soft"
          size="sm"
          @click="logout"
        />
    
    </nav>
  </header>
</template>

<script setup lang="ts">
import { useAuthStore } from '~/stores/auth'

const authStore = useAuthStore()

async function logout() {
  await authStore.logout()
  await navigateTo('/')
}
</script>