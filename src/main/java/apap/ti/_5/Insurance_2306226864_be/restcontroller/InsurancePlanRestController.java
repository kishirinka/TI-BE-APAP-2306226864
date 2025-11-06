package apap.ti._5.Insurance_2306226864_be.restcontroller;

import apap.ti._5.Insurance_2306226864_be.restdto.request.insuranceplan.CreateInsurancePlanRequestDTO;
import apap.ti._5.Insurance_2306226864_be.restdto.request.insuranceplan.UpdateInsurancePlanRequestDTO;
import apap.ti._5.Insurance_2306226864_be.restdto.response.BaseResponseDTO;
import apap.ti._5.Insurance_2306226864_be.restdto.response.insuranceplan.InsurancePlanResponseDTO;
import apap.ti._5.Insurance_2306226864_be.service.InsurancePlanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/insurance-plans")
public class InsurancePlanRestController {
    
    @Autowired
    private InsurancePlanService insurancePlanService;

    /**
     * GET /api/insurance-plans - Get all insurance plans
     * @return BaseResponseDTO with list of insurance plans
     */
    @GetMapping
    public ResponseEntity<BaseResponseDTO<List<InsurancePlanResponseDTO>>> getAllInsurancePlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        List<InsurancePlanResponseDTO> plans = insurancePlanService.getAllInsurancePlans(PageRequest.of(page, size));
        
        String message = plans.isEmpty() 
            ? "No insurance plans available yet" 
            : "Successfully retrieved " + plans.size() + " insurance plan(s)";
        
        return ResponseEntity.ok(BaseResponseDTO.success(message, plans));
    }

    /**
     * GET /api/insurance-plans/{id} - Get insurance plan detail
     * @param id Insurance plan ID
     * @return BaseResponseDTO with insurance plan detail
     */
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<InsurancePlanResponseDTO>> getInsurancePlanById(@PathVariable String id) {
        InsurancePlanResponseDTO plan = insurancePlanService.getInsurancePlanById(id);
        
        if (plan != null) {
            return ResponseEntity.ok(
                BaseResponseDTO.success("Successfully retrieved insurance plan with ID: " + id, plan)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                BaseResponseDTO.error(HttpStatus.NOT_FOUND.value(), "Insurance plan with ID " + id + " not found")
            );
        }
    }

    /**
     * POST /api/insurance-plans - Create insurance plan
     * @param request CreateInsurancePlanRequestDTO
     * @param bindingResult Validation result
     * @return BaseResponseDTO with created insurance plan
     */
    @PostMapping
    public ResponseEntity<BaseResponseDTO<InsurancePlanResponseDTO>> createInsurancePlan(
            @Valid @RequestBody CreateInsurancePlanRequestDTO request,
            BindingResult bindingResult) {
        
        // Handle validation errors
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            
            return ResponseEntity.badRequest().body(
                BaseResponseDTO.error(HttpStatus.BAD_REQUEST.value(), "Validation failed: " + errorMessage)
            );
        }
        
        InsurancePlanResponseDTO createdPlan = insurancePlanService.createInsurancePlan(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
            BaseResponseDTO.success("Insurance plan created successfully", createdPlan)
        );
    }

    /**
     * PUT /api/insurance-plans/{id} - Update insurance plan
     * @param id Insurance plan ID
     * @param request UpdateInsurancePlanRequestDTO
     * @param bindingResult Validation result
     * @return BaseResponseDTO with updated insurance plan
     */
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<InsurancePlanResponseDTO>> updateInsurancePlan(
            @PathVariable String id,
            @Valid @RequestBody UpdateInsurancePlanRequestDTO request,
            BindingResult bindingResult) {
        
        // Handle validation errors
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            
            return ResponseEntity.badRequest().body(
                BaseResponseDTO.error(HttpStatus.BAD_REQUEST.value(), "Validation failed: " + errorMessage)
            );
        }
        
        InsurancePlanResponseDTO updatedPlan = insurancePlanService.updateInsurancePlan(id, request);
        
        if (updatedPlan != null) {
            return ResponseEntity.ok(
                BaseResponseDTO.success("Insurance plan updated successfully", updatedPlan)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                BaseResponseDTO.error(HttpStatus.NOT_FOUND.value(), "Insurance plan with ID " + id + " not found")
            );
        }
    }

    /**
     * DELETE /api/insurance-plans/{id} - Soft delete insurance plan
     * @param id Insurance plan ID
     * @return BaseResponseDTO with success or error message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<String>> deleteInsurancePlan(@PathVariable String id) {
        try {
            boolean deleted = insurancePlanService.deleteInsurancePlan(id);
            
            if (deleted) {
                return ResponseEntity.ok(
                    BaseResponseDTO.success("Insurance plan deleted successfully", 
                        "Insurance plan with ID " + id + " has been soft deleted")
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    BaseResponseDTO.error(HttpStatus.NOT_FOUND.value(), "Insurance plan with ID " + id + " not found")
                );
            }
        } catch (IllegalStateException e) {
            // Handle case when there are active ordered plans
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                BaseResponseDTO.error(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        } catch (RuntimeException e) {
            // Handle other runtime exceptions
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                BaseResponseDTO.error(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        }
    }
}
