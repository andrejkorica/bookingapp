<script setup lang="ts">
import { useAuthStore } from "~/stores/auth";

const config = useRuntimeConfig();
const toast = useToast();
const authStore = useAuthStore();

const isSavingProfile = ref(false);
const isSavingPayment = ref(false);

const form = reactive({
  name: "",
  surname: "",
  phoneNumber: "",
  email: "",
  cardholderName: "",
  cardNumber: "",
  expiryDate: "",
});

watch(
  () => authStore.user,
  (user) => {
    if (!user) return;

    form.name = user.name;
    form.surname = user.surname;
    form.email = user.email;
    form.phoneNumber = user.phoneNumber
  },
  { immediate: true },
);

async function saveProfile() {
  if (!authStore.user) return;

  isSavingProfile.value = true;

  try {
    const updatedUser = await $fetch<{
      email: string;
      name: string;
      surname: string;
      phoneNumber: string;
      role: string;
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

async function savePaymentMethod() {
  isSavingPayment.value = true;

  try {
    await $fetch(`${config.public.apiBase}/users/payment-method`, {
      method: "POST",
      credentials: "include",
      body: {
        cardholderName: form.cardholderName,
        cardNumber: form.cardNumber,
        expiryDate: form.expiryDate,
      },
    });

    toast.add({
      title: "Success",
      description: "Payment method updated successfully!",
      color: "success",
    });
  } catch (error) {
    console.error("Failed to update payment method:", error);

    toast.add({
      title: "Error",
      description: "Failed to update payment method.",
      color: "error",
    });
  } finally {
    isSavingPayment.value = false;
  }
}
</script>

<template>
  <div class="space-y-6">
    <div class="text-center">
      <h2 class="text-xl font-semibold">Profile Settings</h2>

      <p class="mt-1 text-sm text-slate-500">
        Manage your account information and payment details.
      </p>
    </div>

    <div class="flex flex-col items-center gap-4 text-center">
      <img
        src="https://i.pravatar.cc/150"
        class="h-20 w-20 rounded-full border object-cover" />

      <div class="text-sm text-slate-600">
        <p>
          <span class="font-medium">Role:</span>
          {{ authStore.user?.role || "user" }}
        </p>

        <p>
          <span class="font-medium">Email verified:</span>
          Yes
        </p>
      </div>
    </div>

    <div class="mx-auto max-w-lg space-y-5">
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
              placeholder="Enter your phone number" />
          </UFormField>

          <UFormField label="Email Address">
            <UInput v-model="form.email" placeholder="Enter your email" />
          </UFormField>

          <div class="pt-2">
            <UButton
              label="Save Profile"
              :loading="isSavingProfile"
              class="bg-indigo-600 text-white hover:bg-indigo-700"
              @click="saveProfile" />
          </div>
        </div>
      </UCard>

      <UCard>
        <template #header>
          <h3 class="font-semibold">Payment Method</h3>
        </template>

        <div class="space-y-4">
          <UFormField label="Cardholder Name">
            <UInput v-model="form.cardholderName" placeholder="John Smith" />
          </UFormField>

          <UFormField label="Card Number">
            <UInput
              v-model="form.cardNumber"
              placeholder="**** **** **** 1234" />
          </UFormField>

          <UFormField label="Expiry Date">
            <UInput v-model="form.expiryDate" placeholder="MM/YY" />
          </UFormField>

          <div class="pt-2">
            <UButton
              label="Save Payment Method"
              :loading="isSavingPayment"
              variant="soft"
              class="bg-indigo-600 text-white hover:bg-indigo-700"
              @click="savePaymentMethod" />
          </div>
        </div>
      </UCard>
    </div>
  </div>
</template>
