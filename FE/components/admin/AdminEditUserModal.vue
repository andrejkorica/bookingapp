<script setup lang="ts">
import type { User } from '~/types/UserTypes';

const open = defineModel<boolean>("open", {
  default: false,
});

const props = defineProps<{
  user: User | null;
}>();

const emit = defineEmits<{
  updated: [user: User];
}>();

const config = useRuntimeConfig();
const toast = useToast();

const isSaving = ref(false);

const form = reactive({
  name: "",
  surname: "",
  email: "",
  phoneNumber: "",
  role: "USER",
});

watch(
  () => props.user,
  (user) => {
    if (!user) return;

    form.name = user.name;
    form.surname = user.surname;
    form.email = user.email;
    form.phoneNumber = user.phoneNumber;
    form.role = user.role;
  },
  { immediate: true },
);

async function saveUserChanges() {
  if (!props.user) return;

  isSaving.value = true;

  try {
    const updatedUser = await $fetch<User>(
      `${config.public.apiBase}/admin/users/${props.user.id}`,
      {
        method: "POST",
        credentials: "include",
        body: {
          name: form.name,
          surname: form.surname,
          email: form.email,
          phoneNumber: form.phoneNumber,
          role: form.role,
        },
      },
    );

    emit("updated", updatedUser);

    toast.add({
      title: "Saved",
      description: "User updated successfully.",
      color: "success",
    });

    open.value = false;
  } catch (error) {
    console.error(error);

    toast.add({
      title: "Error",
      description: "Failed to update user.",
      color: "error",
    });
  } finally {
    isSaving.value = false;
  }
}
</script>

<template>
  <UModal
    v-model:open="open"
    :ui="{
      content: 'max-w-lg w-full overflow-hidden',
    }">
    <template #content>
      <div class="max-h-[90vh] overflow-y-auto p-4">
        <UCard>
          <template #header>
            <h3 class="text-lg font-semibold">Edit User</h3>
          </template>

          <div class="space-y-4">
            <UFormField label="First Name">
              <UInput v-model="form.name" placeholder="First name" />
            </UFormField>

            <UFormField label="Last Name">
              <UInput v-model="form.surname" placeholder="Last name" />
            </UFormField>

            <UFormField label="Email">
              <UInput v-model="form.email" placeholder="Email" />
            </UFormField>

            <UFormField label="Phone Number">
              <UInput v-model="form.phoneNumber" placeholder="Phone number" />
            </UFormField>

            <UFormField label="Role">
              <USelect
                v-model="form.role"
                :items="['USER', 'SELLER', 'ADMIN']" />
            </UFormField>

            <div class="flex justify-end gap-2 pt-2">
              <UButton
                label="Cancel"
                variant="soft"
                color="neutral"
                @click="open = false" />

              <UButton
                label="Save Changes"
                :loading="isSaving"
                class="bg-indigo-600 text-white hover:bg-indigo-700"
                @click="saveUserChanges" />
            </div>
          </div>
        </UCard>
      </div>
    </template>
  </UModal>
</template>
