export enum OrderedPlanStatusEnum {
  ORDERED = 'ORDERED',
  PAID = 'PAID',
  WAITING_FOR_REVIEW = 'WAITING_FOR_REVIEW',
  CLAIMED = 'CLAIMED',
  REJECTED = 'REJECTED',
  EXPIRED = 'EXPIRED'
}

export const OrderedPlanStatusLabels: Record<OrderedPlanStatusEnum, string> = {
  [OrderedPlanStatusEnum.ORDERED]: 'Ordered',
  [OrderedPlanStatusEnum.PAID]: 'Paid',
  [OrderedPlanStatusEnum.WAITING_FOR_REVIEW]: 'Waiting for Review',
  [OrderedPlanStatusEnum.CLAIMED]: 'Claimed',
  [OrderedPlanStatusEnum.REJECTED]: 'Rejected',
  [OrderedPlanStatusEnum.EXPIRED]: 'Expired'
}

export const OrderedPlanStatusColors: Record<OrderedPlanStatusEnum, string> = {
  [OrderedPlanStatusEnum.ORDERED]: 'bg-gray-100 text-gray-800',
  [OrderedPlanStatusEnum.PAID]: 'bg-blue-100 text-blue-800',
  [OrderedPlanStatusEnum.WAITING_FOR_REVIEW]: 'bg-yellow-100 text-yellow-800',
  [OrderedPlanStatusEnum.CLAIMED]: 'bg-green-100 text-green-800',
  [OrderedPlanStatusEnum.REJECTED]: 'bg-red-100 text-red-800',
  [OrderedPlanStatusEnum.EXPIRED]: 'bg-gray-100 text-gray-600'
}
