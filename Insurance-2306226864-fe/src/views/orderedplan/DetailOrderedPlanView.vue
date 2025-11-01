<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useOrderedPlanStore } from '@/stores/orderedPlan.store';
import VButton from '@/components/common/VButton.vue';
import VDataTable from '@/components/common/VDataTable.vue';
import VClaimDetailModal from '@/components/claim/VClaimDetailModal.vue';
import { OrderedPlanStatusEnum, OrderedPlanStatusLabels, OrderedPlanStatusColors } from '@/enums/orderedPlan.enum';
import { ClaimStatusEnum, ClaimStatusLabels, ClaimStatusColors } from '@/enums/claim.enum';
import { format, isPast, parseISO } from 'date-fns';
import type { Column } from '@/interfaces/datatable.interface';
import type { Claim } from '@/interfaces/claim.interface';

// Router
const router = useRouter();
const route = useRoute();

// Store
const orderedPlanStore = useOrderedPlanStore();

// State
const orderedPlanId = ref<string>('');
const selectedClaim = ref<Claim | null>(null);
const isDetailModalOpen = ref(false);
const detailModalType = ref<'accepted' | 'rejected'>('accepted');

// Computed
const orderedPlan = computed(() => orderedPlanStore.currentOrderedPlan);
const loading = computed(() => orderedPlanStore.loading);
const error = computed(() => orderedPlanStore.error);

// Check if expired
const isExpired = computed(() => {
  if (!orderedPlan.value?.expiredDate) return false;
  try {
    return isPast(parseISO(orderedPlan.value.expiredDate));
  } catch {
    return false;
  }
});

// Check if can claim
const canClaim = computed(() => {
  if (!orderedPlan.value) return false;
  return orderedPlan.value.status === OrderedPlanStatusEnum.PAID && !isExpired.value;
});

// Claim button tooltip
const claimButtonTooltip = computed(() => {
  if (!orderedPlan.value) return '';
  if (orderedPlan.value.status !== OrderedPlanStatusEnum.PAID) {
    return 'Order must be paid before claiming';
  }
  if (isExpired.value) {
    return 'This ordered plan has expired';
  }
  return '';
});

// Table columns
const columns: Column[] = [
  {
    key: 'id',
    label: 'ID',
    sortable: true
  },
  {
    key: 'status',
    label: 'Status',
    sortable: true
  },
  {
    key: 'proof',
    label: 'Proof',
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

// Format currency
const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('id-ID', {
    style: 'currency',
    currency: 'IDR',
    minimumFractionDigits: 0
  }).format(amount);
};

// Format date
const formatDate = (date: string): string => {
  try {
    return format(new Date(date), 'dd MMMM yyyy');
  } catch {
    return 'Invalid date';
  }
};

const formatDateTime = (date: string): string => {
  try {
    return format(new Date(date), 'dd MMMM yyyy HH:mm');
  } catch {
    return 'Invalid date';
  }
};

// Truncate text
const truncateText = (text: string, maxLength: number = 50): string => {
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength) + '...';
};

// Navigate to claim
const handleNavigateToClaim = () => {
  router.push(`/ordered-plans/${orderedPlanId.value}/claim`);
};

// Navigate back to policy
const handleBack = () => {
  if (orderedPlan.value?.policyId) {
    router.push(`/policies/${orderedPlan.value.policyId}`);
  } else {
    router.push('/policies');
  }
};

// Handle view claim detail
const handleViewClaimDetail = (claim: Claim) => {
  selectedClaim.value = claim;
  detailModalType.value = claim.status === ClaimStatusEnum.ACCEPTED ? 'accepted' : 'rejected';
  isDetailModalOpen.value = true;
};

const handleCloseDetailModal = () => {
  isDetailModalOpen.value = false;
  selectedClaim.value = null;
};

// Lifecycle
onMounted(async () => {
  orderedPlanId.value = route.params.id as string;
  await orderedPlanStore.fetchOrderedPlanById(orderedPlanId.value);
  
  // Redirect if not found
  if (!orderedPlan.value && !loading.value) {
    router.push('/policies');
  }
});
</script>

