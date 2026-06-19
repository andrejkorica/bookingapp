<script setup lang="ts">
defineProps<{
  step: number
}>()
</script>

<template>
  <div class="mb-8 grid grid-cols-3 gap-3">
    <div
      class="rounded-xl p-4 text-center"
      :class="step === 1 ? 'bg-indigo-600 text-white' : 'bg-white text-slate-500 ring-1 ring-slate-200'"
    >
      <p class="text-sm">Step 1</p>
      <p class="font-semibold">Unit & dates</p>
    </div>

    <div
      class="rounded-xl p-4 text-center"
      :class="step === 2 ? 'bg-indigo-600 text-white' : 'bg-white text-slate-500 ring-1 ring-slate-200'"
    >
      <p class="text-sm">Step 2</p>
      <p class="font-semibold">Guest info</p>
    </div>

    <div
      class="rounded-xl p-4 text-center"
      :class="step === 3 ? 'bg-indigo-600 text-white' : 'bg-white text-slate-500 ring-1 ring-slate-200'"
    >
      <p class="text-sm">Step 3</p>
      <p class="font-semibold">Payment</p>
    </div>
  </div>
</template>