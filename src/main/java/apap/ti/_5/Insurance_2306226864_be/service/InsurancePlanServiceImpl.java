package apap.ti._5.Insurance_2306226864_be.service;

import apap.ti._5.Insurance_2306226864_be.model.InsurancePlan;
import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
import apap.ti._5.Insurance_2306226864_be.repository.InsurancePlanRepository;
import apap.ti._5.Insurance_2306226864_be.repository.OrderedPlanRepository;
import apap.ti._5.Insurance_2306226864_be.restdto.request.insuranceplan.CreateInsurancePlanRequestDTO;
import apap.ti._5.Insurance_2306226864_be.restdto.request.insuranceplan.UpdateInsurancePlanRequestDTO;
import apap.ti._5.Insurance_2306226864_be.restdto.response.insuranceplan.InsurancePlanResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InsurancePlanServiceImpl implements InsurancePlanService {

    @Autowired
    private InsurancePlanRepository insurancePlanRepository;

    @Autowired
    private OrderedPlanRepository orderedPlanRepository;

    // ========================== GET ALL ==========================
    @Override
    public List<InsurancePlanResponseDTO> getAllInsurancePlans(PageRequest pageRequest) {
        return insurancePlanRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(InsurancePlanResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ========================== GET BY ID ==========================
    @Override
    public InsurancePlanResponseDTO getInsurancePlanById(String id) {
        InsurancePlan plan = insurancePlanRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() ->
                        new RuntimeException("Insurance Plan with ID " + id + " not found or has been deleted"));
        return InsurancePlanResponseDTO.fromEntity(plan);
    }

    // ========================== GET ENTITY BY ID (for internal service use) ==========================
    @Override
    public InsurancePlan getInsurancePlanEntityById(String id) {
        return insurancePlanRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() ->
                        new RuntimeException("Insurance Plan with ID " + id + " not found or has been deleted"));
    }

    // ========================== CREATE ==========================
    @Override
    public InsurancePlanResponseDTO createInsurancePlan(CreateInsurancePlanRequestDTO request) {
        Long totalCount = insurancePlanRepository.count();
        String newId = "INS" + (totalCount + 1);

        InsurancePlan plan = new InsurancePlan();
        plan.setId(newId);
        plan.setPlanName(request.getPlanName());
        plan.setProviderId(request.getProviderId());
        plan.setPrice(request.getPrice());
        plan.setCoverage(request.getCoverage());
        plan.setCoverageDetails(request.getCoverageDetails());
        plan.setApplicableService(request.getApplicableService());
        plan.setExpiredByDays(request.getExpiredByDays());
        plan.setCreatedAt(LocalDateTime.now());
        plan.setUpdatedAt(LocalDateTime.now());
        plan.setDeletedAt(null);

        InsurancePlan savedPlan = insurancePlanRepository.save(plan);
        return InsurancePlanResponseDTO.fromEntity(savedPlan);
    }

    // ========================== UPDATE ==========================
    @Override
    public InsurancePlanResponseDTO updateInsurancePlan(String id, UpdateInsurancePlanRequestDTO request) {
        InsurancePlan existingPlan = insurancePlanRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() ->
                        new RuntimeException("Insurance Plan with ID " + id + " not found or has been deleted"));

        existingPlan.setPlanName(request.getPlanName());
        existingPlan.setPrice(request.getPrice());
        existingPlan.setCoverage(request.getCoverage());
        existingPlan.setCoverageDetails(request.getCoverageDetails());
        existingPlan.setApplicableService(request.getApplicableService());
        existingPlan.setExpiredByDays(request.getExpiredByDays());
        existingPlan.setUpdatedAt(LocalDateTime.now());

        InsurancePlan updatedPlan = insurancePlanRepository.save(existingPlan);
        return InsurancePlanResponseDTO.fromEntity(updatedPlan);
    }

    // ========================== DELETE (SOFT DELETE) ==========================
    @Override
    public boolean deleteInsurancePlan(String id) {
        InsurancePlan plan = insurancePlanRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() ->
                        new RuntimeException("Insurance Plan with ID " + id + " not found or has been deleted"));

        boolean canDelete = plan.getOrderedPlans() == null ||
                plan.getOrderedPlans().stream()
                        .allMatch(orderedPlan ->
                                orderedPlan.getExpiredDate().isBefore(LocalDate.now()));

        if (!canDelete) {
            throw new RuntimeException("Cannot delete insurance plan: some ordered plans are still active");
        }

        plan.setDeletedAt(LocalDateTime.now());
        plan.setUpdatedAt(LocalDateTime.now());
        insurancePlanRepository.save(plan);
        return true;
    }

    // ========================== COUNT ==========================
    @Override
    public Long getInsurancePlansCount() {
        return insurancePlanRepository.countByDeletedAtIsNull();
    }
}
