<script setup lang="ts">
import { useRouter } from 'vue-router';
import VButton from '@/components/common/VButton.vue';

const router = useRouter();

const goHome = () => {
  router.push('/');
};

const goBack = () => {
  router.back();
};
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 px-4">
    <div class="max-w-md w-full text-center">
      <!-- 404 Illustration -->
      <div class="mb-8">
        <h1 class="text-9xl font-bold text-blue-600 mb-4">404</h1>
        <div class="flex justify-center mb-6">
          <svg class="w-32 h-32 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M12 12h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
        </div>
      </div>

      <!-- Message -->
      <h2 class="text-3xl font-bold text-gray-900 mb-4">
        Page Not Found
      </h2>
      <p class="text-gray-600 mb-8">
        Sorry, we couldn't find the page you're looking for. The page might have been moved or doesn't exist.
      </p>

      <!-- Actions -->
      <div class="flex flex-col sm:flex-row gap-4 justify-center">
        <VButton
          variant="secondary"
          @click="goBack"
          class="w-full sm:w-auto"
        >
          <svg class="w-4 h-4 mr-2 inline" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          Go Back
        </VButton>
        <VButton
          variant="primary"
          @click="goHome"
          class="w-full sm:w-auto"
        >
          <svg class="w-4 h-4 mr-2 inline" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"
            />
          </svg>
          Go Home
        </VButton>
      </div>

      <!-- Help Links -->
      <div class="mt-12 pt-8 border-t border-gray-200">
        <p class="text-sm text-gray-600 mb-4">Looking for something specific?</p>
        <div class="flex flex-wrap justify-center gap-4 text-sm">
          <button
            @click="router.push('/insurance-plans')"
            class="text-blue-600 hover:text-blue-800 hover:underline"
          >
            Insurance Plans
          </button>
          <button
            @click="router.push('/policies')"
            class="text-blue-600 hover:text-blue-800 hover:underline"
          >
            Policies
          </button>
          <button
            @click="router.push('/claims')"
            class="text-blue-600 hover:text-blue-800 hover:underline"
          >
            Claims
          </button>
          <button
            @click="router.push('/statistics')"
            class="text-blue-600 hover:text-blue-800 hover:underline"
          >
            Statistics
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
