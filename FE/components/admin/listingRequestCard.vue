<script setup lang="ts">
type ListingRequest = {
  id: number;
  title: string;
  location: string;
  description: string;
  pricePerNight: number;
  rating: number;
  images: string[];
  amenities: string[];
  status: string;
  sellerEmail: string;
  createdAt: string;
};

defineProps<{
  listing: ListingRequest;
}>();

const emit = defineEmits<{
  approve: [id: number];
  reject: [id: number];
}>();
</script>

<template>
  <UCard class="h-[530px]">
    <template #header>
      <div class="flex items-start justify-between gap-3">
        <div>
          <h2 class="line-clamp-2 font-semibold text-slate-900">
            {{ listing.title }}
          </h2>

          <p class="text-sm text-slate-500">
            {{ listing.sellerEmail }}
          </p>
        </div>

        <UBadge
          :label="listing.status"
          :color="
            listing.status === 'APPROVED'
              ? 'success'
              : listing.status === 'REJECTED'
                ? 'error'
                : 'warning'
          "
          variant="soft"
        />
      </div>
    </template>

    <div
      class="flex h-[340px] flex-col gap-4 overflow-y-auto pr-2 text-sm text-slate-600"
    >
      <img
        v-if="listing.images?.length"
        :src="listing.images[0]"
        class="h-40 w-full rounded-xl object-cover"
        alt="Listing image"
      />

      <div
        v-else
        class="flex h-40 items-center justify-center rounded-xl bg-slate-100 text-slate-400"
      >
        No image
      </div>

      <div class="space-y-2">
        <p>
          <span class="font-medium text-slate-800">Location:</span>
          {{ listing.location }}
        </p>
        <p>
          <span class="font-medium text-slate-800">Price:</span> €{{
            listing.pricePerNight
          }}
          / night
        </p>
        <p>
          <span class="font-medium text-slate-800">Rating:</span>
          {{ listing.rating }}/5
        </p>
      </div>

      <p class="line-clamp-4">
        {{ listing.description }}
      </p>

      <p class="text-xs text-slate-400">Created: {{ listing.createdAt }}</p>
    </div>

    <template #footer>
      <div class="flex w-full items-center justify-between">
        <UButton
          label="Preview"
          icon="i-lucide-eye"
          variant="soft"
          color="neutral"
          :to="`/admin/listings/${listing.id}`"
        />

        <div class="flex gap-2">
          <UButton
            label="Approve"
            icon="i-lucide-check"
            :color="listing.status === 'PENDING' ? 'success' : 'neutral'"
            variant="soft"
            :disabled="listing.status !== 'PENDING'"
            :class="listing.status !== 'PENDING' ? 'opacity-50' : ''"
            @click="emit('approve', listing.id)"
          />

          <UButton
            label="Reject"
            icon="i-lucide-x"
            :color="listing.status === 'PENDING' ? 'error' : 'neutral'"
            variant="soft"
            :disabled="listing.status !== 'PENDING'"
            :class="listing.status !== 'PENDING' ? 'opacity-50' : ''"
            @click="emit('reject', listing.id)"
          />
        </div>
      </div>
    </template>
  </UCard>
</template>
