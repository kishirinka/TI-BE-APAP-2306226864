export interface Policy {
  id: string;
  bookingId: string;
  userId: string;
  startDate: string;
  status: string;
  service: string;
  totalCoverage: number;
  totalPrice: number;
  orderedPlans: OrderedPlan[];
  createdAt: string;
  updatedAt: string;
}

export interface OrderedPlan {
  id: string;
  status: string;
  expiredDate: string;
  insurancePlan: {
    id: string;
    planName: string;
    price: number;
    coverage: number;
  };
  createdAt: string;
  updatedAt: string;
}

export interface CreatePolicyRequest {
  userId: string;
  bookingId: string;
  service: string;
  insurancePlanIds: string[];
}
