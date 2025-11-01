<template>
  <div class="bg-white rounded-lg shadow-sm border border-gray-200">
    <form @submit.prevent="handleSubmit" class="p-8 space-y-6">
      <!-- Insurance Plan ID (hanya untuk update) -->
      <div v-if="mode === 'update'">
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Insurance Plan ID <span class="text-red-500">*</span>
        </label>
        <input
          v-model="form.id"
          type="text"
          readonly
          class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-500 cursor-not-allowed"
        >
        <p class="text-xs text-gray-500 mt-1">Insurance Plan ID cannot be changed</p>
      </div>

      <!-- Provider ID dan Plan Name -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- Provider ID -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Provider ID <span class="text-red-500">*</span>
          </label>
          <input
            v-model="form.providerId"
            type="text"
            required
            :readonly="mode === 'update'"
            :class="mode === 'update' 
              ? 'w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-500 cursor-not-allowed'
              : 'w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500'"
            placeholder="Enter provider ID"
          >
          <p v-if="mode === 'update'" class="text-xs text-gray-500 mt-1">Provider ID cannot be changed</p>
        </div>

        <!-- Plan Name -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Plan Name <span class="text-red-500">*</span>
          </label>
          <input
            v-model="form.planName"
            type="text"
            required
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            placeholder="Enter plan name"
          >
        </div>
      </div>

      <!-- Price dan Coverage -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- Price -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Price (IDR) <span class="text-red-500">*</span>
          </label>
          <input
            v-model.number="form.price"
            type="number"
            required
            min="0"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            placeholder="0"
          >
        </div>

        <!-- Coverage -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Coverage (IDR) <span class="text-red-500">*</span>
          </label>
          <input
            v-model.number="form.coverage"
            type="number"
            required
            min="0"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            placeholder="0"
          >
        </div>
      </div>

      <!-- Coverage Details -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Coverage Details <span class="text-red-500">*</span>
        </label>
        <textarea
          v-model="form.coverageDetails"
          required
          rows="4"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          placeholder="Describe what this insurance plan covers..."
        ></textarea>
      </div>

      <!-- Expired By Days -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Expired By Days <span class="text-red-500">*</span>
        </label>
        <input
          v-model.number="form.expiredByDays"
          type="number"
          required
          min="1"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          placeholder="1"
        >
      </div>

      <!-- Applicable Services -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-3">
          Applicable Services <span class="text-red-500">*</span>
        </label>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <label 
            v-for="service in availableServices" 
            :key="service.value"
            class="flex items-center space-x-2 cursor-pointer"
          >
            <input
              v-model="form.applicableService"
              :value="service.value"
              type="checkbox"
              class="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
            >
            <span class="text-sm text-gray-700">{{ service.label }}</span>
          </label>
        </div>
        <p v-if="form.applicableService.length === 0" class="text-xs text-red-500 mt-1">
          Please select at least one service
        </p>
      </div>

      <!-- Action Buttons -->
      <div class="flex justify-end gap-4 pt-6 border-t border-gray-200">
        <button
          type="button"
          @click="$emit('cancel')"
          class="px-6 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 font-medium"
        >
          Cancel
        </button>
        <button
          type="submit"
          :disabled="submitting || form.applicableService.length === 0"
          class="px-6 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white rounded-lg font-medium transition-colors"
        >
          {{ submitting ? (mode === 'create' ? 'Creating...' : 'Updating...') : (mode === 'create' ? 'Create Insurance Plan' : 'Update Insurance Plan') }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import type { InsurancePlanRequest, ServiceEnum } from '@/interfaces/insurance'

interface Props {
  mode: 'create' | 'update'
  initialData?: Partial<InsurancePlanRequest & { id: string; providerId: string }>
  submitting?: boolean
}

interface Emits {
  (e: 'submit', data: InsurancePlanRequest): void
  (e: 'cancel'): void
}

const props = withDefaults(defineProps<Props>(), {
  submitting: false
})

const emit = defineEmits<Emits>()

// Form data
const form = ref<InsurancePlanRequest & { id?: string }>({
  planName: '',
  providerId: '',
  price: 0,
  coverage: 0,
  coverageDetails: '',
  applicableService: [],
  expiredByDays: 1
})

// Available services
const availableServices = [
  { value: 'ACCOMMODATION' as ServiceEnum, label: 'Accommodation' },
  { value: 'FLIGHT' as ServiceEnum, label: 'Flight' },
  { value: 'TOUR_PACKAGE' as ServiceEnum, label: 'Tour Package' },
  { value: 'RENTALS' as ServiceEnum, label: 'Rentals' }
]

// Watch for initial data changes
watch(() => props.initialData, (newData) => {
  if (newData) {
    form.value = { ...form.value, ...newData }
  }
}, { immediate: true, deep: true })

// Methods
const handleSubmit = () => {
  const submitData: InsurancePlanRequest = {
    planName: form.value.planName,
    providerId: form.value.providerId,
    price: form.value.price,
    coverage: form.value.coverage,
    coverageDetails: form.value.coverageDetails,
    applicableService: form.value.applicableService,
    expiredByDays: form.value.expiredByDays
  }
  
  emit('submit', submitData)
}
</script>