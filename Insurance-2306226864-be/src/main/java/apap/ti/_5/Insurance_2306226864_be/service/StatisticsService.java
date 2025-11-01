package apap.ti._5.Insurance_2306226864_be.service;

import java.util.Map;

public interface StatisticsService {

    Map<String, Long> getInsurancePlanStatistics(String service, Integer months);

    Map<String, Integer> getHomepageStatistics();
}
