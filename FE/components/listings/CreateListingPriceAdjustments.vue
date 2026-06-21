<script setup lang="ts">
import { breakpointsTailwind, useBreakpoints } from '@vueuse/core'
import { DateFormatter, getLocalTimeZone, parseDate } from '@internationalized/date'
import type { DateValue } from '@internationalized/date'

export type PriceAdjustment = {
  startDate: string
  endDate: string
  percent: number
}

type DateRangeValue = {
  start: DateValue | undefined
  end: DateValue | undefined
}

const priceAdjustments = defineModel<PriceAdjustment[]>({
  required: true
})

const tz = getLocalTimeZone()
const df = new DateFormatter('en-US', { dateStyle: 'medium' })

const breakpoints = useBreakpoints(breakpointsTailwind)
const isDesktop = breakpoints.greaterOrEqual('sm')

function getDateRange(adjustment: PriceAdjustment): DateRangeValue {
  return {
    start: adjustment.startDate ? parseDate(adjustment.startDate) : undefined,
    end: adjustment.endDate ? parseDate(adjustment.endDate) : undefined
  }
}

function setDateRange(index: number, value: DateRangeValue | null | undefined) {
  const adjustment = priceAdjustments.value[index]

  if (!adjustment) {
    return
  }

  adjustment.startDate = value?.start?.toString() || ''
  adjustment.endDate = value?.end?.toString() || ''
}

function getDateLabel(adjustment: PriceAdjustment) {
  if (!adjustment.startDate) {
    return 'Pick date range'
  }

  const start = parseDate(adjustment.startDate)

  if (!adjustment.endDate) {
    return df.format(start.toDate(tz))
  }

  const end = parseDate(adjustment.endDate)

  return `${df.format(start.toDate(tz))} - ${df.format(end.toDate(tz))}`
}

function addAdjustment() {
  priceAdjustments.value.push({
    startDate: '',
    endDate: '',
    percent: 0
  })
}

function removeAdjustment(index: number) {
  priceAdjustments.value.splice(index, 1)
}
</script>

<template>
  <div>
    <h3 class="mb-4 text-xl font-bold">
      Seasonal Price Adjustments
    </h3>

    <div class="space-y-4">
      <UCard
        v-for="(adjustment, index) in priceAdjustments"
        :key="index"
      >
        <div class="grid grid-cols-1 gap-4 md:grid-cols-[1fr_180px_auto] md:items-end">
          <UFormField label="Date Range">
            <UPopover :content="{ align: 'start' }">
              <UButton
                color="neutral"
                variant="subtle"
                icon="i-lucide-calendar"
                block
                class="justify-start"
              >
                {{ getDateLabel(adjustment) }}
              </UButton>

              <template #content>
                <UCalendar
                  :model-value="getDateRange(adjustment)"
                  class="p-2"
                  :number-of-months="isDesktop ? 2 : 1"
                  range
                  @update:model-value="setDateRange(index, $event as DateRangeValue)"
                />
              </template>
            </UPopover>
          </UFormField>

          <UFormField label="Increase %">
            <UInput
              v-model.number="adjustment.percent"
              type="number"
              min="0"
              placeholder="20"
              icon="i-lucide-percent"
            />
          </UFormField>

          <div class="flex items-end">
            <UButton
              icon="i-lucide-trash"
              color="error"
              variant="soft"
              :disabled="priceAdjustments.length === 1"
              @click="removeAdjustment(index)"
            />
          </div>
        </div>
      </UCard>

      <UButton
        label="Add Price Adjustment"
        icon="i-lucide-plus"
        variant="soft"
        color="neutral"
        @click="addAdjustment"
      />
    </div>
  </div>
</template>