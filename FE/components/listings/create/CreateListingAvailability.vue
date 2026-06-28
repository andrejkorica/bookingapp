<script setup lang="ts">
import type { DateValue } from '@internationalized/date'
import { today, getLocalTimeZone } from '@internationalized/date'

const availableFrom = defineModel<DateValue>({
  required: true
})

const minDate = today(getLocalTimeZone())
</script>

<template>
  <div>
    <h3 class="mb-4 text-xl font-bold">
      Available From
    </h3>

    <UCard class="mb-8">
      <div class="flex justify-center">
        <UCalendar
          v-model="availableFrom"
          :min-value="minDate"
          size="lg"
          color="primary"
        />
      </div>
    </UCard>
  </div>
</template>