<script setup lang="ts">
import type { DateValue } from '@internationalized/date'
import { today, getLocalTimeZone, parseDate } from '@internationalized/date'
import type { DateRangeValue } from '~/types/ComponentTypes';
import type { BookedRange } from '~/types/BookingTypes';

const dateRange = defineModel<DateRangeValue>({
  required: true
})

const props = defineProps<{
  minDate?: DateValue
  bookedRanges?: BookedRange[]
}>()

const fallbackMinDate = today(getLocalTimeZone())

const isDateUnavailable = (date: DateValue) => {
  const min = props.minDate ?? fallbackMinDate

  if (date.compare(min) < 0) {
    return true
  }

  return (
    props.bookedRanges?.some((range) => {
      const checkIn = parseDate(range.checkIn)
      const checkOut = parseDate(range.checkOut)

      return date.compare(checkIn) >= 0 && date.compare(checkOut) < 0
    }) ?? false
  )
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
        :min-value="props.minDate"
        color="primary"
      />
    </div>
  </UCard>
</template>