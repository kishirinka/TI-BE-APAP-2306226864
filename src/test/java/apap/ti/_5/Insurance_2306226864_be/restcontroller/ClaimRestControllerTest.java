package apap.ti._5.Insurance_2306226864_be.restcontroller;

import apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.OrderedPlanStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import apap.ti._5.Insurance_2306226864_be.model.Claim;
import apap.ti._5.Insurance_2306226864_be.model.InsurancePlan;
import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
import apap.ti._5.Insurance_2306226864_be.restdto.request.claim.CreateClaimRequestDTO;
import apap.ti._5.Insurance_2306226864_be.restdto.request.claim.ProcessClaimRequestDTO;
import apap.ti._5.Insurance_2306226864_be.service.ClaimService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClaimRestController.class)
class ClaimRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClaimService claimService;

    private Claim testClaim;
    private OrderedPlan testOrderedPlan;
    private InsurancePlan testInsurancePlan;

    @BeforeEach
    void setUp() {
        // Setup test data
        testInsurancePlan = new InsurancePlan();
        testInsurancePlan.setId("INS1");
        testInsurancePlan.setPlanName("Health Plus");
        testInsurancePlan.setProviderId("PROV123");
        testInsurancePlan.setPrice(500000);
        testInsurancePlan.setCoverage(10000000);
        testInsurancePlan.setApplicableService(Arrays.asList(ServiceEnum.ACCOMMODATION));

        testOrderedPlan = new OrderedPlan();
        testOrderedPlan.setId("POL1-OP1");
        testOrderedPlan.setStatus(OrderedPlanStatusEnum.PAID);
        testOrderedPlan.setExpiredDate(LocalDate.now().plusDays(30));
        testOrderedPlan.setInsurancePlan(testInsurancePlan);

        testClaim = new Claim();
        testClaim.setId("POL1-OP1-CLAIM1");
        testClaim.setStatus(ClaimStatusEnum.WAITING_FOR_REVIEW);
        testClaim.setProof("proof-document.pdf");
        testClaim.setOrderedPlan(testOrderedPlan);
        testClaim.setCreatedAt(LocalDateTime.now());
        testClaim.setUpdatedAt(LocalDateTime.now());
    }

    // ========================== GET ALL CLAIMS ==========================
    @Test
    void testGetAllClaims_NoFilter_Success() throws Exception {
        // Arrange
        when(claimService.getAllClaims()).thenReturn(Arrays.asList(testClaim));

        // Act & Assert
        mockMvc.perform(get("/api/claims"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value("POL1-OP1-CLAIM1"))
                .andExpect(jsonPath("$.data[0].status").value("WAITING_FOR_REVIEW"));

        verify(claimService, times(1)).getAllClaims();
    }

    @Test
    void testGetAllClaims_FilterByStatus_Success() throws Exception {
        // Arrange
        when(claimService.getClaimsByStatus("WAITING_FOR_REVIEW"))
                .thenReturn(Arrays.asList(testClaim));

        // Act & Assert
        mockMvc.perform(get("/api/claims")
                        .param("status", "WAITING_FOR_REVIEW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].status").value("WAITING_FOR_REVIEW"));

        verify(claimService, times(1)).getClaimsByStatus("WAITING_FOR_REVIEW");
    }

    @Test
    void testGetAllClaims_FilterByInsurancePlanId_Success() throws Exception {
        // Arrange
        when(claimService.getClaimsByInsurancePlanId("INS1"))
                .thenReturn(Arrays.asList(testClaim));

        // Act & Assert
        mockMvc.perform(get("/api/claims")
                        .param("insurancePlanId", "INS1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());

        verify(claimService, times(1)).getClaimsByInsurancePlanId("INS1");
    }

    @Test
    void testGetAllClaims_FilterByBoth_Success() throws Exception {
        // Arrange
        when(claimService.getClaimsByFilters("WAITING_FOR_REVIEW", "INS1"))
                .thenReturn(Arrays.asList(testClaim));

        // Act & Assert
        mockMvc.perform(get("/api/claims")
                        .param("status", "WAITING_FOR_REVIEW")
                        .param("insurancePlanId", "INS1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(claimService, times(1)).getClaimsByFilters("WAITING_FOR_REVIEW", "INS1");
    }

    @Test
    void testGetAllClaims_InvalidStatus() throws Exception {
        // Arrange
        when(claimService.getClaimsByStatus("INVALID_STATUS"))
                .thenThrow(new RuntimeException("Invalid status"));

        // Act & Assert
        mockMvc.perform(get("/api/claims")
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testGetAllClaims_EmptyResult() throws Exception {
        // Arrange
        when(claimService.getAllClaims()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/claims"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("No claims found"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // ========================== GET CLAIM BY ID ==========================
    @Test
    void testGetClaimById_Success() throws Exception {
        // Arrange
        when(claimService.getClaimById("POL1-OP1-CLAIM1")).thenReturn(testClaim);

        // Act & Assert
        mockMvc.perform(get("/api/claims/POL1-OP1-CLAIM1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("POL1-OP1-CLAIM1"))
                .andExpect(jsonPath("$.data.status").value("WAITING_FOR_REVIEW"))
                .andExpect(jsonPath("$.data.proof").value("proof-document.pdf"));

        verify(claimService, times(1)).getClaimById("POL1-OP1-CLAIM1");
    }

    @Test
    void testGetClaimById_NotFound() throws Exception {
        // Arrange
        when(claimService.getClaimById("INVALID_ID"))
                .thenThrow(new RuntimeException("Claim not found"));

        // Act & Assert
        mockMvc.perform(get("/api/claims/INVALID_ID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404));
    }

    // ========================== CREATE CLAIM ==========================
    @Test
    void testCreateClaim_Success() throws Exception {
        // Arrange
        CreateClaimRequestDTO requestDTO = new CreateClaimRequestDTO();
        requestDTO.setOrderedPlanId("POL1-OP1");
        requestDTO.setProof("proof-document.pdf");

        when(claimService.createClaim(any(Claim.class), eq("POL1-OP1")))
                .thenReturn(testClaim);

        // Act & Assert
        mockMvc.perform(post("/api/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.id").value("POL1-OP1-CLAIM1"));

        verify(claimService, times(1)).createClaim(any(Claim.class), eq("POL1-OP1"));
    }

    @Test
    void testCreateClaim_ValidationError_MissingOrderedPlanId() throws Exception {
        // Arrange
        CreateClaimRequestDTO requestDTO = new CreateClaimRequestDTO();
        requestDTO.setProof("proof-document.pdf");
        // orderedPlanId is missing

        // Act & Assert
        mockMvc.perform(post("/api/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testCreateClaim_ValidationError_MissingProof() throws Exception {
        // Arrange
        CreateClaimRequestDTO requestDTO = new CreateClaimRequestDTO();
        requestDTO.setOrderedPlanId("POL1-OP1");
        // proof is missing

        // Act & Assert
        mockMvc.perform(post("/api/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testCreateClaim_BusinessLogicError() throws Exception {
        // Arrange
        CreateClaimRequestDTO requestDTO = new CreateClaimRequestDTO();
        requestDTO.setOrderedPlanId("POL1-OP1");
        requestDTO.setProof("proof-document.pdf");

        when(claimService.createClaim(any(Claim.class), eq("POL1-OP1")))
                .thenThrow(new RuntimeException("OrderedPlan must be PAID"));

        // Act & Assert
        mockMvc.perform(post("/api/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400));
    }

    // ========================== PROCESS CLAIM - ACCEPT ==========================
    @Test
    void testProcessClaim_Accept_Success() throws Exception {
        // Arrange
        ProcessClaimRequestDTO requestDTO = new ProcessClaimRequestDTO();
        requestDTO.setAction("ACCEPT");
        requestDTO.setNote("Claim approved");

        testClaim.setStatus(ClaimStatusEnum.ACCEPTED);
        testClaim.setAcceptedNote("Claim approved");
        testClaim.setAcceptedTimestamp(LocalDateTime.now());

        when(claimService.acceptClaim("POL1-OP1-CLAIM1", "Claim approved"))
                .thenReturn(testClaim);

        // Act & Assert
        mockMvc.perform(put("/api/claims/POL1-OP1-CLAIM1/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Claim accepted successfully"))
                .andExpect(jsonPath("$.data.status").value("ACCEPTED"));

        verify(claimService, times(1)).acceptClaim("POL1-OP1-CLAIM1", "Claim approved");
    }

    @Test
    void testProcessClaim_Accept_MissingNote() throws Exception {
        // Arrange
        ProcessClaimRequestDTO requestDTO = new ProcessClaimRequestDTO();
        requestDTO.setAction("ACCEPT");
        // note is missing

        // Act & Assert
        mockMvc.perform(put("/api/claims/POL1-OP1-CLAIM1/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Note is required when accepting a claim"));
    }

    // ========================== PROCESS CLAIM - REJECT ==========================
    @Test
    void testProcessClaim_Reject_Success() throws Exception {
        // Arrange
        ProcessClaimRequestDTO requestDTO = new ProcessClaimRequestDTO();
        requestDTO.setAction("REJECT");
        requestDTO.setRejectionReason("Invalid proof");
        requestDTO.setRejectionDescription("The proof document is not valid");

        testClaim.setStatus(ClaimStatusEnum.REJECTED);
        testClaim.setRejectionReason("Invalid proof");
        testClaim.setRejectionDescription("The proof document is not valid");
        testClaim.setRejectionTimestamp(LocalDateTime.now());

        when(claimService.rejectClaim("POL1-OP1-CLAIM1", "Invalid proof", "The proof document is not valid"))
                .thenReturn(testClaim);

        // Act & Assert
        mockMvc.perform(put("/api/claims/POL1-OP1-CLAIM1/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Claim rejected successfully"))
                .andExpect(jsonPath("$.data.status").value("REJECTED"));

        verify(claimService, times(1)).rejectClaim("POL1-OP1-CLAIM1", "Invalid proof", "The proof document is not valid");
    }

    @Test
    void testProcessClaim_Reject_MissingReason() throws Exception {
        // Arrange
        ProcessClaimRequestDTO requestDTO = new ProcessClaimRequestDTO();
        requestDTO.setAction("REJECT");
        requestDTO.setRejectionDescription("Description");
        // rejectionReason is missing

        // Act & Assert
        mockMvc.perform(put("/api/claims/POL1-OP1-CLAIM1/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Rejection reason is required when rejecting a claim"));
    }

    @Test
    void testProcessClaim_Reject_MissingDescription() throws Exception {
        // Arrange
        ProcessClaimRequestDTO requestDTO = new ProcessClaimRequestDTO();
        requestDTO.setAction("REJECT");
        requestDTO.setRejectionReason("Invalid proof");
        // rejectionDescription is missing

        // Act & Assert
        mockMvc.perform(put("/api/claims/POL1-OP1-CLAIM1/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Rejection description is required when rejecting a claim"));
    }

    @Test
    void testProcessClaim_InvalidAction() throws Exception {
        // Arrange
        ProcessClaimRequestDTO requestDTO = new ProcessClaimRequestDTO();
        requestDTO.setAction("INVALID_ACTION");

        // Act & Assert
        mockMvc.perform(put("/api/claims/POL1-OP1-CLAIM1/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid action: INVALID_ACTION. Must be ACCEPT or REJECT"));
    }

    @Test
    void testProcessClaim_ValidationError_BlankAction() throws Exception {
        // Arrange
        ProcessClaimRequestDTO requestDTO = new ProcessClaimRequestDTO();
        requestDTO.setAction("");

        // Act & Assert
        mockMvc.perform(put("/api/claims/POL1-OP1-CLAIM1/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testProcessClaim_BusinessLogicError() throws Exception {
        // Arrange
        ProcessClaimRequestDTO requestDTO = new ProcessClaimRequestDTO();
        requestDTO.setAction("ACCEPT");
        requestDTO.setNote("Approved");

        when(claimService.acceptClaim("POL1-OP1-CLAIM1", "Approved"))
                .thenThrow(new RuntimeException("Claim must be in WAITING_FOR_REVIEW status"));

        // Act & Assert
        mockMvc.perform(put("/api/claims/POL1-OP1-CLAIM1/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400));
    }
}