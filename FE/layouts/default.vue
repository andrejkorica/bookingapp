<script setup lang="ts">
import Navbar from "~/components/Navbar.vue";
</script>

<template>
  <div class="min-h-screen bg-slate-50">
    <Navbar />

    <main>
      <slot />
    </main>
  </div>
</template>