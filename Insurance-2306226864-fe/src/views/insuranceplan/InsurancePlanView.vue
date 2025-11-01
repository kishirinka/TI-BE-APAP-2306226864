<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import Vnavbar from '@/components/layout/Vnavbar.vue';
import VDeleteInsurancePlanButton from '@/components/insuranceplan/VDeleteInsurancePlanButton.vue';
import { useInsurancePlanStore } from '@/stores/insurancePlan/insurancePlan.store';
import { ServiceLabels } from '@/enums';
import type { InsurancePlan } from '@/interfaces/insurancePlan.interface';

const router = useRouter();
const insurancePlanStore = useInsurancePlanStore();

const searchQuery = ref('');
const pageSize = ref(10);
const currentPage = ref(1);

// Fetch data on mount
onMounted(async () => {
  await insurancePlanStore.fetchInsurancePlans();
});

// Format currency
const formatCurrency = (value: number): string => {
  return new Intl.NumberFormat('id-ID', {
    style: 'currency',
    currency: 'IDR',
    minimumFractionDigits: 0
  }).format(value);
};

// Filter plans based on search query
const filteredPlans = computed(() => {
  if (!searchQuery.value) {
    return insurancePlanStore.insurancePlans;
  }
  
  const query = searchQuery.value.toLowerCase();
  return insurancePlanStore.insurancePlans.filter(plan =>
    plan.id.toLowerCase().includes(query) ||
    plan.planName.toLowerCase().includes(query) ||
    plan.providerId.toLowerCase().includes(query)
  );
});

// Paginated plans
const paginatedPlans = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredPlans.value.slice(start, end);
});

// Total pages
const totalPages = computed(() => {
  return Math.ceil(filteredPlans.value.length / pageSize.value);
});

// Handle page change
const goToPage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
};

// Handle delete success
const handleDeleted = () => {
  // Store already removed the item from insurancePlans array
  // Just reset to first page if current page is now empty
  if (paginatedPlans.value.length === 0 && currentPage.value > 1) {
    currentPage.value = 1;
  }
};

