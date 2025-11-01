package apap.ti._5.Insurance_2306226864_be.model;

import apap.ti._5.Insurance_2306226864_be.enums.OrderedPlanStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ordered_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderedPlan {
    
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderedPlanStatusEnum status;
    
    @Column(name = "expired_date", nullable = false)
    private LocalDate expiredDate;
    
    @ManyToOne
    @JoinColumn(name = "insurance_plan_id", referencedColumnName = "id")
    private InsurancePlan insurancePlan;
    
    @ManyToOne
    @JoinColumn(name = "policy_id", referencedColumnName = "id")
    private Policy policy;
    
    @OneToMany(mappedBy = "orderedPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Claim> claims;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
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