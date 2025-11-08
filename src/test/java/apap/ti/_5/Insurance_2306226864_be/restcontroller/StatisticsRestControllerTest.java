package apap.ti._5.Insurance_2306226864_be.restcontroller;

import apap.ti._5.Insurance_2306226864_be.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

/**
 * Unit tests for StatisticsRestController.
 * Uses @WebMvcTest to focus on the web layer and mocks the StatisticsService dependency.
 */
@WebMvcTest(StatisticsRestController.class)
class StatisticsRestControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to perform mock HTTP requests

    @MockBean
    private StatisticsService statisticsService; // Mocked service layer

    private Map<String, Integer> homepageStats;
    private Map<String, Long> planStats;

    @BeforeEach
    void setUp() {
        // Setup data for homepage statistics
        homepageStats = new HashMap<>();
        homepageStats.put("insurancePlans", 10);
        homepageStats.put("policies", 50);
        homepageStats.put("claims", 100);

        // Setup data for insurance plan statistics
        planStats = new HashMap<>();
        planStats.put("Health Plus Plan", 5L);
        planStats.put("Travel Safe Plan", 2L);
    }

    // ========================== GET /api/statistics/homepage ==========================

    @Test
    void testGetHomepageStatistics_Success() throws Exception {
        // Arrange: Mock the service to return the homepage stats map
        when(statisticsService.getHomepageStatistics()).thenReturn(homepageStats);

        // Act & Assert
        mockMvc.perform(get("/api/statistics/homepage")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Successfully retrieved homepage statistics"))
                .andExpect(jsonPath("$.data.insurancePlans").value(10))
                .andExpect(jsonPath("$.data.policies").value(50))
                .andExpect(jsonPath("$.data.claims").value(100));
    }

    // ========================== GET /api/statistics/insurance-plans ==========================

    @Test
    void testGetInsurancePlanStatistics_Success() throws Exception {
        // Arrange: Mock the service to return plan stats for specific valid inputs
        when(statisticsService.getInsurancePlanStatistics("ALL", 6)).thenReturn(planStats);

        // Act & Assert
        mockMvc.perform(get("/api/statistics/insurance-plans")
                        .param("service", "ALL")
                        .param("months", "6")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data['Health Plus Plan']").value(5))
                .andExpect(jsonPath("$.data['Travel Safe Plan']").value(2))
                .andExpect(jsonPath("$.message", containsString("service 'ALL' in the last 6 months")));
    }
    
    @Test
    void testGetInsurancePlanStatistics_Success_CaseInsensitiveService() throws Exception {
        // Arrange: Mock the service to return plan stats for "flight" (lowercase)
        when(statisticsService.getInsurancePlanStatistics("FLIGHT", 3)).thenReturn(planStats);

        // Act & Assert: Call with "flight" (lowercase)
        mockMvc.perform(get("/api/statistics/insurance-plans")
                        .param("service", "flight") // Use lowercase
                        .param("months", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message", containsString("service 'FLIGHT' in the last 3 months")));
    }

    @Test
    void testGetInsurancePlanStatistics_Fail_MissingServiceParam() throws Exception {
        // Act & Assert: Perform request without the 'service' parameter
        // This is caught by @RequestParam(required = true)
        mockMvc.perform(get("/api/statistics/insurance-plans")
                        .param("months", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Expect HTTP 400 Bad Request
    }
    
    @Test
    void testGetInsurancePlanStatistics_Fail_EmptyServiceParam() throws Exception {
        // Act & Assert: Perform request with an empty 'service' parameter
        // This is caught by the controller's "if (service == null || service.trim().isEmpty())"
        mockMvc.perform(get("/api/statistics/insurance-plans")
                        .param("service", "  ") // Empty/whitespace string
                        .param("months", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Service parameter is required")));
    }

    @Test
    void testGetInsurancePlanStatistics_Fail_InvalidServiceValue() throws Exception {
        // Act & Assert: Perform request with an invalid 'service' value
        mockMvc.perform(get("/api/statistics/insurance-plans")
                        .param("service", "INVALID_SERVICE")
                        .param("months", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Invalid service: INVALID_SERVICE")));
    }

    @Test
    void testGetInsurancePlanStatistics_Fail_MissingMonthsParam() throws Exception {
        // Act & Assert: Perform request without the 'months' parameter
        // This is caught by @RequestParam(required = true)
        mockMvc.perform(get("/api/statistics/insurance-plans")
                        .param("service", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Expect HTTP 400 Bad Request
    }

    @Test
    void testGetInsurancePlanStatistics_Fail_InvalidMonthsValue() throws Exception {
        // Act & Assert: Perform request with an invalid 'months' value (e.g., 5)
        mockMvc.perform(get("/api/statistics/insurance-plans")
                        .param("service", "ALL")
                        .param("months", "5") // 5 is not in [3, 6, 12]
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Invalid months: 5")));
    }
}