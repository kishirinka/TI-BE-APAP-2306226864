package apap.ti._5.Insurance_2306226864_be.service;

import apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.OrderedPlanStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import apap.ti._5.Insurance_2306226864_be.model.Claim;
import apap.ti._5.Insurance_2306226864_be.model.InsurancePlan;
import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
import apap.ti._5.Insurance_2306226864_be.model.Policy;
import apap.ti._5.Insurance_2306226864_be.repository.ClaimRepository;
import apap.ti._5.Insurance_2306226864_be.repository.OrderedPlanRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

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
    private Policy testPolicy;

    @BeforeEach
    void setUp() {
        // Setup test insurance plan
        testInsurancePlan = new InsurancePlan();
        testInsurancePlan.setId("INS1");
        testInsurancePlan.setPlanName("Health Plus");
        testInsurancePlan.setProviderId("PROV123");
        testInsurancePlan.setPrice(500000);
        testInsurancePlan.setCoverage(10000000);
        testInsurancePlan.setApplicableService(Arrays.asList(ServiceEnum.ACCOMMODATION));
        testInsurancePlan.setExpiredByDays(365);

        // Setup test policy
        testPolicy = new Policy();
        testPolicy.setId("POL1");
        testPolicy.setUserId("USER123");

        // Setup test ordered plan
        testOrderedPlan = new OrderedPlan();
        testOrderedPlan.setId("OP1");
        testOrderedPlan.setPolicy(testPolicy);
        testOrderedPlan.setInsurancePlan(testInsurancePlan);
        testOrderedPlan.setStatus(OrderedPlanStatusEnum.PAID);
        testOrderedPlan.setExpiredDate(LocalDate.now().plusDays(30)); // Not expired yet
        testOrderedPlan.setCreatedAt(LocalDateTime.now());
        testOrderedPlan.setUpdatedAt(LocalDateTime.now());
        testOrderedPlan.setClaims(new ArrayList<>());

        // Setup test claim
        testClaim = new Claim();
        testClaim.setId("CLM1");
        testClaim.setStatus(ClaimStatusEnum.WAITING_FOR_REVIEW);
        testClaim.setProof("proof-document.pdf");
        testClaim.setOrderedPlan(testOrderedPlan);
        testClaim.setCreatedAt(LocalDateTime.now());
        testClaim.setUpdatedAt(LocalDateTime.now());
    }

    // ========================== TEST CREATE CLAIM - FAIL (ORDERED PLAN NOT FOUND) ==========================
    @Test
    void testCreateClaim_Fail_OrderedPlanNotFound() {
        // Arrange
        Claim newClaim = new Claim();
        newClaim.setProof("medical-report.pdf");

        when(orderedPlanService.getOrderedPlanById("OP999"))
                .thenThrow(new RuntimeException("OrderedPlan with ID OP999 not found"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            claimService.createClaim(newClaim, "OP999");
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(orderedPlanService, times(1)).getOrderedPlanById("OP999");
        verify(claimRepository, never()).save(any(Claim.class));
    }

    // ========================== TEST ACCEPT CLAIM - SUCCESS ==========================
    @Test
    void testAcceptClaim_Success() {
        // Arrange
        when(claimRepository.findById("CLM1")).thenReturn(Optional.of(testClaim));
        when(claimRepository.save(any(Claim.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderedPlanService.updateOrderedPlanStatus(anyString(), anyString())).thenReturn(testOrderedPlan);

        // Act
        Claim result = claimService.acceptClaim("CLM1", "Claim is valid and approved");

        // Assert
        assertNotNull(result);
        assertEquals("CLM1", result.getId());
        assertEquals(ClaimStatusEnum.ACCEPTED, result.getStatus());
        assertEquals("Claim is valid and approved", result.getAcceptedNote());
        assertNotNull(result.getAcceptedTimestamp());
        assertNotNull(result.getUpdatedAt());

        verify(claimRepository, times(1)).findById("CLM1");
        verify(claimRepository, times(1)).save(testClaim);
        verify(orderedPlanService, times(1)).updateOrderedPlanStatus("OP1", OrderedPlanStatusEnum.CLAIMED.name());
    }

    // ========================== TEST ACCEPT CLAIM - FAIL (NOT WAITING FOR REVIEW) ==========================
    @Test
    void testAcceptClaim_Fail_NotWaitingForReview() {
        // Arrange
        testClaim.setStatus(ClaimStatusEnum.ACCEPTED); // Already accepted

        when(claimRepository.findById("CLM1")).thenReturn(Optional.of(testClaim));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            claimService.acceptClaim("CLM1", "Note");
        });

        assertTrue(exception.getMessage().contains("must be in WAITING_FOR_REVIEW status"));
        verify(claimRepository, times(1)).findById("CLM1");
        verify(claimRepository, never()).save(any(Claim.class));
        verify(orderedPlanService, never()).updateOrderedPlanStatus(anyString(), anyString());
    }

    // ========================== TEST REJECT CLAIM - SUCCESS ==========================
    @Test
    void testRejectClaim_Success() {
        // Arrange
        when(claimRepository.findById("CLM1")).thenReturn(Optional.of(testClaim));
        when(claimRepository.save(any(Claim.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(claimRepository.findByOrderedPlanIdAndStatus("OP1", ClaimStatusEnum.REJECTED))
                .thenReturn(new ArrayList<>()); // No previous rejections
        when(orderedPlanRepository.save(any(OrderedPlan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Claim result = claimService.rejectClaim("CLM1", "Incomplete documents", "Medical report is missing");

        // Assert
        assertNotNull(result);
        assertEquals("CLM1", result.getId());
        assertEquals(ClaimStatusEnum.REJECTED, result.getStatus());
        assertEquals("Incomplete documents", result.getRejectionReason());
        assertEquals("Medical report is missing", result.getRejectionDescription());
        assertNotNull(result.getRejectionTimestamp());
        assertNotNull(result.getUpdatedAt());

        // Verify ordered plan status set back to PAID (not 3 rejections yet)
        assertEquals(OrderedPlanStatusEnum.PAID, testOrderedPlan.getStatus());

        verify(claimRepository, times(1)).findById("CLM1");
        verify(claimRepository, times(1)).save(testClaim);
        verify(claimRepository, times(1)).findByOrderedPlanIdAndStatus("OP1", ClaimStatusEnum.REJECTED);
        verify(orderedPlanRepository, times(1)).save(testOrderedPlan);
        verify(orderedPlanService, never()).updateOrderedPlanStatus(anyString(), anyString());
    }

    // ========================== TEST REJECT CLAIM - THIRD REJECTION ==========================
    @Test
    void testRejectClaim_ThirdRejection() {
        // Arrange
        // Create 2 previous rejected claims
        Claim rejectedClaim1 = new Claim();
        rejectedClaim1.setId("CLM_OLD1");
        rejectedClaim1.setStatus(ClaimStatusEnum.REJECTED);
        rejectedClaim1.setOrderedPlan(testOrderedPlan);

        Claim rejectedClaim2 = new Claim();
        rejectedClaim2.setId("CLM_OLD2");
        rejectedClaim2.setStatus(ClaimStatusEnum.REJECTED);
        rejectedClaim2.setOrderedPlan(testOrderedPlan);

        // Mock current claim (will be 3rd rejection)
        when(claimRepository.findById("CLM1")).thenReturn(Optional.of(testClaim));
        when(claimRepository.save(any(Claim.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(claimRepository.findByOrderedPlanIdAndStatus("OP1", ClaimStatusEnum.REJECTED))
                .thenReturn(Arrays.asList(rejectedClaim1, rejectedClaim2, testClaim)); // 3 rejections total
        when(orderedPlanService.updateOrderedPlanStatus(anyString(), anyString())).thenReturn(testOrderedPlan);

        // Act
        Claim result = claimService.rejectClaim("CLM1", "Invalid claim", "Claim does not meet requirements");

        // Assert
        assertNotNull(result);
        assertEquals(ClaimStatusEnum.REJECTED, result.getStatus());
        assertEquals("Invalid claim", result.getRejectionReason());
        assertEquals("Claim does not meet requirements", result.getRejectionDescription());

        verify(claimRepository, times(1)).findById("CLM1");
        verify(claimRepository, times(1)).save(testClaim);
        verify(claimRepository, times(1)).findByOrderedPlanIdAndStatus("OP1", ClaimStatusEnum.REJECTED);
        verify(orderedPlanService, times(1)).updateOrderedPlanStatus("OP1", OrderedPlanStatusEnum.REJECTED.name());
        verify(orderedPlanRepository, never()).save(any(OrderedPlan.class)); // Used service instead
    }

    // ========================== TEST REJECT CLAIM - FAIL (NOT WAITING FOR REVIEW) ==========================
    @Test
    void testRejectClaim_Fail_NotWaitingForReview() {
        // Arrange
        testClaim.setStatus(ClaimStatusEnum.REJECTED); // Already rejected

        when(claimRepository.findById("CLM1")).thenReturn(Optional.of(testClaim));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            claimService.rejectClaim("CLM1", "Reason", "Description");
        });

        assertTrue(exception.getMessage().contains("must be in WAITING_FOR_REVIEW status"));
        verify(claimRepository, times(1)).findById("CLM1");
        verify(claimRepository, never()).save(any(Claim.class));
    }

    // ========================== TEST GET CLAIMS BY FILTERS - BOTH FILTERS ==========================
    @Test
    void testGetClaimsByFilters_BothFilters() {
        // Arrange
        Claim claim1 = new Claim();
        claim1.setId("CLM1");
        claim1.setStatus(ClaimStatusEnum.ACCEPTED);
        claim1.setOrderedPlan(testOrderedPlan);

        Claim claim2 = new Claim();
        claim2.setId("CLM2");
        claim2.setStatus(ClaimStatusEnum.ACCEPTED);
        claim2.setOrderedPlan(testOrderedPlan);

        when(claimRepository.findAll()).thenReturn(Arrays.asList(claim1, claim2));

        // Act
        List<Claim> result = claimService.getClaimsByFilters("ACCEPTED", "INS1");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(ClaimStatusEnum.ACCEPTED, result.get(0).getStatus());
        assertEquals("INS1", result.get(0).getOrderedPlan().getInsurancePlan().getId());

        verify(claimRepository, atLeastOnce()).findAll();
    }

    // ========================== TEST GET CLAIMS BY FILTERS - STATUS ONLY ==========================
    @Test
    void testGetClaimsByFilters_StatusOnly() {
        // Arrange
        Claim claim1 = new Claim();
        claim1.setId("CLM1");
        claim1.setStatus(ClaimStatusEnum.WAITING_FOR_REVIEW);

        Claim claim2 = new Claim();
        claim2.setId("CLM2");
        claim2.setStatus(ClaimStatusEnum.WAITING_FOR_REVIEW);

        when(claimRepository.findByStatus(ClaimStatusEnum.WAITING_FOR_REVIEW))
                .thenReturn(Arrays.asList(claim1, claim2));

        // Act
        List<Claim> result = claimService.getClaimsByFilters("WAITING_FOR_REVIEW", null);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(ClaimStatusEnum.WAITING_FOR_REVIEW, result.get(0).getStatus());
        assertEquals(ClaimStatusEnum.WAITING_FOR_REVIEW, result.get(1).getStatus());

        verify(claimRepository, times(1)).findByStatus(ClaimStatusEnum.WAITING_FOR_REVIEW);
    }

    // ========================== TEST GET CLAIMS BY FILTERS - INSURANCE PLAN ONLY ==========================
    @Test
    void testGetClaimsByFilters_InsurancePlanOnly() {
        // Arrange
        Claim claim1 = new Claim();
        claim1.setId("CLM1");
        claim1.setOrderedPlan(testOrderedPlan);

        Claim claim2 = new Claim();
        claim2.setId("CLM2");
        claim2.setOrderedPlan(testOrderedPlan);

        when(claimRepository.findAll()).thenReturn(Arrays.asList(claim1, claim2));

        // Act
        List<Claim> result = claimService.getClaimsByFilters(null, "INS1");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("INS1", result.get(0).getOrderedPlan().getInsurancePlan().getId());

        verify(claimRepository, times(1)).findAll();
    }

    // ========================== TEST GET CLAIMS BY FILTERS - NO FILTER ==========================
    @Test
    void testGetClaimsByFilters_NoFilter() {
        // Arrange
        Claim claim1 = new Claim();
        claim1.setId("CLM1");
        claim1.setStatus(ClaimStatusEnum.WAITING_FOR_REVIEW);

        Claim claim2 = new Claim();
        claim2.setId("CLM2");
        claim2.setStatus(ClaimStatusEnum.ACCEPTED);

        when(claimRepository.findAll()).thenReturn(Arrays.asList(claim1, claim2));

        // Act
        List<Claim> result = claimService.getClaimsByFilters(null, null);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(claimRepository, times(1)).findAll();
    }

    // ========================== TEST GET CLAIMS BY FILTERS - EMPTY STRINGS ==========================
    @Test
    void testGetClaimsByFilters_EmptyStrings() {
        // Arrange
        Claim claim1 = new Claim();
        claim1.setId("CLM1");

        when(claimRepository.findAll()).thenReturn(Arrays.asList(claim1));

        // Act
        List<Claim> result = claimService.getClaimsByFilters("", "");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(claimRepository, times(1)).findAll();
    }

    // ========================== TEST GET ALL CLAIMS ==========================
    @Test
    void testGetAllClaims() {
        // Arrange
        Claim claim1 = new Claim();
        claim1.setId("CLM1");

        Claim claim2 = new Claim();
        claim2.setId("CLM2");

        when(claimRepository.findAll()).thenReturn(Arrays.asList(claim1, claim2));

        // Act
        List<Claim> result = claimService.getAllClaims();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("CLM1", result.get(0).getId());
        assertEquals("CLM2", result.get(1).getId());

        verify(claimRepository, times(1)).findAll();
    }

    // ========================== TEST GET CLAIMS BY STATUS ==========================
    @Test
    void testGetClaimsByStatus() {
        // Arrange
        Claim claim1 = new Claim();
        claim1.setId("CLM1");
        claim1.setStatus(ClaimStatusEnum.ACCEPTED);

        when(claimRepository.findByStatus(ClaimStatusEnum.ACCEPTED))
                .thenReturn(Arrays.asList(claim1));

        // Act
        List<Claim> result = claimService.getClaimsByStatus("ACCEPTED");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ClaimStatusEnum.ACCEPTED, result.get(0).getStatus());

        verify(claimRepository, times(1)).findByStatus(ClaimStatusEnum.ACCEPTED);
    }

    // ========================== TEST GET CLAIMS BY STATUS - INVALID STATUS ==========================
    @Test
    void testGetClaimsByStatus_InvalidStatus() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            claimService.getClaimsByStatus("INVALID_STATUS");
        });

        assertTrue(exception.getMessage().contains("Invalid claim status"));
        verify(claimRepository, never()).findByStatus(any());
    }

    // ========================== TEST GET CLAIM BY ID - SUCCESS ==========================
    @Test
    void testGetClaimById_Success() {
        // Arrange
        when(claimRepository.findById("CLM1")).thenReturn(Optional.of(testClaim));

        // Act
        Claim result = claimService.getClaimById("CLM1");

        // Assert
        assertNotNull(result);
        assertEquals("CLM1", result.getId());
        assertEquals(testClaim, result);

        verify(claimRepository, times(1)).findById("CLM1");
    }

    // ========================== TEST GET CLAIM BY ID - NOT FOUND ==========================
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

    // ========================== TEST COUNT ALL CLAIMS ==========================
    @Test
    void testCountAllClaims() {
        // Arrange
        when(claimRepository.count()).thenReturn(50L);

        // Act
        int result = claimService.countAllClaims();

        // Assert
        assertEquals(50, result);
        verify(claimRepository, times(1)).count();
    }

    // ========================== TEST GET CLAIMS BY INSURANCE PLAN ID ==========================
    @Test
    void testGetClaimsByInsurancePlanId() {
        // Arrange
        Claim claim1 = new Claim();
        claim1.setId("CLM1");
        claim1.setOrderedPlan(testOrderedPlan);

        when(claimRepository.findAll()).thenReturn(Arrays.asList(claim1));

        // Act
        List<Claim> result = claimService.getClaimsByInsurancePlanId("INS1");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("INS1", result.get(0).getOrderedPlan().getInsurancePlan().getId());

        verify(claimRepository, times(1)).findAll();
    }
}
