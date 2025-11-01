import { defineStore } from 'pinia';
import { ref } from 'vue';
import axios, { type AxiosError } from 'axios';
import { toast } from 'vue-sonner';
import type { OrderedPlan } from '@/interfaces/policy.interface';
import type { CommonResponseInterface } from '@/interfaces/common.response.interface';

const API_BASE_URL = `${import.meta.env.VITE_API_URL}/ordered-plans`;

export const useOrderedPlanStore = defineStore('orderedPlan', () => {
  const currentOrderedPlan = ref<OrderedPlan | null>(null);
  const loading = ref(false);
  const error = ref<string | null>(null);

  /**
   * Fetch ordered plan by ID
   * Includes insurance plan details and claims
   * @param id - Ordered Plan ID
   */
  const fetchOrderedPlanById = async (id: string) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await axios.get<CommonResponseInterface<OrderedPlan>>(`${API_BASE_URL}/${id}`);
      currentOrderedPlan.value = response.data.data;
      return { success: true, data: response.data.data };
    } catch (err) {
      const axiosError = err as AxiosError<CommonResponseInterface<null>>;
      error.value = axiosError.response?.data?.message || 'Failed to fetch ordered plan';
      currentOrderedPlan.value = null;
      toast.error(error.value);
      return { success: false, error: error.value };
    } finally {
      loading.value = false;
    }
  };

  return {
    currentOrderedPlan,
    loading,
    error,
    fetchOrderedPlanById
  };
});
