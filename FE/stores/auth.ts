import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null as {
      email: string
      name: string
      surname: string
      role: string
      phoneNumber: string
      profileImageUrl?: string
    } | null
  }),

  actions: {
    initAuth() {
      if (!import.meta.client) return

      const storedUser = localStorage.getItem('auth_user')

      if (storedUser) {
        try {
          this.user = JSON.parse(storedUser)
        } catch (e) {
          localStorage.removeItem('auth_user')
          this.user = null
        }
      }
    },

    async fetchUser() {
      const config = useRuntimeConfig()

      try {
        const data = await $fetch<{
          email: string
          name: string
          surname: string
          role: string
          phoneNumber: string
          profileImageUrl?: string
        }>(
          `${config.public.apiBase}/users/me`,
          {
            credentials: 'include'
          }
        )

        this.user = data

        if (import.meta.client) {
          localStorage.setItem('auth_user', JSON.stringify(data))
        }
      } catch (err) {
        this.user = null

        if (import.meta.client) {
          localStorage.removeItem('auth_user')
        }
      }
    },

    async logout() {
      const config = useRuntimeConfig()

      try {
        await $fetch(
          `${config.public.apiBase}/users/logout`,
          {
            method: 'POST',
            credentials: 'include'
          }
        )
      } finally {
        this.user = null

        if (import.meta.client) {
          localStorage.removeItem('auth_user')
        }
      }
    }
  }
})