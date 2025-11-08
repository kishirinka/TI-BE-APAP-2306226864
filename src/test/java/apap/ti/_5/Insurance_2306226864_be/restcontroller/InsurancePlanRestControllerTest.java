// package apap.ti._5.Insurance_2306226864_be.restcontroller;

// import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
// import apap.ti._5.Insurance_2306226864_be.restdto.request.insuranceplan.CreateInsurancePlanRequestDTO;
// import apap.ti._5.Insurance_2306226864_be.restdto.request.insuranceplan.UpdateInsurancePlanRequestDTO;
// import apap.ti._5.Insurance_2306226864_be.restdto.response.insuranceplan.InsurancePlanResponseDTO;
// import apap.ti._5.Insurance_2306226864_be.service.InsurancePlanService;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;

// import java.time.LocalDateTime;
// import java.util.Arrays;
// import java.util.List;

// import static org.hamcrest.Matchers.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.Mockito.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(InsurancePlanRestController.class)
// class InsurancePlanRestControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private ObjectMapper objectMapper;

//     @MockBean
//     private InsurancePlanService insurancePlanService;

//     private InsurancePlanResponseDTO testInsurancePlanDTO;
//     private CreateInsurancePlanRequestDTO createRequestDTO;
//     private UpdateInsurancePlanRequestDTO updateRequestDTO;

//     @BeforeEach
//     void setUp() {
//         // Setup test insurance plan DTO
//         testInsurancePlanDTO = new InsurancePlanResponseDTO();
//         testInsurancePlanDTO.setId("INS1");
//         testInsurancePlanDTO.setPlanName("Health Plus");
//         testInsurancePlanDTO.setProviderId("PROV123");
//         testInsurancePlanDTO.setPrice(500000);
//         testInsurancePlanDTO.setCoverage(10000000);
//         testInsurancePlanDTO.setCoverageDetails("Comprehensive health coverage");
//         testInsurancePlanDTO.setApplicableService(Arrays.asList(ServiceEnum.ACCOMMODATION));
//         testInsurancePlanDTO.setExpiredByDays(365);
//         testInsurancePlanDTO.setCreatedAt(LocalDateTime.now());
//         testInsurancePlanDTO.setUpdatedAt(LocalDateTime.now());

//         // Setup create request DTO
//         createRequestDTO = new CreateInsurancePlanRequestDTO();
//         createRequestDTO.setPlanName("Travel Safe");
//         createRequestDTO.setProviderId("PROV456");
//         createRequestDTO.setPrice(300000);
//         createRequestDTO.setCoverage(5000000);
//         createRequestDTO.setCoverageDetails("Travel insurance coverage");
//         createRequestDTO.setApplicableService(Arrays.asList(ServiceEnum.FLIGHT));
//         createRequestDTO.setExpiredByDays(180);

//         // Setup update request DTO
//         updateRequestDTO = new UpdateInsurancePlanRequestDTO();
//         updateRequestDTO.setPlanName("Health Plus Updated");
//         updateRequestDTO.setPrice(600000);
//         updateRequestDTO.setCoverage(12000000);
//         updateRequestDTO.setCoverageDetails("Updated comprehensive health coverage");
//         updateRequestDTO.setApplicableService(Arrays.asList(ServiceEnum.ACCOMMODATION, ServiceEnum.FLIGHT));
//         updateRequestDTO.setExpiredByDays(400);
//     }

//     // ========================== TEST GET ALL INSURANCE PLANS - SUCCESS ==========================
//     @Test
//     void testGetAllInsurancePlans_Success() throws Exception {
//         // Arrange
//         InsurancePlanResponseDTO plan2 = new InsurancePlanResponseDTO();
//         plan2.setId("INS2");
//         plan2.setPlanName("Travel Safe");
//         plan2.setProviderId("PROV456");
//         plan2.setPrice(300000);
//         plan2.setCoverage(5000000);
//         plan2.setCoverageDetails("Travel coverage");
//         plan2.setApplicableService(Arrays.asList(ServiceEnum.FLIGHT));
//         plan2.setExpiredByDays(180);
//         plan2.setCreatedAt(LocalDateTime.now());
//         plan2.setUpdatedAt(LocalDateTime.now());

//         List<InsurancePlanResponseDTO> mockPlans = Arrays.asList(testInsurancePlanDTO, plan2);

//         when(insurancePlanService.getAllInsurancePlans(any(PageRequest.class))).thenReturn(mockPlans);

