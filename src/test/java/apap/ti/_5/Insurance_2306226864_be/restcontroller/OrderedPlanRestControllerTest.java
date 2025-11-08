package apap.ti._5.Insurance_2306226864_be.restcontroller;

import apap.ti._5.Insurance_2306226864_be.enums.OrderedPlanStatusEnum;
import apap.ti._5.Insurance_2306226864_be.model.InsurancePlan;
import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
import apap.ti._5.Insurance_2306226864_be.service.OrderedPlanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for OrderedPlanRestController.
 * Uses @WebMvcTest to focus on the web layer and mocks the OrderedPlanService dependency.
 */
@WebMvcTest(OrderedPlanRestController.class)
class OrderedPlanRestControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to perform mock HTTP requests

    @Autowired
    private ObjectMapper objectMapper; // Not strictly needed for GET, but good practice

    @MockBean
    private OrderedPlanService orderedPlanService; // Mocked service layer

    private OrderedPlan testOrderedPlan;

    @BeforeEach
    void setUp() {
        // 1. Setup a dummy InsurancePlan to avoid NullPointerException in the DTO mapper
        InsurancePlan dummyPlan = new InsurancePlan();
        dummyPlan.setId("INS1");
        dummyPlan.setPlanName("Dummy Plan");

        // 2. Setup a common OrderedPlan entity for responses
        testOrderedPlan = new OrderedPlan();
        testOrderedPlan.setId("OP1");
        testOrderedPlan.setStatus(OrderedPlanStatusEnum.PAID);
        testOrderedPlan.setExpiredDate(LocalDate.now().plusDays(30));
        
        // IMPORTANT: Initialize relationships to avoid NPE in OrderedPlanResponseDTO.fromEntity
        testOrderedPlan.setInsurancePlan(dummyPlan); 
        testOrderedPlan.setClaims(Collections.emptyList()); 
    }

    // ========================== GET /api/ordered-plans/{id} ==========================

    @Test
    void testGetOrderedPlanById_Success() throws Exception {
        // Arrange: Mock the service to return the test entity when "OP1" is requested
        when(orderedPlanService.getOrderedPlanById("OP1")).thenReturn(testOrderedPlan);

        // Act & Assert
        mockMvc.perform(get("/api/ordered-plans/OP1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("OP1"))
                .andExpect(jsonPath("$.data.status").value("PAID"))
                .andExpect(jsonPath("$.message").value("Successfully retrieved ordered plan with ID: OP1"));
    }

    @Test
    void testGetOrderedPlanById_NotFound() throws Exception {
        // Arrange: Mock the service to throw a RuntimeException when "OP999" is requested
        String errorMessage = "Ordered plan not found";
        when(orderedPlanService.getOrderedPlanById("OP999"))
                .thenThrow(new RuntimeException(errorMessage));

        // Act & Assert
        mockMvc.perform(get("/api/ordered-plans/OP999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Ordered plan not found with ID: OP999"));
    }
}