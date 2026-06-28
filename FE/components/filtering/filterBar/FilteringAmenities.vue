<script setup lang="ts">
const selectedAmenities = defineModel<string[]>({
  required: true,
});

const amenities = [
  "Wi-Fi",
  "Air conditioning",
  "Free parking",
  "Swimming pool",
  "Sea view",
  "Pets allowed",
  "Kitchen",
  "Washing machine",
  "Dishwasher",
  "Terrace",
  "Balcony",
  "BBQ grill",
];

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
</script>

<template>
  <div>
    <h3 class="mb-3 font-semibold text-slate-900">
      Amenities
    </h3>

    <div class="space-y-3">
      <UCheckbox
        v-for="amenity in amenities"
        :key="amenity"
        :model-value="isSelected(amenity)"
        :label="amenity"
        @update:model-value="toggleAmenity(amenity, $event)"
      />
    </div>
  </div>
</template>