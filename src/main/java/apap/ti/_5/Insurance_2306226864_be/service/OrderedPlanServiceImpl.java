package apap.ti._5.Insurance_2306226864_be.service;

import apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.OrderedPlanStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.PolicyStatusEnum;
import apap.ti._5.Insurance_2306226864_be.model.Claim;
import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
import apap.ti._5.Insurance_2306226864_be.model.Policy;
import apap.ti._5.Insurance_2306226864_be.repository.ClaimRepository;
import apap.ti._5.Insurance_2306226864_be.repository.OrderedPlanRepository;
import apap.ti._5.Insurance_2306226864_be.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderedPlanServiceImpl implements OrderedPlanService {

    @Autowired
    private OrderedPlanRepository orderedPlanRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private ClaimRepository claimRepository;

    // ========================== GET BY ID ==========================
    @Override
    public OrderedPlan getOrderedPlanById(String id) {
        return orderedPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderedPlan with ID " + id + " not found"));
    }

    // ========================== CREATE ==========================
    @Override
    public OrderedPlan createOrderedPlan(OrderedPlan orderedPlan) {
        // Generate ID: OP + (count + 1)
        long count = orderedPlanRepository.count();
        orderedPlan.setId("OP" + (count + 1));

        // Set timestamps
        orderedPlan.setCreatedAt(LocalDateTime.now());
        orderedPlan.setUpdatedAt(LocalDateTime.now());

        return orderedPlanRepository.save(orderedPlan);
    }

    // ========================== UPDATE STATUS ==========================
    @Override
    public OrderedPlan updateOrderedPlanStatus(String id, String status) {
        // Get ordered plan
        OrderedPlan orderedPlan = getOrderedPlanById(id);

        // Parse status
        OrderedPlanStatusEnum newStatus;
        try {
            newStatus = OrderedPlanStatusEnum.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }

        // Check business rules based on claims
        if (orderedPlan.getClaims() != null && !orderedPlan.getClaims().isEmpty()) {
            int rejectedClaimsCount = 0;
            boolean hasAcceptedClaim = false;

            for (Claim claim : orderedPlan.getClaims()) {
                if (claim.getStatus() == ClaimStatusEnum.REJECTED) {
                    rejectedClaimsCount++;
                }
                if (claim.getStatus() == ClaimStatusEnum.ACCEPTED) {
                    hasAcceptedClaim = true;
                }
            }

            // Business Rule 1: If 3 claims REJECTED, set status to REJECTED
            if (rejectedClaimsCount >= 3) {
                newStatus = OrderedPlanStatusEnum.REJECTED;
            }

            // Business Rule 2: If 1 claim ACCEPTED, set status to CLAIMED
            if (hasAcceptedClaim) {
                newStatus = OrderedPlanStatusEnum.CLAIMED;
            }
        }

        // Update ordered plan status
        orderedPlan.setStatus(newStatus);
        orderedPlan.setUpdatedAt(LocalDateTime.now());
        orderedPlan = orderedPlanRepository.save(orderedPlan);

        // Update policy status if needed
        updatePolicyStatus(orderedPlan.getPolicy());

        return orderedPlan;
    }

    // ========================== GET BY POLICY ID ==========================
    @Override
    public List<OrderedPlan> getOrderedPlansByPolicyId(String policyId) {
        return orderedPlanRepository.findByPolicy_Id(policyId);
    }

    // ========================== HELPER: UPDATE POLICY STATUS ==========================
    private void updatePolicyStatus(Policy policy) {
        if (policy == null || policy.getOrderedPlans() == null || policy.getOrderedPlans().isEmpty()) {
            return;
        }

        int claimedCount = 0;
        int totalOrderedPlans = policy.getOrderedPlans().size();

        // Count claimed ordered plans
        for (OrderedPlan orderedPlan : policy.getOrderedPlans()) {
            if (orderedPlan.getStatus() == OrderedPlanStatusEnum.CLAIMED) {
                claimedCount++;
            }
        }

        // Update policy status based on claimed count
        if (claimedCount == totalOrderedPlans && totalOrderedPlans > 0) {
            // All ordered plans are claimed
            policy.setStatus(PolicyStatusEnum.FULLY_CLAIMED);
            policy.setUpdatedAt(LocalDateTime.now());
            policyRepository.save(policy);
        } else if (claimedCount > 0) {
            // At least one ordered plan is claimed but not all
            policy.setStatus(PolicyStatusEnum.PARTIALLY_CLAIMED);
            policy.setUpdatedAt(LocalDateTime.now());
            policyRepository.save(policy);
        }
        // If no ordered plans are claimed, keep current status (PAID or CREATED)
    }
}
