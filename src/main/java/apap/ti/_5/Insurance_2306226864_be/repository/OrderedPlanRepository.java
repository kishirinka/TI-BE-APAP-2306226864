package apap.ti._5.Insurance_2306226864_be.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import apap.ti._5.Insurance_2306226864_be.enums.OrderedPlanStatusEnum;
import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;

@Repository
public interface OrderedPlanRepository extends JpaRepository<OrderedPlan, String> {
    
    List<OrderedPlan> findByStatus(OrderedPlanStatusEnum status);
    List<OrderedPlan> findByPolicy_Id(String policyId);
    List<OrderedPlan> findByInsurancePlan_Id(String insurancePlanId);
    List<OrderedPlan> findByExpiredDateBefore(LocalDate currentDate);
    List<OrderedPlan> findByExpiredDateBetween(LocalDate startDate, LocalDate endDate);
    long countByPolicyId(String policyId);


    /**
     * Find active OrderedPlans by InsurancePlan ID (not expired and not in EXPIRED/REJECTED status)
     * @param insurancePlanId The insurance plan ID
     * @param currentDate The current date to check for expiration
     * @return List of active ordered plans
     */
    @Query("SELECT op FROM OrderedPlan op WHERE op.insurancePlan.id = :insurancePlanId " +
           "AND op.expiredDate >= :currentDate " +
           "AND op.status NOT IN (OrderedPlanStatusEnum.EXPIRED, OrderedPlanStatusEnum.REJECTED)")
    List<OrderedPlan> findActiveOrderedPlansByInsurancePlanId(@Param("insurancePlanId") String insurancePlanId, 
                                                               @Param("currentDate") LocalDate currentDate);

    
    @Query("SELECT DISTINCT op FROM OrderedPlan op " +
           "JOIN op.claims c " +
           "WHERE op.insurancePlan.id = :insurancePlanId")
    List<OrderedPlan> findOrderedPlansWithClaimsByInsurancePlanId(@Param("insurancePlanId") String insurancePlanId);
}