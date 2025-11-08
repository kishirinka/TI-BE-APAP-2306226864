package apap.ti._5.Insurance_2306226864_be.model;

import apap.ti._5.Insurance_2306226864_be.enums.PolicyStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PolicyTest {
    
    @Test
    void testCreatePolicy_AllFields_Success() {
        // Given & When
        Policy policy = new Policy();
        policy.setId("POL1");
        policy.setBookingId("BOOK001");
        policy.setUserId("USER001");
        policy.setStartDate(LocalDate.now());
        policy.setStatus(PolicyStatusEnum.CREATED);
        policy.setService(ServiceEnum.FLIGHT);
        policy.setTotalPrice(200000);
        policy.setTotalCoverage(1000000);
        policy.onCreate();
        
        // Then
        assertEquals("POL1", policy.getId());
        assertEquals("BOOK001", policy.getBookingId());
        assertEquals("USER001", policy.getUserId());
        assertNotNull(policy.getStartDate());
        assertEquals(PolicyStatusEnum.CREATED, policy.getStatus());
        assertEquals(ServiceEnum.FLIGHT, policy.getService());
        assertEquals(200000, policy.getTotalPrice());
        assertEquals(1000000, policy.getTotalCoverage());
        assertNotNull(policy.getCreatedAt());
        assertNotNull(policy.getUpdatedAt());
    }
    
    @Test
    void testPolicy_StatusTransitions() {
        // Given
        Policy policy = new Policy();
        policy.setStatus(PolicyStatusEnum.CREATED);
        
        // When & Then
        policy.setStatus(PolicyStatusEnum.PAID);
        assertEquals(PolicyStatusEnum.PAID, policy.getStatus());
        
        policy.setStatus(PolicyStatusEnum.PARTIALLY_CLAIMED);
        assertEquals(PolicyStatusEnum.PARTIALLY_CLAIMED, policy.getStatus());
        
        policy.setStatus(PolicyStatusEnum.FULLY_CLAIMED);
        assertEquals(PolicyStatusEnum.FULLY_CLAIMED, policy.getStatus());
    }
    
    @Test
    void testPolicy_OrderedPlansRelation() {
        // Given
        Policy policy = new Policy();
        List<OrderedPlan> orderedPlans = new ArrayList<>();
        
        OrderedPlan op1 = new OrderedPlan();
        op1.setId("OP1");
        orderedPlans.add(op1);
        
        OrderedPlan op2 = new OrderedPlan();
        op2.setId("OP2");
        orderedPlans.add(op2);
        
        // When
        policy.setOrderedPlans(orderedPlans);
        
        // Then
        assertNotNull(policy.getOrderedPlans());
        assertEquals(2, policy.getOrderedPlans().size());
    }
    
    @Test
    void testPolicy_AllServiceTypes() {
        // Test all service enums
        for (ServiceEnum service : ServiceEnum.values()) {
            Policy policy = new Policy();
            policy.setService(service);
            assertEquals(service, policy.getService());
        }
    }
}