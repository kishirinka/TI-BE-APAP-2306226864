package apap.ti._5.Insurance_2306226864_be.service;package apap.ti._5.Insurance_2306226864_be.service;



import apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum;import apap.ti._5.Insurance_2306226864_be.dto.claim.ClaimRequestDto;

import apap.ti._5.Insurance_2306226864_be.enums.OrderedPlanStatusEnum;import apap.ti._5.Insurance_2306226864_be.dto.claim.ClaimResponseDto;

import apap.ti._5.Insurance_2306226864_be.model.Claim;import apap.ti._5.Insurance_2306226864_be.repository.ClaimRepository;

import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;import org.springframework.beans.factory.annotation.Autowired;

import apap.ti._5.Insurance_2306226864_be.repository.ClaimRepository;import org.springframework.stereotype.Service;

import apap.ti._5.Insurance_2306226864_be.repository.OrderedPlanRepository;

import org.springframework.beans.factory.annotation.Autowired;import java.util.List;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;@Service

public class ClaimServiceImpl implements ClaimService {

import java.time.LocalDate;    

import java.time.LocalDateTime;    @Autowired

import java.util.List;    private ClaimRepository claimRepository;

import java.util.stream.Collectors;

    @Override

@Service    public Long getProcessedClaimsCount() {

@Transactional        // Count claims that have been processed (status is not "WAITING")

public class ClaimServiceImpl implements ClaimService {        return claimRepository.countByStatusNot("WAITING_FOR_REVIEW");

    }

    @Autowired

    private ClaimRepository claimRepository;    @Override

    public ClaimResponseDto createClaim(ClaimRequestDto requestDto) {

    @Autowired        // TODO: Implement create claim logic

    private OrderedPlanRepository orderedPlanRepository;        throw new UnsupportedOperationException("createClaim method not implemented yet");

    }

    @Autowired

    private OrderedPlanService orderedPlanService;    @Override

    public List<ClaimResponseDto> getAllClaims(String roomID, String rackID) {

    // ========================== GET ALL ==========================        // TODO: Implement get all claims logic

    @Override        throw new UnsupportedOperationException("getAllClaims method not implemented yet");

    public List<Claim> getAllClaims() {    }

        return claimRepository.findAll();}

    }

    // ========================== GET BY STATUS ==========================
    @Override
    public List<Claim> getClaimsByStatus(String status) {
        try {
            ClaimStatusEnum statusEnum = ClaimStatusEnum.valueOf(status.toUpperCase());
            return claimRepository.findByStatus(statusEnum);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid claim status: " + status);
        }
    }

    // ========================== GET BY INSURANCE PLAN ID ==========================
    @Override
    public List<Claim> getClaimsByInsurancePlanId(String insurancePlanId) {
        // Get all claims and filter by insurance plan through OrderedPlan relationship
        List<Claim> allClaims = claimRepository.findAll();
        return allClaims.stream()
                .filter(claim -> claim.getOrderedPlan() != null 
                        && claim.getOrderedPlan().getInsurancePlan() != null
                        && insurancePlanId.equals(claim.getOrderedPlan().getInsurancePlan().getId()))
                .collect(Collectors.toList());
    }

    // ========================== GET BY FILTERS ==========================
    @Override
    public List<Claim> getClaimsByFilters(String status, String insurancePlanId) {
        // Case 1: No filters - return all
        if ((status == null || status.isEmpty()) && (insurancePlanId == null || insurancePlanId.isEmpty())) {
            return getAllClaims();
        }
        
        // Case 2: Filter by status only
        if ((insurancePlanId == null || insurancePlanId.isEmpty())) {
            return getClaimsByStatus(status);
        }
        
        // Case 3: Filter by insurance plan only
        if ((status == null || status.isEmpty())) {
            return getClaimsByInsurancePlanId(insurancePlanId);
        }
        
        // Case 4: Filter by both status and insurance plan
        try {
            ClaimStatusEnum statusEnum = ClaimStatusEnum.valueOf(status.toUpperCase());
            List<Claim> claimsByPlan = getClaimsByInsurancePlanId(insurancePlanId);
            return claimsByPlan.stream()
                    .filter(claim -> claim.getStatus() == statusEnum)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid claim status: " + status);
        }
    }

    // ========================== GET BY ID ==========================
    @Override
    public Claim getClaimById(String id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim with ID " + id + " not found"));
    }

