<script setup lang="ts">
import ListingCard from "~/components/listings/ListingCard.vue";
import type { Listing } from "~/types/ListingTypes";

definePageMeta({
  middleware: "user-guard",
});

const config = useRuntimeConfig();

const favorites = ref<Listing[]>([]);
const isLoading = ref(false);

async function fetchFavorites() {
  isLoading.value = true;

  try {
    favorites.value = await $fetch<Listing[]>(
      `${config.public.apiBase}/favorites`,
      {
        credentials: "include",
      },
    );
  } catch (error) {
    console.error(error);
    favorites.value = [];
  } finally {
    isLoading.value = false;
  }
}

onMounted(fetchFavorites);
</script>

<template>
  <div class="min-h-screen bg-slate-50 text-slate-900">
    <UContainer class="py-12">
      <header class="mb-8">
        <h1 class="text-4xl font-bold tracking-tight">Favorites</h1>

        <p class="mt-2 text-slate-600">
          Listings you saved for later.
        </p>
      </header>

      <div v-if="isLoading" class="py-16 text-center text-slate-500">
        Loading favorites...
      </div>

      <div
        v-else-if="favorites.length"
        class="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-2"
      >
        <ListingCard
          v-for="listing in favorites"
          :key="listing.id"
          :listing="listing"
        />
      </div>

      <div
        v-else
        class="rounded-2xl border border-slate-200 bg-white p-10 text-center"
      >
        <UIcon
          name="i-heroicons-heart"
          class="mx-auto mb-4 h-10 w-10 text-slate-300"
        />

        <h2 class="text-xl font-semibold">No favorites yet</h2>

        <p class="mt-2 text-slate-500">
          Save listings you like and they will appear here.
        </p>

        <UButton
          label="Browse listings"
          to="/listings"
          class="mt-6 bg-indigo-600 text-white hover:bg-indigo-700"
        />
      </div>
    </UContainer>
  </div>
</template>