<script setup lang="ts">
import type { BookingDetails } from "~/types/BookingTypes";

const route = useRoute();
const router = useRouter();
const config = useRuntimeConfig();
const toast = useToast();

const booking = ref<BookingDetails | null>(null);
const isLoading = ref(false);

const statusColor = computed(() => {
  if (booking.value?.status === "CONFIRMED") return "success";
  if (booking.value?.status === "PENDING") return "warning";
  return "error";
});

async function fetchBooking() {
  isLoading.value = true;

  try {
    booking.value = await $fetch<BookingDetails>(
      `${config.public.apiBase}/bookings/${route.params.id}`,
      {
        credentials: "include",
      },
    );
  } catch (error) {
    console.error(error);

    toast.add({
      title: "Error",
      description: "Failed to load booking details.",
      color: "error",
    });
  } finally {
    isLoading.value = false;
  }
}

onMounted(fetchBooking);
</script>

<template>
  <div class="min-h-screen bg-slate-50 text-slate-900">
    <UContainer class="py-10">
      <div class="mb-8 flex items-center justify-between gap-4">
        <div>
          <h1 class="text-3xl font-bold">Booking request</h1>
          <p class="mt-2 text-slate-600">
            Review all details submitted for this booking.
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

      <div v-if="isLoading" class="py-16 text-center text-slate-500">
        Loading booking...
      </div>

      <div v-else-if="!booking" class="py-16 text-center text-slate-500">
        Booking not found.
      </div>

      <div v-else class="grid grid-cols-1 gap-8 lg:grid-cols-3">
        <div class="space-y-6 lg:col-span-2">
          <UCard>
            <div class="flex flex-col gap-5 md:flex-row">
              <img
                v-if="booking.listingImage"
                :src="booking.listingImage"
                :alt="booking.listingTitle"
                class="h-48 w-full rounded-xl object-cover md:w-64"
              />

              <div
                v-else
                class="flex h-48 w-full items-center justify-center rounded-xl bg-slate-100 text-slate-400 md:w-64"
              >
                No image
              </div>

              <div class="flex-1">
                <div class="flex items-start justify-between gap-4">
                  <div>
                    <h2 class="text-2xl font-bold">
                      {{ booking.listingTitle }}
                    </h2>

                    <p class="mt-1 text-slate-500">
                      {{ booking.listingLocation }}
                    </p>
                  </div>

                  <UBadge :color="statusColor" variant="soft">
                    {{ booking.status }}
                  </UBadge>
                </div>

                <div class="mt-5 grid grid-cols-2 gap-4 text-sm">
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
                    <p class="text-slate-500">Created</p>
                    <p class="font-medium">{{ booking.createdAt }}</p>
                  </div>
                </div>
              </div>
            </div>
          </UCard>

          <UCard>
            <template #header>
              <h2 class="text-xl font-semibold">Selected units</h2>
            </template>

            <div class="space-y-3">
              <div
                v-for="unit in booking.units"
                :key="unit.unitType"
                class="flex items-center justify-between rounded-xl border border-slate-200 bg-slate-50 p-4"
              >
                <div>
                  <p class="font-semibold">
                    {{ unit.unitLabel }}
                  </p>

                  <p class="text-sm text-slate-500">
                    {{ unit.unitType }}
                  </p>
                </div>

                <p class="font-bold">× {{ unit.quantity }}</p>
              </div>
            </div>
          </UCard>

          <UCard>
            <template #header>
              <h2 class="text-xl font-semibold">Guest information</h2>
            </template>

            <div class="grid grid-cols-1 gap-4 text-sm md:grid-cols-2">
              <div>
                <p class="text-slate-500">Name</p>
                <p class="font-medium">
                  {{ booking.guestName }} {{ booking.guestSurname }}
                </p>
              </div>

              <div>
                <p class="text-slate-500">Email</p>
                <p class="font-medium">{{ booking.guestEmail }}</p>
              </div>

              <div>
                <p class="text-slate-500">Phone</p>
                <p class="font-medium">{{ booking.guestPhoneNumber }}</p>
              </div>

              <div>
                <p class="text-slate-500">Travel purpose</p>
                <p class="font-medium">
                  {{ booking.travelPurpose || "Not specified" }}
                </p>
              </div>

              <div>
                <p class="text-slate-500">Arrival time</p>
                <p class="font-medium">
                  {{ booking.arrivalTime || "Not specified" }}
                </p>
              </div>

              <div>
                <p class="text-slate-500">Arrival method</p>
                <p class="font-medium">
                  {{ booking.arrivalMethod || "Not specified" }}
                </p>
              </div>
            </div>
          </UCard>

          <UCard>
            <template #header>
              <h2 class="text-xl font-semibold">Additional requests</h2>
            </template>

            <div class="space-y-4 text-sm">
              <div>
                <p class="text-slate-500">Special requests</p>
                <p class="font-medium">
                  {{ booking.specialRequests || "None" }}
                </p>
              </div>

              <div>
                <p class="text-slate-500">Accessibility requirements</p>
                <p class="font-medium">
                  {{ booking.accessibilityRequirements || "None" }}
                </p>
              </div>

              <div class="flex flex-wrap gap-3">
                <UBadge
                  :color="booking.hasPets ? 'success' : 'neutral'"
                  variant="soft"
                >
                  Pets: {{ booking.hasPets ? "Yes" : "No" }}
                </UBadge>

                <UBadge
                  :color="booking.needsParking ? 'success' : 'neutral'"
                  variant="soft"
                >
                  Parking: {{ booking.needsParking ? "Yes" : "No" }}
                </UBadge>
              </div>
            </div>
          </UCard>
        </div>

        <div class="space-y-6">
          <UCard class="sticky top-6">
            <template #header>
              <h2 class="text-xl font-semibold">Payment summary</h2>
            </template>

            <div class="space-y-4 text-sm">
              <div class="flex justify-between">
                <span class="text-slate-500">Average per night</span>
                <span class="font-medium">€{{ booking.pricePerNight }}</span>
              </div>

              <div class="flex justify-between">
                <span class="text-slate-500">Nights</span>
                <span class="font-medium">{{ booking.nights }}</span>
              </div>

              <div class="border-t border-slate-200 pt-4">
                <div class="flex justify-between text-lg font-bold">
                  <span>Total</span>
                  <span>€{{ booking.totalPrice }}</span>
                </div>
              </div>

              <div class="pt-4 text-slate-500">
                <p class="font-medium text-slate-700">Billing address</p>
                <p>{{ booking.billingAddress || "Not provided" }}</p>
              </div>
            </div>
          </UCard>

          <UCard>
            <template #header>
              <h2 class="text-xl font-semibold">Confirmations</h2>
            </template>

            <div class="space-y-3 text-sm">
              <p>
                Rules accepted:
                <strong>{{ booking.agreedToRules ? "Yes" : "No" }}</strong>
              </p>

              <p>
                Cancellation policy accepted:
                <strong>{{
                  booking.agreedToCancellationPolicy ? "Yes" : "No"
                }}</strong>
              </p>

              <p>
                Info confirmed correct:
                <strong>{{
                  booking.confirmedInfoCorrect ? "Yes" : "No"
                }}</strong>
              </p>
            </div>
          </UCard>
        </div>
      </div>
    </UContainer>
  </div>
</template>
