package apap.ti._5.Insurance_2306226864_be.restcontroller;

import apap.ti._5.Insurance_2306226864_be.model.Claim;
import apap.ti._5.Insurance_2306226864_be.restdto.request.claim.CreateClaimRequestDTO;
import apap.ti._5.Insurance_2306226864_be.restdto.request.claim.ProcessClaimRequestDTO;
import apap.ti._5.Insurance_2306226864_be.restdto.response.BaseResponseDTO;
import apap.ti._5.Insurance_2306226864_be.restdto.response.claim.ClaimResponseDTO;
import apap.ti._5.Insurance_2306226864_be.service.ClaimService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/claims")
public class ClaimRestController {

    @Autowired
    private ClaimService claimService;

    /**
     * GET /api/claims
     * Get all claims with optional filters (status, insurancePlanId)
     * Supports:
     * - No filter (return all)
     * - Filter by status only
     * - Filter by insurance plan only
     * - Filter by both
     */
    @GetMapping
    public ResponseEntity<BaseResponseDTO<List<ClaimResponseDTO>>> getAllClaims(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String insurancePlanId) {
        
        List<Claim> claims;
        
        // Handle all filter combinations
        if ((status == null || status.isEmpty()) && (insurancePlanId == null || insurancePlanId.isEmpty())) {
            // No filter - return all
            claims = claimService.getAllClaims();
        } else if (insurancePlanId == null || insurancePlanId.isEmpty()) {
            // Filter by status only
            try {
                claims = claimService.getClaimsByStatus(status);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(BaseResponseDTO.error(400, "Invalid status: " + status + ". Valid values: WAITING_FOR_REVIEW, ACCEPTED, REJECTED"));
            }
        } else if (status == null || status.isEmpty()) {
            // Filter by insurance plan only
            claims = claimService.getClaimsByInsurancePlanId(insurancePlanId);
        } else {
            // Filter by both
            try {
                claims = claimService.getClaimsByFilters(status, insurancePlanId);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(BaseResponseDTO.error(400, "Invalid status: " + status + ". Valid values: WAITING_FOR_REVIEW, ACCEPTED, REJECTED"));
            }
        }
        
        // Convert to DTO
        List<ClaimResponseDTO> claimDTOs = claims.stream()
                .map(ClaimResponseDTO::fromEntity)
                .collect(Collectors.toList());
        
        String message = claimDTOs.isEmpty()
                ? "No claims found"
                : "Successfully retrieved " + claimDTOs.size() + " claim(s)";
        
        return ResponseEntity.ok(BaseResponseDTO.success(message, claimDTOs));
    }

    /**
     * GET /api/claims/{id}
     * Get claim detail by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<ClaimResponseDTO>> getClaimById(@PathVariable String id) {
        try {
            Claim claim = claimService.getClaimById(id);
            ClaimResponseDTO claimDTO = ClaimResponseDTO.fromEntity(claim);
            
            return ResponseEntity.ok(
                    BaseResponseDTO.success("Successfully retrieved claim with ID: " + id, claimDTO)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponseDTO.error(404, "Claim not found with ID: " + id));
        }
    }

    /**
     * POST /api/claims
     * Create a new claim
     */
    @PostMapping
    public ResponseEntity<BaseResponseDTO<ClaimResponseDTO>> createClaim(
            @Valid @RequestBody CreateClaimRequestDTO requestDTO,
            BindingResult bindingResult) {
        
        // Validation error handling
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponseDTO.error(400, "Validation failed: " + errorMessage));
        }
        
        try {
            // Create claim entity
            Claim claim = new Claim();
            claim.setProof(requestDTO.getProof());
            
            // Create claim with ordered plan ID
            Claim createdClaim = claimService.createClaim(claim, requestDTO.getOrderedPlanId());
            ClaimResponseDTO claimDTO = ClaimResponseDTO.fromEntity(createdClaim);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponseDTO.created("Claim created successfully with ID: " + createdClaim.getId(), claimDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponseDTO.error(400, "Failed to create claim: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/claims/{id}/process
     * Process claim (accept or reject)
     */
    @PutMapping("/{id}/process")
    public ResponseEntity<BaseResponseDTO<ClaimResponseDTO>> processClaim(
            @PathVariable String id,
            @Valid @RequestBody ProcessClaimRequestDTO requestDTO,
            BindingResult bindingResult) {
        
        // Validation error handling
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponseDTO.error(400, "Validation failed: " + errorMessage));
        }
        
        // Validate action
        String action = requestDTO.getAction().toUpperCase();
        if (!action.equals("ACCEPT") && !action.equals("REJECT")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponseDTO.error(400, "Invalid action: " + requestDTO.getAction() + ". Must be ACCEPT or REJECT"));
        }
        
        try {
            Claim processedClaim;
            
            if (action.equals("ACCEPT")) {
                // Validate note is provided for ACCEPT
                if (requestDTO.getNote() == null || requestDTO.getNote().trim().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(BaseResponseDTO.error(400, "Note is required when accepting a claim"));
                }
                
                // Accept claim
                processedClaim = claimService.acceptClaim(id, requestDTO.getNote());
                
            } else { // REJECT
                // Validate rejection fields are provided
                if (requestDTO.getRejectionReason() == null || requestDTO.getRejectionReason().trim().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(BaseResponseDTO.error(400, "Rejection reason is required when rejecting a claim"));
                }
                
                if (requestDTO.getRejectionDescription() == null || requestDTO.getRejectionDescription().trim().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(BaseResponseDTO.error(400, "Rejection description is required when rejecting a claim"));
                }
                
                // Reject claim
                processedClaim = claimService.rejectClaim(
                        id, 
                        requestDTO.getRejectionReason(), 
                        requestDTO.getRejectionDescription()
                );
            }
            
            ClaimResponseDTO claimDTO = ClaimResponseDTO.fromEntity(processedClaim);
            
            return ResponseEntity.ok(
                    BaseResponseDTO.success(
                            "Claim " + action.toLowerCase() + "ed successfully", 
                            claimDTO
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponseDTO.error(400, "Failed to process claim: " + e.getMessage()));
        }
    }
}
