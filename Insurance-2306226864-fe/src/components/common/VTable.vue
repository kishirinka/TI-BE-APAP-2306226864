<template>
  <div class="v-table">
    <div v-if="$slots.header" class="v-table__header">
      <slot name="header"></slot>
    </div>
    
    <div class="v-table__container">
      <table class="v-table__table">
        <thead class="v-table__thead">
          <tr>
            <th
              v-for="column in columns"
              :key="column.key"
              :class="getColumnClasses(column)"
              @click="handleSort(column)"
            >
              <div class="v-table__header-content">
                {{ column.title }}
                <span v-if="column.sortable" class="v-table__sort-icon">
                  <svg
                    v-if="sortBy === column.key && sortOrder === 'asc'"
                    class="h-4 w-4"
                    fill="currentColor"
                    viewBox="0 0 20 20"
                  >
                    <path d="M3 3a1 1 0 000 2h11.586l-2.293 2.293a1 1 0 001.414 1.414l4-4a1 1 0 000-1.414l-4-4a1 1 0 10-1.414 1.414L14.586 3H3z" />
                  </svg>
                  <svg
                    v-else-if="sortBy === column.key && sortOrder === 'desc'"
                    class="h-4 w-4"
                    fill="currentColor"
                    viewBox="0 0 20 20"
                  >
                    <path d="M17 17a1 1 0 01-1 1H4.414l2.293 2.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 1.414L4.414 15H16a1 1 0 011 1z" />
                  </svg>
                  <svg
                    v-else
                    class="h-4 w-4 opacity-50"
                    fill="currentColor"
                    viewBox="0 0 20 20"
                  >
                    <path d="M5 12a1 1 0 102 0V6.414l1.293 1.293a1 1 0 001.414-1.414l-3-3a1 1 0 00-1.414 0l-3 3a1 1 0 001.414 1.414L5 6.414V12z" />
                    <path d="M15 8a1 1 0 10-2 0v5.586l-1.293-1.293a1 1 0 00-1.414 1.414l3 3a1 1 0 001.414 0l3-3a1 1 0 00-1.414-1.414L15 13.586V8z" />
                  </svg>
                </span>
              </div>
            </th>
            <th v-if="$slots.actions" class="v-table__actions-header">
              Actions
            </th>
          </tr>
        </thead>
        <tbody class="v-table__tbody">
          <tr v-if="loading" class="v-table__loading">
            <td :colspan="totalColumns" class="v-table__loading-cell">
              <div class="v-table__loading-content">
                <div class="v-table__spinner"></div>
                <span>Loading...</span>
              </div>
            </td>
          </tr>
          <tr v-else-if="sortedData.length === 0" class="v-table__empty">
            <td :colspan="totalColumns" class="v-table__empty-cell">
              <slot name="empty">No data available</slot>
            </td>
          </tr>
          <tr
            v-else
            v-for="(item, index) in paginatedData"
            :key="getRowKey(item, index)"
            :class="getRowClasses(item, index)"
            @click="handleRowClick(item, index)"
          >
            <td
              v-for="column in columns"
              :key="column.key"
              :class="getCellClasses(column)"
            >
              <slot
                :name="`cell-${column.key}`"
                :item="item"
                :value="getItemValue(item, column.key)"
                :index="index"
              >
                {{ formatCellValue(getItemValue(item, column.key), column) }}
              </slot>
            </td>
            <td v-if="$slots.actions" class="v-table__actions-cell">
              <slot name="actions" :item="item" :index="index"></slot>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div v-if="showPagination && totalPages > 1" class="v-table__pagination">
      <div class="v-table__pagination-info">
        Showing {{ paginationStart }} to {{ paginationEnd }} of {{ totalItems }} entries
      </div>
      <div class="v-table__pagination-controls">
        <button
          :disabled="currentPage === 1"
          class="v-table__pagination-btn"
          @click="goToPage(currentPage - 1)"
        >
          Previous
        </button>
        <button
          v-for="page in visiblePages"
          :key="page"
          :class="getPageClasses(page)"
          @click="goToPage(page)"
        >
          {{ page }}
        </button>
        <button
          :disabled="currentPage === totalPages"
          class="v-table__pagination-btn"
          @click="goToPage(currentPage + 1)"
        >
          Next
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

