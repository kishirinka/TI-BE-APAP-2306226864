package apap.ti._5.Insurance_2306226864_be.restcontroller;

import apap.ti._5.Insurance_2306226864_be.enums.PolicyStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import apap.ti._5.Insurance_2306226864_be.model.Policy;
import apap.ti._5.Insurance_2306226864_be.restdto.request.policy.CreatePolicyRequestDTO;
import apap.ti._5.Insurance_2306226864_be.service.PolicyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for PolicyRestController.
 * Uses @WebMvcTest to focus on the web layer and mocks the PolicyService dependency.
 */
@WebMvcTest(PolicyRestController.class)
class PolicyRestControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to perform mock HTTP requests

    @Autowired
    private ObjectMapper objectMapper; // Used to serialize/deserialize JSON

    @MockBean
    private PolicyService policyService; // Mocked service layer

    private Policy testPolicy;
    private CreatePolicyRequestDTO createRequestDTO;

    @BeforeEach
    void setUp() {
        // 1. Setup a common Policy entity for responses
        testPolicy = new Policy();
        testPolicy.setId("POL1");
        testPolicy.setUserId("user123");
        testPolicy.setBookingId("BOOK123");
        testPolicy.setStatus(PolicyStatusEnum.CREATED);
        testPolicy.setService(ServiceEnum.FLIGHT);
        testPolicy.setStartDate(LocalDate.now());
        testPolicy.setTotalPrice(100000);
        testPolicy.setTotalCoverage(5000000);
        testPolicy.setOrderedPlans(Collections.emptyList()); // Initialize to avoid NPE in DTO mapper

        // 2. Setup a common CreatePolicyRequestDTO for POST requests
        createRequestDTO = new CreatePolicyRequestDTO(
                "user123", "BOOK123", ServiceEnum.FLIGHT,
                LocalDate.now(), Arrays.asList("INS1")
        );
    }

    // ========================== GET /api/policies (Get All) ==========================

    @Test
    void testGetAllPolicies_Success() throws Exception {
        // Arrange: Mock service to return a list containing our test policy
        when(policyService.getAllPolicies()).thenReturn(Arrays.asList(testPolicy));
        // Mock the void method updateExpiredPolicies() which is called in this endpoint
        doNothing().when(policyService).updateExpiredPolicies();

        // Act & Assert
        mockMvc.perform(get("/api/policies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1)) // Expect one item in the data array
                .andExpect(jsonPath("$.data[0].id").value("POL1"));

        // Verify that updateExpiredPolicies was called exactly once
        verify(policyService, times(1)).updateExpiredPolicies();
    }

    @Test
    void testGetAllPolicies_Empty() throws Exception {
        // Arrange: Mock service to return an empty list
        when(policyService.getAllPolicies()).thenReturn(Collections.emptyList());
        doNothing().when(policyService).updateExpiredPolicies();

        // Act & Assert
        mockMvc.perform(get("/api/policies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0)) // Expect empty data array
                .andExpect(jsonPath("$.message").value("No policies found"));
    }

    // ========================== GET /api/policies/{id} (Get By ID) ==========================

    @Test
    void testGetPolicyById_Success() throws Exception {
        // Arrange: Mock service to return the test policy when ID "POL1" is requested
        when(policyService.getPolicyById("POL1")).thenReturn(testPolicy);

        // Act & Assert
        mockMvc.perform(get("/api/policies/POL1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("POL1"))
                .andExpect(jsonPath("$.data.userId").value("user123"));
    }

    @Test
    void testGetPolicyById_NotFound() throws Exception {
        // Arrange: Mock service to throw a RuntimeException when a non-existent ID is requested
        String errorMessage = "Policy with ID POL999 not found";
        when(policyService.getPolicyById("POL999")).thenThrow(new RuntimeException(errorMessage));

        // Act & Assert
        mockMvc.perform(get("/api/policies/POL999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    // ========================== POST /api/policies (Create) ==========================

    @Test
    void testCreatePolicy_Success() throws Exception {
        // Arrange: Mock service to return the created policy
        when(policyService.createPolicy(any(Policy.class), anyList())).thenReturn(testPolicy);

        // Act & Assert
        mockMvc.perform(post("/api/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDTO))) // Send DTO as JSON body
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.id").value("POL1"));
    }

    @Test
    void testCreatePolicy_ValidationFail_UserIdNull() throws Exception {
        // Arrange: Modify DTO to be invalid
        createRequestDTO.setUserId(null); // Set a required field to null

        // Act & Assert
        mockMvc.perform(post("/api/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDTO)))
                .andExpect(status().isBadRequest()) // Expect HTTP 400 Bad Request
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("User ID cannot be null")));
    }
    
    @Test
    void testCreatePolicy_ValidationFail_PlanIdsEmpty() throws Exception {
        // Arrange: Modify DTO to be invalid
        createRequestDTO.setInsurancePlanIds(Collections.emptyList()); // Set plan IDs to empty list

        // Act & Assert
        mockMvc.perform(post("/api/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("At least one insurance plan must be selected")));
    }

    @Test
    void testCreatePolicy_ServiceFail_BusinessLogic() throws Exception {
        // Arrange: Mock service to throw an exception (e.g., plan not applicable)
        String errorMessage = "Insurance plan not applicable for this service";
        when(policyService.createPolicy(any(Policy.class), anyList()))
                .thenThrow(new RuntimeException(errorMessage));

        // Act & Assert
        mockMvc.perform(post("/api/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDTO)))
                .andExpect(status().isBadRequest()) // Expect 400 for business logic failure
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    // ========================== PUT /api/policies/{id}/pay (Pay Policy) ==========================

    @Test
    void testPayPolicy_Success() throws Exception {
        // Arrange: Simulate the policy being returned in a PAID state
        Policy paidPolicy = testPolicy;
        paidPolicy.setStatus(PolicyStatusEnum.PAID); 
        
        when(policyService.payPolicy("POL1")).thenReturn(paidPolicy);

        // Act & Assert
        mockMvc.perform(put("/api/policies/POL1/pay")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Policy paid successfully"))
                .andExpect(jsonPath("$.data.status").value("PAID"));
    }

    @Test
    void testPayPolicy_NotFound() throws Exception {
        // Arrange: Mock service to throw "not found" exception
        String errorMessage = "Policy with ID POL999 not found";
        when(policyService.payPolicy("POL999")).thenThrow(new RuntimeException(errorMessage));

        // Act & Assert
        mockMvc.perform(put("/api/policies/POL999/pay")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Controller logic should catch "not found" and return 404
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void testPayPolicy_Fail_AlreadyPaid() throws Exception {
        // Arrange: Mock service to throw business logic exception
        String errorMessage = "Only policies with CREATED status can be paid";
        when(policyService.payPolicy("POL1")).thenThrow(new RuntimeException(errorMessage));

        // Act & Assert
        mockMvc.perform(put("/api/policies/POL1/pay")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()) // Controller logic should catch other errors and return 400
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }
}