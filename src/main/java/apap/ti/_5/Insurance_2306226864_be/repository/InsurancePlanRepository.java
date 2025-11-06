package apap.ti._5.Insurance_2306226864_be.repository;

import apap.ti._5.Insurance_2306226864_be.model.InsurancePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsurancePlanRepository extends JpaRepository<InsurancePlan, String> {

    List<InsurancePlan> findByDeletedAtIsNull();
    List<InsurancePlan> findAllByDeletedAtIsNull();
    Optional<InsurancePlan> findByIdAndDeletedAtIsNull(String id);
    Long countByDeletedAtIsNull();
}
