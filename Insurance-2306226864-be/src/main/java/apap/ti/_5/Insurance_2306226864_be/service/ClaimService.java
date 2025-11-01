package apap.ti._5.Insurance_2306226864_be.service;

import apap.ti._5.Insurance_2306226864_be.model.Claim;
import java.util.List;

public interface ClaimService {

    List<Claim> getAllClaims();

    List<Claim> getClaimsByStatus(String status);

    List<Claim> getClaimsByInsurancePlanId(String insurancePlanId);

    List<Claim> getClaimsByFilters(String status, String insurancePlanId);

    Claim getClaimById(String id);

    Claim createClaim(Claim claim, String orderedPlanId);

    Claim acceptClaim(String id, String note);

    Claim rejectClaim(String id, String reason, String description);

    int countAllClaims();
}
