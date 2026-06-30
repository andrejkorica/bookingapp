<script setup lang="ts">
import BookingSellerCard from "~/components/booking/BookingSellerCard.vue";
import ListingAvailableUnits from "~/components/listings/ListingAvailableUnits.vue";
import type { UserBooking } from "~/types/BookingTypes";
import type { ListingUnit } from "~/types/ListingTypes";

definePageMeta({
  middleware: "seller-guard",
});

const route = useRoute();
const router = useRouter();
const config = useRuntimeConfig();
const toast = useToast();

const bookings = ref<UserBooking[]>([]);
const availableUnits = ref<ListingUnit[]>([]);

const isLoading = ref(false);
const isLoadingAvailableUnits = ref(false);

const availableUnitsOpen = ref(true);

async function fetchBookings() {
  isLoading.value = true;

  try {
    bookings.value = await $fetch<UserBooking[]>(
      `${config.public.apiBase}/bookings/seller/listings/${route.params.id}/bookings`,
      {
        credentials: "include",
      },
    );
  } catch (error) {
    console.error(error);

    toast.add({
      title: "Error",
      description: "Failed to load listing bookings.",
      color: "error",
    });
  } finally {
    isLoading.value = false;
  }
}

async function fetchAvailableUnits() {
  isLoadingAvailableUnits.value = true;

  try {
    availableUnits.value = await $fetch<ListingUnit[]>(
      `${config.public.apiBase}/listings/${route.params.id}/available-units`,
      {
        credentials: "include",
      },
    );
  } catch (error) {
    console.error(error);

    toast.add({
      title: "Error",
      description: "Failed to load available units.",
      color: "error",
    });
  } finally {
    isLoadingAvailableUnits.value = false;
  }
}

async function approveBooking(bookingId: number) {
  try {
    await $fetch(
      `${config.public.apiBase}/bookings/seller/${bookingId}/approve`,
      {
        method: "POST",
        credentials: "include",
      },
    );

    toast.add({
      title: "Booking approved",
      description: "The booking has been approved successfully.",
      color: "success",
    });

    await Promise.all([fetchBookings(), fetchAvailableUnits()]);
  } catch (error) {
    console.error(error);

    toast.add({
      title: "Approval failed",
      description: "Unable to approve the booking.",
      color: "error",
    });
  }
}

async function rejectBooking(bookingId: number) {
  try {
    await $fetch(
      `${config.public.apiBase}/bookings/seller/${bookingId}/reject`,
      {
        method: "POST",
        credentials: "include",
      },
    );

    toast.add({
      title: "Booking rejected",
      description: "The booking has been rejected.",
      color: "success",
    });

    await Promise.all([fetchBookings(), fetchAvailableUnits()]);
  } catch (error) {
    console.error(error);

    toast.add({
      title: "Rejection failed",
      description: "Unable to reject the booking.",
      color: "error",
    });
  }
}

function viewListing(listingId: number) {
  navigateTo(`/listings/${listingId}`);
}

function reviewRequest(bookingId: number) {
  navigateTo(`/bookings/${bookingId}`);
}

onMounted(async () => {
  await Promise.all([fetchBookings(), fetchAvailableUnits()]);
});
</script>

<template>
  <div class="min-h-screen bg-slate-50 text-slate-900">
    <UContainer class="py-10">
      <div class="mb-8 flex items-center justify-between gap-4">
        <div>
          <h1 class="text-3xl font-bold">Listing availability</h1>

          <p class="mt-2 text-slate-600">
            Review available units and bookings connected to this listing.
          </p>
        </div>

        <UButton
          label="Back"
          icon="i-lucide-arrow-left"
          variant="soft"
          color="neutral"
          @click="router.back()"
        />
      </div>

      <UCard class="mb-6">
        <UCollapsible v-model:open="availableUnitsOpen">
          <UButton
            label="Available units"
            color="neutral"
            variant="ghost"
            trailing-icon="i-lucide-chevron-down"
            block
            class="justify-between px-0 py-0 text-xl font-semibold hover:bg-transparent active:bg-transparent focus:bg-transparent focus-visible:ring-0"
            :ui="{
              base: 'hover:bg-transparent active:bg-transparent focus:bg-transparent focus-visible:ring-0',
            }"
          />

          <template #content>
            <div class="pt-4">
              <div
                v-if="isLoadingAvailableUnits"
                class="py-8 text-center text-slate-500"
              >
                Loading available units...
              </div>

              <ListingAvailableUnits v-else :units="availableUnits" title="" />
            </div>
          </template>
        </UCollapsible>
      </UCard>

      <UCard>
        <template #header>
          <div>
            <h2 class="text-xl font-semibold">Current bookings</h2>

            <p class="mt-1 text-sm text-slate-500">
              Pending and confirmed booking requests for this listing.
            </p>
          </div>
        </template>

        <div v-if="isLoading" class="py-12 text-center text-slate-500">
          Loading bookings...
        </div>

        <div
          v-else-if="bookings.length === 0"
          class="rounded-xl border border-dashed border-slate-300 bg-white py-12 text-center text-slate-500"
        >
          No bookings for this listing yet.
        </div>

        <div v-else class="space-y-4">
          <BookingSellerCard
            v-for="booking in bookings"
            :key="booking.id"
            :booking="booking"
            @view-listing="viewListing"
            @review-request="reviewRequest"
            @approve="approveBooking"
            @reject="rejectBooking"
          />
        </div>
      </UCard>
    </UContainer>
  </div>
</template>
