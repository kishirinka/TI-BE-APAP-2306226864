package apap.ti._5.Insurance_2306226864_be.restdto.response.policy;

import apap.ti._5.Insurance_2306226864_be.enums.PolicyStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import apap.ti._5.Insurance_2306226864_be.model.Policy;
import apap.ti._5.Insurance_2306226864_be.restdto.response.orderedplan.OrderedPlanResponseDTO;
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
public class PolicyResponseDTO {
    
    private String id;
    private String bookingId;
    private String userId;
    private LocalDate startDate;
    private PolicyStatusEnum status;
    private ServiceEnum service;
    private Integer totalCoverage;
    private Integer totalPrice;
    private List<OrderedPlanResponseDTO> orderedPlans;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Mapper from Policy entity to DTO
     */
    public static PolicyResponseDTO fromEntity(Policy policy) {
        if (policy == null) {
            return null;
        }
        
        PolicyResponseDTO dto = new PolicyResponseDTO();
        dto.setId(policy.getId());
        dto.setBookingId(policy.getBookingId());
        dto.setUserId(policy.getUserId());
        dto.setStartDate(policy.getStartDate());
        dto.setStatus(policy.getStatus());
        dto.setService(policy.getService());
        dto.setTotalCoverage(policy.getTotalCoverage());
        dto.setTotalPrice(policy.getTotalPrice());
        dto.setCreatedAt(policy.getCreatedAt());
        dto.setUpdatedAt(policy.getUpdatedAt());
        
        // Map ordered plans (null-safe)
        if (policy.getOrderedPlans() != null && !policy.getOrderedPlans().isEmpty()) {
            dto.setOrderedPlans(policy.getOrderedPlans().stream()
                    .map(OrderedPlanResponseDTO::fromEntity)
                    .collect(Collectors.toList()));
        } else {
            dto.setOrderedPlans(Collections.emptyList());
        }
        
        return dto;
    }
}
