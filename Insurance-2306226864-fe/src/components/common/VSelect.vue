<template>
  <div class="v-select">
    <label v-if="label" :for="selectId" class="v-select__label">
      {{ label }}
      <span v-if="required" class="v-select__required">*</span>
    </label>
    
    <div class="v-select__container">
      <select
        :id="selectId"
        v-model="selectValue"
        :disabled="disabled"
        :required="required"
        :class="selectClasses"
        @change="handleChange"
        @blur="handleBlur"
        @focus="handleFocus"
      >
        <option v-if="placeholder" value="" disabled>{{ placeholder }}</option>
        <option
          v-for="option in options"
          :key="getOptionValue(option)"
          :value="getOptionValue(option)"
        >
          {{ getOptionLabel(option) }}
        </option>
      </select>
      <div class="v-select__icon">
        <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
        </svg>
      </div>
    </div>
    
    <div v-if="errorMessage || helperText" class="v-select__footer">
      <p v-if="errorMessage" class="v-select__error">{{ errorMessage }}</p>
      <p v-else-if="helperText" class="v-select__helper">{{ helperText }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

interface Option {
  label: string
  value: string | number
}

interface Props {
  modelValue?: string | number
  options: Option[] | string[] | number[]
  label?: string
  placeholder?: string
  disabled?: boolean
  required?: boolean
  size?: 'sm' | 'md' | 'lg'
  variant?: 'default' | 'filled' | 'outline'
  errorMessage?: string
  helperText?: string
  valueKey?: string
  labelKey?: string
}

interface Emits {
  (e: 'update:modelValue', value: string | number): void
  (e: 'change', value: string | number): void
  (e: 'blur', event: FocusEvent): void
  (e: 'focus', event: FocusEvent): void
}

const props = withDefaults(defineProps<Props>(), {
  size: 'md',
  variant: 'outline',
  disabled: false,
  required: false,
  valueKey: 'value',
  labelKey: 'label'
})

const emit = defineEmits<Emits>()

const selectId = ref(`v-select-${Math.random().toString(36).substr(2, 9)}`)

const selectValue = computed({
  get: () => props.modelValue ?? '',
  set: (value) => {
    emit('update:modelValue', value)
  }
})

const selectClasses = computed(() => {
  const baseClasses = [
    'v-select__field',
    'w-full',
    'appearance-none',
    'transition-colors',
    'focus:outline-none',
    'focus:ring-2',
    'focus:ring-offset-1',
    'pr-10'
  ]

  // Size classes
  const sizeClasses = {
    sm: 'px-3 py-1.5 text-sm',
    md: 'px-4 py-2 text-base',
    lg: 'px-5 py-3 text-lg'
  }

  // Variant classes
  const variantClasses = {
    default: 'border-0 bg-gray-100 focus:bg-white focus:ring-blue-500',
    filled: 'border-0 bg-gray-100 focus:bg-gray-200 focus:ring-blue-500',
    outline: 'border border-gray-300 bg-white focus:border-blue-500 focus:ring-blue-500'
  }

  const classes = [
    ...baseClasses,
    sizeClasses[props.size],
    variantClasses[props.variant],
    'rounded-md'
  ]

  if (props.errorMessage) {
    classes.push('border-red-500', 'focus:border-red-500', 'focus:ring-red-500')
  }

  if (props.disabled) {
    classes.push('bg-gray-100', 'text-gray-500', 'cursor-not-allowed')
  }

  return classes.join(' ')
})

const getOptionValue = (option: Option | string | number): string | number => {
  if (typeof option === 'object' && option !== null) {
    return option[props.valueKey as keyof Option] as string | number
  }
  return option as string | number
}

const getOptionLabel = (option: Option | string | number): string => {
  if (typeof option === 'object' && option !== null) {
    return option[props.labelKey as keyof Option] as string
  }
  return String(option)
}

const handleChange = (event: Event) => {
  const target = event.target as HTMLSelectElement
  const value = target.value
  selectValue.value = value
  emit('change', value)
}

const handleBlur = (event: FocusEvent) => {
  emit('blur', event)
}

const handleFocus = (event: FocusEvent) => {
  emit('focus', event)
}
</script>

<style scoped>
.v-select__label {
  @apply block text-sm font-medium text-gray-700 mb-1;
}

.v-select__required {
  @apply text-red-500;
}

.v-select__container {
  @apply relative;
}

.v-select__icon {
  @apply absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none;
}

.v-select__footer {
  @apply mt-1;
}

.v-select__error {
  @apply text-sm text-red-600;
}

.v-select__helper {
  @apply text-sm text-gray-500;
}
</style>