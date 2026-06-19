<script setup lang="ts">
type UnitOption = {
  label: string
  value: string
  multiplier: number
}

const selectedUnit = defineModel<string>({
  required: true
})

defineProps<{
  unitOptions: UnitOption[]
  basePricePerNight: number
}>()
</script>

<template>
  <UCard>
    <template #header>
      <h2 class="text-xl font-semibold">
        Choose unit type
      </h2>
    </template>

    <div class="grid grid-cols-1 gap-3 md:grid-cols-2">
      <button
        v-for="unit in unitOptions"
        :key="unit.value"
        type="button"
        class="rounded-xl border p-4 text-left transition hover:border-indigo-400 hover:bg-indigo-50"
        :class="
          selectedUnit === unit.value
            ? 'border-indigo-500 bg-indigo-50 ring-2 ring-indigo-200'
            : 'border-slate-200 bg-white'
        "
        @click="selectedUnit = unit.value"
      >
        <div class="flex items-center justify-between gap-3">
          <div>
            <p class="font-semibold text-slate-900">
              {{ unit.label }}
            </p>

            <p class="mt-1 text-sm text-slate-500">
              €{{ Math.round(basePricePerNight * unit.multiplier) }} / night before date changes
            </p>
          </div>

          <UIcon
            v-if="selectedUnit === unit.value"
            name="i-lucide-check-circle"
            class="size-5 text-indigo-600"
          />
        </div>
      </button>
    </div>
  </UCard>
</template>