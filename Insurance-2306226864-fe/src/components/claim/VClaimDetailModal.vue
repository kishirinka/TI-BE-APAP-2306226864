<script setup lang="ts">
import { computed } from 'vue';
import { format } from 'date-fns';
import VModal from '@/components/common/VModal.vue';

interface Props {
  isOpen: boolean;
  type: 'accepted' | 'rejected';
  acceptedNote?: string;
  acceptedTimestamp?: string;
  rejectionReason?: string;
  rejectionDescription?: string;
  rejectionTimestamp?: string;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  close: [];
}>();

// Theme colors based on type
const themeClasses = computed(() => {
  return props.type === 'accepted'
    ? {
        icon: 'text-green-600 bg-green-100',
        badge: 'bg-green-100 text-green-800 border-green-200',
        label: 'text-green-700',
        border: 'border-green-200',
        title: 'text-green-900'
      }
    : {
        icon: 'text-red-600 bg-red-100',
        badge: 'bg-red-100 text-red-800 border-red-200',
        label: 'text-red-700',
        border: 'border-red-200',
        title: 'text-red-900'
      };
});

// Format timestamp
const formatTimestamp = (timestamp?: string): string => {
  if (!timestamp) return 'N/A';
  try {
    return format(new Date(timestamp), 'dd MMMM yyyy HH:mm');
  } catch {
    return 'Invalid date';
  }
};

const handleClose = () => {
  emit('close');
};
</script>

<template>
  <VModal
    :isOpen="isOpen"
    :title="type === 'accepted' ? 'Accepted Claim Details' : 'Rejected Claim Details'"
    :hideFooter="true"
    @cancel="handleClose"
  >
    <div class="space-y-6">
      <!-- Status Icon and Badge -->
      <div class="flex flex-col items-center gap-3">
        <div class="flex items-center justify-center w-20 h-20 rounded-full" :class="themeClasses.icon">
          <!-- Check Icon for Accepted -->
          <svg
            v-if="type === 'accepted'"
            class="w-10 h-10"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
          </svg>
          <!-- X Icon for Rejected -->
          <svg
            v-else
            class="w-10 h-10"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </div>
        <span
          class="px-4 py-1.5 text-sm font-semibold rounded-full border"
          :class="themeClasses.badge"
        >
          {{ type === 'accepted' ? 'ACCEPTED' : 'REJECTED' }}
        </span>
      </div>

      <!-- Details -->
      <div class="space-y-4">
        <!-- Accepted Details -->
        <div v-if="type === 'accepted'" class="space-y-4">
          <!-- Accepted Note -->
          <div class="bg-green-50 border rounded-lg p-4" :class="themeClasses.border">
            <label class="block text-sm font-semibold mb-2" :class="themeClasses.label">
              Note:
            </label>
            <div class="bg-white border border-green-200 rounded p-3">
              <p class="text-sm text-gray-700 whitespace-pre-wrap">{{ acceptedNote || 'No note provided' }}</p>
            </div>
          </div>

          <!-- Accepted Timestamp -->
          <div class="bg-green-50 border rounded-lg p-4" :class="themeClasses.border">
            <label class="block text-sm font-semibold mb-1" :class="themeClasses.label">
              Accepted At:
            </label>
            <p class="text-base font-medium text-gray-900">
              {{ formatTimestamp(acceptedTimestamp) }}
            </p>
          </div>

          <!-- Success Message -->
          <div class="flex items-start gap-2 bg-green-50 border border-green-200 rounded-lg p-4">
            <svg class="w-5 h-5 text-green-600 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <div class="text-sm text-green-800">
              <p class="font-semibold">Claim Accepted Successfully</p>
              <p class="mt-1">The claim has been processed and approved. Payment will be processed accordingly.</p>
            </div>
          </div>
        </div>

        <!-- Rejected Details -->
        <div v-else class="space-y-4">
          <!-- Rejection Reason -->
          <div class="bg-red-50 border rounded-lg p-4" :class="themeClasses.border">
            <label class="block text-sm font-semibold mb-2" :class="themeClasses.label">
              Rejection Reason:
            </label>
            <div class="bg-white border border-red-200 rounded p-3">
              <p class="text-sm font-medium text-gray-900">{{ rejectionReason || 'No reason provided' }}</p>
            </div>
          </div>

          <!-- Rejection Description -->
          <div class="bg-red-50 border rounded-lg p-4" :class="themeClasses.border">
            <label class="block text-sm font-semibold mb-2" :class="themeClasses.label">
              Description:
            </label>
            <div class="bg-white border border-red-200 rounded p-3">
              <p class="text-sm text-gray-700 whitespace-pre-wrap">{{ rejectionDescription || 'No description provided' }}</p>
            </div>
          </div>

          <!-- Rejected Timestamp -->
          <div class="bg-red-50 border rounded-lg p-4" :class="themeClasses.border">
            <label class="block text-sm font-semibold mb-1" :class="themeClasses.label">
              Rejected At:
            </label>
            <p class="text-base font-medium text-gray-900">
              {{ formatTimestamp(rejectionTimestamp) }}
            </p>
          </div>

          <!-- Warning Message -->
          <div class="flex items-start gap-2 bg-red-50 border border-red-200 rounded-lg p-4">
            <svg class="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
            <div class="text-sm text-red-800">
              <p class="font-semibold">Claim Rejected</p>
              <p class="mt-1">The claim has been reviewed and rejected. Please review the reason and description above.</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Close Button -->
      <div class="flex justify-end pt-4 border-t">
        <button
          @click="handleClose"
          type="button"
          class="px-6 py-2 text-white rounded-md transition-colors"
          :class="type === 'accepted' ? 'bg-green-600 hover:bg-green-700' : 'bg-red-600 hover:bg-red-700'"
        >
          Close
        </button>
      </div>
    </div>
  </VModal>
</template>