// Navigate to create page
const goToCreate = () => {
  router.push('/insurance-plans/create');
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
    <main class="max-w-7xl mx-auto pt-24 pb-16 px-4 sm:px-6 lg:px-8">
      <!-- Header Section -->
      <div class="mb-8">
        <div class="flex justify-between items-center">
          <div>
            <h1 class="text-3xl font-bold text-gray-900">Insurance Plans</h1>
            <p class="text-gray-600 mt-2">Manage and monitor all insurance plans</p>
          </div>
          <button
            @click="goToCreate"
            class="px-4 py-2 text-white bg-blue-600 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-colors flex items-center gap-2"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            Create New Plan
          </button>
        </div>
      </div>

      <!-- Filters and Search -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
        <div class="flex flex-col md:flex-row gap-4 items-center justify-between">
          <div class="flex gap-4 items-center">
            <div class="flex items-center gap-2">
              <label class="text-sm font-medium text-gray-700">Show:</label>
              <select
                v-model="pageSize"
                class="border border-gray-300 rounded-md px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                @change="currentPage = 1"
              >
                <option :value="10">10</option>
                <option :value="25">25</option>
                <option :value="50">50</option>
              </select>
              <span class="text-sm text-gray-500">entries</span>
            </div>
          </div>

          <div class="relative">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search plans..."
              class="border border-gray-300 rounded-md px-4 py-2 pr-10 w-64 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              @input="currentPage = 1"
            />
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
      </div>

      <!-- Loading State -->
      <div v-if="insurancePlanStore.loading" class="bg-white rounded-lg shadow-sm border border-gray-200 p-12">
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
          <p class="text-gray-600">Loading insurance plans...</p>
        </div>
      </div>

      <!-- Empty State -->
      <div
        v-else-if="filteredPlans.length === 0"
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
          <div class="text-center">
            <h3 class="text-lg font-semibold text-gray-900">No insurance plans found</h3>
            <p class="text-gray-600 mt-1">
              {{ searchQuery ? 'Try adjusting your search' : 'Get started by creating a new insurance plan' }}
            </p>
          </div>
          <button
            v-if="!searchQuery"
            @click="goToCreate"
            class="mt-4 px-4 py-2 text-white bg-blue-600 rounded-md hover:bg-blue-700 transition-colors"
          >
            Create New Plan
          </button>
        </div>
      </div>

      <!-- Data Table -->
      <div v-else class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  ID
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Plan Name
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Provider ID
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Price
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Coverage
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Applicable Service
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr
                v-for="plan in paginatedPlans"
                :key="plan.id"
                class="hover:bg-gray-50 transition-colors"
              >
                <!-- ID -->
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                  {{ plan.id }}
                </td>

                <!-- Plan Name -->
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ plan.planName }}
                </td>

                <!-- Provider ID -->
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                  {{ plan.providerId }}
                </td>

                <!-- Price -->
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ formatCurrency(plan.price) }}
                </td>

                <!-- Coverage -->
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ formatCurrency(plan.coverage) }}
                </td>

                <!-- Applicable Service -->
                <td class="px-6 py-4 text-sm">
                  <div class="flex flex-wrap gap-1">
                    <span
                      v-for="service in plan.applicableService"
                      :key="service"
                      :class="getServiceColor(service)"
                      class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                    >
                      {{ ServiceLabels[service] || service }}
                    </span>
                  </div>
                </td>

                <!-- Actions -->
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  <div class="flex items-center gap-2">
                    <RouterLink
                      :to="`/insurance-plans/${plan.id}`"
                      class="text-blue-600 hover:text-blue-900 transition-colors"
                    >
                      View
                    </RouterLink>
                    <span class="text-gray-300">|</span>
                    <RouterLink
                      :to="`/insurance-plans/${plan.id}/edit`"
                      class="text-indigo-600 hover:text-indigo-900 transition-colors"
                    >
                      Edit
                    </RouterLink>
                    <span class="text-gray-300">|</span>
                    <VDeleteInsurancePlanButton
                      :planId="plan.id"
                      :planName="plan.planName"
                      @deleted="handleDeleted"
                    />
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <div
          v-if="totalPages > 1"
          class="bg-white px-4 py-3 flex items-center justify-between border-t border-gray-200 sm:px-6"
        >
          <div class="flex-1 flex justify-between sm:hidden">
            <button
              @click="goToPage(currentPage - 1)"
              :disabled="currentPage === 1"
              class="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Previous
            </button>
            <button
              @click="goToPage(currentPage + 1)"
              :disabled="currentPage === totalPages"
              class="ml-3 relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Next
            </button>
          </div>
          <div class="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
            <div>
              <p class="text-sm text-gray-700">
                Showing
                <span class="font-medium">{{ (currentPage - 1) * pageSize + 1 }}</span>
                to
                <span class="font-medium">{{ Math.min(currentPage * pageSize, filteredPlans.length) }}</span>
                of
                <span class="font-medium">{{ filteredPlans.length }}</span>
                results
              </p>
            </div>
            <div>
              <nav class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
                <button
                  @click="goToPage(currentPage - 1)"
                  :disabled="currentPage === 1"
                  class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <span class="sr-only">Previous</span>
                  <svg class="h-5 w-5" fill="currentColor" viewBox="0 0 20 20">
                    <path
                      fill-rule="evenodd"
                      d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z"
                      clip-rule="evenodd"
                    />
                  </svg>
                </button>

                <button
                  v-for="page in totalPages"
                  :key="page"
                  @click="goToPage(page)"
                  :class="[
                    page === currentPage
                      ? 'z-10 bg-blue-50 border-blue-500 text-blue-600'
                      : 'bg-white border-gray-300 text-gray-500 hover:bg-gray-50',
                    'relative inline-flex items-center px-4 py-2 border text-sm font-medium'
                  ]"
                >
                  {{ page }}
                </button>

                <button
                  @click="goToPage(currentPage + 1)"
                  :disabled="currentPage === totalPages"
                  class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <span class="sr-only">Next</span>
                  <svg class="h-5 w-5" fill="currentColor" viewBox="0 0 20 20">
                    <path
                      fill-rule="evenodd"
                      d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z"
                      clip-rule="evenodd"
                    />
                  </svg>
                </button>
              </nav>
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
