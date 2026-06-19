<script setup lang="ts">
import { useAuthStore } from '~/stores/auth'

definePageMeta({
  layout: 'default',
  middleware: 'seller-guard'
})

const authStore = useAuthStore()

const actionCardUi = {
  root: 'flex h-full flex-col',
  body: 'flex-1',
  footer: 'shrink-0'
}
</script>

<template>
  <div class="min-h-screen bg-slate-50">
    <div class="container mx-auto px-4 py-10">
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-slate-900">
          Seller Dashboard
        </h1>

        <p class="mt-2 text-slate-600">
          Welcome back, {{ authStore.user?.name }}. Manage your listings and track your seller activity.
        </p>
      </div>

      <div class="mb-8 grid gap-6 md:grid-cols-3">
        <UCard>
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-slate-500">Total Earnings</p>
              <p class="mt-2 text-3xl font-bold text-slate-900">€0.00</p>
            </div>

            <UIcon name="i-lucide-euro" class="size-8 text-slate-400" />
          </div>
        </UCard>

        <UCard>
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-slate-500">Total Listings</p>
              <p class="mt-2 text-3xl font-bold text-slate-900">0</p>
            </div>

            <UIcon name="i-lucide-building-2" class="size-8 text-slate-400" />
          </div>
        </UCard>

        <UCard>
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-slate-500">Active Bookings</p>
              <p class="mt-2 text-3xl font-bold text-slate-900">0</p>
            </div>

            <UIcon name="i-lucide-calendar-check" class="size-8 text-slate-400" />
          </div>
        </UCard>
      </div>

      <div class="grid items-stretch gap-6 md:grid-cols-2">
        <UCard :ui="actionCardUi">
          <template #header>
            <h2 class="font-semibold">Create Listing</h2>
          </template>

          <p class="text-sm text-slate-600">
            Add a new apartment, room, or property to your seller account.
          </p>

          <template #footer>
            <UButton
              label="Create Listing"
              icon="i-lucide-plus"
              variant="soft"
              color="neutral"
              to="/seller/listings/create"
            />
          </template>
        </UCard>

        <UCard :ui="actionCardUi">
          <template #header>
            <h2 class="font-semibold">Manage Listings</h2>
          </template>

          <p class="text-sm text-slate-600">
            Edit listing details, update pricing, manage availability, and temporarily disable bookings during renovations or maintenance.
          </p>

          <template #footer>
            <UButton
              label="Manage Listings"
              icon="i-lucide-settings"
              variant="soft"
              color="neutral"
              to="/seller/listings/manage"
            />
          </template>
        </UCard>


      </div>
    </div>
  </div>
</template>