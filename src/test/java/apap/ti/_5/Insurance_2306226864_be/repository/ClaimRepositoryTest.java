// package apap.ti._5.Insurance_2306226864_be.repository;

// import apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum;
// import apap.ti._5.Insurance_2306226864_be.enums.OrderedPlanStatusEnum;
// import apap.ti._5.Insurance_2306226864_be.model.Claim;
// import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
// import org.springframework.test.context.ActiveProfiles;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.*;

// @DataJpaTest
// @ActiveProfiles("test")
// class ClaimRepositoryTest {

//     @Autowired
//     private ClaimRepository claimRepository;

//     @Autowired
//     private TestEntityManager entityManager;

//     private Claim testClaim;
//     private OrderedPlan testOrderedPlan;

//     @BeforeEach
//     void setUp() {
//         // Create test ordered plan
//         testOrderedPlan = new OrderedPlan();
//         testOrderedPlan.setId("OP1");
//         testOrderedPlan.setStatus(OrderedPlanStatusEnum.PAID);
//         entityManager.persist(testOrderedPlan);

//         // Create test claim
//         testClaim = new Claim();
//         testClaim.setId("CLM1");
//         testClaim.setStatus(ClaimStatusEnum.WAITING_FOR_REVIEW);
//         testClaim.setProof("Test proof document");
//         testClaim.setOrderedPlan(testOrderedPlan);
//         testClaim.setCreatedAt(LocalDateTime.now());
//         testClaim.setUpdatedAt(LocalDateTime.now());
//     }

//     @Test
//     void testSaveClaim_Success() {
//         // When
//         Claim saved = claimRepository.save(testClaim);
//         entityManager.flush();

//         // Then
//         assertNotNull(saved);
//         assertEquals("CLM1", saved.getId());
//         assertEquals(ClaimStatusEnum.WAITING_FOR_REVIEW, saved.getStatus());
//         assertEquals("Test proof document", saved.getProof());
//     }

//     @Test
//     void testFindById_ClaimExists_ReturnsClaim() {
//         // Given
//         entityManager.persist(testClaim);
//         entityManager.flush();

//         // When
//         Optional<Claim> found = claimRepository.findById("CLM1");

//         // Then
//         assertTrue(found.isPresent());
//         assertEquals("Test proof document", found.get().getProof());
//     }

//     @Test
//     void testFindById_ClaimNotExists_ReturnsEmpty() {
//         // When
//         Optional<Claim> found = claimRepository.findById("NONEXISTENT");

//         // Then
//         assertFalse(found.isPresent());
//     }

//     @Test
//     void testFindByStatus() {
//         // Given
//         entityManager.persist(testClaim);

//         Claim acceptedClaim = new Claim();
//         acceptedClaim.setId("CLM2");
//         acceptedClaim.setStatus(ClaimStatusEnum.ACCEPTED);
//         acceptedClaim.setProof("Accepted claim proof");
//         acceptedClaim.setOrderedPlan(testOrderedPlan);
//         entityManager.persist(acceptedClaim);

//         entityManager.flush();

//         // When
//         List<Claim> waitingClaims = claimRepository.findByStatus(ClaimStatusEnum.WAITING_FOR_REVIEW);
//         List<Claim> acceptedClaims = claimRepository.findByStatus(ClaimStatusEnum.ACCEPTED);

//         // Then
//         assertEquals(1, waitingClaims.size());
//         assertEquals(1, acceptedClaims.size());
//         assertEquals("CLM1", waitingClaims.get(0).getId());
//         assertEquals("CLM2", acceptedClaims.get(0).getId());
//     }

//     @Test
//     void testFindByOrderedPlanId() {
//         // Given
//         entityManager.persist(testClaim);
//         entityManager.flush();

//         // When
//         List<Claim> found = claimRepository.findByOrderedPlanId(testOrderedPlan.getId());

//         // Then
//         assertEquals(1, found.size());
//         assertEquals("CLM1", found.get(0).getId());
//     }

//     @Test
//     void testFindByOrderedPlanIdAndStatus() {
//         // Given
//         entityManager.persist(testClaim);

//         Claim rejectedClaim = new Claim();
//         rejectedClaim.setId("CLM2");
//         rejectedClaim.setStatus(ClaimStatusEnum.REJECTED);
//         rejectedClaim.setProof("Rejected claim proof");
//         rejectedClaim.setOrderedPlan(testOrderedPlan);
//         entityManager.persist(rejectedClaim);

//         entityManager.flush();

//         // When
//         List<Claim> waitingClaims = claimRepository.findByOrderedPlanIdAndStatus(
//             testOrderedPlan.getId(),
//             ClaimStatusEnum.WAITING_FOR_REVIEW
//         );
//         List<Claim> rejectedClaims = claimRepository.findByOrderedPlanIdAndStatus(
//             testOrderedPlan.getId(),
//             ClaimStatusEnum.REJECTED
//         );

//         // Then
//         assertEquals(1, waitingClaims.size());
//         assertEquals(1, rejectedClaims.size());
//         assertEquals("CLM1", waitingClaims.get(0).getId());
//         assertEquals("CLM2", rejectedClaims.get(0).getId());
//     }

//     @Test
//     void testUpdateClaimStatus() {
//         // Given
//         entityManager.persist(testClaim);
//         entityManager.flush();

//         // When
//         testClaim.setStatus(ClaimStatusEnum.ACCEPTED);
//         testClaim.setAcceptedNote("Claim approved");
//         claimRepository.save(testClaim);
//         entityManager.flush();

//         // Then
//         Optional<Claim> updated = claimRepository.findById("CLM1");
//         assertTrue(updated.isPresent());
//         assertEquals(ClaimStatusEnum.ACCEPTED, updated.get().getStatus());
//         assertEquals("Claim approved", updated.get().getAcceptedNote());
//     }

//     @Test
//     void testCountClaims() {
//         // Given
//         entityManager.persist(testClaim);

//         Claim claim2 = new Claim();
//         claim2.setId("CLM2");
//         claim2.setStatus(ClaimStatusEnum.WAITING_FOR_REVIEW);
//         claim2.setProof("Another proof");
//         claim2.setOrderedPlan(testOrderedPlan);
//         entityManager.persist(claim2);

//         entityManager.flush();

//         // When
//         long count = claimRepository.count();

//         // Then
//         assertEquals(2, count);
//     }
// }