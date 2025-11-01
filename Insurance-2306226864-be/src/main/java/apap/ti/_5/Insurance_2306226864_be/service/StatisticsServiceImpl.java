package apap.ti._5.Insurance_2306226864_be.service;

import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
import apap.ti._5.Insurance_2306226864_be.repository.ClaimRepository;
import apap.ti._5.Insurance_2306226864_be.repository.InsurancePlanRepository;
import apap.ti._5.Insurance_2306226864_be.repository.OrderedPlanRepository;
import apap.ti._5.Insurance_2306226864_be.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private InsurancePlanRepository insurancePlanRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private OrderedPlanRepository orderedPlanRepository;

    // ========================== INSURANCE PLAN STATISTICS ==========================
    @Override
    public Map<String, Long> getInsurancePlanStatistics(String service, Integer months) {
        // Calculate time period (from X months ago to now)
        LocalDateTime startDate = LocalDate.now().minusMonths(months).atStartOfDay();
        
        // Get all ordered plans within the time period
        List<OrderedPlan> orderedPlans = orderedPlanRepository.findAll().stream()
                .filter(op -> op.getCreatedAt() != null && op.getCreatedAt().isAfter(startDate))
                .collect(Collectors.toList());
        
        // Filter by service if not "ALL"
        if (service != null && !service.equalsIgnoreCase("ALL")) {
            try {
                ServiceEnum serviceEnum = ServiceEnum.valueOf(service.toUpperCase());
                orderedPlans = orderedPlans.stream()
                        .filter(op -> op.getInsurancePlan() != null 
                                && op.getInsurancePlan().getApplicableService() != null
                                && op.getInsurancePlan().getApplicableService().contains(serviceEnum))
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Invalid service, return empty map
                return new HashMap<>();
            }
        }
        
        // Group by insurance plan name and count
        Map<String, Long> statistics = orderedPlans.stream()
                .filter(op -> op.getInsurancePlan() != null)
                .collect(Collectors.groupingBy(
                    op -> op.getInsurancePlan().getPlanName(),
                    Collectors.counting()
                ));
        
        return statistics;
    }

    // ========================== HOMEPAGE STATISTICS ==========================
    @Override
    public Map<String, Integer> getHomepageStatistics() {
        Map<String, Integer> statistics = new HashMap<>();
        
        // Count total insurance plans (not deleted)
        Long insurancePlansCount = insurancePlanRepository.countByDeletedAtIsNull();
        statistics.put("insurancePlans", insurancePlansCount.intValue());
        
        // Count total policies
        Long policiesCount = policyRepository.count();
        statistics.put("policies", policiesCount.intValue());
        
        // Count total claims
        Long claimsCount = claimRepository.count();
        statistics.put("claims", claimsCount.intValue());
        
        return statistics;
    }
}
