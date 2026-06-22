<script setup lang="ts">
const paymentInfo = defineModel<{
  cardholderName: string
  cardNumber: string
  expiryDate: string
  cvc: string
  billingAddress: string
  agreedToRules: boolean
  agreedToCancellationPolicy: boolean
  confirmedInfoCorrect: boolean
}>({
  required: true
})

const emit = defineEmits<{
  back: []
  confirm: []
}>()

const canConfirm = computed(() => {
  return Boolean(
    paymentInfo.value.cardholderName &&
    paymentInfo.value.cardNumber &&
    paymentInfo.value.expiryDate &&
    paymentInfo.value.cvc &&
    paymentInfo.value.agreedToRules &&
    paymentInfo.value.agreedToCancellationPolicy &&
    paymentInfo.value.confirmedInfoCorrect
  )
})
</script>

<template>
  <div class="space-y-6">
    <UCard>
      <template #header>
        <h2 class="text-xl font-semibold">
          Payment information
        </h2>
      </template>

      <div class="space-y-4">
        <UFormField label="Cardholder name" required>
          <UInput
            v-model="paymentInfo.cardholderName"
            placeholder="Name on card"
            class="w-full"
          />
        </UFormField>

        <UFormField label="Card number" required>
          <UInput
            v-model="paymentInfo.cardNumber"
            placeholder="1234 5678 9012 3456"
            class="w-full"
          />
        </UFormField>

        <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
          <UFormField label="Expiry date" required>
            <UInput
              v-model="paymentInfo.expiryDate"
              placeholder="MM/YY"
              class="w-full"
            />
          </UFormField>

          <UFormField label="CVC" required>
            <UInput
              v-model="paymentInfo.cvc"
              placeholder="123"
              class="w-full"
            />
          </UFormField>
        </div>

        <UFormField label="Billing address">
          <UTextarea
            v-model="paymentInfo.billingAddress"
            placeholder="Street, city, country..."
            :rows="3"
            class="w-full"
          />
        </UFormField>
      </div>
    </UCard>

    <UCard>
      <template #header>
        <h2 class="text-xl font-semibold">
          Confirmation
        </h2>
      </template>

      <div class="space-y-4">
        <UCheckbox
          v-model="paymentInfo.agreedToRules"
          label="I agree to the property rules."
        />

        <UCheckbox
          v-model="paymentInfo.agreedToCancellationPolicy"
          label="I understand the cancellation policy."
          
        />

        <UCheckbox
          v-model="paymentInfo.confirmedInfoCorrect"
          label="I confirm that my guest information is correct."
        />
      </div>
    </UCard>

    <div class="flex justify-between">
      <UButton
        label="Back"
        variant="ghost"
        color="neutral"
        @click="emit('back')"
      />

      <UButton
        label="Confirm booking"
        color="primary"
        class="font-semibold text-white"
        :disabled="!canConfirm"
        @click="emit('confirm')"
      />
    </div>
  </div>
</template>