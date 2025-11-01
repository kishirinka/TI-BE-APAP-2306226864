export interface InsuranceRequest {
  id?: string;
  providerId: string;
  planName: string;
  price: string;
  coverage: string;
  coverageDetails: string;
  applicableService: string[];
  expiredByDays: string;
}
