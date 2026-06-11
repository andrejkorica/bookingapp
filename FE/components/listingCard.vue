<template>
  <NuxtLink :to="`/listings/${listing.id}`" class="block w-full text-slate-900 no-underline">
    <div
      class="w-full bg-white rounded-2xl shadow-md overflow-hidden flex h-full transition-all duration-300 hover:-translate-y-1 hover:shadow-xl"
    >
      <div class="flex-1 p-6 flex flex-col justify-between max-w-2xl">
        <div>
          <h2 class="text-xl font-bold leading-snug text-slate-800 group-hover:text-indigo-600 transition">
            {{ listing.title }}
          </h2>
          <div class="text-sm text-slate-500 mt-1">
            {{ listing.location }} · {{ listing.distance }}
          </div>
          <div class="mt-3 text-sm text-slate-600">
            {{ listing.details }}
          </div>
        </div>

        <div class="mt-4 space-y-3">
          <div class="flex items-center justify-between">
            <div class="text-sm">
              <span class="text-indigo-600 font-semibold">{{ listing.ratingLabel }}</span>
              <span class="text-slate-400 ml-1">({{ listing.reviews }} reviews)</span>
            </div>
            <div class="bg-indigo-600 text-white font-bold px-2.5 py-1 rounded-lg text-sm">
              {{ listing.rating }}
            </div>
          </div>
          <div class="flex items-center justify-between">
            <div v-if="listing.oldPrice" class="text-sm text-slate-400 line-through">
              € {{ listing.oldPrice }}
            </div>
            <div velse></div>
            <div class="text-xl font-bold text-slate-900">
              € {{ listing.price }}
            </div>
          </div>
        </div>
      </div>

      <div class="relative w-1/2 h-auto hidden sm:block">
        <img
          :src="listing.images[currentImage]"
          alt="Listing photo"
          class="w-full h-full object-cover"
        />
        </div>
    </div>
  </NuxtLink>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from "vue"

const props = defineProps({
  listing: {
    type: Object,
    required: true
  }
})

const currentImage = ref(0)
let interval: number | null = null;

function startSlideshow() {
  stopSlideshow(); 
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

onUnmounted(() => {
  stopSlideshow();
});
</script>

<style scoped>
.no-underline {
  text-decoration: none;
}
</style>