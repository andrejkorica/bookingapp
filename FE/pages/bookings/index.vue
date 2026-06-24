<script setup lang="ts">
definePageMeta({
  layout: "default",
});

type BookingStatus = "PENDING" | "CONFIRMED" | "CANCELLED";

type UserBooking = {
  id: number;
  title: string;
  location: string;
  image: string;
  checkIn: string;
  checkOut: string;
  nights: number;
  totalPrice: number;
  status: BookingStatus;
};

const activeOpen = ref(true);
const favoritesOpen = ref(true);
const pastOpen = ref(false);

const activeBookings = ref<UserBooking[]>([]);
const favorites = ref([]);
const pastBookings = ref<UserBooking[]>([]);

function statusColor(status: BookingStatus) {
  if (status === "CONFIRMED") return "success";
  if (status === "PENDING") return "warning";
  return "error";
}
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
              :name="activeOpen ? 'i-lucide-chevron-up' : 'i-lucide-chevron-down'"
              class="h-5 w-5 text-slate-500"
            />
          </button>

          <div v-if="activeOpen" class="mt-6">
            <div v-if="activeBookings.length" class="space-y-4">
              <UCard
                v-for="booking in activeBookings"
                :key="booking.id"
                variant="soft"
              >
                <div class="flex flex-col gap-5 md:flex-row">
                  <img
                    :src="booking.image"
                    :alt="booking.title"
                    class="h-44 w-full rounded-xl object-cover md:w-56"
                  />

                  <div class="flex flex-1 flex-col justify-between">
                    <div>
                      <div class="flex justify-between gap-4">
                        <div>
                          <h3 class="text-lg font-semibold">
                            {{ booking.title }}
                          </h3>
                          <p class="text-sm text-slate-500">
                            {{ booking.location }}
                          </p>
                        </div>

                        <UBadge :color="statusColor(booking.status)" variant="soft">
                          {{ booking.status }}
                        </UBadge>
                      </div>

                      <div class="mt-4 grid grid-cols-2 gap-4 text-sm md:grid-cols-4">
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
                          <p class="text-slate-500">Total</p>
                          <p class="font-medium">€{{ booking.totalPrice }}</p>
                        </div>
                      </div>
                    </div>

                    <div class="mt-5 flex flex-wrap gap-3">
                      <UButton size="sm">View details</UButton>
                      <UButton size="sm" color="neutral" variant="soft">
                        Contact host
                      </UButton>
                      <UButton size="sm" color="error" variant="ghost">
                        Cancel
                      </UButton>
                    </div>
                  </div>
                </div>
              </UCard>
            </div>

            <div v-else class="rounded-xl border border-dashed border-slate-300 py-10 text-center">
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
              :name="favoritesOpen ? 'i-lucide-chevron-up' : 'i-lucide-chevron-down'"
              class="h-5 w-5 text-slate-500"
            />
          </button>

          <div v-if="favoritesOpen" class="mt-6">
            <div v-if="favorites.length" class="grid grid-cols-1 gap-4 md:grid-cols-2">
              <!-- favorite cards later -->
            </div>

            <div v-else class="rounded-xl border border-dashed border-slate-300 py-10 text-center">
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
              :name="pastOpen ? 'i-lucide-chevron-up' : 'i-lucide-chevron-down'"
              class="h-5 w-5 text-slate-500"
            />
          </button>

          <div v-if="pastOpen" class="mt-6">
            <div v-if="pastBookings.length" class="space-y-4">
              <!-- past booking cards later -->
            </div>

            <div v-else class="rounded-xl border border-dashed border-slate-300 py-10 text-center">
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