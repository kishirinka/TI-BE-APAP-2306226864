<template>
  <div v-if="show" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-lg max-w-4xl w-full max-h-[90vh] overflow-y-auto">
      <!-- Header -->
      <div class="sticky top-0 bg-white border-b border-gray-200 px-8 py-6">
        <div class="flex justify-between items-center">
          <div>
            <h2 class="text-2xl font-bold text-gray-900">Create Insurance Plan</h2>
            <p class="text-gray-600 mt-1">Add a new insurance plan to the system</p>
          </div>
          <button 
            @click="$emit('close')"
            class="text-gray-400 hover:text-gray-600"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>
      </div>

      <!-- Form -->
      <div class="p-8">
        <InsurancePlanForm
          mode="create"
          :submitting="submitting"
          @submit="handleSubmit"
          @cancel="$emit('close')"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import InsurancePlanForm from '@/components/forms/InsurancePlanForm.vue'
import type { InsurancePlanRequest } from '@/interfaces/insurance'

interface Props {
  show: boolean
}

interface Emits {
  (e: 'close'): void
  (e: 'created', plan: InsurancePlanRequest): void
}

defineProps<Props>()
const emit = defineEmits<Emits>()

const submitting = ref(false)

const handleSubmit = async (plan: InsurancePlanRequest) => {
  submitting.value = true
  try {
    emit('created', plan)
  } finally {
    submitting.value = false
  }
}
</script>