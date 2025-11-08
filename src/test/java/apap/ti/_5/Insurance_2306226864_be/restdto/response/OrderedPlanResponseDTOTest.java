package apap.ti._5.Insurance_2306226864_be.restdto.response.orderedplan;

import apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.OrderedPlanStatusEnum;
import apap.ti._5.Insurance_2306226864_be.model.Claim;
import apap.ti._5.Insurance_2306226864_be.model.InsurancePlan;
import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class OrderedPlanResponseDTOTest {

    private OrderedPlan testOrderedPlan;
    private InsurancePlan testInsurancePlan;
    private Claim testClaim1, testClaim2;

    @BeforeEach
    void setUp() {
        // 1. Setup InsurancePlan
        testInsurancePlan = new InsurancePlan();
        testInsurancePlan.setId("INS1");
        testInsurancePlan.setPlanName("Test Plan");
        testInsurancePlan.setProviderId("PROV1");
        testInsurancePlan.setPrice(1000);
        testInsurancePlan.setCoverage(5000);

        // 2. Setup Claims
        testClaim1 = new Claim();
        testClaim1.setId("CLM1");
        testClaim1.setStatus(ClaimStatusEnum.ACCEPTED);
        // Set createdAt 5 days ago for logic test
        testClaim1.setCreatedAt(LocalDateTime.now().minusDays(5));
        testClaim1.setAcceptedNote("OK");

        testClaim2 = new Claim();
        testClaim2.setId("CLM2");
        testClaim2.setStatus(ClaimStatusEnum.REJECTED);
        // Set createdAt 2 days ago (this is the most recent claim)
        testClaim2.setCreatedAt(LocalDateTime.now().minusDays(2));
        testClaim2.setRejectionReason("Bad");

        // 3. Setup OrderedPlan
        testOrderedPlan = new OrderedPlan();
        testOrderedPlan.setId("OP1");
        testOrderedPlan.setStatus(OrderedPlanStatusEnum.CLAIMED);
        testOrderedPlan.setExpiredDate(LocalDate.now().plusDays(30));
        testOrderedPlan.setCreatedAt(LocalDateTime.now().minusDays(10));
        testOrderedPlan.setInsurancePlan(testInsurancePlan);
        testOrderedPlan.setClaims(new ArrayList<>(Arrays.asList(testClaim1, testClaim2)));
    }

    @Test
    void testFromEntity_Success_WithClaims() {
        // Act
        OrderedPlanResponseDTO dto = OrderedPlanResponseDTO.fromEntity(testOrderedPlan);

        // Assert (Top-level)
        assertNotNull(dto);
        assertEquals("OP1", dto.getId());
        assertEquals(OrderedPlanStatusEnum.CLAIMED, dto.getStatus());
        assertEquals(testOrderedPlan.getExpiredDate(), dto.getExpiredDate());

        // Assert (Nested InsurancePlanSummaryDTO)
        assertNotNull(dto.getInsurancePlan());
        assertEquals("INS1", dto.getInsurancePlan().getId());
        assertEquals("Test Plan", dto.getInsurancePlan().getPlanName());
        assertEquals(1000, dto.getInsurancePlan().getPrice());

        // Assert (Nested ClaimSummaryDTO List)
        assertNotNull(dto.getClaims());
        assertEquals(2, dto.getClaims().size());
        assertEquals("CLM1", dto.getClaims().get(0).getId());
        assertEquals("CLM2", dto.getClaims().get(1).getId());
        assertEquals("OK", dto.getClaims().get(0).getAcceptedNote());
        assertEquals("Bad", dto.getClaims().get(1).getRejectionReason());

        // Assert (Calculated fields)
        assertEquals(2, dto.getClaimsCount());
        // daysSinceClaimed = days since most recent claim (testClaim2, 2 days ago)
        assertTrue(dto.getDaysSinceClaimed() >= 1); // >= 1 handles edge case of running at midnight
    }

    @Test
    void testFromEntity_NullSafety_NullEntity() {
        // Act
        OrderedPlanResponseDTO dto = OrderedPlanResponseDTO.fromEntity(null);
        // Assert
        assertNull(dto);
    }

    @Test
    void testFromEntity_NullSafety_NullClaimsList() {
        // Arrange
        testOrderedPlan.setClaims(null); // Set nested list to null

        // Act
        OrderedPlanResponseDTO dto = OrderedPlanResponseDTO.fromEntity(testOrderedPlan);

        // Assert
        assertNotNull(dto);
        // Mapper should initialize an empty list
        assertNotNull(dto.getClaims()); 
        assertTrue(dto.getClaims().isEmpty());
        
        // Calculated fields should have default values
        assertEquals(0, dto.getClaimsCount());
        assertEquals(-1, dto.getDaysSinceClaimed());
    }
    
    @Test
    void testFromEntity_NullSafety_EmptyClaimsList() {
        // Arrange
        testOrderedPlan.setClaims(Collections.emptyList()); // Set to empty list

        // Act
        OrderedPlanResponseDTO dto = OrderedPlanResponseDTO.fromEntity(testOrderedPlan);

        // Assert
        assertNotNull(dto);
        assertNotNull(dto.getClaims());
        assertTrue(dto.getClaims().isEmpty());
        assertEquals(0, dto.getClaimsCount());
        assertEquals(-1, dto.getDaysSinceClaimed());
    }

    @Test
    void testFromEntity_NullSafety_NullInsurancePlan() {
        // Arrange
        testOrderedPlan.setInsurancePlan(null); // Set nested object to null

        // Act
        OrderedPlanResponseDTO dto = OrderedPlanResponseDTO.fromEntity(testOrderedPlan);

        // Assert
        assertNotNull(dto);
        assertNull(dto.getInsurancePlan());
    }
    
    // --- Tests for Nested DTOs ---

    @Test
    void testInsurancePlanSummaryDTO_fromEntity_Success() {
        // Act
        OrderedPlanResponseDTO.InsurancePlanSummaryDTO summaryDTO =
                OrderedPlanResponseDTO.InsurancePlanSummaryDTO.fromEntity(testInsurancePlan);
        
        // Assert
        assertNotNull(summaryDTO);
        assertEquals("INS1", summaryDTO.getId());
        assertEquals("Test Plan", summaryDTO.getPlanName());
        assertEquals("PROV1", summaryDTO.getProviderId());
        assertEquals(1000, summaryDTO.getPrice());
        assertEquals(5000, summaryDTO.getCoverage());
    }

    @Test
    void testInsurancePlanSummaryDTO_fromEntity_Null() {
        // Act
        OrderedPlanResponseDTO.InsurancePlanSummaryDTO summaryDTO =
                OrderedPlanResponseDTO.InsurancePlanSummaryDTO.fromEntity(null);
        // Assert
        assertNull(summaryDTO);
    }

    @Test
    void testClaimSummaryDTO_fromEntity_Success() {
        // Act
        OrderedPlanResponseDTO.ClaimSummaryDTO summaryDTO =
                OrderedPlanResponseDTO.ClaimSummaryDTO.fromEntity(testClaim2);
        
        // Assert
        assertNotNull(summaryDTO);
        assertEquals("CLM2", summaryDTO.getId());
        assertEquals(ClaimStatusEnum.REJECTED, summaryDTO.getStatus());
        assertEquals(testClaim2.getCreatedAt(), summaryDTO.getCreatedAt());
        assertEquals("Bad", summaryDTO.getRejectionReason());
        assertNull(summaryDTO.getAcceptedNote());
    }
    
    @Test
    void testClaimSummaryDTO_fromEntity_Null() {
        // Act
        OrderedPlanResponseDTO.ClaimSummaryDTO summaryDTO =
                OrderedPlanResponseDTO.ClaimSummaryDTO.fromEntity(null);
        // Assert
        assertNull(summaryDTO);
    }
}