package apap.ti._5.Insurance_2306226864_be.restdto.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BaseResponseDTOTest {

    @Test
    void testSuccess() {
        // Arrange
        String testData = "Test Data";
        
        // Act
        BaseResponseDTO<String> response = BaseResponseDTO.success("OK", testData);

        // Assert
        assertTrue(response.getSuccess());
        assertEquals(200, response.getStatus());
        assertEquals("OK", response.getMessage());
        assertEquals(testData, response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testCreated() {
        // Arrange
        String testData = "New Resource";

        // Act
        BaseResponseDTO<String> response = BaseResponseDTO.created("Created", testData);

        // Assert
        assertTrue(response.getSuccess());
        assertEquals(201, response.getStatus());
        assertEquals("Created", response.getMessage());
        assertEquals(testData, response.getData());
    }

    @Test
    void testError_WithStatus() {
        // Act
        BaseResponseDTO<Object> response = BaseResponseDTO.error(404, "Not Found");

        // Assert
        assertFalse(response.getSuccess());
        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testError_DefaultStatus() {
        // Act
        BaseResponseDTO<Object> response = BaseResponseDTO.error("Internal Error");

        // Assert
        assertFalse(response.getSuccess());
        assertEquals(500, response.getStatus()); // Default error status
        assertEquals("Internal Error", response.getMessage());
        assertNull(response.getData());
    }
}