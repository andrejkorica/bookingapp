<template>
  <div
    class="min-h-screen flex items-center justify-center bg-gradient-to-br from-indigo-50 via-white to-slate-100 py-12">
    <UCard class="max-w-md w-full bg-white shadow-2xl border-0">
      <div class="space-y-6">
        <div class="text-center">
          <div
            class="mx-auto h-16 w-16 bg-indigo-100 rounded-lg mb-4 flex items-center justify-center">
            <UIcon name="i-lucide-user-plus" class="w-8 h-8 text-indigo-600" />
          </div>
          <h1 class="text-2xl font-bold text-slate-900">Create an account</h1>
          <p class="text-sm text-slate-500">Enter your details to sign up.</p>
        </div>

        <UForm :state="state" @submit="onSubmit" class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <UFormField name="name">
              <template #label><span class="sr-only">Name</span></template>
              <UInput
                v-model="state.name"
                placeholder="Name"
                size="lg"
                class="w-full" />
            </UFormField>

            <UFormField name="surname">
              <template #label><span class="sr-only">Last name</span></template>
              <UInput
                v-model="state.surname"
                placeholder="Last name"
                size="lg"
                class="w-full" />
            </UFormField>
          </div>

          <UFormField name="phoneNumber">
            <template #label>
              <span class="sr-only">Phone Number</span>
            </template>

            <UInput
              v-model="state.phoneNumber"
              placeholder="Phone Number"
              size="lg"
              class="w-full" />
          </UFormField>

          <UFormField name="email">
            <template #label><span class="sr-only">Email</span></template>
            <UInput
              v-model="state.email"
              placeholder="Enter your email..."
              size="lg"
              class="w-full" />
          </UFormField>

          <UFormField name="password">
            <template #label><span class="sr-only">Password</span></template>
            <UInput
              v-model="state.password"
              type="password"
              placeholder="Password"
              size="lg"
              class="w-full" />
          </UFormField>

          <UFormField name="passwordConfirm">
            <template #label
              ><span class="sr-only">Confirm Password</span></template
            >
            <UInput
              v-model="state.passwordConfirm"
              type="password"
              placeholder="Confirm Password"
              size="lg"
              class="w-full" />
          </UFormField>

          <UButton
            type="submit"
            label="Sign Up"
            size="lg"
            block
            class="mt-6 bg-indigo-600 hover:bg-indigo-700 text-white font-bold" />
        </UForm>

        <p class="text-sm text-center text-slate-500">
          Already have an account?
          <ULink
            to="/auth/signin"
            class="font-semibold text-indigo-600 hover:text-indigo-700">
            Sign in
          </ULink>
        </p>
      </div>
    </UCard>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import type { FormSubmitEvent } from "#ui/types";
import { useAuthStore } from "~/stores/auth";

definePageMeta({
  layout: "auth",
});

const config = useRuntimeConfig();
const toast = useToast();
const auth = useAuthStore();

const state = reactive({
  name: "",
  surname: "",
  phoneNumber: "",
  email: "",
  password: "",
  passwordConfirm: "",
});

async function onSubmit(event: FormSubmitEvent<any>) {
  if (event.data.password !== event.data.passwordConfirm) {
    toast.add({
      title: "Error",
      description: "Passwords do not match",
      color: "error",
    });
    return;
  }

  try {
    await $fetch(`${config.public.apiBase}/users/register`, {
      method: "POST",
      body: {
        name: event.data.name,
        surname: event.data.surname,
        phoneNumber: event.data.phoneNumber,
        email: event.data.email,
        password: event.data.password,
      },
      credentials: "include",
    });

    await auth.fetchUser();

    toast.add({
      title: "Success",
      description: "Account created successfully!",
      color: "success",
    });

    await navigateTo("/");
  } catch (err: any) {
    toast.add({
      title: "Registration failed",
      description: err?.data?.message || "Unable to create account",
      color: "error",
    });
  }
}
</script>