<template>
  <div class="container mx-auto px-4 py-8">
    <!-- Loading State -->
    <div v-if="loading" class="flex items-center justify-center py-12">
      <div class="flex flex-col items-center gap-4">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <p class="text-gray-600">Loading ordered plan details...</p>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-lg p-6">
      <div class="flex items-start gap-3">
        <svg class="w-6 h-6 text-red-600 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
          />
        </svg>
        <div>
          <h3 class="text-red-900 font-semibold">Error</h3>
          <p class="text-red-700 text-sm mt-1">{{ error }}</p>
        </div>
      </div>
    </div>

    <!-- Content -->
    <div v-else-if="orderedPlan" class="space-y-6">
      <!-- Header -->
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 class="text-3xl font-bold text-gray-900">Ordered Plan Details</h1>
          <p class="text-gray-600 mt-1">View ordered plan information and claims</p>
        </div>
        <div class="flex gap-3">
          <VButton variant="secondary" @click="handleBack">
            Back to Policy
          </VButton>
          <div class="relative group">
            <VButton
              variant="primary"
              :disabled="!canClaim"
              @click="handleNavigateToClaim"
            >
              Create Claim
            </VButton>
            <!-- Tooltip -->
            <div
              v-if="!canClaim && claimButtonTooltip"
              class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-3 py-2 bg-gray-900 text-white text-xs rounded-md whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none z-10"
            >
              {{ claimButtonTooltip }}
              <div class="absolute top-full left-1/2 transform -translate-x-1/2 -mt-1 border-4 border-transparent border-t-gray-900"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- Ordered Plan Information -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h2 class="text-xl font-semibold text-gray-900 mb-4">Ordered Plan Information</h2>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- ID -->
          <div>
            <label class="block text-sm font-medium text-gray-600 mb-1">ID</label>
            <p class="text-base font-mono text-gray-900">{{ orderedPlan.id }}</p>
          </div>

          <!-- Status -->
          <div>
            <label class="block text-sm font-medium text-gray-600 mb-1">Status</label>
            <span
              class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold border"
              :class="{
                'bg-yellow-100 text-yellow-800 border-yellow-200': orderedPlan.status === OrderedPlanStatusEnum.CREATED,
                'bg-blue-100 text-blue-800 border-blue-200': orderedPlan.status === OrderedPlanStatusEnum.PAID,
                'bg-orange-100 text-orange-800 border-orange-200': orderedPlan.status === OrderedPlanStatusEnum.PARTIALLY_CLAIMED,
                'bg-green-100 text-green-800 border-green-200': orderedPlan.status === OrderedPlanStatusEnum.FULLY_CLAIMED
              }"
            >
              {{ OrderedPlanStatusLabels[orderedPlan.status] }}
            </span>
          </div>

          <!-- Insurance Plan Name -->
          <div>
            <label class="block text-sm font-medium text-gray-600 mb-1">Insurance Plan</label>
            <p class="text-base font-semibold text-gray-900">
              {{ orderedPlan.insurancePlan?.planName || 'N/A' }}
            </p>
          </div>

          <!-- Expired Date -->
          <div>
            <label class="block text-sm font-medium text-gray-600 mb-1">Expired Date</label>
            <p class="text-base text-gray-900" :class="{ 'text-red-600 font-semibold': isExpired }">
              {{ formatDate(orderedPlan.expiredDate) }}
              <span v-if="isExpired" class="text-red-600 text-sm ml-2">(Expired)</span>
            </p>
          </div>

          <!-- Price -->
          <div>
            <label class="block text-sm font-medium text-gray-600 mb-1">Price</label>
            <p class="text-lg font-semibold text-blue-600">
              {{ formatCurrency(orderedPlan.price) }}
            </p>
          </div>

          <!-- Coverage -->
          <div>
            <label class="block text-sm font-medium text-gray-600 mb-1">Coverage</label>
            <p class="text-lg font-semibold text-green-600">
              {{ formatCurrency(orderedPlan.coverage) }}
            </p>
          </div>

          <!-- Created At -->
          <div>
            <label class="block text-sm font-medium text-gray-600 mb-1">Created At</label>
            <p class="text-base text-gray-900">{{ formatDateTime(orderedPlan.createdAt) }}</p>
          </div>

          <!-- Updated At -->
          <div>
            <label class="block text-sm font-medium text-gray-600 mb-1">Updated At</label>
            <p class="text-base text-gray-900">{{ formatDateTime(orderedPlan.updatedAt) }}</p>
          </div>
        </div>
      </div>

      <!-- Claims Table -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h2 class="text-xl font-semibold text-gray-900">Claims</h2>
        </div>

        <VDataTable :columns="columns" :loading="false">
          <template v-if="orderedPlan.claims && orderedPlan.claims.length > 0">
            <tr
              v-for="claim in orderedPlan.claims"
              :key="claim.id"
              class="hover:bg-gray-50 transition-colors"
            >
              <!-- ID -->
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="text-sm font-mono text-gray-900">{{ claim.id }}</span>
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

              <!-- Proof -->
              <td class="px-6 py-4">
                <span class="text-sm text-gray-700">{{ truncateText(claim.proof) }}</span>
              </td>

              <!-- Created At -->
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="text-sm text-gray-600">{{ formatDateTime(claim.createdAt) }}</span>
              </td>

              <!-- Actions -->
              <td class="px-6 py-4 whitespace-nowrap">
                <VButton
                  v-if="claim.status === ClaimStatusEnum.ACCEPTED || claim.status === ClaimStatusEnum.REJECTED"
                  variant="secondary"
                  size="sm"
                  @click="handleViewClaimDetail(claim)"
                >
                  View Details
                </VButton>
                <span v-else class="text-sm text-gray-400">—</span>
              </td>
            </tr>
          </template>

          <!-- Empty State -->
          <template v-else>
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
                  <p class="text-gray-500 font-medium">No claims yet</p>
                  <p class="text-sm text-gray-400">Create a claim to get started</p>
                </div>
              </td>
            </tr>
          </template>
        </VDataTable>
      </div>
    </div>

    <!-- Claim Detail Modal -->
    <VClaimDetailModal
      v-if="selectedClaim"
      :isOpen="isDetailModalOpen"
      :type="detailModalType"
      :acceptedNote="selectedClaim.acceptedNote"
      :acceptedTimestamp="selectedClaim.acceptedTimestamp"
      :rejectionReason="selectedClaim.rejectionReason"
      :rejectionDescription="selectedClaim.rejectionDescription"
      :rejectionTimestamp="selectedClaim.rejectionTimestamp"
      @close="handleCloseDetailModal"
    />
  </div>
</template>
