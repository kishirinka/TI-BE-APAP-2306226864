export enum ClaimStatusEnum {
  WAITING_FOR_REVIEW = 'WAITING_FOR_REVIEW',
  ACCEPTED = 'ACCEPTED',
  REJECTED = 'REJECTED'
}

export const ClaimStatusLabels: Record<ClaimStatusEnum, string> = {
  [ClaimStatusEnum.WAITING_FOR_REVIEW]: 'Waiting for Review',
  [ClaimStatusEnum.ACCEPTED]: 'Accepted',
  [ClaimStatusEnum.REJECTED]: 'Rejected'
}

export const ClaimStatusColors: Record<ClaimStatusEnum, string> = {
  [ClaimStatusEnum.WAITING_FOR_REVIEW]: 'bg-yellow-100 text-yellow-800',
  [ClaimStatusEnum.ACCEPTED]: 'bg-green-100 text-green-800',
  [ClaimStatusEnum.REJECTED]: 'bg-red-100 text-red-800'
}
