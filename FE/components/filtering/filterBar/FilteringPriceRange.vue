<script setup lang="ts">
type PriceRange = {
  min: number | null;
  max: number | null;
};

const priceRange = defineModel<PriceRange>({
  required: true,
});
</script>

<template>
  <div>
    <h3 class="mb-3 font-semibold text-slate-900">
      Price range
    </h3>

    <div class="grid grid-cols-2 gap-2">
      <UInput
        v-model.number="priceRange.min"
        type="number"
        min="0"
        placeholder="Min"
        icon="i-lucide-euro"
      />

      <UInput
        v-model.number="priceRange.max"
        type="number"
        min="0"
        placeholder="Max"
        icon="i-lucide-euro"
      />
    </div>
  </div>
</template>