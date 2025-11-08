package apap.ti._5.Insurance_2306226864_be.model;

import apap.ti._5.Insurance_2306226864_be.enums.OrderedPlanStatusEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderedPlanTest {
    
    @Test
    void testCreateOrderedPlan_AllFields_Success() {
        // Given
        OrderedPlan orderedPlan = new OrderedPlan();
        orderedPlan.setId("OP1");
        orderedPlan.setStatus(OrderedPlanStatusEnum.ORDERED);
        orderedPlan.setExpiredDate(LocalDate.now().plusDays(30));
        
        InsurancePlan insurancePlan = new InsurancePlan();
        insurancePlan.setId("INS1");
        orderedPlan.setInsurancePlan(insurancePlan);
        
        Policy policy = new Policy();
        policy.setId("POL1");
        orderedPlan.setPolicy(policy);
        
        // When
        orderedPlan.onCreate();
        
        // Then
        assertEquals("OP1", orderedPlan.getId());
        assertEquals(OrderedPlanStatusEnum.ORDERED, orderedPlan.getStatus());
        assertNotNull(orderedPlan.getExpiredDate());
        assertEquals("INS1", orderedPlan.getInsurancePlan().getId());
        assertEquals("POL1", orderedPlan.getPolicy().getId());
        assertNotNull(orderedPlan.getCreatedAt());
        assertNotNull(orderedPlan.getUpdatedAt());
    }
    
    @Test
    void testOrderedPlan_StatusTransitions() {
        // Given
        OrderedPlan orderedPlan = new OrderedPlan();
        orderedPlan.setStatus(OrderedPlanStatusEnum.ORDERED);
        
        // When & Then - ORDERED to PAID
        orderedPlan.setStatus(OrderedPlanStatusEnum.PAID);
        assertEquals(OrderedPlanStatusEnum.PAID, orderedPlan.getStatus());
        
        // PAID to WAITING_FOR_REVIEW
        orderedPlan.setStatus(OrderedPlanStatusEnum.WAITING_FOR_REVIEW);
        assertEquals(OrderedPlanStatusEnum.WAITING_FOR_REVIEW, orderedPlan.getStatus());
        
        // WAITING_FOR_REVIEW to CLAIMED
        orderedPlan.setStatus(OrderedPlanStatusEnum.CLAIMED);
        assertEquals(OrderedPlanStatusEnum.CLAIMED, orderedPlan.getStatus());
    }
    
    @Test
    void testOrderedPlan_ClaimsRelation() {
        // Given
        OrderedPlan orderedPlan = new OrderedPlan();
        List<Claim> claims = new ArrayList<>();
        
        Claim claim1 = new Claim();
        claim1.setId("CLM1");
        claims.add(claim1);
        
        Claim claim2 = new Claim();
        claim2.setId("CLM2");
        claims.add(claim2);
        
        // When
        orderedPlan.setClaims(claims);
        
        // Then
        assertNotNull(orderedPlan.getClaims());
        assertEquals(2, orderedPlan.getClaims().size());
    }
    
    @Test
    void testOrderedPlan_ExpiredDateCalculation() {
        // Given
        LocalDate startDate = LocalDate.now();
        int expiredByDays = 30;
        
        // When
        OrderedPlan orderedPlan = new OrderedPlan();
        orderedPlan.setExpiredDate(startDate.plusDays(expiredByDays));
        
        // Then
        assertEquals(startDate.plusDays(30), orderedPlan.getExpiredDate());
    }
}