<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navigation Bar -->
    <Vnavbar />

    <!-- Main Content -->
    <main class="max-w-7xl mx-auto pt-24 pb-16 px-4 sm:px-6 lg:px-8">
      <!-- Header Section -->
      <div class="mb-8">
        <div class="flex justify-between items-center">
          <div>
            <h1 class="text-3xl font-bold text-gray-900">Insurance Plans</h1>
            <p class="text-gray-600 mt-2">Manage and monitor all insurance plans available for travelers</p>
          </div>
          <button 
            @click="showCreateModal = true"
            class="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-lg flex items-center gap-2 transition-colors"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
            </svg>
            Add New Plan
          </button>
        </div>
      </div>

      <!-- Filters and Search -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
        <div class="flex flex-col md:flex-row gap-4 items-center justify-between">
          <div class="flex gap-4 items-center">
            <div class="flex items-center gap-2">
              <label class="text-sm font-medium text-gray-700">Tampilkan:</label>
              <select v-model="pageSize" class="border border-gray-300 rounded px-3 py-1 text-sm">
                <option value="10">10</option>
                <option value="25">25</option>
                <option value="50">50</option>
              </select>
              <span class="text-sm text-gray-500">data per halaman</span>
            </div>
          </div>
          
          <div class="flex gap-4 items-center">
            <div class="relative">
              <input
                v-model="searchQuery"
                type="text"
                placeholder="Cari..."
                class="border border-gray-300 rounded-lg px-4 py-2 pr-10 w-64 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              >
              <svg class="w-5 h-5 text-gray-400 absolute right-3 top-2.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
              </svg>
            </div>
          </div>
        </div>
      </div>

      <!-- Table -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Provider ID</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Plan Name</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Price</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Coverage</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Applicable Services</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Duration</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-if="loading" v-for="n in 5" :key="n" class="animate-pulse">
                <td class="px-6 py-4"><div class="h-4 bg-gray-200 rounded w-16"></div></td>
                <td class="px-6 py-4"><div class="h-4 bg-gray-200 rounded w-20"></div></td>
                <td class="px-6 py-4"><div class="h-4 bg-gray-200 rounded w-32"></div></td>
                <td class="px-6 py-4"><div class="h-4 bg-gray-200 rounded w-24"></div></td>
                <td class="px-6 py-4"><div class="h-4 bg-gray-200 rounded w-24"></div></td>
                <td class="px-6 py-4"><div class="h-4 bg-gray-200 rounded w-16"></div></td>
                <td class="px-6 py-4"><div class="h-4 bg-gray-200 rounded w-16"></div></td>
                <td class="px-6 py-4"><div class="h-4 bg-gray-200 rounded w-20"></div></td>
              </tr>

              <tr v-else-if="error" class="text-center">
                <td colspan="8" class="px-6 py-8 text-red-500">
                  <div class="flex flex-col items-center gap-2">
                    <svg class="w-12 h-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                    </svg>
                    <p class="font-medium">{{ error }}</p>
                    <button @click="fetchPlans" class="text-blue-600 hover:text-blue-700 font-medium">Try Again</button>
                  </div>
                </td>
              </tr>

              <tr v-else-if="filteredPlans.length === 0" class="text-center">
                <td colspan="8" class="px-6 py-8 text-gray-500">
                  <div class="flex flex-col items-center gap-2">
                    <svg class="w-12 h-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                    </svg>
                    <p class="font-medium">No insurance plans found</p>
                    <p class="text-sm">Try adjusting your search or create a new plan</p>
                  </div>
                </td>
              </tr>

              <tr v-else v-for="plan in paginatedPlans" :key="plan.id" class="hover:bg-gray-50">
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ plan.id }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ plan.providerId }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ plan.planName }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ plan.price }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ plan.coverage }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  <div class="flex gap-1 flex-wrap">
                    <span 
                      v-for="service in plan.applicableService" 
                      :key="service"
                      class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800"
                    >
                      {{ service }}
                    </span>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ plan.expiredByDays }} days</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  <div class="flex gap-2">
                    <button 
                      @click="viewPlan(plan)"
                      class="text-blue-600 hover:text-blue-900"
                    >
                      View
                    </button>
                    <button 
                      @click="editPlan(plan)"
                      class="text-green-600 hover:text-green-900"
                    >
                      Edit
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <div v-if="!loading && !error && filteredPlans.length > 0" class="bg-white px-6 py-4 border-t border-gray-200 flex items-center justify-between">
          <div class="text-sm text-gray-700">
            Menampilkan {{ ((currentPage - 1) * pageSize) + 1 }} sampai {{ Math.min(currentPage * pageSize, filteredPlans.length) }} dari {{ filteredPlans.length }} data
          </div>
          <div class="flex gap-2">
            <button 
              @click="currentPage = Math.max(1, currentPage - 1)"
              :disabled="currentPage === 1"
              class="px-3 py-1 border border-gray-300 rounded text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
            >
              Previous
            </button>
            <span class="px-3 py-1 text-sm">{{ currentPage }}</span>
            <button 
              @click="currentPage = Math.min(totalPages, currentPage + 1)"
              :disabled="currentPage === totalPages"
              class="px-3 py-1 border border-gray-300 rounded text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
            >
              Next
            </button>
            <span class="px-3 py-1 text-sm text-gray-500">Selanjutnya</span>
            <span class="px-3 py-1 text-sm">Terakhir</span>
          </div>
        </div>
      </div>
    </main>

    <!-- Create Insurance Plan Modal -->
    <CreateInsurancePlanModal 
      :show="showCreateModal"
      @close="showCreateModal = false"
      @created="handleCreatePlan"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Vnavbar from '@/components/layout/Vnavbar.vue'
