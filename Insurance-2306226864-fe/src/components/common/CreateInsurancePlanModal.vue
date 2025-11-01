<template>
  <div v-if="show" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg p-8 max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto">
      <!-- Header -->
      <div class="flex justify-between items-center mb-6">
        <div>
          <h2 class="text-2xl font-bold text-gray-900">Create Insurance Plan</h2>
          <p class="text-gray-600 mt-1">Add a new insurance plan to the system</p>
        </div>
        <button 
          @click="closeModal"
          class="text-gray-400 hover:text-gray-600 text-2xl"
        >
          ×
        </button>
      </div>

      <!-- Form -->
      <form @submit.prevent="submitForm" class="space-y-6">
        <!-- Provider ID and Plan Name Row -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Provider ID *
            </label>
            <input
              v-model="form.providerId"
              type="text"
              placeholder="Enter provider ID"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              :class="{ 'border-red-500': errors.providerId }"
              required
            >
            <p v-if="errors.providerId" class="text-red-500 text-sm mt-1">{{ errors.providerId }}</p>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Plan Name *
            </label>
            <input
              v-model="form.planName"
              type="text"
              placeholder="Enter plan name"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              :class="{ 'border-red-500': errors.planName }"
              required
            >
            <p v-if="errors.planName" class="text-red-500 text-sm mt-1">{{ errors.planName }}</p>
          </div>
        </div>

        <!-- Price and Coverage Row -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Price (IDR) *
            </label>
            <input
              v-model.number="form.price"
              type="number"
              placeholder="0"
              min="0"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              :class="{ 'border-red-500': errors.price }"
              required
            >
            <p v-if="errors.price" class="text-red-500 text-sm mt-1">{{ errors.price }}</p>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Coverage (IDR) *
            </label>
            <input
              v-model.number="form.coverage"
              type="number"
              placeholder="0"
              min="0"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              :class="{ 'border-red-500': errors.coverage }"
              required
            >
            <p v-if="errors.coverage" class="text-red-500 text-sm mt-1">{{ errors.coverage }}</p>
          </div>
        </div>

        <!-- Coverage Details -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Coverage Details *
          </label>
          <textarea
            v-model="form.coverageDetails"
            placeholder="Describe what this insurance plan covers..."
            rows="4"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            :class="{ 'border-red-500': errors.coverageDetails }"
            required
          ></textarea>
          <p v-if="errors.coverageDetails" class="text-red-500 text-sm mt-1">{{ errors.coverageDetails }}</p>
        </div>

        <!-- Expired by Days -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Expired by Days *
          </label>
          <input
            v-model.number="form.expiredByDays"
            type="number"
            placeholder="1"
            min="1"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            :class="{ 'border-red-500': errors.expiredByDays }"
            required
          >
          <p v-if="errors.expiredByDays" class="text-red-500 text-sm mt-1">{{ errors.expiredByDays }}</p>
        </div>

        <!-- Applicable Services -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Applicable Services *
          </label>
          <div class="grid grid-cols-2 md:grid-cols-3 gap-4">
            <label 
              v-for="service in availableServices" 
              :key="service.value"
              class="flex items-center space-x-3 cursor-pointer"
            >
              <input
                type="checkbox"
                :value="service.value"
                v-model="form.applicableService"
                class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
              >
              <span class="text-sm text-gray-700">{{ service.label }}</span>
            </label>
          </div>
          <p v-if="errors.applicableService" class="text-red-500 text-sm mt-1">{{ errors.applicableService }}</p>
        </div>

        <!-- Buttons -->
        <div class="flex justify-end space-x-4 pt-6 border-t border-gray-200">
          <button
            type="button"
            @click="closeModal"
            class="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 font-medium"
            :disabled="submitting"
          >
            Cancel
          </button>
          <button
            type="submit"
            class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-medium disabled:opacity-50 disabled:cursor-not-allowed"
            :disabled="submitting"
          >
            {{ submitting ? 'Creating...' : 'Create Insurance Plan' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ServiceEnum, type InsurancePlanRequest } from '@/interfaces/insurance'

interface Props {
  show: boolean
}

interface Emits {
  (e: 'close'): void
  (e: 'created', plan: InsurancePlanRequest): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// Form data
const form = reactive<InsurancePlanRequest>({
  planName: '',
  providerId: '',
  price: 0,
  coverage: 0,
  coverageDetails: '',
  applicableService: [],
  expiredByDays: 1
})

// Form validation errors
const errors = reactive({
  planName: '',
  providerId: '',
  price: '',
  coverage: '',
  coverageDetails: '',
  applicableService: '',
  expiredByDays: ''
})

const submitting = ref(false)

// Available services with labels
const availableServices = [
  { value: ServiceEnum.ACCOMMODATION, label: 'Accommodation' },
  { value: ServiceEnum.FLIGHT, label: 'Flight' },
  { value: ServiceEnum.TOUR_PACKAGE, label: 'Tour Package' },
  { value: ServiceEnum.RENTALS, label: 'Rentals' }
]

// Methods
const validateForm = (): boolean => {
  // Reset errors
  Object.keys(errors).forEach(key => {
    errors[key as keyof typeof errors] = ''
  })

  let isValid = true

  if (!form.planName.trim()) {
    errors.planName = 'Plan name is required'
    isValid = false
  }

  if (!form.providerId.trim()) {
    errors.providerId = 'Provider ID is required'
    isValid = false
  }

  if (!form.price || form.price <= 0) {
    errors.price = 'Price must be greater than 0'
    isValid = false
  }

  if (!form.coverage || form.coverage <= 0) {
    errors.coverage = 'Coverage must be greater than 0'
    isValid = false
  }

  if (!form.coverageDetails.trim()) {
    errors.coverageDetails = 'Coverage details are required'
    isValid = false
  }

  if (!form.expiredByDays || form.expiredByDays <= 0) {
    errors.expiredByDays = 'Expired by days must be greater than 0'
    isValid = false
  }

  if (form.applicableService.length === 0) {
    errors.applicableService = 'At least one service must be selected'
    isValid = false
  }

  return isValid
}

const resetForm = () => {
  form.planName = ''
  form.providerId = ''
  form.price = 0
  form.coverage = 0
  form.coverageDetails = ''
  form.applicableService = []
  form.expiredByDays = 1
  
  Object.keys(errors).forEach(key => {
    errors[key as keyof typeof errors] = ''
  })
}

const closeModal = () => {
  resetForm()
  emit('close')
}

const submitForm = async () => {
  if (!validateForm()) {
    return
  }

  submitting.value = true
  try {
    emit('created', { ...form })
    closeModal()
  } catch (error) {
    console.error('Error creating insurance plan:', error)
  } finally {
    submitting.value = false
  }
}

// Watch for modal close to reset form
watch(() => props.show, (newVal) => {
  if (!newVal) {
    resetForm()
  }
})
</script>

<style scoped>
/* Custom scrollbar for modal */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>