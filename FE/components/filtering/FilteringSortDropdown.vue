<script setup lang="ts">
const sort = defineModel<string>({
  default: "top",
});


const items = computed(() => [
  {
    label: "Our top picks",
    value: "top",
    onSelect: () => {
      sort.value = "top";
    },
  },
  {
    label: "Price highest first",
    value: "price_desc",
    onSelect: () => {
      sort.value = "price_desc";
    },
  },
  {
    label: "Price lowest first",
    value: "price_asc",
    onSelect: () => {
      sort.value = "price_asc";
    },
  },
  {
    label: "Rating high to low",
    value: "rating_desc",
    onSelect: () => {
      sort.value = "rating_desc";
    },
  },
  {
    label: "Rating low to high",
    value: "rating_asc",
    onSelect: () => {
      sort.value = "rating_asc";
    },
  },
]);

const selectedLabel = computed(() => {
  return items.value.find((item) => item.value === sort.value)?.label ?? "Our top picks";
});
</script>

<template>
  <UDropdownMenu :items="items">
    <UButton
      :label="`Sort by: ${selectedLabel}`"
      icon="i-lucide-arrow-up-down"
      color="neutral"
      variant="soft"
    />
  </UDropdownMenu>
</template>