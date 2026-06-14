<script setup lang="ts">
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
    <div class="mb-8 flex items-center justify-between">
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

      <div v-else class="divide-y divide-slate-200">
        <div
          v-for="request in requests"
          :key="request.id"
          class="py-5"
        >
          <div class="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
            <div class="space-y-2">
              <div>
                <h2 class="font-semibold text-slate-900">
                  {{ request.userName }} {{ request.userSurname }}
                </h2>

                <p class="text-sm text-slate-500">
                  {{ request.userEmail }}
                </p>
              </div>

              <div class="text-sm text-slate-600">
                <p>
                  <span class="font-medium">Business:</span>
                  {{ request.businessName }}
                </p>

                <p>
                  <span class="font-medium">OIB:</span>
                  {{ request.oib }}
                </p>

                <p>
                  <span class="font-medium">IBAN:</span>
                  {{ request.iban }}
                </p>

                <p>
                  <span class="font-medium">Billing Address:</span>
                  {{ request.billingAddress }}
                </p>
              </div>

              <p class="max-w-2xl text-sm text-slate-600">
                <span class="font-medium">Message:</span>
                {{ request.requestText }}
              </p>

              <UBadge
                :label="request.status"
                :color="request.status === 'APPROVED' ? 'success' : request.status === 'REJECTED' ? 'error' : 'warning'"
                variant="soft"
              />
            </div>

            <div class="flex gap-2">
              <UButton
                label="Approve"
                icon="i-lucide-check"
                color="success"
                variant="soft"
                :disabled="request.status !== 'PENDING'"
                @click="approveRequest(request.id)"
              />

              <UButton
                label="Reject"
                icon="i-lucide-x"
                color="error"
                variant="soft"
                :disabled="request.status !== 'PENDING'"
                @click="rejectRequest(request.id)"
              />
            </div>
          </div>
        </div>
      </div>
    </UCard>
  </div>
</template>