<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { format } from 'date-fns';
import Vnavbar from '@/components/layout/Vnavbar.vue';
import VDeleteInsurancePlanButton from '@/components/insuranceplan/VDeleteInsurancePlanButton.vue';
import { useInsurancePlanStore } from '@/stores/insurancePlan/insurancePlan.store';
import { ServiceLabels } from '@/enums';

const router = useRouter();
const route = useRoute();
const insurancePlanStore = useInsurancePlanStore();

const planId = route.params.id as string;
const isLoading = ref(true);

// Fetch plan detail on mount
onMounted(async () => {
  isLoading.value = true;
  
  const result = await insurancePlanStore.fetchInsurancePlanById(planId);
  
  if (!result.success || !insurancePlanStore.currentPlan) {
    // Plan not found, redirect to list
    router.push('/insurance-plans');
  }
  
  isLoading.value = false;
});

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
    return format(date, 'dd MMMM yyyy HH:mm');
  } catch (error) {
    return dateString;
  }
};

// Navigate to edit
const goToEdit = () => {
  router.push(`/insurance-plans/${planId}/edit`);
};

// Navigate back to list
const goBack = () => {
  router.push('/insurance-plans');
};

// Handle delete success
const handleDeleted = async () => {
  // Navigate back to list page after successful delete
  await router.push('/insurance-plans');
};

