<script setup lang="ts">
type ListingImagePreview = {
  previewUrl: string;
};

defineProps<{
  images: ListingImagePreview[];
}>();

const open = defineModel<boolean>("open", { default: false });
</script>

<template>
  <UModal v-model:open="open" :ui="{ content: 'max-w-5xl bg-white p-0' }">
    <template #content>
      <div class="p-16">
        <UCarousel
          v-slot="{ item }"
          :items="images"
          arrows
          dots
          :prev="{
            color: 'primary',
            variant: 'solid',
            class: 'bg-white border-white text-slate-900 shadow-lg',
          }"
          :next="{
            color: 'primary',
            variant: 'solid',
            class: 'bg-white border-white text-slate-900 shadow-lg',
          }"
          :ui="{ item: 'h-[75vh] w-full object-cover', dot: 'bg-primary' }">
          <img
            :src="item.previewUrl"
            class="h-full w-full object-cover""
            loading="lazy" />
        </UCarousel>
      </div>
    </template>
  </UModal>
</template>
