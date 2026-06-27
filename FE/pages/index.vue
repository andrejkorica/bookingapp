

<script setup lang="ts">
definePageMeta({
  layout: "default",
});

import listingCard from "../components/listings/listingCard.vue";
import FilteringSearchBar from "~/components/filtering/FilteringSearchBar.vue";

type Listing = {
  id: number;
  title: string;
  location: string;
  description: string;
  lowestPrice: number;
  highestPrice: number;
  rating: number;
  images: string[];
  amenities: string[];
  status: string;
  sellerEmail: string;
  createdAt: string;
};

const config = useRuntimeConfig();

const searchQuery = reactive({
  destination: "",
  dates: "",
  occupancy: "",
});

const listings = ref<Listing[]>([]);
const isLoading = ref(false);

async function fetchListings() {
  isLoading.value = true;

  try {
    listings.value = await $fetch<Listing[]>(
      `${config.public.apiBase}/listings`,
    );
  } catch (error) {
    console.error(error);
  } finally {
    isLoading.value = false;
  }
}

function handleSearch() {
  console.log("Searching with:", searchQuery);
}

onMounted(fetchListings);
</script>

<template>
  <div class="min-h-screen bg-slate-100 text-slate-900">
    <FilteringSearchBar
      @search="handleSearch"
    />

    <main class="container mx-auto px-4 sm:px-6 lg:px-8 pt-10">
      <section class="pb-12">
        <div v-if="isLoading" class="py-12 text-center text-slate-500">
          Loading listings...
        </div>

        <div
          v-else-if="listings.length === 0"
          class="py-12 text-center text-slate-500"
        >
          No listings found.
        </div>

        <div v-else class="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <listingCard
            v-for="listing in listings"
            :key="listing.id"
            :listing="listing"
          />
        </div>
      </section>
    </main>
  </div>
</template>
