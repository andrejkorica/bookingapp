<script setup lang="ts">
import type { UserBooking } from '~/types/BookingTypes';

const props = defineProps<{
  booking: UserBooking;
}>();

const emit = defineEmits<{
  viewListing: [listingId: number];
  reviewRequest: [bookingId: number];
  approve: [bookingId: number];
  reject: [bookingId: number];
}>();

const statusColor = computed(() => {
  if (props.booking.status === "CONFIRMED") return "success";
  if (props.booking.status === "PENDING") return "warning";
  return "error";
});
</script>

<template>
  <UCard variant="soft">
    <div class="flex flex-col gap-5 md:flex-row">
      <img
        v-if="booking.listingImage"
        :src="booking.listingImage"
        :alt="booking.listingTitle"
        class="h-44 w-full rounded-xl object-cover md:w-56"
      />

      <div
        v-else
        class="flex h-44 w-full items-center justify-center rounded-xl bg-slate-100 text-slate-400 md:w-56"
      >
        No image
      </div>

      <div class="flex flex-1 flex-col justify-between">
        <div>
          <div class="flex items-start justify-between gap-4">
            <div>
              <h3 class="text-lg font-semibold">
                {{ booking.listingTitle }}
              </h3>

              <p class="text-sm text-slate-500">
                {{ booking.listingLocation }}
              </p>
            </div>

            <UBadge :color="statusColor" variant="soft">
              {{ booking.status }}
            </UBadge>
          </div>

          <div class="mt-4 grid grid-cols-2 gap-4 text-sm md:grid-cols-5">
            <div>
              <p class="text-slate-500">Check-in</p>
              <p class="font-medium">{{ booking.checkIn }}</p>
            </div>

            <div>
              <p class="text-slate-500">Check-out</p>
              <p class="font-medium">{{ booking.checkOut }}</p>
            </div>

            <div>
              <p class="text-slate-500">Nights</p>
              <p class="font-medium">{{ booking.nights }}</p>
            </div>

            <div>
              <p class="text-slate-500">Unit</p>
              <p class="font-medium">
                {{ booking.unitLabel || "Standard" }}
              </p>
            </div>

            <div>
              <p class="text-slate-500">Total</p>
              <p class="font-medium">€{{ booking.totalPrice }}</p>
            </div>
          </div>
        </div>

        <div class="mt-5 flex flex-wrap gap-3">
          <UButton
            label="View listing"
            icon="i-lucide-eye"
            size="sm"
            color="neutral"
            variant="soft"
            @click="emit('viewListing', booking.listingId)"
          />

          <UButton
            label="Review request"
            icon="i-lucide-file-text"
            size="sm"
            class="bg-indigo-600 text-white hover:bg-indigo-700"
            @click="emit('reviewRequest', booking.id)"
          />

          <UButton
            label="Approve"
            icon="i-lucide-check"
            color="success"
            variant="soft"
            size="sm"
            :disabled="booking.status !== 'PENDING'"
            :class="
              booking.status === 'PENDING'
                ? 'hover:bg-green-600 hover:text-white transition-colors'
                : 'bg-slate-200 text-slate-500 cursor-not-allowed'
            "
            @click="emit('approve', booking.id)"
          />

          <UButton
            label="Reject"
            icon="i-lucide-x"
            color="error"
            variant="ghost"
            size="sm"
            :disabled="booking.status !== 'PENDING'"
            :class="
              booking.status === 'PENDING'
                ? 'hover:bg-red-600 hover:text-white transition-colors'
                : 'bg-slate-200 text-slate-500 cursor-not-allowed'
            "
            @click="emit('reject', booking.id)"
          />
          
        </div>
      </div>
    </div>
  </UCard>
</template>
