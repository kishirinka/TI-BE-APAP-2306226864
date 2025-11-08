package apap.ti._5.Insurance_2306226864_be.util;

import apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.OrderedPlanStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.PolicyStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import apap.ti._5.Insurance_2306226864_be.model.Claim;
import apap.ti._5.Insurance_2306226864_be.model.InsurancePlan;
import apap.ti._5.Insurance_2306226864_be.model.OrderedPlan;
import apap.ti._5.Insurance_2306226864_be.model.Policy;
import apap.ti._5.Insurance_2306226864_be.repository.ClaimRepository;
import apap.ti._5.Insurance_2306226864_be.repository.InsurancePlanRepository;
import apap.ti._5.Insurance_2306226864_be.repository.OrderedPlanRepository;
import apap.ti._5.Insurance_2306226864_be.repository.PolicyRepository;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private InsurancePlanRepository insurancePlanRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private OrderedPlanRepository orderedPlanRepository;

    @Autowired
    private ClaimRepository claimRepository;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🗑️  Clearing existing data...");
        
        // Delete all data in correct order (respecting foreign key constraints)
        claimRepository.deleteAll();
        orderedPlanRepository.deleteAll();
        policyRepository.deleteAll();
        insurancePlanRepository.deleteAll();
        
        System.out.println("✅ Existing data cleared!");
        System.out.println("🌱 Starting fresh data seeding...");
        
        seedInsurancePlans();
        seedPolicies();
        seedOrderedPlans();
        seedClaims();
        
        System.out.println("✅ Data seeding completed!");
    }

    private void seedInsurancePlans() {
        System.out.println("📋 Seeding Insurance Plans...");
        
        List<InsurancePlan> plans = new ArrayList<>();
        ServiceEnum[] services = ServiceEnum.values();
        
        String[] planTypes = {
            "Basic Protection", "Standard Coverage", "Premium Shield", 
            "Ultimate Care", "Essential Guard", "Complete Safety",
            "Advanced Security", "Elite Protection", "Super Coverage",
            "Total Defense"
        };

        for (int i = 0; i < 15; i++) {
            InsurancePlan plan = new InsurancePlan();
            plan.setId("INS" + (i + 1));
            plan.setProviderId("PROV" + faker.number().numberBetween(100, 999));
            plan.setPlanName(planTypes[i % planTypes.length] + " " + services[i % services.length].name());
            plan.setPrice(faker.number().numberBetween(50000, 1000000));
            plan.setCoverage(faker.number().numberBetween(1000000, 50000000));
            plan.setCoverageDetails("Comprehensive coverage for " + services[i % services.length].name().toLowerCase() + 
                                   " including emergency assistance, " +
                                   faker.company().buzzword() + " support, and " +
                                   faker.company().catchPhrase());
            
            // Random applicable services (1-3 services)
            List<ServiceEnum> applicableServices = new ArrayList<>();
            int numServices = random.nextInt(3) + 1;
            for (int j = 0; j < numServices; j++) {
                ServiceEnum service = services[random.nextInt(services.length)];
                if (!applicableServices.contains(service)) {
                    applicableServices.add(service);
                }
            }
            plan.setApplicableService(applicableServices);
            
            plan.setExpiredByDays(faker.number().numberBetween(30, 365));
            
            // Created between Jan 2024 - Nov 2025
            LocalDateTime jan2024 = LocalDateTime.of(2024, 1, 1, 0, 0);
            LocalDateTime nov2025 = LocalDateTime.of(2025, 11, 30, 23, 59);
            long daysBetween = java.time.Duration.between(jan2024, nov2025).toDays();
            LocalDateTime createdAt = jan2024.plusDays(faker.number().numberBetween(0, (int) daysBetween));
            plan.setCreatedAt(createdAt);
            plan.setUpdatedAt(createdAt);
            
            plans.add(plan);
        }
        
        insurancePlanRepository.saveAll(plans);
        System.out.println("✅ Created " + plans.size() + " insurance plans");
    }

    private void seedPolicies() {
        System.out.println("📜 Seeding Policies...");
        
        List<Policy> policies = new ArrayList<>();
        ServiceEnum[] services = ServiceEnum.values();
        PolicyStatusEnum[] statuses = PolicyStatusEnum.values();

        // Create policies spread across Jan 2024 - Nov 2025
        LocalDate jan2024 = LocalDate.of(2024, 1, 1);
        LocalDate nov2025 = LocalDate.of(2025, 11, 30);
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(jan2024, nov2025);

        for (int i = 0; i < 50; i++) {
            Policy policy = new Policy();
            policy.setId("POL" + (i + 1));
            policy.setBookingId("BOOK" + faker.number().numberBetween(10000, 99999));
            policy.setUserId("USER" + faker.number().numberBetween(1000, 9999));
            
            // Random date between Jan 2024 - Nov 2025
            LocalDate startDate = jan2024.plusDays(faker.number().numberBetween(0, (int) daysBetween));
            policy.setStartDate(startDate);
            
            policy.setStatus(statuses[random.nextInt(statuses.length)]);
            policy.setService(services[random.nextInt(services.length)]);
            
            policy.setTotalPrice(faker.number().numberBetween(100000, 5000000));
            policy.setTotalCoverage(faker.number().numberBetween(5000000, 100000000));
            
            LocalDateTime createdAt = startDate.atTime(faker.number().numberBetween(8, 18), 
                                                       faker.number().numberBetween(0, 59));
            policy.setCreatedAt(createdAt);
            policy.setUpdatedAt(createdAt);
            
            policies.add(policy);
        }
        
        policyRepository.saveAll(policies);
        System.out.println("✅ Created " + policies.size() + " policies spread across Jan 2024 - Nov 2025");
    }

    private void seedOrderedPlans() {
        System.out.println("📦 Seeding Ordered Plans...");
        
        List<Policy> allPolicies = policyRepository.findAll();
        List<InsurancePlan> allPlans = insurancePlanRepository.findAll();
        
        List<OrderedPlan> orderedPlans = new ArrayList<>();
        OrderedPlanStatusEnum[] statuses = OrderedPlanStatusEnum.values();
        
        int orderedPlanCounter = 1;
        
        // Create 1-3 ordered plans per policy
        for (Policy policy : allPolicies) {
            int numOrderedPlans = random.nextInt(3) + 1;
            
            for (int i = 0; i < numOrderedPlans; i++) {
                OrderedPlan orderedPlan = new OrderedPlan();
                orderedPlan.setId(policy.getId() + "-OP" + (i + 1));
                orderedPlan.setPolicy(policy);
                
                // Random insurance plan
                InsurancePlan plan = allPlans.get(random.nextInt(allPlans.size()));
                orderedPlan.setInsurancePlan(plan);
                
                orderedPlan.setStatus(statuses[random.nextInt(statuses.length)]);
                
                LocalDate expiredDate = policy.getStartDate().plusDays(plan.getExpiredByDays());
                orderedPlan.setExpiredDate(expiredDate);
                
                orderedPlan.setCreatedAt(policy.getCreatedAt());
                orderedPlan.setUpdatedAt(policy.getCreatedAt());
                
                orderedPlans.add(orderedPlan);
                orderedPlanCounter++;
            }
        }
        
        orderedPlanRepository.saveAll(orderedPlans);
        System.out.println("✅ Created " + orderedPlans.size() + " ordered plans");
    }

    private void seedClaims() {
        System.out.println("🔔 Seeding Claims...");
        
        List<OrderedPlan> allOrderedPlans = orderedPlanRepository.findAll();
        List<Claim> claims = new ArrayList<>();
        ClaimStatusEnum[] statuses = ClaimStatusEnum.values();
        
        String[] proofTypes = {
            "Medical Report - " + faker.file().fileName(),
            "Receipt - " + faker.file().fileName(),
            "Invoice - " + faker.file().fileName(),
            "Police Report - " + faker.file().fileName(),
            "Hospital Documentation - " + faker.file().fileName(),
            "Photo Evidence - " + faker.file().fileName(),
            "Flight Cancellation Notice - " + faker.file().fileName(),
            "Hotel Cancellation - " + faker.file().fileName()
        };
        
        String[] rejectionReasons = {
            "Incomplete Documentation",
            "Policy Expired",
            "Out of Coverage Area",
            "Pre-existing Condition",
            "Fraudulent Claim",
            "Missing Required Proof",
            "Claim Amount Exceeds Limit",
            "Service Not Covered"
        };
        
        String[] rejectionDescriptions = {
            "The submitted documents are incomplete and do not meet the minimum requirements for claim processing.",
            "Your insurance policy had expired before the incident occurred. Claims must be filed within the policy validity period.",
            "The incident occurred in a location not covered by your insurance policy.",
            "The claim relates to a pre-existing condition that was not disclosed during policy purchase.",
            "Investigation revealed inconsistencies in the claim documentation that suggest fraudulent activity.",
            "Required documentation such as medical reports or police reports were not provided.",
            "The claimed amount exceeds the maximum coverage limit specified in your policy.",
            "The service or incident type is not covered under your current insurance plan."
        };
        
        // Create 0-2 claims per ordered plan (not all ordered plans have claims)
        for (OrderedPlan orderedPlan : allOrderedPlans) {
            // 60% chance to have claims
            if (random.nextDouble() < 0.6) {
                int numClaims = random.nextInt(2) + 1;
                
                for (int i = 0; i < numClaims; i++) {
                    Claim claim = new Claim();
                    claim.setId(orderedPlan.getId() + "-CLM" + (i + 1));
                    claim.setOrderedPlan(orderedPlan);
                    
                    // Random status
                    ClaimStatusEnum status = statuses[random.nextInt(statuses.length)];
                    claim.setStatus(status);
                    claim.setProof(proofTypes[random.nextInt(proofTypes.length)]);
                    
                    LocalDateTime claimDate = orderedPlan.getCreatedAt()
                        .plusDays(faker.number().numberBetween(1, 30));
                    claim.setCreatedAt(claimDate);
                    claim.setUpdatedAt(claimDate);
                    
                    // Add rejection details if status is REJECTED
                    if (status == ClaimStatusEnum.REJECTED) {
                        int reasonIndex = random.nextInt(rejectionReasons.length);
                        claim.setRejectionReason(rejectionReasons[reasonIndex]);
                        claim.setRejectionDescription(rejectionDescriptions[reasonIndex]);
                        claim.setRejectionTimestamp(claimDate.plusDays(faker.number().numberBetween(1, 5)));
                    }
                    
                    // Add acceptance details if status is ACCEPTED
                    if (status == ClaimStatusEnum.ACCEPTED) {
                        String[] acceptedNotes = {
                            "All documentation verified and approved. Payment will be processed within 3-5 business days.",
                            "Claim approved after thorough review. Coverage amount will be credited to your account.",
                            "Your claim has been successfully processed and approved for payment.",
                            "After careful evaluation, your claim meets all requirements and is approved.",
                            "Documentation reviewed and claim approved. Please allow 5-7 business days for payment processing."
                        };
                        claim.setAcceptedNote(acceptedNotes[random.nextInt(acceptedNotes.length)]);
                        claim.setAcceptedTimestamp(claimDate.plusDays(faker.number().numberBetween(1, 3)));
                    }
                    
                    claims.add(claim);
                }
            }
        }
        
        claimRepository.saveAll(claims);
        System.out.println("✅ Created " + claims.size() + " claims");
    }
}
