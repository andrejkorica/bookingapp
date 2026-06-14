<script setup lang="ts">
 import adminNavbar from '~/components/admin/adminNavbar.vue';
</script>

<template>
  <div>
    <adminNavbar />

    <main class="min-h-screen bg-slate-50">
      <slot />
    </main>
  </div>
</template>