import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
    },
    {
      path: '/insurance-plans',
      name: 'insurance-plans',
      component: () => import('../views/insuranceplan/InsurancePlanView.vue'),
    },
    {
      path: '/insurance-plans/create',
      name: 'insurance-plan-create',
      component: () => import('../views/insuranceplan/CreateInsurancePlanView.vue'),
    },
    {
      path: '/insurance-plans/:id',
      name: 'insurance-plan-detail',
      component: () => import('../views/insuranceplan/DetailInsurancePlanView.vue'),
    },
    {
      path: '/insurance-plans/:id/edit',
      name: 'insurance-plan-edit',
      component: () => import('../views/insuranceplan/EditInsurancePlanView.vue'),
    },
    {
      path: '/policies',
      name: 'policies',
      component: () => import('../views/PoliciesView.vue'),
    },
    {
      path: '/claims', 
      name: 'claims',
      component: () => import('../views/ClaimsView.vue'),
    },
    {
      path: '/statistics',
      name: 'statistics',
      component: () => import('../views/StatisticsView.vue'),
    },
  ],
})

export default router
