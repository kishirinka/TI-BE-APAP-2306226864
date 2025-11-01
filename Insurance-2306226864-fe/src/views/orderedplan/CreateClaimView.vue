<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useOrderedPlanStore } from '@/stores/orderedPlan.store';
import { useClaimStore } from '@/stores/claim.store';
import VClaimForm from '@/components/claim/VClaimForm.vue';
import VButton from '@/components/common/VButton.vue';
import { OrderedPlanStatusEnum } from '@/enums/orderedPlan.enum';
import { toast } from 'vue-sonner';
import { isPast, parseISO } from 'date-fns';

// Router
const router = useRouter();
const route = useRoute();

// Stores
const orderedPlanStore = useOrderedPlanStore();
const claimStore = useClaimStore();

// State
const orderedPlanId = ref<string>('');
const isSubmitting = ref(false);

// Computed
const orderedPlan = computed(() => orderedPlanStore.currentOrderedPlan);
const loading = computed(() => orderedPlanStore.loading);

// Check if expired
const isExpired = computed(() => {
  if (!orderedPlan.value?.expiredDate) return false;
  try {
    return isPast(parseISO(orderedPlan.value.expiredDate));
  } catch {
    return false;
  }
});

// Check if eligible
const isEligible = computed(() => {
  if (!orderedPlan.value) return false;
  return orderedPlan.value.status === OrderedPlanStatusEnum.PAID && !isExpired.value;
});

// Handle submit
const handleSubmit = async (proof: string) => {
  if (!orderedPlan.value) return;
  
  isSubmitting.value = true;
  
  try {
    await claimStore.createClaim({
      orderedPlanId: orderedPlan.value.id,
      proof
    });
    
    // Redirect to ordered plan detail
    router.push(`/ordered-plans/${orderedPlan.value.id}`);
  } catch (error) {
    // Error already handled by store
  } finally {
    isSubmitting.value = false;
  }
};

// Handle cancel
const handleCancel = () => {
  router.push(`/ordered-plans/${orderedPlanId.value}`);
};

// Validate and redirect if not eligible
const validateEligibility = () => {
  if (!orderedPlan.value) return;
  
  if (orderedPlan.value.status !== OrderedPlanStatusEnum.PAID) {
    toast.error('Order must be paid before creating a claim');
    router.push(`/ordered-plans/${orderedPlanId.value}`);
    return;
  }
  
  if (isExpired.value) {
    toast.error('This ordered plan has expired and cannot be claimed');
    router.push(`/ordered-plans/${orderedPlanId.value}`);
    return;
  }
};

// Lifecycle
onMounted(async () => {
  orderedPlanId.value = route.params.id as string;
  
  await orderedPlanStore.fetchOrderedPlanById(orderedPlanId.value);
  
  // Redirect if not found
  if (!orderedPlan.value && !loading.value) {
    toast.error('Ordered plan not found');
    router.push('/policies');
    return;
  }
  
  // Validate eligibility
  validateEligibility();
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

    <!-- Content -->
    <div v-else-if="orderedPlan && isEligible" class="max-w-3xl mx-auto space-y-6">
      <!-- Header -->
      <div>
        <h1 class="text-3xl font-bold text-gray-900 mb-2">Create Claim</h1>
        <p class="text-gray-600">Submit a claim for your ordered insurance plan</p>
      </div>

      <!-- Form Card -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <VClaimForm
          :orderedPlan="orderedPlan"
          @submit="handleSubmit"
        />

        <!-- Loading Overlay -->
        <div
          v-if="isSubmitting"
          class="fixed inset-0 bg-gray-900 bg-opacity-50 flex items-center justify-center z-50"
        >
          <div class="bg-white rounded-lg p-6 flex flex-col items-center gap-4">
            <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            <p class="text-gray-600 font-medium">Creating claim...</p>
          </div>
        </div>
      </div>

      <!-- Cancel Button -->
      <div class="flex justify-end">
        <VButton
          variant="secondary"
          @click="handleCancel"
          :disabled="isSubmitting"
        >
          Cancel
        </VButton>
      </div>

      <!-- Help Section -->
      <div class="bg-blue-50 border border-blue-200 rounded-lg p-6">
        <div class="flex items-start gap-3">
          <svg class="w-6 h-6 text-blue-600 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
          <div class="text-sm text-blue-900">
            <p class="font-semibold mb-2">What happens after submission?</p>
            <ul class="list-disc list-inside space-y-1 text-blue-800">
              <li>Your claim will be reviewed by our team</li>
              <li>You'll receive a notification once the review is complete</li>
              <li>If accepted, the claim amount will be processed within 3-5 business days</li>
              <li>If rejected, you'll receive detailed information about the reason</li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <!-- Not Eligible State -->
    <div v-else-if="orderedPlan && !isEligible" class="max-w-2xl mx-auto">
      <div class="bg-yellow-50 border border-yellow-200 rounded-lg p-6">
        <div class="flex items-start gap-3">
          <svg class="w-6 h-6 text-yellow-600 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
            />
          </svg>
          <div>
            <h3 class="text-yellow-900 font-semibold">Cannot Create Claim</h3>
            <p class="text-yellow-800 text-sm mt-1">
              This ordered plan is not eligible for claiming.
            </p>
            <ul class="list-disc list-inside mt-2 text-sm text-yellow-800">
              <li v-if="orderedPlan.status !== OrderedPlanStatusEnum.PAID">
                Order must be paid before creating a claim
              </li>
              <li v-if="isExpired">
                This ordered plan has expired
              </li>
            </ul>
            <VButton
              variant="secondary"
              class="mt-4"
              @click="handleCancel"
            >
              Back to Ordered Plan
            </VButton>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
