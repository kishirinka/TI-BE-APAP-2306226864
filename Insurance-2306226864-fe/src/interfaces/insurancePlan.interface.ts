export interface InsurancePlan {
  id: string;
  providerId: string;
  planName: string;
  price: number;
  coverage: number;
  coverageDetails: string;
  applicableService: string[];
  expiredByDays: number;
  createdAt: string;
  updatedAt: string;
}

export interface InsurancePlanRequest {
  id?: string;
  providerId?: string;
  planName: string;
  price: number;
  coverage: number;
  coverageDetails: string;
  applicableService: string[];
  expiredByDays: number;
}

export interface CreateInsurancePlanRequest {
  providerId: string;
  planName: string;
  price: number;
  coverage: number;
  coverageDetails: string;
  applicableService: string[];
  expiredByDays: number;
}

export interface UpdateInsurancePlanRequest {
  planName: string;
  price: number;
  coverage: number;
  coverageDetails: string;
  applicableService: string[];
  expiredByDays: number;
}