interface Column {
  key: string
  title: string
  sortable?: boolean
  width?: string
  align?: 'left' | 'center' | 'right'
  formatter?: (value: any) => string
}

interface Props {
  data: any[]
  columns: Column[]
  loading?: boolean
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
  showPagination?: boolean
  pageSize?: number
  rowKey?: string | ((row: any, index: number) => string | number)
  hoverable?: boolean
  striped?: boolean
}

interface Emits {
  (e: 'sort', column: Column, order: 'asc' | 'desc'): void
  (e: 'row-click', row: any, index: number): void
  (e: 'page-change', page: number): void
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  showPagination: true,
  pageSize: 10,
  rowKey: 'id',
  hoverable: true,
  striped: false
})

const emit = defineEmits<Emits>()

const currentPage = ref(1)
const internalSortBy = ref(props.sortBy || '')
const internalSortOrder = ref<'asc' | 'desc'>(props.sortOrder || 'asc')

const sortBy = computed(() => props.sortBy || internalSortBy.value)
const sortOrder = computed(() => props.sortOrder || internalSortOrder.value)

const totalColumns = computed(() => {
  return props.columns.length + (!!props.$slots?.actions ? 1 : 0)
})

const sortedData = computed(() => {
  if (!sortBy.value) return props.data

  return [...props.data].sort((a, b) => {
    const aValue = getItemValue(a, sortBy.value)
    const bValue = getItemValue(b, sortBy.value)

    if (aValue === null || aValue === undefined) return 1
    if (bValue === null || bValue === undefined) return -1

    if (aValue < bValue) return sortOrder.value === 'asc' ? -1 : 1
    if (aValue > bValue) return sortOrder.value === 'asc' ? 1 : -1
    return 0
  })
})

const totalItems = computed(() => sortedData.value.length)
const totalPages = computed(() => Math.ceil(totalItems.value / props.pageSize))

const paginatedData = computed(() => {
  if (!props.showPagination) return sortedData.value

  const start = (currentPage.value - 1) * props.pageSize
  const end = start + props.pageSize
  return sortedData.value.slice(start, end)
})

const paginationStart = computed(() => {
  return totalItems.value === 0 ? 0 : (currentPage.value - 1) * props.pageSize + 1
})

const paginationEnd = computed(() => {
  return Math.min(currentPage.value * props.pageSize, totalItems.value)
})

const visiblePages = computed(() => {
  const delta = 2
  const range = []
  const rangeWithDots = []

  for (let i = Math.max(2, currentPage.value - delta); i <= Math.min(totalPages.value - 1, currentPage.value + delta); i++) {
    range.push(i)
  }

  if (currentPage.value - delta > 2) {
    rangeWithDots.push(1, '...')
  } else {
    rangeWithDots.push(1)
  }

  rangeWithDots.push(...range)

  if (currentPage.value + delta < totalPages.value - 1) {
    rangeWithDots.push('...', totalPages.value)
  } else if (totalPages.value > 1) {
    rangeWithDots.push(totalPages.value)
  }

  return rangeWithDots
})

const getItemValue = (item: any, key: string) => {
  return key.split('.').reduce((obj, k) => obj?.[k], item)
}

const getRowKey = (row: any, index: number) => {
  if (typeof props.rowKey === 'function') {
    return props.rowKey(row, index)
  }
  return getItemValue(row, props.rowKey) || index
}

const getColumnClasses = (column: Column) => {
  const classes = ['v-table__th']
  
  if (column.sortable) {
    classes.push('v-table__th--sortable')
  }
  
  if (column.align) {
    classes.push(`text-${column.align}`)
  }
  
  return classes.join(' ')
}

