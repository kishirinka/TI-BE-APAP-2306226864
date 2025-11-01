package apap.ti._5.Insurance_2306226864_be.restcontroller;

import apap.ti._5.Insurance_2306226864_be.model.Policy;
import apap.ti._5.Insurance_2306226864_be.restdto.request.policy.CreatePolicyRequestDTO;
import apap.ti._5.Insurance_2306226864_be.restdto.response.BaseResponseDTO;
import apap.ti._5.Insurance_2306226864_be.restdto.response.policy.PolicyResponseDTO;
import apap.ti._5.Insurance_2306226864_be.service.PolicyService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/policies")
public class PolicyRestController {

    @Autowired
    private PolicyService policyService;

    @GetMapping
    public ResponseEntity<BaseResponseDTO<List<PolicyResponseDTO>>> getAllPolicies() {
        policyService.updateExpiredPolicies();

        List<PolicyResponseDTO> policyDTOs = policyService.getAllPolicies().stream()
                .map(PolicyResponseDTO::fromEntity)
                .collect(Collectors.toList());

        String message = policyDTOs.isEmpty()
                ? "No policies found"
                : "Successfully retrieved all policies";

        return ResponseEntity.ok(BaseResponseDTO.success(message, policyDTOs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<PolicyResponseDTO>> getPolicyById(@PathVariable String id) {
        try {
            Policy policy = policyService.getPolicyById(id);
            PolicyResponseDTO policyDTO = PolicyResponseDTO.fromEntity(policy);

            return ResponseEntity.ok(
                    BaseResponseDTO.success("Successfully retrieved policy with ID: " + id, policyDTO)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponseDTO.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<BaseResponseDTO<PolicyResponseDTO>> createPolicy(
            @Valid @RequestBody CreatePolicyRequestDTO request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));

            return ResponseEntity.badRequest().body(
                    BaseResponseDTO.error(HttpStatus.BAD_REQUEST.value(), "Validation failed: " + errorMessage)
            );
        }

        try {
            Policy policy = new Policy();
            policy.setUserId(request.getUserId());
            policy.setBookingId(request.getBookingId());
            policy.setService(request.getService());
            policy.setStartDate(LocalDate.now());

            Policy createdPolicy = policyService.createPolicy(policy, request.getInsurancePlanIds());
            PolicyResponseDTO policyDTO = PolicyResponseDTO.fromEntity(createdPolicy);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponseDTO.created("Policy created successfully", policyDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    BaseResponseDTO.error(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        }
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<BaseResponseDTO<PolicyResponseDTO>> payPolicy(@PathVariable String id) {
        try {
            Policy paidPolicy = policyService.payPolicy(id);
            PolicyResponseDTO policyDTO = PolicyResponseDTO.fromEntity(paidPolicy);

            return ResponseEntity.ok(BaseResponseDTO.success("Policy paid successfully", policyDTO));
        } catch (RuntimeException e) {
            HttpStatus status = e.getMessage().contains("not found")
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;

            return ResponseEntity.status(status)
                    .body(BaseResponseDTO.error(status.value(), e.getMessage()));
        }
    }
}
