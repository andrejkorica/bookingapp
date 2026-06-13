<script setup lang="ts">
import { useAuthStore } from '~/stores/auth'

const authStore = useAuthStore()
const config = useRuntimeConfig()
const toast = useToast()

const isSending = ref(false)

const form = reactive({
  businessName: '',
  oib: '',
  iban: '',
  address: '',
  requestText: ''
})

const isSeller = computed(() => authStore.user?.role === 'SELLER')

async function sendSellerRequest() {
  isSending.value = true

  try {
    await $fetch(`${config.public.apiBase}/seller-requests`, {
      method: 'POST',
      credentials: 'include',
      body: form
    })

    toast.add({
      title: 'Request sent',
      description: 'Your seller request has been sent for review.',
      color: 'success'
    })
  } catch (error) {
    console.error(error)

    toast.add({
      title: 'Error',
      description: 'Failed to send seller request.',
      color: 'error'
    })
  } finally {
    isSending.value = false
  }
}
</script>

<template>
  <div class="space-y-6">
    <template v-if="isSeller">
      <div class="text-center">
        <h2 class="text-xl font-semibold">
          Seller Info
        </h2>

        <p class="mt-1 text-sm text-slate-600">
          You are already registered as a seller.
        </p>
      </div>
    </template>

    <template v-else>
      <div class="text-center">
        <h2 class="text-xl font-semibold">
          Become a Seller
        </h2>

        <p class="mx-auto mt-1 max-w-md text-sm text-slate-600">
          Fill in your billing information and send a request to become a seller.
          An admin needs to approve your request first.
        </p>
      </div>

      <div class="mx-auto max-w-lg">
        <UCard>
          <template #header>
            <h3 class="font-semibold">
              Seller Request
            </h3>
          </template>

          <div class="space-y-4">
            <UFormField label="Business Name">
              <UInput
                v-model="form.businessName"
                placeholder="Enter your business name"
              />
            </UFormField>

            <UFormField label="OIB">
              <UInput
                v-model="form.oib"
                placeholder="Enter your OIB"
              />
            </UFormField>

            <UFormField label="IBAN">
              <UInput
                v-model="form.iban"
                placeholder="Enter your IBAN"
              />
            </UFormField>

            <UFormField label="Billing Address">
              <UInput
                v-model="form.address"
                placeholder="Enter your billing address"
              />
            </UFormField>

            <UFormField label="Request Message">
              <UTextarea
                v-model="form.requestText"
                placeholder="Tell us why you want to become a seller"
                :rows="4"
                class="w-full"
              />
            </UFormField>

            <div class="pt-2">
              <UButton
                label="Send Request"
                :loading="isSending"
                class="bg-indigo-600 text-white hover:bg-indigo-700"
                @click="sendSellerRequest"
              />
            </div>
          </div>
        </UCard>
      </div>
    </template>
  </div>
</template>