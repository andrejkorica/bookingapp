<script setup lang="ts">
import BookingCard from "~/components/booking/BookingCard.vue";
import type { UserBooking } from "~/types/BookingTypes";

const config = useRuntimeConfig();
const toast = useToast();

const activeOpen = ref(true);
const favoritesOpen = ref(true);
const pastOpen = ref(false);

const bookings = ref<UserBooking[]>([]);
const favorites = ref([]);

const activeBookings = computed(() => {
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  return bookings.value.filter((booking) => {
    const checkOut = new Date(booking.checkOut);
    checkOut.setHours(0, 0, 0, 0);

    return (
      booking.status !== "CANCELLED" &&
      checkOut >= today
    );
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

async function fetchBookings() {
  try {
    bookings.value = await $fetch<UserBooking[]>(
      `${config.public.apiBase}/bookings/me`,
      {
        credentials: "include",
      }
    );
  } catch (error) {
    console.error(error);

    toast.add({
      title: "Error",
      description: "Failed to load your bookings.",
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

async function cancelBooking(bookingId: number) {
  try {
    await $fetch(
      `${config.public.apiBase}/bookings/${bookingId}/cancel`,
      {
        method: "POST",
        credentials: "include",
      }
    );

    toast.add({
      title: "Booking cancelled",
      description: "Your booking has been successfully cancelled.",
      color: "success",
    });

    await fetchBookings();
  } catch (error) {
    console.error(error);

    toast.add({
      title: "Cancellation failed",
      description: "Unable to cancel this booking.",
      color: "error",
    });
  }
}

onMounted(fetchBookings);
</script>

<template>
  <div class="min-h-screen bg-slate-50 text-slate-900">
    <UContainer class="py-10">
      <div class="mb-8">
        <h1 class="text-3xl font-bold">My bookings</h1>
        <p class="mt-2 text-slate-600">
          Manage your active bookings, favorite apartments, and past stays.
        </p>
      </div>

      <div class="space-y-6">
        <!-- Active bookings -->
        <UCard>
          <button
            class="flex w-full items-center justify-between text-left"
            @click="activeOpen = !activeOpen"
          >
            <div>
              <h2 class="text-xl font-semibold">Active bookings</h2>
              <p class="text-sm text-slate-500">
                Upcoming and waiting-for-confirmation stays.
              </p>
            </div>

            <UIcon
              :name="
                activeOpen
                  ? 'i-lucide-chevron-up'
                  : 'i-lucide-chevron-down'
              "
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
                @cancel="cancelBooking"
              />
            </div>

            <div
              v-else
              class="rounded-xl border border-dashed border-slate-300 py-10 text-center"
            >
              <p class="font-medium">No active bookings</p>
              <p class="mt-1 text-sm text-slate-500">
                Your upcoming bookings will appear here.
              </p>
            </div>
          </div>
        </UCard>

        <!-- Favorites -->
        <UCard>
          <button
            class="flex w-full items-center justify-between text-left"
            @click="favoritesOpen = !favoritesOpen"
          >
            <div>
              <h2 class="text-xl font-semibold">Favorites</h2>
              <p class="text-sm text-slate-500">
                Apartments you saved for later.
              </p>
            </div>

            <UIcon
              :name="
                favoritesOpen
                  ? 'i-lucide-chevron-up'
                  : 'i-lucide-chevron-down'
              "
              class="h-5 w-5 text-slate-500"
            />
          </button>

          <div v-if="favoritesOpen" class="mt-6">
            <div
              v-if="favorites.length"
              class="grid grid-cols-1 gap-4 md:grid-cols-2"
            >
              <!-- favorite cards later -->
            </div>

            <div
              v-else
              class="rounded-xl border border-dashed border-slate-300 py-10 text-center"
            >
              <p class="font-medium">No favorites yet</p>
              <p class="mt-1 text-sm text-slate-500">
                Saved apartments will appear here.
              </p>
            </div>
          </div>
        </UCard>

        <!-- Past bookings -->
        <UCard>
          <button
            class="flex w-full items-center justify-between text-left"
            @click="pastOpen = !pastOpen"
          >
            <div>
              <h2 class="text-xl font-semibold">Past bookings</h2>
              <p class="text-sm text-slate-500">
                Your previous completed stays.
              </p>
            </div>

            <UIcon
              :name="
                pastOpen
                  ? 'i-lucide-chevron-up'
                  : 'i-lucide-chevron-down'
              "
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
                @cancel="cancelBooking"
              />
            </div>

            <div
              v-else
              class="rounded-xl border border-dashed border-slate-300 py-10 text-center"
            >
              <p class="font-medium">No past bookings</p>
              <p class="mt-1 text-sm text-slate-500">
                Completed bookings will appear here.
              </p>
            </div>
          </div>
        </UCard>
      </div>
    </UContainer>
  </div>
</template>