package apap.ti._5.Insurance_2306226864_be.model;

import apap.ti._5.Insurance_2306226864_be.enums.PolicyStatusEnum;
import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "policy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Policy {
    
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    
    @Column(name = "booking_id", nullable = false)
    private String bookingId;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PolicyStatusEnum status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "service", nullable = false)
    private ServiceEnum service;
    
    @Column(name = "total_coverage", nullable = false)
    private Integer totalCoverage;
    
    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;
    
    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderedPlan> orderedPlans;
    
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