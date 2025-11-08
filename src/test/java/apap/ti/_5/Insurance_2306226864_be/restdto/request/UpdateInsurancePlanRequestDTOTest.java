package apap.ti._5.Insurance_2306226864_be.restdto.request.insuranceplan;

import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UpdateInsurancePlanRequestDTOTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        // Arrange
        UpdateInsurancePlanRequestDTO dto = new UpdateInsurancePlanRequestDTO();
        List<ServiceEnum> services = Arrays.asList(ServiceEnum.FLIGHT);

        // Act
        dto.setId("INS1");
        dto.setPlanName("Updated Plan");
        dto.setPrice(150);
        dto.setCoverage(1500);
        dto.setCoverageDetails("Updated Details");
        dto.setApplicableService(services);
        dto.setExpiredByDays(45);

        // Assert
        assertEquals("INS1", dto.getId());
        assertEquals("Updated Plan", dto.getPlanName());
        assertEquals(150, dto.getPrice());
        assertEquals(1500, dto.getCoverage());
        assertEquals("Updated Details", dto.getCoverageDetails());
        assertEquals(services, dto.getApplicableService());
        assertEquals(45, dto.getExpiredByDays());
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        // Arrange
        List<ServiceEnum> services = Arrays.asList(ServiceEnum.PACKAGE);
        UpdateInsurancePlanRequestDTO dto = new UpdateInsurancePlanRequestDTO(
                "INS2", "Full Update", 250, 2500,
                "Full Updated Details", services, 180
        );

        // Assert
        assertEquals("INS2", dto.getId());
        assertEquals("Full Update", dto.getPlanName());
        assertEquals(250, dto.getPrice());
        assertEquals(2500, dto.getCoverage());
        assertEquals("Full Updated Details", dto.getCoverageDetails());
        assertEquals(services, dto.getApplicableService());
        assertEquals(180, dto.getExpiredByDays());
    }
}