// Get service badge color
const getServiceColor = (service: string): string => {
  const colors: Record<string, string> = {
    'ACCOMMODATION': 'bg-blue-100 text-blue-800',
    'FLIGHT': 'bg-green-100 text-green-800',
    'PACKAGE': 'bg-purple-100 text-purple-800',
    'RENTALS': 'bg-orange-100 text-orange-800'
  };
  return colors[service] || 'bg-gray-100 text-gray-800';
};
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navigation Bar -->
    <Vnavbar />

    <!-- Main Content -->
    <main class="max-w-4xl mx-auto pt-24 pb-16 px-4 sm:px-6 lg:px-8">
      <!-- Loading State -->
      <div v-if="isLoading" class="bg-white rounded-lg shadow-sm border border-gray-200 p-12">
        <div class="flex flex-col items-center justify-center gap-4">
          <svg
            class="w-12 h-12 text-blue-600 animate-spin"
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
          <p class="text-gray-600">Loading insurance plan details...</p>
        </div>
      </div>

      <!-- Plan Detail -->
      <div v-else-if="insurancePlanStore.currentPlan">
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
          <div class="flex items-start justify-between">
            <div>
              <h1 class="text-3xl font-bold text-gray-900">
                {{ insurancePlanStore.currentPlan.planName }}
              </h1>
              <p class="text-gray-600 mt-2">Insurance Plan Details</p>
            </div>
            <div class="flex items-center gap-3">
              <button
                @click="goToEdit"
                class="px-4 py-2 text-white bg-indigo-600 rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 transition-colors flex items-center gap-2"
              >
                <svg
                  class="w-4 h-4"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
                  />
                </svg>
                Update
              </button>
              <VDeleteInsurancePlanButton
                :planId="insurancePlanStore.currentPlan.id"
                :planName="insurancePlanStore.currentPlan.planName"
                @deleted="handleDeleted"
              />
            </div>
          </div>
        </div>

        <!-- Details Card -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
          <!-- Basic Information -->
          <div class="p-6 border-b border-gray-200">
            <h2 class="text-lg font-semibold text-gray-900 mb-4">Basic Information</h2>
            <dl class="grid grid-cols-1 gap-x-4 gap-y-6 sm:grid-cols-2">
              <!-- ID -->
              <div>
                <dt class="text-sm font-medium text-gray-500">ID</dt>
                <dd class="mt-1 text-sm text-gray-900 font-mono">
                  {{ insurancePlanStore.currentPlan.id }}
                </dd>
              </div>

              <!-- Provider ID -->
              <div>
                <dt class="text-sm font-medium text-gray-500">Provider ID</dt>
                <dd class="mt-1 text-sm text-gray-900 font-mono">
                  {{ insurancePlanStore.currentPlan.providerId }}
                </dd>
              </div>

              <!-- Plan Name -->
              <div>
                <dt class="text-sm font-medium text-gray-500">Plan Name</dt>
                <dd class="mt-1 text-sm text-gray-900 font-semibold">
                  {{ insurancePlanStore.currentPlan.planName }}
                </dd>
              </div>

              <!-- Plan Duration -->
              <div>
                <dt class="text-sm font-medium text-gray-500">Plan Duration</dt>
                <dd class="mt-1 text-sm text-gray-900">
                  {{ insurancePlanStore.currentPlan.expiredByDays }} days
                </dd>
              </div>
            </dl>
          </div>

          <!-- Financial Information -->
          <div class="p-6 border-b border-gray-200">
            <h2 class="text-lg font-semibold text-gray-900 mb-4">Financial Information</h2>
            <dl class="grid grid-cols-1 gap-x-4 gap-y-6 sm:grid-cols-2">
              <!-- Price -->
              <div>
                <dt class="text-sm font-medium text-gray-500">Price</dt>
                <dd class="mt-1 text-lg font-bold text-gray-900">
                  {{ formatCurrency(insurancePlanStore.currentPlan.price) }}
                </dd>
              </div>

              <!-- Coverage Amount -->
              <div>
                <dt class="text-sm font-medium text-gray-500">Coverage Amount</dt>
                <dd class="mt-1 text-lg font-bold text-green-600">
                  {{ formatCurrency(insurancePlanStore.currentPlan.coverage) }}
                </dd>
              </div>
            </dl>
          </div>

          <!-- Coverage Details -->
          <div class="p-6 border-b border-gray-200">
            <h2 class="text-lg font-semibold text-gray-900 mb-4">Coverage Details</h2>
            <p class="text-sm text-gray-700 leading-relaxed whitespace-pre-wrap">
              {{ insurancePlanStore.currentPlan.coverageDetails }}
            </p>
          </div>

          <!-- Applicable Services -->
          <div class="p-6 border-b border-gray-200">
            <h2 class="text-lg font-semibold text-gray-900 mb-4">Applicable Services</h2>
            <div class="flex flex-wrap gap-2">
              <span
                v-for="service in insurancePlanStore.currentPlan.applicableService"
                :key="service"
                :class="getServiceColor(service)"
                class="inline-flex items-center px-3 py-1.5 rounded-full text-sm font-medium"
              >
                {{ ServiceLabels[service] || service }}
              </span>
            </div>
          </div>

          <!-- Timestamps -->
          <div class="p-6 bg-gray-50">
            <h2 class="text-lg font-semibold text-gray-900 mb-4">Timestamps</h2>
            <dl class="grid grid-cols-1 gap-x-4 gap-y-6 sm:grid-cols-2">
              <!-- Created At -->
              <div>
                <dt class="text-sm font-medium text-gray-500">Created At</dt>
                <dd class="mt-1 text-sm text-gray-900">
                  {{ formatDate(insurancePlanStore.currentPlan.createdAt) }}
                </dd>
              </div>

              <!-- Updated At -->
              <div>
                <dt class="text-sm font-medium text-gray-500">Last Updated</dt>
                <dd class="mt-1 text-sm text-gray-900">
                  {{ formatDate(insurancePlanStore.currentPlan.updatedAt) }}
                </dd>
              </div>
            </dl>
          </div>
        </div>

        <!-- Action Buttons (Mobile) -->
        <div class="mt-6 flex flex-col sm:flex-row gap-3 sm:hidden">
          <button
            @click="goToEdit"
            class="w-full px-4 py-2 text-white bg-indigo-600 rounded-md hover:bg-indigo-700 transition-colors flex items-center justify-center gap-2"
          >
            <svg
              class="w-4 h-4"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
              />
            </svg>
            Update Plan
          </button>
        </div>
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
