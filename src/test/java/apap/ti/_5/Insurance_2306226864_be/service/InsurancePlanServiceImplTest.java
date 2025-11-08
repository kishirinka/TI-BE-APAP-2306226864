package apap.ti._5.Insurance_2306226864_be.service;

import apap.ti._5.Insurance_2306226864_be.restdto.request.insuranceplan.CreateInsurancePlanRequestDTO;
import apap.ti._5.Insurance_2306226864_be.restdto.request.insuranceplan.UpdateInsurancePlanRequestDTO;
import apap.ti._5.Insurance_2306226864_be.restdto.response.insuranceplan.InsurancePlanResponseDTO;
import apap.ti._5.Insurance_2306226864_be.enums.ServiceEnum;
import apap.ti._5.Insurance_2306226864_be.model.InsurancePlan;
import apap.ti._5.Insurance_2306226864_be.repository.InsurancePlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InsurancePlanServiceImplTest {
    
    @Mock
    private InsurancePlanRepository insurancePlanRepository;
    
    @InjectMocks
    private InsurancePlanServiceImpl insurancePlanService;
    
    private InsurancePlan testPlan;
    private CreateInsurancePlanRequestDTO createDTO;
    private UpdateInsurancePlanRequestDTO updateDTO;
    
    @BeforeEach
    void setUp() {
        // Setup test entity
        testPlan = new InsurancePlan();
        testPlan.setId("INS1");
        testPlan.setProviderId("PROVIDER001");
        testPlan.setPlanName("Test Plan");
        testPlan.setPrice(100000);
        testPlan.setCoverage(500000);
        testPlan.setCoverageDetails("Test coverage");
        testPlan.setApplicableService(Arrays.asList(ServiceEnum.FLIGHT));
        testPlan.setExpiredByDays(30);
        testPlan.setCreatedAt(LocalDateTime.now());
        testPlan.setUpdatedAt(LocalDateTime.now());

        // Setup create DTO
        createDTO = new CreateInsurancePlanRequestDTO();
        createDTO.setPlanName("Test Plan");
        createDTO.setProviderId("PROVIDER001");
        createDTO.setPrice(100000);
        createDTO.setCoverage(500000);
        createDTO.setCoverageDetails("Test coverage");
        createDTO.setApplicableService(Arrays.asList(ServiceEnum.FLIGHT));
        createDTO.setExpiredByDays(30);

        // Setup update DTO
        updateDTO = new UpdateInsurancePlanRequestDTO();
        updateDTO.setPlanName("Updated Plan");
        updateDTO.setPrice(150000);
        updateDTO.setCoverage(600000);
        updateDTO.setCoverageDetails("Updated coverage");
        updateDTO.setApplicableService(Arrays.asList(ServiceEnum.FLIGHT));
        updateDTO.setExpiredByDays(45);
        createDTO.setExpiredByDays(30);

        // Setup update DTO
        updateDTO = new UpdateInsurancePlanRequestDTO();
        updateDTO.setPlanName("Updated Plan");
        updateDTO.setPrice(150000);
        updateDTO.setCoverage(750000);
        updateDTO.setCoverageDetails("Updated coverage");
        updateDTO.setApplicableService(Arrays.asList(ServiceEnum.FLIGHT));
        updateDTO.setExpiredByDays(45);
    }
    
    // @Test
    // void testGetAllInsurancePlans_Success() {
    //     // Given
    //     List<InsurancePlan> plans = Arrays.asList(testPlan);
    //     Page<InsurancePlan> pagedPlans = new PageImpl<>(plans);
    //     when(insurancePlanRepository.findAll(any(PageRequest.class)))
    //         .thenReturn(pagedPlans);
        
    //     // When
    //     List<InsurancePlanResponseDTO> result = insurancePlanService.getAllInsurancePlans(PageRequest.of(0, 10));
        
    //     // Then
    //     assertNotNull(result);
    //     assertEquals(1, result.size());
    //     assertEquals("Test Plan", result.get(0).getPlanName());
    //     verify(insurancePlanRepository).findAll(any(PageRequest.class));
    // }
    
    @Test
    void testGetInsurancePlanById_Success() {
        // Given
        when(insurancePlanRepository.findByIdAndDeletedAtIsNull("INS1"))
            .thenReturn(Optional.of(testPlan));
        
        // When
        InsurancePlanResponseDTO result = insurancePlanService.getInsurancePlanById("INS1");
        
        // Then
        assertNotNull(result);
        assertEquals("INS1", result.getId());
        assertEquals("Test Plan", result.getPlanName());
        verify(insurancePlanRepository).findByIdAndDeletedAtIsNull("INS1");
    }
    
    @Test
    void testGetInsurancePlanById_NotExists() {
        // Given
        when(insurancePlanRepository.findByIdAndDeletedAtIsNull("NONEXISTENT"))
            .thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> 
            insurancePlanService.getInsurancePlanById("NONEXISTENT"));
        verify(insurancePlanRepository).findByIdAndDeletedAtIsNull("NONEXISTENT");
    }
    
    @Test
    void testCreateInsurancePlan_Success() {
        // Given
        when(insurancePlanRepository.save(any(InsurancePlan.class))).thenReturn(testPlan);
        
        // When
        InsurancePlanResponseDTO result = insurancePlanService.createInsurancePlan(createDTO);
        
        // Then
        assertNotNull(result);
        assertEquals("Test Plan", result.getPlanName());
        verify(insurancePlanRepository).save(any(InsurancePlan.class));
    }
    
    @Test
    void testUpdateInsurancePlan_Success() {
        // Given
        updateDTO = new UpdateInsurancePlanRequestDTO();
        updateDTO.setPlanName("Updated Plan");
        updateDTO.setPrice(150000);
        
        when(insurancePlanRepository.findByIdAndDeletedAtIsNull("INS1"))
            .thenReturn(Optional.of(testPlan));
        when(insurancePlanRepository.save(any(InsurancePlan.class)))
            .thenReturn(testPlan);
            
        // When
        InsurancePlanResponseDTO result = insurancePlanService.updateInsurancePlan("INS1", updateDTO);
        
        // Then
        assertNotNull(result);
        assertEquals("Updated Plan", result.getPlanName());
        assertEquals(150000, result.getPrice());
        verify(insurancePlanRepository).findByIdAndDeletedAtIsNull("INS1");
        verify(insurancePlanRepository).save(any(InsurancePlan.class));
    }
    
    @Test
    void testUpdateInsurancePlan_NotFound() {
        // Given
        when(insurancePlanRepository.findByIdAndDeletedAtIsNull("NONEXISTENT"))
            .thenReturn(Optional.empty());
            
        // When & Then
        assertThrows(RuntimeException.class, () -> 
            insurancePlanService.updateInsurancePlan("NONEXISTENT", updateDTO));
        verify(insurancePlanRepository).findByIdAndDeletedAtIsNull("NONEXISTENT");
        verify(insurancePlanRepository, never()).save(any(InsurancePlan.class));
    }
    
    @Test
    void testDeleteInsurancePlan_Success() {
        // Given
        when(insurancePlanRepository.findByIdAndDeletedAtIsNull("INS1"))
            .thenReturn(Optional.of(testPlan));
        when(insurancePlanRepository.save(any(InsurancePlan.class)))
            .thenReturn(testPlan);
            
        // When
        insurancePlanService.deleteInsurancePlan("INS1");
        
        // Then
        assertNotNull(testPlan.getDeletedAt());
        verify(insurancePlanRepository).findByIdAndDeletedAtIsNull("INS1");
        verify(insurancePlanRepository).save(testPlan);
    }
    
    @Test
    void testDeleteInsurancePlan_NotFound() {
        // Given
        when(insurancePlanRepository.findByIdAndDeletedAtIsNull("NONEXISTENT"))
            .thenReturn(Optional.empty());
            
        // When & Then
        assertThrows(RuntimeException.class, () -> 
            insurancePlanService.deleteInsurancePlan("NONEXISTENT"));
        verify(insurancePlanRepository).findByIdAndDeletedAtIsNull("NONEXISTENT");
        verify(insurancePlanRepository, never()).save(any(InsurancePlan.class));
    }
    

}