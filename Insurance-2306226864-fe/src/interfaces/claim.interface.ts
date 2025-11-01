export interface Claim {
  id: string;
  status: string;
  proof: string;
  rejectionReason?: string;
  rejectionDescription?: string;
  rejectionTimestamp?: string;
  acceptedNote?: string;
  acceptedTimestamp?: string;
  insurancePlanName: string;
  daysSinceClaimed?: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateClaimRequest {
  orderedPlanId: string;
  proof: string;
}

export interface ProcessClaimRequest {
  action: 'ACCEPT' | 'REJECT';
  note?: string;
  rejectionReason?: string;
  rejectionDescription?: string;
}
