<script setup lang="ts">
import type { ListingUnit } from "~/types/ListingTypes";

const selectedUnits = defineModel<Record<string, number>>({
  required: true,
});

defineProps<{
  unitOptions: ListingUnit[];
}>();

function increase(unit: ListingUnit) {
  const current = selectedUnits.value[unit.type] ?? 0;

  if (current >= unit.quantity) {
    return;
  }

  selectedUnits.value[unit.type] = current + 1;
}

function decrease(unit: ListingUnit) {
  const current = selectedUnits.value[unit.type] ?? 0;

  if (current <= 0) {
    return;
  }

  selectedUnits.value[unit.type] = current - 1;
}
</script>

<template>
  <UCard>
    <template #header>
      <h2 class="text-xl font-semibold">
        Choose units
      </h2>
    </template>

    <div class="grid grid-cols-1 gap-3 md:grid-cols-2">
      <div
        v-for="unit in unitOptions"
        :key="unit.type"
        class="rounded-xl border p-4 transition"
        :class="
          (selectedUnits[unit.type] ?? 0) > 0
            ? 'border-indigo-500 bg-indigo-50 ring-2 ring-indigo-200'
            : 'border-slate-200 bg-white'
        "
      >
        <div class="flex items-start justify-between gap-3">
          <div>
            <p class="font-semibold text-slate-900">
              {{ unit.label }}
            </p>

            <p class="mt-1 text-sm text-slate-500">
              {{ unit.quantity }} available
            </p>

            <p
              v-if="unit.maxGuests"
              class="mt-1 text-sm text-slate-500"
            >
              Up to {{ unit.maxGuests }}
              {{ unit.maxGuests === 1 ? 'guest' : 'guests' }} each
            </p>

            <p
              v-else
              class="mt-1 text-sm italic text-slate-400"
            >
              Guest capacity not specified
            </p>

            <p class="mt-3 text-lg font-bold text-slate-900">
              €{{ unit.pricePerNight }} / night
            </p>
          </div>

          <UIcon
            v-if="(selectedUnits[unit.type] ?? 0) > 0"
            name="i-lucide-check-circle"
            class="size-5 shrink-0 text-indigo-600"
          />
        </div>

        <div class="mt-4 flex items-center justify-between">
          <p class="text-sm text-slate-600">
            Selected:
            <span class="font-semibold text-slate-900">
              {{ selectedUnits[unit.type] ?? 0 }}
            </span>
          </p>

          <div class="flex items-center gap-2">
            <UButton
              icon="i-lucide-minus"
              color="neutral"
              variant="soft"
              size="sm"
              :disabled="(selectedUnits[unit.type] ?? 0) <= 0"
              @click="decrease(unit)"
            />

            <span class="w-6 text-center font-semibold">
              {{ selectedUnits[unit.type] ?? 0 }}
            </span>

            <UButton
              icon="i-lucide-plus"
              color="primary"
              variant="soft"
              size="sm"
              :disabled="(selectedUnits[unit.type] ?? 0) >= unit.quantity"
              @click="increase(unit)"
            />
          </div>
        </div>
      </div>
    </div>
  </UCard>
</template>