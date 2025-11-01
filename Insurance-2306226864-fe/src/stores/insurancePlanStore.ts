import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { 
  InsurancePlan, 
  InsurancePlanCreateRequest, 
  InsurancePlanUpdateRequest,
  BaseResponse 
} from '@/types'

export const useInsurancePlanStore = defineStore('insurancePlan', () => {
  // State
  const insurancePlans = ref<InsurancePlan[]>([])
  const currentPlan = ref<InsurancePlan | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const getAllPlans = computed(() => insurancePlans.value)
  const getPlanById = computed(() => (id: string) => 
    insurancePlans.value.find(plan => plan.id === id)
  )
  const isLoading = computed(() => loading.value)
  const hasError = computed(() => error.value !== null)

  // Actions
  const fetchAllPlans = async (): Promise<void> => {
    loading.value = true
    error.value = null
    
    try {
      const response = await fetch('/api/insurance-plans', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const data: BaseResponse<InsurancePlan[]> = await response.json()
      
      if (data.success) {
        insurancePlans.value = data.data || []
      } else {
        throw new Error(data.message || 'Failed to fetch insurance plans')
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'An error occurred'
      console.error('Error fetching insurance plans:', err)
    } finally {
      loading.value = false
    }
  }

  const fetchPlanById = async (id: string): Promise<InsurancePlan | null> => {
    loading.value = true
    error.value = null
    
    try {
      const response = await fetch(`/api/insurance-plans/${id}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const data: BaseResponse<InsurancePlan> = await response.json()
      
      if (data.success) {
        currentPlan.value = data.data || null
        return data.data || null
      } else {
        throw new Error(data.message || 'Failed to fetch insurance plan')
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'An error occurred'
      console.error('Error fetching insurance plan:', err)
      return null
    } finally {
      loading.value = false
    }
  }

  const createPlan = async (planData: InsurancePlanCreateRequest): Promise<InsurancePlan | null> => {
    loading.value = true
    error.value = null
    
    try {
      const response = await fetch('/api/insurance-plans', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(planData),
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const data: BaseResponse<InsurancePlan> = await response.json()
      
      if (data.success && data.data) {
        insurancePlans.value.push(data.data)
        return data.data
      } else {
        throw new Error(data.message || 'Failed to create insurance plan')
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'An error occurred'
      console.error('Error creating insurance plan:', err)
      return null
    } finally {
      loading.value = false
    }
  }

  const updatePlan = async (id: string, planData: InsurancePlanUpdateRequest): Promise<InsurancePlan | null> => {
    loading.value = true
    error.value = null
    
    try {
      const response = await fetch(`/api/insurance-plans/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(planData),
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const data: BaseResponse<InsurancePlan> = await response.json()
      
      if (data.success && data.data) {
        const index = insurancePlans.value.findIndex(plan => plan.id === id)
        if (index !== -1) {
          insurancePlans.value[index] = data.data
        }
        currentPlan.value = data.data
        return data.data
      } else {
        throw new Error(data.message || 'Failed to update insurance plan')
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'An error occurred'
      console.error('Error updating insurance plan:', err)
      return null
    } finally {
      loading.value = false
    }
  }

  const deletePlan = async (id: string): Promise<boolean> => {
    loading.value = true
    error.value = null
    
    try {
      const response = await fetch(`/api/insurance-plans/${id}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        },
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const data: BaseResponse<void> = await response.json()
      
      if (data.success) {
        insurancePlans.value = insurancePlans.value.filter(plan => plan.id !== id)
        if (currentPlan.value?.id === id) {
          currentPlan.value = null
        }
        return true
      } else {
        throw new Error(data.message || 'Failed to delete insurance plan')
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'An error occurred'
      console.error('Error deleting insurance plan:', err)
      return false
    } finally {
      loading.value = false
    }
  }

  const clearError = (): void => {
    error.value = null
  }

  const setCurrentPlan = (plan: InsurancePlan | null): void => {
    currentPlan.value = plan
  }

  return {
    // State
    insurancePlans,
    currentPlan,
    loading,
    error,
    
    // Getters
    getAllPlans,
    getPlanById,
    isLoading,
    hasError,
    
    // Actions
    fetchAllPlans,
    fetchPlanById,
    createPlan,
    updatePlan,
    deletePlan,
    clearError,
    setCurrentPlan
  }
})