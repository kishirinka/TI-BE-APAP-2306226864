// package apap.ti._5.Insurance_2306226864_be.repository;

// import apap.ti._5.Insurance_2306226864_be.enums.PolicyStatusEnum;
// import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
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
// class PolicyRepositoryTest {

//     @Autowired
//     private PolicyRepository policyRepository;

//     @Autowired
//     private TestEntityManager entityManager;

//     private Policy testPolicy;

//     @BeforeEach
//     void setUp() {
//         testPolicy = new Policy();
//         testPolicy.setId("POL1");
//         testPolicy.setBookingId("BOOK001");
//         testPolicy.setUserId("USER001");
//         testPolicy.setStartDate(LocalDate.now());
//         testPolicy.setStatus(PolicyStatusEnum.CREATED);
//         testPolicy.setService(ServiceEnum.FLIGHT);
//         testPolicy.setTotalPrice(200000);
//         testPolicy.setTotalCoverage(1000000);
//         testPolicy.setCreatedAt(LocalDateTime.now());
//         testPolicy.setUpdatedAt(LocalDateTime.now());
//     }

//     @Test
//     void testSavePolicy_Success() {
//         // When
//         Policy saved = policyRepository.save(testPolicy);
//         entityManager.flush();

//         // Then
//         assertNotNull(saved);
//         assertEquals("POL1", saved.getId());
//         assertEquals("BOOK001", saved.getBookingId());
//         assertEquals(PolicyStatusEnum.CREATED, saved.getStatus());
//     }

//     @Test
//     void testFindById_PolicyExists_ReturnsPolicy() {
//         // Given
//         entityManager.persist(testPolicy);
//         entityManager.flush();

//         // When
//         Optional<Policy> found = policyRepository.findById("POL1");

//         // Then
//         assertTrue(found.isPresent());
//         assertEquals("BOOK001", found.get().getBookingId());
//     }

//     @Test
//     void testFindById_PolicyNotExists_ReturnsEmpty() {
//         // When
//         Optional<Policy> found = policyRepository.findById("NONEXISTENT");

//         // Then
//         assertFalse(found.isPresent());
//     }

//     @Test
//     void testFindByBookingId() {
//         // Given
//         entityManager.persist(testPolicy);
//         entityManager.flush();

//         // When
//         Optional<Policy> found = policyRepository.findByBookingId("BOOK001");

//         // Then
//         assertTrue(found.isPresent());
//         assertEquals("POL1", found.get().getId());
//     }

//     @Test
//     void testFindByStatus() {
//         // Given
//         entityManager.persist(testPolicy);

//         Policy paidPolicy = new Policy();
//         paidPolicy.setId("POL2");
//         paidPolicy.setBookingId("BOOK002");
//         paidPolicy.setUserId("USER001");
//         paidPolicy.setStartDate(LocalDate.now());
//         paidPolicy.setStatus(PolicyStatusEnum.PAID);
//         paidPolicy.setService(ServiceEnum.FLIGHT);
//         entityManager.persist(paidPolicy);

//         entityManager.flush();

//         // When
//         List<Policy> createdPolicies = policyRepository.findByStatus(PolicyStatusEnum.CREATED);
//         List<Policy> paidPolicies = policyRepository.findByStatus(PolicyStatusEnum.PAID);

//         // Then
//         assertEquals(1, createdPolicies.size());
//         assertEquals(1, paidPolicies.size());
//         assertEquals("POL1", createdPolicies.get(0).getId());
//         assertEquals("POL2", paidPolicies.get(0).getId());
//     }

//     @Test
//     void testFindByUserId() {
//         // Given
//         entityManager.persist(testPolicy);
//         entityManager.flush();

//         // When
//         List<Policy> found = policyRepository.findByUserId("USER001");

//         // Then
//         assertEquals(1, found.size());
//         assertEquals("POL1", found.get(0).getId());
//     }

//     @Test
//     void testUpdatePolicy() {
//         // Given
//         entityManager.persist(testPolicy);
//         entityManager.flush();

//         // When
//         testPolicy.setStatus(PolicyStatusEnum.PAID);
//         policyRepository.save(testPolicy);
//         entityManager.flush();

//         // Then
//         Optional<Policy> updated = policyRepository.findById("POL1");
//         assertTrue(updated.isPresent());
//         assertEquals(PolicyStatusEnum.PAID, updated.get().getStatus());
//     }

//     @Test
//     void testCountPolicies() {
//         // Given
//         entityManager.persist(testPolicy);

//         Policy policy2 = new Policy();
//         policy2.setId("POL2");
//         policy2.setBookingId("BOOK002");
//         policy2.setUserId("USER002");
//         policy2.setStartDate(LocalDate.now());
//         policy2.setStatus(PolicyStatusEnum.CREATED);
//         policy2.setService(ServiceEnum.ACCOMMODATION);
//         entityManager.persist(policy2);

//         entityManager.flush();

//         // When
//         long count = policyRepository.count();

//         // Then
//         assertEquals(2, count);
//     }
// }