//         // Act & Assert
//         mockMvc.perform(get("/api/insurance-plans")
//                 .param("page", "0")
//                 .param("size", "10")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.success").value(true))
//                 .andExpect(jsonPath("$.status").value(200))
//                 .andExpect(jsonPath("$.message").value("Successfully retrieved all insurance plans"))
//                 .andExpect(jsonPath("$.timestamp").exists())
//                 .andExpect(jsonPath("$.data").isArray())
//                 .andExpect(jsonPath("$.data", hasSize(2)))
//                 .andExpect(jsonPath("$.data[0].id").value("INS1"))
//                 .andExpect(jsonPath("$.data[0].planName").value("Health Plus"))
//                 .andExpect(jsonPath("$.data[0].price").value(500000))
//                 .andExpect(jsonPath("$.data[1].id").value("INS2"))
//                 .andExpect(jsonPath("$.data[1].planName").value("Travel Safe"));

//         verify(insurancePlanService, times(1)).getAllInsurancePlans(any(PageRequest.class));
//     }

//     // ========================== TEST GET ALL INSURANCE PLANS - EMPTY ==========================
//     @Test
//     void testGetAllInsurancePlans_Empty() throws Exception {
//         // Arrange
//         when(insurancePlanService.getAllInsurancePlans(any(PageRequest.class))).thenReturn(Arrays.asList());

//         // Act & Assert
//         mockMvc.perform(get("/api/insurance-plans")
//                 .param("page", "0")
//                 .param("size", "10")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.success").value(true))
//                 .andExpect(jsonPath("$.status").value(200))
//                 .andExpect(jsonPath("$.message").value("No insurance plans found"))
//                 .andExpect(jsonPath("$.data").isArray())
//                 .andExpect(jsonPath("$.data", hasSize(0)));

//         verify(insurancePlanService, times(1)).getAllInsurancePlans(any(PageRequest.class));
//     }

//     // ========================== TEST GET INSURANCE PLAN BY ID - SUCCESS ==========================
//     @Test
//     void testGetInsurancePlanById_Success() throws Exception {
//         // Arrange
//         when(insurancePlanService.getInsurancePlanById("INS1")).thenReturn(testInsurancePlanDTO);

//         // Act & Assert
//         mockMvc.perform(get("/api/insurance-plans/INS1")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.success").value(true))
//                 .andExpect(jsonPath("$.status").value(200))
//                 .andExpect(jsonPath("$.message").value("Successfully retrieved insurance plan with ID: INS1"))
//                 .andExpect(jsonPath("$.timestamp").exists())
//                 .andExpect(jsonPath("$.data").exists())
//                 .andExpect(jsonPath("$.data.id").value("INS1"))
//                 .andExpect(jsonPath("$.data.planName").value("Health Plus"))
//                 .andExpect(jsonPath("$.data.providerId").value("PROV123"))
//                 .andExpect(jsonPath("$.data.price").value(500000))
//                 .andExpect(jsonPath("$.data.coverage").value(10000000))
//                 .andExpect(jsonPath("$.data.coverageDetails").value("Comprehensive health coverage"))
//                 .andExpect(jsonPath("$.data.expiredByDays").value(365))
//                 .andExpect(jsonPath("$.data.applicableService").isArray())
//                 .andExpect(jsonPath("$.data.applicableService[0]").value("ACCOMMODATION"));

//         verify(insurancePlanService, times(1)).getInsurancePlanById("INS1");
//     }

//     // ========================== TEST GET INSURANCE PLAN BY ID - NOT FOUND ==========================
//     @Test
//     void testGetInsurancePlanById_NotFound() throws Exception {
//         // Arrange
//         when(insurancePlanService.getInsurancePlanById("INS999"))
//                 .thenThrow(new RuntimeException("Insurance Plan with ID INS999 not found or has been deleted"));

//         // Act & Assert
//         mockMvc.perform(get("/api/insurance-plans/INS999")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isNotFound())
//                 .andExpect(jsonPath("$.success").value(false))
//                 .andExpect(jsonPath("$.status").value(404))
//                 .andExpect(jsonPath("$.message").value("Insurance plan not found with ID: INS999"))
//                 .andExpect(jsonPath("$.timestamp").exists())
//                 .andExpect(jsonPath("$.data").doesNotExist());

//         verify(insurancePlanService, times(1)).getInsurancePlanById("INS999");
//     }

//     // ========================== TEST CREATE INSURANCE PLAN - SUCCESS ==========================
//     @Test
//     void testCreateInsurancePlan_Success() throws Exception {
//         // Arrange
//         InsurancePlanResponseDTO createdPlan = new InsurancePlanResponseDTO();
//         createdPlan.setId("INS2");
//         createdPlan.setPlanName("Travel Safe");
//         createdPlan.setProviderId("PROV456");
//         createdPlan.setPrice(300000);
//         createdPlan.setCoverage(5000000);
//         createdPlan.setCoverageDetails("Travel insurance coverage");
//         createdPlan.setApplicableService(Arrays.asList(ServiceEnum.FLIGHT));
//         createdPlan.setExpiredByDays(180);
//         createdPlan.setCreatedAt(LocalDateTime.now());
//         createdPlan.setUpdatedAt(LocalDateTime.now());

