export interface InsurancePlan {
  id: string
  planName: string
  providerId: string
  price: string
  coverage: string
  coverageDetails: string
  applicableService: ServiceEnum[]
  expiredByDays: number
  createdAt: string
  updatedAt: string
}

export interface InsurancePlanRequest {
  planName: string
  providerId: string
  price: number
  coverage: number
  coverageDetails: string
  applicableService: ServiceEnum[]
  expiredByDays: number
}

export enum ServiceEnum {
  HEALTH = 'HEALTH',
  VEHICLE = 'VEHICLE', 
  TRAVEL = 'TRAVEL',
  PROPERTY = 'PROPERTY',
  LIFE = 'LIFE',
  ACCIDENT = 'ACCIDENT',
  ACCOMMODATION = 'ACCOMMODATION',
  FLIGHT = 'FLIGHT',
  TOUR_PACKAGE = 'TOUR_PACKAGE',
  RENTALS = 'RENTALS'
}

export interface BaseResponse<T> {
  status: number
  message: string
  timestamp: string
  data: T
}