<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useClaimStore } from '@/stores/claim.store';
import VProcessClaimModal from '@/components/claim/VProcessClaimModal.vue';
import type { ProcessClaimRequest } from '@/interfaces/claim.interface';

// Router
const router = useRouter();
const route = useRoute();

// Store
const claimStore = useClaimStore();

// State
const isModalOpen = ref(false);
const claimId = ref<string>('');

// Handle process
const handleProcess = async (data: ProcessClaimRequest) => {
  await claimStore.processClaim(claimId.value, data);
  
  // Navigate back to claims list
  await router.push('/claims');
};

// Handle close
const handleClose = () => {
  router.push('/claims');
};

// Lifecycle
onMounted(async () => {
  claimId.value = route.params.id as string;
  
  // Fetch claim detail
  await claimStore.fetchClaimById(claimId.value);
  
  // Auto-open modal
  isModalOpen.value = true;
});
</script>

<template>
  <div class="container mx-auto px-4 py-8">
    <!-- Process Modal -->
    <VProcessClaimModal
      v-if="claimStore.currentClaim"
      :isOpen="isModalOpen"
      :claim="claimStore.currentClaim"
      @close="handleClose"
      @process="handleProcess"
    />

    <!-- Loading State -->
    <div v-if="claimStore.loading" class="flex items-center justify-center py-12">
      <div class="flex flex-col items-center gap-4">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <p class="text-gray-600">Loading claim details...</p>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="claimStore.error" class="bg-red-50 border border-red-200 rounded-lg p-6">
      <div class="flex items-start gap-3">
        <svg class="w-6 h-6 text-red-600 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
          />
        </svg>
        <div>
          <h3 class="text-red-900 font-semibold">Error Loading Claim</h3>
          <p class="text-red-700 text-sm mt-1">{{ claimStore.error }}</p>
          <button
            @click="router.push('/claims')"
            class="mt-3 text-sm text-red-800 underline hover:text-red-900"
          >
            Back to Claims List
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
