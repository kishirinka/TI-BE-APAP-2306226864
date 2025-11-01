import { defineStore } from 'pinia';
import { ref } from 'vue';
import axios, { type AxiosError } from 'axios';
import { toast } from 'vue-sonner';
import type { 
  InsurancePlan, 
  CreateInsurancePlanRequest, 
  UpdateInsurancePlanRequest 
} from '@/interfaces/insurancePlan.interface';
import type { CommonResponseInterface } from '@/interfaces/common.response.interface';

const API_BASE_URL = `${import.meta.env.VITE_API_URL}/insurance-plans`;

export const useInsurancePlanStore = defineStore('insurancePlan', () => {
  const insurancePlans = ref<InsurancePlan[]>([]);
  const currentPlan = ref<InsurancePlan | null>(null);
  const loading = ref(false);
  const error = ref<string | null>(null);

  const fetchInsurancePlans = async () => {
    loading.value = true;
    error.value = null;
    try {
      const response = await axios.get<CommonResponseInterface<InsurancePlan[]>>(API_BASE_URL);
      insurancePlans.value = response.data.data;
      return { success: true, data: response.data.data };
    } catch (err) {
      const axiosError = err as AxiosError<CommonResponseInterface<null>>;
      error.value = axiosError.response?.data?.message || 'Failed to fetch insurance plans';
      toast.error(error.value);
      return { success: false, error: error.value };
    } finally {
      loading.value = false;
    }
  };

  const fetchInsurancePlanById = async (id: string) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await axios.get<CommonResponseInterface<InsurancePlan>>(`${API_BASE_URL}/${id}`);
      currentPlan.value = response.data.data;
      return { success: true, data: response.data.data };
    } catch (err) {
      const axiosError = err as AxiosError<CommonResponseInterface<null>>;
      error.value = axiosError.response?.data?.message || 'Failed to fetch insurance plan';
      currentPlan.value = null;
      toast.error(error.value);
      return { success: false, error: error.value };
    } finally {
      loading.value = false;
    }
  };

  const createInsurancePlan = async (data: CreateInsurancePlanRequest) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await axios.post<CommonResponseInterface<InsurancePlan>>(API_BASE_URL, data);
      insurancePlans.value.push(response.data.data);
      toast.success(response.data.message || 'Insurance plan created successfully');
      return { success: true, data: response.data.data, message: response.data.message };
    } catch (err) {
      const axiosError = err as AxiosError<CommonResponseInterface<null>>;
      error.value = axiosError.response?.data?.message || 'Failed to create insurance plan';
      toast.error(error.value);
      return { success: false, error: error.value };
    } finally {
      loading.value = false;
    }
  };

  const updateInsurancePlan = async (id: string, data: UpdateInsurancePlanRequest) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await axios.put<CommonResponseInterface<InsurancePlan>>(`${API_BASE_URL}/${id}`, data);
      const index = insurancePlans.value.findIndex(plan => plan.id === id);
      if (index !== -1) {
        insurancePlans.value[index] = response.data.data;
      }
      if (currentPlan.value && currentPlan.value.id === id) {
        currentPlan.value = response.data.data;
      }
      toast.success(response.data.message || 'Insurance plan updated successfully');
      return { success: true, data: response.data.data, message: response.data.message };
    } catch (err) {
      const axiosError = err as AxiosError<CommonResponseInterface<null>>;
      error.value = axiosError.response?.data?.message || 'Failed to update insurance plan';
      toast.error(error.value);
      return { success: false, error: error.value };
    } finally {
      loading.value = false;
    }
  };

  const deleteInsurancePlan = async (id: string) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await axios.delete<CommonResponseInterface<boolean>>(`${API_BASE_URL}/${id}`);
      insurancePlans.value = insurancePlans.value.filter(plan => plan.id !== id);
      if (currentPlan.value && currentPlan.value.id === id) {
        currentPlan.value = null;
      }
      toast.success(response.data.message || 'Insurance plan deleted successfully');
      return { success: true, message: response.data.message };
    } catch (err) {
      const axiosError = err as AxiosError<CommonResponseInterface<null>>;
      error.value = axiosError.response?.data?.message || 'Failed to delete insurance plan';
      toast.error(error.value);
      return { success: false, error: error.value };
    } finally {
      loading.value = false;
    }
  };

  return {
    insurancePlans,
    currentPlan,
    loading,
    error,
    fetchInsurancePlans,
    fetchInsurancePlanById,
    createInsurancePlan,
    updateInsurancePlan,
    deleteInsurancePlan
  };
});
