package apap.ti._5.Insurance_2306226864_be.restdto.response.insuranceplan;

import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import apap.ti._5.Insurance_2306226864_be.model.InsurancePlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InsurancePlanResponseDTOTest {

    private InsurancePlan testPlan;

    @BeforeEach
    void setUp() {
        testPlan = new InsurancePlan();
        testPlan.setId("INS1");
        testPlan.setPlanName("Test Plan");
        testPlan.setProviderId("PROV1");
        testPlan.setPrice(100000);
        testPlan.setCoverage(5000000);
        testPlan.setCoverageDetails("Details here");
        testPlan.setApplicableService(Arrays.asList(ServiceEnum.FLIGHT, ServiceEnum.PACKAGE));
        testPlan.setExpiredByDays(30);
        testPlan.setCreatedAt(LocalDateTime.now().minusDays(1));
        testPlan.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testFromEntity_Success() {
        // Act
        InsurancePlanResponseDTO dto = InsurancePlanResponseDTO.fromEntity(testPlan);

        // Assert
        assertNotNull(dto);
        assertEquals("INS1", dto.getId());
        assertEquals("Test Plan", dto.getPlanName());
        assertEquals("PROV1", dto.getProviderId());
        assertEquals(100000, dto.getPrice());
        assertEquals(5000000, dto.getCoverage());
        assertEquals("Details here", dto.getCoverageDetails());
        assertEquals(30, dto.getExpiredByDays());
        assertEquals(testPlan.getCreatedAt(), dto.getCreatedAt());
        assertEquals(testPlan.getUpdatedAt(), dto.getUpdatedAt());

        // Assert list mapping
        assertNotNull(dto.getApplicableService());
        assertEquals(2, dto.getApplicableService().size());
        assertTrue(dto.getApplicableService().contains(ServiceEnum.FLIGHT));
        assertTrue(dto.getApplicableService().contains(ServiceEnum.PACKAGE));
    }

    @Test
    void testFromEntity_NullSafety_NullEntity() {
        // Act
        InsurancePlanResponseDTO dto = InsurancePlanResponseDTO.fromEntity(null);

        // Assert
        assertNull(dto);
    }
}