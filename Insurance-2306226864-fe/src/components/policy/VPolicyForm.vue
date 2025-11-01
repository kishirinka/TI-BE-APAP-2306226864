<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import VInput from '@/components/common/VInput.vue';
import VButton from '@/components/common/VButton.vue';
import { useInsurancePlanStore } from '@/stores/insurancePlan/insurancePlan.store';
import { ServiceEnum, ServiceLabels } from '@/enums';
import type { CreatePolicyRequest } from '@/interfaces/policy.interface';
import type { InsurancePlan } from '@/interfaces/insurancePlan.interface';

const emit = defineEmits<{
  submit: [formData: CreatePolicyRequest];
}>();

const insurancePlanStore = useInsurancePlanStore();

// Form data
const formData = ref({
  userId: '',
  bookingId: '',
  service: '',
  startDate: new Date().toISOString().split('T')[0] // Today's date
});

const selectedPlanIds = ref<string[]>([]);
const errors = ref<Record<string, string>>({});
const isLoadingPlans = ref(false);

// Available insurance plans filtered by service
const availablePlans = computed<InsurancePlan[]>(() => {
  if (!formData.value.service) {
    return [];
  }
  
  return insurancePlanStore.insurancePlans.filter(plan => 
    plan.applicableService.includes(formData.value.service)
  );
});

// Selected plans details
const selectedPlans = computed<InsurancePlan[]>(() => {
  return availablePlans.value.filter(plan => 
    selectedPlanIds.value.includes(plan.id)
  );
});

// Total price calculation
const totalPrice = computed(() => {
  return selectedPlans.value.reduce((sum, plan) => sum + plan.price, 0);
});

// Total coverage calculation
const totalCoverage = computed(() => {
  return selectedPlans.value.reduce((sum, plan) => sum + plan.coverage, 0);
});

// Format currency
const formatCurrency = (value: number): string => {
  return new Intl.NumberFormat('id-ID', {
    style: 'currency',
    currency: 'IDR',
    minimumFractionDigits: 0
  }).format(value);
};

// Watch service selection to fetch plans
watch(() => formData.value.service, async (newService) => {
  if (newService) {
    // Reset selected plans when service changes
    selectedPlanIds.value = [];
    
    // Fetch insurance plans if not already loaded
    if (insurancePlanStore.insurancePlans.length === 0) {
      isLoadingPlans.value = true;
      await insurancePlanStore.fetchInsurancePlans();
      isLoadingPlans.value = false;
    }
  } else {
    selectedPlanIds.value = [];
  }
});

// Toggle plan selection
const togglePlan = (planId: string) => {
  const index = selectedPlanIds.value.indexOf(planId);
  if (index > -1) {
    selectedPlanIds.value.splice(index, 1);
  } else {
    selectedPlanIds.value.push(planId);
  }
  
  // Clear error when user selects a plan
  if (selectedPlanIds.value.length > 0) {
    delete errors.value.insurancePlans;
  }
};

// Check if plan is selected
const isPlanSelected = (planId: string): boolean => {
  return selectedPlanIds.value.includes(planId);
};

// Validate form
const validateForm = (): boolean => {
  errors.value = {};
  
  if (!formData.value.userId.trim()) {
    errors.value.userId = 'User ID is required';
  }
  
  if (!formData.value.bookingId.trim()) {
    errors.value.bookingId = 'Booking ID is required';
  }
  
  if (!formData.value.service) {
    errors.value.service = 'Service is required';
  }
  
  if (selectedPlanIds.value.length === 0) {
    errors.value.insurancePlans = 'At least one insurance plan must be selected';
  }
  
  return Object.keys(errors.value).length === 0;
};

// Submit form
const handleSubmit = () => {
  if (!validateForm()) {
    return;
  }
  
  const submitData: CreatePolicyRequest = {
    userId: formData.value.userId.trim(),
    bookingId: formData.value.bookingId.trim(),
    service: formData.value.service,
    insurancePlanIds: [...selectedPlanIds.value]
  };
  
  emit('submit', submitData);
};

// Form is valid
const isFormValid = computed(() => {
  return formData.value.userId.trim() !== '' &&
         formData.value.bookingId.trim() !== '' &&
         formData.value.service !== '' &&
         selectedPlanIds.value.length > 0;
});

// Fetch insurance plans on mount
onMounted(async () => {
  if (insurancePlanStore.insurancePlans.length === 0) {
    isLoadingPlans.value = true;
    await insurancePlanStore.fetchInsurancePlans();
    isLoadingPlans.value = false;
  }
});
</script>

