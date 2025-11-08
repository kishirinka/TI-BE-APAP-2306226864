package apap.ti._5.Insurance_2306226864_be.restdto.response.statistics;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsResponseDTOTest {

    @Test
    void testOf() {
        // Arrange
        Map<String, Object> data = new HashMap<>();
        data.put("key1", "value1");
        data.put("key2", 123);

        // Act
        StatisticsResponseDTO dto = StatisticsResponseDTO.of(data);

        // Assert
        assertNotNull(dto);
        assertNotNull(dto.getData());
        assertEquals(2, dto.getData().size());
        assertEquals("value1", dto.getData().get("key1"));
        assertEquals(123, dto.getData().get("key2"));
    }

    @Test
    void testFromLongMap() {
        // Arrange
        Map<String, Long> longMap = new HashMap<>();
        longMap.put("PlanA", 10L);
        longMap.put("PlanB", 20L);

        // Act
        StatisticsResponseDTO dto = StatisticsResponseDTO.fromLongMap(longMap);

        // Assert
        assertNotNull(dto);
        assertNotNull(dto.getData());
        assertEquals(2, dto.getData().size());
        // Verify that the Long value is correctly stored as an Object
        assertEquals(10L, (Long) dto.getData().get("PlanA"));
        assertEquals(20L, (Long) dto.getData().get("PlanB"));
    }

    @Test
    void testFromIntegerMap() {
        // Arrange
        Map<String, Integer> integerMap = new HashMap<>();
        integerMap.put("Policies", 100);
        integerMap.put("Claims", 200);

        // Act
        StatisticsResponseDTO dto = StatisticsResponseDTO.fromIntegerMap(integerMap);

        // Assert
        assertNotNull(dto);
        assertNotNull(dto.getData());
        assertEquals(2, dto.getData().size());
        // Verify that the Integer value is correctly stored as an Object
        assertEquals(100, (Integer) dto.getData().get("Policies"));
        assertEquals(200, (Integer) dto.getData().get("Claims"));
    }
}