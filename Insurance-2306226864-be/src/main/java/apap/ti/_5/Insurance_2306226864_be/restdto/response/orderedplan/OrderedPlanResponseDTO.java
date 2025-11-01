package apap.ti._5.Insurance_2306226864_be.restdto.response.orderedplan;

import apap.ti._5.Insurance_2306226864_be.enums.OrderedPlanStatusEnum;
import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
import apap.ti._5.Insurance_2306226864_be.restdto.response.claim.ClaimResponseDTO;
import apap.ti._5.Insurance_2306226864_be.restdto.response.insuranceplan.InsurancePlanResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderedPlanResponseDTO {
    
    private String id;
    private OrderedPlanStatusEnum status;
    private LocalDate expiredDate;
    private InsurancePlanSummaryDTO insurancePlan;
    private List<ClaimSummaryDTO> claims;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Mapper from OrderedPlan entity to DTO
     */
    public static OrderedPlanResponseDTO fromEntity(OrderedPlan orderedPlan) {
        if (orderedPlan == null) {
            return null;
        }
        
        OrderedPlanResponseDTO dto = new OrderedPlanResponseDTO();
        dto.setId(orderedPlan.getId());
        dto.setStatus(orderedPlan.getStatus());
        dto.setExpiredDate(orderedPlan.getExpiredDate());
        dto.setCreatedAt(orderedPlan.getCreatedAt());
        dto.setUpdatedAt(orderedPlan.getUpdatedAt());
        
        // Map insurance plan summary (null-safe)
        if (orderedPlan.getInsurancePlan() != null) {
            dto.setInsurancePlan(InsurancePlanSummaryDTO.fromEntity(orderedPlan.getInsurancePlan()));
        }
        
        // Map claims summary (null-safe)
        if (orderedPlan.getClaims() != null && !orderedPlan.getClaims().isEmpty()) {
            dto.setClaims(orderedPlan.getClaims().stream()
                    .map(ClaimSummaryDTO::fromEntity)
                    .collect(Collectors.toList()));
        } else {
            dto.setClaims(Collections.emptyList());
        }
        
        return dto;
    }
    
    /**
     * Insurance Plan Summary DTO - only essential fields for display
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InsurancePlanSummaryDTO {
        private String id;
        private String planName;
        private String providerId;
        private Integer price;
        private Integer coverage;
        
        public static InsurancePlanSummaryDTO fromEntity(apap.ti._5.Insurance_2306226864_be.model.InsurancePlan plan) {
            if (plan == null) {
                return null;
            }
            
            InsurancePlanSummaryDTO dto = new InsurancePlanSummaryDTO();
            dto.setId(plan.getId());
            dto.setPlanName(plan.getPlanName());
            dto.setProviderId(plan.getProviderId());
            dto.setPrice(plan.getPrice());
            dto.setCoverage(plan.getCoverage());
            return dto;
        }
    }
    
    /**
     * Claim Summary DTO - only essential fields for display
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClaimSummaryDTO {
        private String id;
        private apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum status;
        private LocalDateTime createdAt;
        
        public static ClaimSummaryDTO fromEntity(apap.ti._5.Insurance_2306226864_be.model.Claim claim) {
            if (claim == null) {
                return null;
            }
            
            ClaimSummaryDTO dto = new ClaimSummaryDTO();
            dto.setId(claim.getId());
            dto.setStatus(claim.getStatus());
            dto.setCreatedAt(claim.getCreatedAt());
            return dto;
        }
    }
}
