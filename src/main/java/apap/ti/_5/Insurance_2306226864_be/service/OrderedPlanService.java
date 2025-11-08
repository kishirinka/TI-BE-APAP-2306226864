package apap.ti._5.Insurance_2306226864_be.service;

import java.util.List;

import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;

public interface OrderedPlanService {
    OrderedPlan getOrderedPlanById(String id);
    OrderedPlan updateOrderedPlanStatus(String id, String status);
    List<OrderedPlan> getOrderedPlansByPolicyId(String policyId);
}

