<script setup lang="ts">
import { ref, watch } from 'vue';
import VModal from '@/components/common/VModal.vue';
import VTextArea from '@/components/common/VTextArea.vue';
import type { Claim, ProcessClaimRequest } from '@/interfaces/claim.interface';

interface Props {
  isOpen: boolean;
  claim: Claim;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  close: [];
  process: [data: ProcessClaimRequest];
}>();

// State
const selectedAction = ref<'ACCEPT' | 'REJECT' | null>(null);
const acceptNote = ref('');
const rejectionReason = ref('');
const rejectionDescription = ref('');
const showConfirmation = ref(false);
const errors = ref<Record<string, string>>({});

// Rejection reason options
const rejectionReasonOptions = [
  { value: 'Insufficient Evidence', label: 'Insufficient Evidence' },
  { value: 'Expired Claim', label: 'Expired Claim' },
  { value: 'Invalid Documentation', label: 'Invalid Documentation' },
  { value: 'Other', label: 'Other' }
];

// Reset form
const resetForm = () => {
  selectedAction.value = null;
  acceptNote.value = '';
  rejectionReason.value = '';
  rejectionDescription.value = '';
  errors.value = {};
  showConfirmation.value = false;
};

// Watch modal close to reset form
watch(() => props.isOpen, (newValue) => {
  if (!newValue) {
    resetForm();
  }
});

// Handle close
const handleClose = () => {
  resetForm();
  emit('close');
};

// Select action
const selectAction = (action: 'ACCEPT' | 'REJECT') => {
  selectedAction.value = action;
  errors.value = {};
};

// Validate form
const validateForm = (): boolean => {
  errors.value = {};
  
  if (selectedAction.value === 'ACCEPT') {
    if (!acceptNote.value.trim()) {
      errors.value.acceptNote = 'Note is required when accepting a claim';
      return false;
    }
  } else if (selectedAction.value === 'REJECT') {
    if (!rejectionReason.value) {
      errors.value.rejectionReason = 'Rejection reason is required';
      return false;
    }
    if (!rejectionDescription.value.trim()) {
      errors.value.rejectionDescription = 'Rejection description is required';
      return false;
    }
  }
  
  return true;
};

// Show confirmation modal
const handleShowConfirmation = () => {
  if (!validateForm()) {
    return;
  }
  showConfirmation.value = true;
};

// Confirm process
const handleConfirmProcess = () => {
  if (!selectedAction.value) return;
  
  const processData: ProcessClaimRequest = {
    action: selectedAction.value
  };
  
  if (selectedAction.value === 'ACCEPT') {
    processData.note = acceptNote.value.trim();
  } else {
    processData.rejectionReason = rejectionReason.value;
    processData.rejectionDescription = rejectionDescription.value.trim();
  }
  
  emit('process', processData);
  resetForm();
};

// Cancel confirmation
const handleCancelConfirmation = () => {
  showConfirmation.value = false;
};
</script>

