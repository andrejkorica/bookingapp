<script setup lang="ts">
import BookingCard from "~/components/booking/BookingCard.vue";
import type { UserBooking } from "~/types/BookingTypes";

definePageMeta({
  middleware: "seller-guard",
});

const config = useRuntimeConfig();
const toast = useToast();

const pendingOpen = ref(true);
const activeOpen = ref(true);
const pastOpen = ref(false);

const bookings = ref<UserBooking[]>([]);
const isLoading = ref(false);

const pendingBookings = computed(() => {
  return bookings.value.filter((booking) => booking.status === "PENDING");
});

const activeBookings = computed(() => {
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  return bookings.value.filter((booking) => {
    const checkOut = new Date(booking.checkOut);
    checkOut.setHours(0, 0, 0, 0);

    return booking.status === "CONFIRMED" && checkOut >= today;
  });
});

const pastBookings = computed(() => {
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  return bookings.value.filter((booking) => {
    const checkOut = new Date(booking.checkOut);
    checkOut.setHours(0, 0, 0, 0);

    return booking.status === "CANCELLED" || checkOut < today;
  });
});

async function fetchSellerBookings() {
  isLoading.value = true;

  try {
    bookings.value = await $fetch<UserBooking[]>(
      `${config.public.apiBase}/bookings/seller/bookings`,
      {
        credentials: "include",
      },
    );
  } catch (error) {
    console.error(error);

    toast.add({
      title: "Error",
      description: "Failed to load seller bookings.",
      color: "error",
    });
  } finally {
    isLoading.value = false;
  }
}

function viewListing(listingId: number) {
  navigateTo(`/listings/${listingId}`);
}

function reviewRequest(bookingId: number) {
  navigateTo(`/bookings/${bookingId}`);
}

onMounted(fetchSellerBookings);
</script>

<template>
  <div class="min-h-screen bg-slate-50 text-slate-900">
    <UContainer class="py-10">
      <div class="mb-8 flex items-center justify-between gap-4">
        <div>
          <h1 class="text-3xl font-bold">Manage bookings</h1>
          <p class="mt-2 text-slate-600">
            Review pending, active, and past bookings for your listings.
          </p>
        </div>

        <UButton
          label="Back"
          icon="i-lucide-arrow-left"
          variant="soft"
          color="neutral"
          class="shrink-0"
          to="/seller"
        />
      </div>

      <div v-if="isLoading" class="py-16 text-center text-slate-500">
        Loading bookings...
      </div>

      <div v-else class="space-y-6">
        <UCard>
          <button
            class="flex w-full items-center justify-between text-left"
            @click="pendingOpen = !pendingOpen"
          >
            <div>
              <h2 class="text-xl font-semibold">Pending bookings</h2>
              <p class="text-sm text-slate-500">
                Booking requests waiting for confirmation.
              </p>
            </div>

            <UIcon
              :name="pendingOpen ? 'i-lucide-chevron-up' : 'i-lucide-chevron-down'"
              class="h-5 w-5 text-slate-500"
            />
          </button>

          <div v-if="pendingOpen" class="mt-6">
            <div v-if="pendingBookings.length" class="space-y-4">
              <BookingCard
                v-for="booking in pendingBookings"
                :key="booking.id"
                :booking="booking"
                @view-listing="viewListing"
                @review-request="reviewRequest"
              />
            </div>

            <div
              v-else
              class="rounded-xl border border-dashed border-slate-300 py-10 text-center"
            >
              <p class="font-medium">No pending bookings</p>
              <p class="mt-1 text-sm text-slate-500">
                New booking requests will appear here.
              </p>
            </div>
          </div>
        </UCard>

        <UCard>
          <button
            class="flex w-full items-center justify-between text-left"
            @click="activeOpen = !activeOpen"
          >
            <div>
              <h2 class="text-xl font-semibold">Active bookings</h2>
              <p class="text-sm text-slate-500">
                Confirmed bookings that are upcoming or currently active.
              </p>
            </div>

            <UIcon
              :name="activeOpen ? 'i-lucide-chevron-up' : 'i-lucide-chevron-down'"
              class="h-5 w-5 text-slate-500"
            />
          </button>

          <div v-if="activeOpen" class="mt-6">
            <div v-if="activeBookings.length" class="space-y-4">
              <BookingCard
                v-for="booking in activeBookings"
                :key="booking.id"
                :booking="booking"
                @view-listing="viewListing"
                @review-request="reviewRequest"
              />
            </div>

            <div
              v-else
              class="rounded-xl border border-dashed border-slate-300 py-10 text-center"
            >
              <p class="font-medium">No active bookings</p>
              <p class="mt-1 text-sm text-slate-500">
                Confirmed bookings will appear here.
              </p>
            </div>
          </div>
        </UCard>

        <UCard>
          <button
            class="flex w-full items-center justify-between text-left"
            @click="pastOpen = !pastOpen"
          >
            <div>
              <h2 class="text-xl font-semibold">Past bookings</h2>
              <p class="text-sm text-slate-500">
                Cancelled or completed bookings.
              </p>
            </div>

            <UIcon
              :name="pastOpen ? 'i-lucide-chevron-up' : 'i-lucide-chevron-down'"
              class="h-5 w-5 text-slate-500"
            />
          </button>

          <div v-if="pastOpen" class="mt-6">
            <div v-if="pastBookings.length" class="space-y-4">
              <BookingCard
                v-for="booking in pastBookings"
                :key="booking.id"
                :booking="booking"
                @view-listing="viewListing"
                @review-request="reviewRequest"
              />
            </div>

            <div
              v-else
              class="rounded-xl border border-dashed border-slate-300 py-10 text-center"
            >
              <p class="font-medium">No past bookings</p>
              <p class="mt-1 text-sm text-slate-500">
                Completed and cancelled bookings will appear here.
              </p>
            </div>
          </div>
        </UCard>
      </div>
    </UContainer>
  </div>
</template>