package apap.ti._5.Insurance_2306226864_be.restdto.request.insuranceplan;

import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateInsurancePlanRequestDTOTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        // Arrange
        CreateInsurancePlanRequestDTO dto = new CreateInsurancePlanRequestDTO();
        List<ServiceEnum> services = Arrays.asList(ServiceEnum.FLIGHT);

        // Act
        dto.setPlanName("Test Plan");
        dto.setProviderId("PROV1");
        dto.setPrice(100);
        dto.setCoverage(1000);
        dto.setCoverageDetails("Details");
        dto.setApplicableService(services);
        dto.setExpiredByDays(30);

        // Assert
        assertEquals("Test Plan", dto.getPlanName());
        assertEquals("PROV1", dto.getProviderId());
        assertEquals(100, dto.getPrice());
        assertEquals(1000, dto.getCoverage());
        assertEquals("Details", dto.getCoverageDetails());
        assertEquals(services, dto.getApplicableService());
        assertEquals(30, dto.getExpiredByDays());
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        // Arrange
        List<ServiceEnum> services = Arrays.asList(ServiceEnum.ACCOMMODATION, ServiceEnum.RENTALS);
        CreateInsurancePlanRequestDTO dto = new CreateInsurancePlanRequestDTO(
                "Full Plan", "PROV2", 200, 2000,
                "Full Details", services, 90
        );

        // Assert
        assertEquals("Full Plan", dto.getPlanName());
        assertEquals("PROV2", dto.getProviderId());
        assertEquals(200, dto.getPrice());
        assertEquals(2000, dto.getCoverage());
        assertEquals("Full Details", dto.getCoverageDetails());
        assertEquals(services, dto.getApplicableService());
        assertEquals(90, dto.getExpiredByDays());
    }
}