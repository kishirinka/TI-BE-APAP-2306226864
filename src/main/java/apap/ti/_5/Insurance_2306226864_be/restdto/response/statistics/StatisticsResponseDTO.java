package apap.ti._5.Insurance_2306226864_be.restdto.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponseDTO {
    
    private Map<String, Object> data;
    
    /**
     * Helper method to create response with data
     */
    public static StatisticsResponseDTO of(Map<String, Object> data) {
        return new StatisticsResponseDTO(data);
    }
    
    /**
     * Helper method to create response from Map<String, Long>
     */
    public static StatisticsResponseDTO fromLongMap(Map<String, Long> dataMap) {
        Map<String, Object> data = Map.copyOf(dataMap);
        return new StatisticsResponseDTO(data);
    }
    
    /**
     * Helper method to create response from Map<String, Integer>
     */
    public static StatisticsResponseDTO fromIntegerMap(Map<String, Integer> dataMap) {
        Map<String, Object> data = Map.copyOf(dataMap);
        return new StatisticsResponseDTO(data);
    }
}
