<template>
  <div class="v-input">
    <label v-if="label" :for="inputId" class="v-input__label">
      {{ label }}
      <span v-if="required" class="v-input__required">*</span>
    </label>
    
    <div class="v-input__container">
      <input
        :id="inputId"
        v-model="inputValue"
        :type="type"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        :required="required"
        :class="inputClasses"
        @blur="handleBlur"
        @focus="handleFocus"
        @input="handleInput"
      />
      <div v-if="$slots.suffix || suffixIcon" class="v-input__suffix">
        <slot name="suffix">
          <component v-if="suffixIcon" :is="suffixIcon" class="v-input__suffix-icon" />
        </slot>
      </div>
    </div>
    
    <div v-if="errorMessage || helperText" class="v-input__footer">
      <p v-if="errorMessage" class="v-input__error">{{ errorMessage }}</p>
      <p v-else-if="helperText" class="v-input__helper">{{ helperText }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, type Component } from 'vue'

interface Props {
  modelValue?: string | number
  type?: 'text' | 'email' | 'password' | 'number' | 'tel' | 'url' | 'search'
  label?: string
  placeholder?: string
  disabled?: boolean
  readonly?: boolean
  required?: boolean
  size?: 'sm' | 'md' | 'lg'
  variant?: 'default' | 'filled' | 'outline'
  errorMessage?: string
  helperText?: string
  suffixIcon?: Component
}

interface Emits {
  (e: 'update:modelValue', value: string | number): void
  (e: 'blur', event: FocusEvent): void
  (e: 'focus', event: FocusEvent): void
  (e: 'input', event: Event): void
}

const props = withDefaults(defineProps<Props>(), {
  type: 'text',
  size: 'md',
  variant: 'outline',
  disabled: false,
  readonly: false,
  required: false
})

const emit = defineEmits<Emits>()

const inputId = ref(`v-input-${Math.random().toString(36).substr(2, 9)}`)

const inputValue = computed({
  get: () => props.modelValue ?? '',
  set: (value) => {
    const processedValue = props.type === 'number' ? Number(value) : value
    emit('update:modelValue', processedValue)
  }
})

const inputClasses = computed(() => {
  const baseClasses = [
    'v-input__field',
    'w-full',
    'transition-colors',
    'focus:outline-none',
    'focus:ring-2',
    'focus:ring-offset-1'
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

  if (props.readonly) {
    classes.push('bg-gray-50', 'text-gray-700')
  }

  return classes.join(' ')
})

const handleBlur = (event: FocusEvent) => {
  emit('blur', event)
}

const handleFocus = (event: FocusEvent) => {
  emit('focus', event)
}

const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  inputValue.value = target.value
  emit('input', event)
}
</script>

<style scoped>
@reference "tailwindcss";

.v-input__label {
  @apply block text-sm font-medium text-gray-700 mb-1;
}

.v-input__required {
  @apply text-red-500;
}

.v-input__container {
  @apply relative;
}

.v-input__suffix {
  @apply absolute inset-y-0 right-0 flex items-center pr-3;
}

.v-input__suffix-icon {
  @apply h-5 w-5 text-gray-400;
}

.v-input__footer {
  @apply mt-1;
}

.v-input__error {
  @apply text-sm text-red-600;
}

.v-input__helper {
  @apply text-sm text-gray-500;
}
</style>