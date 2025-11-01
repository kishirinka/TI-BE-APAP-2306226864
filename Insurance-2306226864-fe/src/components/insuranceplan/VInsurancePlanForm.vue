<script setup lang="ts">
import { reactive, watch, onMounted, computed } from 'vue';
import VInput from '@/components/common/VInput.vue';
import VTextArea from '@/components/common/VTextArea.vue';
import VCheckbox from '@/components/common/VCheckbox.vue';
import { ServiceEnum, ServiceLabels } from '@/enums';
import type { 
  InsurancePlan, 
  InsurancePlanRequest
} from '@/interfaces/insurancePlan.interface';

interface Props {
  insurancePlan?: InsurancePlan | null;
  mode: 'create' | 'edit';
}

const props = withDefaults(defineProps<Props>(), {
  insurancePlan: null
});

const mode = computed(() => props.mode);

const emit = defineEmits<{
  submit: [formData: InsurancePlanRequest];
}>();

const formData = reactive({
  planName: '',
  providerId: '',
  price: 0,
  coverage: 0,
  coverageDetails: '',
  applicableService: [] as string[],
  expiredByDays: 365
});

const errors = reactive({
  planName: '',
  providerId: '',
  price: '',
  coverage: '',
  coverageDetails: '',
  applicableService: '',
  expiredByDays: ''
});

const serviceOptions = Object.values(ServiceEnum).map(value => ({
  value: value,
  label: ServiceLabels[value]
}));

const populateForm = () => {
  if (props.insurancePlan && props.mode === 'edit') {
    formData.planName = props.insurancePlan.planName ?? '';
    formData.providerId = props.insurancePlan.providerId ?? '';
    formData.price = props.insurancePlan.price ?? 0;
    formData.coverage = props.insurancePlan.coverage ?? 0;
    formData.coverageDetails = props.insurancePlan.coverageDetails ?? '';
    formData.applicableService = [...(props.insurancePlan.applicableService ?? [])];
    formData.expiredByDays = props.insurancePlan.expiredByDays ?? 365;
  }
};

const resetErrors = () => {
  errors.planName = '';
  errors.providerId = '';
  errors.price = '';
  errors.coverage = '';
  errors.coverageDetails = '';
  errors.applicableService = '';
  errors.expiredByDays = '';
};

const validateForm = (): boolean => {
  resetErrors();
  let isValid = true;

  if (!formData.planName.trim()) {
    errors.planName = 'Plan name is required';
    isValid = false;
  }

  if (props.mode === 'create' && !formData.providerId.trim()) {
    errors.providerId = 'Provider ID is required';
    isValid = false;
  }

  if (formData.price === null || formData.price === undefined) {
    errors.price = 'Price is required';
    isValid = false;
  } else if (formData.price < 0) {
    errors.price = 'Price must be at least 0';
    isValid = false;
  }

  if (formData.coverage === null || formData.coverage === undefined) {
    errors.coverage = 'Coverage is required';
    isValid = false;
  } else if (formData.coverage < 0) {
    errors.coverage = 'Coverage must be at least 0';
    isValid = false;
  }

  if (!formData.coverageDetails.trim()) {
    errors.coverageDetails = 'Coverage details is required';
    isValid = false;
  }

  if (formData.applicableService.length === 0) {
    errors.applicableService = 'Please select at least one service';
    isValid = false;
  }

  if (formData.expiredByDays === null || formData.expiredByDays === undefined) {
    errors.expiredByDays = 'Expired by days is required';
    isValid = false;
  } else if (formData.expiredByDays < 1) {
    errors.expiredByDays = 'Expired by days must be at least 1';
    isValid = false;
  }

  return isValid;
};

const isFormValid = computed(() => {
  return (
    formData.planName.trim() !== '' &&
    (props.mode === 'edit' || formData.providerId.trim() !== '') &&
    formData.price >= 0 &&
    formData.coverage >= 0 &&
    formData.coverageDetails.trim() !== '' &&
    formData.applicableService.length > 0 &&
    formData.expiredByDays >= 1
  );
});

const handleSubmit = () => {
  if (validateForm()) {
    const submitData: InsurancePlanRequest = {
      planName: formData.planName,
      price: formData.price,
      coverage: formData.coverage,
      coverageDetails: formData.coverageDetails,
      applicableService: [...formData.applicableService],
      expiredByDays: formData.expiredByDays
    };

    if (props.mode === 'create') {
      submitData.providerId = formData.providerId;
    }

    emit('submit', submitData);
  }
};

const formatCurrency = (value: number): string => {
  return new Intl.NumberFormat('id-ID', {
    style: 'currency',
    currency: 'IDR',
    minimumFractionDigits: 0
  }).format(value);
};

watch(() => props.insurancePlan, () => {
  populateForm();
}, { deep: true });

onMounted(() => {
  populateForm();
});
</script>

<template>
  <form @submit.prevent="handleSubmit" class="space-y-6">
    <VInput
      v-model="formData.planName"
      label="Plan Name"
      placeholder="Enter plan name"
      :required="true"
      :error="errors.planName"
    />

    <VInput
      v-model="formData.providerId"
      label="Provider ID"
      placeholder="Enter provider ID"
      :required="mode === 'create'"
      :disabled="mode === 'edit'"
      :readonly="mode === 'edit'"
      :error="errors.providerId"
    />

    <div>
      <VInput
        v-model.number="formData.price"
        type="number"
        label="Price"
        placeholder="Enter price"
        :required="true"
        :min="0"
        :error="errors.price"
      />
      <p v-if="formData.price > 0" class="mt-1 text-sm text-gray-600">
        {{ formatCurrency(formData.price) }}
      </p>
    </div>

    <div>
      <VInput
        v-model.number="formData.coverage"
        type="number"
        label="Coverage"
        placeholder="Enter coverage amount"
        :required="true"
        :min="0"
        :error="errors.coverage"
      />
      <p v-if="formData.coverage > 0" class="mt-1 text-sm text-gray-600">
        {{ formatCurrency(formData.coverage) }}
      </p>
    </div>

    <VTextArea
      v-model="formData.coverageDetails"
      label="Coverage Details"
      placeholder="Enter coverage details"
      :required="true"
      :rows="4"
      :error="errors.coverageDetails"
    />

    <div>
      <VCheckbox
        v-model="formData.applicableService"
        label="Applicable Service"
        :options="serviceOptions"
      />
      <p v-if="errors.applicableService" class="mt-1 text-sm text-red-600">
        {{ errors.applicableService }}
      </p>
    </div>

    <VInput
      v-model.number="formData.expiredByDays"
      type="number"
      label="Expired By Days"
      placeholder="Enter number of days"
      :required="true"
      :min="1"
      :error="errors.expiredByDays"
    />

    <div class="flex justify-end gap-3 pt-4">
      <button
        type="submit"
        :disabled="!isFormValid"
        class="px-6 py-2 text-white bg-blue-600 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
      >
        {{ mode === 'create' ? 'Create' : 'Update' }} Insurance Plan
      </button>
    </div>
  </form>
</template>

<style scoped>
/* Additional styles if needed */
.insurance-plan-form {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.form-actions {
  margin-top: 1.5rem;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.btn {
  padding: 0.5rem 1.5rem;
  border: none;
  border-radius: 0.25rem;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover {
  background-color: #0056b3;
}

.btn-primary:active {
  transform: scale(0.98);
}
</style>
