package apap.ti._5.Insurance_2306226864_be.restdto.request.policy;

import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePolicyRequestDTO {
    
    @NotBlank(message = "User ID is required")
    @NotNull(message = "User ID cannot be null")
    private String userId;
    
    @NotBlank(message = "Booking ID is required")
    @NotNull(message = "Booking ID cannot be null")
    private String bookingId;
    
    @NotNull(message = "Service type is required")
    private ServiceEnum service;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotEmpty(message = "At least one insurance plan must be selected")
    private List<String> insurancePlanIds;
}
