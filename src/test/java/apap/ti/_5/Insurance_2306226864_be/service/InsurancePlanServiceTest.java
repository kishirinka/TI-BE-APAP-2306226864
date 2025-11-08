package apap.ti._5.Insurance_2306226864_be.service;

import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import apap.ti._5.Insurance_2306226864_be.model.InsurancePlan;
import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
import apap.ti._5.Insurance_2306226864_be.repository.InsurancePlanRepository;
import apap.ti._5.Insurance_2306226864_be.repository.OrderedPlanRepository;
import apap.ti._5.Insurance_2306226864_be.restdto.request.insuranceplan.CreateInsurancePlanRequestDTO;
import apap.ti._5.Insurance_2306226864_be.restdto.request.insuranceplan.UpdateInsurancePlanRequestDTO;
import apap.ti._5.Insurance_2306226864_be.restdto.response.insuranceplan.InsurancePlanResponseDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
class InsurancePlanServiceTest {

    @Mock
    private InsurancePlanRepository insurancePlanRepository;

    @Mock
    private OrderedPlanRepository orderedPlanRepository;

    @InjectMocks
    private InsurancePlanServiceImpl insurancePlanService;

    private InsurancePlan testInsurancePlan;
    private CreateInsurancePlanRequestDTO createRequestDTO;
    private UpdateInsurancePlanRequestDTO updateRequestDTO;

    @BeforeEach
    void setUp() {
        // Setup test insurance plan
        testInsurancePlan = new InsurancePlan();
        testInsurancePlan.setId("INS1");
        testInsurancePlan.setPlanName("Health Plus");
        testInsurancePlan.setProviderId("PROV123");
        testInsurancePlan.setPrice(500000);
        testInsurancePlan.setCoverage(10000000);
        testInsurancePlan.setCoverageDetails("Comprehensive health coverage");
        testInsurancePlan.setApplicableService(Arrays.asList(ServiceEnum.ACCOMMODATION));
        testInsurancePlan.setExpiredByDays(365);
        testInsurancePlan.setCreatedAt(LocalDateTime.now());
        testInsurancePlan.setUpdatedAt(LocalDateTime.now());
        testInsurancePlan.setDeletedAt(null);

        // Setup create request DTO
        createRequestDTO = new CreateInsurancePlanRequestDTO();
        createRequestDTO.setPlanName("Travel Safe");
        createRequestDTO.setProviderId("PROV456");
        createRequestDTO.setPrice(300000);
        createRequestDTO.setCoverage(5000000);
        createRequestDTO.setCoverageDetails("Travel insurance coverage");
        createRequestDTO.setApplicableService(Arrays.asList(ServiceEnum.FLIGHT));
        createRequestDTO.setExpiredByDays(180);

        // Setup update request DTO
        updateRequestDTO = new UpdateInsurancePlanRequestDTO();
        updateRequestDTO.setPlanName("Health Plus Updated");
        updateRequestDTO.setPrice(600000);
        updateRequestDTO.setCoverage(12000000);
        updateRequestDTO.setCoverageDetails("Updated comprehensive health coverage");
        updateRequestDTO.setApplicableService(Arrays.asList(ServiceEnum.ACCOMMODATION, ServiceEnum.FLIGHT));
        updateRequestDTO.setExpiredByDays(400);
    }

    // ========================== TEST GET ALL ==========================
    // @Test
    // void testGetAllInsurancePlans_Success() {
    //     // Arrange
    //     InsurancePlan plan2 = new InsurancePlan();
    //     plan2.setId("INS2");
    //     plan2.setPlanName("Travel Safe");
    //     plan2.setProviderId("PROV456");
    //     plan2.setPrice(300000);
    //     plan2.setCoverage(5000000);
    //     plan2.setCoverageDetails("Travel coverage");
    //     plan2.setApplicableService(Arrays.asList(ServiceEnum.FLIGHT));
    //     plan2.setExpiredByDays(180);
    //     plan2.setCreatedAt(LocalDateTime.now());
    //     plan2.setUpdatedAt(LocalDateTime.now());
    //     plan2.setDeletedAt(null);

    //     List<InsurancePlan> mockPlans = Arrays.asList(testInsurancePlan, plan2);
    //     Page<InsurancePlan> mockPage = new PageImpl<>(mockPlans);
    //     PageRequest pageRequest = PageRequest.of(0, 10);

