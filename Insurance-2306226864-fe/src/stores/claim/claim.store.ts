import { defineStore } from 'pinia';
import { ref } from 'vue';
import axios, { type AxiosError } from 'axios';
import { toast } from 'vue-sonner';
import type { Claim, CreateClaimRequest, ProcessClaimRequest } from '@/interfaces/claim.interface';
import type { CommonResponseInterface } from '@/interfaces/common.response.interface';

const API_BASE_URL = `${import.meta.env.VITE_API_URL}/claims`;

export const useClaimStore = defineStore('claim', () => {
  const claims = ref<Claim[]>([]);
  const currentClaim = ref<Claim | null>(null);
  const loading = ref(false);
  const error = ref<string | null>(null);

  /**
   * Fetch all claims with optional filters
   * @param statusFilter - Optional status filter (e.g., 'WAITING_FOR_REVIEW', 'ACCEPTED', 'REJECTED')
   * @param insurancePlanFilter - Optional insurance plan ID filter
   */
  const fetchClaims = async (statusFilter?: string, insurancePlanFilter?: string) => {
    loading.value = true;
    error.value = null;
    try {
      // Build query params
      const params: Record<string, string> = {};
      if (statusFilter) {
        params.status = statusFilter;
      }
      if (insurancePlanFilter) {
        params.insurancePlanId = insurancePlanFilter;
      }

      const response = await axios.get<CommonResponseInterface<Claim[]>>(API_BASE_URL, { params });
      claims.value = response.data.data;
      return { success: true, data: response.data.data };
    } catch (err) {
      const axiosError = err as AxiosError<CommonResponseInterface<null>>;
      error.value = axiosError.response?.data?.message || 'Failed to fetch claims';
      toast.error(error.value);
      return { success: false, error: error.value };
    } finally {
      loading.value = false;
    }
  };

  /**
   * Fetch claim by ID
   * @param id - Claim ID
   */
  const fetchClaimById = async (id: string) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await axios.get<CommonResponseInterface<Claim>>(`${API_BASE_URL}/${id}`);
      currentClaim.value = response.data.data;
      return { success: true, data: response.data.data };
    } catch (err) {
      const axiosError = err as AxiosError<CommonResponseInterface<null>>;
      error.value = axiosError.response?.data?.message || 'Failed to fetch claim';
      currentClaim.value = null;
      toast.error(error.value);
      return { success: false, error: error.value };
    } finally {
      loading.value = false;
    }
  };

  /**
   * Create a new claim
   * @param data - Claim creation data
   * @returns Created claim or null
   */
  const createClaim = async (data: CreateClaimRequest) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await axios.post<CommonResponseInterface<Claim>>(API_BASE_URL, data);
      const createdClaim = response.data.data;
      
      // Add to claims list
      claims.value.push(createdClaim);
      
      // Show success toast
      toast.success(response.data.message || 'Claim created successfully');
      
      // Return created claim for redirect
      return { success: true, data: createdClaim, message: response.data.message };
    } catch (err) {
      const axiosError = err as AxiosError<CommonResponseInterface<null>>;
      error.value = axiosError.response?.data?.message || 'Failed to create claim';
      toast.error(error.value);
      return { success: false, error: error.value, data: null };
    } finally {
      loading.value = false;
    }
  };

  /**
   * Process a claim (Accept or Reject)
   * @param id - Claim ID
   * @param data - Process claim data with action and required fields
   * @returns Success boolean
   */
  const processClaim = async (id: string, data: ProcessClaimRequest) => {
    loading.value = true;
    error.value = null;
    
    // Validate required fields based on action
    if (data.action === 'REJECT') {
      if (!data.rejectionReason || !data.rejectionDescription) {
        const validationError = 'Rejection reason and description are required when rejecting a claim';
        error.value = validationError;
        toast.error(validationError);
        loading.value = false;
        return { success: false, error: validationError };
      }
    }
    
    try {
      const response = await axios.put<CommonResponseInterface<Claim>>(`${API_BASE_URL}/${id}/process`, data);
      const processedClaim = response.data.data;
      
      // Update claim in list
      const index = claims.value.findIndex(claim => claim.id === id);
      if (index !== -1) {
        claims.value[index] = processedClaim;
      }
      
      // Update current claim if it's the same
      if (currentClaim.value && currentClaim.value.id === id) {
        currentClaim.value = processedClaim;
      }
      
      // Show success toast
      const actionText = data.action === 'ACCEPT' ? 'accepted' : 'rejected';
      toast.success(response.data.message || `Claim ${actionText} successfully`);
      
      return { success: true, data: processedClaim, message: response.data.message };
    } catch (err) {
      const axiosError = err as AxiosError<CommonResponseInterface<null>>;
      error.value = axiosError.response?.data?.message || 'Failed to process claim';
      toast.error(error.value);
      return { success: false, error: error.value };
    } finally {
      loading.value = false;
    }
  };

  return {
    claims,
    currentClaim,
    loading,
    error,
    fetchClaims,
    fetchClaimById,
    createClaim,
    processClaim
  };
});