<template>
  <form @submit.prevent="handleSubmit" class="space-y-6">
    <!-- User ID -->
    <div>
      <VInput
        v-model="formData.userId"
        label="User ID"
        placeholder="Enter user ID"
        required
        :error="errors.userId"
      />
    </div>

    <!-- Booking ID -->
    <div>
      <VInput
        v-model="formData.bookingId"
        label="Booking ID"
        placeholder="Enter booking ID"
        required
        :error="errors.bookingId"
      />
    </div>

    <!-- Service Selection -->
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-2">
        Service <span class="text-red-500">*</span>
      </label>
      <select
        v-model="formData.service"
        class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        :class="{ 'border-red-500': errors.service }"
      >
        <option value="">Select service</option>
        <option
          v-for="service in Object.values(ServiceEnum)"
          :key="service"
          :value="service"
        >
          {{ ServiceLabels[service] }}
        </option>
      </select>
      <p v-if="errors.service" class="mt-1 text-sm text-red-600">
        {{ errors.service }}
      </p>
    </div>

    <!-- Start Date -->
    <div>
      <VInput
        v-model="formData.startDate"
        label="Start Date"
        type="date"
        readonly
        disabled
      />
      <p class="mt-1 text-sm text-gray-500">
        Policy start date is automatically set to today
      </p>
    </div>

    <!-- Insurance Plans Selection -->
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-2">
        Select Insurance Plans <span class="text-red-500">*</span>
      </label>
      
      <!-- Loading state -->
      <div v-if="isLoadingPlans" class="border border-gray-300 rounded-md p-8">
        <div class="flex flex-col items-center gap-3">
          <svg
            class="w-8 h-8 text-blue-600 animate-spin"
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
          <p class="text-gray-600">Loading insurance plans...</p>
        </div>
      </div>
      
      <!-- No service selected -->
      <div
        v-else-if="!formData.service"
        class="border border-gray-300 rounded-md p-6 text-center text-gray-500"
      >
        <svg
          class="w-12 h-12 mx-auto mb-3 text-gray-400"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
          />
        </svg>
        <p>Please select a service first to view available insurance plans</p>
      </div>
      
      <!-- No plans available -->
      <div
        v-else-if="availablePlans.length === 0"
        class="border border-gray-300 rounded-md p-6 text-center text-gray-500"
      >
        <svg
          class="w-12 h-12 mx-auto mb-3 text-gray-400"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"
          />
        </svg>
        <p>No insurance plans available for {{ ServiceLabels[formData.service] }}</p>
      </div>
      
      <!-- Plans list -->
      <div v-else class="border border-gray-300 rounded-md divide-y divide-gray-200">
        <label
          v-for="plan in availablePlans"
          :key="plan.id"
          class="flex items-start gap-4 p-4 hover:bg-gray-50 cursor-pointer transition-colors"
          :class="{ 'bg-blue-50': isPlanSelected(plan.id) }"
        >
          <input
            type="checkbox"
            :checked="isPlanSelected(plan.id)"
            @change="togglePlan(plan.id)"
            class="mt-1 w-5 h-5 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
          />
          <div class="flex-1">
            <div class="flex items-start justify-between">
              <div>
                <p class="font-semibold text-gray-900">{{ plan.planName }}</p>
                <p class="text-sm text-gray-600 mt-1">Provider: {{ plan.providerId }}</p>
              </div>
              <span
                v-if="isPlanSelected(plan.id)"
                class="ml-2 px-2 py-1 bg-blue-100 text-blue-800 text-xs font-medium rounded"
              >
                Selected
              </span>
            </div>
            <div class="mt-2 grid grid-cols-2 gap-4 text-sm">
              <div>
                <span class="text-gray-500">Price:</span>
                <span class="ml-2 font-medium text-gray-900">{{ formatCurrency(plan.price) }}</span>
              </div>
              <div>
                <span class="text-gray-500">Coverage:</span>
                <span class="ml-2 font-medium text-green-600">{{ formatCurrency(plan.coverage) }}</span>
              </div>
            </div>
          </div>
        </label>
      </div>
      
      <p v-if="errors.insurancePlans" class="mt-2 text-sm text-red-600">
        {{ errors.insurancePlans }}
      </p>
      
      <p v-if="selectedPlanIds.length > 0" class="mt-2 text-sm text-gray-600">
        {{ selectedPlanIds.length }} plan(s) selected
      </p>
    </div>

    <!-- Summary Section -->
    <div
      v-if="selectedPlanIds.length > 0"
      class="bg-gradient-to-r from-blue-50 to-indigo-50 border border-blue-200 rounded-lg p-6"
    >
      <h3 class="text-lg font-semibold text-gray-900 mb-4">Policy Summary</h3>
      <div class="space-y-3">
        <div class="flex justify-between items-center">
          <span class="text-gray-700">Total Plans:</span>
          <span class="font-semibold text-gray-900">{{ selectedPlanIds.length }}</span>
        </div>
        <div class="flex justify-between items-center">
          <span class="text-gray-700">Total Price:</span>
          <span class="text-xl font-bold text-blue-600">{{ formatCurrency(totalPrice) }}</span>
        </div>
        <div class="flex justify-between items-center">
          <span class="text-gray-700">Total Coverage:</span>
          <span class="text-xl font-bold text-green-600">{{ formatCurrency(totalCoverage) }}</span>
        </div>
      </div>
    </div>

    <!-- Submit Button -->
    <div class="flex justify-end pt-4 border-t">
      <VButton
        type="submit"
        :disabled="!isFormValid"
      >
        Create Policy
      </VButton>
    </div>
  </form>
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
