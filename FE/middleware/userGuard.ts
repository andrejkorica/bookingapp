import { useAuthStore } from "~/stores/auth";

export default defineNuxtRouteMiddleware(async () => {
  const authStore = useAuthStore();

  if (!authStore.user) {
    await authStore.fetchUser();
  }

  if (!authStore.user) {
    return navigateTo("/auth/signin");
  }
});