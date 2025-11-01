import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { 
  Policy, 
  PolicyCreateRequest, 
  PolicyUpdateRequest,
  BaseResponse 
} from '@/types'

export const usePolicyStore = defineStore('policy', () => {
  // State
  const policies = ref<Policy[]>([])
  const currentPolicy = ref<Policy | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const getAllPolicies = computed(() => policies.value)
  const getPolicyById = computed(() => (id: string) => 
    policies.value.find(policy => policy.id === id)
  )
  const isLoading = computed(() => loading.value)
  const hasError = computed(() => error.value !== null)

  // Actions
  const fetchAllPolicies = async (): Promise<void> => {
    loading.value = true
    error.value = null
    
    try {
      const response = await fetch('/api/policies', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const data: BaseResponse<Policy[]> = await response.json()
      
      if (data.success) {
        policies.value = data.data || []
      } else {
        throw new Error(data.message || 'Failed to fetch policies')
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'An error occurred'
      console.error('Error fetching policies:', err)
    } finally {
      loading.value = false
    }
  }

  const fetchPolicyById = async (id: string): Promise<Policy | null> => {
    loading.value = true
    error.value = null
    
    try {
      const response = await fetch(`/api/policies/${id}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const data: BaseResponse<Policy> = await response.json()
      
      if (data.success) {
        currentPolicy.value = data.data || null
        return data.data || null
      } else {
        throw new Error(data.message || 'Failed to fetch policy')
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'An error occurred'
      console.error('Error fetching policy:', err)
      return null
    } finally {
      loading.value = false
    }
  }

  const createPolicy = async (policyData: PolicyCreateRequest): Promise<Policy | null> => {
    loading.value = true
    error.value = null
    
    try {
      const response = await fetch('/api/policies', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(policyData),
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const data: BaseResponse<Policy> = await response.json()
      
      if (data.success && data.data) {
        policies.value.push(data.data)
        return data.data
      } else {
        throw new Error(data.message || 'Failed to create policy')
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'An error occurred'
      console.error('Error creating policy:', err)
      return null
    } finally {
      loading.value = false
    }
  }

  const updatePolicy = async (id: string, policyData: PolicyUpdateRequest): Promise<Policy | null> => {
    loading.value = true
    error.value = null
    
    try {
      const response = await fetch(`/api/policies/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(policyData),
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const data: BaseResponse<Policy> = await response.json()
      
      if (data.success && data.data) {
        const index = policies.value.findIndex(policy => policy.id === id)
        if (index !== -1) {
          policies.value[index] = data.data
        }
        currentPolicy.value = data.data
        return data.data
      } else {
        throw new Error(data.message || 'Failed to update policy')
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'An error occurred'
      console.error('Error updating policy:', err)
      return null
    } finally {
      loading.value = false
    }
  }

  const deletePolicy = async (id: string): Promise<boolean> => {
    loading.value = true
    error.value = null
    
    try {
      const response = await fetch(`/api/policies/${id}`, {
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
        policies.value = policies.value.filter(policy => policy.id !== id)
        if (currentPolicy.value?.id === id) {
          currentPolicy.value = null
        }
        return true
      } else {
        throw new Error(data.message || 'Failed to delete policy')
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'An error occurred'
      console.error('Error deleting policy:', err)
      return false
    } finally {
      loading.value = false
    }
  }

  const clearError = (): void => {
    error.value = null
  }

  const setCurrentPolicy = (policy: Policy | null): void => {
    currentPolicy.value = policy
  }

  return {
    // State
    policies,
    currentPolicy,
    loading,
    error,
    
    // Getters
    getAllPolicies,
    getPolicyById,
    isLoading,
    hasError,
    
    // Actions
    fetchAllPolicies,
    fetchPolicyById,
    createPolicy,
    updatePolicy,
    deletePolicy,
    clearError,
    setCurrentPolicy
  }
})