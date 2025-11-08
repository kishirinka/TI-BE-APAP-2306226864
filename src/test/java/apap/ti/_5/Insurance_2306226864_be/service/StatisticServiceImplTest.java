package apap.ti._5.Insurance_2306226864_be.service;

import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import apap.ti._5.Insurance_2306226864_be.model.InsurancePlan;
import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
import apap.ti._5.Insurance_2306226864_be.repository.ClaimRepository;
import apap.ti._5.Insurance_2306226864_be.repository.InsurancePlanRepository;
import apap.ti._5.Insurance_2306226864_be.repository.OrderedPlanRepository;
import apap.ti._5.Insurance_2306226864_be.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceImplTest {

    @Mock
    private InsurancePlanRepository insurancePlanRepository;

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private OrderedPlanRepository orderedPlanRepository;

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    private OrderedPlan opMonth1_Flight;
    private OrderedPlan opMonth1_Accommodation;
    private OrderedPlan opMonth2_Flight;
    private OrderedPlan opOld;

    private String month1Key;
    private String month2Key;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime month1Date = now.minusMonths(1);
        LocalDateTime month2Date = now.minusMonths(2);
        LocalDateTime oldDate = now.minusMonths(13); // Older than 12 months

        // Format keys for assertions (YYYY-MM)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        month1Key = month1Date.format(formatter);
        month2Key = month2Date.format(formatter);

        // Setup Insurance Plans
        InsurancePlan flightPlan = new InsurancePlan();
        flightPlan.setApplicableService(Collections.singletonList(ServiceEnum.FLIGHT));

        InsurancePlan accommodationPlan = new InsurancePlan();
        accommodationPlan.setApplicableService(Collections.singletonList(ServiceEnum.ACCOMMODATION));

        // Setup Ordered Plans
        opMonth1_Flight = new OrderedPlan();
        opMonth1_Flight.setCreatedAt(month1Date);
        opMonth1_Flight.setInsurancePlan(flightPlan);

        opMonth1_Accommodation = new OrderedPlan();
        opMonth1_Accommodation.setCreatedAt(month1Date);
        opMonth1_Accommodation.setInsurancePlan(accommodationPlan);

        opMonth2_Flight = new OrderedPlan();
        opMonth2_Flight.setCreatedAt(month2Date);
        opMonth2_Flight.setInsurancePlan(flightPlan);

        opOld = new OrderedPlan();
        opOld.setCreatedAt(oldDate);
        opOld.setInsurancePlan(flightPlan);
    }

    // ========================== TESTS FOR getInsurancePlanStatistics ==========================

    @Test
    void testGetInsurancePlanStatistics_AllServices_Success() {
        // Arrange
        when(orderedPlanRepository.findAll()).thenReturn(Arrays.asList(
                opMonth1_Flight, opMonth1_Accommodation, opMonth2_Flight, opOld
        ));

        // Act: Request last 6 months, ALL services
        Map<String, Long> result = statisticsService.getInsurancePlanStatistics("ALL", 6);

        // Assert
        // opOld should be excluded because it's 13 months old
        assertEquals(2, result.size()); // Should have entries for month1 and month2
        assertEquals(2L, result.get(month1Key)); // 2 plans in month 1
        assertEquals(1L, result.get(month2Key)); // 1 plan in month 2
    }

    @Test
    void testGetInsurancePlanStatistics_FilteredService_Success() {
        // Arrange
        when(orderedPlanRepository.findAll()).thenReturn(Arrays.asList(
                opMonth1_Flight, opMonth1_Accommodation, opMonth2_Flight
        ));

        // Act: Request FLIGHT service only
        Map<String, Long> result = statisticsService.getInsurancePlanStatistics("FLIGHT", 6);

        // Assert
        // Should only count FLIGHT plans
        assertEquals(1L, result.get(month1Key)); // Only 1 FLIGHT plan in month 1
        assertEquals(1L, result.get(month2Key)); // 1 FLIGHT plan in month 2
        assertFalse(result.values().contains(2L)); // Ensure total wasn't counted for month 1
    }

    @Test
    void testGetInsurancePlanStatistics_InvalidService_ReturnsEmpty() {
        // Arrange
        when(orderedPlanRepository.findAll()).thenReturn(Arrays.asList(opMonth1_Flight));

        // Act
        Map<String, Long> result = statisticsService.getInsurancePlanStatistics("INVALID_SERVICE", 6);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetInsurancePlanStatistics_DateFiltering() {
        // Arrange
        when(orderedPlanRepository.findAll()).thenReturn(Arrays.asList(opMonth1_Flight, opOld));

        // Act: Request ONLY last 1 month
        // Assuming opMonth1 is exactly 1 month ago, sometimes "isAfter" might exclude it 
        // if execution time matches exactly, but usually it's safe for general testing.
        // Let's use 2 months to be safe it includes month 1 and excludes month 13.
        Map<String, Long> result = statisticsService.getInsurancePlanStatistics("ALL", 2);

        // Assert
        assertTrue(result.containsKey(month1Key));
        assertEquals(1, result.size()); // opOld should be filtered out
    }

    // ========================== TESTS FOR getHomepageStatistics ==========================

    @Test
    void testGetHomepageStatistics_Success() {
        // Arrange
        when(insurancePlanRepository.countByDeletedAtIsNull()).thenReturn(10L);
        when(policyRepository.count()).thenReturn(50L);
        when(claimRepository.count()).thenReturn(100L);

        // Act
        Map<String, Integer> result = statisticsService.getHomepageStatistics();

        // Assert
        assertEquals(3, result.size());
        assertEquals(10, result.get("insurancePlans"));
        assertEquals(50, result.get("policies"));
        assertEquals(100, result.get("claims"));

        verify(insurancePlanRepository, times(1)).countByDeletedAtIsNull();
        verify(policyRepository, times(1)).count();
        verify(claimRepository, times(1)).count();
    }
}