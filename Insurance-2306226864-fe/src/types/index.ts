// Base Response Type
export interface BaseResponse<T = any> {
  success: boolean
  message: string
  data: T
}

// Insurance Plan Types
export interface InsurancePlan {
  id: string
  name: string
  description: string
  premium: number
  coverage: number
  duration: number
  createdAt: string
  updatedAt: string
}

export interface InsurancePlanCreateRequest {
  name: string
  description: string
  premium: number
  coverage: number
  duration: number
}

export interface InsurancePlanUpdateRequest {
  name: string
  description: string
  premium: number
  coverage: number
  duration: number
}

// Policy Types
export interface Policy {
  id: string
  policyNumber: string
  holderName: string
  holderEmail: string
  holderPhone: string
  startDate: string
  endDate: string
  createdAt: string
  updatedAt: string
  insurancePlanId: string
  insurancePlanName: string
}

export interface PolicyCreateRequest {
  policyNumber: string
  holderName: string
  holderEmail: string
  holderPhone: string
  startDate: string
  endDate: string
  insurancePlanId: string
}

export interface PolicyUpdateRequest {
  policyNumber: string
  holderName: string
  holderEmail: string
  holderPhone: string
  startDate: string
  endDate: string
  insurancePlanId: string
}

// Claim Types
export interface Claim {
  id: string
  claimNumber: string
  description: string
  amount: number
  claimDate: string
  createdAt: string
  updatedAt: string
  policyId: string
  policyNumber: string
  policyHolderName: string
}

export interface ClaimCreateRequest {
  claimNumber: string
  description: string
  amount: number
  claimDate: string
  policyId: string
}

export interface ClaimUpdateRequest {
  claimNumber: string
  description: string
  amount: number
  claimDate: string
  policyId: string
}

// Ordered Plan Types
export interface OrderedPlan {
  id: string
  customerName: string
  customerEmail: string
  customerPhone: string
  orderDate: string
  createdAt: string
  updatedAt: string
  insurancePlanId: string
  insurancePlanName: string
  insurancePlanPremium: number
}

export interface OrderedPlanCreateRequest {
  customerName: string
  customerEmail: string
  customerPhone: string
  orderDate: string
  insurancePlanId: string
}

export interface OrderedPlanUpdateRequest {
  customerName: string
  customerEmail: string
  customerPhone: string
  orderDate: string
  insurancePlanId: string
}

// Common Types
export interface TableColumn {
  key: string
  title: string
  sortable?: boolean
  width?: string
  align?: 'left' | 'center' | 'right'
  formatter?: (value: any) => string
}

export interface SelectOption {
  label: string
  value: string | number
}