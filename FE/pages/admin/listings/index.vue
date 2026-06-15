<script setup lang="ts">
import listingRequestCard from '~/components/admin/listingRequestCard.vue'

definePageMeta({
  layout: 'admin',
  middleware: 'admin-guard'
})

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
      description: 'Failed to load listings.',
      color: 'error'
    })
  } finally {
    isLoading.value = false
  }
}

async function approveListing(listingId: number) {
  try {
    const updatedListing = await $fetch<ListingRequest>(
      `${config.public.apiBase}/admin/listings/${listingId}/approve`,
      {
        method: 'POST',
        credentials: 'include'
      }
    )

    listings.value = listings.value.map(listing =>
      listing.id === updatedListing.id ? updatedListing : listing
    )

    toast.add({
      title: 'Approved',
      description: 'Listing approved successfully.',
      color: 'success'
    })
  } catch (error) {
    console.error(error)

    toast.add({
      title: 'Error',
      description: 'Failed to approve listing.',
      color: 'error'
    })
  }
}

async function rejectListing(listingId: number) {
  try {
    const updatedListing = await $fetch<ListingRequest>(
      `${config.public.apiBase}/admin/listings/${listingId}/reject`,
      {
        method: 'POST',
        credentials: 'include'
      }
    )

    listings.value = listings.value.map(listing =>
      listing.id === updatedListing.id ? updatedListing : listing
    )

    toast.add({
      title: 'Rejected',
      description: 'Listing rejected.',
      color: 'success'
    })
  } catch (error) {
    console.error(error)

    toast.add({
      title: 'Error',
      description: 'Failed to reject listing.',
      color: 'error'
    })
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
          @approve="approveListing"
          @reject="rejectListing"
        />
      </div>
    </UCard>
  </div>
</template>