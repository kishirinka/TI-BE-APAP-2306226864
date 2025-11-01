package apap.ti._5.Insurance_2306226864_be.restdto.request.claim;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessClaimRequestDTO {
    
    @NotBlank(message = "Action is required (ACCEPT or REJECT)")
    private String action; // "ACCEPT" or "REJECT"
    
    // For ACCEPT action
    private String note;
    
    // For REJECT action
    private String rejectionReason;
    private String rejectionDescription;
}
