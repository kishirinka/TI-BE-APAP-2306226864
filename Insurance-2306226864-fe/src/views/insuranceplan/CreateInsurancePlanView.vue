<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import Vnavbar from '@/components/layout/Vnavbar.vue';
import VInsurancePlanForm from '@/components/insuranceplan/VInsurancePlanForm.vue';
import { useInsurancePlanStore } from '@/stores/insurancePlan/insurancePlan.store';
import type { InsurancePlanRequest } from '@/interfaces/insurancePlan.interface';

const router = useRouter();
const insurancePlanStore = useInsurancePlanStore();

const isSubmitting = ref(false);

const handleSubmit = async (formData: InsurancePlanRequest) => {
  isSubmitting.value = true;

  try {
    const result = await insurancePlanStore.createInsurancePlan({
      planName: formData.planName,
      providerId: formData.providerId!,
      price: formData.price,
      coverage: formData.coverage,
      coverageDetails: formData.coverageDetails,
      applicableService: formData.applicableService,
      expiredByDays: formData.expiredByDays
    });

    if (result.success) {
      // Toast already shown by store
      // Redirect after short delay
      setTimeout(() => {
        router.push('/insurance-plans');
      }, 1000);
    }
  } finally {
    isSubmitting.value = false;
  }
};

const goBack = () => {
  router.push('/insurance-plans');
};
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navigation Bar -->
    <Vnavbar />

    <!-- Main Content -->
    <main class="max-w-4xl mx-auto pt-24 pb-16 px-4 sm:px-6 lg:px-8">
      <!-- Header Section -->
      <div class="mb-8">
        <div class="flex items-center gap-4 mb-4">
          <button
            @click="goBack"
            class="flex items-center text-gray-600 hover:text-gray-900 transition-colors"
          >
            <svg
              class="w-5 h-5 mr-1"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M15 19l-7-7 7-7"
              />
            </svg>
            Back to Insurance Plans
          </button>
        </div>
        <h1 class="text-3xl font-bold text-gray-900">Create Insurance Plan</h1>
        <p class="text-gray-600 mt-2">Add a new insurance plan to the system</p>
      </div>

      <!-- Form Card -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 relative">
        <!-- Loading Overlay -->
        <div
          v-if="isSubmitting"
          class="absolute inset-0 bg-white bg-opacity-75 flex items-center justify-center z-10 rounded-lg"
        >
          <div class="flex flex-col items-center gap-3">
            <svg
              class="w-10 h-10 text-blue-600 animate-spin"
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
            <p class="text-gray-700 font-medium">Creating insurance plan...</p>
          </div>
        </div>

        <!-- Form -->
        <VInsurancePlanForm
          mode="create"
          @submit="handleSubmit"
        />
      </div>

      <!-- Help Section -->
      <div class="mt-6 bg-blue-50 border border-blue-200 rounded-lg p-6">
        <h3 class="text-lg font-semibold text-blue-900 mb-3 flex items-center gap-2">
          <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
            <path
              fill-rule="evenodd"
              d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z"
              clip-rule="evenodd"
            />
          </svg>
          Important Information
        </h3>
        <ul class="space-y-2 text-sm text-blue-800">
          <li class="flex items-start gap-2">
            <svg class="w-4 h-4 mt-0.5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
              <path
                fill-rule="evenodd"
                d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                clip-rule="evenodd"
              />
            </svg>
            <span>All fields are <strong>required</strong> unless marked as optional</span>
          </li>
          <li class="flex items-start gap-2">
            <svg class="w-4 h-4 mt-0.5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
              <path
                fill-rule="evenodd"
                d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                clip-rule="evenodd"
              />
            </svg>
            <span><strong>Price</strong> and <strong>Coverage</strong> must be greater than or equal to 0</span>
          </li>
          <li class="flex items-start gap-2">
            <svg class="w-4 h-4 mt-0.5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
              <path
                fill-rule="evenodd"
                d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                clip-rule="evenodd"
              />
            </svg>
            <span>At least <strong>one service</strong> must be selected from Applicable Services</span>
          </li>
          <li class="flex items-start gap-2">
            <svg class="w-4 h-4 mt-0.5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
              <path
                fill-rule="evenodd"
                d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                clip-rule="evenodd"
              />
            </svg>
            <span><strong>Plan Duration</strong> must be at least 1 day</span>
          </li>
          <li class="flex items-start gap-2">
            <svg class="w-4 h-4 mt-0.5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
              <path
                fill-rule="evenodd"
                d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                clip-rule="evenodd"
              />
            </svg>
            <span><strong>Provider ID</strong> cannot be changed after creation</span>
          </li>
        </ul>
      </div>
    </main>
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
