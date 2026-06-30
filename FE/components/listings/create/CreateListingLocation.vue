<script setup lang="ts">
import "leaflet/dist/leaflet.css";
import type { LocationSuggestion } from "~/types/ComponentTypes";

const location = defineModel<string>("location", { default: "" });
const latitude = defineModel<number | null>("latitude", { default: null });
const longitude = defineModel<number | null>("longitude", { default: null });

const mapEl = ref<HTMLElement | null>(null);
const searchQuery = ref("");
const suggestions = ref<LocationSuggestion[]>([]);
const isSearching = ref(false);
const isSuggestionsOpen = ref(false);

let map: any = null;
let marker: any = null;
let L: any = null;
let searchTimeout: ReturnType<typeof setTimeout> | null = null;

function setMarker(lat: number, lng: number) {
  latitude.value = lat;
  longitude.value = lng;

  if (!L || !map) return;

  if (!marker) {
    marker = L.marker([lat, lng], { draggable: true }).addTo(map);

    marker.on("dragend", () => {
      const position = marker.getLatLng();
      latitude.value = position.lat;
      longitude.value = position.lng;
    });
  } else {
    marker.setLatLng([lat, lng]);
  }

  map.setView([lat, lng], 16);
}

async function fetchSuggestions() {
  if (searchQuery.value.trim().length < 3) {
    suggestions.value = [];
    return;
  }

  isSearching.value = true;

  try {
    suggestions.value = await $fetch<LocationSuggestion[]>(
      "https://nominatim.openstreetmap.org/search",
      {
        query: {
          q: searchQuery.value,
          format: "json",
          limit: 5,
          addressdetails: 1,
          countrycodes: "hr",
        },
      },
    );

    isSuggestionsOpen.value = suggestions.value.length > 0;
  } finally {
    isSearching.value = false;
  }
}

function onSearchInput() {
  if (searchTimeout) {
    clearTimeout(searchTimeout);
  }

  searchTimeout = setTimeout(() => {
    fetchSuggestions();
  }, 400);
}

function selectSuggestion(suggestion: LocationSuggestion) {
  location.value = suggestion.display_name;
  searchQuery.value = suggestion.display_name;
  suggestions.value = [];
  isSuggestionsOpen.value = false;

  setMarker(Number(suggestion.lat), Number(suggestion.lon));
}

async function searchFirstSuggestion() {
  const firstSuggestion = suggestions.value[0];

  if (firstSuggestion) {
    selectSuggestion(firstSuggestion);
    return;
  }

  await fetchSuggestions();
}

onMounted(async () => {
  await nextTick();

  L = await import("leaflet");

  if (!mapEl.value) return;

  const defaultLat = latitude.value ?? 44.8666;
  const defaultLng = longitude.value ?? 13.8496;

  map = L.map(mapEl.value).setView([defaultLat, defaultLng], 13);

  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    attribution: "&copy; OpenStreetMap contributors",
  }).addTo(map);

  map.on("click", (event: any) => {
    setMarker(event.latlng.lat, event.latlng.lng);
  });

  setTimeout(() => {
    map?.invalidateSize();
  }, 100);

  if (latitude.value !== null && longitude.value !== null) {
    setMarker(latitude.value, longitude.value);
  }
});

onUnmounted(() => {
  map?.remove();

  if (searchTimeout) {
    clearTimeout(searchTimeout);
  }
});
</script>

<template>
  <section class="mb-10">
    <h2 class="mb-4 border-b border-slate-200 pb-2 text-2xl font-bold">
      Location
    </h2>

    <div class="relative mb-4 flex gap-2">
      <div class="relative flex-1">
        <UInput
          v-model="searchQuery"
          placeholder="Search street, address, city..."
          icon="i-heroicons-map-pin"
          class="w-full"
          @input="onSearchInput"
          @focus="isSuggestionsOpen = suggestions.length > 0"
          @keyup.enter="searchFirstSuggestion"
        />

        <div
          v-if="isSuggestionsOpen"
          class="absolute z-[1000] mt-2 max-h-72 w-full overflow-auto rounded-xl border border-slate-200 bg-white shadow-lg"
        >
          <button
            v-for="suggestion in suggestions"
            :key="`${suggestion.lat}-${suggestion.lon}-${suggestion.display_name}`"
            type="button"
            class="block w-full px-4 py-3 text-left text-sm text-slate-700 hover:bg-indigo-50 hover:text-indigo-700"
            @click="selectSuggestion(suggestion)"
          >
            {{ suggestion.display_name }}
          </button>
        </div>
      </div>

      <UButton
        label="Search"
        :loading="isSearching"
        class="bg-indigo-600 text-white hover:bg-indigo-700"
        @click="searchFirstSuggestion"
      />
    </div>

    <ClientOnly>
      <div
        ref="mapEl"
        class="h-[400px] w-full overflow-hidden rounded-2xl border border-slate-200 shadow-sm"
      />

      <template #fallback>
        <div
          class="flex h-[400px] items-center justify-center rounded-2xl border border-slate-200 bg-slate-50 text-slate-500"
        >
          Loading map...
        </div>
      </template>
    </ClientOnly>

    <div class="mt-4 space-y-1">
      <p v-if="location" class="text-sm text-slate-600">
        <span class="font-medium text-slate-800">Selected:</span>
        {{ location }}
      </p>

      <p
        v-if="latitude !== null && longitude !== null"
        class="text-sm text-slate-500"
      >
        Coordinates: {{ latitude.toFixed(5) }}, {{ longitude.toFixed(5) }}
      </p>
    </div>
  </section>
</template>
