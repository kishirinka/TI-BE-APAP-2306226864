<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navigation Bar -->
    <Vnavbar />

    <!-- Main Content -->
    <main class="max-w-4xl mx-auto pt-24 pb-16 px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900">Update Insurance Plan</h1>
        <p class="text-gray-600 mt-2">Update existing Insurance Plan in the system</p>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
        <div class="animate-pulse space-y-6">
          <div class="h-6 bg-gray-200 rounded w-1/4"></div>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="h-10 bg-gray-200 rounded"></div>
            <div class="h-10 bg-gray-200 rounded"></div>
          </div>
          <div class="h-32 bg-gray-200 rounded"></div>
          <div class="h-10 bg-gray-200 rounded w-1/3"></div>
        </div>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="bg-white rounded-lg shadow-sm border border-red-200 p-8">
        <div class="text-center">
          <div class="text-red-500 mb-4">
            <svg class="w-16 h-16 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
          </div>
          <h3 class="text-lg font-medium text-red-900 mb-2">Error Loading Insurance Plan</h3>
          <p class="text-red-600 mb-4">{{ error }}</p>
          <button 
            @click="fetchPlan" 
            class="bg-red-600 hover:bg-red-700 text-white font-medium py-2 px-4 rounded-lg"
          >
            Try Again
          </button>
        </div>
      </div>

      <!-- Update Form -->
      <div v-else>
        <InsurancePlanForm
          mode="update"
          :initial-data="formData"
          :submitting="submitting"
          @submit="handleUpdate"
          @cancel="handleCancel"
        />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Vnavbar from '@/components/layout/Vnavbar.vue'
import InsurancePlanForm from '@/components/forms/InsurancePlanForm.vue'
import { insuranceService } from '@/services/insurance'
import type { InsurancePlanResponse, InsurancePlanRequest } from '@/interfaces/insurance'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const submitting = ref(false)
const error = ref('')
const formData = ref<InsurancePlanResponse | null>(null)

const fetchPlan = async () => {
  loading.value = true
  error.value = ''
  
  try {
    const planId = route.params.id as string
    const response = await insuranceService.getInsurancePlan(planId)
    
    if (response.success && response.data) {
      formData.value = response.data
    } else {
      error.value = response.message || 'Failed to load insurance plan'
    }
  } catch (err: any) {
    error.value = err.response?.data?.message || 'An error occurred while loading the insurance plan'
    console.error('Error fetching insurance plan:', err)
  } finally {
    loading.value = false
  }
}

const handleUpdate = async (updatedPlan: InsurancePlanRequest) => {
  submitting.value = true
  
  try {
    const planId = route.params.id as string
    const response = await insuranceService.updateInsurancePlan(planId, updatedPlan)
    
    if (response.success) {
      // Show success notification (you can implement your notification system here)
      console.log('Insurance plan updated successfully')
      
      // Navigate back to detail view or plans list
      router.push(`/insurance-plans/${planId}`)
    } else {
      error.value = response.message || 'Failed to update insurance plan'
    }
  } catch (err: any) {
    error.value = err.response?.data?.message || 'An error occurred while updating the insurance plan'
    console.error('Error updating insurance plan:', err)
  } finally {
    submitting.value = false
  }
}

const handleCancel = () => {
  const planId = route.params.id as string
  router.push(`/insurance-plans/${planId}`)
}

onMounted(() => {
  fetchPlan()
})
</script>