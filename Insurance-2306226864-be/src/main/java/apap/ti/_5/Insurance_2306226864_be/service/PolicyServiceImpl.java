package apap.ti._5.Insurance_2306226864_be.service;

import apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.OrderedPlanStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.PolicyStatusEnum;
import apap.ti._5.Insurance_2306226864_be.model.Claim;
import apap.ti._5.Insurance_2306226864_be.model.InsurancePlan;
import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
import apap.ti._5.Insurance_2306226864_be.model.Policy;
import apap.ti._5.Insurance_2306226864_be.repository.OrderedPlanRepository;
import apap.ti._5.Insurance_2306226864_be.repository.PolicyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class PolicyServiceImpl implements PolicyService {

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private OrderedPlanRepository orderedPlanRepository;

    @Autowired
    private InsurancePlanService insurancePlanService;

    @Override
    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    @Override
    public Policy getPolicyById(String id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy with ID " + id + " not found"));
    }

    @Override
    public Policy createPolicy(Policy policy, List<String> insurancePlanIds) {
        if (insurancePlanIds == null || insurancePlanIds.isEmpty()) {
            throw new RuntimeException("At least one insurance plan must be selected");
        }

        Set<String> uniqueIds = new HashSet<>(insurancePlanIds);
        if (uniqueIds.size() != insurancePlanIds.size()) {
            throw new RuntimeException("Duplicate insurance plans are not allowed in a single policy");
        }

        long count = policyRepository.count();
        policy.setId("POL" + (count + 1));
        policy.setStatus(PolicyStatusEnum.CREATED);
        policy.setCreatedAt(LocalDateTime.now());
        policy.setUpdatedAt(LocalDateTime.now());

        int totalPrice = 0;
        int totalCoverage = 0;
        List<OrderedPlan> orderedPlans = new ArrayList<>();

        for (String planId : insurancePlanIds) {
            InsurancePlan insurancePlan = insurancePlanService.getInsurancePlanEntityById(planId);

            if (!insurancePlan.getApplicableService().contains(policy.getService())) {
                throw new RuntimeException(
                        "Insurance plan " + insurancePlan.getPlanName() +
                        " is not applicable for service: " + policy.getService()
                );
            }

            totalPrice += insurancePlan.getPrice();
            totalCoverage += insurancePlan.getCoverage();

            OrderedPlan orderedPlan = new OrderedPlan();
            long opCount = orderedPlanRepository.count();
            orderedPlan.setId("OP" + (opCount + orderedPlans.size() + 1));

            orderedPlan.setPolicy(policy);
            orderedPlan.setInsurancePlan(insurancePlan);
            orderedPlan.setStatus(OrderedPlanStatusEnum.ORDERED);

            LocalDate expiredDate = policy.getStartDate().plusDays(insurancePlan.getExpiredByDays());
            orderedPlan.setExpiredDate(expiredDate);
            orderedPlan.setCreatedAt(LocalDateTime.now());
            orderedPlan.setUpdatedAt(LocalDateTime.now());

            orderedPlans.add(orderedPlan);
        }

        policy.setTotalPrice(totalPrice);
        policy.setTotalCoverage(totalCoverage);
        policy.setOrderedPlans(orderedPlans);

        return policyRepository.save(policy);
    }

    @Override
    public Policy payPolicy(String id) {
        Policy policy = getPolicyById(id);

        if (policy.getStatus() != PolicyStatusEnum.CREATED) {
            throw new RuntimeException("Only policies with CREATED status can be paid");
        }

        policy.setStatus(PolicyStatusEnum.PAID);
        policy.setUpdatedAt(LocalDateTime.now());

        for (OrderedPlan orderedPlan : policy.getOrderedPlans()) {
            orderedPlan.setStatus(OrderedPlanStatusEnum.PAID);
            orderedPlan.setUpdatedAt(LocalDateTime.now());
            orderedPlanRepository.save(orderedPlan);
        }

        return policyRepository.save(policy);
    }

    @Override
    public void updateExpiredPolicies() {
        LocalDate currentDate = LocalDate.now();
        List<OrderedPlan> expiredOrderedPlans = orderedPlanRepository.findByExpiredDateBefore(currentDate);

        for (OrderedPlan orderedPlan : expiredOrderedPlans) {
            if (orderedPlan.getStatus() == OrderedPlanStatusEnum.EXPIRED ||
                orderedPlan.getStatus() == OrderedPlanStatusEnum.CLAIMED ||
                orderedPlan.getStatus() == OrderedPlanStatusEnum.REJECTED) {
                continue;
            }

            boolean hasAcceptedClaim = false;
            if (orderedPlan.getClaims() != null) {
                for (Claim claim : orderedPlan.getClaims()) {
                    if (claim.getStatus() == ClaimStatusEnum.ACCEPTED) {
                        hasAcceptedClaim = true;
                        break;
                    }
                }
            }

            if (!hasAcceptedClaim) {
                orderedPlan.setStatus(OrderedPlanStatusEnum.EXPIRED);
                orderedPlan.setUpdatedAt(LocalDateTime.now());
                orderedPlanRepository.save(orderedPlan);
            }
        }

        List<Policy> allPolicies = policyRepository.findAll();
        for (Policy policy : allPolicies) {
            if (policy.getStatus() == PolicyStatusEnum.EXPIRED ||
                policy.getStatus() == PolicyStatusEnum.FULLY_CLAIMED) {
                continue;
            }

            boolean allExpired = true;
            for (OrderedPlan orderedPlan : policy.getOrderedPlans()) {
                if (orderedPlan.getStatus() != OrderedPlanStatusEnum.EXPIRED &&
                    orderedPlan.getStatus() != OrderedPlanStatusEnum.REJECTED) {
                    allExpired = false;
                    break;
                }
            }

            if (allExpired && !policy.getOrderedPlans().isEmpty()) {
                policy.setStatus(PolicyStatusEnum.EXPIRED);
                policy.setUpdatedAt(LocalDateTime.now());
                policyRepository.save(policy);
            }
        }
    }

    @Override
    public int countAllPolicies() {
        return (int) policyRepository.count();
    }
}
