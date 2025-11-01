<template>
  <div :class="cardClasses">
    <!-- Header -->
    <div v-if="$slots.header || title || $slots.actions" class="v-card__header">
      <div class="v-card__header-content">
        <slot name="header">
          <h3 v-if="title" class="v-card__title">{{ title }}</h3>
          <p v-if="subtitle" class="v-card__subtitle">{{ subtitle }}</p>
        </slot>
      </div>
      <div v-if="$slots.actions" class="v-card__actions">
        <slot name="actions"></slot>
      </div>
    </div>

    <!-- Body -->
    <div v-if="$slots.default" class="v-card__body">
      <slot></slot>
    </div>

    <!-- Footer -->
    <div v-if="$slots.footer" class="v-card__footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  title?: string
  subtitle?: string
  variant?: 'default' | 'outlined' | 'elevated' | 'flat'
  size?: 'sm' | 'md' | 'lg'
  hoverable?: boolean
  clickable?: boolean
  loading?: boolean
}

interface Emits {
  (e: 'click', event: MouseEvent): void
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'default',
  size: 'md',
  hoverable: false,
  clickable: false,
  loading: false
})

const emit = defineEmits<Emits>()

const cardClasses = computed(() => {
  const baseClasses = [
    'v-card',
    'bg-white',
    'transition-all',
    'duration-200'
  ]

  // Variant classes
  const variantClasses = {
    default: 'border border-gray-200 shadow-sm',
    outlined: 'border-2 border-gray-300',
    elevated: 'shadow-lg border border-gray-100',
    flat: 'border-0'
  }

  // Size classes
  const sizeClasses = {
    sm: 'rounded',
    md: 'rounded-lg',
    lg: 'rounded-xl'
  }

  const classes = [
    ...baseClasses,
    variantClasses[props.variant],
    sizeClasses[props.size]
  ]

  if (props.hoverable) {
    classes.push('hover:shadow-md', 'hover:scale-[1.02]')
  }

  if (props.clickable) {
    classes.push('cursor-pointer', 'hover:shadow-lg')
  }

  if (props.loading) {
    classes.push('opacity-50', 'pointer-events-none')
  }

  return classes.join(' ')
})

const handleClick = (event: MouseEvent) => {
  if (props.clickable && !props.loading) {
    emit('click', event)
  }
}
</script>

<style scoped>
.v-card__header {
  @apply flex items-start justify-between p-6 border-b border-gray-200;
}

.v-card__header-content {
  @apply flex-1;
}

.v-card__title {
  @apply text-lg font-semibold text-gray-900 leading-6;
}

.v-card__subtitle {
  @apply mt-1 text-sm text-gray-500;
}

.v-card__actions {
  @apply flex items-center space-x-2 ml-4;
}

.v-card__body {
  @apply p-6;
}

.v-card__footer {
  @apply px-6 py-4 bg-gray-50 border-t border-gray-200 rounded-b-lg;
}

/* No header variant */
.v-card:not(:has(.v-card__header)) .v-card__body {
  @apply rounded-t-lg;
}

/* No footer variant */
.v-card:not(:has(.v-card__footer)) .v-card__body {
  @apply rounded-b-lg;
}
</style>