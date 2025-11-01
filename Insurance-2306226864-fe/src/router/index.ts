import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // Home
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    
    // Insurance Plan Routes
    {
      path: '/insurance-plans',
      name: 'insurance-plans',
      component: () => import('../views/insuranceplan/InsurancePlanView.vue')
    },
    {
      path: '/insurance-plans/create',
      name: 'create-insurance-plan',
      component: () => import('../views/insuranceplan/CreateInsurancePlanView.vue')
    },
    {
      path: '/insurance-plans/:id',
      name: 'detail-insurance-plan',
      component: () => import('../views/insuranceplan/DetailInsurancePlanView.vue')
    },
    {
      path: '/insurance-plans/:id/edit',
      name: 'edit-insurance-plan',
      component: () => import('../views/insuranceplan/EditInsurancePlanView.vue')
    },
    
    // Policy Routes
    {
      path: '/policies',
      name: 'policies',
      component: () => import('../views/policy/PolicyView.vue')
    },
    {
      path: '/policies/create',
      name: 'create-policy',
      component: () => import('../views/policy/CreatePolicyView.vue')
    },
    {
      path: '/policies/:id',
      name: 'detail-policy',
      component: () => import('../views/policy/DetailPolicyView.vue')
    },
    
    // Ordered Plan Routes
    {
      path: '/ordered-plans/:id',
      name: 'detail-ordered-plan',
      component: () => import('../views/orderedplan/DetailOrderedPlanView.vue')
    },
    {
      path: '/ordered-plans/:id/claim',
      name: 'create-claim',
      component: () => import('../views/orderedplan/CreateClaimView.vue')
    },
    
    // Claim Routes
    {
      path: '/claims',
      name: 'claims',
      component: () => import('../views/claim/ClaimView.vue')
    },
    {
      path: '/claims/:id/process',
      name: 'process-claim',
      component: () => import('../views/claim/ProcessClaimView.vue')
    },
    
    // Statistics
    {
      path: '/statistics',
      name: 'statistics',
      component: () => import('../views/StatisticsView.vue')
    },
    
    // 404 Not Found
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('../views/NotFoundView.vue')
    }
  ]
})

export default router
