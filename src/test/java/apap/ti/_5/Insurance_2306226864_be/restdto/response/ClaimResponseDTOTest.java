package apap.ti._5.Insurance_2306226864_be.restdto.response.claim;

import apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.OrderedPlanStatusEnum;
import apap.ti._5.Insurance_2306226864_be.model.Claim;
import apap.ti._5.Insurance_2306226864_be.model.InsurancePlan;
import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ClaimResponseDTOTest {

    private Claim testClaim;
    private OrderedPlan testOrderedPlan;
    private InsurancePlan testInsurancePlan;

    @BeforeEach
    void setUp() {
        // 1. Setup nested InsurancePlan
        testInsurancePlan = new InsurancePlan();
        testInsurancePlan.setId("INS1");
        testInsurancePlan.setPlanName("Health Plus");

        // 2. Setup nested OrderedPlan
        testOrderedPlan = new OrderedPlan();
        testOrderedPlan.setId("OP1");
        testOrderedPlan.setStatus(OrderedPlanStatusEnum.WAITING_FOR_REVIEW);
        testOrderedPlan.setInsurancePlan(testInsurancePlan);

        // 3. Setup main Claim object
        testClaim = new Claim();
        testClaim.setId("CLM1");
        testClaim.setStatus(ClaimStatusEnum.WAITING_FOR_REVIEW);
        testClaim.setProof("proof.jpg");
        testClaim.setAcceptedNote("Note");
        testClaim.setRejectionReason("Reason");
        // Set created at to 2 days ago
        testClaim.setCreatedAt(LocalDateTime.now().minusDays(2));
        testClaim.setOrderedPlan(testOrderedPlan);
    }

    @Test
    void testFromEntity_Success_WaitingForReview() {
        // Act
        ClaimResponseDTO dto = ClaimResponseDTO.fromEntity(testClaim);

        // Assert
        assertNotNull(dto);
        assertEquals("CLM1", dto.getId());
        assertEquals(ClaimStatusEnum.WAITING_FOR_REVIEW, dto.getStatus());
        assertEquals("proof.jpg", dto.getProof());
        assertEquals("Note", dto.getAcceptedNote());
        assertEquals("Reason", dto.getRejectionReason());

        // Assert nested OrderedPlanSummaryDTO
        assertNotNull(dto.getOrderedPlan());
        assertEquals("OP1", dto.getOrderedPlan().getId());
        assertEquals(OrderedPlanStatusEnum.WAITING_FOR_REVIEW, dto.getOrderedPlan().getStatus());
        
        // Assert nested plan name (denormalized)
        assertEquals("Health Plus", dto.getInsurancePlanName());
        assertEquals("Health Plus", dto.getOrderedPlan().getInsurancePlanName());

        // Assert conditional logic: daysSinceClaimed
        // Should be 2 days (or 1 if run just before midnight, hence >= 1)
        assertNotNull(dto.getDaysSinceClaimed());
        assertTrue(dto.getDaysSinceClaimed() >= 1);
    }

    @Test
    void testFromEntity_Success_AcceptedStatus() {
        // Arrange
        testClaim.setStatus(ClaimStatusEnum.ACCEPTED);

        // Act
        ClaimResponseDTO dto = ClaimResponseDTO.fromEntity(testClaim);

        // Assert
        assertEquals(ClaimStatusEnum.ACCEPTED, dto.getStatus());
        
        // Assert conditional logic: daysSinceClaimed should be null
        // because status is not WAITING_FOR_REVIEW
        assertNull(dto.getDaysSinceClaimed());
    }

    @Test
    void testFromEntity_NullSafety_NullClaim() {
        // Act
        ClaimResponseDTO dto = ClaimResponseDTO.fromEntity(null);
        
        // Assert
        assertNull(dto);
    }

    @Test
    void testFromEntity_NullSafety_NullOrderedPlan() {
        // Arrange
        testClaim.setOrderedPlan(null); // Set nested object to null

        // Act
        ClaimResponseDTO dto = ClaimResponseDTO.fromEntity(testClaim);

        // Assert
        assertNotNull(dto);
        // Assert nested DTO and fields are null
        assertNull(dto.getOrderedPlan());
        assertNull(dto.getInsurancePlanName());
    }
    
    @Test
    void testFromEntity_NullSafety_NullInsurancePlan() {
        // Arrange
        testOrderedPlan.setInsurancePlan(null); // Set deeply nested object to null
        
        // Act
        ClaimResponseDTO dto = ClaimResponseDTO.fromEntity(testClaim);
        
        // Assert
        assertNotNull(dto);
        assertNotNull(dto.getOrderedPlan());
        
        // Assert nested names are null
        assertNull(dto.getInsurancePlanName());
        assertNull(dto.getOrderedPlan().getInsurancePlanName());
    }
}