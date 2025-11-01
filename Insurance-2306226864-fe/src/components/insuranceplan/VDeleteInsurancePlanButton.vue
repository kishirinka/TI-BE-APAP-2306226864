<script setup lang="ts">
import { ref } from 'vue';
import VModal from '@/components/common/VModal.vue';
import { useInsurancePlanStore } from '@/stores/insurancePlan/insurancePlan.store';

interface Props {
  planId: string;
  planName: string;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  deleted: [];
}>();

const insurancePlanStore = useInsurancePlanStore();
const isModalOpen = ref(false);
const isDeleting = ref(false);

const openModal = () => {
  isModalOpen.value = true;
};

const closeModal = () => {
  if (!isDeleting.value) {
    isModalOpen.value = false;
  }
};

const handleConfirmDelete = async () => {
  isDeleting.value = true;
  
  try {
    const result = await insurancePlanStore.deleteInsurancePlan(props.planId);
    
    if (result.success) {
      // Emit deleted event first before closing modal
      emit('deleted');
      // Small delay to ensure event is processed
      await new Promise(resolve => setTimeout(resolve, 100));
      closeModal();
    }
  } finally {
    isDeleting.value = false;
  }
};
</script>

<template>
  <div>
    <!-- Delete Button -->
    <button
      @click="openModal"
      type="button"
      class="px-4 py-2 text-sm font-medium text-white bg-red-600 border border-transparent rounded-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 disabled:bg-red-300 disabled:cursor-not-allowed transition-colors"
      :disabled="isDeleting || insurancePlanStore.loading"
    >
      <span v-if="!isDeleting">Delete</span>
      <span v-else class="flex items-center">
        <svg
          class="w-4 h-4 mr-2 animate-spin"
          fill="none"
          viewBox="0 0 24 24"
        >
          <circle
            class="opacity-25"
            cx="12"
            cy="12"
            r="10"
            stroke="currentColor"
            stroke-width="4"
          />
          <path
            class="opacity-75"
            fill="currentColor"
            d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
          />
        </svg>
        Deleting...
      </span>
    </button>

    <!-- Confirmation Modal -->
    <VModal
      :isOpen="isModalOpen"
      title="Delete Insurance Plan"
      :confirmText="isDeleting ? 'Deleting...' : 'Delete'"
      :confirmDisabled="isDeleting"
      cancelText="Cancel"
      @confirm="handleConfirmDelete"
      @cancel="closeModal"
    >
      <div class="space-y-4">
        <!-- Warning Icon -->
        <div class="flex items-center justify-center w-12 h-12 mx-auto bg-red-100 rounded-full">
          <svg
            class="w-6 h-6 text-red-600"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
            />
          </svg>
        </div>

        <!-- Confirmation Message -->
        <div class="text-center">
          <p class="text-sm text-gray-600">
            Are you sure you want to delete
          </p>
          <p class="mt-1 text-lg font-semibold text-gray-900">
            "{{ planName }}"?
          </p>
          <p class="mt-3 text-sm text-gray-500">
            This action cannot be undone. All data associated with this insurance plan will be permanently deleted.
          </p>
        </div>

        <!-- Loading Indicator -->
        <div v-if="isDeleting" class="flex items-center justify-center py-2">
          <svg
            class="w-5 h-5 text-red-600 animate-spin"
            fill="none"
            viewBox="0 0 24 24"
          >
            <circle
              class="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              stroke-width="4"
            />
            <path
              class="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
            />
          </svg>
          <span class="ml-2 text-sm text-gray-600">Deleting insurance plan...</span>
        </div>

        <!-- Error Message -->
        <div v-if="insurancePlanStore.error && !isDeleting" class="p-3 bg-red-50 border border-red-200 rounded-md">
          <p class="text-sm text-red-800">
            {{ insurancePlanStore.error }}
          </p>
        </div>
      </div>
    </VModal>
  </div>
</template>

<style scoped>
/* Animation for spinner */
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.animate-spin {
  animation: spin 1s linear infinite;
}
</style>

