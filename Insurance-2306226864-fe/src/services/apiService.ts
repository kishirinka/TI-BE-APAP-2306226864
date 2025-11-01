import type { InsurancePlan, InsurancePlanRequest } from '@/interfaces/insurance'

export interface StatisticsData {
  insurancePlans: number
  insurancePolicies: number
  processedClaims: number
}

export interface BaseResponse<T> {
  status: number
  message: string
  timestamp: string
  data: T
}

class ApiService {
  private baseURL = 'http://localhost:8080/api'

  async getStatistics(): Promise<StatisticsData> {
    try {
      const response = await fetch(`${this.baseURL}/statistics`)
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const result: BaseResponse<StatisticsData> = await response.json()
      
      if (result.status !== 200) {
        throw new Error(result.message)
      }

      return result.data
    } catch (error) {
      console.error('Error fetching statistics:', error)
      throw error
    }
  }

  // Insurance Plans API
  async getInsurancePlans(): Promise<InsurancePlan[]> {
    try {
      const response = await fetch(`${this.baseURL}/insurance-plans`)
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const result: BaseResponse<InsurancePlan[]> = await response.json()
      
      if (result.status !== 200) {
        throw new Error(result.message)
      }

      return result.data
    } catch (error) {
      console.error('Error fetching insurance plans:', error)
      throw error
    }
  }

  async getInsurancePlanById(id: string): Promise<InsurancePlan> {
    try {
      const response = await fetch(`${this.baseURL}/insurance-plans/${id}`)
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const result: BaseResponse<InsurancePlan> = await response.json()
      
      if (result.status !== 200) {
        throw new Error(result.message)
      }

      return result.data
    } catch (error) {
      console.error('Error fetching insurance plan:', error)
      throw error
    }
  }

  async createInsurancePlan(plan: InsurancePlanRequest): Promise<InsurancePlan> {
    try {
      const response = await fetch(`${this.baseURL}/insurance-plans`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(plan),
      })
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const result: BaseResponse<InsurancePlan> = await response.json()
      
      if (result.status !== 201) {
        throw new Error(result.message)
      }

      return result.data
    } catch (error) {
      console.error('Error creating insurance plan:', error)
      throw error
    }
  }

  async updateInsurancePlan(id: string, plan: InsurancePlanRequest): Promise<InsurancePlan> {
    try {
      const response = await fetch(`${this.baseURL}/insurance-plans/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(plan),
      })
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const result: BaseResponse<InsurancePlan> = await response.json()
      
      if (result.status !== 200) {
        throw new Error(result.message)
      }

      return result.data
    } catch (error) {
      console.error('Error updating insurance plan:', error)
      throw error
    }
  }

  async deleteInsurancePlan(id: string): Promise<boolean> {
    try {
      const response = await fetch(`${this.baseURL}/insurance-plans/${id}`, {
        method: 'DELETE',
      })
      
      const result: BaseResponse<string> = await response.json()
      
      if (!response.ok) {
        // Handle business logic errors (400) and other errors
        throw new Error(result.message || `HTTP error! status: ${response.status}`)
      }
      
      return result.status === 200
    } catch (error) {
      console.error('Error deleting insurance plan:', error)
      throw error
    }
  }
}

export const apiService = new ApiService()