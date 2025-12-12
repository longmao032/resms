import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/userStore'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      name: 'Layout',
      component: () => import('@/views/AdminLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'Dashboard',
          component: () => import('@/views/Dashboard.vue'),
          meta: { title: '首页' }
        },
        // 系统管理
        {
          path: '/system/user',
          name: 'UserManage',
          component: () => import('@/views/system/UserManage.vue'),
          meta: { title: '用户管理', permission: 'user:manage' }
        },
        {
          path: '/system/role',
          name: 'RoleManage',
          component: () => import('@/views/system/RoleManage.vue'),
          meta: { title: '角色管理', permission: 'role:manage' }
        },
        {
          path: '/system/permission',
          name: 'PermissionManage',
          component: () => import('@/views/system/PermissionManage.vue'),
          meta: { title: '权限管理', permission: 'permission:manage' }
        },
        {
          path: '/system/menu',
          name: 'MenuManage',
          component: () => import('@/views/system/MenuManage.vue'),
          meta: { title: '菜单管理', permission: 'menu:manage' }
        },
        // 房源管理
        {
          path: '/house/list',
          name: 'HouseList',
          component: () => import('@/views/house/HouseList.vue'),
          meta: { title: '房源列表', permission: 'house:list:manage' }
        },
        // 注释掉独立的"新增房源"菜单路由
        // 根据权限优化方案，新增房源改为仅在房源列表页通过按钮访问
        // 如需恢复独立菜单，请取消注释并在数据库中启用 house:add:page 权限
        /*
        {
          path: '/house/add',
          name: 'HouseAdd',
          component: () => import('@/views/house/HouseAdd.vue'),
          meta: { title: '新增房源', permission: 'house:add:page' }
        },
        */
        {
          path: '/house/edit/:id',
          name: 'HouseEdit',
          component: () => import('@/views/house/HouseEdit.vue'),
          meta: { title: '编辑房源', permission: 'house:edit' }
        },
        {
          path: '/house/detail/:id',
          name: 'HouseDetail',
          component: () => import('@/views/house/HouseDetail.vue'),
          meta: { title: '房源详情', permission: 'house:detail:page' }
        },
        {
          path: '/house/audit',
          name: 'HouseAudit',
          component: () => import('@/views/house/HouseAudit.vue'),
          meta: { title: '房源审核', permission: 'house:audit:manage' }
        },
        {
          path: '/house/statistics',
          name: 'HouseStatistics',
          component: () => import('@/views/house/HouseStatistics.vue'),
          meta: { title: '房源统计', permission: 'house:statistics:manage' }
        },
        // 项目/小区管理
        {
          path: '/project/list',
          name: 'ProjectList',
          component: () => import('@/views/project/ProjectList.vue'),
          meta: { title: '楼盘列表', permission: 'project:list:page' }
        },
        {
          path: '/project/add',
          name: 'ProjectAdd',
          component: () => import('@/views/project/ProjectAdd.vue'),
          meta: { title: '新增项目', permission: 'project:add' }
        },
        {
          path: '/project/detail/:id',
          name: 'ProjectDetail',
          component: () => import('@/views/project/ProjectDetail.vue'),
          meta: { title: '项目详情', permission: 'project:detail' }
        },
        {
          path: '/project/edit/:id',
          name: 'ProjectEdit',
          component: () => import('@/views/project/ProjectEdit.vue'),
          meta: { title: '编辑项目', permission: 'project:edit' }
        },
        {
          path: '/community/list',
          name: 'CommunityList',
          component: () => import('@/views/community/CommunityList.vue'),
          meta: { title: '小区列表', permission: 'community:list:page' }
        },
        {
          path: '/community/add',
          name: 'CommunityAdd',
          component: () => import('@/views/community/CommunityAdd.vue'),
          meta: { title: '新增小区', permission: 'community:add' }
        },
        {
          path: '/community/detail/:id',
          name: 'CommunityDetail',
          component: () => import('@/views/community/CommunityDetail.vue'),
          meta: { title: '小区详情', permission: 'community:detail' }
        },
        {
          path: '/community/edit/:id',
          name: 'CommunityEdit',
          component: () => import('@/views/community/CommunityEdit.vue'),
          meta: { title: '编辑小区', permission: 'community:edit' }
        },
        // 客户管理
        {
          path: '/customer/list',
          name: 'CustomerList',
          component: () => import('@/views/customer/CustomerList.vue'),
          meta: { title: '客户列表', permission: 'customer:list:manage' }
        },
        {
          path: '/customer/follow',
          name: 'CustomerFollow',
          component: () => import('@/views/customer/CustomerFollow.vue'),
          meta: { title: '客户跟进', permission: 'customer:follow:manage' }
        },
        {
          path: '/customer/statistics',
          name: 'CustomerStatistics',
          component: () => import('@/views/customer/CustomerStatistics.vue'),
          meta: { title: '客户统计', permission: 'customer:statistics:manage' }
        },
        // 交易管理
        {
          path: '/transaction/list',
          name: 'TransactionList',
          component: () => import('@/views/transaction/TransactionList.vue'),
          meta: { title: '交易列表', permission: 'transaction:list:manage' }
        },
        {
          path: '/transaction/audit',
          name: 'TransactionAudit',
          component: () => import('@/views/transaction/TransactionAudit.vue'),
          meta: { title: '交易审核', permission: 'transaction:audit:manage' }
        },
        {
          path: '/transaction/statistics',
          name: 'TransactionStatistics',
          component: () => import('@/views/transaction/TransactionStatistics.vue'),
          meta: { title: '交易统计', permission: 'transaction:statistics:manage' }
        },
        // 佣金管理
        {
          path: '/commission/list',
          name: 'CommissionList',
          component: () => import('@/views/commission/CommissionList.vue'),
          meta: { title: '佣金列表', permission: 'commission:list:manage' }
        },
        {
          path: '/commission/calculate',
          name: 'CommissionCalculate',
          component: () => import('@/views/commission/CommissionCalculate.vue'),
          meta: { title: '佣金核算', permission: 'commission:calculate:manage' }
        },
        {
          path: '/commission/statistics',
          name: 'CommissionStatistics',
          component: () => import('@/views/commission/CommissionStatistics.vue'),
          meta: { title: '佣金统计', permission: 'commission:statistics:manage' }
        },
        // 团队管理
        {
          path: '/team/list',
          name: 'TeamList',
          component: () => import('@/views/team/TeamList.vue'),
          meta: { title: '团队列表', permission: 'team:list' }
        },
        {
          path: '/team/performance',
          name: 'TeamPerformance',
          component: () => import('@/views/team/TeamPerformance.vue'),
          meta: { title: '团队业绩', permission: 'team:performance' }
        },
        // 收款记录
        {
          path: '/payment/list',
          name: 'PaymentList',
          component: () => import('@/views/payment/payMentList.vue'),
          meta: { title: '收款记录列表', permission: 'payment:list:manage' }
        },
        {
          path: '/payment/statistics',
          name: 'PaymentStatistics',
          component: () => import('@/views/payment/PaymentStatistics.vue'),
          meta: { title: '收款统计', permission: 'payment:statistics:manage' }
        },
        // 工作通知
        {
          path: '/work-notice/list',
          name: 'WorkNoticeList',
          component: () => import('@/views/work-notice/list/index.vue'),
          meta: { title: '通知列表', permission: 'work:notice:list:manage' }
        },
        {
          path: '/work-notice/send',
          redirect: '/work-notice/list'
        },
        // 报表管理
        {
          path: '/report/sale',
          name: 'SaleReport',
          component: () => import('@/views/report/sale/index.vue'),
          meta: { title: '销售报表', permission: 'report:sale:manage' }
        },
        {
          path: '/report/financial',
          name: 'FinancialReport',
          component: () => import('@/views/report/financial/index.vue'),
          meta: { title: '财务报表', permission: 'report:financial:manage' }
        },
        {
          path: '/report/customer',
          name: 'CustomerReport',
          component: () => import('@/views/report/customer/index.vue'),
          meta: { title: '客户报表', permission: 'report:customer:manage' }
        },
        // 个人中心
        {
          path: '/profile',
          name: 'Profile',
          component: () => import('@/views/profile/index.vue'),
          meta: { title: '个人中心' }
        },
        {
          path: '/profile/info',
          name: 'ProfileInfo',
          component: () => import('@/views/profile/index.vue'),
          meta: { title: '个人资料' }
        },
        {
          path: '/profile/password',
          name: 'ProfilePassword',
          component: () => import('@/views/profile/index.vue'),
          meta: { title: '修改密码' }
        },
        // 操作日志
        {
          path: '/system/log',
          name: 'SystemLog',
          component: () => import('@/views/Dashboard.vue'),
          meta: { title: '操作日志', permission: 'system:log:manage' }
        }
      ]
    },
    // 旧路由保留用于兼容
    {
      path: '/Projects',
      name: 'Projects',
      component: () => import('@/views/Project.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/second-hand',
      name: 'second-hand',
      component: () => import('@/views/SecondHand.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/main-layout',
      name: 'main-layout',
      component: () => import('@/views/MainLayout.vue'),
      meta: { requiresAuth: false }
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // 检查是否需要登录
  if (to.meta.requiresAuth !== false && !userStore.isLoggedIn) {
    next('/login')
    return
  }

  // 检查权限（如果路由定义了权限要求）
  if (to.meta.permission) {
    const permission = to.meta.permission as string
    // 确保权限数组已加载
    if (!userStore.permissions || userStore.permissions.length === 0) {
      // 如果权限未加载，可能是首次登录，允许访问但会在组件中处理
      console.warn(`权限未加载，路由: ${to.path}, 需要权限: ${permission}`)
    } else if (!userStore.hasPermission(permission)) {
      // 没有权限，重定向到首页
      console.warn(`用户无权限访问: ${to.path}, 需要权限: ${permission}`)
      next('/')
      return
    }
  }

  next()
})

export default router
