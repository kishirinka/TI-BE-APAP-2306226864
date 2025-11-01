package apap.ti._5.Insurance_2306226864_be.restcontroller;

import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
import apap.ti._5.Insurance_2306226864_be.restdto.response.BaseResponseDTO;
import apap.ti._5.Insurance_2306226864_be.restdto.response.orderedplan.OrderedPlanResponseDTO;
import apap.ti._5.Insurance_2306226864_be.service.OrderedPlanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ordered-plans")
public class OrderedPlanRestController {

    @Autowired
    private OrderedPlanService orderedPlanService;

    /**
     * GET /api/ordered-plans/{id}
     * Get ordered plan detail with nested insurance plan and claims data
     * 
     * @param id - Ordered Plan ID
     * @return BaseResponseDTO<OrderedPlanResponseDTO> with nested insurance plan and claims
     */
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<OrderedPlanResponseDTO>> getOrderedPlanById(@PathVariable String id) {
        try {
            OrderedPlan orderedPlan = orderedPlanService.getOrderedPlanById(id);
            
            // Convert to DTO (automatically includes insurance plan and claims)
            OrderedPlanResponseDTO orderedPlanDTO = OrderedPlanResponseDTO.fromEntity(orderedPlan);
            
            return ResponseEntity.ok(
                    BaseResponseDTO.success(
                            "Successfully retrieved ordered plan with ID: " + id, 
                            orderedPlanDTO
                    )
            );
        } catch (RuntimeException e) {
            // Ordered plan not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponseDTO.error(404, "Ordered plan not found with ID: " + id));
        }
    }
}
