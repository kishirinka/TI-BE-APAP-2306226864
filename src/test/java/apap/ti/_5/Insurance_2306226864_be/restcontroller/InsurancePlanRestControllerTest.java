package apap.ti._5.Insurance_2306226864_be.restcontroller;

import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import apap.ti._5.Insurance_2306226864_be.restdto.request.insuranceplan.CreateInsurancePlanRequestDTO;
import apap.ti._5.Insurance_2306226864_be.restdto.request.insuranceplan.UpdateInsurancePlanRequestDTO;
import apap.ti._5.Insurance_2306226864_be.restdto.response.insuranceplan.InsurancePlanResponseDTO;
import apap.ti._5.Insurance_2306226864_be.service.InsurancePlanService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InsurancePlanRestController.class)
class InsurancePlanRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InsurancePlanService insurancePlanService;

    private InsurancePlanResponseDTO testPlanDTO;

    @BeforeEach
    void setUp() {
        testPlanDTO = new InsurancePlanResponseDTO();
        testPlanDTO.setId("INS1");
        testPlanDTO.setPlanName("Health Plus");
        testPlanDTO.setProviderId("PROV123");
        testPlanDTO.setPrice(500000);
        testPlanDTO.setCoverage(10000000);
        testPlanDTO.setCoverageDetails("Comprehensive health coverage");
        testPlanDTO.setApplicableService(Arrays.asList(ServiceEnum.ACCOMMODATION));
        testPlanDTO.setExpiredByDays(365);
        testPlanDTO.setCreatedAt(LocalDateTime.now());
        testPlanDTO.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testGetAllInsurancePlans_Success() throws Exception {
        when(insurancePlanService.getAllInsurancePlans(any(PageRequest.class)))
                .thenReturn(Arrays.asList(testPlanDTO));

        mockMvc.perform(get("/api/insurance-plans")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("INS1"));

        verify(insurancePlanService, times(1)).getAllInsurancePlans(any(PageRequest.class));
    }

    @Test
    void testGetAllInsurancePlans_EmptyList() throws Exception {
        when(insurancePlanService.getAllInsurancePlans(any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/insurance-plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("No insurance plans available yet"));
    }

    @Test
    void testGetInsurancePlanById_Success() throws Exception {
        when(insurancePlanService.getInsurancePlanById("INS1"))
                .thenReturn(testPlanDTO);

        mockMvc.perform(get("/api/insurance-plans/INS1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("INS1"));

        verify(insurancePlanService, times(1)).getInsurancePlanById("INS1");
    }

    @Test
    void testGetInsurancePlanById_NotFound() throws Exception {
        when(insurancePlanService.getInsurancePlanById("INVALID_ID"))
                .thenReturn(null);

        mockMvc.perform(get("/api/insurance-plans/INVALID_ID"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateInsurancePlan_Success() throws Exception {
        CreateInsurancePlanRequestDTO requestDTO = new CreateInsurancePlanRequestDTO();
        requestDTO.setPlanName("Health Plus");
        requestDTO.setProviderId("PROV123");
        requestDTO.setPrice(500000);
        requestDTO.setCoverage(10000000);
        requestDTO.setCoverageDetails("Comprehensive health coverage");
        requestDTO.setApplicableService(Arrays.asList(ServiceEnum.ACCOMMODATION));
        requestDTO.setExpiredByDays(365);

        when(insurancePlanService.createInsurancePlan(any(CreateInsurancePlanRequestDTO.class)))
                .thenReturn(testPlanDTO);

        mockMvc.perform(post("/api/insurance-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value("INS1"));
    }

    @Test
    void testCreateInsurancePlan_ValidationError() throws Exception {
        CreateInsurancePlanRequestDTO requestDTO = new CreateInsurancePlanRequestDTO();
        requestDTO.setProviderId("PROV123");

        mockMvc.perform(post("/api/insurance-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateInsurancePlan_Success() throws Exception {
        UpdateInsurancePlanRequestDTO requestDTO = new UpdateInsurancePlanRequestDTO();
        requestDTO.setPlanName("Health Plus Updated");
        requestDTO.setPrice(600000);
        requestDTO.setCoverage(12000000);
        requestDTO.setCoverageDetails("Updated coverage");
        requestDTO.setApplicableService(Arrays.asList(ServiceEnum.ACCOMMODATION));
        requestDTO.setExpiredByDays(400);

        testPlanDTO.setPlanName("Health Plus Updated");
        when(insurancePlanService.updateInsurancePlan(eq("INS1"), any(UpdateInsurancePlanRequestDTO.class)))
                .thenReturn(testPlanDTO);

        mockMvc.perform(put("/api/insurance-plans/INS1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.planName").value("Health Plus Updated"));
    }

    @Test
    void testUpdateInsurancePlan_NotFound() throws Exception {
        UpdateInsurancePlanRequestDTO requestDTO = new UpdateInsurancePlanRequestDTO();
        requestDTO.setPlanName("Updated");
        requestDTO.setPrice(600000);
        requestDTO.setCoverage(12000000);
        requestDTO.setCoverageDetails("Updated");
        requestDTO.setApplicableService(Arrays.asList(ServiceEnum.ACCOMMODATION));
        requestDTO.setExpiredByDays(400);

        when(insurancePlanService.updateInsurancePlan(eq("INVALID"), any(UpdateInsurancePlanRequestDTO.class)))
                .thenReturn(null);

        mockMvc.perform(put("/api/insurance-plans/INVALID")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteInsurancePlan_Success() throws Exception {
        when(insurancePlanService.deleteInsurancePlan("INS1"))
                .thenReturn(true);

        mockMvc.perform(delete("/api/insurance-plans/INS1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Insurance plan deleted successfully"));
    }

    @Test
    void testDeleteInsurancePlan_HasActiveOrderedPlans() throws Exception {
        when(insurancePlanService.deleteInsurancePlan("INS1"))
                .thenThrow(new RuntimeException("Cannot delete: some ordered plans are still active"));

        mockMvc.perform(delete("/api/insurance-plans/INS1"))
                .andExpect(status().isBadRequest());
    }
}