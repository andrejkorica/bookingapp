<script setup lang="ts">
import FilteringSearchBar from "~/components/filtering/FilteringSearchBar.vue";
import FilteringSidebar from "~/components/filtering/filterBar/FilteringSidebar.vue";
import ListingCard from "~/components/listings/listingCard.vue";
import FilteringSortDropdown from "~/components/filtering/FilteringSortDropdown.vue";


const sort = ref("top");

definePageMeta({
  layout: "default",
});

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
const route = useRoute();

const listings = ref<Listing[]>([]);
const isLoading = ref(false);

async function fetchListings() {
  isLoading.value = true;

  try {
    listings.value = await $fetch<Listing[]>(
      `${config.public.apiBase}/listings`,
      {
        query: {
          location: route.query.location,
          checkIn: route.query.checkIn,
          checkOut: route.query.checkOut,
          adults: route.query.adults,
          children: route.query.children,
          rooms: route.query.rooms,
        },
      },
    );
  } catch (error) {
    console.error(error);
    listings.value = [];
  } finally {
    isLoading.value = false;
  }
}

watch(() => route.query, fetchListings, { immediate: true });
</script>

<template>
  <div class="min-h-screen bg-slate-100 text-slate-900">
    <FilteringSearchBar />

    <main class="mx-auto max-w-7xl px-4 pt-10 sm:px-6 lg:px-8">
      <div class="flex items-start gap-6">
        <aside class="w-[280px] shrink-0">
          <FilteringSidebar />
        </aside>

        <section class="min-w-0 flex-1 pb-12">
          <div class="mb-4 flex items-center justify-between gap-4">
            <div>
              <h1 class="text-2xl font-bold text-slate-900">Search results</h1>

              <p class="mt-1 text-sm text-slate-500">
                {{ listings.length }} properties found
              </p>
            </div>

            <FilteringSortDropdown v-model="sort" />

          </div>

          <div v-if="isLoading" class="py-12 text-center text-slate-500">
            Loading listings...
          </div>

          <div
            v-else-if="listings.length === 0"
            class="rounded-xl border border-slate-200 bg-white py-12 text-center text-slate-500"
          >
            No listings found.
          </div>

          <div v-else class="space-y-5">
            <ListingCard
              v-for="listing in listings"
              :key="listing.id"
              :listing="listing"
            />
          </div>
        </section>
      </div>
    </main>
  </div>
</template>
