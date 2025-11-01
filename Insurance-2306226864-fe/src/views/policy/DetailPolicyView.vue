<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { format } from 'date-fns';
import Vnavbar from '@/components/layout/Vnavbar.vue';
import VPayPolicyButton from '@/components/policy/VPayPolicyButton.vue';
import { usePolicyStore } from '@/stores/policy/policy.store';
import { ServiceLabels, PolicyStatusLabels, PolicyStatusColors, OrderedPlanStatusLabels, OrderedPlanStatusColors } from '@/enums';

const router = useRouter();
const route = useRoute();
const policyStore = usePolicyStore();

const policyId = route.params.id as string;
const isLoading = ref(true);

// Fetch policy detail on mount
onMounted(async () => {
  isLoading.value = true;
  
  const result = await policyStore.fetchPolicyById(policyId);
  
  if (!result.success || !policyStore.currentPolicy) {
    // Policy not found, redirect to list
    router.push('/policies');
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
    return format(date, 'dd MMMM yyyy');
  } catch (error) {
    return dateString;
  }
};

// Format datetime
const formatDateTime = (dateString: string): string => {
  try {
    const date = new Date(dateString);
    return format(date, 'dd MMMM yyyy HH:mm');
  } catch (error) {
    return dateString;
  }
};

// Navigate back to list
const goBack = () => {
  router.push('/policies');
};

// Handle payment success
const handlePaid = async () => {
  // Refresh policy detail after payment
  await policyStore.fetchPolicyById(policyId);
};

// Get status badge color
const getStatusColor = (status: string): string => {
  return PolicyStatusColors[status] || 'bg-gray-100 text-gray-800';
};

// Get ordered plan status color
const getOrderedPlanStatusColor = (status: string): string => {
  return OrderedPlanStatusColors[status] || 'bg-gray-100 text-gray-800';
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

// Navigate to ordered plan detail (future feature)
const viewOrderedPlan = (orderedPlanId: string) => {
  // For now, just log - can implement later
  console.log('View ordered plan:', orderedPlanId);
  // router.push(`/ordered-plans/${orderedPlanId}`);
};
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navigation Bar -->
    <Vnavbar />

    <!-- Main Content -->
    <main class="max-w-6xl mx-auto pt-24 pb-16 px-4 sm:px-6 lg:px-8">
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
          <p class="text-gray-600">Loading policy details...</p>
        </div>
      </div>

      <!-- Policy Detail -->
      <div v-else-if="policyStore.currentPolicy">
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
              Back to Policies
            </button>
          </div>
          <div class="flex items-start justify-between">
            <div>
              <h1 class="text-3xl font-bold text-gray-900">Policy Details</h1>
              <p class="text-gray-600 mt-2 font-mono">{{ policyStore.currentPolicy.id }}</p>
            </div>
            <VPayPolicyButton
              :policyId="policyStore.currentPolicy.id"
              :totalPrice="policyStore.currentPolicy.totalPrice"
              :status="policyStore.currentPolicy.status"
              @paid="handlePaid"
            />
          </div>
        </div>

        <!-- Policy Information Card -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden mb-6">
          <!-- Basic Information -->
          <div class="p-6 border-b border-gray-200">
            <h2 class="text-lg font-semibold text-gray-900 mb-4">Basic Information</h2>
            <dl class="grid grid-cols-1 gap-x-4 gap-y-6 sm:grid-cols-2 lg:grid-cols-3">
              <!-- ID -->
              <div>
                <dt class="text-sm font-medium text-gray-500">Policy ID</dt>
                <dd class="mt-1 text-sm text-gray-900 font-mono">
                  {{ policyStore.currentPolicy.id }}
                </dd>
              </div>

              <!-- User ID -->
              <div>
                <dt class="text-sm font-medium text-gray-500">User ID</dt>
                <dd class="mt-1 text-sm text-gray-900">
                  {{ policyStore.currentPolicy.userId }}
                </dd>
              </div>

              <!-- Booking ID -->
              <div>
                <dt class="text-sm font-medium text-gray-500">Booking ID</dt>
                <dd class="mt-1 text-sm text-gray-900">
                  {{ policyStore.currentPolicy.bookingId }}
                </dd>
              </div>

              <!-- Service -->
              <div>
                <dt class="text-sm font-medium text-gray-500">Service</dt>
                <dd class="mt-1">
                  <span
                    :class="getServiceColor(policyStore.currentPolicy.service)"
                    class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                  >
                    {{ ServiceLabels[policyStore.currentPolicy.service] }}
                  </span>
                </dd>
              </div>

              <!-- Start Date -->
              <div>
                <dt class="text-sm font-medium text-gray-500">Start Date</dt>
                <dd class="mt-1 text-sm text-gray-900">
                  {{ formatDate(policyStore.currentPolicy.startDate) }}
                </dd>
              </div>

              <!-- Status -->
              <div>
                <dt class="text-sm font-medium text-gray-500">Status</dt>
                <dd class="mt-1">
                  <span
                    :class="getStatusColor(policyStore.currentPolicy.status)"
                    class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                  >
                    {{ PolicyStatusLabels[policyStore.currentPolicy.status] || policyStore.currentPolicy.status }}
                  </span>
                </dd>
              </div>
            </dl>
          </div>

          <!-- Financial Information -->
          <div class="p-6 border-b border-gray-200 bg-gradient-to-r from-blue-50 to-indigo-50">
            <h2 class="text-lg font-semibold text-gray-900 mb-4">Financial Summary</h2>
            <dl class="grid grid-cols-1 gap-x-4 gap-y-6 sm:grid-cols-2">
              <!-- Total Price -->
              <div>
                <dt class="text-sm font-medium text-gray-500">Total Price</dt>
                <dd class="mt-1 text-2xl font-bold text-blue-600">
                  {{ formatCurrency(policyStore.currentPolicy.totalPrice) }}
                </dd>
              </div>

              <!-- Total Coverage -->
              <div>
                <dt class="text-sm font-medium text-gray-500">Total Coverage</dt>
                <dd class="mt-1 text-2xl font-bold text-green-600">
                  {{ formatCurrency(policyStore.currentPolicy.totalCoverage) }}
                </dd>
              </div>
            </dl>
          </div>

          <!-- Timestamps -->
          <div class="p-6 bg-gray-50">
            <h2 class="text-lg font-semibold text-gray-900 mb-4">Timestamps</h2>
            <dl class="grid grid-cols-1 gap-x-4 gap-y-6 sm:grid-cols-2">
              <!-- Created At -->
              <div>
                <dt class="text-sm font-medium text-gray-500">Created At</dt>
                <dd class="mt-1 text-sm text-gray-900">
                  {{ formatDateTime(policyStore.currentPolicy.createdAt) }}
                </dd>
              </div>

              <!-- Updated At -->
              <div>
                <dt class="text-sm font-medium text-gray-500">Last Updated</dt>
                <dd class="mt-1 text-sm text-gray-900">
                  {{ formatDateTime(policyStore.currentPolicy.updatedAt) }}
                </dd>
              </div>
            </dl>
          </div>
        </div>

        <!-- Ordered Plans Section -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
          <div class="p-6 border-b border-gray-200">
            <h2 class="text-lg font-semibold text-gray-900">Insurance Plans</h2>
            <p class="text-sm text-gray-600 mt-1">
              {{ policyStore.currentPolicy.orderedPlans.length }} plan(s) in this policy
            </p>
          </div>

          <!-- Desktop Table -->
          <div class="hidden md:block overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Plan ID
                  </th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Plan Name
                  </th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Price
                  </th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Coverage
                  </th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Expired Date
                  </th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr
                  v-for="orderedPlan in policyStore.currentPolicy.orderedPlans"
                  :key="orderedPlan.id"
                  class="hover:bg-gray-50 transition-colors"
                >
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-mono text-gray-900">
                    {{ orderedPlan.id }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-semibold text-gray-900">
                    {{ orderedPlan.insurancePlan.planName }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-semibold text-blue-600">
                    {{ formatCurrency(orderedPlan.insurancePlan.price) }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-semibold text-green-600">
                    {{ formatCurrency(orderedPlan.insurancePlan.coverage) }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {{ formatDate(orderedPlan.expiredDate) }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span
                      :class="getOrderedPlanStatusColor(orderedPlan.status)"
                      class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                    >
                      {{ OrderedPlanStatusLabels[orderedPlan.status] || orderedPlan.status }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Mobile Cards -->
          <div class="md:hidden divide-y divide-gray-200">
            <div
              v-for="orderedPlan in policyStore.currentPolicy.orderedPlans"
              :key="orderedPlan.id"
              class="p-4 hover:bg-gray-50 transition-colors"
            >
              <div class="space-y-3">
                <div class="flex justify-between items-start">
                  <div>
                    <p class="text-sm font-semibold text-gray-900">
                      {{ orderedPlan.insurancePlan.planName }}
                    </p>
                    <p class="text-xs text-gray-500 font-mono mt-1">{{ orderedPlan.id }}</p>
                  </div>
                  <span
                    :class="getOrderedPlanStatusColor(orderedPlan.status)"
                    class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                  >
                    {{ OrderedPlanStatusLabels[orderedPlan.status] || orderedPlan.status }}
                  </span>
                </div>

                <div class="grid grid-cols-2 gap-3 text-sm">
                  <div>
                    <p class="text-xs text-gray-500">Price</p>
                    <p class="font-semibold text-blue-600">
                      {{ formatCurrency(orderedPlan.insurancePlan.price) }}
                    </p>
                  </div>
                  <div>
                    <p class="text-xs text-gray-500">Coverage</p>
                    <p class="font-semibold text-green-600">
                      {{ formatCurrency(orderedPlan.insurancePlan.coverage) }}
                    </p>
                  </div>
                </div>

                <div>
                  <p class="text-xs text-gray-500">Expired Date</p>
                  <p class="text-sm text-gray-900">{{ formatDate(orderedPlan.expiredDate) }}</p>
                </div>
              </div>
            </div>
          </div>
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
