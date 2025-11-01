package apap.ti._5.Insurance_2306226864_be.service;

import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
import java.util.List;

public interface OrderedPlanService {
    OrderedPlan getOrderedPlanById(String id);
    OrderedPlan createOrderedPlan(OrderedPlan orderedPlan);
    OrderedPlan updateOrderedPlanStatus(String id, String status);
    List<OrderedPlan> getOrderedPlansByPolicyId(String policyId);
}

