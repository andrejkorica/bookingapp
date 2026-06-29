<script setup lang="ts">
import FilteringAmenities from "~/components/filtering/filterBar/FilteringAmenities.vue";
import FilteringPriceRange from "~/components/filtering/filterBar/FilteringPriceRange.vue";
import FilteringRating from "~/components/filtering/filterBar/FilteringRating.vue";

const router = useRouter();
const route = useRoute();

const rating = ref<number | null>(null);

const priceRange = ref<{
  min: number | null;
  max: number | null;
}>({
  min: typeof route.query.minPrice === "string" ? Number(route.query.minPrice) : null,
  max: typeof route.query.maxPrice === "string" ? Number(route.query.maxPrice) : null,
});

const selectedAmenities = ref<string[]>(
  Array.isArray(route.query.amenities)
    ? route.query.amenities.filter((item): item is string => typeof item === "string")
    : typeof route.query.amenities === "string"
      ? [route.query.amenities]
      : [],
);

watch(
  selectedAmenities,
  () => {
    router.replace({
      query: {
        ...route.query,
        amenities: selectedAmenities.value.length
          ? selectedAmenities.value
          : undefined,
      },
    });
  },
  { deep: true },
);

watch(
  priceRange,
  () => {
    router.replace({
      query: {
        ...route.query,
        minPrice: priceRange.value.min ?? undefined,
        maxPrice: priceRange.value.max ?? undefined,
      },
    });
  },
  { deep: true },
);
</script>

<template>
  <aside class="mb-5 rounded-xl border border-slate-200 bg-white">
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