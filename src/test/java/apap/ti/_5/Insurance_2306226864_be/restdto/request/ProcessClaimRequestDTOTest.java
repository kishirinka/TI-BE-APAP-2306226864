package apap.ti._5.Insurance_2306226864_be.restdto.request.claim;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProcessClaimRequestDTOTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        // Arrange
        ProcessClaimRequestDTO dto = new ProcessClaimRequestDTO();

        // Act
        dto.setAction("ACCEPT");
        dto.setNote("Approved");
        dto.setRejectionReason("Reason");
        dto.setRejectionDescription("Description");

        // Assert
        assertEquals("ACCEPT", dto.getAction());
        assertEquals("Approved", dto.getNote());
        assertEquals("Reason", dto.getRejectionReason());
        assertEquals("Description", dto.getRejectionDescription());
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        // Arrange
        ProcessClaimRequestDTO dto = new ProcessClaimRequestDTO(
                "REJECT", null, "Invalid", "Details missing"
        );

        // Assert
        assertEquals("REJECT", dto.getAction());
        assertNull(dto.getNote());
        assertEquals("Invalid", dto.getRejectionReason());
        assertEquals("Details missing", dto.getRejectionDescription());
    }
}