<template>
  <div class="min-h-screen bg-slate-100 text-slate-900">
    <navbar/>

    <!-- Hero banner with gradient -->
    <div class="bg-gradient-to-br from-indigo-600 via-indigo-500 to-blue-500 pb-24 pt-12 md:pt-16">
      <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <div class="max-w-4xl mx-auto text-center">
          <h1 class="text-4xl md:text-5xl font-extrabold text-white">
            Find your next stay
          </h1>
          <p class="text-lg text-indigo-100 mt-4">
            Search deals on hotels, homes, and much more...
          </p>
        </div>
      </div>
    </div>

    <!-- Search bar overlapping the hero -->
    <div class="container mx-auto px-4 sm:px-6 lg:px-8 -mt-14">
      <form @submit.prevent="handleSearch" class="max-w-5xl mx-auto">
        <div
          class="bg-white shadow-2xl rounded-2xl p-4 border border-slate-100"
        >
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

    <!-- Listings -->
    <main class="container mx-auto px-4 sm:px-6 lg:px-8 pt-10">
      <section class="pb-12">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
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
import { reactive } from 'vue'
import { useAuthStore } from '~/stores/auth'
import ListingCard from '~/components/listingCard.vue'; 

const authStore = useAuthStore()

const searchQuery = reactive({
  destination: '',
  dates: '',
  occupancy: ''
})

function handleSearch() {
  console.log('Searching with:', searchQuery)

}




const listings = [
  {
    id: 123,
    title: "Prostrani stan starog kova na Maksimiru",
    location: "Maksimir, Zagreb",
    distance: "2.1 km od centra",
    details: "Cijeli apartman · 1 spavaća soba · 1 kupaonica",
    rating: 7.8,
    ratingLabel: "Dobro",
    reviews: 5,
    oldPrice: 129,
    price: 119,
    images: [
      "https://picsum.photos/id/10/800/600",
      "https://picsum.photos/id/11/800/600",
      "https://picsum.photos/id/12/800/600",
    ]
  },
  {
    id: 456,
    title: "Moderan studio apartman u centru grada",
    location: "Donji grad, Zagreb",
    distance: "300 m od centra",
    details: "Studio apartman · 1 kupaonica · Čajna kuhinja",
    rating: 9.2,
    ratingLabel: "Izvanredno",
    reviews: 88,
    oldPrice: null,
    price: 95,
    images: [
      "https://picsum.photos/id/20/800/600",
      "https://picsum.photos/id/21/800/600",
      "https://picsum.photos/id/22/800/600",
    ]
  },
  {
    id: 789,
    title: "Obiteljska kuća s vrtom i roštiljem",
    location: "Trešnjevka, Zagreb",
    distance: "4.5 km od centra",
    details: "Cijela kuća · 3 spavaće sobe · 2 kupaonice",
    rating: 8.9,
    ratingLabel: "Sjajno",
    reviews: 42,
    oldPrice: 250,
    price: 220,
    images: [
      "https://picsum.photos/id/30/800/600",
      "https://picsum.photos/id/31/800/600",
      "https://picsum.photos/id/32/800/600",
    ]
  }
];

</script>