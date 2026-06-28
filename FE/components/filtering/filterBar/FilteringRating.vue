<script setup lang="ts">
const rating = defineModel<number | null>({
  required: true,
});

const ratingOptions = [
  { label: "Any rating", value: null },
  { label: "5 stars", value: 5 },
  { label: "4 stars and up", value: 4 },
  { label: "3 stars and up", value: 3 },
  { label: "2 stars and up", value: 2 },
];
</script>

<template>
  <div>
    <h3 class="mb-3 font-semibold text-slate-900">
      Rating
    </h3>

    <USelect
      v-model="rating"
      :items="ratingOptions"
      placeholder="Any rating"
      class="w-full"
    />
  </div>
</template>