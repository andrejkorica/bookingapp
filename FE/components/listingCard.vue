<template>
  <NuxtLink
    :to="`/listings/${listing.id}`"
    class="block w-full text-slate-900 no-underline"
  >
    <div
      class="w-full h-[270px] bg-white rounded-2xl shadow-md overflow-hidden flex transition-all duration-300 hover:-translate-y-1 hover:shadow-xl"
    >
      <div class="w-1/2 min-w-0 p-6 flex flex-col justify-between">
        <div>
          <h2 class="text-xl font-bold leading-snug text-slate-800 transition">
            {{ listing.title }}
          </h2>

          <div class="text-sm text-slate-500 mt-1 line-clamp-2">
            {{ listing.location }}
          </div>

          <div class="mt-3 text-sm text-slate-600 line-clamp-2">
            {{ listing.description }}
          </div>
        </div>

        <div class="mt-4 space-y-3">
          <div class="flex items-center justify-between">
            <div class="text-sm">
              <span class="text-indigo-600 font-semibold">
                {{ ratingLabel }}
              </span>

              <span class="text-slate-400 ml-1"> (No reviews yet) </span>
            </div>

            <div
              class="bg-indigo-600 text-white font-bold px-2.5 py-1 rounded-lg text-sm"
            >
              {{ listing.rating }}
            </div>
          </div>

          <div class="flex items-center justify-between">
            <div class="text-sm text-slate-500">Price per night</div>

            <div class="text-xl font-bold text-slate-900">
              € {{ listing.pricePerNight }}
            </div>
          </div>
        </div>
      </div>

      <div class="relative w-1/2 shrink-0 h-full hidden sm:block bg-slate-100 overflow-hidden">
        <img
          v-if="listing.images?.length"
          :src="listing.images[currentImage]"
          alt="Listing image"
          class="w-full h-full object-cover"
        />

        <div
          v-else
          class="flex h-full items-center justify-center text-slate-400"
        >
          No image
        </div>
      </div>
    </div>
  </NuxtLink>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from "vue";

type Listing = {
  id: number;
  title: string;
  location: string;
  description: string;
  pricePerNight: number;
  rating: number;
  images: string[];
  amenities: string[];
};

const props = defineProps<{
  listing: Listing;
}>();

const currentImage = ref(0);
let interval: ReturnType<typeof setInterval> | null = null;

const ratingLabel = computed(() => {
  if (props.listing.rating >= 5) return "Exceptional";
  if (props.listing.rating >= 4) return "Great";
  if (props.listing.rating >= 3) return "Good";
  return "New";
});

function startSlideshow() {
  stopSlideshow();

  if (!props.listing.images || props.listing.images.length <= 1) {
    return;
  }

  interval = setInterval(() => {
    currentImage.value = (currentImage.value + 1) % props.listing.images.length;
  }, 3000);
}

function stopSlideshow() {
  if (interval) {
    clearInterval(interval);
    interval = null;
  }
}

onMounted(startSlideshow);
onUnmounted(stopSlideshow);
</script>

<style scoped>
.no-underline {
  text-decoration: none;
}
</style>
