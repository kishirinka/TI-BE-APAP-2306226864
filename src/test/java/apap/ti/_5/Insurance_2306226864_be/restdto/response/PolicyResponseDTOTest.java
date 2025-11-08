package apap.ti._5.Insurance_2306226864_be.restdto.response.policy;

import apap.ti._5.Insurance_2306226864_be.enums.PolicyStatusEnum;
import apap.ti._5.Insurance_2306226864_be.model.InsurancePlan;
import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
import apap.ti._5.Insurance_2306226864_be.model.Policy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PolicyResponseDTOTest {

    private Policy testPolicy;
    private OrderedPlan op1, op2;

    @BeforeEach
    void setUp() {
        // 1. Setup OrderedPlans (with minimal data for nested mapping)
        InsurancePlan dummyPlan = new InsurancePlan();
        
        op1 = new OrderedPlan();
        op1.setId("OP1");
        op1.setInsurancePlan(dummyPlan); // To avoid NPE in OrderedPlanResponseDTO
        op1.setClaims(Collections.emptyList()); // To avoid NPE
        op1.setExpiredDate(LocalDate.now().plusDays(10));
        
        op2 = new OrderedPlan();
        op2.setId("OP2");
        op2.setInsurancePlan(dummyPlan);
        op2.setClaims(Collections.emptyList());
        op2.setExpiredDate(LocalDate.now().plusDays(20));
        
        List<OrderedPlan> plans = new ArrayList<>(Arrays.asList(op1, op2));

        // 2. Setup main Policy
        testPolicy = new Policy();
        testPolicy.setId("POL1");
        testPolicy.setBookingId("BOOK123");
        testPolicy.setUserId("user1");
        testPolicy.setStatus(PolicyStatusEnum.PAID);
        testPolicy.setTotalPrice(200000);
        testPolicy.setOrderedPlans(plans);
    }

    @Test
    void testFromEntity_Success_WithList() {
        // Act
        PolicyResponseDTO dto = PolicyResponseDTO.fromEntity(testPolicy);

        // Assert (Top-level fields)
        assertNotNull(dto);
        assertEquals("POL1", dto.getId());
        assertEquals("BOOK123", dto.getBookingId());
        assertEquals("user1", dto.getUserId());
        assertEquals(PolicyStatusEnum.PAID, dto.getStatus());
        assertEquals(200000, dto.getTotalPrice());
        
        // Assert (Nested list)
        assertNotNull(dto.getOrderedPlans());
        assertEquals(2, dto.getOrderedPlans().size());
        assertEquals("OP1", dto.getOrderedPlans().get(0).getId());
        assertEquals("OP2", dto.getOrderedPlans().get(1).getId());
    }

    @Test
    void testFromEntity_NullSafety_NullPolicy() {
        // Act
        PolicyResponseDTO dto = PolicyResponseDTO.fromEntity(null);
        
        // Assert
        assertNull(dto);
    }

    @Test
    void testFromEntity_NullSafety_NullOrderedPlanList() {
        // Arrange
        testPolicy.setOrderedPlans(null); // Set nested list to null

        // Act
        PolicyResponseDTO dto = PolicyResponseDTO.fromEntity(testPolicy);

        // Assert
        assertNotNull(dto);
        // Verify mapper initializes an empty list instead of null
        assertNotNull(dto.getOrderedPlans());
        assertTrue(dto.getOrderedPlans().isEmpty());
    }

    @Test
    void testFromEntity_Success_EmptyOrderedPlanList() {
        // Arrange
        testPolicy.setOrderedPlans(Collections.emptyList());

        // Act
        PolicyResponseDTO dto = PolicyResponseDTO.fromEntity(testPolicy);

        // Assert
        assertNotNull(dto);
        assertNotNull(dto.getOrderedPlans());
        assertTrue(dto.getOrderedPlans().isEmpty());
    }
}