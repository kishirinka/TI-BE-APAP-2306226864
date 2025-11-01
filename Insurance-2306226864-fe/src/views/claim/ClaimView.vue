<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import { useClaimStore } from '@/stores/claim.store';
import { useInsurancePlanStore } from '@/stores/insurancePlan.store';
import VDataTable from '@/components/common/VDataTable.vue';
import VButton from '@/components/common/VButton.vue';
import VProcessClaimModal from '@/components/claim/VProcessClaimModal.vue';
import { ClaimStatusEnum, ClaimStatusLabels, ClaimStatusColors } from '@/enums/claim.enum';
import { format } from 'date-fns';
import type { Claim, ProcessClaimRequest } from '@/interfaces/claim.interface';
import type { Column } from '@/interfaces/datatable.interface';

// Stores
const claimStore = useClaimStore();
const insurancePlanStore = useInsurancePlanStore();

// State
const statusFilter = ref<string>('');
const insurancePlanFilter = ref<string>('');
const selectedClaimForProcess = ref<Claim | null>(null);
const isProcessModalOpen = ref(false);

// Computed
const claims = computed(() => claimStore.claims);
const loading = computed(() => claimStore.loading);
const insurancePlans = computed(() => insurancePlanStore.insurancePlans);

// Status filter options
const statusFilterOptions = [
  { value: '', label: 'All Status' },
  { value: ClaimStatusEnum.WAITING_FOR_REVIEW, label: ClaimStatusLabels[ClaimStatusEnum.WAITING_FOR_REVIEW] },
  { value: ClaimStatusEnum.ACCEPTED, label: ClaimStatusLabels[ClaimStatusEnum.ACCEPTED] },
  { value: ClaimStatusEnum.REJECTED, label: ClaimStatusLabels[ClaimStatusEnum.REJECTED] }
];

// Insurance plan filter options
const insurancePlanFilterOptions = computed(() => {
  const allOption = { value: '', label: 'All Insurance Plans' };
  const planOptions = insurancePlans.value.map(plan => ({
    value: plan.id,
    label: `${plan.id} - ${plan.planName}`
  }));
  return [allOption, ...planOptions];
});

// Table columns
const columns: Column[] = [
  {
    key: 'id',
    label: 'ID',
    sortable: true
  },
  {
    key: 'insurancePlanName',
    label: 'Insurance Plan',
    sortable: true
  },
  {
    key: 'status',
    label: 'Status',
    sortable: true
  },
  {
    key: 'daysSinceClaimed',
    label: 'Days Since Claimed',
    sortable: false
  },
  {
    key: 'createdAt',
    label: 'Created At',
    sortable: true
  },
  {
    key: 'actions',
    label: 'Actions',
    sortable: false
  }
];

// Format date
const formatDate = (date: string): string => {
  try {
    return format(new Date(date), 'dd MMM yyyy HH:mm');
  } catch {
    return 'Invalid date';
  }
};

// Fetch claims with filters
const fetchClaims = async () => {
  const statusParam = statusFilter.value || undefined;
  const planParam = insurancePlanFilter.value || undefined;
  await claimStore.fetchClaims(statusParam, planParam);
};

// Watch filters
watch([statusFilter, insurancePlanFilter], () => {
  fetchClaims();
});

// Handle process claim
const handleOpenProcessModal = (claim: Claim) => {
  selectedClaimForProcess.value = claim;
  isProcessModalOpen.value = true;
};

const handleProcessClaim = async (data: ProcessClaimRequest) => {
  if (!selectedClaimForProcess.value) return;
  
  await claimStore.processClaim(selectedClaimForProcess.value.id, data);
  isProcessModalOpen.value = false;
  selectedClaimForProcess.value = null;
  
  // Refresh claims
  await fetchClaims();
};

const handleCloseProcessModal = () => {
  isProcessModalOpen.value = false;
  selectedClaimForProcess.value = null;
};

// Lifecycle
onMounted(async () => {
  await Promise.all([
    claimStore.fetchClaims(),
    insurancePlanStore.fetchInsurancePlans()
  ]);
});
</script>

