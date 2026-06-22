<script setup lang="ts">
import "leaflet/dist/leaflet.css";

const props = defineProps<{
  location: string
  latitude: number | null
  longitude: number | null
}>()

const mapEl = ref<HTMLElement | null>(null)

let map: any = null
let L: any = null

onMounted(async () => {
  await nextTick()

  if (props.latitude === null || props.longitude === null) {
    return
  }

  L = await import("leaflet")

  if (!mapEl.value) return

  map = L.map(mapEl.value, {
    dragging: false,
    scrollWheelZoom: false
  }).setView([props.latitude, props.longitude], 15)

  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    attribution: "&copy; OpenStreetMap contributors"
  }).addTo(map)

  L.marker([props.latitude, props.longitude]).addTo(map)

  setTimeout(() => {
    map?.invalidateSize()
  }, 100)
})

onUnmounted(() => {
  map?.remove()
})
</script>

<template>
  <section>
    <h3 class="mb-4 text-xl font-bold">
      Location
    </h3>

    <p class="mb-4 flex items-center text-slate-600">
      <UIcon name="i-heroicons-map-pin" class="mr-2 h-5 w-5 text-indigo-500" />
      {{ location }}
    </p>

    <ClientOnly>
      <div
        v-if="latitude !== null && longitude !== null"
        ref="mapEl"
        class="h-[320px] w-full overflow-hidden rounded-2xl border border-slate-200 shadow-sm"
      />

      <div
        v-else
        class="flex h-[320px] items-center justify-center rounded-2xl bg-slate-100 text-slate-500"
      >
        No coordinates saved for this listing.
      </div>
    </ClientOnly>
  </section>
</template>