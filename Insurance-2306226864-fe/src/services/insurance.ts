import type { InsurancePlanRequest, InsurancePlan, BaseResponse } from '@/interfaces/insurance'

export interface StatisticsData {
  insurancePlans: number
  insurancePolicies: number
  processedClaims: number
}

class InsuranceService {
  private baseURL = 'http://localhost:8080/api'

  async getStatistics(): Promise<BaseResponse<StatisticsData>> {
    try {
      const response = await fetch(`${this.baseURL}/statistics`)
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const result: BaseResponse<StatisticsData> = await response.json()
      return result
    } catch (error) {
      console.error('Error fetching statistics:', error)
      throw error
    }
  }

  // Insurance Plans API
  async getInsurancePlans(): Promise<BaseResponse<InsurancePlan[]>> {
    try {
      const response = await fetch(`${this.baseURL}/insurance-plans`)
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const result: BaseResponse<InsurancePlan[]> = await response.json()
      return result
    } catch (error) {
      console.error('Error fetching insurance plans:', error)
      throw error
    }
  }

  async getInsurancePlan(id: string): Promise<BaseResponse<InsurancePlan>> {
    try {
      const response = await fetch(`${this.baseURL}/insurance-plans/${id}`)
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const result: BaseResponse<InsurancePlan> = await response.json()
      return result
    } catch (error) {
      console.error('Error fetching insurance plan:', error)
      throw error
    }
  }

  async createInsurancePlan(plan: InsurancePlanRequest): Promise<BaseResponse<InsurancePlan>> {
    try {
      const response = await fetch(`${this.baseURL}/insurance-plans`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(plan)
      })
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const result: BaseResponse<InsurancePlan> = await response.json()
      return result
    } catch (error) {
      console.error('Error creating insurance plan:', error)
      throw error
    }
  }

  async updateInsurancePlan(id: string, plan: InsurancePlanRequest): Promise<BaseResponse<InsurancePlan>> {
    try {
      const response = await fetch(`${this.baseURL}/insurance-plans/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(plan)
      })
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const result: BaseResponse<InsurancePlan> = await response.json()
      return result
    } catch (error) {
      console.error('Error updating insurance plan:', error)
      throw error
    }
  }

  async deleteInsurancePlan(id: string): Promise<BaseResponse<void>> {
    try {
      const response = await fetch(`${this.baseURL}/insurance-plans/${id}`, {
        method: 'DELETE'
      })
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const result: BaseResponse<void> = await response.json()
      return result
    } catch (error) {
      console.error('Error deleting insurance plan:', error)
      throw error
    }
  }
}

export const insuranceService = new InsuranceService()