<template>
  <div class="container mx-auto px-4 py-8">
    <!-- Header -->
    <div class="mb-6">
      <h1 class="text-3xl font-bold text-gray-900 mb-2">Claims Management</h1>
      <p class="text-gray-600">View and process all insurance claims</p>
    </div>

    <!-- Filters -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
      <h2 class="text-lg font-semibold text-gray-900 mb-4">Filters</h2>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <!-- Status Filter -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Status
          </label>
          <select
            v-model="statusFilter"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          >
            <option
              v-for="option in statusFilterOptions"
              :key="option.value"
              :value="option.value"
            >
              {{ option.label }}
            </option>
          </select>
        </div>

        <!-- Insurance Plan Filter -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Insurance Plan
          </label>
          <select
            v-model="insurancePlanFilter"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          >
            <option
              v-for="option in insurancePlanFilterOptions"
              :key="option.value"
              :value="option.value"
            >
              {{ option.label }}
            </option>
          </select>
        </div>
      </div>
    </div>

    <!-- Data Table -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-200">
      <VDataTable :columns="columns" :loading="loading">
        <template v-if="!loading && claims.length > 0">
          <tr
            v-for="claim in claims"
            :key="claim.id"
            class="hover:bg-gray-50 transition-colors"
          >
            <!-- ID -->
            <td class="px-6 py-4 whitespace-nowrap">
              <span class="text-sm font-mono text-gray-900">{{ claim.id }}</span>
            </td>

            <!-- Insurance Plan Name -->
            <td class="px-6 py-4">
              <span class="text-sm font-medium text-gray-900">
                {{ claim.orderedPlan?.insurancePlan?.planName || 'N/A' }}
              </span>
            </td>

            <!-- Status -->
            <td class="px-6 py-4 whitespace-nowrap">
              <span
                class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold border"
                :class="{
                  'bg-yellow-100 text-yellow-800 border-yellow-200': claim.status === ClaimStatusEnum.WAITING_FOR_REVIEW,
                  'bg-green-100 text-green-800 border-green-200': claim.status === ClaimStatusEnum.ACCEPTED,
                  'bg-red-100 text-red-800 border-red-200': claim.status === ClaimStatusEnum.REJECTED
                }"
              >
                {{ ClaimStatusLabels[claim.status] }}
              </span>
            </td>

            <!-- Days Since Claimed -->
            <td class="px-6 py-4 whitespace-nowrap">
              <span
                v-if="claim.status === ClaimStatusEnum.WAITING_FOR_REVIEW && claim.daysSinceClaimed !== undefined"
                class="text-sm text-gray-900"
              >
                {{ claim.daysSinceClaimed }} {{ claim.daysSinceClaimed === 1 ? 'day' : 'days' }}
              </span>
              <span v-else class="text-sm text-gray-400">—</span>
            </td>

            <!-- Created At -->
            <td class="px-6 py-4 whitespace-nowrap">
              <span class="text-sm text-gray-600">{{ formatDate(claim.createdAt) }}</span>
            </td>

            <!-- Actions -->
            <td class="px-6 py-4 whitespace-nowrap">
              <VButton
                v-if="claim.status === ClaimStatusEnum.WAITING_FOR_REVIEW"
                variant="primary"
                size="sm"
                @click="handleOpenProcessModal(claim)"
              >
                Process
              </VButton>
              <span v-else class="text-sm text-gray-400">—</span>
            </td>
          </tr>
        </template>

        <!-- Empty State -->
        <template v-else-if="!loading && claims.length === 0">
          <tr>
            <td :colspan="columns.length" class="px-6 py-12 text-center">
              <div class="flex flex-col items-center gap-2">
                <svg class="w-12 h-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
                  />
                </svg>
                <p class="text-gray-500 font-medium">No claims found</p>
                <p class="text-sm text-gray-400">Try adjusting your filters</p>
              </div>
            </td>
          </tr>
        </template>
      </VDataTable>
    </div>

    <!-- Process Claim Modal -->
    <VProcessClaimModal
      v-if="selectedClaimForProcess"
      :isOpen="isProcessModalOpen"
      :claim="selectedClaimForProcess"
      @close="handleCloseProcessModal"
      @process="handleProcessClaim"
    />
  </div>
</template>
