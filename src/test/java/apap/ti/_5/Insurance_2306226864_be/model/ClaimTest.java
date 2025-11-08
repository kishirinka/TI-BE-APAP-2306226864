package apap.ti._5.Insurance_2306226864_be.model;

import apap.ti._5.Insurance_2306226864_be.enums.ClaimStatusEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ClaimTest {
    
    @Test
    void testCreateClaim_AllFields_Success() {
        // Given & When
        Claim claim = new Claim();
        claim.setId("CLM1");
        claim.setStatus(ClaimStatusEnum.WAITING_FOR_REVIEW);
        claim.setProof("Insurance claim proof document");
        claim.onCreate();
        
        OrderedPlan orderedPlan = new OrderedPlan();
        orderedPlan.setId("OP1");
        claim.setOrderedPlan(orderedPlan);
        
        // Then
        assertEquals("CLM1", claim.getId());
        assertEquals(ClaimStatusEnum.WAITING_FOR_REVIEW, claim.getStatus());
        assertEquals("Insurance claim proof document", claim.getProof());
        assertNotNull(claim.getOrderedPlan());
        assertEquals("OP1", claim.getOrderedPlan().getId());
        assertNotNull(claim.getCreatedAt());
        assertNotNull(claim.getUpdatedAt());
    }
    
    @Test
    void testClaim_AcceptedFields() {
        // Given
        Claim claim = new Claim();
        claim.setStatus(ClaimStatusEnum.ACCEPTED);
        
        LocalDateTime acceptedTime = LocalDateTime.now();
        claim.setAcceptedTimestamp(acceptedTime);
        claim.setAcceptedNote("Claim approved");
        
        // Then
        assertEquals(ClaimStatusEnum.ACCEPTED, claim.getStatus());
        assertEquals(acceptedTime, claim.getAcceptedTimestamp());
        assertEquals("Claim approved", claim.getAcceptedNote());
        assertNull(claim.getRejectionReason());
        assertNull(claim.getRejectionDescription());
    }
    
    @Test
    void testClaim_RejectedFields() {
        // Given
        Claim claim = new Claim();
        claim.setStatus(ClaimStatusEnum.REJECTED);
        
        LocalDateTime rejectedTime = LocalDateTime.now();
        claim.setRejectionTimestamp(rejectedTime);
        claim.setRejectionReason("Insufficient Evidence");
        claim.setRejectionDescription("The provided documents are not sufficient");
        
        // Then
        assertEquals(ClaimStatusEnum.REJECTED, claim.getStatus());
        assertEquals(rejectedTime, claim.getRejectionTimestamp());
        assertEquals("Insufficient Evidence", claim.getRejectionReason());
        assertEquals("The provided documents are not sufficient", claim.getRejectionDescription());
        assertNull(claim.getAcceptedNote());
        assertNull(claim.getAcceptedTimestamp());
    }
    
    @Test
    void testClaim_StatusTransitions() {
        // Given
        Claim claim = new Claim();
        
        // When & Then
        claim.setStatus(ClaimStatusEnum.WAITING_FOR_REVIEW);
        assertEquals(ClaimStatusEnum.WAITING_FOR_REVIEW, claim.getStatus());
        
        claim.setStatus(ClaimStatusEnum.ACCEPTED);
        assertEquals(ClaimStatusEnum.ACCEPTED, claim.getStatus());
        
        // Test rejection
        Claim claim2 = new Claim();
        claim2.setStatus(ClaimStatusEnum.WAITING_FOR_REVIEW);
        claim2.setStatus(ClaimStatusEnum.REJECTED);
        assertEquals(ClaimStatusEnum.REJECTED, claim2.getStatus());
    }
}