package apap.ti._5.Insurance_2306226864_be.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.OrderedPlanStatusEnum;
import apap.ti._5.Insurance_2306226864_be.model.Claim;
import apap.ti._5.Insurance_2306226864_be.model.InsurancePlan;
import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
import apap.ti._5.Insurance_2306226864_be.repository.ClaimRepository;
import apap.ti._5.Insurance_2306226864_be.repository.OrderedPlanRepository;

@ExtendWith(MockitoExtension.class)
class ClaimServiceImplTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private OrderedPlanRepository orderedPlanRepository;

    @Mock
    private OrderedPlanService orderedPlanService;

    @InjectMocks
    private ClaimServiceImpl claimService;

    private Claim testClaim;
    private OrderedPlan testOrderedPlan;
    private InsurancePlan testInsurancePlan;

    @BeforeEach
    void setUp() {
        testInsurancePlan = new InsurancePlan();
        testInsurancePlan.setId("INS1");
        testInsurancePlan.setPlanName("Test Plan");

        testOrderedPlan = new OrderedPlan();
        testOrderedPlan.setId("OP1");
        testOrderedPlan.setStatus(OrderedPlanStatusEnum.PAID);
        testOrderedPlan.setExpiredDate(LocalDate.now().plusDays(30));
        testOrderedPlan.setInsurancePlan(testInsurancePlan);

        testClaim = new Claim();
        testClaim.setId("OP1-CLAIM1");
        testClaim.setOrderedPlan(testOrderedPlan);
        testClaim.setStatus(ClaimStatusEnum.WAITING_FOR_REVIEW);
    }

    // ========================== TEST GET ALL / BY STATUS ==========================
    @Test
    void testGetAllClaims_Success() {
        // Arrange
        when(claimRepository.findAll()).thenReturn(Arrays.asList(testClaim));

        // Act
        List<Claim> result = claimService.getAllClaims();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(claimRepository, times(1)).findAll();
    }

    @Test
    void testGetClaimsByStatus_Success() {
        // Arrange
        testClaim.setStatus(ClaimStatusEnum.ACCEPTED);
        when(claimRepository.findByStatus(ClaimStatusEnum.ACCEPTED)).thenReturn(Arrays.asList(testClaim));

        // Act
        List<Claim> result = claimService.getClaimsByStatus("ACCEPTED");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ClaimStatusEnum.ACCEPTED, result.get(0).getStatus());
        verify(claimRepository, times(1)).findByStatus(ClaimStatusEnum.ACCEPTED);
    }

    @Test
    void testGetClaimsByStatus_InvalidStatus() {
        // Arrange (No mocks needed, should fail enum parsing)
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            claimService.getClaimsByStatus("INVALID_STATUS");
        });
        assertTrue(exception.getMessage().contains("Invalid claim status"));
        verify(claimRepository, never()).findByStatus(any(ClaimStatusEnum.class));
    }

    // ========================== TEST GET BY FILTERS ==========================
    @Test
    void testGetClaimsByInsurancePlanId_Success() {
        // Arrange
        InsurancePlan plan2 = new InsurancePlan();
        plan2.setId("INS2");
        OrderedPlan op2 = new OrderedPlan();
        op2.setId("OP2");
        op2.setInsurancePlan(plan2);
        Claim claim2 = new Claim();
        claim2.setId("OP2-CLAIM1");
        claim2.setOrderedPlan(op2);

        when(claimRepository.findAll()).thenReturn(Arrays.asList(testClaim, claim2));

        // Act
        List<Claim> result = claimService.getClaimsByInsurancePlanId("INS1");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("OP1-CLAIM1", result.get(0).getId());
    }

    @Test
    void testGetClaimsByFilters_ByStatusAndPlanId_Success() {
        // Arrange
        testClaim.setStatus(ClaimStatusEnum.REJECTED);
        when(claimRepository.findAll()).thenReturn(Arrays.asList(testClaim));

        // Act
        List<Claim> result = claimService.getClaimsByFilters("REJECTED", "INS1");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ClaimStatusEnum.REJECTED, result.get(0).getStatus());
        assertEquals("INS1", result.get(0).getOrderedPlan().getInsurancePlan().getId());
    }
    
    @Test
    void testGetClaimsByFilters_NoFilters() {
        // Arrange
        when(claimRepository.findAll()).thenReturn(Arrays.asList(testClaim));
        // Act
        List<Claim> result = claimService.getClaimsByFilters(null, null);
        // Assert
        assertEquals(1, result.size());
        verify(claimRepository, times(1)).findAll();
    }

    // ========================== TEST GET BY ID ==========================
    @Test
    void testGetClaimById_Success() {
        // Arrange
        when(claimRepository.findById("OP1-CLAIM1")).thenReturn(Optional.of(testClaim));
        
        // Act
        Claim result = claimService.getClaimById("OP1-CLAIM1");
        
        // Assert
        assertNotNull(result);
        assertEquals("OP1-CLAIM1", result.getId());
        verify(claimRepository, times(1)).findById("OP1-CLAIM1");
    }

    @Test
    void testGetClaimById_NotFound() {
        // Arrange
        when(claimRepository.findById("CLM999")).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            claimService.getClaimById("CLM999");
        });
        assertTrue(exception.getMessage().contains("not found"));
        verify(claimRepository, times(1)).findById("CLM999");
    }

    // ========================== TEST CREATE CLAIM ==========================

    @Test
    void testCreateClaim_Fail_OrderedPlanExpired() {
        // Arrange
        testOrderedPlan.setStatus(OrderedPlanStatusEnum.PAID);
        testOrderedPlan.setExpiredDate(LocalDate.now().minusDays(1)); // Expired yesterday
        when(orderedPlanService.getOrderedPlanById("OP1")).thenReturn(testOrderedPlan);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            claimService.createClaim(new Claim(), "OP1");
        });
        assertTrue(exception.getMessage().contains("has expired"));
        verify(claimRepository, never()).save(any(Claim.class));
    }

    // ========================== TEST ACCEPT CLAIM ==========================
    @Test
    void testAcceptClaim_Success() {
        // Arrange
        testClaim.setStatus(ClaimStatusEnum.WAITING_FOR_REVIEW);
        when(claimRepository.findById("OP1-CLAIM1")).thenReturn(Optional.of(testClaim));
        when(claimRepository.save(any(Claim.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Claim result = claimService.acceptClaim("OP1-CLAIM1", "Approved");

        // Assert
        assertNotNull(result);
        assertEquals(ClaimStatusEnum.ACCEPTED, result.getStatus());
        assertEquals("Approved", result.getAcceptedNote());
        assertNotNull(result.getAcceptedTimestamp());
        
        // Verify OrderedPlanService was called to update status
        verify(orderedPlanService, times(1)).updateOrderedPlanStatus("OP1", OrderedPlanStatusEnum.CLAIMED.name());
        verify(claimRepository, times(1)).save(result);
    }

    @Test
    void testAcceptClaim_Fail_NotWaitingForReview() {
        // Arrange
        testClaim.setStatus(ClaimStatusEnum.ACCEPTED); // Already accepted
        when(claimRepository.findById("OP1-CLAIM1")).thenReturn(Optional.of(testClaim));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            claimService.acceptClaim("OP1-CLAIM1", "Approved");
        });
        assertTrue(exception.getMessage().contains("must be in WAITING_FOR_REVIEW status"));
        verify(claimRepository, never()).save(any(Claim.class));
    }

    // ========================== TEST REJECT CLAIM ==========================
    @Test
    void testRejectClaim_Success_FirstRejection() {
        // Arrange
        testClaim.setStatus(ClaimStatusEnum.WAITING_FOR_REVIEW);
        when(claimRepository.findById("OP1-CLAIM1")).thenReturn(Optional.of(testClaim));
        when(claimRepository.save(any(Claim.class))).thenAnswer(inv -> inv.getArgument(0));
        
        // Mock findByOrderedPlanIdAndStatus to return 0 rejected claims (it will be 1 after save)
        when(claimRepository.findByOrderedPlanIdAndStatus("OP1", ClaimStatusEnum.REJECTED))
                .thenReturn(new ArrayList<>()); // Empty list

        // Act
        Claim result = claimService.rejectClaim("OP1-CLAIM1", "Reason", "Desc");

        // Assert
        assertNotNull(result);
        assertEquals(ClaimStatusEnum.REJECTED, result.getStatus());
        assertEquals("Reason", result.getRejectionReason());
        assertNotNull(result.getRejectionTimestamp());
        
        // Verify OrderedPlan status reset to PAID
        assertEquals(OrderedPlanStatusEnum.PAID, testOrderedPlan.getStatus());
        verify(orderedPlanRepository, times(1)).save(testOrderedPlan);
        verify(orderedPlanService, never()).updateOrderedPlanStatus(anyString(), anyString());
    }

    @Test
    void testRejectClaim_Success_ThirdRejection() {
        // Arrange
        testClaim.setStatus(ClaimStatusEnum.WAITING_FOR_REVIEW);
        
        // Simulate 2 already rejected claims
        Claim rejected1 = new Claim();
        Claim rejected2 = new Claim();
        List<Claim> existingRejected = Arrays.asList(rejected1, rejected2);
        
        when(claimRepository.findById("OP1-CLAIM1")).thenReturn(Optional.of(testClaim));
        
        // When save is called, return the claim (now rejected)
        when(claimRepository.save(any(Claim.class))).thenAnswer(inv -> {
            Claim savedClaim = inv.getArgument(0);
            savedClaim.setStatus(ClaimStatusEnum.REJECTED); // Simulate the save
            return savedClaim;
        });

        // When findBy... is called AFTER save, it should return 3 claims
        when(claimRepository.findByOrderedPlanIdAndStatus("OP1", ClaimStatusEnum.REJECTED))
                .thenReturn(Arrays.asList(rejected1, rejected2, testClaim)); // Total is now 3

        // Act
        Claim result = claimService.rejectClaim("OP1-CLAIM1", "Reason", "Desc");

        // Assert
        assertNotNull(result);
        assertEquals(ClaimStatusEnum.REJECTED, result.getStatus());
        
        // Verify OrderedPlan status is set to REJECTED by the service
        verify(orderedPlanService, times(1)).updateOrderedPlanStatus("OP1", OrderedPlanStatusEnum.REJECTED.name());
        verify(orderedPlanRepository, never()).save(testOrderedPlan); // orderedPlanService handles the save
    }

    // ========================== TEST COUNT ==========================
    @Test
    void testCountAllClaims() {
        // Arrange
        when(claimRepository.count()).thenReturn(10L);

        // Act
        int result = claimService.countAllClaims();

        // Assert
        assertEquals(10, result);
        verify(claimRepository, times(1)).count();
    }
}