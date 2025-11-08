package apap.ti._5.Insurance_2306226864_be.service;

import apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.OrderedPlanStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.PolicyStatusEnum;
import apap.ti._5.Insurance_2306226864_be.model.Claim;
import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
import apap.ti._5.Insurance_2306226864_be.model.Policy;
import apap.ti._5.Insurance_2306226864_be.repository.ClaimRepository;
import apap.ti._5.Insurance_2306226864_be.repository.OrderedPlanRepository;
import apap.ti._5.Insurance_2306226864_be.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderedPlanServiceImpl.
 * Uses @ExtendWith(MockitoExtension.class) to enable Mockito annotations.
 */
@ExtendWith(MockitoExtension.class)
class OrderedPlanServiceImplTest {

    @Mock
    private OrderedPlanRepository orderedPlanRepository;

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private ClaimRepository claimRepository; // Mocked, though not directly used by this service

    @InjectMocks
    private OrderedPlanServiceImpl orderedPlanService;

    private OrderedPlan testOrderedPlan;
    private Policy testPolicy;

    @BeforeEach
    void setUp() {
        // 1. Setup a test Policy
        testPolicy = new Policy();
        testPolicy.setId("POL1");
        testPolicy.setStatus(PolicyStatusEnum.PAID);
        testPolicy.setOrderedPlans(new ArrayList<>()); // Initialize list

        // 2. Setup a test OrderedPlan
        testOrderedPlan = new OrderedPlan();
        testOrderedPlan.setId("OP1");
        testOrderedPlan.setStatus(OrderedPlanStatusEnum.PAID);
        testOrderedPlan.setExpiredDate(LocalDate.now().plusDays(30));
        testOrderedPlan.setPolicy(testPolicy);
        testOrderedPlan.setClaims(new ArrayList<>()); // Initialize list

        // 3. Link them together
        testPolicy.getOrderedPlans().add(testOrderedPlan);
    }

    // ========================== GET BY ID ==========================

    @Test
    void testGetOrderedPlanById_Success() {
        // Arrange
        when(orderedPlanRepository.findById("OP1")).thenReturn(Optional.of(testOrderedPlan));

        // Act
        OrderedPlan result = orderedPlanService.getOrderedPlanById("OP1");

        // Assert
        assertNotNull(result);
        assertEquals("OP1", result.getId());
        verify(orderedPlanRepository, times(1)).findById("OP1");
    }

