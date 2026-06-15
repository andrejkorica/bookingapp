<script setup lang="ts">

definePageMeta({
  layout: 'admin',
  middleware: 'admin-guard'
})

import listingRequestCard from '~/components/admin/listingRequestCard.vue'

type ListingRequest = {
  id: number
  title: string
  location: string
  description: string
  pricePerNight: number
  rating: number
  images: string[]
  amenities: string[]
  status: string
  sellerEmail: string
  createdAt: string
}

const config = useRuntimeConfig()
const toast = useToast()

const listings = ref<ListingRequest[]>([])
const isLoading = ref(false)

async function fetchListings() {
  isLoading.value = true

  try {
    listings.value = await $fetch<ListingRequest[]>(
      `${config.public.apiBase}/admin/listings`,
      {
        credentials: 'include'
      }
    )
  } catch (error) {
    console.error(error)

    toast.add({
      title: 'Error',
      description: 'Failed to load listing requests.',
      color: 'error'
    })
  } finally {
    isLoading.value = false
  }
}

onMounted(fetchListings)
</script>

<template>
  <div class="container mx-auto px-4 py-10">
    <div class="mb-8 flex items-center justify-between gap-4">
      <div>
        <h1 class="text-3xl font-bold text-slate-900">
          Listing Requests
        </h1>

        <p class="mt-2 text-slate-600">
          Review and approve property listings submitted by sellers.
        </p>
      </div>

      <UButton
        label="Back to Dashboard"
        icon="i-lucide-arrow-left"
        variant="soft"
        color="neutral"
        to="/admin"
      />
    </div>

    <UCard>
      <div v-if="isLoading" class="py-10 text-center text-slate-500">
        Loading listing requests...
      </div>

      <div v-else-if="listings.length === 0" class="py-10 text-center text-slate-500">
        No listing requests found.
      </div>

      <div
        v-else
        class="grid grid-cols-1 gap-5 md:grid-cols-2 xl:grid-cols-3"
      >
        <listingRequestCard
          v-for="listing in listings"
          :key="listing.id"
          :listing="listing"
        />
      </div>
    </UCard>
  </div>
</template>