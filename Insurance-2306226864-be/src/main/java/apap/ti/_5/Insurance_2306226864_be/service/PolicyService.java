package apap.ti._5.Insurance_2306226864_be.service;

import java.util.List;
import apap.ti._5.Insurance_2306226864_be.model.Policy;

public interface PolicyService {
    List<Policy> getAllPolicies();
    Policy getPolicyById(String id);
    Policy createPolicy(Policy policy, List<String> insurancePlanIds);
    Policy payPolicy(String id);
    void updateExpiredPolicies();
    int countAllPolicies();
}