//         when(insurancePlanService.createInsurancePlan(any(CreateInsurancePlanRequestDTO.class)))
//                 .thenReturn(createdPlan);

//         // Act & Assert
//         mockMvc.perform(post("/api/insurance-plans")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(createRequestDTO)))
//                 .andExpect(status().isCreated())
//                 .andExpect(jsonPath("$.success").value(true))
//                 .andExpect(jsonPath("$.status").value(201))
//                 .andExpect(jsonPath("$.message").value("Insurance plan created successfully"))
//                 .andExpect(jsonPath("$.timestamp").exists())
//                 .andExpect(jsonPath("$.data").exists())
//                 .andExpect(jsonPath("$.data.id").value("INS2"))
//                 .andExpect(jsonPath("$.data.planName").value("Travel Safe"))
//                 .andExpect(jsonPath("$.data.providerId").value("PROV456"))
//                 .andExpect(jsonPath("$.data.price").value(300000))
//                 .andExpect(jsonPath("$.data.coverage").value(5000000))
//                 .andExpect(jsonPath("$.data.expiredByDays").value(180));

//         verify(insurancePlanService, times(1)).createInsurancePlan(any(CreateInsurancePlanRequestDTO.class));
//     }

//     // ========================== TEST CREATE INSURANCE PLAN - VALIDATION ERROR ==========================
//     @Test
//     void testCreateInsurancePlan_ValidationError() throws Exception {
//         // Arrange - Create invalid DTO with empty fields
//         CreateInsurancePlanRequestDTO invalidDTO = new CreateInsurancePlanRequestDTO();
//         // All fields are null/empty, which should fail validation

//         // Act & Assert
//         mockMvc.perform(post("/api/insurance-plans")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(invalidDTO)))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(jsonPath("$.success").value(false))
//                 .andExpect(jsonPath("$.status").value(400))
//                 .andExpect(jsonPath("$.message").exists())
//                 .andExpect(jsonPath("$.timestamp").exists())
//                 .andExpect(jsonPath("$.data").doesNotExist());

//         verify(insurancePlanService, never()).createInsurancePlan(any(CreateInsurancePlanRequestDTO.class));
//     }

//     // ========================== TEST CREATE INSURANCE PLAN - MISSING REQUIRED FIELDS ==========================
//     @Test
//     void testCreateInsurancePlan_MissingRequiredFields() throws Exception {
//         // Arrange - Create DTO with some missing fields
//         CreateInsurancePlanRequestDTO partialDTO = new CreateInsurancePlanRequestDTO();
//         partialDTO.setPlanName("Test Plan");
//         // Missing other required fields

//         // Act & Assert
//         mockMvc.perform(post("/api/insurance-plans")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(partialDTO)))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(jsonPath("$.success").value(false))
//                 .andExpect(jsonPath("$.status").value(400));

//         verify(insurancePlanService, never()).createInsurancePlan(any(CreateInsurancePlanRequestDTO.class));
//     }

//     // ========================== TEST UPDATE INSURANCE PLAN - SUCCESS ==========================
//     @Test
//     void testUpdateInsurancePlan_Success() throws Exception {
//         // Arrange
//         InsurancePlanResponseDTO updatedPlan = new InsurancePlanResponseDTO();
//         updatedPlan.setId("INS1");
//         updatedPlan.setPlanName("Health Plus Updated");
//         updatedPlan.setProviderId("PROV123");
//         updatedPlan.setPrice(600000);
//         updatedPlan.setCoverage(12000000);
//         updatedPlan.setCoverageDetails("Updated comprehensive health coverage");
//         updatedPlan.setApplicableService(Arrays.asList(ServiceEnum.ACCOMMODATION, ServiceEnum.FLIGHT));
//         updatedPlan.setExpiredByDays(400);
//         updatedPlan.setCreatedAt(LocalDateTime.now());
//         updatedPlan.setUpdatedAt(LocalDateTime.now());

//         when(insurancePlanService.updateInsurancePlan(anyString(), any(UpdateInsurancePlanRequestDTO.class)))
//                 .thenReturn(updatedPlan);

//         // Act & Assert
//         mockMvc.perform(put("/api/insurance-plans/INS1")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(updateRequestDTO)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.success").value(true))
//                 .andExpect(jsonPath("$.status").value(200))
//                 .andExpect(jsonPath("$.message").value("Insurance plan updated successfully"))
//                 .andExpect(jsonPath("$.timestamp").exists())
//                 .andExpect(jsonPath("$.data").exists())
//                 .andExpect(jsonPath("$.data.id").value("INS1"))
//                 .andExpect(jsonPath("$.data.planName").value("Health Plus Updated"))
//                 .andExpect(jsonPath("$.data.price").value(600000))
//                 .andExpect(jsonPath("$.data.coverage").value(12000000))
//                 .andExpect(jsonPath("$.data.expiredByDays").value(400))
//                 .andExpect(jsonPath("$.data.applicableService", hasSize(2)));