<template>
  <VModal
    :isOpen="isOpen"
    title="Process Claim"
    :hideFooter="true"
    @cancel="handleClose"
  >
    <div class="space-y-6">
      <!-- Claim Proof Display -->
      <div class="bg-gray-50 border border-gray-200 rounded-lg p-4">
        <h4 class="text-sm font-semibold text-gray-900 mb-2">Claim Proof:</h4>
        <div class="bg-white border border-gray-200 rounded p-3">
          <p class="text-sm text-gray-700 whitespace-pre-wrap">{{ claim.proof }}</p>
        </div>
        <p class="text-xs text-gray-500 mt-2">
          Review the proof carefully before making a decision
        </p>
      </div>

      <!-- Action Selection (if not selected yet) -->
      <div v-if="!selectedAction" class="space-y-3">
        <p class="text-sm font-medium text-gray-700">Choose an action:</p>
        <div class="grid grid-cols-2 gap-4">
          <!-- Accept Button -->
          <button
            @click="selectAction('ACCEPT')"
            type="button"
            class="flex flex-col items-center gap-2 p-4 border-2 border-green-300 rounded-lg hover:bg-green-50 hover:border-green-500 transition-colors"
          >
            <svg class="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
            </svg>
            <span class="font-semibold text-green-700">Accept Claim</span>
          </button>

          <!-- Reject Button -->
          <button
            @click="selectAction('REJECT')"
            type="button"
            class="flex flex-col items-center gap-2 p-4 border-2 border-red-300 rounded-lg hover:bg-red-50 hover:border-red-500 transition-colors"
          >
            <svg class="w-8 h-8 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
            <span class="font-semibold text-red-700">Reject Claim</span>
          </button>
        </div>
      </div>

      <!-- Accept Form -->
      <div v-if="selectedAction === 'ACCEPT'" class="space-y-4">
        <div class="flex items-center gap-2 text-green-700 font-semibold">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
          </svg>
          <span>Accepting Claim</span>
        </div>

        <VTextArea
          v-model="acceptNote"
          label="Note"
          placeholder="Enter a note for accepting this claim (required)..."
          :rows="4"
          required
          :error="errors.acceptNote"
        />

        <div class="flex gap-3 pt-4 border-t">
          <button
            @click="selectedAction = null"
            type="button"
            class="flex-1 px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
          >
            Back
          </button>
          <button
            @click="handleShowConfirmation"
            type="button"
            class="flex-1 px-4 py-2 text-white bg-green-600 rounded-md hover:bg-green-700 transition-colors"
          >
            Confirm Accept
          </button>
        </div>
      </div>

      <!-- Reject Form -->
      <div v-if="selectedAction === 'REJECT'" class="space-y-4">
        <div class="flex items-center gap-2 text-red-700 font-semibold">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
          <span>Rejecting Claim</span>
        </div>

        <!-- Rejection Reason -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Rejection Reason <span class="text-red-500">*</span>
          </label>
          <select
            v-model="rejectionReason"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-red-500 focus:border-red-500"
            :class="{ 'border-red-500': errors.rejectionReason }"
          >
            <option value="">Select a reason</option>
            <option
              v-for="option in rejectionReasonOptions"
              :key="option.value"
              :value="option.value"
            >
              {{ option.label }}
            </option>
          </select>
          <p v-if="errors.rejectionReason" class="mt-1 text-sm text-red-600">
            {{ errors.rejectionReason }}
          </p>
        </div>

        <!-- Rejection Description -->
        <VTextArea
          v-model="rejectionDescription"
          label="Rejection Description"
          placeholder="Provide detailed description for rejection (required)..."
          :rows="4"
          required
          :error="errors.rejectionDescription"
        />

        <div class="flex gap-3 pt-4 border-t">
          <button
            @click="selectedAction = null"
            type="button"
            class="flex-1 px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
          >
            Back
          </button>
          <button
            @click="handleShowConfirmation"
            type="button"
            class="flex-1 px-4 py-2 text-white bg-red-600 rounded-md hover:bg-red-700 transition-colors"
          >
            Confirm Reject
          </button>
        </div>
      </div>

      <!-- No Action Selected - Cancel Button -->
      <div v-if="!selectedAction" class="flex justify-end pt-4 border-t">
        <button
          @click="handleClose"
          type="button"
          class="px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
        >
          Cancel
        </button>
      </div>
    </div>

    <!-- Confirmation Nested Modal -->
    <VModal
      :isOpen="showConfirmation"
      :title="selectedAction === 'ACCEPT' ? 'Confirm Accept Claim' : 'Confirm Reject Claim'"
      confirmText="Confirm"
      cancelText="Cancel"
      @confirm="handleConfirmProcess"
      @cancel="handleCancelConfirmation"
    >
      <div class="space-y-4">
        <!-- Warning Icon -->
        <div class="flex items-center justify-center">
          <div
            class="flex items-center justify-center w-16 h-16 rounded-full"
            :class="selectedAction === 'ACCEPT' ? 'bg-green-100' : 'bg-red-100'"
          >
            <svg
              v-if="selectedAction === 'ACCEPT'"
              class="w-8 h-8 text-green-600"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
            </svg>
            <svg
              v-else
              class="w-8 h-8 text-red-600"
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
        </div>

        <!-- Message -->
        <div class="text-center">
          <p class="text-lg font-semibold text-gray-900">
            {{ selectedAction === 'ACCEPT' ? 'Accept this claim?' : 'Reject this claim?' }}
          </p>
          <p class="text-sm text-gray-600 mt-1 font-mono">
            Claim ID: {{ claim.id }}
          </p>
        </div>

        <!-- Summary -->
        <div class="bg-gray-50 rounded-lg p-4 border border-gray-200">
          <div v-if="selectedAction === 'ACCEPT'" class="space-y-2">
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">Action:</span>
              <span class="font-semibold text-green-600">Accept</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">Note:</span>
              <span class="font-medium text-gray-900 text-right ml-4">{{ acceptNote }}</span>
            </div>
          </div>
          <div v-else class="space-y-2">
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">Action:</span>
              <span class="font-semibold text-red-600">Reject</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">Reason:</span>
              <span class="font-medium text-gray-900">{{ rejectionReason }}</span>
            </div>
            <div class="text-sm">
              <span class="text-gray-600">Description:</span>
              <p class="font-medium text-gray-900 mt-1">{{ rejectionDescription }}</p>
            </div>
          </div>
        </div>

        <!-- Warning -->
        <div
          class="border rounded-lg p-3"
          :class="selectedAction === 'ACCEPT' ? 'bg-green-50 border-green-200' : 'bg-red-50 border-red-200'"
        >
          <p
            class="text-sm font-medium"
            :class="selectedAction === 'ACCEPT' ? 'text-green-800' : 'text-red-800'"
          >
            ⚠️ This action cannot be undone
          </p>
        </div>
      </div>
    </VModal>
  </VModal>
</template>
