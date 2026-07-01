<script setup lang="ts">
defineProps<{
  priceLabel: string;
  canBook: boolean;
  bookTo?: string;
  disabledMessage?: string;
  isFavorited: boolean;
  isTogglingFavorite: boolean;
}>();

const emit = defineEmits<{
  toggleFavorite: [];
}>();
</script>

<template>
  <div class="sticky top-6 space-y-4">
    <UCard class="border border-slate-200 bg-white shadow-lg">
      <div class="space-y-4 text-center">
        <p class="text-lg text-slate-500">Price per night</p>

        <p class="text-4xl font-bold">
          {{ priceLabel }}
        </p>

        <UButton
          label="Book now"
          size="xl"
          block
          class="bg-indigo-600 font-bold text-white hover:bg-indigo-700"
          :disabled="!canBook"
          :to="canBook ? bookTo : undefined"
        />

        <p v-if="disabledMessage" class="text-sm text-slate-500">
          {{ disabledMessage }}
        </p>
      </div>
    </UCard>

    <UCard class="border border-slate-200 bg-white shadow-lg">
      <div class="space-y-4 text-center">
        <p class="text-lg font-semibold text-slate-900">
          Save this listing
        </p>

        <UButton
          :label="isFavorited ? 'Added to favorites' : 'Add to favorites'"
          size="lg"
          block
          variant="soft"
          color="neutral"
          :icon="isFavorited ? 'i-heroicons-heart-solid' : 'i-heroicons-heart'"
          :class="isFavorited ? 'text-red-500' : ''"
          :loading="isTogglingFavorite"
          @click="emit('toggleFavorite')"
        />
      </div>
    </UCard>
  </div>
</template>