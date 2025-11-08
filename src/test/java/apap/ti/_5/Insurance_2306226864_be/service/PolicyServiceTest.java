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
class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private OrderedPlanRepository orderedPlanRepository;

    @Mock
    private InsurancePlanService insurancePlanService;

    @InjectMocks
    private PolicyServiceImpl policyService;

    private Policy testPolicy;
    private InsurancePlan testInsurancePlan1;
    private InsurancePlan testInsurancePlan2;
    private OrderedPlan testOrderedPlan1;
    private OrderedPlan testOrderedPlan2;

    @BeforeEach
    void setUp() {
        // Setup test insurance plan 1
        testInsurancePlan1 = new InsurancePlan();
        testInsurancePlan1.setId("INS1");
        testInsurancePlan1.setPlanName("Health Plus");
        testInsurancePlan1.setProviderId("PROV123");
        testInsurancePlan1.setPrice(500000);
        testInsurancePlan1.setCoverage(10000000);
        testInsurancePlan1.setCoverageDetails("Health coverage");
        testInsurancePlan1.setApplicableService(Arrays.asList(ServiceEnum.ACCOMMODATION));
        testInsurancePlan1.setExpiredByDays(365);
        testInsurancePlan1.setCreatedAt(LocalDateTime.now());
        testInsurancePlan1.setUpdatedAt(LocalDateTime.now());

        // Setup test insurance plan 2
        testInsurancePlan2 = new InsurancePlan();
        testInsurancePlan2.setId("INS2");
        testInsurancePlan2.setPlanName("Travel Safe");
        testInsurancePlan2.setProviderId("PROV456");
        testInsurancePlan2.setPrice(300000);
        testInsurancePlan2.setCoverage(5000000);
        testInsurancePlan2.setCoverageDetails("Travel coverage");
        testInsurancePlan2.setApplicableService(Arrays.asList(ServiceEnum.ACCOMMODATION));
        testInsurancePlan2.setExpiredByDays(180);
        testInsurancePlan2.setCreatedAt(LocalDateTime.now());
        testInsurancePlan2.setUpdatedAt(LocalDateTime.now());

        // Setup test policy
        testPolicy = new Policy();
        testPolicy.setId("POL1");
        testPolicy.setUserId("USER123");
        testPolicy.setBookingId("BOOK456");
        testPolicy.setService(ServiceEnum.ACCOMMODATION);
        testPolicy.setStartDate(LocalDate.now());
        testPolicy.setStatus(PolicyStatusEnum.CREATED);
        testPolicy.setTotalPrice(800000);
        testPolicy.setTotalCoverage(15000000);
        testPolicy.setCreatedAt(LocalDateTime.now());
        testPolicy.setUpdatedAt(LocalDateTime.now());

        // Setup test ordered plans
        testOrderedPlan1 = new OrderedPlan();
        testOrderedPlan1.setId("OP1");
        testOrderedPlan1.setPolicy(testPolicy);
        testOrderedPlan1.setInsurancePlan(testInsurancePlan1);
        testOrderedPlan1.setStatus(OrderedPlanStatusEnum.ORDERED);
        testOrderedPlan1.setExpiredDate(LocalDate.now().plusDays(365));
        testOrderedPlan1.setCreatedAt(LocalDateTime.now());
        testOrderedPlan1.setUpdatedAt(LocalDateTime.now());

        testOrderedPlan2 = new OrderedPlan();
        testOrderedPlan2.setId("OP2");
        testOrderedPlan2.setPolicy(testPolicy);
        testOrderedPlan2.setInsurancePlan(testInsurancePlan2);
        testOrderedPlan2.setStatus(OrderedPlanStatusEnum.ORDERED);
        testOrderedPlan2.setExpiredDate(LocalDate.now().plusDays(180));
        testOrderedPlan2.setCreatedAt(LocalDateTime.now());
        testOrderedPlan2.setUpdatedAt(LocalDateTime.now());

        testPolicy.setOrderedPlans(Arrays.asList(testOrderedPlan1, testOrderedPlan2));
    }
    // ========================== TEST CREATE POLICY - FAIL (INSURANCE PLAN NOT FOUND) ==========================
    @Test
    void testCreatePolicy_Fail_InsurancePlanNotFound() {
        // Arrange
        Policy newPolicy = new Policy();
        newPolicy.setUserId("USER123");
        newPolicy.setBookingId("BOOK789");
        newPolicy.setService(ServiceEnum.ACCOMMODATION);
        newPolicy.setStartDate(LocalDate.now());

        List<String> insurancePlanIds = Arrays.asList("INS999");

        when(policyRepository.count()).thenReturn(5L);
        when(insurancePlanService.getInsurancePlanEntityById("INS999"))
                .thenThrow(new RuntimeException("Insurance Plan with ID INS999 not found"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            policyService.createPolicy(newPolicy, insurancePlanIds);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(policyRepository, times(1)).count();
        verify(insurancePlanService, times(1)).getInsurancePlanEntityById("INS999");
        verify(policyRepository, never()).save(any(Policy.class));
    }

    // ========================== TEST CREATE POLICY - FAIL (DUPLICATE INSURANCE PLAN) ==========================
    @Test
    void testCreatePolicy_Fail_DuplicateInsurancePlan() {
        // Arrange
        Policy newPolicy = new Policy();
        newPolicy.setUserId("USER123");
        newPolicy.setBookingId("BOOK789");
        newPolicy.setService(ServiceEnum.ACCOMMODATION);
        newPolicy.setStartDate(LocalDate.now());

        // Duplicate INS1 in the list
        List<String> insurancePlanIds = Arrays.asList("INS1", "INS2", "INS1");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            policyService.createPolicy(newPolicy, insurancePlanIds);
        });

        assertTrue(exception.getMessage().contains("Duplicate insurance plans are not allowed"));
        verify(policyRepository, never()).count();
        verify(insurancePlanService, never()).getInsurancePlanEntityById(anyString());
        verify(policyRepository, never()).save(any(Policy.class));
    }

    // ========================== TEST CREATE POLICY - FAIL (EMPTY INSURANCE PLAN LIST) ==========================
    @Test
    void testCreatePolicy_Fail_EmptyInsurancePlanList() {
        // Arrange
        Policy newPolicy = new Policy();
        newPolicy.setUserId("USER123");
        newPolicy.setBookingId("BOOK789");
        newPolicy.setService(ServiceEnum.ACCOMMODATION);
        newPolicy.setStartDate(LocalDate.now());

        List<String> insurancePlanIds = new ArrayList<>();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            policyService.createPolicy(newPolicy, insurancePlanIds);
        });

        assertTrue(exception.getMessage().contains("At least one insurance plan must be selected"));
        verify(policyRepository, never()).save(any(Policy.class));
    }

    // ========================== TEST CREATE POLICY - FAIL (INCOMPATIBLE SERVICE) ==========================
    @Test
    void testCreatePolicy_Fail_IncompatibleService() {
        // Arrange
        Policy newPolicy = new Policy();
        newPolicy.setUserId("USER123");
        newPolicy.setBookingId("BOOK789");
        newPolicy.setService(ServiceEnum.FLIGHT); // Different service
        newPolicy.setStartDate(LocalDate.now());

        List<String> insurancePlanIds = Arrays.asList("INS1");

        when(policyRepository.count()).thenReturn(5L);
        when(insurancePlanService.getInsurancePlanEntityById("INS1")).thenReturn(testInsurancePlan1);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            policyService.createPolicy(newPolicy, insurancePlanIds);
        });

        assertTrue(exception.getMessage().contains("is not applicable for service"));
        verify(policyRepository, times(1)).count();
        verify(insurancePlanService, times(1)).getInsurancePlanEntityById("INS1");
        verify(policyRepository, never()).save(any(Policy.class));
    }

    // ========================== TEST PAY POLICY - SUCCESS ==========================
    @Test
    void testPayPolicy_Success() {
        // Arrange
        when(policyRepository.findById("POL1")).thenReturn(Optional.of(testPolicy));
        when(policyRepository.save(any(Policy.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderedPlanRepository.save(any(OrderedPlan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Policy result = policyService.payPolicy("POL1");

        // Assert
        assertNotNull(result);
        assertEquals("POL1", result.getId());
        assertEquals(PolicyStatusEnum.PAID, result.getStatus());

        // Verify all ordered plans are paid
        for (OrderedPlan op : result.getOrderedPlans()) {
            assertEquals(OrderedPlanStatusEnum.PAID, op.getStatus());
        }

        verify(policyRepository, times(1)).findById("POL1");
        verify(policyRepository, times(1)).save(testPolicy);
        verify(orderedPlanRepository, times(2)).save(any(OrderedPlan.class)); // 2 ordered plans
    }

    // ========================== TEST PAY POLICY - FAIL (ALREADY PAID) ==========================
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

    // ========================== TEST PAY POLICY - FAIL (POLICY NOT FOUND) ==========================
    @Test
    void testPayPolicy_Fail_PolicyNotFound() {
        // Arrange
        when(policyRepository.findById("POL999")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            policyService.payPolicy("POL999");
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(policyRepository, times(1)).findById("POL999");
        verify(policyRepository, never()).save(any(Policy.class));
    }

    // ========================== TEST UPDATE EXPIRED POLICIES ==========================
    @Test
    void testUpdateExpiredPolicies() {
        // Arrange
        LocalDate pastDate = LocalDate.now().minusDays(10);

        // Create expired ordered plans without accepted claims
        OrderedPlan expiredOP1 = new OrderedPlan();
        expiredOP1.setId("OP1");
        expiredOP1.setStatus(OrderedPlanStatusEnum.PAID);
        expiredOP1.setExpiredDate(pastDate);
        expiredOP1.setClaims(new ArrayList<>());

        OrderedPlan expiredOP2 = new OrderedPlan();
        expiredOP2.setId("OP2");
        expiredOP2.setStatus(OrderedPlanStatusEnum.PAID);
        expiredOP2.setExpiredDate(pastDate);
        expiredOP2.setClaims(new ArrayList<>());

        Policy expiredPolicy = new Policy();
        expiredPolicy.setId("POL1");
        expiredPolicy.setStatus(PolicyStatusEnum.PAID);
        expiredPolicy.setOrderedPlans(Arrays.asList(expiredOP1, expiredOP2));
        expiredOP1.setPolicy(expiredPolicy);
        expiredOP2.setPolicy(expiredPolicy);

        when(orderedPlanRepository.findByExpiredDateBefore(any(LocalDate.class)))
                .thenReturn(Arrays.asList(expiredOP1, expiredOP2));
        when(policyRepository.findAll()).thenReturn(Arrays.asList(expiredPolicy));
        when(orderedPlanRepository.save(any(OrderedPlan.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(policyRepository.save(any(Policy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        policyService.updateExpiredPolicies();

        // Assert
        // Verify ordered plans status updated to EXPIRED
        assertEquals(OrderedPlanStatusEnum.EXPIRED, expiredOP1.getStatus());
        assertEquals(OrderedPlanStatusEnum.EXPIRED, expiredOP2.getStatus());

        // Verify policy status updated to EXPIRED
        assertEquals(PolicyStatusEnum.EXPIRED, expiredPolicy.getStatus());

        verify(orderedPlanRepository, times(1)).findByExpiredDateBefore(any(LocalDate.class));
        verify(policyRepository, times(1)).findAll();
        verify(orderedPlanRepository, times(2)).save(any(OrderedPlan.class));
        verify(policyRepository, times(1)).save(expiredPolicy);
    }

    // ========================== TEST UPDATE EXPIRED POLICIES - WITH ACCEPTED CLAIM ==========================
    @Test
    void testUpdateExpiredPolicies_WithAcceptedClaim() {
        // Arrange
        LocalDate pastDate = LocalDate.now().minusDays(10);

        // Create expired ordered plan with accepted claim
        OrderedPlan expiredOP = new OrderedPlan();
        expiredOP.setId("OP1");
        expiredOP.setStatus(OrderedPlanStatusEnum.PAID);
        expiredOP.setExpiredDate(pastDate);

        Claim acceptedClaim = new Claim();
        acceptedClaim.setId("CLM1");
        acceptedClaim.setStatus(ClaimStatusEnum.ACCEPTED);
        acceptedClaim.setOrderedPlan(expiredOP);

        expiredOP.setClaims(Arrays.asList(acceptedClaim));

        Policy policy = new Policy();
        policy.setId("POL1");
        policy.setStatus(PolicyStatusEnum.PAID);
        policy.setOrderedPlans(Arrays.asList(expiredOP));
        expiredOP.setPolicy(policy);

        when(orderedPlanRepository.findByExpiredDateBefore(any(LocalDate.class)))
                .thenReturn(Arrays.asList(expiredOP));
        when(policyRepository.findAll()).thenReturn(Arrays.asList(policy));

        // Act
        policyService.updateExpiredPolicies();

        // Assert
        // Verify ordered plan status NOT changed (because has accepted claim)
        assertEquals(OrderedPlanStatusEnum.PAID, expiredOP.getStatus());

        // Verify policy status NOT changed to EXPIRED
        assertEquals(PolicyStatusEnum.PAID, policy.getStatus());

        verify(orderedPlanRepository, times(1)).findByExpiredDateBefore(any(LocalDate.class));
        verify(policyRepository, times(1)).findAll();
        verify(orderedPlanRepository, never()).save(any(OrderedPlan.class)); // Not saved because not changed
        verify(policyRepository, never()).save(any(Policy.class)); // Not saved because not changed
    }

    // ========================== TEST UPDATE EXPIRED POLICIES - SKIP ALREADY EXPIRED ==========================
    @Test
    void testUpdateExpiredPolicies_SkipAlreadyExpired() {
        // Arrange
        LocalDate pastDate = LocalDate.now().minusDays(10);

        OrderedPlan alreadyExpiredOP = new OrderedPlan();
        alreadyExpiredOP.setId("OP1");
        alreadyExpiredOP.setStatus(OrderedPlanStatusEnum.EXPIRED); // Already expired
        alreadyExpiredOP.setExpiredDate(pastDate);
        alreadyExpiredOP.setClaims(new ArrayList<>());

        Policy policy = new Policy();
        policy.setId("POL1");
        policy.setStatus(PolicyStatusEnum.EXPIRED); // Already expired
        policy.setOrderedPlans(Arrays.asList(alreadyExpiredOP));
        alreadyExpiredOP.setPolicy(policy);

        when(orderedPlanRepository.findByExpiredDateBefore(any(LocalDate.class)))
                .thenReturn(Arrays.asList(alreadyExpiredOP));
        when(policyRepository.findAll()).thenReturn(Arrays.asList(policy));

        // Act
        policyService.updateExpiredPolicies();

        // Assert
        // Verify no changes
        assertEquals(OrderedPlanStatusEnum.EXPIRED, alreadyExpiredOP.getStatus());
        assertEquals(PolicyStatusEnum.EXPIRED, policy.getStatus());

        verify(orderedPlanRepository, times(1)).findByExpiredDateBefore(any(LocalDate.class));
        verify(policyRepository, times(1)).findAll();
        verify(orderedPlanRepository, never()).save(any(OrderedPlan.class)); // Skipped
        verify(policyRepository, never()).save(any(Policy.class)); // Skipped
    }

    // ========================== TEST COUNT ALL POLICIES ==========================
    @Test
    void testCountAllPolicies() {
        // Arrange
        when(policyRepository.count()).thenReturn(25L);

        // Act
        int result = policyService.countAllPolicies();

        // Assert
        assertEquals(25, result);
        verify(policyRepository, times(1)).count();
    }

    // ========================== TEST GET ALL POLICIES ==========================
    @Test
    void testGetAllPolicies() {
        // Arrange
        Policy policy2 = new Policy();
        policy2.setId("POL2");
        policy2.setUserId("USER456");
        policy2.setStatus(PolicyStatusEnum.PAID);

        when(policyRepository.findAll()).thenReturn(Arrays.asList(testPolicy, policy2));

        // Act
        List<Policy> result = policyService.getAllPolicies();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("POL1", result.get(0).getId());
        assertEquals("POL2", result.get(1).getId());
        verify(policyRepository, times(1)).findAll();
    }

    // ========================== TEST GET POLICY BY ID - SUCCESS ==========================
    @Test
    void testGetPolicyById_Success() {
        // Arrange
        when(policyRepository.findById("POL1")).thenReturn(Optional.of(testPolicy));

        // Act
        Policy result = policyService.getPolicyById("POL1");

        // Assert
        assertNotNull(result);
        assertEquals("POL1", result.getId());
        assertEquals(testPolicy, result);
        verify(policyRepository, times(1)).findById("POL1");
    }

    // ========================== TEST GET POLICY BY ID - NOT FOUND ==========================
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
}
