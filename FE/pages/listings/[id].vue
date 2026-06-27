<template>
  <div class="min-h-screen bg-white text-slate-900">
    <UContainer class="py-12">
      <div v-if="isLoading">
        <p>Loading listing data...</p>
      </div>

      <div v-else-if="listingData">
        <header class="mb-8">
          <h1
            class="mb-2 text-4xl font-bold tracking-tight text-slate-900 md:text-5xl"
          >
            {{ listingData.title }}
          </h1>

          <div class="flex items-center justify-between gap-4">
            <div class="flex min-w-0 items-center space-x-4 text-slate-500">
              <div class="flex shrink-0 items-center">
                <UIcon
                  v-for="i in 5"
                  :key="i"
                  name="i-heroicons-star-solid"
                  :class="
                    i <= listingData.rating
                      ? 'text-yellow-400'
                      : 'text-slate-200'
                  "
                  class="h-5 w-5"
                />
              </div>

              <div class="flex min-w-0 items-center">
                <UIcon
                  name="i-heroicons-map-pin"
                  class="mr-2 h-5 w-5 shrink-0"
                />
                <span class="truncate">{{ listingData.location }}</span>
              </div>
            </div>

            <UButton
              label="Back"
              icon="i-lucide-arrow-left"
              variant="soft"
              color="neutral"
              class="shrink-0"
              @click="router.back()"
            />
          </div>
        </header>

        <div class="mb-12">
          <CreateListingImagePreview :images="previewImages" />
        </div>

        <div class="grid grid-cols-1 gap-12 lg:grid-cols-3">
          <div class="lg:col-span-2">
            <h2 class="mb-4 border-b border-slate-200 pb-2 text-2xl font-bold">
              About the Property
            </h2>

            <p class="mb-8 leading-relaxed text-slate-600">
              {{ listingData.description }}
            </p>

            <ListingLocationMap
              :location="listingData.location"
              :latitude="listingData.latitude"
              :longitude="listingData.longitude"
              class="mb-6"
            />

            <h3 class="mb-4 text-xl font-bold">Amenities</h3>

            <ul
              v-if="listingData.amenities?.length"
              class="grid grid-cols-2 gap-x-8 gap-y-2"
            >
              <li
                v-for="amenity in listingData.amenities"
                :key="amenity"
                class="flex items-center"
              >
                <UIcon
                  name="i-heroicons-check-circle"
                  class="mr-3 h-5 w-5 text-indigo-500"
                />
                <span class="text-slate-600">{{ amenity }}</span>
              </li>
            </ul>

            <p v-else class="text-slate-500">No amenities listed.</p>

            <div
              v-if="isLoadingAvailableUnits"
              class="mt-10 rounded-xl border border-slate-200 bg-slate-50 p-4 text-sm text-slate-500"
            >
              Loading available units...
            </div>

            <ListingAvailableUnits
              v-else
              :units="availableUnits"
              title="Available units"
              class="mt-10"
            />
          </div>

          <div>
            <UCard class="border border-slate-200 bg-white shadow-lg">
              <div class="space-y-4 text-center">
                <p class="text-lg text-slate-500">Price per night</p>

                <p class="text-4xl font-bold text-slate-900">
                  {{ priceLabel }}
                </p>

                <UButton
                  label="Book now"
                  size="xl"
                  block
                  class="bg-indigo-600 font-bold text-white hover:bg-indigo-700"
                  :disabled="
                    !authStore.user ||
                    isOwner ||
                    !isAvailableForBooking ||
                    !hasAvailableUnits
                  "
                  :to="
                    authStore.user &&
                    !isOwner &&
                    isAvailableForBooking &&
                    hasAvailableUnits
                      ? `/bookings/create?listingId=${listingData.id}`
                      : undefined
                  "
                />

                <p v-if="!authStore.user" class="text-sm text-slate-500">
                  You need to log in before booking this listing.
                </p>

                <p v-else-if="isOwner" class="text-sm text-slate-500">
                  You cannot book your own listing.
                </p>
                <p
                  v-else-if="!hasAvailableUnits"
                  class="text-sm text-slate-500"
                >
                  No units are currently available for booking.
                </p>

                <p
                  v-else-if="!isAvailableForBooking"
                  class="text-sm text-slate-500"
                >
                  This listing is not available yet.
                </p>
              </div>
            </UCard>
          </div>
        </div>

        <div class="mt-16">
          <h2 class="mb-6 border-b border-slate-200 pb-2 text-2xl font-bold">
            Reviews and Comments (0)
          </h2>

          <div
            class="rounded-xl border border-slate-200 bg-slate-50 p-5 text-slate-500"
          >
            No reviews yet.
          </div>
        </div>
      </div>

      <div v-else>
        <p>Listing not found.</p>
      </div>
    </UContainer>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from "~/stores/auth";