    // ========================== CREATE CLAIM ==========================
    @Override
    public Claim createClaim(Claim claim, String orderedPlanId) {
        // Get ordered plan
        OrderedPlan orderedPlan = orderedPlanService.getOrderedPlanById(orderedPlanId);
        
        // Validation 1: OrderedPlan must be PAID
        if (orderedPlan.getStatus() != OrderedPlanStatusEnum.PAID) {
            throw new RuntimeException("Cannot create claim: OrderedPlan must be PAID. Current status: " + orderedPlan.getStatus());
        }
        
        // Validation 2: Check if expired
        LocalDate today = LocalDate.now();
        if (orderedPlan.getExpiredDate() != null && orderedPlan.getExpiredDate().isBefore(today)) {
            throw new RuntimeException("Cannot create claim: OrderedPlan has expired on " + orderedPlan.getExpiredDate());
        }
        
        // Generate ID: CLM + (count + 1)
        long count = claimRepository.count();
        claim.setId("CLM" + (count + 1));
        
        // Set initial status
        claim.setStatus(ClaimStatusEnum.WAITING_FOR_REVIEW);
        
        // Link to ordered plan
        claim.setOrderedPlan(orderedPlan);
        
        // Set timestamps
        claim.setCreatedAt(LocalDateTime.now());
        claim.setUpdatedAt(LocalDateTime.now());
        
        // Save claim
        Claim savedClaim = claimRepository.save(claim);
        
        // Update OrderedPlan status to WAITING_FOR_REVIEW
        orderedPlan.setStatus(OrderedPlanStatusEnum.WAITING_FOR_REVIEW);
        orderedPlan.setUpdatedAt(LocalDateTime.now());
        orderedPlanRepository.save(orderedPlan);
        
        return savedClaim;
    }

    // ========================== ACCEPT CLAIM ==========================
    @Override
    public Claim acceptClaim(String id, String note) {
        // Get claim
        Claim claim = getClaimById(id);
        
        // Validate claim is in WAITING_FOR_REVIEW status
        if (claim.getStatus() != ClaimStatusEnum.WAITING_FOR_REVIEW) {
            throw new RuntimeException("Cannot accept claim: Claim must be in WAITING_FOR_REVIEW status. Current status: " + claim.getStatus());
        }
        
        // Update claim
        claim.setStatus(ClaimStatusEnum.ACCEPTED);
        claim.setAcceptedNote(note);
        claim.setAcceptedTimestamp(LocalDateTime.now());
        claim.setUpdatedAt(LocalDateTime.now());
        
        Claim savedClaim = claimRepository.save(claim);
        
        // Update OrderedPlan status to CLAIMED
        OrderedPlan orderedPlan = claim.getOrderedPlan();
        orderedPlanService.updateOrderedPlanStatus(orderedPlan.getId(), OrderedPlanStatusEnum.CLAIMED.name());
        
        return savedClaim;
    }

    // ========================== REJECT CLAIM ==========================
    @Override
    public Claim rejectClaim(String id, String reason, String description) {
        // Get claim
        Claim claim = getClaimById(id);
        
        // Validate claim is in WAITING_FOR_REVIEW status
        if (claim.getStatus() != ClaimStatusEnum.WAITING_FOR_REVIEW) {
            throw new RuntimeException("Cannot reject claim: Claim must be in WAITING_FOR_REVIEW status. Current status: " + claim.getStatus());
        }
        
        // Update claim
        claim.setStatus(ClaimStatusEnum.REJECTED);
        claim.setRejectionReason(reason);
        claim.setRejectionDescription(description);
        claim.setRejectionTimestamp(LocalDateTime.now());
        claim.setUpdatedAt(LocalDateTime.now());
        
        Claim savedClaim = claimRepository.save(claim);
        
        // Check total rejected claims for this ordered plan
        OrderedPlan orderedPlan = claim.getOrderedPlan();
        List<Claim> rejectedClaims = claimRepository.findByOrderedPlanIdAndStatus(
                orderedPlan.getId(), 
                ClaimStatusEnum.REJECTED
        );
        
        // If 3 or more rejected claims, set OrderedPlan status to REJECTED
        if (rejectedClaims.size() >= 3) {
            orderedPlanService.updateOrderedPlanStatus(orderedPlan.getId(), OrderedPlanStatusEnum.REJECTED.name());
        } else {
            // Otherwise, set back to PAID (ready for new claim)
            orderedPlan.setStatus(OrderedPlanStatusEnum.PAID);
            orderedPlan.setUpdatedAt(LocalDateTime.now());
            orderedPlanRepository.save(orderedPlan);
        }
        
        return savedClaim;
    }

    // ========================== COUNT ALL ==========================
    @Override
    public int countAllClaims() {
        return (int) claimRepository.count();
    }
}
