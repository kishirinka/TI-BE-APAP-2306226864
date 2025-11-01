<script setup lang="ts">
import { ref, computed } from 'vue';
import { format } from 'date-fns';
import VTextArea from '@/components/common/VTextArea.vue';
import VButton from '@/components/common/VButton.vue';
import type { OrderedPlan } from '@/interfaces/policy.interface';

interface Props {
  orderedPlan: OrderedPlan;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  submit: [proof: string];
}>();

// Form data
const proof = ref('');
const errors = ref<Record<string, string>>({});

// Format currency
const formatCurrency = (value: number): string => {
  return new Intl.NumberFormat('id-ID', {
    style: 'currency',
    currency: 'IDR',
    minimumFractionDigits: 0
  }).format(value);
};

// Format date
const formatDate = (dateString: string): string => {
  try {
    const date = new Date(dateString);
    return format(date, 'dd MMMM yyyy');
  } catch (error) {
    return dateString;
  }
};

// Validate form
const validateForm = (): boolean => {
  errors.value = {};
  
  if (!proof.value.trim()) {
    errors.value.proof = 'Proof is required';
    return false;
  }
  
  if (proof.value.trim().length < 10) {
    errors.value.proof = 'Proof must be at least 10 characters';
    return false;
  }
  
  return true;
};

// Check if form is valid
const isFormValid = computed(() => {
  return proof.value.trim().length >= 10;
});

// Handle submit
const handleSubmit = () => {
  if (!validateForm()) {
    return;
  }
  
  emit('submit', proof.value.trim());
};
</script>

<template>
  <form @submit.prevent="handleSubmit" class="space-y-6">
    <!-- Ordered Plan Information -->
    <div class="bg-blue-50 border border-blue-200 rounded-lg p-6">
      <h3 class="text-lg font-semibold text-blue-900 mb-4 flex items-center gap-2">
        <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
          <path
            fill-rule="evenodd"
            d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z"
            clip-rule="evenodd"
          />
        </svg>
        Insurance Plan Information
      </h3>
      <dl class="space-y-3">
        <!-- Plan Name -->
        <div class="flex justify-between items-center">
          <dt class="text-sm font-medium text-blue-700">Plan Name:</dt>
          <dd class="text-sm font-semibold text-blue-900">
            {{ orderedPlan.insurancePlan.planName }}
          </dd>
        </div>

        <!-- Coverage Amount -->
        <div class="flex justify-between items-center">
          <dt class="text-sm font-medium text-blue-700">Coverage Amount:</dt>
          <dd class="text-sm font-bold text-green-600">
            {{ formatCurrency(orderedPlan.insurancePlan.coverage) }}
          </dd>
        </div>

        <!-- Expired Date -->
        <div class="flex justify-between items-center">
          <dt class="text-sm font-medium text-blue-700">Expired Date:</dt>
          <dd class="text-sm font-semibold text-blue-900">
            {{ formatDate(orderedPlan.expiredDate) }}
          </dd>
        </div>

        <!-- Ordered Plan ID -->
        <div class="flex justify-between items-center pt-2 border-t border-blue-200">
          <dt class="text-sm font-medium text-blue-700">Ordered Plan ID:</dt>
          <dd class="text-xs font-mono text-blue-900">
            {{ orderedPlan.id }}
          </dd>
        </div>
      </dl>
    </div>

    <!-- Proof Field -->
    <div>
      <VTextArea
        v-model="proof"
        label="Claim Proof"
        placeholder="Enter detailed proof of your claim (minimum 10 characters)..."
        :rows="6"
        required
        :error="errors.proof"
      />
      <p class="mt-2 text-sm text-gray-600">
        <span :class="proof.trim().length >= 10 ? 'text-green-600' : 'text-gray-500'">
          {{ proof.trim().length }}
        </span>
        / 10 minimum characters
      </p>
    </div>

    <!-- Help Text -->
    <div class="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
      <div class="flex gap-3">
        <svg
          class="w-5 h-5 text-yellow-600 flex-shrink-0 mt-0.5"
          fill="currentColor"
          viewBox="0 0 20 20"
        >
          <path
            fill-rule="evenodd"
            d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z"
            clip-rule="evenodd"
          />
        </svg>
        <div class="text-sm text-yellow-800">
          <p class="font-medium mb-1">Important Information</p>
          <ul class="space-y-1">
            <li>• Provide detailed proof of your claim</li>
            <li>• Include relevant URLs, document references, or descriptions</li>
            <li>• Minimum 10 characters required</li>
            <li>• Your claim will be reviewed by administrators</li>
          </ul>
        </div>
      </div>
    </div>

    <!-- Submit Button -->
    <div class="flex justify-end pt-4 border-t">
      <VButton
        type="submit"
        :disabled="!isFormValid"
      >
        Submit Claim
      </VButton>
    </div>
  </form>
</template>
