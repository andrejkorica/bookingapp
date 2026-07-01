<script setup lang="ts">
import { useAuthStore } from "~/stores/auth";
import defaultAvatar from "~/public/images/default-avatar.png";

const config = useRuntimeConfig();
const toast = useToast();
const authStore = useAuthStore();

const isSavingProfile = ref(false);
const isUploadingImage = ref(false);

const form = reactive({
  name: "",
  surname: "",
  phoneNumber: "",
  email: "",
});

const selectedImage = ref<string>(defaultAvatar);
const selectedFile = ref<File | null>(null);
const fileInput = ref<HTMLInputElement | null>(null);

function openFilePicker() {
  if (isUploadingImage.value) return;
  fileInput.value?.click();
}

function onFileSelected(event: Event) {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];

  if (!file) return;

  selectedFile.value = file;
  selectedImage.value = URL.createObjectURL(file);
}

watch(
  () => authStore.user,
  (user) => {
    if (!user) return;

    form.name = user.name;
    form.surname = user.surname;
    form.phoneNumber = user.phoneNumber;
    form.email = user.email;

    selectedImage.value = user.profileImageUrl || defaultAvatar;
  },
  { immediate: true },
);

async function uploadProfileImage() {
  if (!selectedFile.value) return null;

  isUploadingImage.value = true;

  try {
    const formData = new FormData();
    formData.append("file", selectedFile.value);

    const response = await $fetch<{ imageUrl: string }>(
      `${config.public.apiBase}/users/profile-image`,
      {
        method: "POST",
        credentials: "include",
        body: formData,
      },
    );

    selectedFile.value = null;
    selectedImage.value = response.imageUrl;

    return response.imageUrl;
  } finally {
    isUploadingImage.value = false;
  }
}

async function saveProfile() {
  if (!authStore.user) return;

  isSavingProfile.value = true;

  try {
    const uploadedImageUrl = await uploadProfileImage();

    const updatedUser = await $fetch<{
      email: string;
      name: string;
      surname: string;
      phoneNumber: string;
      role: string;
      profileImageUrl?: string;
    }>(`${config.public.apiBase}/users/update`, {
      method: "POST",
      credentials: "include",
      body: {
        name: form.name,
        surname: form.surname,
        phoneNumber: form.phoneNumber,
        email: form.email,
      },
    });

    authStore.user = {
      ...authStore.user,
      ...updatedUser,
      ...(uploadedImageUrl ? { profileImageUrl: uploadedImageUrl } : {}),
    };

    localStorage.setItem("auth_user", JSON.stringify(authStore.user));

    toast.add({
      title: "Success",
      description: "Profile updated successfully!",
      color: "success",
    });
  } catch (error) {
    console.error("Failed to update profile:", error);

    toast.add({
      title: "Error",
      description: "Failed to update profile.",
      color: "error",
    });
  } finally {
    isSavingProfile.value = false;
  }
}
</script>

<template>
  <div class="space-y-6">
    <div class="text-center">
      <h2 class="text-xl font-semibold">Profile Settings</h2>

      <p class="mt-1 text-sm text-slate-500">
        Manage your account information.
      </p>
    </div>

    <div class="flex flex-col items-center gap-4 text-center">
      <div class="group relative cursor-pointer" @click="openFilePicker">
        <img
          :src="selectedImage"
          class="h-24 w-24 rounded-full border object-cover transition group-hover:brightness-75"
          :class="{ 'brightness-75': isUploadingImage }"
        />

        <div
          v-if="isUploadingImage"
          class="absolute inset-0 flex items-center justify-center rounded-full bg-black/40"
        >
          <UIcon
            name="i-lucide-loader-circle"
            class="h-7 w-7 animate-spin text-white"
          />
        </div>

        <div
          v-else
          class="absolute inset-0 flex items-center justify-center rounded-full bg-black/40 opacity-0 transition group-hover:opacity-100"
        >
          <UIcon name="i-heroicons-camera" class="h-6 w-6 text-white" />
        </div>
      </div>

      <input
        ref="fileInput"
        type="file"
        accept="image/*"
        class="hidden"
        @change="onFileSelected"
      />

      <div class="text-sm text-slate-600">
        <p>
          <span class="font-medium">Role:</span>
          {{ authStore.user?.role || "USER" }}
        </p>

        <p>
          <span class="font-medium">Email verified:</span>
          Yes
        </p>
      </div>
    </div>

    <div class="mx-auto max-w-lg">
      <UCard>
        <template #header>
          <h3 class="font-semibold">Personal Information</h3>
        </template>

        <div class="space-y-4">
          <UFormField label="First Name">
            <UInput v-model="form.name" placeholder="Enter your first name" />
          </UFormField>

          <UFormField label="Last Name">
            <UInput v-model="form.surname" placeholder="Enter your last name" />
          </UFormField>

          <UFormField label="Phone Number">
            <UInput
              v-model="form.phoneNumber"
              placeholder="Enter your phone number"
            />
          </UFormField>

          <UFormField label="Email Address">
            <UInput v-model="form.email" placeholder="Enter your email" />
          </UFormField>

          <div class="pt-2">
            <UButton
              label="Save Profile"
              :loading="isSavingProfile"
              class="bg-indigo-600 text-white hover:bg-indigo-700"
              @click="saveProfile"
            />
          </div>
        </div>
      </UCard>
    </div>
  </div>
</template>
