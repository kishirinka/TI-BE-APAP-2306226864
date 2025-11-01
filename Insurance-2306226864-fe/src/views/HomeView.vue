<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useStatisticsStore } from '@/stores/statistics.store';
import VButton from '@/components/common/VButton.vue';

// Router
const router = useRouter();

// Store
const statisticsStore = useStatisticsStore();

// Computed
const homeStats = computed(() => statisticsStore.homeStats);
const loading = computed(() => statisticsStore.loading);
const error = computed(() => statisticsStore.error);

// Navigate to page
const navigateTo = (path: string) => {
  router.push(path);
};

// Lifecycle
onMounted(async () => {
  await statisticsStore.fetchHomeStatistics();
});
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navigation Bar -->
    <Vnavbar />

    <!-- Main Content -->
    <main class="max-w-7xl mx-auto pt-24 pb-16 px-4 sm:px-6 lg:px-8">
      <!-- Hero Section -->
      <div class="text-center mb-16">
        <h1 class="text-5xl font-bold text-gray-900 mb-4">
          Welcome to <span class="text-blue-600">Insurance</span>
        </h1>
        <p class="text-xl text-gray-600 max-w-2xl mx-auto">
          Your comprehensive travel insurance management platform
        </p>
      </div>

      <!-- Platform Statistics Section -->
      <div class="mb-16">
        <h2 class="text-3xl font-bold text-gray-900 text-center mb-12">
          Platform Statistics
        </h2>
        
        <!-- Loading State -->
        <div v-if="loading" class="flex justify-center">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="text-center text-red-600">
          <p>{{ error }}</p>
          <button @click="fetchStatistics" class="mt-2 text-blue-600 hover:underline">
            Try Again
          </button>
        </div>

        <!-- Statistics Cards -->
        <div v-else class="grid grid-cols-1 md:grid-cols-3 gap-8 max-w-5xl mx-auto">
          <!-- Insurance Plans Card -->
          <StatsCard
            title="Insurance Plans"
            :count="statistics.insurancePlans"
            description="Active insurance plans available"
            class="stats-card-blue"
          >
            <template #icon>
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect x="3" y="4" width="18" height="16" rx="2" stroke="currentColor" stroke-width="2"/>
                <path d="M7 8h6M7 12h8M7 16h4" stroke="currentColor" stroke-width="2"/>
              </svg>
            </template>
          </StatsCard>

          <!-- Insurance Policies Card -->
          <StatsCard
            title="Insurance Policies"
            :count="statistics.insurancePolicies"
            description="Total policies issued to travelers"
            class="stats-card-red"
          >
            <template #icon>
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2L2 7V12C2 17 6 21 12 22C18 21 22 17 22 12V7L12 2Z" stroke="currentColor" stroke-width="2" fill="none"/>
                <path d="M9 12L11 14L16 9" stroke="currentColor" stroke-width="2"/>
              </svg>
            </template>
          </StatsCard>

          <!-- Claims Processed Card -->
          <StatsCard
            title="Claims Processed"
            :count="statistics.processedClaims"
            description="Claims handled and resolved"
            class="stats-card-green"
          >
            <template #icon>
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M3 3v5h5M21 21v-5h-5" stroke="currentColor" stroke-width="2"/>
                <path d="M21 12A9 9 0 0 0 12 3a9 9 0 0 0-9 9 9 9 0 0 0 9 9" stroke="currentColor" stroke-width="2"/>
                <path d="M12 7v5l3 3" stroke="currentColor" stroke-width="2"/>
              </svg>
            </template>
          </StatsCard>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
/* Custom card color variants */
:deep(.stats-card-blue) {
  border-color: #3b82f6;
}

:deep(.stats-card-blue .stats-number) {
  color: #3b82f6;
}

:deep(.stats-card-blue .stats-card-icon) {
  background: #eff6ff;
  color: #3b82f6;
}

:deep(.stats-card-red) {
  border-color: #ef4444;
}

:deep(.stats-card-red .stats-number) {
  color: #ef4444;
}

:deep(.stats-card-red .stats-card-icon) {
  background: #fef2f2;
  color: #ef4444;
}

:deep(.stats-card-green) {
  border-color: #10b981;
}

:deep(.stats-card-green .stats-number) {
  color: #10b981;
}

:deep(.stats-card-green .stats-card-icon) {
  background: #ecfdf5;
  color: #10b981;
}

/* Animation for loading */
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.animate-spin {
  animation: spin 1s linear infinite;
}
</style>