import CreateListingImagePreview from "~/components/listings/CreateListingImagePreview.vue";
import ListingLocationMap from "~/components/listings/ListingLocationMap.vue";
import ListingAvailableUnits from "~/components/listings/ListingAvailableUnits.vue";

type ListingUnit = {
  id?: number;
  type: string;
  label: string;
  quantity: number;
  availableQuantity?: number;
  maxGuests?: number;
  pricePerNight: number;
};

type Listing = {
  id: number;
  title: string;
  location: string;
  latitude: number | null;
  longitude: number | null;
  description: string;
  lowestPrice: number;
  highestPrice: number;
  rating: number;
  images: string[];
  amenities: string[];
  availableFrom: string;
  units: ListingUnit[];
  status: string;
  sellerEmail: string;
  createdAt: string;
};

const route = useRoute();
const router = useRouter();
const config = useRuntimeConfig();
const authStore = useAuthStore();

const listingData = ref<Listing | null>(null);
const availableUnits = ref<ListingUnit[]>([]);
const isLoading = ref(false);
const isLoadingAvailableUnits = ref(false);

const previewImages = computed(() => {
  return (
    listingData.value?.images.map((image) => ({
      previewUrl: image,
    })) ?? []
  );
});

const hasAvailableUnits = computed(() => {
  return availableUnits.value.some((unit) => {
    const quantity =
      unit.availableQuantity !== undefined
        ? unit.availableQuantity
        : unit.quantity;

    return quantity > 0;
  });
});

const isAvailableForBooking = computed(() => {
  if (!listingData.value?.availableFrom) {
    return true;
  }

  const today = new Date();
  const availableFrom = new Date(listingData.value.availableFrom);

  today.setHours(0, 0, 0, 0);
  availableFrom.setHours(0, 0, 0, 0);

  return availableFrom <= today;
});

const priceLabel = computed(() => {
  if (!listingData.value) {
    return "Price not set";
  }

  if (!listingData.value.lowestPrice && !listingData.value.highestPrice) {
    return "Price not set";
  }

  if (listingData.value.lowestPrice === listingData.value.highestPrice) {
    return `€${listingData.value.lowestPrice}`;
  }

  return `€${listingData.value.lowestPrice} - €${listingData.value.highestPrice}`;
});

const isOwner = computed(() => {
  if (!authStore.user || !listingData.value) {
    return false;
  }

  return authStore.user.email === listingData.value.sellerEmail;
});

async function fetchAvailableUnits(listingId: number) {
  isLoadingAvailableUnits.value = true;

  try {
    availableUnits.value = await $fetch<ListingUnit[]>(
      `${config.public.apiBase}/listings/${listingId}/available-units`,
    );
  } catch (error) {
    console.error(error);

    availableUnits.value = listingData.value?.units ?? [];
  } finally {
    isLoadingAvailableUnits.value = false;
  }
}

async function fetchListing() {
  isLoading.value = true;

  try {
    listingData.value = await $fetch<Listing>(
      `${config.public.apiBase}/listings/${route.params.id}`,
    );

    await fetchAvailableUnits(listingData.value.id);
  } catch (error) {
    console.error(error);
    listingData.value = null;
    availableUnits.value = [];
  } finally {
    isLoading.value = false;
  }
}

onMounted(fetchListing);

useHead(() => ({
  title: listingData.value
    ? `${listingData.value.title} | Details`
    : "Listing Details",
}));
</script>
