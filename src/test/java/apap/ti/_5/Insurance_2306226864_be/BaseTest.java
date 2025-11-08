package apap.ti._5.Insurance_2306226864_be;

import apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.OrderedPlanStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.PolicyStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import apap.ti._5.Insurance_2306226864_be.model.Claim;
import apap.ti._5.Insurance_2306226864_be.model.InsurancePlan;
import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
import apap.ti._5.Insurance_2306226864_be.model.Policy;
import apap.ti._5.Insurance_2306226864_be.repository.ClaimRepository;
import apap.ti._5.Insurance_2306226864_be.repository.InsurancePlanRepository;
import apap.ti._5.Insurance_2306226864_be.repository.OrderedPlanRepository;
import apap.ti._5.Insurance_2306226864_be.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class BaseTest {
    
    @Autowired
    protected InsurancePlanRepository insurancePlanRepository;
    
    @Autowired
    protected OrderedPlanRepository orderedPlanRepository;
    
    @Autowired
    protected PolicyRepository policyRepository;
    
    @Autowired
    protected ClaimRepository claimRepository;
    
    protected InsurancePlan testInsurancePlan;
    protected Policy testPolicy;
    protected OrderedPlan testOrderedPlan;
    protected Claim testClaim;
    
    @BeforeEach
    void setUpBaseTestData() {
        // Create test insurance plan
        testInsurancePlan = new InsurancePlan();
        testInsurancePlan.setId("INS1");
        testInsurancePlan.setProviderId("PROVIDER001");
        testInsurancePlan.setPlanName("Test Plan");
        testInsurancePlan.setPrice(100000);
        testInsurancePlan.setCoverage(500000);
        testInsurancePlan.setCoverageDetails("Test coverage details");
        testInsurancePlan.setApplicableService(Arrays.asList(ServiceEnum.FLIGHT));
        testInsurancePlan.setExpiredByDays(30);
        testInsurancePlan.setCreatedAt(LocalDateTime.now());
        testInsurancePlan.setUpdatedAt(LocalDateTime.now());
        testInsurancePlan = insurancePlanRepository.save(testInsurancePlan);
        
        // Create test policy
        testPolicy = new Policy();
        testPolicy.setId("POL1");
        testPolicy.setBookingId("BOOK001");
        testPolicy.setUserId("USER001");
        testPolicy.setStartDate(LocalDate.now());
        testPolicy.setStatus(PolicyStatusEnum.CREATED);
        testPolicy.setService(ServiceEnum.FLIGHT);
        testPolicy.setTotalPrice(100000);
        testPolicy.setTotalCoverage(500000);
        testPolicy.setCreatedAt(LocalDateTime.now());
        testPolicy.setUpdatedAt(LocalDateTime.now());
        testPolicy = policyRepository.save(testPolicy);
        
        // Create test ordered plan
        testOrderedPlan = new OrderedPlan();
        testOrderedPlan.setId("OP1");
        testOrderedPlan.setStatus(OrderedPlanStatusEnum.ORDERED);
        testOrderedPlan.setExpiredDate(LocalDate.now().plusDays(30));
        testOrderedPlan.setInsurancePlan(testInsurancePlan);
        testOrderedPlan.setPolicy(testPolicy);
        testOrderedPlan.setCreatedAt(LocalDateTime.now());
        testOrderedPlan.setUpdatedAt(LocalDateTime.now());
        testOrderedPlan = orderedPlanRepository.save(testOrderedPlan);
        
        // Create test claim
        testClaim = new Claim();
        testClaim.setId("CLM1");
        testClaim.setStatus(ClaimStatusEnum.WAITING_FOR_REVIEW);
        testClaim.setProof("Test proof document");
        testClaim.setOrderedPlan(testOrderedPlan);
        testClaim.setCreatedAt(LocalDateTime.now());
        testClaim.setUpdatedAt(LocalDateTime.now());
        testClaim = claimRepository.save(testClaim);
    }
    
    @AfterEach
    void tearDown() {
        claimRepository.deleteAll();
        orderedPlanRepository.deleteAll();
        policyRepository.deleteAll();
        insurancePlanRepository.deleteAll();
    }
}