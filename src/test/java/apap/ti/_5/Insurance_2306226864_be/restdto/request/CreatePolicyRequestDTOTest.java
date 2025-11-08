package apap.ti._5.Insurance_2306226864_be.restdto.request.policy;

import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreatePolicyRequestDTOTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        // Arrange
        CreatePolicyRequestDTO dto = new CreatePolicyRequestDTO();
        LocalDate date = LocalDate.now();
        List<String> ids = Arrays.asList("INS1", "INS2");

        // Act
        dto.setUserId("user1");
        dto.setBookingId("book1");
        dto.setService(ServiceEnum.FLIGHT);
        dto.setStartDate(date);
        dto.setInsurancePlanIds(ids);

        // Assert
        assertEquals("user1", dto.getUserId());
        assertEquals("book1", dto.getBookingId());
        assertEquals(ServiceEnum.FLIGHT, dto.getService());
        assertEquals(date, dto.getStartDate());
        assertEquals(ids, dto.getInsurancePlanIds());
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        // Arrange
        LocalDate date = LocalDate.now().minusDays(1);
        List<String> ids = Arrays.asList("INS3");
        
        CreatePolicyRequestDTO dto = new CreatePolicyRequestDTO(
                "user2", "book2", ServiceEnum.ACCOMMODATION, date, ids
        );

        // Assert
        assertEquals("user2", dto.getUserId());
        assertEquals("book2", dto.getBookingId());
        assertEquals(ServiceEnum.ACCOMMODATION, dto.getService());
        assertEquals(date, dto.getStartDate());
        assertEquals(ids, dto.getInsurancePlanIds());
    }
}