//         verify(insurancePlanService, times(1)).updateInsurancePlan(eq("INS1"), any(UpdateInsurancePlanRequestDTO.class));
//     }

//     // ========================== TEST UPDATE INSURANCE PLAN - NOT FOUND ==========================
//     @Test
//     void testUpdateInsurancePlan_NotFound() throws Exception {
//         // Arrange
//         when(insurancePlanService.updateInsurancePlan(eq("INS999"), any(UpdateInsurancePlanRequestDTO.class)))
//                 .thenThrow(new RuntimeException("Insurance Plan with ID INS999 not found"));

//         // Act & Assert
//         mockMvc.perform(put("/api/insurance-plans/INS999")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(updateRequestDTO)))
//                 .andExpect(status().isNotFound())
//                 .andExpect(jsonPath("$.success").value(false))
//                 .andExpect(jsonPath("$.status").value(404))
//                 .andExpect(jsonPath("$.message").exists())
//                 .andExpect(jsonPath("$.data").doesNotExist());

//         verify(insurancePlanService, times(1)).updateInsurancePlan(eq("INS999"), any(UpdateInsurancePlanRequestDTO.class));
//     }

//     // ========================== TEST DELETE INSURANCE PLAN - SUCCESS ==========================
//     @Test
//     void testDeleteInsurancePlan_Success() throws Exception {
//         // Arrange
//         when(insurancePlanService.deleteInsurancePlan("INS1")).thenReturn(true);

//         // Act & Assert
//         mockMvc.perform(delete("/api/insurance-plans/INS1")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.success").value(true))
//                 .andExpect(jsonPath("$.status").value(200))
//                 .andExpect(jsonPath("$.message").value("Insurance plan deleted successfully"))
//                 .andExpect(jsonPath("$.timestamp").exists())
//                 .andExpect(jsonPath("$.data").value(true));

//         verify(insurancePlanService, times(1)).deleteInsurancePlan("INS1");
//     }

//     // ========================== TEST DELETE INSURANCE PLAN - HAS ACTIVE ORDERED PLANS ==========================
//     @Test
//     void testDeleteInsurancePlan_HasActiveOrderedPlans() throws Exception {
//         // Arrange
//         when(insurancePlanService.deleteInsurancePlan("INS1"))
//                 .thenThrow(new RuntimeException("Cannot delete insurance plan: some ordered plans are still active"));

//         // Act & Assert
//         mockMvc.perform(delete("/api/insurance-plans/INS1")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(jsonPath("$.success").value(false))
//                 .andExpect(jsonPath("$.status").value(400))
//                 .andExpect(jsonPath("$.message").value("Cannot delete insurance plan: some ordered plans are still active"))
//                 .andExpect(jsonPath("$.timestamp").exists())
//                 .andExpect(jsonPath("$.data").doesNotExist());

//         verify(insurancePlanService, times(1)).deleteInsurancePlan("INS1");
//     }

//     // ========================== TEST DELETE INSURANCE PLAN - NOT FOUND ==========================
//     @Test
//     void testDeleteInsurancePlan_NotFound() throws Exception {
//         // Arrange
//         when(insurancePlanService.deleteInsurancePlan("INS999"))
//                 .thenThrow(new RuntimeException("Insurance Plan with ID INS999 not found or has been deleted"));

//         // Act & Assert
//         mockMvc.perform(delete("/api/insurance-plans/INS999")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isNotFound())
//                 .andExpect(jsonPath("$.success").value(false))
//                 .andExpect(jsonPath("$.status").value(404))
//                 .andExpect(jsonPath("$.message").exists())
//                 .andExpect(jsonPath("$.data").doesNotExist());

//         verify(insurancePlanService, times(1)).deleteInsurancePlan("INS999");
//     }

//     // ========================== TEST GET INSURANCE PLANS COUNT ==========================
//     @Test
//     void testGetInsurancePlansCount() throws Exception {
//         // Arrange
//         when(insurancePlanService.getInsurancePlansCount()).thenReturn(25L);

//         // Act & Assert
//         mockMvc.perform(get("/api/insurance-plans/count")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.success").value(true))
//                 .andExpect(jsonPath("$.status").value(200))
//                 .andExpect(jsonPath("$.message").value("Successfully retrieved insurance plans count"))
//                 .andExpect(jsonPath("$.data").value(25));

//         verify(insurancePlanService, times(1)).getInsurancePlansCount();
//     }
// }
