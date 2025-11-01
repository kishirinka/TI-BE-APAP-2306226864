package apap.ti._5.Insurance_2306226864_be.restdto.request.claim;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateClaimRequestDTO {
    
    @NotNull(message = "Ordered Plan ID is required")
    @NotBlank(message = "Ordered Plan ID cannot be blank")
    private String orderedPlanId;
    
    @NotNull(message = "Proof is required")
    @NotBlank(message = "Proof cannot be blank")
    private String proof;
}
