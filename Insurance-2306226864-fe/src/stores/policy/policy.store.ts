import { defineStore } from 'pinia';
import { ref } from 'vue';
import axios, { type AxiosError } from 'axios';
import { toast } from 'vue-sonner';
import type { Policy, CreatePolicyRequest } from '@/interfaces/policy.interface';
import type { CommonResponseInterface } from '@/interfaces/common.response.interface';

const API_BASE_URL = `${import.meta.env.VITE_API_URL}/policies`;

export const usePolicyStore = defineStore('policy', () => {
  const policies = ref<Policy[]>([]);
  const currentPolicy = ref<Policy | null>(null);
  const loading = ref(false);
  const error = ref<string | null>(null);

  /**
   * Fetch all policies
   * This will automatically update expired policies in backend
   */
  const fetchPolicies = async () => {
    loading.value = true;
    error.value = null;
    try {
      const response = await axios.get<CommonResponseInterface<Policy[]>>(API_BASE_URL);
      policies.value = response.data.data;
      return { success: true, data: response.data.data };
    } catch (err) {
      const axiosError = err as AxiosError<CommonResponseInterface<null>>;
      error.value = axiosError.response?.data?.message || 'Failed to fetch policies';
      toast.error(error.value);
      return { success: false, error: error.value };
    } finally {
      loading.value = false;
    }
  };

  /**
   * Fetch policy by ID
   * @param id - Policy ID
   */
  const fetchPolicyById = async (id: string) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await axios.get<CommonResponseInterface<Policy>>(`${API_BASE_URL}/${id}`);
      currentPolicy.value = response.data.data;
      return { success: true, data: response.data.data };
    } catch (err) {
      const axiosError = err as AxiosError<CommonResponseInterface<null>>;
      error.value = axiosError.response?.data?.message || 'Failed to fetch policy';
      currentPolicy.value = null;
      toast.error(error.value);
      return { success: false, error: error.value };
    } finally {
      loading.value = false;
    }
  };

  /**
   * Create a new policy
   * @param data - Policy creation data
   * @returns Created policy or null
   */
  const createPolicy = async (data: CreatePolicyRequest) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await axios.post<CommonResponseInterface<Policy>>(API_BASE_URL, data);
      const createdPolicy = response.data.data;
      
      // Add to policies list
      policies.value.push(createdPolicy);
      
      // Show success toast
      toast.success(response.data.message || 'Policy created successfully');
      
      // Return created policy for redirect
      return { success: true, data: createdPolicy, message: response.data.message };
    } catch (err) {
      const axiosError = err as AxiosError<CommonResponseInterface<null>>;
      error.value = axiosError.response?.data?.message || 'Failed to create policy';
      toast.error(error.value);
      return { success: false, error: error.value, data: null };
    } finally {
      loading.value = false;
    }
  };

  /**
   * Pay for a policy
   * This will update the policy status and ordered plans status
   * @param id - Policy ID
   * @returns Success boolean
   */
  const payPolicy = async (id: string) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await axios.put<CommonResponseInterface<Policy>>(`${API_BASE_URL}/${id}/pay`);
      const updatedPolicy = response.data.data;
      
      // Update policy in list
      const index = policies.value.findIndex(policy => policy.id === id);
      if (index !== -1) {
        policies.value[index] = updatedPolicy;
      }
      
      // Update current policy if it's the same
      if (currentPolicy.value && currentPolicy.value.id === id) {
        currentPolicy.value = updatedPolicy;
      }
      
      // Show success toast
      toast.success(response.data.message || 'Policy payment successful');
      
      return { success: true, data: updatedPolicy, message: response.data.message };
    } catch (err) {
      const axiosError = err as AxiosError<CommonResponseInterface<null>>;
      error.value = axiosError.response?.data?.message || 'Failed to pay policy';
      toast.error(error.value);
      return { success: false, error: error.value };
    } finally {
      loading.value = false;
    }
  };

  return {
    policies,
    currentPolicy,
    loading,
    error,
    fetchPolicies,
    fetchPolicyById,
    createPolicy,
    payPolicy
  };
});
