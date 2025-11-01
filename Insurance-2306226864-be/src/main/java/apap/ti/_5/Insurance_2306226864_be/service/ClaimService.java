package apap.ti._5.Insurance_2306226864_be.service;package apap.ti._5.Insurance_2306226864_be.service;



import apap.ti._5.Insurance_2306226864_be.model.Claim;import apap.ti._5.Insurance_2306226864_be.dto.claim.ClaimRequestDto;

import java.util.List;import apap.ti._5.Insurance_2306226864_be.dto.claim.ClaimResponseDto;



public interface ClaimService {import java.util.HashMap;

    import java.util.List;

    /**import java.util.Map;

     * Get all claims in the systempublic interface ClaimService {

     */    Long getProcessedClaimsCount();

    List<Claim> getAllClaims();    ClaimResponseDto createClaim(ClaimRequestDto requestDto);

        List<ClaimResponseDto> getAllClaims(String roomID, String rackID);

    /**}

     * Get claims by status
     * @param status - Claim status (WAITING_FOR_REVIEW, ACCEPTED, REJECTED)
     */
    List<Claim> getClaimsByStatus(String status);
    
    /**
     * Get claims by insurance plan ID (through OrderedPlan relationship)
     * @param insurancePlanId - Insurance Plan ID
     */
    List<Claim> getClaimsByInsurancePlanId(String insurancePlanId);
    
    /**
     * Get claims by filters (status and/or insurance plan ID)
     * Supports:
     * - Filter by status only
     * - Filter by insurance plan only
     * - Filter by both
     * - No filter (return all)
     * 
     * @param status - Claim status (optional)
     * @param insurancePlanId - Insurance Plan ID (optional)
     */
    List<Claim> getClaimsByFilters(String status, String insurancePlanId);
    
    /**
     * Get claim by ID
     * @param id - Claim ID
     * @throws RuntimeException if claim not found
     */
    Claim getClaimById(String id);
    
    /**
     * Create a new claim
     * - Generate ID: CLM + (count + 1)
     * - Validate: OrderedPlan must be PAID
     * - Validate: expiredDate not passed
     * - Initial status: WAITING_FOR_REVIEW
     * - Update OrderedPlan status to WAITING_FOR_REVIEW
     * 
     * @param claim - Claim object with proof
     * @param orderedPlanId - OrderedPlan ID
     * @throws RuntimeException if validations fail
     */
    Claim createClaim(Claim claim, String orderedPlanId);
    
    /**
     * Accept a claim
     * - Set status = ACCEPTED
     * - Set acceptedTimestamp = now
     * - Set acceptedNote
     * - Update OrderedPlan status = CLAIMED
     * - Update Policy status if needed (PARTIALLY_CLAIMED or FULLY_CLAIMED)
     * 
     * @param id - Claim ID
     * @param note - Acceptance note
     * @throws RuntimeException if claim not found
     */
    Claim acceptClaim(String id, String note);
    
    /**
     * Reject a claim
     * - Set status = REJECTED
     * - Set rejectionReason, rejectionDescription, rejectionTimestamp
     * - Check total rejected claims for this ordered plan
     * - If 3+ rejected claims, set OrderedPlan status = REJECTED
     * 
     * @param id - Claim ID
     * @param reason - Rejection reason
     * @param description - Rejection description
     * @throws RuntimeException if claim not found
     */
    Claim rejectClaim(String id, String reason, String description);
    
    /**
     * Count all claims in the system
     */
    int countAllClaims();
}