    //     when(insurancePlanRepository.findAll(pageRequest)).thenReturn(mockPage);

    //     // Act
    //     List<InsurancePlanResponseDTO> result = insurancePlanService.getAllInsurancePlans(pageRequest);

    //     // Assert
    //     assertNotNull(result);
    //     assertEquals(2, result.size());
    //     assertEquals("INS1", result.get(0).getId());
    //     assertEquals("Health Plus", result.get(0).getPlanName());
    //     assertEquals("INS2", result.get(1).getId());
    //     assertEquals("Travel Safe", result.get(1).getPlanName());
    //     verify(insurancePlanRepository, times(1)).findAll(pageRequest);
    // }

    // ========================== TEST GET BY ID - SUCCESS ==========================
    @Test
    void testGetInsurancePlanById_Success() {
        // Arrange
        when(insurancePlanRepository.findByIdAndDeletedAtIsNull("INS1"))
                .thenReturn(Optional.of(testInsurancePlan));

        // Act
        InsurancePlanResponseDTO result = insurancePlanService.getInsurancePlanById("INS1");

        // Assert
        assertNotNull(result);
        assertEquals("INS1", result.getId());
        assertEquals("Health Plus", result.getPlanName());
        assertEquals("PROV123", result.getProviderId());
        assertEquals(500000, result.getPrice());
        assertEquals(10000000, result.getCoverage());
        assertEquals(365, result.getExpiredByDays());
        verify(insurancePlanRepository, times(1)).findByIdAndDeletedAtIsNull("INS1");
    }

