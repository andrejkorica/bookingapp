<script setup lang="ts">
defineProps<{
  selectedUnitLabel: string
  checkIn: string
  checkOut: string
  nights: number
  finalPricePerNight: number
  totalPrice: number
  canContinue: boolean
}>()

const emit = defineEmits<{
  continue: []
}>()
</script>

<template>
  <div>
    <UCard class="sticky top-6">
      <template #header>
        <h2 class="text-xl font-semibold">
          Your stay
        </h2>
      </template>

      <div class="space-y-4 text-sm text-slate-600">
        <div class="flex justify-between">
          <span>Selected unit</span>
          <span class="font-medium text-slate-900">
            {{ selectedUnitLabel }}
          </span>
        </div>

        <div class="flex justify-between">
          <span>Check-in</span>
          <span class="font-medium text-slate-900">
            {{ checkIn }}
          </span>
        </div>

        <div class="flex justify-between">
          <span>Check-out</span>
          <span class="font-medium text-slate-900">
            {{ checkOut }}
          </span>
        </div>

        <div class="flex justify-between">
          <span>Length of stay</span>
          <span class="font-medium text-slate-900">
            {{ nights }} night<span v-if="nights !== 1">s</span>
          </span>
        </div>

        <div class="rounded-xl bg-slate-50 p-3 text-xs text-slate-500">
          The displayed total includes any applicable seasonal price adjustments for the selected dates.
        </div>

        <div class="border-t border-slate-200 pt-4">
          <div class="flex items-center justify-between">
            <span class="text-base font-semibold text-slate-900">
              Total
            </span>

            <span class="text-3xl font-bold text-slate-900">
              €{{ totalPrice }}
            </span>
          </div>
        </div>

        <UButton
          label="Continue to guest info"
          icon="i-lucide-arrow-right"
          size="xl"
          block
          class="bg-indigo-600 font-bold text-white hover:bg-indigo-700"
          :disabled="!canContinue"
          @click="emit('continue')"
        />
      </div>
    </UCard>
  </div>
</template>