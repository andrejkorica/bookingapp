<script setup lang="ts">
import ListingCard from "~/components/listings/ListingCard.vue";
import FilteringSearchBar from "~/components/filtering/FilteringSearchBar.vue";
import type { Listing } from "~/types/ListingTypes";

definePageMeta({
  layout: "default",
});

const config = useRuntimeConfig();

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
    listings.value = [];
  } finally {
    isLoading.value = false;
  }
}

onMounted(fetchListings);
</script>

<template>
  <div class="min-h-screen bg-slate-100 text-slate-900">
    <FilteringSearchBar />

    <main class="container mx-auto px-4 pt-10 sm:px-6 lg:px-8">
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

        <div v-else class="grid grid-cols-1 gap-8 lg:grid-cols-2">
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