    // ========================== TEST GET BY ID - NOT FOUND ==========================
    @Test
    void testGetInsurancePlanById_NotFound() {
        // Arrange
        when(insurancePlanRepository.findByIdAndDeletedAtIsNull("INS999"))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            insurancePlanService.getInsurancePlanById("INS999");
        });

        assertTrue(exception.getMessage().contains("not found or has been deleted"));
        verify(insurancePlanRepository, times(1)).findByIdAndDeletedAtIsNull("INS999");
    }

    // ========================== TEST CREATE - SUCCESS ==========================
    @Test
    void testCreateInsurancePlan_Success() {
        // Arrange
        Long currentCount = 5L;
        when(insurancePlanRepository.count()).thenReturn(currentCount);
        when(insurancePlanRepository.save(any(InsurancePlan.class))).thenAnswer(invocation -> {
            InsurancePlan savedPlan = invocation.getArgument(0);
            return savedPlan;
        });

        // Act
        InsurancePlanResponseDTO result = insurancePlanService.createInsurancePlan(createRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("INS6", result.getId()); // count + 1 = 6
        assertEquals("Travel Safe", result.getPlanName());
        assertEquals("PROV456", result.getProviderId());
        assertEquals(300000, result.getPrice());
        assertEquals(5000000, result.getCoverage());
        assertEquals(180, result.getExpiredByDays());
        
        verify(insurancePlanRepository, times(1)).count();
        verify(insurancePlanRepository, times(1)).save(any(InsurancePlan.class));
    }

    // // ========================== TEST UPDATE - SUCCESS ==========================
    // @Test
    // void testUpdateInsurancePlan_Success() {
    //     // Arrange
    //     LocalDateTime originalUpdatedAt = testInsurancePlan.getUpdatedAt();
        
    //     when(insurancePlanRepository.findByIdAndDeletedAtIsNull("INS1"))
    //             .thenReturn(Optional.of(testInsurancePlan));
    //     when(insurancePlanRepository.save(any(InsurancePlan.class))).thenAnswer(invocation -> {
    //         InsurancePlan savedPlan = invocation.getArgument(0);
    //         return savedPlan;
    //     });

    //     // Act
    //     InsurancePlanResponseDTO result = insurancePlanService.updateInsurancePlan("INS1", updateRequestDTO);

    //     // Assert
    //     assertNotNull(result);
    //     assertEquals("INS1", result.getId());
    //     assertEquals("Health Plus Updated", result.getPlanName());
    //     assertEquals(600000, result.getPrice());
    //     assertEquals(12000000, result.getCoverage());
    //     assertEquals("Updated comprehensive health coverage", result.getCoverageDetails());
    //     assertEquals(400, result.getExpiredByDays());
        
    //     // Verify updatedAt changed
    //     assertNotEquals(originalUpdatedAt, testInsurancePlan.getUpdatedAt());
        
    //     verify(insurancePlanRepository, times(1)).findByIdAndDeletedAtIsNull("INS1");
    //     verify(insurancePlanRepository, times(1)).save(testInsurancePlan);
    // }

    // ========================== TEST DELETE - SUCCESS (ALL ORDERED PLANS EXPIRED) ==========================
    @Test
    void testDeleteInsurancePlan_Success_AllOrderedPlansExpired() {
        // Arrange
        OrderedPlan expiredPlan1 = new OrderedPlan();
        expiredPlan1.setId("OP1");
        expiredPlan1.setExpiredDate(LocalDate.now().minusDays(10)); // Expired 10 days ago

        OrderedPlan expiredPlan2 = new OrderedPlan();
        expiredPlan2.setId("OP2");
        expiredPlan2.setExpiredDate(LocalDate.now().minusDays(5)); // Expired 5 days ago

        List<OrderedPlan> expiredOrderedPlans = Arrays.asList(expiredPlan1, expiredPlan2);
        testInsurancePlan.setOrderedPlans(expiredOrderedPlans);

        when(insurancePlanRepository.findByIdAndDeletedAtIsNull("INS1"))
                .thenReturn(Optional.of(testInsurancePlan));
        when(insurancePlanRepository.save(any(InsurancePlan.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        // Act
        boolean result = insurancePlanService.deleteInsurancePlan("INS1");

        // Assert
        assertTrue(result);
        assertNotNull(testInsurancePlan.getDeletedAt());
        verify(insurancePlanRepository, times(1)).findByIdAndDeletedAtIsNull("INS1");
        verify(insurancePlanRepository, times(1)).save(testInsurancePlan);
    }

    // ========================== TEST DELETE - SUCCESS (NO ORDERED PLANS) ==========================
    @Test
    void testDeleteInsurancePlan_Success_NoOrderedPlans() {
        // Arrange
        testInsurancePlan.setOrderedPlans(null); // No ordered plans

        when(insurancePlanRepository.findByIdAndDeletedAtIsNull("INS1"))
                .thenReturn(Optional.of(testInsurancePlan));
        when(insurancePlanRepository.save(any(InsurancePlan.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        // Act
        boolean result = insurancePlanService.deleteInsurancePlan("INS1");

        // Assert
        assertTrue(result);
        assertNotNull(testInsurancePlan.getDeletedAt());
        verify(insurancePlanRepository, times(1)).findByIdAndDeletedAtIsNull("INS1");
        verify(insurancePlanRepository, times(1)).save(testInsurancePlan);
    }

    // ========================== TEST DELETE - SUCCESS (EMPTY ORDERED PLANS LIST) ==========================
    @Test
    void testDeleteInsurancePlan_Success_EmptyOrderedPlansList() {
        // Arrange
        testInsurancePlan.setOrderedPlans(new ArrayList<>()); // Empty list

        when(insurancePlanRepository.findByIdAndDeletedAtIsNull("INS1"))
                .thenReturn(Optional.of(testInsurancePlan));
        when(insurancePlanRepository.save(any(InsurancePlan.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        // Act
        boolean result = insurancePlanService.deleteInsurancePlan("INS1");

        // Assert
        assertTrue(result);
        assertNotNull(testInsurancePlan.getDeletedAt());
        verify(insurancePlanRepository, times(1)).findByIdAndDeletedAtIsNull("INS1");
        verify(insurancePlanRepository, times(1)).save(testInsurancePlan);
    }

    // ========================== TEST DELETE - FAIL (ORDERED PLAN NOT EXPIRED) ==========================
    @Test
    void testDeleteInsurancePlan_Fail_OrderedPlanNotExpired() {
        // Arrange
        OrderedPlan expiredPlan = new OrderedPlan();
        expiredPlan.setId("OP1");
        expiredPlan.setExpiredDate(LocalDate.now().minusDays(10)); // Expired

        OrderedPlan activePlan = new OrderedPlan();
        activePlan.setId("OP2");
        activePlan.setExpiredDate(LocalDate.now().plusDays(30)); // Still active

        List<OrderedPlan> orderedPlans = Arrays.asList(expiredPlan, activePlan);
        testInsurancePlan.setOrderedPlans(orderedPlans);

        when(insurancePlanRepository.findByIdAndDeletedAtIsNull("INS1"))
                .thenReturn(Optional.of(testInsurancePlan));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            insurancePlanService.deleteInsurancePlan("INS1");
        });

        assertTrue(exception.getMessage().contains("some ordered plans are still active"));
        assertNull(testInsurancePlan.getDeletedAt()); // deletedAt should not be set
        verify(insurancePlanRepository, times(1)).findByIdAndDeletedAtIsNull("INS1");
        verify(insurancePlanRepository, never()).save(any(InsurancePlan.class)); // save should not be called
    }

    // ========================== TEST DELETE - FAIL (ORDERED PLAN EXPIRES TODAY) ==========================
    @Test
    void testDeleteInsurancePlan_Fail_OrderedPlanExpiresExactlyToday() {
        // Arrange
        OrderedPlan planExpiringToday = new OrderedPlan();
        planExpiringToday.setId("OP1");
        planExpiringToday.setExpiredDate(LocalDate.now()); // Expires exactly today

        List<OrderedPlan> orderedPlans = Arrays.asList(planExpiringToday);
        testInsurancePlan.setOrderedPlans(orderedPlans);

        when(insurancePlanRepository.findByIdAndDeletedAtIsNull("INS1"))
                .thenReturn(Optional.of(testInsurancePlan));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            insurancePlanService.deleteInsurancePlan("INS1");
        });

        assertTrue(exception.getMessage().contains("some ordered plans are still active"));
        assertNull(testInsurancePlan.getDeletedAt());
        verify(insurancePlanRepository, times(1)).findByIdAndDeletedAtIsNull("INS1");
        verify(insurancePlanRepository, never()).save(any(InsurancePlan.class));
    }

    // ========================== TEST DELETE - NOT FOUND ==========================
    @Test
    void testDeleteInsurancePlan_NotFound() {
        // Arrange
        when(insurancePlanRepository.findByIdAndDeletedAtIsNull("INS999"))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            insurancePlanService.deleteInsurancePlan("INS999");
        });

        assertTrue(exception.getMessage().contains("not found or has been deleted"));
        verify(insurancePlanRepository, times(1)).findByIdAndDeletedAtIsNull("INS999");
        verify(insurancePlanRepository, never()).save(any(InsurancePlan.class));
    }

    // ========================== TEST COUNT ALL ==========================
    @Test
    void testCountAllInsurancePlans() {
        // Arrange
        Long expectedCount = 10L;
        when(insurancePlanRepository.countByDeletedAtIsNull()).thenReturn(expectedCount);

        // Act
        Long result = insurancePlanService.getInsurancePlansCount();

        // Assert
        assertNotNull(result);
        assertEquals(expectedCount, result);
        verify(insurancePlanRepository, times(1)).countByDeletedAtIsNull();
    }

    // ========================== TEST COUNT ALL - ZERO ==========================
    @Test
    void testCountAllInsurancePlans_Zero() {
        // Arrange
        when(insurancePlanRepository.countByDeletedAtIsNull()).thenReturn(0L);

        // Act
        Long result = insurancePlanService.getInsurancePlansCount();

        // Assert
        assertNotNull(result);
        assertEquals(0L, result);
        verify(insurancePlanRepository, times(1)).countByDeletedAtIsNull();
    }

    // ========================== TEST GET ENTITY BY ID - SUCCESS ==========================
    @Test
    void testGetInsurancePlanEntityById_Success() {
        // Arrange
        when(insurancePlanRepository.findByIdAndDeletedAtIsNull("INS1"))
                .thenReturn(Optional.of(testInsurancePlan));

        // Act
        InsurancePlan result = insurancePlanService.getInsurancePlanEntityById("INS1");

        // Assert
        assertNotNull(result);
        assertEquals("INS1", result.getId());
        assertEquals("Health Plus", result.getPlanName());
        assertEquals(testInsurancePlan, result); // Same instance
        verify(insurancePlanRepository, times(1)).findByIdAndDeletedAtIsNull("INS1");
    }

    // ========================== TEST GET ENTITY BY ID - NOT FOUND ==========================
    @Test
    void testGetInsurancePlanEntityById_NotFound() {
        // Arrange
        when(insurancePlanRepository.findByIdAndDeletedAtIsNull("INS999"))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            insurancePlanService.getInsurancePlanEntityById("INS999");
        });

        assertTrue(exception.getMessage().contains("not found or has been deleted"));
        verify(insurancePlanRepository, times(1)).findByIdAndDeletedAtIsNull("INS999");
    }
}
