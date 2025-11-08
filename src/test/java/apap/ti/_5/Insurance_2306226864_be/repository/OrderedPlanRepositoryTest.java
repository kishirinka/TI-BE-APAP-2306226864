// package apap.ti._5.Insurance_2306226864_be.repository;

// import apap.ti._5.Insurance_2306226864_be.enums.OrderedPlanStatusEnum;
// import apap.ti._5.Insurance_2306226864_be.model.InsurancePlan;
// import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
// import apap.ti._5.Insurance_2306226864_be.model.Policy;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
// import org.springframework.test.context.ActiveProfiles;

// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.*;

// @DataJpaTest
// @ActiveProfiles("test")
// class OrderedPlanRepositoryTest {

//     @Autowired
//     private OrderedPlanRepository orderedPlanRepository;

//     @Autowired
//     private TestEntityManager entityManager;

//     private OrderedPlan testOrderedPlan;
//     private InsurancePlan testInsurancePlan;
//     private Policy testPolicy;

//     @BeforeEach
//     void setUp() {
//         // Create test insurance plan
//         testInsurancePlan = new InsurancePlan();
//         testInsurancePlan.setId("INS1");
//         testInsurancePlan.setProviderId("PROVIDER001");
//         testInsurancePlan.setPlanName("Test Plan");
//         entityManager.persist(testInsurancePlan);

//         // Create test policy
//         testPolicy = new Policy();
//         testPolicy.setId("POL1");
//         testPolicy.setBookingId("BOOK001");
//         testPolicy.setUserId("USER001");
//         entityManager.persist(testPolicy);

//         // Create test ordered plan
//         testOrderedPlan = new OrderedPlan();
//         testOrderedPlan.setId("OP1");
//         testOrderedPlan.setStatus(OrderedPlanStatusEnum.ORDERED);
//         testOrderedPlan.setExpiredDate(LocalDate.now().plusDays(30));
//         testOrderedPlan.setInsurancePlan(testInsurancePlan);
//         testOrderedPlan.setPolicy(testPolicy);
//         testOrderedPlan.setCreatedAt(LocalDateTime.now());
//         testOrderedPlan.setUpdatedAt(LocalDateTime.now());
//     }

//     @Test
//     void testSaveOrderedPlan_Success() {
//         // When
//         OrderedPlan saved = orderedPlanRepository.save(testOrderedPlan);
//         entityManager.flush();

//         // Then
//         assertNotNull(saved);
//         assertEquals("OP1", saved.getId());
//         assertEquals(OrderedPlanStatusEnum.ORDERED, saved.getStatus());
//         assertEquals(testInsurancePlan.getId(), saved.getInsurancePlan().getId());
//         assertEquals(testPolicy.getId(), saved.getPolicy().getId());
//     }

//     @Test
//     void testFindById_PlanExists_ReturnsPlan() {
//         // Given
//         entityManager.persist(testOrderedPlan);
//         entityManager.flush();

//         // When
//         Optional<OrderedPlan> found = orderedPlanRepository.findById("OP1");

//         // Then
//         assertTrue(found.isPresent());
//         assertEquals(OrderedPlanStatusEnum.ORDERED, found.get().getStatus());
//     }

//     @Test
//     void testFindById_PlanNotExists_ReturnsEmpty() {
//         // When
//         Optional<OrderedPlan> found = orderedPlanRepository.findById("NONEXISTENT");

//         // Then
//         assertFalse(found.isPresent());
//     }

//     @Test
//     void testFindByStatus() {
//         // Given
//         entityManager.persist(testOrderedPlan);

//         OrderedPlan paidPlan = new OrderedPlan();
//         paidPlan.setId("OP2");
//         paidPlan.setStatus(OrderedPlanStatusEnum.PAID);
//         paidPlan.setInsurancePlan(testInsurancePlan);
//         paidPlan.setPolicy(testPolicy);
//         entityManager.persist(paidPlan);

//         entityManager.flush();

//         // When
//         List<OrderedPlan> orderedPlans = orderedPlanRepository.findByStatus(OrderedPlanStatusEnum.ORDERED);
//         List<OrderedPlan> paidPlans = orderedPlanRepository.findByStatus(OrderedPlanStatusEnum.PAID);

//         // Then
//         assertEquals(1, orderedPlans.size());
//         assertEquals(1, paidPlans.size());
//         assertEquals("OP1", orderedPlans.get(0).getId());
//         assertEquals("OP2", paidPlans.get(0).getId());
//     }

//     // @Test
//     // void testFindByPolicyId() {
//     //     // Given
//     //     entityManager.persist(testOrderedPlan);
//     //     entityManager.flush();

//     //     // When
//     //     List<OrderedPlan> found = orderedPlanRepository.findByPolicyId(testPolicy.getId());

//     //     // Then
//     //     assertEquals(1, found.size());
//     //     assertEquals(testOrderedPlan.getId(), found.get(0).getId());
//     // }

//     @Test
//     void testUpdateStatus() {
//         // Given
//         entityManager.persist(testOrderedPlan);
//         entityManager.flush();

//         // When
//         testOrderedPlan.setStatus(OrderedPlanStatusEnum.PAID);
//         orderedPlanRepository.save(testOrderedPlan);
//         entityManager.flush();

//         // Then
//         Optional<OrderedPlan> updated = orderedPlanRepository.findById("OP1");
//         assertTrue(updated.isPresent());
//         assertEquals(OrderedPlanStatusEnum.PAID, updated.get().getStatus());
//     }

//     @Test
//     void testCountOrderedPlans() {
//         // Given
//         entityManager.persist(testOrderedPlan);

//         OrderedPlan plan2 = new OrderedPlan();
//         plan2.setId("OP2");
//         plan2.setStatus(OrderedPlanStatusEnum.PAID);
//         plan2.setInsurancePlan(testInsurancePlan);
//         plan2.setPolicy(testPolicy);
//         entityManager.persist(plan2);

//         entityManager.flush();

//         // When
//         long count = orderedPlanRepository.count();

//         // Then
//         assertEquals(2, count);
//     }
// }