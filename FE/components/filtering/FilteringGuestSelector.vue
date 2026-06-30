<script setup lang="ts">
import type { GuestInfoFilter } from '~/types/ComponentTypes';


const model = defineModel<GuestInfoFilter>({
  required: true,
});

const isOpen = ref(false);

const label = computed(() => {
  const adults = model.value.adults;
  const children = model.value.children;
  const rooms = model.value.rooms;

  return `${adults} adult${adults !== 1 ? "s" : ""} · ${children} children · ${rooms} room${rooms !== 1 ? "s" : ""}`;
});

function increment(key: keyof GuestInfoFilter) {
  model.value[key]++;
}

function decrement(key: keyof GuestInfoFilter) {
  if (key === "adults" && model.value.adults <= 1) return;
  if (key === "rooms" && model.value.rooms <= 1) return;
  if (key === "children" && model.value.children <= 0) return;

  model.value[key]--;
}
</script>

<template>
  <UPopover v-model:open="isOpen" :content="{ align: 'start' }">
    <UInput
      :model-value="label"
      icon="i-heroicons-user-group"
      size="xl"
      readonly
      class="w-full cursor-pointer"
      :ui="{
        leadingIcon: 'text-indigo-400',
        root: 'w-full',
        base: 'cursor-pointer text-left'
      }"
    />

    <template #content>
      <div class="w-80 rounded-xl bg-white p-5 shadow-xl">
        <div class="space-y-4">
          <div class="flex items-center justify-between gap-4">
            <div>
              <p class="font-medium text-slate-900">Adults</p>
              <p class="text-sm text-slate-500">Ages 13 or above</p>
            </div>

            <div class="flex items-center gap-4 rounded-lg border border-slate-300 px-3 py-2">
              <UButton
                icon="i-lucide-minus"
                color="primary"
                variant="ghost"
                size="sm"
                :disabled="model.adults <= 1"
                @click="decrement('adults')"
              />

              <span class="w-6 text-center font-medium">
                {{ model.adults }}
              </span>

              <UButton
                icon="i-lucide-plus"
                color="primary"
                variant="ghost"
                size="sm"
                @click="increment('adults')"
              />
            </div>
          </div>

          <div class="flex items-center justify-between gap-4">
            <div>
              <p class="font-medium text-slate-900">Children</p>
              <p class="text-sm text-slate-500">Ages 2–12</p>
            </div>

            <div class="flex items-center gap-4 rounded-lg border border-slate-300 px-3 py-2">
              <UButton
                icon="i-lucide-minus"
                color="primary"
                variant="ghost"
                size="sm"
                :disabled="model.children <= 0"
                @click="decrement('children')"
              />

              <span class="w-6 text-center font-medium">
                {{ model.children }}
              </span>

              <UButton
                icon="i-lucide-plus"
                color="primary"
                variant="ghost"
                size="sm"
                @click="increment('children')"
              />
            </div>
          </div>

          <div class="flex items-center justify-between gap-4">
            <div>
              <p class="font-medium text-slate-900">Rooms</p>
              <p class="text-sm text-slate-500">Separate bedrooms</p>
            </div>

            <div class="flex items-center gap-4 rounded-lg border border-slate-300 px-3 py-2">
              <UButton
                icon="i-lucide-minus"
                color="primary"
                variant="ghost"
                size="sm"
                :disabled="model.rooms <= 1"
                @click="decrement('rooms')"
              />

              <span class="w-6 text-center font-medium">
                {{ model.rooms }}
              </span>

              <UButton
                icon="i-lucide-plus"
                color="primary"
                variant="ghost"
                size="sm"
                @click="increment('rooms')"
              />
            </div>
          </div>
        </div>

        <div class="mt-5 border-t border-slate-200 pt-4">
          <UButton
            label="Done"
            block
            variant="outline"
            color="primary"
            @click="isOpen = false"
          />
        </div>
      </div>
    </template>
  </UPopover>
</template>