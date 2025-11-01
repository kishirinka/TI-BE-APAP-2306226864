package apap.ti._5.Insurance_2306226864_be.restdto.response.insuranceplan;

import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import apap.ti._5.Insurance_2306226864_be.model.InsurancePlan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsurancePlanResponseDTO {
    private String id;
    private String planName;
    private String providerId;
    private Integer price;
    private Integer coverage;
    private String coverageDetails;
    private List<ServiceEnum> applicableService;
    private Integer expiredByDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InsurancePlanResponseDTO fromEntity(InsurancePlan entity) {
        if (entity == null) {
            return null;
        }
        return new InsurancePlanResponseDTO(
            entity.getId(),
            entity.getPlanName(),
            entity.getProviderId(),
            entity.getPrice(),
            entity.getCoverage(),
            entity.getCoverageDetails(),
            entity.getApplicableService(),
            entity.getExpiredByDays(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
