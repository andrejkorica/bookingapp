<template>
  <div class="min-h-screen bg-slate-100 text-slate-900">
    <div class="bg-gradient-to-br from-indigo-600 via-indigo-500 to-blue-500 pb-24 pt-12 md:pt-16">
      <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <div class="max-w-4xl mx-auto text-center">
          <h1 class="text-4xl md:text-5xl font-extrabold text-white">
            Find your next stay
          </h1>

          <p class="text-lg text-indigo-100 mt-4">
            Search deals on hotels, homes, and more...
          </p>
        </div>
      </div>
    </div>

    <div class="container mx-auto px-4 sm:px-6 lg:px-8 -mt-14">
      <form @submit.prevent="handleSearch" class="max-w-5xl mx-auto">
        <div class="bg-white shadow-2xl rounded-2xl p-4 border border-slate-100">
          <div class="grid grid-cols-1 md:grid-cols-10 gap-3 items-center">
            <div class="md:col-span-3">
              <UInput
                v-model="searchQuery.destination"
                icon="i-heroicons-map-pin"
                size="xl"
                placeholder="Where are you going?"
                :ui="{ leadingIcon: 'text-indigo-400' }"
              />
            </div>

            <div class="md:col-span-3">
              <UInput
                v-model="searchQuery.dates"
                icon="i-heroicons-calendar-days"
                size="xl"
                placeholder="Check-in — Check-out"
                :ui="{ leadingIcon: 'text-indigo-400' }"
              />
            </div>

            <div class="md:col-span-3">
              <UInput
                v-model="searchQuery.occupancy"
                icon="i-heroicons-user-group"
                size="xl"
                placeholder="2 adults · 0 children"
                :ui="{ leadingIcon: 'text-indigo-400' }"
              />
            </div>

            <div class="md:col-span-1">
              <UButton
                type="submit"
                label="Search"
                size="xl"
                block
                class="bg-indigo-600 hover:bg-indigo-700 text-white font-bold"
              />
            </div>
          </div>
        </div>
      </form>
    </div>

    <main class="container mx-auto px-4 sm:px-6 lg:px-8 pt-10">
      <section class="pb-12">
        <div v-if="isLoading" class="py-12 text-center text-slate-500">
          Loading listings...
        </div>

        <div v-else-if="listings.length === 0" class="py-12 text-center text-slate-500">
          No listings found.
        </div>

        <div v-else class="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <ListingCard
            v-for="listing in listings"
            :key="listing.id"
            :listing="listing"
          />
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import ListingCard from '~/components/listingCard.vue'

definePageMeta({
  layout: 'default'
})

type Listing = {
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

const searchQuery = reactive({
  destination: '',
  dates: '',
  occupancy: ''
})

const listings = ref<Listing[]>([])
const isLoading = ref(false)

async function fetchListings() {
  isLoading.value = true

  try {
    listings.value = await $fetch<Listing[]>(
      `${config.public.apiBase}/listings`
    )
  } catch (error) {
    console.error(error)
  } finally {
    isLoading.value = false
  }
}

function handleSearch() {
  console.log('Searching with:', searchQuery)
}

onMounted(fetchListings)
</script>