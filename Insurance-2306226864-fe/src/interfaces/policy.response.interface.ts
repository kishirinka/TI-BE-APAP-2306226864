export interface PolicyRequest {
  id?: string;
  userId: string;
  bookingId: string;
  service: string;
  insurancePlanIds: string[];
}