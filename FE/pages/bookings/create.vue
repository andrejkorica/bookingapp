<script setup lang="ts">
import type { DateValue } from '@internationalized/date'
import { getLocalTimeZone } from '@internationalized/date'
import BookingSteps from '~/components/booking/BookingSteps.vue'
import BookingUnitSelector from '~/components/booking/BookingUnitSelector.vue'
import BookingDateSelector from '~/components/booking/BookingDateSelector.vue'
import BookingPriceSummary from '~/components/booking/BookingPriceSummary.vue'

definePageMeta({
  layout: 'default',
})

type UnitOption = {
  label: string
  value: string
  multiplier: number
}

type DateRangeValue = {
  start: DateValue | undefined
  end: DateValue | undefined
}

const route = useRoute()

const step = ref(1)
const basePricePerNight = 100

const unitOptions: UnitOption[] = [
  { label: 'Single room', value: 'single_room', multiplier: 0.75 },
  { label: 'Double room', value: 'double_room', multiplier: 1 },
  { label: 'Twin room', value: 'twin_room', multiplier: 1 },
  { label: 'Queen room', value: 'queen_room', multiplier: 1.15 },
  { label: 'King room', value: 'king_room', multiplier: 1.25 },
  { label: 'Studio apartment', value: 'studio_apartment', multiplier: 1.1 },
  { label: 'One-bedroom apartment', value: 'one_bedroom_apartment', multiplier: 1.25 },
  { label: 'Two-bedroom apartment', value: 'two_bedroom_apartment', multiplier: 1.6 },
  { label: 'Three-bedroom apartment', value: 'three_bedroom_apartment', multiplier: 2 },
  { label: 'Family apartment', value: 'family_apartment', multiplier: 1.8 },
  { label: 'Penthouse apartment', value: 'penthouse_apartment', multiplier: 2.5 }
]

const selectedUnit = ref(unitOptions[1]?.value ?? 'double_room')

const dateRange = shallowRef<DateRangeValue>({
  start: undefined,
  end: undefined
})

const selectedUnitData = computed(() => {
  return unitOptions.find(unit => unit.value === selectedUnit.value) ?? unitOptions[0]!
})

const nights = computed(() => {
  if (!dateRange.value.start || !dateRange.value.end) {
    return 0
  }

  const start = dateRange.value.start.toDate(getLocalTimeZone())
  const end = dateRange.value.end.toDate(getLocalTimeZone())
  const diff = end.getTime() - start.getTime()

  return Math.max(0, Math.ceil(diff / (1000 * 60 * 60 * 24)))
})

const unitPricePerNight = computed(() => {
  return Math.round(basePricePerNight * selectedUnitData.value.multiplier)
})

const dateMultiplier = computed(() => {
  if (!dateRange.value.start) {
    return 1
  }

  const month = dateRange.value.start.month

  if ([6, 7, 8].includes(month)) return 1.35
  if ([12, 1].includes(month)) return 1.2

  return 1
})

const finalPricePerNight = computed(() => {
  return Math.round(unitPricePerNight.value * dateMultiplier.value)
})

const totalPrice = computed(() => {
  return finalPricePerNight.value * nights.value
})

const canContinue = computed(() => {
  return Boolean(selectedUnit.value && nights.value > 0)
})

const checkInLabel = computed(() => {
  if (!dateRange.value.start) return 'Not selected'

  return dateRange.value.start
    .toDate(getLocalTimeZone())
    .toLocaleDateString()
})

const checkOutLabel = computed(() => {
  if (!dateRange.value.end) return 'Not selected'

  return dateRange.value.end
    .toDate(getLocalTimeZone())
    .toLocaleDateString()
})

function goNext() {
  if (!canContinue.value) return

  step.value = 2
}
</script>

<template>
  <div class="min-h-screen bg-slate-50 text-slate-900">
    <UContainer class="py-10">
      <div class="mb-8">
        <h1 class="text-3xl font-bold">
          Complete your booking
        </h1>

        <p class="mt-2 text-slate-600">
          Choose the unit type and dates for your stay.
        </p>
      </div>

      <BookingSteps :step="step" />

      <div class="grid grid-cols-1 gap-8 lg:grid-cols-3">
        <div class="space-y-6 lg:col-span-2">
          <BookingUnitSelector
            v-model="selectedUnit"
            :unit-options="unitOptions"
            :base-price-per-night="basePricePerNight"
          />

          <BookingDateSelector v-model="dateRange" />
        </div>

        <BookingPriceSummary
          :selected-unit-label="selectedUnitData.label"
          :check-in="checkInLabel"
          :check-out="checkOutLabel"
          :nights="nights"
          :final-price-per-night="finalPricePerNight"
          :total-price="totalPrice"
          :can-continue="canContinue"
          @continue="goNext"
        />
      </div>
    </UContainer>
  </div>
</template>