import CreateInsurancePlanModal from '@/components/insurance/CreateInsurancePlanModal.vue'
import { apiService } from '@/services/apiService'
import type { InsurancePlan, InsurancePlanRequest } from '@/interfaces/insurance'

const router = useRouter()

// Reactive state
const plans = ref<InsurancePlan[]>([])
const loading = ref(true)
const error = ref<string | null>(null)
const searchQuery = ref('')
const pageSize = ref(10)
const currentPage = ref(1)
const showCreateModal = ref(false)

// Computed properties
const filteredPlans = computed(() => {
  if (!searchQuery.value) return plans.value
  
  const query = searchQuery.value.toLowerCase()
  return plans.value.filter(plan => 
    plan.planName.toLowerCase().includes(query) ||
    plan.providerId.toLowerCase().includes(query) ||
    plan.applicableService.some(service => service.toLowerCase().includes(query))
  )
})

const totalPages = computed(() => Math.ceil(filteredPlans.value.length / pageSize.value))

const paginatedPlans = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredPlans.value.slice(start, end)
})

// Methods
const fetchPlans = async () => {
  try {
    loading.value = true
    error.value = null
    
    const data = await apiService.getInsurancePlans()
    plans.value = data
  } catch (err: any) {
    error.value = err.message || 'Failed to load insurance plans'
    console.error('Error fetching plans:', err)
  } finally {
    loading.value = false
  }
}

const viewPlan = (plan: InsurancePlan) => {
  // Navigate to detail page
  router.push(`/insurance-plans/${plan.id}`)
}

const editPlan = (plan: InsurancePlan) => {
  // TODO: Implement edit modal
  console.log('Edit plan:', plan)
}

const handleCreatePlan = async (newPlan: InsurancePlanRequest) => {
  try {
    const createdPlan = await apiService.createInsurancePlan(newPlan)
    showCreateModal.value = false
    // Refresh the list to get updated data
    await fetchPlans()
    // TODO: Show success toast/notification
  } catch (error) {
    console.error('Error creating plan:', error)
    // TODO: Show error toast/notification
  }
}

// Lifecycle
onMounted(() => {
  fetchPlans()
})
</script>

<style scoped>
/* Animation for loading */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}
</style>