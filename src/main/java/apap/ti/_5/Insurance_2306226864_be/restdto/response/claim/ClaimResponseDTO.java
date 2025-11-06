package apap.ti._5.Insurance_2306226864_be.restdto.response.claim;

import apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum;
import apap.ti._5.Insurance_2306226864_be.model.Claim;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimResponseDTO {
    
    private String id;
    private ClaimStatusEnum status;
    private String proof;
    
    // Rejection fields
    private String rejectionReason;
    private String rejectionDescription;
    private LocalDateTime rejectionTimestamp;
    
    // Acceptance fields
    private String acceptedNote;
    private LocalDateTime acceptedTimestamp;
    
    // OrderedPlan summary
    private OrderedPlanSummaryDTO orderedPlan;
    
    // Insurance plan name (from ordered plan)
    private String insurancePlanName;
    
    // Days since claim created (only if WAITING_FOR_REVIEW)
    private Long daysSinceClaimed;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Mapper from Claim entity to DTO
     */
    public static ClaimResponseDTO fromEntity(Claim claim) {
        if (claim == null) {
            return null;
        }
        
        ClaimResponseDTO dto = new ClaimResponseDTO();
        dto.setId(claim.getId());
        dto.setStatus(claim.getStatus());
        dto.setProof(claim.getProof());
        
        // Rejection fields (null-safe)
        dto.setRejectionReason(claim.getRejectionReason());
        dto.setRejectionDescription(claim.getRejectionDescription());
        dto.setRejectionTimestamp(claim.getRejectionTimestamp());
        
        // Acceptance fields (null-safe)
        dto.setAcceptedNote(claim.getAcceptedNote());
        dto.setAcceptedTimestamp(claim.getAcceptedTimestamp());
        
        // Timestamps
        dto.setCreatedAt(claim.getCreatedAt());
        dto.setUpdatedAt(claim.getUpdatedAt());
        
        // OrderedPlan summary (null-safe)
        if (claim.getOrderedPlan() != null) {
            dto.setOrderedPlan(OrderedPlanSummaryDTO.fromEntity(claim.getOrderedPlan()));
            
            // Insurance plan name (null-safe)
            if (claim.getOrderedPlan().getInsurancePlan() != null) {
                dto.setInsurancePlanName(claim.getOrderedPlan().getInsurancePlan().getPlanName());
            }
        }
        
        // Calculate days since claimed (only if WAITING_FOR_REVIEW)
        if (claim.getStatus() == ClaimStatusEnum.WAITING_FOR_REVIEW && claim.getCreatedAt() != null) {
            dto.setDaysSinceClaimed(ChronoUnit.DAYS.between(claim.getCreatedAt(), LocalDateTime.now()));
        }
        
        return dto;
    }
    
    /**
     * OrderedPlan Summary DTO - only essential fields
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderedPlanSummaryDTO {
        private String id;
        private apap.ti._5.Insurance_2306226864_be.enums.OrderedPlanStatusEnum status;
        private String insurancePlanName;
        
        public static OrderedPlanSummaryDTO fromEntity(apap.ti._5.Insurance_2306226864_be.model.OrderedPlan orderedPlan) {
            if (orderedPlan == null) {
                return null;
            }
            
            OrderedPlanSummaryDTO dto = new OrderedPlanSummaryDTO();
            dto.setId(orderedPlan.getId());
            dto.setStatus(orderedPlan.getStatus());
            
            // Insurance plan name (null-safe)
            if (orderedPlan.getInsurancePlan() != null) {
                dto.setInsurancePlanName(orderedPlan.getInsurancePlan().getPlanName());
            }
            
            return dto;
        }
    }
}
