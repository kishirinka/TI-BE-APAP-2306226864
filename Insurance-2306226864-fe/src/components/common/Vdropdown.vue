<script setup lang="ts">
import { ref, computed } from 'vue'

interface DropdownOption {
  value: string | number
  label: string
}

const props = withDefaults(defineProps<{
  id: string
  label?: string
  name?: string
  modelValue: string | number
  options: DropdownOption[]
  placeholder?: string
  disabled?: boolean
}>(), {
  label: '',
  name: '',
  placeholder: 'Select an option...',
  disabled: false
})

const emit = defineEmits(['update:modelValue'])

const handleInput = (e: Event) => {
  const target = e.target as HTMLSelectElement
  if (target) {
    emit('update:modelValue', target.value)
  }
}

const selectedOption = computed(() => {
  return props.options.find(option => option.value === props.modelValue)
})
</script>

<template>
  <div class="flex flex-col gap-1 w-full">
    <label :for="id" v-if="label" class="text-sm font-medium text-gray-700">
      {{ label }}
    </label>
    <select 
      :id="id" 
      :name="name"
      :value="modelValue" 
      :disabled="disabled"
      @input="handleInput"
      class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-pink-500 focus:border-pink-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
    >
      <option value="" v-if="placeholder">{{ placeholder }}</option>
      <option 
        v-for="option in options" 
        :key="option.value" 
        :value="option.value"
      >
        {{ option.label }}
      </option>
    </select>
  </div>
</template>

<style scoped>
</style>
