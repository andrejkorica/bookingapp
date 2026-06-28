<script setup lang="ts">
import FilteringAmenities from "~/components/filtering/filterBar/FilteringAmenities.vue";
import FilteringPriceRange from "~/components/filtering/filterBar/FilteringPriceRange.vue";
import FilteringRating from "~/components/filtering/filterBar/FilteringRating.vue";

const rating = ref<number | null>(null);

const priceRange = ref({
  min: null,
  max: null,
});

const selectedAmenities = ref<string[]>([]);
</script>

<template>
  <aside class="rounded-xl border border-slate-200 bg-white mb-5">
    <div class="border-b border-slate-200 p-4">
      <h2 class="font-bold text-slate-900">Filter by:</h2>
    </div>

    <div class="space-y-5 p-4">
      <div class="border-slate-200">
        <FilteringAmenities v-model="selectedAmenities" />
      </div>

      <div class="border-t border-slate-200 pt-4">
        <FilteringPriceRange v-model="priceRange" />
      </div>

      <div class="border-t border-slate-200 pt-4">
        <FilteringRating v-model="rating" />
      </div>
    </div>
  </aside>
</template>
