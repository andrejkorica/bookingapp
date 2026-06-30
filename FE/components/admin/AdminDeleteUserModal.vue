<script setup lang="ts">
import type { User } from '~/types/UserTypes';

const open = defineModel<boolean>('open', {
  default: false
})

const props = defineProps<{
  user: User | null
}>()

const emit = defineEmits<{
  deleted: [userId: number]
}>()

const config = useRuntimeConfig()
const toast = useToast()

const isDeleting = ref(false)

async function confirmDeleteUser() {
  if (!props.user) return

  isDeleting.value = true

  try {
    await $fetch(`${config.public.apiBase}/admin/users/${props.user.id}`, {
      method: 'DELETE',
      credentials: 'include'
    })

    emit('deleted', props.user.id)

    toast.add({
      title: 'Deleted',
      description: 'User deleted successfully.',
      color: 'success'
    })

    open.value = false
  } catch (error) {
    console.error(error)

    toast.add({
      title: 'Error',
      description: 'Failed to delete user.',
      color: 'error'
    })
  } finally {
    isDeleting.value = false
  }
}
</script>

<template>
  <UModal
    v-model:open="open"
    :ui="{
      content: 'max-w-md w-full max-h-[90vh] overflow-y-auto'
    }"
  >
    <template #content>
      <UCard>
        <template #header>
          <h3 class="text-lg font-semibold">
            Delete User
          </h3>
        </template>

        <div class="space-y-4">
          <p class="text-sm text-slate-600">
            Are you sure you want to delete
            <span class="font-semibold">
              {{ user?.name }} {{ user?.surname }}
            </span>?
            This action cannot be undone.
          </p>

          <div class="flex justify-end gap-2">
            <UButton
              label="Cancel"
              variant="soft"
              color="neutral"
              @click="open = false"
            />

            <UButton
              label="Delete"
              color="error"
              :loading="isDeleting"
              @click="confirmDeleteUser"
            />
          </div>
        </div>
      </UCard>
    </template>
  </UModal>
</template>