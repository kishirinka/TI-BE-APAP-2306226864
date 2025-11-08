package apap.ti._5.Insurance_2306226864_be.service;

import apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.OrderedPlanStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.PolicyStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import apap.ti._5.Insurance_2306226864_be.model.Claim;
import apap.ti._5.Insurance_2306226864_be.model.InsurancePlan;
import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
import apap.ti._5.Insurance_2306226864_be.model.Policy;
import apap.ti._5.Insurance_2306226864_be.repository.OrderedPlanRepository;
import apap.ti._5.Insurance_2306226864_be.repository.PolicyRepository;
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
class PolicyServiceImplTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private OrderedPlanRepository orderedPlanRepository;

    @Mock
    private InsurancePlanService insurancePlanService;

    @InjectMocks
    private PolicyServiceImpl policyService;

    private Policy testPolicy;
    private InsurancePlan testInsurancePlan;
    private OrderedPlan testOrderedPlan;

    @BeforeEach
    void setUp() {
        // Setup Test Insurance Plan
        testInsurancePlan = new InsurancePlan();
        testInsurancePlan.setId("INS1");
        testInsurancePlan.setPlanName("Flight Basic");
        testInsurancePlan.setPrice(100000);
        testInsurancePlan.setCoverage(5000000);
        testInsurancePlan.setApplicableService(Arrays.asList(ServiceEnum.FLIGHT));
        testInsurancePlan.setExpiredByDays(30);

        // Setup Test Policy
        testPolicy = new Policy();
        testPolicy.setId("POL1");
        testPolicy.setUserId("user123");
        testPolicy.setBookingId("BOOK123");
        testPolicy.setService(ServiceEnum.FLIGHT);
        testPolicy.setStatus(PolicyStatusEnum.CREATED);
        testPolicy.setStartDate(LocalDate.now());

        // Setup Test Ordered Plan
        testOrderedPlan = new OrderedPlan();
        testOrderedPlan.setId("POL1-OP1");
        testOrderedPlan.setPolicy(testPolicy);
        testOrderedPlan.setInsurancePlan(testInsurancePlan);
        testOrderedPlan.setStatus(OrderedPlanStatusEnum.ORDERED);
        testOrderedPlan.setExpiredDate(LocalDate.now().plusDays(30));
        testOrderedPlan.setClaims(new ArrayList<>());

        testPolicy.setOrderedPlans(new ArrayList<>(Arrays.asList(testOrderedPlan)));
    }

    // ========================== TEST GET ALL ==========================
    @Test
    void testGetAllPolicies_Success() {
        // Arrange
        when(policyRepository.findAll()).thenReturn(Arrays.asList(testPolicy));

        // Act
        List<Policy> result = policyService.getAllPolicies();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("POL1", result.get(0).getId());
        verify(policyRepository, times(1)).findAll();
    }

    // ========================== TEST GET BY ID ==========================
    @Test
    void testGetPolicyById_Success() {
        // Arrange
        when(policyRepository.findById("POL1")).thenReturn(Optional.of(testPolicy));

        // Act
        Policy result = policyService.getPolicyById("POL1");

        // Assert
        assertNotNull(result);
        assertEquals("POL1", result.getId());
        verify(policyRepository, times(1)).findById("POL1");
    }

    @Test
    void testGetPolicyById_NotFound() {
        // Arrange
        when(policyRepository.findById("POL999")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            policyService.getPolicyById("POL999");
        });
        assertTrue(exception.getMessage().contains("not found"));
        verify(policyRepository, times(1)).findById("POL999");
    }

    // ========================== TEST CREATE POLICY ==========================
    @Test
    void testCreatePolicy_Success() {
        // Arrange
        Policy newPolicy = new Policy();
        newPolicy.setUserId("user123");
        newPolicy.setBookingId("BOOK123");
        newPolicy.setService(ServiceEnum.FLIGHT);
        newPolicy.setStartDate(LocalDate.now());

        List<String> planIds = Arrays.asList("INS1");

        when(policyRepository.count()).thenReturn(0L);
        when(orderedPlanRepository.count()).thenReturn(0L);
        when(insurancePlanService.getInsurancePlanEntityById("INS1")).thenReturn(testInsurancePlan);
        when(policyRepository.save(any(Policy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Policy result = policyService.createPolicy(newPolicy, planIds);

        // Assert
        assertNotNull(result);
        assertEquals("POL1", result.getId());
        assertEquals(PolicyStatusEnum.CREATED, result.getStatus());
        assertEquals(100000, result.getTotalPrice());
        assertEquals(5000000, result.getTotalCoverage());
        assertEquals(1, result.getOrderedPlans().size());
        assertEquals("POL1-OP1", result.getOrderedPlans().get(0).getId());
        assertEquals(OrderedPlanStatusEnum.ORDERED, result.getOrderedPlans().get(0).getStatus());
        verify(policyRepository, times(1)).count();
        verify(orderedPlanRepository, times(1)).count();
        verify(policyRepository, times(1)).save(any(Policy.class));
    }

    @Test
    void testCreatePolicy_Fail_PlanNotApplicable() {
        // Arrange
        Policy newPolicy = new Policy();
        newPolicy.setService(ServiceEnum.ACCOMMODATION); // Policy is for Accommodation
        newPolicy.setStartDate(LocalDate.now());

        // testInsurancePlan is for FLIGHT
        when(insurancePlanService.getInsurancePlanEntityById("INS1")).thenReturn(testInsurancePlan);

        List<String> planIds = Arrays.asList("INS1");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            policyService.createPolicy(newPolicy, planIds);
        });
        assertTrue(exception.getMessage().contains("is not applicable for service"));
        verify(policyRepository, never()).save(any(Policy.class));
    }

    @Test
    void testCreatePolicy_Fail_NoPlans() {
        // Arrange
        List<String> planIds = new ArrayList<>();
        Policy newPolicy = new Policy();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            policyService.createPolicy(newPolicy, planIds);
        });
        assertTrue(exception.getMessage().contains("one insurance plan must be selected"));
        verify(policyRepository, never()).save(any(Policy.class));
    }

    @Test
    void testCreatePolicy_Fail_DuplicatePlans() {
        // Arrange
        List<String> planIds = Arrays.asList("INS1", "INS1");
        Policy newPolicy = new Policy();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            policyService.createPolicy(newPolicy, planIds);
        });
        assertTrue(exception.getMessage().contains("Duplicate insurance plans"));
        verify(policyRepository, never()).save(any(Policy.class));
    }

    // ========================== TEST PAY POLICY ==========================
    @Test
    void testPayPolicy_Success() {
        // Arrange
        testPolicy.setStatus(PolicyStatusEnum.CREATED);
        testOrderedPlan.setStatus(OrderedPlanStatusEnum.ORDERED);
        when(policyRepository.findById("POL1")).thenReturn(Optional.of(testPolicy));
        when(policyRepository.save(any(Policy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Policy result = policyService.payPolicy("POL1");

        // Assert
        assertNotNull(result);
        assertEquals(PolicyStatusEnum.PAID, result.getStatus());
        assertEquals(1, result.getOrderedPlans().size());
        assertEquals(OrderedPlanStatusEnum.PAID, result.getOrderedPlans().get(0).getStatus());
        verify(policyRepository, times(1)).findById("POL1");
        verify(policyRepository, times(1)).save(result);
        verify(orderedPlanRepository, times(1)).save(testOrderedPlan);
    }

    @Test
    void testPayPolicy_Fail_AlreadyPaid() {
        // Arrange
        testPolicy.setStatus(PolicyStatusEnum.PAID);
        when(policyRepository.findById("POL1")).thenReturn(Optional.of(testPolicy));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            policyService.payPolicy("POL1");
        });
        assertTrue(exception.getMessage().contains("Only policies with CREATED status can be paid"));
        verify(policyRepository, times(1)).findById("POL1");
        verify(policyRepository, never()).save(any(Policy.class));
        verify(orderedPlanRepository, never()).save(any(OrderedPlan.class));
    }

    // ========================== TEST UPDATE EXPIRED POLICIES ==========================
    @Test
    void testUpdateExpiredPolicies_Success_OPExpires() {
        // Arrange
        testOrderedPlan.setStatus(OrderedPlanStatusEnum.PAID);
        testOrderedPlan.setExpiredDate(LocalDate.now().minusDays(1)); // Expired yesterday
        testOrderedPlan.setClaims(new ArrayList<>()); // No claims
        
        testPolicy.setStatus(PolicyStatusEnum.PAID);
        testPolicy.setOrderedPlans(Arrays.asList(testOrderedPlan));

        when(orderedPlanRepository.findByExpiredDateBefore(any(LocalDate.class)))
                .thenReturn(Arrays.asList(testOrderedPlan));
        when(policyRepository.findAll()).thenReturn(Arrays.asList(testPolicy));

        // Act
        policyService.updateExpiredPolicies();

        // Assert
        // Verify OrderedPlan becomes EXPIRED
        assertEquals(OrderedPlanStatusEnum.EXPIRED, testOrderedPlan.getStatus());
        verify(orderedPlanRepository, times(1)).save(testOrderedPlan);

        // Verify Policy becomes EXPIRED because all its OPs are expired
        assertEquals(PolicyStatusEnum.EXPIRED, testPolicy.getStatus());
        verify(policyRepository, times(1)).save(testPolicy);
    }

    @Test
    void testUpdateExpiredPolicies_Skip_OPAlreadyClaimed() {
        // Arrange
        testOrderedPlan.setStatus(OrderedPlanStatusEnum.CLAIMED); // Already claimed
        testOrderedPlan.setExpiredDate(LocalDate.now().minusDays(1)); // Expired yesterday

        when(orderedPlanRepository.findByExpiredDateBefore(any(LocalDate.class)))
                .thenReturn(Arrays.asList(testOrderedPlan));
        when(policyRepository.findAll()).thenReturn(new ArrayList<>()); // No policies to check

        // Act
        policyService.updateExpiredPolicies();

        // Assert
        // Status should remain CLAIMED, save should not be called
        assertEquals(OrderedPlanStatusEnum.CLAIMED, testOrderedPlan.getStatus());
        verify(orderedPlanRepository, never()).save(testOrderedPlan);
    }

    @Test
    void testUpdateExpiredPolicies_Skip_OPHasAcceptedClaim() {
        // Arrange
        Claim acceptedClaim = new Claim();
        acceptedClaim.setStatus(ClaimStatusEnum.ACCEPTED);

        testOrderedPlan.setStatus(OrderedPlanStatusEnum.PAID); // Still PAID
        testOrderedPlan.setExpiredDate(LocalDate.now().minusDays(1)); // Expired yesterday
        testOrderedPlan.setClaims(Arrays.asList(acceptedClaim)); // Has accepted claim

        when(orderedPlanRepository.findByExpiredDateBefore(any(LocalDate.class)))
                .thenReturn(Arrays.asList(testOrderedPlan));
        when(policyRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        policyService.updateExpiredPolicies();

        // Assert
        // Status should remain PAID, not EXPIRED, because it has an accepted claim
        assertEquals(OrderedPlanStatusEnum.PAID, testOrderedPlan.getStatus());
        verify(orderedPlanRepository, never()).save(testOrderedPlan);
    }

    @Test
    void testUpdateExpiredPolicies_PolicyNotExpired_OneOPActive() {
        // Arrange
        OrderedPlan expiredOP = new OrderedPlan();
        expiredOP.setId("OP_EXP");
        expiredOP.setStatus(OrderedPlanStatusEnum.EXPIRED); // Already expired
        
        OrderedPlan activeOP = new OrderedPlan();
        activeOP.setId("OP_ACT");
        activeOP.setStatus(OrderedPlanStatusEnum.PAID); // Still active
        activeOP.setExpiredDate(LocalDate.now().plusDays(10));

        testPolicy.setStatus(PolicyStatusEnum.PAID);
        testPolicy.setOrderedPlans(Arrays.asList(expiredOP, activeOP));

        // We only mock findAll() for the policy check, not the OP check
        when(policyRepository.findAll()).thenReturn(Arrays.asList(testPolicy));
        when(orderedPlanRepository.findByExpiredDateBefore(any(LocalDate.class)))
                .thenReturn(new ArrayList<>()); // Assume OP update already ran

        // Act
        policyService.updateExpiredPolicies();

        // Assert
        // Policy status should remain PAID because one OP is still active
        assertEquals(PolicyStatusEnum.PAID, testPolicy.getStatus());
        verify(policyRepository, never()).save(testPolicy);
    }

    // ========================== TEST COUNT ==========================
    @Test
    void testCountAllPolicies() {
        // Arrange
        when(policyRepository.count()).thenReturn(5L);

        // Act
        int result = policyService.countAllPolicies();

        // Assert
        assertEquals(5, result);
        verify(policyRepository, times(1)).count();
    }
}