package apap.ti._5.Insurance_2306226864_be.repository;

import apap.ti._5.Insurance_2306226864_be.model.Policy;
import apap.ti._5.Insurance_2306226864_be.enums.PolicyStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, String> {

    List<Policy> findByStatus(PolicyStatusEnum status);
    List<Policy> findByUserId(String userId);
    Optional<Policy> findByBookingId(String bookingId);
    List<Policy> findByService(ServiceEnum service);
    List<Policy> findByUserIdAndStatus(String userId, PolicyStatusEnum status);
    List<Policy> findByStartDateGreaterThanEqual(LocalDate startDate);
    List<Policy> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    @Query("SELECT p FROM Policy p WHERE p.totalCoverage >= :minCoverage")
    List<Policy> findPoliciesWithMinimumCoverage(@Param("minCoverage") Integer minCoverage);
}