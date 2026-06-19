<script setup lang="ts">
import type { DateValue } from '@internationalized/date'
import { today, getLocalTimeZone } from '@internationalized/date'

type DateRangeValue = {
  start: DateValue | undefined
  end: DateValue | undefined
}

const dateRange = defineModel<DateRangeValue>({
  required: true
})

const minDate = today(getLocalTimeZone())

const isDateUnavailable = (date: DateValue) => {
  return date.compare(minDate) < 0
}
</script>

<template>
  <UCard>
    <template #header>
      <h2 class="text-xl font-semibold">
        Choose dates
      </h2>
    </template>

    <div class="flex justify-center">
      <UCalendar
        v-model="dateRange"
        range
        :is-date-unavailable="isDateUnavailable"
        :number-of-months="2"
        size="lg"
        color="primary"
      />
    </div>
  </UCard>
</template>