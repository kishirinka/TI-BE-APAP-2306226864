<script setup lang="ts">
import { RouterLink, useRoute } from 'vue-router'

const route = useRoute()

const getLinkClass = (path: string) => route.path === path ? 'text-blue-600 font-semibold' : 'text-gray-600 hover:text-blue-600'
</script>

<template>
    <header class="fixed top-0 left-0 flex items-center w-full gap-8 px-6 py-4 bg-white border-b border-gray-200 z-50">
        <RouterLink to="/" class="text-xl font-bold text-orange-500">Insurance</RouterLink>
        <nav class="flex gap-8">
            <RouterLink to="/" :class="getLinkClass('/')">Home</RouterLink>
            <RouterLink to="/insurance-plans" :class="getLinkClass('/insurance-plans')">Insurance Plans</RouterLink>
            <RouterLink to="/policies" :class="getLinkClass('/policies')">Policies</RouterLink>
            <RouterLink to="/claims" :class="getLinkClass('/claims')">Claims</RouterLink>
            <RouterLink to="/statistics" :class="getLinkClass('/statistics')">Statistics</RouterLink>
        </nav>
    </header>
</template>