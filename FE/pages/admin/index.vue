<script setup lang="ts">
import { useAuthStore } from '~/stores/auth'

definePageMeta({
  middleware: 'admin-guard'
})

const authStore = useAuthStore()
</script>

<template>
  <div class="min-h-screen bg-slate-50">
    <div class="container mx-auto px-4 py-10">
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-slate-900">
          Admin Dashboard
        </h1>

        <p class="mt-2 text-slate-600">
          Welcome back, {{ authStore.user?.name }}. Manage the platform from here.
        </p>
      </div>

      <div class="grid gap-6 md:grid-cols-3">
        <UCard>
          <template #header>
            <h2 class="font-semibold">
              Seller Requests
            </h2>
          </template>

          <p class="text-sm text-slate-600">
            Review users who requested to become sellers.
          </p>

          <template #footer>
            <UButton
              label="View Requests"
              class="bg-indigo-600 text-white hover:bg-indigo-700"
            />
          </template>
        </UCard>

        <UCard>
          <template #header>
            <h2 class="font-semibold">
              Users
            </h2>
          </template>

          <p class="text-sm text-slate-600">
            Manage registered users.
          </p>

          <template #footer>
            <UButton
              label="View Users"
              variant="soft"
              color="neutral"
            />
          </template>
        </UCard>

        <UCard>
          <template #header>
            <h2 class="font-semibold">
              Listings
            </h2>
          </template>

          <p class="text-sm text-slate-600">
            Review apartment listings.
          </p>

          <template #footer>
            <UButton
              label="View Listings"
              variant="soft"
              color="neutral"
            />
          </template>
        </UCard>
      </div>
    </div>
  </div>
</template>