export default defineNuxtRouteMiddleware(async () => {
  if (import.meta.server) return

  const authStore = useAuthStore()

  if (!authStore.user) {
    await authStore.fetchUser()
  }

  if (!authStore.user) {
    return navigateTo('/auth/signin')
  }

  if (
    authStore.user.role !== 'SELLER' &&
    authStore.user.role !== 'ADMIN'
  ) {
    return navigateTo('/')
  }
})