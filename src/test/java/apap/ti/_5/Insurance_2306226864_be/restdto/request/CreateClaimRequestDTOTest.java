package apap.ti._5.Insurance_2306226864_be.restdto.request.claim;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CreateClaimRequestDTOTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        // Arrange
        CreateClaimRequestDTO dto = new CreateClaimRequestDTO();
        
        // Act
        dto.setOrderedPlanId("OP1");
        dto.setProof("proof.jpg");

        // Assert
        assertEquals("OP1", dto.getOrderedPlanId());
        assertEquals("proof.jpg", dto.getProof());
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        // Arrange
        CreateClaimRequestDTO dto = new CreateClaimRequestDTO("OP2", "image.png");

        // Assert
        assertEquals("OP2", dto.getOrderedPlanId());
        assertEquals("image.png", dto.getProof());
    }
}