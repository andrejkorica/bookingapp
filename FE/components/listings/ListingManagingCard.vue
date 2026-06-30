<script setup lang="ts">
import type { Listing } from '~/types/ListingTypes';

const props = defineProps<{
  listing: Listing
}>()

const emit = defineEmits<{
  preview: [id: number]
  availability: [id: number]
  edit: [id: number]
  delete: [id: number]
}>()

const isDeleteModalOpen = ref(false)

const cardUi = {
  root: 'flex h-full flex-col',
  body: 'flex-1',
  footer: 'shrink-0'
}

const priceLabel = computed(() => {
  if (!props.listing.lowestPrice && !props.listing.highestPrice) {
    return 'Price not set'
  }

  if (props.listing.lowestPrice === props.listing.highestPrice) {
    return `€${props.listing.lowestPrice} / night`
  }

  return `€${props.listing.lowestPrice} - €${props.listing.highestPrice} / night`
})

function confirmDelete(id: number) {
  emit('delete', id)
  isDeleteModalOpen.value = false
}
</script>

<template>
  <UCard :ui="cardUi">
    <template #header>
      <div>
        <h2 class="font-bold text-slate-900">
          {{ listing.title }}
        </h2>

        <p class="text-sm text-slate-500">
          {{ listing.sellerEmail || 'Your listing' }}
        </p>
      </div>
    </template>

    <div class="space-y-4">
      <div class="h-40 overflow-hidden rounded-xl bg-slate-100">
        <img
          v-if="listing.images?.length"
          :src="listing.images[0]"
          alt="Listing image"
          class="h-full w-full object-cover"
        >

        <div
          v-else
          class="flex h-full items-center justify-center text-slate-400"
        >
          No image
        </div>
      </div>

      <div class="space-y-2 text-sm text-slate-700">
        <p>
          <span class="font-semibold text-slate-900">Location:</span>
          {{ listing.location }}
        </p>

        <p>
          <span class="font-semibold text-slate-900">Price:</span>
          {{ priceLabel }}
        </p>

        <p>
          <span class="font-semibold text-slate-900">Rating:</span>
          {{ listing.rating }}/5
        </p>

        <p class="line-clamp-2 text-slate-600">
          {{ listing.description }}
        </p>
      </div>
    </div>

    <template #footer>
      <div class="grid grid-cols-2 gap-2">
        <UButton
          label="Preview"
          icon="i-lucide-eye"
          variant="soft"
          color="neutral"
          block
          @click="emit('preview', listing.id)"
        />

        <UButton
          label="Availability"
          icon="i-lucide-calendar-days"
          variant="soft"
          color="neutral"
          block
          @click="emit('availability', listing.id)"
        />

        <UButton
          label="Edit"
          icon="i-lucide-pencil"
          class="bg-indigo-600 text-white hover:bg-indigo-700"
          block
          @click="emit('edit', listing.id)"
        />

        <UButton
          label="Delete"
          icon="i-lucide-trash"
          color="error"
          variant="soft"
          block
          @click="isDeleteModalOpen = true"
        />
      </div>
    </template>
  </UCard>

  <UModal
    v-model:open="isDeleteModalOpen"
    title="Delete listing"
  >
    <template #body>
      <p class="text-sm text-slate-600">
        Are you sure you want to delete this listing? This action cannot be undone. You are about to delete
        <span class="font-semibold text-slate-900">
          {{ listing.title }}
        </span>.
      </p>
    </template>

    <template #footer>
      <div class="flex w-full justify-end gap-2">
        <UButton
          label="Cancel"
          variant="soft"
          color="neutral"
          @click="isDeleteModalOpen = false"
        />

        <UButton
          label="Delete"
          icon="i-lucide-trash"
          color="error"
          @click="confirmDelete(listing.id)"
        />
      </div>
    </template>
  </UModal>
</template>