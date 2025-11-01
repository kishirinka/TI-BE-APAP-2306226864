<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navigation Bar -->
    <Vnavbar />

    <!-- Main Content -->
    <main class="max-w-7xl mx-auto pt-24 pb-16 px-4 sm:px-6 lg:px-8">
      <!-- Back Button -->
      <div class="mb-6">
        <button 
          @click="$router.push('/insurance-plans')"
          class="inline-flex items-center text-gray-600 hover:text-gray-900 font-medium"
        >
          <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
          </svg>
          Back to Insurance Plans
        </button>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
        <div class="animate-pulse">
          <div class="h-8 bg-gray-200 rounded w-1/3 mb-6"></div>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div class="space-y-4">
              <div class="h-4 bg-gray-200 rounded w-1/4"></div>
              <div class="h-6 bg-gray-200 rounded w-3/4"></div>
            </div>
            <div class="space-y-4">
              <div class="h-4 bg-gray-200 rounded w-1/4"></div>
              <div class="h-6 bg-gray-200 rounded w-1/2"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="bg-white rounded-lg shadow-sm border border-gray-200 p-8 text-center">
        <div class="text-red-500">
          <svg class="w-16 h-16 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
          <h3 class="text-lg font-semibold mb-2">Error loading insurance plan</h3>
          <p class="text-gray-600 mb-4">{{ error }}</p>
          <button 
            @click="fetchPlanDetail"
            class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg"
          >
            Try Again
          </button>
        </div>
      </div>

      <!-- Plan Detail Content -->
      <div v-else-if="plan" class="space-y-6">
        <!-- Header -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex justify-between items-start">
            <div>
              <h1 class="text-3xl font-bold text-gray-900 mb-2">Insurance Plan Details</h1>
              <p class="text-gray-600">{{ plan.planName }}</p>
            </div>
            <div class="flex gap-3">
              <router-link 
                :to="`/insurance-plans/${plan.id}/edit`"
                class="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-lg transition-colors"
              >
                Update Plan
              </router-link>
              <button 
                @click="showDeleteModal = true"
                class="bg-red-600 hover:bg-red-700 text-white font-medium py-2 px-4 rounded-lg transition-colors"
              >
                Delete Plan
              </button>
            </div>
          </div>
        </div>

        <!-- Main Content Grid -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <!-- Basic Information -->
          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <h2 class="text-xl font-semibold text-gray-900 mb-6">Basic Information</h2>
            
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-500 mb-1">ID</label>
                <p class="text-gray-900 font-medium">{{ plan.id }}</p>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-500 mb-1">Plan Name</label>
                <p class="text-gray-900">{{ plan.planName }}</p>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-500 mb-1">Provider ID</label>
                <p class="text-gray-900">{{ plan.providerId }}</p>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-500 mb-1">Plan Duration</label>
                <p class="text-gray-900">{{ plan.expiredByDays }} days</p>
              </div>
            </div>
          </div>

          <!-- Financial Information -->
          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <h2 class="text-xl font-semibold text-gray-900 mb-6">Financial Information</h2>
            
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-500 mb-1">Price</label>
                <p class="text-gray-900 font-medium text-lg text-green-600">{{ plan.price }}</p>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-500 mb-1">Coverage Amount</label>
                <p class="text-gray-900 font-medium text-lg text-blue-600">{{ plan.coverage }}</p>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-500 mb-1">Created Date</label>
                <p class="text-gray-900">{{ formatDate(plan.createdAt) }}</p>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-500 mb-1">Last Updated</label>
                <p class="text-gray-900">{{ formatDate(plan.updatedAt) }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Coverage Details -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h2 class="text-xl font-semibold text-gray-900 mb-4">Coverage Details</h2>
          <div class="bg-gray-50 rounded-lg p-4">
            <p class="text-gray-700 leading-relaxed">{{ plan.coverageDetails }}</p>
          </div>
        </div>

        <!-- Applicable Services -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h2 class="text-xl font-semibold text-gray-900 mb-4">Applicable Services</h2>
          <div class="flex flex-wrap gap-2">
            <span 
              v-for="service in plan.applicableService" 
              :key="service"
              class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium"
              :class="getServiceBadgeClass(service)"
            >
              {{ formatServiceName(service) }}
            </span>
          </div>
        </div>
      </div>
    </main>

    <!-- Delete Confirmation Modal -->
    <div v-if="showDeleteModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div class="bg-white rounded-lg max-w-md w-full p-6">
        <div class="flex items-center mb-4">
          <svg class="w-6 h-6 text-red-600 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-2.464 0L3.34 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
          </svg>
          <h3 class="text-lg font-semibold text-gray-900">Confirm Delete</h3>
        </div>
        <p class="text-gray-600 mb-6">
          Are you sure you want to delete this insurance plan? This action cannot be undone.
        </p>
        <div class="flex gap-3 justify-end">
          <button 
            @click="showDeleteModal = false"
            class="px-4 py-2 text-gray-700 hover:text-gray-900 font-medium"
            :disabled="deleting"
          >
            Cancel
          </button>
          <button 
            @click="handleDelete"
            class="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-lg font-medium transition-colors disabled:opacity-50"
            :disabled="deleting"
          >
            {{ deleting ? 'Deleting...' : 'Delete Plan' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Vnavbar from '@/components/layout/Vnavbar.vue'
import { apiService } from '@/services/apiService'
import type { InsurancePlan } from '@/interfaces/insurance'

const route = useRoute()
const router = useRouter()

// Reactive state
const plan = ref<InsurancePlan | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)
const showDeleteModal = ref(false)
const deleting = ref(false)

// Methods
const fetchPlanDetail = async () => {
  try {
    loading.value = true
    error.value = null
    const planId = route.params.id as string
    plan.value = await apiService.getInsurancePlanById(planId)
  } catch (err) {
    error.value = 'Failed to load insurance plan details'
    console.error('Error fetching plan detail:', err)
  } finally {
    loading.value = false
  }
}

const formatDate = (dateString: string): string => {
  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('id-ID', {
      day: '2-digit',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch {
    return dateString
  }
}

const formatServiceName = (service: string): string => {
  return service.charAt(0).toUpperCase() + service.slice(1).toLowerCase().replace('_', ' ')
}

const getServiceBadgeClass = (service: string): string => {
  const classes = {
    'FLIGHT': 'bg-blue-100 text-blue-800',
    'ACCOMMODATION': 'bg-green-100 text-green-800',
    'TOUR_PACKAGE': 'bg-purple-100 text-purple-800',
    'RENTALS': 'bg-orange-100 text-orange-800',
    'HEALTH': 'bg-red-100 text-red-800',
    'VEHICLE': 'bg-yellow-100 text-yellow-800',
    'TRAVEL': 'bg-indigo-100 text-indigo-800',
    'PROPERTY': 'bg-gray-100 text-gray-800',
    'LIFE': 'bg-pink-100 text-pink-800',
    'ACCIDENT': 'bg-red-100 text-red-800'
  }
  return classes[service as keyof typeof classes] || 'bg-gray-100 text-gray-800'
}

const handleUpdate = () => {
  // TODO: Navigate to update page or show update modal
  router.push(`/insurance-plans/${plan.value?.id}/edit`)
}

const handleDelete = async () => {
  if (!plan.value) return
  
  try {
    deleting.value = true
    await apiService.deleteInsurancePlan(plan.value.id)
    
    // Show success message and redirect
    alert('Insurance plan deleted successfully!')
    router.push('/insurance-plans')
  } catch (err: any) {
    console.error('Error deleting plan:', err)
    // Show specific error message from backend
    alert(err.message || 'Failed to delete insurance plan. Please try again.')
  } finally {
    deleting.value = false
    showDeleteModal.value = false
  }
}

// Lifecycle
onMounted(() => {
  fetchPlanDetail()
})
</script>

<style scoped>
/* Animation for loading */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}
</style>