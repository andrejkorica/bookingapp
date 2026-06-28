<script setup lang="ts">
export type ListingUnit = {
  type: string
  label: string
  quantity: number
  maxGuests: number
  pricePerNight: number
}

const listingUnits = defineModel<ListingUnit[]>({
  required: true
})

const selectedUnits = computed(() =>
  listingUnits.value.filter(
    (unit: ListingUnit) =>
      unit.quantity > 0 &&
      unit.pricePerNight > 0
  )
)

const lowestPrice = computed(() => {
  if (!selectedUnits.value.length) {
    return 0
  }

  return Math.min(
    ...selectedUnits.value.map(
      (unit: ListingUnit) => unit.pricePerNight
    )
  )
})

const highestPrice = computed(() => {
  if (!selectedUnits.value.length) {
    return 0
  }

  return Math.max(
    ...selectedUnits.value.map(
      (unit: ListingUnit) => unit.pricePerNight
    )
  )
})
</script>

<template>
  <div>
    <h3 class="mb-4 text-xl font-bold">
      Units and Prices
    </h3>

    <UCard class="mb-8">
      <div class="space-y-4">
        <div
          v-for="unit in listingUnits"
          :key="unit.type"
          class="grid grid-cols-1 gap-3 rounded-xl border p-4 transition-all md:grid-cols-5 md:items-center"
          :class="
            unit.quantity > 0
              ? 'border-violet-500 bg-violet-50'
              : 'border-slate-200 bg-white'
          "
        >
          <div>
            <p class="font-medium text-slate-900">
              {{ unit.label }}
            </p>
            <p class="text-xs text-slate-500">
              Unit type
            </p>
          </div>

          <UInput
            v-model.number="unit.quantity"
            type="number"
            min="0"
            placeholder="How many?"
          />

          <UInput
            v-model.number="unit.maxGuests"
            type="number"
            min="1"
            icon="i-lucide-users"
            placeholder="Max guests"
            :disabled="unit.quantity <= 0"
          />

          <UInput
            v-model.number="unit.pricePerNight"
            type="number"
            min="0"
            icon="i-lucide-euro"
            placeholder="Price per night"
            :disabled="unit.quantity <= 0"
          />

          <p class="text-sm text-slate-500">
            {{
              unit.quantity > 0
                ? `Up to ${unit.maxGuests} ${unit.maxGuests === 1 ? 'guest' : 'guests'}`
                : 'Enter quantity to offer this unit'
            }}
          </p>
        </div>
      </div>
    </UCard>

    <div class="mb-8 rounded-xl bg-slate-50 p-4 text-sm text-slate-600">
      <span v-if="selectedUnits.length">
        Price range:
        <strong class="text-slate-900">
          €{{ lowestPrice }} - €{{ highestPrice }}
        </strong>
      </span>

      <span v-else>
        Add at least one unit with quantity and price.
      </span>
    </div>
  </div>
</template>