const getRowClasses = (item: any, index: number) => {
  const classes = ['v-table__tr']
  
  if (props.hoverable) {
    classes.push('v-table__tr--hoverable')
  }
  
  if (props.striped && index % 2 === 1) {
    classes.push('v-table__tr--striped')
  }
  
  return classes.join(' ')
}

const getCellClasses = (column: Column) => {
  const classes = ['v-table__td']
  
  if (column.align) {
    classes.push(`text-${column.align}`)
  }
  
  return classes.join(' ')
}

const getPageClasses = (page: number | string) => {
  const classes = ['v-table__pagination-btn']
  
  if (page === currentPage.value) {
    classes.push('v-table__pagination-btn--active')
  }
  
  if (page === '...') {
    classes.push('v-table__pagination-btn--disabled')
  }
  
  return classes.join(' ')
}

const formatCellValue = (value: any, column: Column) => {
  if (column.formatter) {
    return column.formatter(value)
  }
  return value !== null && value !== undefined ? String(value) : ''
}

const handleSort = (column: Column) => {
  if (!column.sortable) return

  let newOrder: 'asc' | 'desc' = 'asc'
  
  if (sortBy.value === column.key) {
    newOrder = sortOrder.value === 'asc' ? 'desc' : 'asc'
  }

  internalSortBy.value = column.key
  internalSortOrder.value = newOrder

  emit('sort', column, newOrder)
}

const handleRowClick = (row: any, index: number) => {
  emit('row-click', row, index)
}

const goToPage = (page: number | string) => {
  if (typeof page !== 'number' || page < 1 || page > totalPages.value) return
  
  currentPage.value = page
  emit('page-change', page)
}
</script>

<style scoped>
.v-table {
  @apply bg-white shadow rounded-lg overflow-hidden;
}

.v-table__header {
  @apply p-6 border-b border-gray-200;
}

.v-table__container {
  @apply overflow-x-auto;
}

.v-table__table {
  @apply min-w-full divide-y divide-gray-200;
}

.v-table__thead {
  @apply bg-gray-50;
}

.v-table__th {
  @apply px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider;
}

.v-table__th--sortable {
  @apply cursor-pointer hover:bg-gray-100;
}

.v-table__header-content {
  @apply flex items-center justify-between;
}

.v-table__sort-icon {
  @apply ml-2;
}

.v-table__tbody {
  @apply bg-white divide-y divide-gray-200;
}

.v-table__tr--hoverable {
  @apply hover:bg-gray-50;
}

.v-table__tr--striped {
  @apply bg-gray-50;
}

.v-table__td {
  @apply px-6 py-4 whitespace-nowrap text-sm text-gray-900;
}

.v-table__actions-header {
  @apply px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider;
}

.v-table__actions-cell {
  @apply px-6 py-4 whitespace-nowrap text-right text-sm font-medium;
}

.v-table__loading-cell,
.v-table__empty-cell {
  @apply px-6 py-8 text-center text-sm text-gray-500;
}

.v-table__loading-content {
  @apply flex items-center justify-center space-x-2;
}

.v-table__spinner {
  @apply animate-spin h-4 w-4 border-2 border-blue-500 border-t-transparent rounded-full;
}

.v-table__pagination {
  @apply flex items-center justify-between px-6 py-3 bg-white border-t border-gray-200;
}

.v-table__pagination-info {
  @apply text-sm text-gray-700;
}

.v-table__pagination-controls {
  @apply flex items-center space-x-1;
}

.v-table__pagination-btn {
  @apply px-3 py-1 text-sm border border-gray-300 bg-white text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed;
}

.v-table__pagination-btn--active {
  @apply bg-blue-500 text-white border-blue-500;
}

.v-table__pagination-btn--disabled {
  @apply cursor-default hover:bg-white;
}
</style>