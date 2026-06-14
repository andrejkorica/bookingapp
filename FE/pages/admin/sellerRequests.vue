<script setup lang="ts">
import sellerRequestCard from '~/components/admin/sellerRequestCard.vue'

definePageMeta({
  layout: 'admin',
  middleware: 'admin-guard'
})

type SellerRequest = {
  id: number
  requestText: string
  status: string
  createdAt: string
  userName: string
  userSurname: string
  userEmail: string
  businessName: string
  oib: string
  iban: string
  billingAddress: string
}

const config = useRuntimeConfig()
const toast = useToast()

const requests = ref<SellerRequest[]>([])
const isLoading = ref(false)

async function fetchRequests() {
  isLoading.value = true

  try {
    requests.value = await $fetch<SellerRequest[]>(
      `${config.public.apiBase}/admin/seller-requests`,
      {
        credentials: 'include'
      }
    )
  } catch (error) {
    console.error(error)

    toast.add({
      title: 'Error',
      description: 'Failed to load seller requests.',
      color: 'error'
    })
  } finally {
    isLoading.value = false
  }
}

async function approveRequest(requestId: number) {
  try {
    const updatedRequest = await $fetch<SellerRequest>(
      `${config.public.apiBase}/admin/seller-requests/${requestId}/approve`,
      {
        method: 'POST',
        credentials: 'include'
      }
    )

    requests.value = requests.value.map(request =>
      request.id === updatedRequest.id ? updatedRequest : request
    )

    toast.add({
      title: 'Approved',
      description: 'Seller request approved successfully.',
      color: 'success'
    })
  } catch (error) {
    console.error(error)

    toast.add({
      title: 'Error',
      description: 'Failed to approve seller request.',
      color: 'error'
    })
  }
}

async function rejectRequest(requestId: number) {
  try {
    const updatedRequest = await $fetch<SellerRequest>(
      `${config.public.apiBase}/admin/seller-requests/${requestId}/reject`,
      {
        method: 'POST',
        credentials: 'include'
      }
    )

    requests.value = requests.value.map(request =>
      request.id === updatedRequest.id ? updatedRequest : request
    )

    toast.add({
      title: 'Rejected',
      description: 'Seller request rejected.',
      color: 'success'
    })
  } catch (error) {
    console.error(error)

    toast.add({
      title: 'Error',
      description: 'Failed to reject seller request.',
      color: 'error'
    })
  }
}

onMounted(fetchRequests)
</script>

<template>
  <div class="container mx-auto px-4 py-10">
    <div class="mb-8 flex items-center justify-between gap-4">
      <div>
        <h1 class="text-3xl font-bold text-slate-900">
          Seller Requests
        </h1>

        <p class="mt-2 text-slate-600">
          Review and approve users who requested to become sellers.
        </p>
      </div>

      <UButton
        label="Back to Dashboard"
        icon="i-lucide-arrow-left"
        variant="soft"
        color="neutral"
        to="/admin"
      />
    </div>

    <UCard>
      <div v-if="isLoading" class="py-10 text-center text-slate-500">
        Loading seller requests...
      </div>

      <div v-else-if="requests.length === 0" class="py-10 text-center text-slate-500">
        No seller requests found.
      </div>

      <div
        v-else
        class="grid grid-cols-1 gap-5 md:grid-cols-2 xl:grid-cols-3"
      >
        <sellerRequestCard
          v-for="request in requests"
          :key="request.id"
          :request="request"
          @approve="approveRequest"
          @reject="rejectRequest"
        />
      </div>
    </UCard>
  </div>
</template>