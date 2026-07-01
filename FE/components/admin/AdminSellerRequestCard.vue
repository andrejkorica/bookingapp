<script setup lang="ts">
import type { SellerRequest } from "~/types/SellerTypes";

const props = defineProps<{
  request: SellerRequest;
}>();

const emit = defineEmits<{
  approve: [id: number];
  reject: [id: number];
}>();

const canReview = computed(() => {
  return (
    props.request.status === "PENDING" || props.request.status === "UPDATE"
  );
});

const statusColor = computed(() => {
  if (props.request.status === "APPROVED") return "success";
  if (props.request.status === "REJECTED") return "error";
  if (props.request.status === "UPDATE") return "info";

  return "warning";
});
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

        <UBadge :label="request.status" :color="statusColor" variant="soft" />
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
        <p class="font-medium text-slate-800">Message:</p>

        <p class="mt-1 whitespace-pre-line">
          {{ request.requestText }}
        </p>
      </div>

      <p class="text-xs text-slate-400">Created: {{ request.createdAt }}</p>
    </div>

    <template #footer>
      <div class="flex justify-end gap-2">
        <UButton
          label="Approve"
          icon="i-lucide-check"
          :color="canReview ? 'success' : 'neutral'"
          variant="soft"
          :disabled="!canReview"
          :class="!canReview ? 'opacity-50' : ''"
          @click="emit('approve', request.id)"
        />

        <UButton
          label="Reject"
          icon="i-lucide-x"
          :color="canReview ? 'error' : 'neutral'"
          variant="soft"
          :disabled="!canReview"
          :class="!canReview ? 'opacity-50' : ''"
          @click="emit('reject', request.id)"
        />
      </div>
    </template>
  </UCard>
</template>
