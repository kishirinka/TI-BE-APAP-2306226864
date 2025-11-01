package apap.ti._5.Insurance_2306226864_be.model;

import apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "claim")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Claim {
    
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClaimStatusEnum status;
    
    @Column(name = "proof", nullable = false)
    private String proof;
    
    @Column(name = "rejection_reason")
    private String rejectionReason;
    
    @Column(name = "rejection_description")
    private String rejectionDescription;
    
    @Column(name = "rejection_timestamp")
    private LocalDateTime rejectionTimestamp;
    
    @Column(name = "accepted_note")
    private String acceptedNote;
    
    @Column(name = "accepted_timestamp")
    private LocalDateTime acceptedTimestamp;
    
    @ManyToOne
    @JoinColumn(name = "ordered_plan_id", referencedColumnName = "id")
    private OrderedPlan orderedPlan;
    
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