package apap.ti._5.Insurance_2306226864_be.model;

import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "insurance_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InsurancePlan {
    
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    
    @Column(name = "provider_id", nullable = false)
    private String providerId;
    
    @Column(name = "plan_name", nullable = false)
    private String planName;
    
    @Column(name = "price", nullable = false)
    private Integer price;
    
    @Column(name = "coverage", nullable = false)
    private Integer coverage;
    
    @Column(name = "coverage_details", nullable = false, columnDefinition = "TEXT")
    private String coverageDetails;
    
    @ElementCollection(targetClass = ServiceEnum.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = "insurance_plan_applicable_service",
        joinColumns = @JoinColumn(name = "insurance_plan_id")
    )
    @Column(name = "applicable_service", nullable = false)
    private List<ServiceEnum> applicableService;
    
    @Column(name = "expired_by_days", nullable = false)
    private Integer expiredByDays;
    
    @OneToMany(mappedBy = "insurancePlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderedPlan> orderedPlans;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}