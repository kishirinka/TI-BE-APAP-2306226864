<script setup lang="ts">
import { computed } from 'vue'

interface Option {
  value: string
  label: string
}

interface Props {
  modelValue: string[]
  options: Option[]
  label?: string
  disabled?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  disabled: false
})

const emit = defineEmits<{
  'update:modelValue': [value: string[]]
}>()

const isChecked = (value: string) => {
  return props.modelValue.includes(value)
}

const toggleOption = (value: string) => {
  if (props.disabled) return

  const newValue = isChecked(value)
    ? props.modelValue.filter(v => v !== value)
    : [...props.modelValue, value]
  
  emit('update:modelValue', newValue)
}

const selectAll = () => {
  if (props.disabled) return
  
  if (props.modelValue.length === props.options.length) {
    emit('update:modelValue', [])
  } else {
    emit('update:modelValue', props.options.map(opt => opt.value))
  }
}

const allSelected = computed(() => {
  return props.modelValue.length === props.options.length && props.options.length > 0
})

const someSelected = computed(() => {
  return props.modelValue.length > 0 && props.modelValue.length < props.options.length
})
</script>

<template>
  <div class="space-y-2">
    <!-- Label with Select All -->
    <div v-if="label" class="flex items-center justify-between">
      <label class="block text-sm font-medium text-gray-700">
        {{ label }}
      </label>
      <button
        v-if="options.length > 1"
        @click="selectAll"
        type="button"
        :disabled="disabled"
        class="text-xs text-blue-600 hover:text-blue-800 disabled:text-gray-400 disabled:cursor-not-allowed"
      >
        {{ allSelected ? 'Deselect All' : 'Select All' }}
      </button>
    </div>

    <!-- Checkboxes -->
    <div class="space-y-2">
      <label
        v-for="option in options"
        :key="option.value"
        class="flex items-center space-x-3 cursor-pointer"
        :class="{ 'cursor-not-allowed opacity-60': disabled }"
      >
        <input
          type="checkbox"
          :checked="isChecked(option.value)"
          @change="toggleOption(option.value)"
          :disabled="disabled"
          class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500 focus:ring-offset-0 disabled:cursor-not-allowed"
        />
        <span class="text-sm text-gray-700">{{ option.label }}</span>
      </label>
    </div>

    <!-- Selected Count -->
    <div v-if="modelValue.length > 0" class="text-xs text-gray-500 mt-2">
      {{ modelValue.length }} of {{ options.length }} selected
    </div>
  </div>
</template>
