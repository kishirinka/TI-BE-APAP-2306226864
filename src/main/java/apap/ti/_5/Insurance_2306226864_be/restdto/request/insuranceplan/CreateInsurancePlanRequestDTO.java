package apap.ti._5.Insurance_2306226864_be.restdto.request.insuranceplan;

import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInsurancePlanRequestDTO {
    @NotBlank(message = "Plan name is required")
    @NotNull(message = "Plan name cannot be null")
    private String planName;

    @NotBlank(message = "Provider ID is required")
    @NotNull(message = "Provider ID cannot be null")
    private String providerId;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be at least 0")
    private Integer price;

    @NotNull(message = "Coverage is required")
    @Min(value = 0, message = "Coverage must be at least 0")
    private Integer coverage;

    @NotBlank(message = "Coverage details is required")
    @NotNull(message = "Coverage details cannot be null")
    private String coverageDetails;

    @NotEmpty(message = "Applicable service cannot be empty")
    private List<ServiceEnum> applicableService;

    @NotNull(message = "Expired by days is required")
    @Min(value = 1, message = "Expired by days must be at least 1")
    private Integer expiredByDays;
}