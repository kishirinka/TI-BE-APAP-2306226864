<script setup lang="ts">
import { RouterView } from 'vue-router'
import VNavbar from './components/layout/VNavbar.vue'
import { Toaster } from 'vue-sonner'
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <VNavbar />
    <main class="container mx-auto px-4 py-8">
      <RouterView />
    </main>
    <Toaster position="top-right" richColors />
  </div>
</template>