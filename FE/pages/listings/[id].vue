<template>
  <div class="bg-white text-slate-900 min-h-screen">
    <UContainer class="py-12">
      <div v-if="isLoading">
        <p>Loading listing data...</p>
      </div>

      <div v-else-if="listingData">
        <header class="mb-8">
          <h1
            class="text-4xl md:text-5xl font-bold tracking-tight text-slate-900 mb-2"
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
                  class="w-5 h-5"
                />
              </div>

              <div class="flex min-w-0 items-center">
                <UIcon
                  name="i-heroicons-map-pin"
                  class="w-5 h-5 mr-2 shrink-0"
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
          <UCarousel
            v-if="listingData.images?.length"
            v-slot="{ item }"
            :items="listingData.images"
            :ui="{ item: 'basis-full' }"
            :prev="{ color: 'primary' }"
            :next="{ color: 'primary' }"
            class="rounded-2xl overflow-hidden shadow-lg"
            arrows
            indicators
          >
            <img
              :src="item"
              class="w-full h-96 object-cover"
              draggable="false"
            />
          </UCarousel>

          <div
            v-else
            class="h-96 rounded-2xl bg-slate-100 flex items-center justify-center text-slate-400"
          >
            No images uploaded.
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-12">
          <div class="lg:col-span-2">
            <h2 class="text-2xl font-bold border-b border-slate-200 pb-2 mb-4">
              About the Property
            </h2>

            <p class="text-slate-600 leading-relaxed mb-8">
              {{ listingData.description }}
            </p>

            <h3 class="text-xl font-bold mb-4">Amenities</h3>

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
                  class="w-5 h-5 text-indigo-500 mr-3"
                />
                <span class="text-slate-600">{{ amenity }}</span>
              </li>
            </ul>

            <p v-else class="text-slate-500">No amenities listed.</p>
          </div>

          <div>
            <UCard class="bg-white shadow-lg border border-slate-200">
              <div class="text-center space-y-4">
                <p class="text-lg text-slate-500">Price per night</p>

                <p class="text-4xl font-bold text-slate-900">
                  €{{ listingData.pricePerNight }}
                </p>

                <UButton
                  label="Book now"
                  size="xl"
                  block
                  class="bg-indigo-600 hover:bg-indigo-700 text-white font-bold"
                  :disabled="!authStore.user || isOwner"
                  :to="authStore.user && !isOwner ? `/bookings/create?listingId=${listingData.id}` : undefined"
                />

                <p v-if="!authStore.user" class="text-sm text-slate-500">
                  You need to log in before booking this listing.
                </p>

                <p v-else-if="isOwner" class="text-sm text-slate-500">
                  You cannot book your own listing.
                </p>
              </div>
            </UCard>
          </div>
        </div>

        <div class="mt-16">
          <h2 class="text-2xl font-bold border-b border-slate-200 pb-2 mb-6">
            Reviews and Comments (0)
          </h2>

          <div
            class="p-5 border border-slate-200 rounded-xl bg-slate-50 text-slate-500"
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

type Listing = {
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

const route = useRoute();
const router = useRouter();
const config = useRuntimeConfig();
const authStore = useAuthStore();

const listingData = ref<Listing | null>(null);
const isLoading = ref(false);

const isOwner = computed(() => {
  if (!authStore.user || !listingData.value) {
    return false;
  }

  return authStore.user.email === listingData.value.sellerEmail;
});

async function fetchListing() {
  isLoading.value = true;

  try {
    listingData.value = await $fetch<Listing>(
      `${config.public.apiBase}/listings/${route.params.id}`,
    );
  } catch (error) {
    console.error(error);
    listingData.value = null;
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
