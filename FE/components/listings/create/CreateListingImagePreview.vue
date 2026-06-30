<script setup lang="ts">
import CreateListingImageGalleryModal from "./CreateListingImageGalleryModal.vue";
import type { ListingImage } from "~/types/ListingTypes.js";

const props = defineProps<{
  images: ListingImage[];
}>();

const isGalleryOpen = ref(false);

const coverImage = computed(() => props.images[0] ?? null);
const sideImages = computed(() => props.images.slice(1, 5));
const emptySlots = computed(() => Math.max(0, 5 - props.images.length));
</script>

<template>
  <div class="mb-12">
    <div
      v-if="coverImage"
      class="relative grid h-[440px] grid-cols-4 grid-rows-2 gap-2 overflow-hidden rounded-2xl">
      <div class="col-span-2 row-span-2">
        <img :src="coverImage.previewUrl" class="h-full w-full object-cover" />
      </div>

      <div v-for="image in sideImages" :key="image.previewUrl">
        <img :src="image.previewUrl" class="h-full w-full object-cover" />
      </div>

      <div
        v-for="index in emptySlots"
        :key="`empty-${index}`"
        class="flex items-center justify-center bg-slate-100 text-slate-400">
        <UIcon name="i-lucide-image" class="size-8" />
      </div>

      <UButton
        label="Show all photos"
        icon="i-lucide-grid-3x3"
        color="neutral"
        variant="solid"
        class="absolute bottom-4 right-4 z-20 border border-slate-300 bg-white text-slate-900 shadow-lg hover:border-indigo-600 hover:bg-indigo-600 hover:text-white"
        @click="isGalleryOpen = true" />
    </div>

    <UCard
      v-else
      class="flex h-72 items-center justify-center rounded-2xl border border-dashed border-slate-300 bg-slate-50">
      <div class="text-center text-slate-500">
        <UIcon name="i-lucide-image-plus" class="mx-auto mb-3 size-10" />
        <p class="font-medium">Upload photos to preview your listing.</p>
      </div>
    </UCard>

    <CreateListingImageGalleryModal
      v-model:open="isGalleryOpen"
      :images="images" />
  </div>
</template>
