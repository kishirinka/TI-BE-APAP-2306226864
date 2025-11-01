package apap.ti._5.Insurance_2306226864_be.service;

import apap.ti._5.Insurance_2306226864_be.model.InsurancePlan;
import apap.ti._5.Insurance_2306226864_be.restdto.request.insuranceplan.CreateInsurancePlanRequestDTO;
import apap.ti._5.Insurance_2306226864_be.restdto.request.insuranceplan.UpdateInsurancePlanRequestDTO;
import apap.ti._5.Insurance_2306226864_be.restdto.response.insuranceplan.InsurancePlanResponseDTO;

import java.util.List;

import org.springframework.data.domain.PageRequest;

public interface InsurancePlanService {

    List<InsurancePlanResponseDTO> getAllInsurancePlans(PageRequest pageRequest);

    InsurancePlanResponseDTO getInsurancePlanById(String id);
    
    InsurancePlan getInsurancePlanEntityById(String id);

    InsurancePlanResponseDTO createInsurancePlan(CreateInsurancePlanRequestDTO request);

    InsurancePlanResponseDTO updateInsurancePlan(String id, UpdateInsurancePlanRequestDTO request);

    boolean deleteInsurancePlan(String id);

    Long getInsurancePlansCount();
}
