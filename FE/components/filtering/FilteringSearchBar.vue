<script setup lang="ts">
import {
  DateFormatter,
  getLocalTimeZone,
  parseDate,
} from "@internationalized/date";
import type { DateValue } from "@internationalized/date";
import type { ListingSearchQuery } from "~/types/Search";
import FilteringGuestSelector from "~/components/filtering/FilteringGuestSelector.vue";

type DateRangeValue = {
  start: DateValue | undefined;
  end: DateValue | undefined;
};

const config = useRuntimeConfig();

const destination = ref("");
const checkIn = ref("");
const checkOut = ref("");
const isCalendarOpen = ref(false);

const guestInfo = ref({
  adults: 2,
  children: 0,
  rooms: 1,
});

const locationSuggestions = ref<string[]>([]);
const isLoadingLocations = ref(false);

const filteredLocationSuggestions = computed(() => {
  if (!destination.value) {
    return locationSuggestions.value.slice(0, 4);
  }

  return locationSuggestions.value
    .filter((location) =>
      location.toLowerCase().includes(destination.value.toLowerCase()),
    )
    .slice(0, 4);
});

async function fetchLocations() {
  isLoadingLocations.value = true;

  try {
    locationSuggestions.value = await $fetch<string[]>(
      `${config.public.apiBase}/listings/locations`,
    );
  } catch (error) {
    console.error(error);
    locationSuggestions.value = [];
  } finally {
    isLoadingLocations.value = false;
  }
}

onMounted(fetchLocations);

const tz = getLocalTimeZone();
const df = new DateFormatter("en-US", {
  dateStyle: "medium",
});

function getDateRange(): DateRangeValue {
  return {
    start: checkIn.value ? parseDate(checkIn.value) : undefined,
    end: checkOut.value ? parseDate(checkOut.value) : undefined,
  };
}

function setDateRange(value: DateRangeValue | null | undefined) {
  checkIn.value = value?.start?.toString() || "";
  checkOut.value = value?.end?.toString() || "";
}

function getDateLabel() {
  if (!checkIn.value) {
    return "Check-in — Check-out";
  }

  const start = parseDate(checkIn.value);

  if (!checkOut.value) {
    return df.format(start.toDate(tz));
  }

  const end = parseDate(checkOut.value);

  return `${df.format(start.toDate(tz))} - ${df.format(end.toDate(tz))}`;
}

const emit = defineEmits<{
  search: [query: ListingSearchQuery];
}>();

function handleSearch() {
  emit("search", {
    destination: destination.value,
    checkIn: checkIn.value,
    checkOut: checkOut.value,
    occupancy: `${guestInfo.value.adults} adults · ${guestInfo.value.children} children · ${guestInfo.value.rooms} rooms`,
  });
}
</script>

<template>
  <div>
    <div
      class="bg-gradient-to-br from-indigo-600 via-indigo-500 to-blue-500 pb-24 pt-12 md:pt-16"
    >
      <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <div class="mx-auto max-w-4xl text-center">
          <h1 class="text-4xl font-extrabold text-white md:text-5xl">
            Find your next stay
          </h1>

          <p class="mt-4 text-lg text-indigo-100">
            Search deals on hotels, homes, and more...
          </p>
        </div>
      </div>
    </div>

    <div class="container relative z-10 mx-auto -mt-14 px-4 sm:px-6 lg:px-8">
      <form class="mx-auto max-w-5xl" @submit.prevent="handleSearch">
        <div
          class="rounded-2xl border border-slate-100 bg-white p-4 shadow-2xl"
        >
          <div class="grid grid-cols-1 gap-3 md:grid-cols-10 md:items-center">
            <div class="md:col-span-3">
              <UInputMenu
                v-model="destination"
                :items="filteredLocationSuggestions"
                :loading="isLoadingLocations"
                open-on-focus
                icon="i-heroicons-map-pin"
                size="xl"
                placeholder="Where are you going?"
                :ui="{ leadingIcon: 'text-indigo-400' }"
              />
            </div>

            <div class="md:col-span-3">
              <UPopover
                v-model:open="isCalendarOpen"
                :content="{ align: 'start' }"
              >
                <UInput
                  :model-value="getDateLabel()"
                  icon="i-heroicons-calendar-days"
                  size="xl"
                  readonly
                  class="w-full cursor-pointer"
                  :ui="{
                    leadingIcon: 'text-indigo-400',
                    base: [
                      'cursor-pointer text-left',
                      !checkIn ? 'text-slate-500' : 'text-slate-900',
                      isCalendarOpen
                        ? 'ring-2 ring-indigo-500 border-indigo-500'
                        : '',
                    ],
                  }"
                />

                <template #content>
                  <UCalendar
                    :model-value="getDateRange()"
                    class="p-2"
                    :number-of-months="2"
                    range
                    @update:model-value="
                      setDateRange($event as DateRangeValue)
                    "
                  />
                </template>
              </UPopover>
            </div>

            <div class="md:col-span-3">
              <FilteringGuestSelector v-model="guestInfo" />
            </div>

            <div class="md:col-span-1">
              <UButton
                type="submit"
                label="Search"
                size="xl"
                block
                class="bg-indigo-600 font-bold text-white hover:bg-indigo-700"
              />
            </div>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>