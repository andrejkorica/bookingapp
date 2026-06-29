<script setup lang="ts">
const selectedAmenities = defineModel<string[]>({
  required: true,
});

const config = useRuntimeConfig();

const amenities = ref<string[]>([]);
const showAllAmenities = ref(false);

const visibleAmenities = computed(() => {
  if (showAllAmenities.value) {
    return amenities.value;
  }

  return amenities.value.slice(0, 10);
});

async function fetchAmenities() {
  try {
    amenities.value = await $fetch<string[]>(
      `${config.public.apiBase}/listings/amenities`,
    );
  } catch (error) {
    console.error(error);
    amenities.value = [];
  }
}

function isSelected(amenity: string) {
  return selectedAmenities.value.includes(amenity);
}

function toggleAmenity(amenity: string, checked: boolean | "indeterminate") {
  if (checked === true) {
    if (!selectedAmenities.value.includes(amenity)) {
      selectedAmenities.value.push(amenity);
    }

    return;
  }

  selectedAmenities.value = selectedAmenities.value.filter(
    (item) => item !== amenity,
  );
}

onMounted(fetchAmenities);
</script>

<template>
  <div>
    <h3 class="mb-3 font-semibold text-slate-900">
      Amenities
    </h3>

    <div class="space-y-3">
      <UCheckbox
        v-for="amenity in visibleAmenities"
        :key="amenity"
        :model-value="isSelected(amenity)"
        :label="amenity"
        @update:model-value="toggleAmenity(amenity, $event)"
      />
    </div>

    <UButton
      v-if="amenities.length > 10"
      variant="ghost"
      color="neutral"
      size="sm"
      class="mt-3 px-0"
      :icon="showAllAmenities ? 'i-heroicons-chevron-up' : 'i-heroicons-chevron-down'"
      @click="showAllAmenities = !showAllAmenities"
    >
      {{ showAllAmenities ? "Show less" : `Show all ${amenities.length}` }}
    </UButton>
  </div>
</template>