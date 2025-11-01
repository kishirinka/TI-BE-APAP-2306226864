<script setup lang="ts">
import { ref } from 'vue';
import VModal from '@/components/common/VModal.vue';
import { usePolicyStore } from '@/stores/policy/policy.store';

interface Props {
  policyId: string;
  totalPrice: number;
  status: string;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  paid: [];
}>();

const policyStore = usePolicyStore();
const isModalOpen = ref(false);
const isPaying = ref(false);

// Check if policy is already paid
const isAlreadyPaid = () => {
  return props.status === 'PAID' || props.status === 'FULLY_CLAIMED' || props.status === 'PARTIALLY_CLAIMED' || props.status === 'EXPIRED';
};

// Open payment confirmation modal
const openModal = () => {
  if (!isAlreadyPaid()) {
    isModalOpen.value = true;
  }
};

// Close modal
const closeModal = () => {
  if (!isPaying.value) {
    isModalOpen.value = false;
  }
};

// Format currency
const formatCurrency = (value: number): string => {
  return new Intl.NumberFormat('id-ID', {
    style: 'currency',
    currency: 'IDR',
    minimumFractionDigits: 0
  }).format(value);
};

// Handle payment confirmation
const handleConfirmPayment = async () => {
  isPaying.value = true;
  
  try {
    const result = await policyStore.payPolicy(props.policyId);
    
    if (result.success) {
      // Emit paid event first
      emit('paid');
      // Small delay to ensure event is processed
      await new Promise(resolve => setTimeout(resolve, 100));
      closeModal();
    }
  } finally {
    isPaying.value = false;
  }
};
</script>

<template>
  <div>
    <!-- Pay Button -->
    <button
      v-if="!isAlreadyPaid()"
      @click="openModal"
      type="button"
      class="px-4 py-2 text-sm font-medium text-white bg-green-600 border border-transparent rounded-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:bg-green-300 disabled:cursor-not-allowed transition-colors"
      :disabled="isPaying || policyStore.loading"
    >
      <span v-if="!isPaying">Pay</span>
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
        Processing...
      </span>
    </button>

    <!-- Already Paid Button -->
    <button
      v-else
      type="button"
      class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 border border-gray-300 rounded-md cursor-not-allowed"
      disabled
    >
      Already Paid
    </button>

    <!-- Payment Confirmation Modal -->
    <VModal
      :isOpen="isModalOpen"
      title="Confirm Payment"
      confirmText="Confirm Payment"
      cancelText="Cancel"
      :confirmDisabled="isPaying"
      @confirm="handleConfirmPayment"
      @cancel="closeModal"
    >
      <div class="space-y-4">
        <!-- Warning Icon -->
        <div class="flex items-center justify-center">
          <div class="flex items-center justify-center w-16 h-16 bg-green-100 rounded-full">
            <svg
              class="w-8 h-8 text-green-600"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
              />
            </svg>
          </div>
        </div>

        <!-- Message -->
        <div class="text-center">
          <p class="text-lg font-semibold text-gray-900">
            Confirm payment for Policy
          </p>
          <p class="text-sm text-gray-600 mt-1 font-mono">
            {{ policyId }}
          </p>
        </div>

        <!-- Payment Details -->
        <div class="bg-gray-50 rounded-lg p-4 border border-gray-200">
          <div class="flex justify-between items-center">
            <span class="text-gray-700 font-medium">Total Amount:</span>
            <span class="text-2xl font-bold text-green-600">
              {{ formatCurrency(totalPrice) }}
            </span>
          </div>
        </div>

        <!-- Info -->
        <div class="bg-blue-50 border border-blue-200 rounded-lg p-4">
          <div class="flex gap-3">
            <svg
              class="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5"
              fill="currentColor"
              viewBox="0 0 20 20"
            >
              <path
                fill-rule="evenodd"
                d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z"
                clip-rule="evenodd"
              />
            </svg>
            <div class="text-sm text-blue-800">
              <p class="font-medium mb-1">Payment Information</p>
              <ul class="space-y-1 text-blue-700">
                <li>• Payment will activate all insurance plans</li>
                <li>• Policy status will be updated to PAID</li>
                <li>• This action cannot be undone</li>
              </ul>
            </div>
          </div>
        </div>

        <!-- Loading State in Modal -->
        <div
          v-if="isPaying"
          class="flex items-center justify-center gap-3 py-2"
        >
          <svg
            class="w-6 h-6 text-green-600 animate-spin"
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
          <span class="text-gray-700 font-medium">Processing payment...</span>
        </div>
      </div>
    </VModal>
  </div>
</template>

<style scoped>
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.animate-spin {
  animation: spin 1s linear infinite;
}
</style>
