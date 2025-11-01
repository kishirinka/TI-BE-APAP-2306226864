<template>
  <div class="stats-card">
    <div class="stats-card-header">
      <h3 class="stats-card-title">{{ title }}</h3>
      <div class="stats-card-icon" v-if="showIcon">
        <slot name="icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="3" y="4" width="18" height="16" rx="2" stroke="currentColor" stroke-width="2"/>
            <path d="M7 8h6M7 12h8M7 16h4" stroke="currentColor" stroke-width="2"/>
          </svg>
        </slot>
      </div>
    </div>
    
    <div class="stats-card-content">
      <div class="stats-number">{{ count }}</div>
      <div class="stats-description">{{ description }}</div>
    </div>

    <div class="stats-card-footer" v-if="$slots.footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  title: string
  count: number | string
  description: string
  showIcon?: boolean
}

withDefaults(defineProps<Props>(), {
  showIcon: true
})
</script>

<style scoped>
.stats-card {
  background: white;
  border-radius: 12px;
  padding: 32px 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 2px solid #e5e7eb;
  transition: all 0.2s ease-in-out;
  min-height: 160px;
  display: flex;
  flex-direction: column;
  position: relative;
}

.stats-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.stats-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.stats-card-title {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin: 0;
}

.stats-card-icon {
  color: #6b7280;
  opacity: 0.8;
  padding: 12px;
  border-radius: 10px;
  background: #f8fafc;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stats-card-content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stats-number {
  font-size: 48px;
  font-weight: 800;
  color: #1f2937;
  margin-bottom: 8px;
  line-height: 1;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.stats-description {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.stats-card-footer {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

/* Variants */
.stats-card.primary {
  border-color: #3b82f6;
}

.stats-card.primary .stats-number {
  color: #3b82f6;
}

.stats-card.primary .stats-card-icon {
  background: #eff6ff;
  color: #3b82f6;
}

.stats-card.success {
  border-color: #10b981;
}

.stats-card.success .stats-number {
  color: #10b981;
}

.stats-card.success .stats-card-icon {
  background: #ecfdf5;
  color: #10b981;
}

.stats-card.warning {
  border-color: #f59e0b;
}

.stats-card.warning .stats-number {
  color: #f59e0b;
}

.stats-card.warning .stats-card-icon {
  background: #fffbeb;
  color: #f59e0b;
}

.stats-card.danger {
  border-color: #ef4444;
}

.stats-card.danger .stats-number {
  color: #ef4444;
}

.stats-card.danger .stats-card-icon {
  background: #fef2f2;
  color: #ef4444;
}
</style>