<script setup lang="ts">
import type { SellerRequest } from '~/types/SellerTypes';

defineProps<{
  request: SellerRequest
}>()

const emit = defineEmits<{
  approve: [id: number]
  reject: [id: number]
}>()
</script>

<template>
  <UCard class="h-[520px]">
    <template #header>
      <div class="flex items-start justify-between gap-3">
        <div>
          <h2 class="font-semibold text-slate-900">
            {{ request.userName }} {{ request.userSurname }}
          </h2>

          <p class="text-sm text-slate-500">
            {{ request.userEmail }}
          </p>
        </div>

        <UBadge
          :label="request.status"
          :color="
            request.status === 'APPROVED'
              ? 'success'
              : request.status === 'REJECTED'
                ? 'error'
                : 'warning'
          "
          variant="soft"
        />
      </div>
    </template>

    <div
      class="flex h-[340px] flex-col gap-4 overflow-y-auto pr-2 text-sm text-slate-600"
    >
      <div class="space-y-2">
        <p>
          <span class="font-medium text-slate-800">Business:</span>
          {{ request.businessName }}
        </p>

        <p>
          <span class="font-medium text-slate-800">OIB:</span>
          {{ request.oib }}
        </p>

        <p>
          <span class="font-medium text-slate-800">IBAN:</span>
          {{ request.iban }}
        </p>

        <p>
          <span class="font-medium text-slate-800">Billing Address:</span>
          {{ request.billingAddress }}
        </p>
      </div>

      <div>
        <p class="font-medium text-slate-800">
          Message:
        </p>

        <p class="mt-1 whitespace-pre-line">
          {{ request.requestText }}
        </p>
      </div>

      <p class="text-xs text-slate-400">
        Created: {{ request.createdAt }}
      </p>
    </div>

    <template #footer>
      <div class="flex justify-end gap-2">
        <UButton
          label="Approve"
          icon="i-lucide-check"
          :color="request.status === 'PENDING' ? 'success' : 'neutral'"
          variant="soft"
          :disabled="request.status !== 'PENDING'"
          :class="request.status !== 'PENDING' ? 'opacity-50' : ''"
          @click="emit('approve', request.id)"
        />

        <UButton
          label="Reject"
          icon="i-lucide-x"
          :color="request.status === 'PENDING' ? 'error' : 'neutral'"
          variant="soft"
          :disabled="request.status !== 'PENDING'"
          :class="request.status !== 'PENDING' ? 'opacity-50' : ''"
          @click="emit('reject', request.id)"
        />
      </div>
    </template>
  </UCard>
</template>