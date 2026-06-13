<script setup lang="ts">
definePageMeta({
  layout: 'admin',
  middleware: 'admin-guard'
})

type User = {
  id: number
  name: string
  surname: string
  email: string
  phoneNumber: string
  role: string
}

const config = useRuntimeConfig()
const toast = useToast()

const users = ref<User[]>([])
const isLoading = ref(false)

async function fetchUsers() {
  isLoading.value = true

  try {
    users.value = await $fetch<User[]>(`${config.public.apiBase}/admin/users`, {
      credentials: 'include'
    })
  } catch (error) {
    console.error(error)

    toast.add({
      title: 'Error',
      description: 'Failed to load users.',
      color: 'error'
    })
  } finally {
    isLoading.value = false
  }
}

async function deleteUser(userId: number) {
  try {
    await $fetch(`${config.public.apiBase}/admin/users/${userId}`, {
      method: 'DELETE',
      credentials: 'include'
    })

    users.value = users.value.filter(user => user.id !== userId)

    toast.add({
      title: 'Deleted',
      description: 'User deleted successfully.',
      color: 'success'
    })
  } catch (error) {
    console.error(error)

    toast.add({
      title: 'Error',
      description: 'Failed to delete user.',
      color: 'error'
    })
  }
}

onMounted(fetchUsers)
</script>

<template>
  <div class="container mx-auto px-4 py-10">
    <div class="mb-8 flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-bold text-slate-900">
          Registered Users
        </h1>

        <p class="mt-2 text-slate-600">
          View, edit, and delete users.
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
        Loading users...
      </div>

      <div v-else-if="users.length === 0" class="py-10 text-center text-slate-500">
        No users found.
      </div>

      <div v-else class="divide-y divide-slate-200">
        <div
          v-for="user in users"
          :key="user.id"
          class="flex items-center justify-between gap-4 py-4"
        >
          <div>
            <h2 class="font-semibold text-slate-900">
              {{ user.name }} {{ user.surname }}
            </h2>

            <p class="text-sm text-slate-500">
              {{ user.email }}
            </p>

            <p class="text-sm text-slate-500">
              {{ user.phoneNumber }} · {{ user.role }}
            </p>
          </div>

          <div class="flex items-center gap-2">
            <UButton
              icon="i-lucide-pencil"
              variant="soft"
              color="neutral"
              :to="`/admin/users/${user.id}/edit`"
            />

            <UButton
              icon="i-lucide-trash-2"
              variant="soft"
              color="error"
              @click="deleteUser(user.id)"
            />
          </div>
        </div>
      </div>
    </UCard>
  </div>
</template>