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

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<OrderedPlanResponseDTO>> getOrderedPlanById(@PathVariable String id) {
        try {
            OrderedPlan orderedPlan = orderedPlanService.getOrderedPlanById(id);
            OrderedPlanResponseDTO orderedPlanDTO = OrderedPlanResponseDTO.fromEntity(orderedPlan);

            return ResponseEntity.ok(
                BaseResponseDTO.success("Successfully retrieved ordered plan with ID: " + id, orderedPlanDTO)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponseDTO.error(404, "Ordered plan not found with ID: " + id));
        }
    }
}
