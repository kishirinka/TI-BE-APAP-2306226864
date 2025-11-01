export enum PolicyStatusEnum {
  CREATED = 'CREATED',
  PAID = 'PAID',
  PARTIALLY_CLAIMED = 'PARTIALLY_CLAIMED',
  FULLY_CLAIMED = 'FULLY_CLAIMED',
  EXPIRED = 'EXPIRED'
}

export const PolicyStatusLabels: Record<PolicyStatusEnum, string> = {
  [PolicyStatusEnum.CREATED]: 'Created',
  [PolicyStatusEnum.PAID]: 'Paid',
  [PolicyStatusEnum.PARTIALLY_CLAIMED]: 'Partially Claimed',
  [PolicyStatusEnum.FULLY_CLAIMED]: 'Fully Claimed',
  [PolicyStatusEnum.EXPIRED]: 'Expired'
}

export const PolicyStatusColors: Record<PolicyStatusEnum, string> = {
  [PolicyStatusEnum.CREATED]: 'bg-yellow-100 text-yellow-800',
  [PolicyStatusEnum.PAID]: 'bg-blue-100 text-blue-800',
  [PolicyStatusEnum.PARTIALLY_CLAIMED]: 'bg-orange-100 text-orange-800',
  [PolicyStatusEnum.FULLY_CLAIMED]: 'bg-green-100 text-green-800',
  [PolicyStatusEnum.EXPIRED]: 'bg-red-100 text-red-800'
}
