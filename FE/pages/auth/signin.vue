<template>
  <div
    class="min-h-screen flex items-center justify-center bg-gradient-to-br from-indigo-50 via-white to-slate-100">
    <UCard class="max-w-sm w-full bg-white shadow-2xl border-0">
      <div class="space-y-6">
        <div class="text-center">
          <div
            class="mx-auto h-16 w-16 bg-indigo-100 rounded-lg mb-4 flex items-center justify-center">
            <UIcon name="i-lucide-building-2" class="w-8 h-8 text-indigo-600" />
          </div>

          <h1 class="text-2xl font-bold text-slate-900">Welcome back</h1>
          <p class="text-sm text-slate-500">Enter your details to sign in.</p>
        </div>

        <div class="grid grid-cols-3 gap-3">
          <UButton
            icon="i-simple-icons-apple"
            aria-label="Apple"
            size="lg"
            variant="outline"
            class="w-full justify-center text-slate-600 border-slate-300 hover:bg-slate-50" />
          <UButton
            icon="i-simple-icons-google"
            aria-label="Google"
            size="lg"
            variant="outline"
            class="w-full justify-center text-slate-600 border-slate-300 hover:bg-slate-50" />
          <UButton
            icon="i-simple-icons-twitter"
            aria-label="Twitter"
            size="lg"
            variant="outline"
            class="w-full justify-center text-slate-600 border-slate-300 hover:bg-slate-50" />
        </div>

        <UForm :state="state" @submit="onSubmitLogin" class="space-y-4">
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

          <UButton
            type="submit"
            label="Sign in"
            size="lg"
            block
            class="!mt-6 bg-indigo-600 hover:bg-indigo-700 text-white font-bold" />
        </UForm>

        <p class="text-sm text-center text-slate-500 -mb-[2px]">
          Don't have an account yet?
          <ULink
            to="/auth/signup"
            class="font-semibold text-indigo-600 hover:text-indigo-700">
            Sign up
          </ULink>
        </p>

        <p class="text-sm text-center text-slate-500">
          Want to go back?
          <ULink
            to="/"
            class="font-semibold text-indigo-600 hover:text-indigo-700">
            Home page
          </ULink>
        </p>
      </div>
    </UCard>
  </div>
</template>

<script setup lang="ts">
import * as v from "valibot";
import { reactive } from "vue";
import type { FormSubmitEvent } from "#ui/types";
import { useAuthStore } from "~/stores/auth";
const config = useRuntimeConfig();

definePageMeta({
layout: false
  
})

const schema = v.object({
  email: v.pipe(
    v.string(),
    v.email("Invalid email"),
    v.nonEmpty("Please enter your email"),
  ),
  password: v.pipe(
    v.string(),
    v.minLength(8, "Must be at least 8 characters long."),
    v.nonEmpty("Please enter your password"),
  ),
});

type Schema = v.InferOutput<typeof schema>;

const toast = useToast();
async function onSubmitLogin(event: FormSubmitEvent<Schema>) {
  const auth = useAuthStore();

  try {
    const { email, password } = event.data;

    await $fetch(`${config.public.apiBase}/users/login`, {
      method: "POST",
      body: { email, password },
      credentials: "include",
    });

    await auth.fetchUser();

    toast.add({
      title: "Success",
      description: "Logged in successfully!",
      color: "success",
    });

    if (auth.user?.role === "ADMIN") {
      await navigateTo("/admin");
    } else {
      await navigateTo("/");
    }
  } catch (err: any) {
    toast.add({
      title: "Login failed",
      description: err?.data?.message || "Invalid email or password",
      color: "error",
    });
  }
}

const state = reactive({
  email: "",
  password: "",
});
</script>
