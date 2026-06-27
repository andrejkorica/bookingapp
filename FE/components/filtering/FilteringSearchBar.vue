<script setup lang="ts">
import { DateFormatter, getLocalTimeZone, parseDate } from "@internationalized/date";
import type { DateValue } from "@internationalized/date";
import type { ListingSearchQuery } from "~/types/Search";

type DateRangeValue = {
  start: DateValue | undefined;
  end: DateValue | undefined;
};

const destination = ref("");
const checkIn = ref("");
const checkOut = ref("");
const occupancy = ref("");

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
    occupancy: occupancy.value,
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
              <UInput
                v-model="destination"
                icon="i-heroicons-map-pin"
                size="xl"
                placeholder="Where are you going?"
                :ui="{ leadingIcon: 'text-indigo-400' }"
              />
            </div>

            <div class="md:col-span-3">
              <UPopover :content="{ align: 'start' }">
                <UButton
                  color="neutral"
                  variant="subtle"
                  icon="i-lucide-calendar"
                  size="xl"
                  block
                  class="justify-start"
                >
                  {{ getDateLabel() }}
                </UButton>

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
              <UInput
                v-model="occupancy"
                icon="i-heroicons-user-group"
                size="xl"
                placeholder="2 adults · 0 children"
                :ui="{ leadingIcon: 'text-indigo-400' }"
              />
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