package apap.ti._5.Insurance_2306226864_be.restcontroller;

import apap.ti._5.Insurance_2306226864_be.restdto.response.BaseResponseDTO;
import apap.ti._5.Insurance_2306226864_be.service.StatisticsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsRestController {

    @Autowired
    private StatisticsService statisticsService;

    // Valid service types
    private static final List<String> VALID_SERVICES = Arrays.asList(
            "ALL", "ACCOMMODATION", "FLIGHT", "PACKAGE", "RENTALS"
    );

    // Valid months
    private static final List<Integer> VALID_MONTHS = Arrays.asList(3, 6, 12);

    /**
     * GET /api/statistics/insurance-plans
     * Get insurance plan statistics (ordered plan count per insurance plan)
     * 
     * Query Params:
     * - service (required): "ALL", "ACCOMMODATION", "FLIGHT", "PACKAGE", "RENTALS"
     * - months (required): 3, 6, or 12
     */
    @GetMapping("/insurance-plans")
    public ResponseEntity<BaseResponseDTO<Map<String, Long>>> getInsurancePlanStatistics(
            @RequestParam(required = true) String service,
            @RequestParam(required = true) Integer months) {
        
        // Validate service parameter
        if (service == null || service.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponseDTO.error(400, 
                            "Service parameter is required. Valid values: ALL, ACCOMMODATION, FLIGHT, PACKAGE, RENTALS"));
        }
        
        String serviceUpper = service.toUpperCase();
        if (!VALID_SERVICES.contains(serviceUpper)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponseDTO.error(400, 
                            "Invalid service: " + service + ". Valid values: ALL, ACCOMMODATION, FLIGHT, PACKAGE, RENTALS"));
        }
        
        // Validate months parameter
        if (months == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponseDTO.error(400, 
                            "Months parameter is required. Valid values: 3, 6, 12"));
        }
        
        if (!VALID_MONTHS.contains(months)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponseDTO.error(400, 
                            "Invalid months: " + months + ". Valid values: 3, 6, 12"));
        }
        
        // Get statistics
        Map<String, Long> statistics = statisticsService.getInsurancePlanStatistics(serviceUpper, months);
        
        String message = String.format(
                "Successfully retrieved insurance plan statistics for service '%s' in the last %d months", 
                serviceUpper, months
        );
        
        return ResponseEntity.ok(BaseResponseDTO.success(message, statistics));
    }

    /**
     * GET /api/statistics/homepage
     * Get homepage statistics (total counts for insurance plans, policies, claims)
     */
    @GetMapping("/homepage")
    public ResponseEntity<BaseResponseDTO<Map<String, Integer>>> getHomepageStatistics() {
        Map<String, Integer> statistics = statisticsService.getHomepageStatistics();
        
        return ResponseEntity.ok(
                BaseResponseDTO.success("Successfully retrieved homepage statistics", statistics)
        );
    }
}
