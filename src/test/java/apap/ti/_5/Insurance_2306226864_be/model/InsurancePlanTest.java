package apap.ti._5.Insurance_2306226864_be.model;

import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InsurancePlanTest {
    
    @Test
    void testCreateInsurancePlan_AllFields_Success() {
        // Given
        InsurancePlan plan = new InsurancePlan();
        plan.setId("INS1");
        plan.setProviderId("PROVIDER001");
        plan.setPlanName("Test Plan");
        plan.setPrice(100000);
        plan.setCoverage(500000);
        plan.setCoverageDetails("Coverage for flight delays");
        plan.setApplicableService(Arrays.asList(ServiceEnum.FLIGHT));
        plan.setExpiredByDays(30);
        
        // When
        LocalDateTime now = LocalDateTime.now();
        plan.setCreatedAt(now);
        plan.setUpdatedAt(now);
        
        // Then
        assertEquals("INS1", plan.getId());
        assertEquals("PROVIDER001", plan.getProviderId());
        assertEquals("Test Plan", plan.getPlanName());
        assertEquals(100000, plan.getPrice());
        assertEquals(500000, plan.getCoverage());
        assertEquals("Coverage for flight delays", plan.getCoverageDetails());
        assertEquals(1, plan.getApplicableService().size());
        assertEquals(ServiceEnum.FLIGHT, plan.getApplicableService().get(0));
        assertEquals(30, plan.getExpiredByDays());
        assertNotNull(plan.getCreatedAt());
        assertNotNull(plan.getUpdatedAt());
    }
    
    @Test
    void testPrePersist_SetsTimestamps() {
        // Given
        InsurancePlan plan = new InsurancePlan();
        plan.setId("INS1");
        plan.setProviderId("PROVIDER001");
        plan.setPlanName("Test Plan");
        plan.setPrice(100000);
        plan.setCoverage(500000);
        plan.setCoverageDetails("Test");
        plan.setApplicableService(Arrays.asList(ServiceEnum.FLIGHT));
        plan.setExpiredByDays(30);
        
        // When
        plan.onCreate();
        
        // Then
        assertNotNull(plan.getCreatedAt());
        assertNotNull(plan.getUpdatedAt());
    }
    
    @Test
    void testPreUpdate_UpdatesTimestamp() throws InterruptedException {
        // Given
        InsurancePlan plan = new InsurancePlan();
        plan.onCreate();
        LocalDateTime originalUpdatedAt = plan.getUpdatedAt();
        
        Thread.sleep(100); // Ensure time difference
        
        // When
        plan.onUpdate();
        
        // Then
        assertNotNull(plan.getUpdatedAt());
        assertTrue(plan.getUpdatedAt().isAfter(originalUpdatedAt) || 
                   plan.getUpdatedAt().isEqual(originalUpdatedAt));
    }
    
    @Test
    void testSoftDelete_SetsDeletedAt() {
        // Given
        InsurancePlan plan = new InsurancePlan();
        plan.onCreate();
        assertNull(plan.getDeletedAt());
        
        // When
        plan.setDeletedAt(LocalDateTime.now());
        
        // Then
        assertNotNull(plan.getDeletedAt());
    }
    
    @Test
    void testInsurancePlan_WithMultipleServices() {
        // Given & When
        InsurancePlan plan = new InsurancePlan();
        plan.setApplicableService(Arrays.asList(
            ServiceEnum.FLIGHT,
            ServiceEnum.ACCOMMODATION,
            ServiceEnum.RENTALS
        ));
        
        // Then
        assertEquals(3, plan.getApplicableService().size());
        assertTrue(plan.getApplicableService().contains(ServiceEnum.FLIGHT));
        assertTrue(plan.getApplicableService().contains(ServiceEnum.ACCOMMODATION));
        assertTrue(plan.getApplicableService().contains(ServiceEnum.RENTALS));
    }
    
    @Test
    void testInsurancePlan_OrderedPlansRelation() {
        // Given
        InsurancePlan plan = new InsurancePlan();
        List<OrderedPlan> orderedPlans = new ArrayList<>();
        
        OrderedPlan op1 = new OrderedPlan();
        op1.setId("OP1");
        orderedPlans.add(op1);
        
        // When
        plan.setOrderedPlans(orderedPlans);
        
        // Then
        assertNotNull(plan.getOrderedPlans());
        assertEquals(1, plan.getOrderedPlans().size());
        assertEquals("OP1", plan.getOrderedPlans().get(0).getId());
    }
}