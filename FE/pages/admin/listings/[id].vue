<script setup lang="ts">
definePageMeta({
  layout: 'admin',
  middleware: 'admin-guard'
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

const route = useRoute()
const config = useRuntimeConfig()
const toast = useToast()

const listing = ref<Listing | null>(null)
const isLoading = ref(false)

async function fetchListing() {
  isLoading.value = true

  try {
    listing.value = await $fetch<Listing>(
      `${config.public.apiBase}/listings/${route.params.id}`,
      {
        credentials: 'include'
      }
    )
  } catch (error) {
    console.error(error)

    toast.add({
      title: 'Error',
      description: 'Failed to load listing.',
      color: 'error'
    })
  } finally {
    isLoading.value = false
  }
}

onMounted(fetchListing)
</script>

<template>
  <div class="container mx-auto px-4 py-10">
    <div class="mb-8 flex items-center justify-between gap-4">
      <div>
        <h1 class="text-3xl font-bold text-slate-900">
          Listing Preview
        </h1>

        <p class="mt-2 text-slate-600">
          Review the full listing before approving or rejecting it.
        </p>
      </div>

      <UButton
        label="Back to Dashboard"
        icon="i-lucide-arrow-left"
        variant="soft"
        color="neutral"
        to="/admin/listings"
      />
    </div>

    <div v-if="isLoading" class="py-10 text-center text-slate-500">
      Loading listing...
    </div>

    <div v-else-if="!listing" class="py-10 text-center text-slate-500">
      Listing not found.
    </div>

    <UCard v-else class="bg-white shadow-lg">
      <header class="mb-8">
        <div class="mb-3 flex items-start justify-between gap-4">
          <div>
            <h2 class="text-4xl font-bold tracking-tight text-slate-900">
              {{ listing.title }}
            </h2>

            <p class="mt-2 text-sm text-slate-500">
              Submitted by {{ listing.sellerEmail }}
            </p>
          </div>

          <UBadge
            :label="listing.status"
            :color="listing.status === 'APPROVED' ? 'success' : listing.status === 'REJECTED' ? 'error' : 'warning'"
            variant="soft"
          />
        </div>

        <div class="flex items-center space-x-4 text-slate-500">
          <div class="flex items-center">
            <UIcon
              v-for="i in 5"
              :key="i"
              name="i-heroicons-star-solid"
              :class="i <= listing.rating ? 'text-yellow-400' : 'text-slate-200'"
              class="h-5 w-5"
            />
          </div>

          <div class="flex items-center">
            <UIcon
              name="i-heroicons-map-pin"
              class="mr-2 h-5 w-5"
            />
            <span>{{ listing.location }}</span>
          </div>
        </div>
      </header>

      <div class="mb-12">
        <UCarousel
          v-if="listing.images?.length"
          v-slot="{ item }"
          :items="listing.images"
          :ui="{ item: 'basis-full' }"
          :prev="{ color: 'primary' }"
          :next="{ color: 'primary' }"
          class="overflow-hidden rounded-2xl shadow-lg"
          arrows
          indicators
        >
          <img
            :src="item"
            class="h-96 w-full object-cover"
            draggable="false"
          >
        </UCarousel>

        <div
          v-else
          class="flex h-96 items-center justify-center rounded-2xl bg-slate-100 text-slate-400"
        >
          No images uploaded
        </div>
      </div>

      <div class="grid grid-cols-1 gap-12 lg:grid-cols-3">
        <div class="lg:col-span-2">
          <h2 class="mb-4 border-b border-slate-200 pb-2 text-2xl font-bold">
            About the Property
          </h2>

          <p class="mb-8 leading-relaxed text-slate-600">
            {{ listing.description }}
          </p>

          <h3 class="mb-4 text-xl font-bold">
            Amenities
          </h3>

          <ul class="grid grid-cols-1 gap-x-8 gap-y-2 sm:grid-cols-2">
            <li
              v-for="amenity in listing.amenities"
              :key="amenity"
              class="flex items-center"
            >
              <UIcon
                name="i-heroicons-check-circle"
                class="mr-3 h-5 w-5 text-indigo-500"
              />

              <span class="text-slate-600">
                {{ amenity }}
              </span>
            </li>
          </ul>
        </div>

        <div>
          <UCard class="border border-slate-200 bg-white shadow-lg">
            <div class="space-y-4 text-center">
              <p class="text-lg text-slate-500">
                Price per night
              </p>

              <p class="text-4xl font-bold text-slate-900">
                €{{ listing.pricePerNight }}
              </p>

              <p class="text-sm text-slate-500">
                Preview only. Guests cannot book pending listings.
              </p>
            </div>
          </UCard>
        </div>
      </div>
    </UCard>
  </div>
</template>