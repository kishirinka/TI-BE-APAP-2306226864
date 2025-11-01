export interface ClaimRequest {
  claimId?: string;
  decision: string;
  rejectionReason?: string;
  rejectionDescription?: string;
  acceptedNote?: string;
}
