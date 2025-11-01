import { ref } from 'vue';
import { defineStore } from 'pinia';
import axios from 'axios';
import { toast } from 'vue-sonner';
import type { CommonResponseInterface } from '@/interfaces/common.interface';

const API_BASE_URL = `${import.meta.env.VITE_API_URL}/api`;

interface HomeStatistics {
  insurancePlans: number;
  policies: number;
  claims: number;
}

export const useStatisticsStore = defineStore('statistics', () => {
  // State
  const statistics = ref<Map<string, number>>(new Map());
  const homeStats = ref<HomeStatistics>({
    insurancePlans: 0,
    policies: 0,
    claims: 0
  });
  const loading = ref(false);
  const error = ref<string | null>(null);

  // Actions
  const fetchInsurancePlanStatistics = async (service: string, months: number) => {
    loading.value = true;
    error.value = null;
    
    try {
      const params: Record<string, string | number> = {
        months
      };
      
      // Only add service param if not "ALL"
      if (service && service !== 'ALL') {
        params.service = service;
      }
      
      const response = await axios.get<CommonResponseInterface<Record<string, number>>>(
        `${API_BASE_URL}/statistics/insurance-plans`,
        { params }
      );
      
      if (response.data.status === 'success' && response.data.data) {
        // Convert object to Map
        const statsMap = new Map(Object.entries(response.data.data));
        statistics.value = statsMap;
      } else {
        throw new Error(response.data.message || 'Failed to fetch statistics');
      }
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to fetch statistics';
      error.value = errorMessage;
      toast.error(errorMessage);
      statistics.value = new Map();
    } finally {
      loading.value = false;
    }
  };

  const fetchHomeStatistics = async () => {
    loading.value = true;
    error.value = null;
    
    try {
      const response = await axios.get<CommonResponseInterface<HomeStatistics>>(
        `${API_BASE_URL}/statistics/homepage`
      );
      
      if (response.data.status === 'success' && response.data.data) {
        homeStats.value = response.data.data;
      } else {
        throw new Error(response.data.message || 'Failed to fetch home statistics');
      }
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to fetch home statistics';
      error.value = errorMessage;
      toast.error(errorMessage);
      homeStats.value = {
        insurancePlans: 0,
        policies: 0,
        claims: 0
      };
    } finally {
      loading.value = false;
    }
  };

  return {
    // State
    statistics,
    homeStats,
    loading,
    error,
    
    // Actions
    fetchInsurancePlanStatistics,
    fetchHomeStatistics
  };
});
