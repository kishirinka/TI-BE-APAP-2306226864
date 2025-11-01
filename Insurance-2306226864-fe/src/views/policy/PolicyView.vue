<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { format } from 'date-fns';
import Vnavbar from '@/components/layout/Vnavbar.vue';
import { usePolicyStore } from '@/stores/policy/policy.store';
import { ServiceLabels, PolicyStatusLabels, PolicyStatusColors } from '@/enums';
import type { Policy } from '@/interfaces/policy.interface';

const router = useRouter();
const policyStore = usePolicyStore();

const searchQuery = ref('');

// Fetch policies on mount
onMounted(async () => {
  await policyStore.fetchPolicies();
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
    return format(date, 'dd MMM yyyy');
  } catch (error) {
    return dateString;
  }
};

// Navigate to create page
const goToCreate = () => {
  router.push('/policies/create');
};

// Navigate to detail page
const viewPolicy = (id: string) => {
  router.push(`/policies/${id}`);
};

// Get status badge color
const getStatusColor = (status: string): string => {
  return PolicyStatusColors[status] || 'bg-gray-100 text-gray-800';
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

// Filtered policies
const filteredPolicies = computed(() => {
  if (!searchQuery.value) {
    return policyStore.policies;
  }
  
  const query = searchQuery.value.toLowerCase();
  return policyStore.policies.filter(policy =>
    policy.id.toLowerCase().includes(query) ||
    policy.userId.toLowerCase().includes(query) ||
    policy.bookingId.toLowerCase().includes(query)
  );
});
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navigation Bar -->
    <Vnavbar />

    <!-- Main Content -->
    <main class="max-w-7xl mx-auto pt-24 pb-16 px-4 sm:px-6 lg:px-8">
      <!-- Header Section -->
      <div class="mb-8">
        <div class="flex justify-between items-center">
          <div>
            <h1 class="text-3xl font-bold text-gray-900">Policies</h1>
            <p class="text-gray-600 mt-2">Manage and monitor all insurance policies</p>
          </div>
          <button
            @click="goToCreate"
            class="px-4 py-2 text-white bg-blue-600 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-colors flex items-center gap-2"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            Create New Policy
          </button>
        </div>
      </div>

      <!-- Search Bar -->
      <div class="mb-6 bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <div class="relative">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Search by ID, User ID, or Booking ID..."
            class="w-full px-4 py-2 pr-10 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          >
          <svg
            class="w-5 h-5 text-gray-400 absolute right-3 top-2.5"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
            />
          </svg>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="policyStore.loading" class="bg-white rounded-lg shadow-sm border border-gray-200 p-12">
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
          <p class="text-gray-600">Loading policies...</p>
        </div>
      </div>

      <!-- Empty State -->
      <div
        v-else-if="filteredPolicies.length === 0"
        class="bg-white rounded-lg shadow-sm border border-gray-200 p-12"
      >
        <div class="flex flex-col items-center justify-center gap-4">
          <svg
            class="w-16 h-16 text-gray-400"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
            />
          </svg>
          <p class="text-gray-600 text-lg">
            {{ searchQuery ? 'No policies found matching your search' : 'No policies found' }}
          </p>
          <button
            v-if="!searchQuery"
            @click="goToCreate"
            class="mt-4 px-4 py-2 text-white bg-blue-600 rounded-md hover:bg-blue-700 transition-colors"
          >
            Create your first policy
          </button>
        </div>
      </div>

      <!-- Policies Table -->
      <div v-else class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
        <!-- Desktop Table -->
        <div class="hidden md:block overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  ID
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  User ID
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Booking ID
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Service
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Start Date
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Total Price
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Total Coverage
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr
                v-for="policy in filteredPolicies"
                :key="policy.id"
                class="hover:bg-gray-50 transition-colors"
              >
                <td class="px-6 py-4 whitespace-nowrap text-sm font-mono text-gray-900">
                  {{ policy.id }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ policy.userId }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ policy.bookingId }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span
                    :class="getServiceColor(policy.service)"
                    class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                  >
                    {{ ServiceLabels[policy.service] }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ formatDate(policy.startDate) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-semibold text-blue-600">
                  {{ formatCurrency(policy.totalPrice) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-semibold text-green-600">
                  {{ formatCurrency(policy.totalCoverage) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span
                    :class="getStatusColor(policy.status)"
                    class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                  >
                    {{ PolicyStatusLabels[policy.status] || policy.status }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm">
                  <button
                    @click="viewPolicy(policy.id)"
                    class="text-blue-600 hover:text-blue-900 font-medium"
                  >
                    View
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Mobile Cards -->
        <div class="md:hidden divide-y divide-gray-200">
          <div
            v-for="policy in filteredPolicies"
            :key="policy.id"
            class="p-4 hover:bg-gray-50 transition-colors"
          >
            <div class="space-y-3">
              <div class="flex justify-between items-start">
                <div>
                  <p class="text-xs text-gray-500">Policy ID</p>
                  <p class="text-sm font-mono font-semibold text-gray-900">{{ policy.id }}</p>
                </div>
                <span
                  :class="getStatusColor(policy.status)"
                  class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                >
                  {{ PolicyStatusLabels[policy.status] || policy.status }}
                </span>
              </div>
              
              <div class="grid grid-cols-2 gap-3 text-sm">
                <div>
                  <p class="text-xs text-gray-500">User ID</p>
                  <p class="font-medium text-gray-900">{{ policy.userId }}</p>
                </div>
                <div>
                  <p class="text-xs text-gray-500">Booking ID</p>
                  <p class="font-medium text-gray-900">{{ policy.bookingId }}</p>
                </div>
              </div>

              <div class="flex items-center gap-2">
                <span
                  :class="getServiceColor(policy.service)"
                  class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                >
                  {{ ServiceLabels[policy.service] }}
                </span>
                <span class="text-sm text-gray-600">{{ formatDate(policy.startDate) }}</span>
              </div>

              <div class="grid grid-cols-2 gap-3">
                <div>
                  <p class="text-xs text-gray-500">Total Price</p>
                  <p class="text-sm font-semibold text-blue-600">{{ formatCurrency(policy.totalPrice) }}</p>
                </div>
                <div>
                  <p class="text-xs text-gray-500">Total Coverage</p>
                  <p class="text-sm font-semibold text-green-600">{{ formatCurrency(policy.totalCoverage) }}</p>
                </div>
              </div>

              <button
                @click="viewPolicy(policy.id)"
                class="w-full px-4 py-2 text-sm text-blue-600 bg-blue-50 rounded-md hover:bg-blue-100 transition-colors"
              >
                View Details
              </button>
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