    @Test
    void testGetOrderedPlanById_NotFound() {
        // Arrange
        when(orderedPlanRepository.findById("OP999")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderedPlanService.getOrderedPlanById("OP999");
        });
        assertTrue(exception.getMessage().contains("OrderedPlan with ID OP999 not found"));
        verify(orderedPlanRepository, times(1)).findById("OP999");
    }

    // ========================== GET BY POLICY ID ==========================

    @Test
    void testGetOrderedPlansByPolicyId_Success() {
        // Arrange
        when(orderedPlanRepository.findByPolicy_Id("POL1")).thenReturn(Arrays.asList(testOrderedPlan));

        // Act
        List<OrderedPlan> result = orderedPlanService.getOrderedPlansByPolicyId("POL1");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("OP1", result.get(0).getId());
        verify(orderedPlanRepository, times(1)).findByPolicy_Id("POL1");
    }

    // ========================== UPDATE STATUS (Complex Logic) ==========================

    @Test
    void testUpdateOrderedPlanStatus_Fail_InvalidStatus() {
        // Arrange
        when(orderedPlanRepository.findById("OP1")).thenReturn(Optional.of(testOrderedPlan));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderedPlanService.updateOrderedPlanStatus("OP1", "INVALID_STATUS_STRING");
        });
        assertTrue(exception.getMessage().contains("Invalid status: INVALID_STATUS_STRING"));
        verify(orderedPlanRepository, never()).save(any(OrderedPlan.class));
    }

    @Test
    void testUpdateOrderedPlanStatus_Logic_OneAcceptedClaim() {
        // Arrange
        Claim acceptedClaim = new Claim();
        acceptedClaim.setStatus(ClaimStatusEnum.ACCEPTED);
        testOrderedPlan.getClaims().add(acceptedClaim); // Add accepted claim

        when(orderedPlanRepository.findById("OP1")).thenReturn(Optional.of(testOrderedPlan));
        when(orderedPlanRepository.save(any(OrderedPlan.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        // We pass "PAID", but the internal logic should override it to "CLAIMED"
        OrderedPlan result = orderedPlanService.updateOrderedPlanStatus("OP1", "PAID"); 

        // Assert
        // 1. Verify OrderedPlan status became CLAIMED
        assertEquals(OrderedPlanStatusEnum.CLAIMED, result.getStatus());

        // 2. Verify (private) updatePolicyStatus was called and worked
        // Since it's the only OP in the policy, policy status should be FULLY_CLAIMED
        assertEquals(PolicyStatusEnum.FULLY_CLAIMED, testPolicy.getStatus());
        verify(policyRepository, times(1)).save(testPolicy);
    }

    @Test
    void testUpdateOrderedPlanStatus_Logic_ThreeRejectedClaims() {
        // Arrange
        Claim r1 = new Claim(); r1.setStatus(ClaimStatusEnum.REJECTED);
        Claim r2 = new Claim(); r2.setStatus(ClaimStatusEnum.REJECTED);
        Claim r3 = new Claim(); r3.setStatus(ClaimStatusEnum.REJECTED);
        testOrderedPlan.getClaims().addAll(Arrays.asList(r1, r2, r3)); // Add 3 rejected claims

        when(orderedPlanRepository.findById("OP1")).thenReturn(Optional.of(testOrderedPlan));
        when(orderedPlanRepository.save(any(OrderedPlan.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        // We pass "PAID", but the internal logic should override it to "REJECTED"
        OrderedPlan result = orderedPlanService.updateOrderedPlanStatus("OP1", "PAID");

        // Assert
        // 1. Verify OrderedPlan status became REJECTED
        assertEquals(OrderedPlanStatusEnum.REJECTED, result.getStatus());

        // 2. Verify updatePolicyStatus was called, but did NOT change policy status
        // (REJECTED status doesn't trigger PARTIALLY or FULLY claimed)
        assertEquals(PolicyStatusEnum.PAID, testPolicy.getStatus()); // Stays as PAID
        verify(policyRepository, never()).save(testPolicy); // Save is not called
    }
    
    @Test
    void testUpdateOrderedPlanStatus_Logic_AcceptedTrumpsRejected() {
        // Arrange: Add 3 rejected AND 1 accepted claim
        Claim r1 = new Claim(); r1.setStatus(ClaimStatusEnum.REJECTED);
        Claim r2 = new Claim(); r2.setStatus(ClaimStatusEnum.REJECTED);
        Claim r3 = new Claim(); r3.setStatus(ClaimStatusEnum.REJECTED);
        Claim a1 = new Claim(); a1.setStatus(ClaimStatusEnum.ACCEPTED);
        testOrderedPlan.getClaims().addAll(Arrays.asList(r1, r2, r3, a1));

        when(orderedPlanRepository.findById("OP1")).thenReturn(Optional.of(testOrderedPlan));
        when(orderedPlanRepository.save(any(OrderedPlan.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        // Logic checks REJECTED first, then ACCEPTED. Accepted should win.
        OrderedPlan result = orderedPlanService.updateOrderedPlanStatus("OP1", "PAID");

        // Assert
        // 1. Verify OrderedPlan status became CLAIMED (not REJECTED)
        assertEquals(OrderedPlanStatusEnum.CLAIMED, result.getStatus());

        // 2. Verify Policy status became FULLY_CLAIMED
        assertEquals(PolicyStatusEnum.FULLY_CLAIMED, testPolicy.getStatus());
        verify(policyRepository, times(1)).save(testPolicy);
    }

    @Test
    void testUpdateOrderedPlanStatus_Logic_PartiallyClaimedPolicy() {
        // Arrange
        // Add a second, non-claimed plan to the policy
        OrderedPlan op2_paid = new OrderedPlan();
        op2_paid.setId("OP2");
        op2_paid.setStatus(OrderedPlanStatusEnum.PAID);
        testPolicy.getOrderedPlans().add(op2_paid); // Policy now has [OP1, OP2]

        // Add an accepted claim to OP1
        Claim acceptedClaim = new Claim();
        acceptedClaim.setStatus(ClaimStatusEnum.ACCEPTED);
        testOrderedPlan.getClaims().add(acceptedClaim); 

        when(orderedPlanRepository.findById("OP1")).thenReturn(Optional.of(testOrderedPlan));
        when(orderedPlanRepository.save(any(OrderedPlan.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act: Update OP1
        OrderedPlan result = orderedPlanService.updateOrderedPlanStatus("OP1", "PAID"); 

        // Assert
        // 1. Verify OP1 status became CLAIMED
        assertEquals(OrderedPlanStatusEnum.CLAIMED, result.getStatus());

        // 2. Verify Policy status became PARTIALLY_CLAIMED
        // (1 claimed, 1 not claimed)
        assertEquals(PolicyStatusEnum.PARTIALLY_CLAIMED, testPolicy.getStatus());
        verify(policyRepository, times(1)).save(testPolicy);
    }

    @Test
    void testUpdateOrderedPlanStatus_Logic_NoClaims() {
        // Arrange
        // testOrderedPlan has no claims by default
        assertEquals(0, testOrderedPlan.getClaims().size());

        when(orderedPlanRepository.findById("OP1")).thenReturn(Optional.of(testOrderedPlan));
        when(orderedPlanRepository.save(any(OrderedPlan.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        OrderedPlan result = orderedPlanService.updateOrderedPlanStatus("OP1", "PAID"); 

        // Assert
        // 1. Verify OrderedPlan status is just set to what was passed
        assertEquals(OrderedPlanStatusEnum.PAID, result.getStatus());

        // 2. Verify Policy status remains unchanged
        assertEquals(PolicyStatusEnum.PAID, testPolicy.getStatus());
        verify(policyRepository, never()).save(testPolicy);
    }
}