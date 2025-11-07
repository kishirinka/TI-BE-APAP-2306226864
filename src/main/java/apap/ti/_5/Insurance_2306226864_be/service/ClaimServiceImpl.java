package apap.ti._5.Insurance_2306226864_be.service;

import apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.OrderedPlanStatusEnum;
import apap.ti._5.Insurance_2306226864_be.model.Claim;
import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
import apap.ti._5.Insurance_2306226864_be.repository.ClaimRepository;
import apap.ti._5.Insurance_2306226864_be.repository.OrderedPlanRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClaimServiceImpl implements ClaimService {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private OrderedPlanRepository orderedPlanRepository;

    @Autowired
    private OrderedPlanService orderedPlanService;

    // ========================== GET ALL ==========================
    @Override
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
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

    @Override
    public List<Claim> getClaimsByInsurancePlanId(String insurancePlanId) {
        return claimRepository.findAll().stream()
                .filter(claim -> claim.getOrderedPlan() != null
                        && claim.getOrderedPlan().getInsurancePlan() != null
                        && insurancePlanId.equals(claim.getOrderedPlan().getInsurancePlan().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Claim> getClaimsByFilters(String status, String insurancePlanId) {
        if ((status == null || status.isEmpty()) && (insurancePlanId == null || insurancePlanId.isEmpty())) {
            return getAllClaims();
        }

        if (insurancePlanId == null || insurancePlanId.isEmpty()) {
            return getClaimsByStatus(status);
        }

        if (status == null || status.isEmpty()) {
            return getClaimsByInsurancePlanId(insurancePlanId);
        }

        try {
            ClaimStatusEnum statusEnum = ClaimStatusEnum.valueOf(status.toUpperCase());
            return getClaimsByInsurancePlanId(insurancePlanId).stream()
                    .filter(claim -> claim.getStatus() == statusEnum)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid claim status: " + status);
        }
    }

    @Override
    public Claim getClaimById(String id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim with ID " + id + " not found"));
    }

    @Override
    public Claim createClaim(Claim claim, String orderedPlanId) {
        OrderedPlan orderedPlan = orderedPlanService.getOrderedPlanById(orderedPlanId);

        if (orderedPlan.getStatus() != OrderedPlanStatusEnum.PAID) {
            throw new RuntimeException("Cannot create claim: OrderedPlan must be PAID. Current status: " + orderedPlan.getStatus());
        }

        LocalDate today = LocalDate.now();
        if (orderedPlan.getExpiredDate() != null && orderedPlan.getExpiredDate().isBefore(today)) {
            throw new RuntimeException("Cannot create claim: OrderedPlan has expired on " + orderedPlan.getExpiredDate());
        }

        long count = claimRepository.count();
        // Set ID dengan format: CLM{number}-{orderedPlanId}
        claim.setId(orderedPlanId + "-CLAIM" + (count + 1));
        claim.setStatus(ClaimStatusEnum.WAITING_FOR_REVIEW);
        claim.setOrderedPlan(orderedPlan);
        claim.setCreatedAt(LocalDateTime.now());
        claim.setUpdatedAt(LocalDateTime.now());

        Claim savedClaim = claimRepository.save(claim);

        orderedPlan.setStatus(OrderedPlanStatusEnum.WAITING_FOR_REVIEW);
        orderedPlan.setUpdatedAt(LocalDateTime.now());
        orderedPlanRepository.save(orderedPlan);

        return savedClaim;
    }

    @Override
    public Claim acceptClaim(String id, String note) {
        Claim claim = getClaimById(id);

        if (claim.getStatus() != ClaimStatusEnum.WAITING_FOR_REVIEW) {
            throw new RuntimeException("Cannot accept claim: Claim must be in WAITING_FOR_REVIEW status. Current status: " + claim.getStatus());
        }

        claim.setStatus(ClaimStatusEnum.ACCEPTED);
        claim.setAcceptedNote(note);
        claim.setAcceptedTimestamp(LocalDateTime.now());
        claim.setUpdatedAt(LocalDateTime.now());

        Claim savedClaim = claimRepository.save(claim);

        OrderedPlan orderedPlan = claim.getOrderedPlan();
        orderedPlanService.updateOrderedPlanStatus(orderedPlan.getId(), OrderedPlanStatusEnum.CLAIMED.name());

        return savedClaim;
    }

    @Override
    public Claim rejectClaim(String id, String reason, String description) {
        Claim claim = getClaimById(id);
        System.out.println("===============" + claim.getId());

        if (claim.getStatus() != ClaimStatusEnum.WAITING_FOR_REVIEW) {
            throw new RuntimeException("Cannot reject claim: Claim must be in WAITING_FOR_REVIEW status. Current status: " + claim.getStatus());
        }

        claim.setStatus(ClaimStatusEnum.REJECTED);
        claim.setRejectionReason(reason);
        claim.setRejectionDescription(description);
        claim.setRejectionTimestamp(LocalDateTime.now());
        claim.setUpdatedAt(LocalDateTime.now());

        Claim savedClaim = claimRepository.save(claim);

        OrderedPlan orderedPlan = claim.getOrderedPlan();
        List<Claim> rejectedClaims = claimRepository.findByOrderedPlanIdAndStatus(
                orderedPlan.getId(),
                ClaimStatusEnum.REJECTED
        );

        if (rejectedClaims.size() >= 3) {
            orderedPlanService.updateOrderedPlanStatus(orderedPlan.getId(), OrderedPlanStatusEnum.REJECTED.name());
        } else {
            orderedPlan.setStatus(OrderedPlanStatusEnum.PAID);
            orderedPlan.setUpdatedAt(LocalDateTime.now());
            orderedPlanRepository.save(orderedPlan);
        }

        return savedClaim;
    }

    @Override
    public int countAllClaims() {
        return (int) claimRepository.count();
    }
}
