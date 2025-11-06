package apap.ti._5.Insurance_2306226864_be.repository;

import apap.ti._5.Insurance_2306226864_be.model.Claim;
import apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, String> {

    // Find by status
    List<Claim> findByStatus(ClaimStatusEnum status);
    
    // Find by OrderedPlan
    List<Claim> findByOrderedPlanId(String orderedPlanId);
    
    // Find by OrderedPlan and Status
    List<Claim> findByOrderedPlanIdAndStatus(String orderedPlanId, ClaimStatusEnum status);
    
    // Find by Policy through OrderedPlan relationship
    @Query("SELECT c FROM Claim c WHERE c.orderedPlan.policy.id = :policyId")
    List<Claim> findByPolicyId(@Param("policyId") String policyId);
    
    // Find by Policy and Status
    @Query("SELECT c FROM Claim c WHERE c.orderedPlan.policy.id = :policyId AND c.status = :status")
    List<Claim> findByPolicyIdAndStatus(@Param("policyId") String policyId, @Param("status") ClaimStatusEnum status);
    
    // Find pending claims (waiting for review)
    @Query("SELECT c FROM Claim c WHERE c.status = apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum.WAITING_FOR_REVIEW")
    List<Claim> findPendingClaims();
    
    // Find accepted claims by policy
    @Query("SELECT c FROM Claim c WHERE c.orderedPlan.policy.id = :policyId AND c.status = apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum.ACCEPTED")
    List<Claim> findAcceptedClaimsByPolicy(@Param("policyId") String policyId);
    
    // Find claims by user (through policy)
    @Query("SELECT c FROM Claim c WHERE c.orderedPlan.policy.userId = :userId")
    List<Claim> findClaimsByUserId(@Param("userId") String userId);
    
    // Find claims requiring review (older than cutoff date)
    @Query("SELECT c FROM Claim c WHERE c.status = apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum.WAITING_FOR_REVIEW AND c.createdAt < :cutoffDate")
    List<Claim> findClaimsRequiringReview(@Param("cutoffDate") LocalDateTime cutoffDate);
}