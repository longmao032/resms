<template>
  <div class="admin-layout">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo">
          <h2>后台管理系统</h2>
        </div>
        <div class="user-info">
          <el-popover v-if="userStore.isLoggedIn" placement="bottom" :width="360" trigger="hover" popper-class="notice-popover">
            <template #reference>
              <div class="header-icon">
                <el-badge :value="totalUnread" :max="99" :hidden="totalUnread === 0" class="notice-badge">
                  <el-icon :size="20">
                    <Bell />
                  </el-icon>
                </el-badge>
              </div>
            </template>
            <div class="message-center">
              <el-tabs v-model="activeMessageTab" class="message-tabs">
                <el-tab-pane name="notice">
                  <template #label>
                    <span>系统通知</span>
                    <el-badge :value="noticeStore.unreadCount" :max="99" :hidden="noticeStore.unreadCount === 0"
                      class="tab-badge" />
                  </template>
                  <div class="notice-list">
                    <div class="notice-header">
                      <span>未读通知 ({{ noticeStore.unreadCount }})</span>
                      <el-button link type="primary" size="small" @click="goToNoticeList"
                        class="text-link-btn">查看全部</el-button>
                    </div>
                    <div v-if="noticeStore.unreadList.length > 0" class="notice-items">
                      <div v-for="item in noticeStore.unreadList" :key="item.id" class="notice-item"
                        @click="handleNoticeClick(item)">
                        <div class="notice-title">{{ item.noticeTitle }}</div>
                        <div class="notice-time">{{ item.sendTime }}</div>
                      </div>
                    </div>
                    <el-empty v-else description="暂无未读通知" :image-size="60" />
                  </div>
                </el-tab-pane>
                <el-tab-pane name="chat">
                  <template #label>
                    <span>我的消息</span>
                    <el-badge :value="chatStore.unreadTotal" :max="99" :hidden="chatStore.unreadTotal === 0"
                      class="tab-badge" />
                  </template>
                  <div class="notice-list">
                    <div class="notice-header">
                      <span>消息列表</span>
                      <el-button link type="primary" size="small" @click="goToChat"
                        class="text-link-btn">进入聊天室</el-button>
                    </div>
                    <div v-if="chatStore.sessionList.length > 0" class="notice-items">
                      <div v-for="session in chatStore.sessionList" :key="session.id" class="notice-item"
                        @click="handleChatSessionClick(session)">
                        <div style="display: flex; align-items: center;">
                          <el-avatar :size="36"
                            :src="getImageUrl(session.targetAvatar) || defaultAvatar"
                            style="margin-right: 10px; flex-shrink: 0;" />
                          <div style="flex: 1; overflow: hidden;">
                            <div class="notice-title" style="display: flex; justify-content: space-between;">
                              <span>{{ session.sessionName || session.targetUserName || '未知用户' }}</span>
                              <span class="notice-time">{{ formatTime(session.lastMessageTime) }}</span>
                            </div>
                            <div class="notice-time"
                              style="overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                              {{ session.lastMessageContent || '暂无消息' }}
                            </div>
                          </div>
                          <el-badge :value="session.unreadCount" :max="99" :hidden="!session.unreadCount"
                            class="item-badge" style="margin-left: 5px;" />
                        </div>
                      </div>
                    </div>
                    <el-empty v-else description="暂无消息" :image-size="60" />
                  </div>
                </el-tab-pane>
              </el-tabs>
            </div>
          </el-popover>
          <el-avatar :src="getImageUrl(userStore.currentUser?.avatar) || defaultAvatar" :alt="userStore.currentUser?.realName"
            class="user-avatar">
            {{ userStore.currentUser?.realName?.charAt(0) }}
          </el-avatar>
          <div class="user-details">
            <span class="username">{{ userStore.currentUser?.realName }}</span>
          </div>
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              <el-icon>
                <ArrowDown />
              </el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <!-- 主内容区域 -->
    <el-container class="main-container">
      <!-- 左侧菜单 -->
      <el-aside :width="sidebarCollapsed ? '64px' : '200px'" class="sidebar">
        <div class="sidebar-header">
          <el-button link @click="toggleSidebar" class="collapse-btn" :icon="sidebarCollapsed ? Expand : Fold" />
        </div>

        <el-menu :default-active="activeMenu" :collapse="sidebarCollapsed" :unique-opened="true" router class="menu"
          @select="handleMenuSelect">
          <template v-for="menu in filteredMenus" :key="menu.id">
            <!-- 有子菜单的项目 -->
            <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="menu.path">
              <template #title>
                <el-icon>
                  <component :is="getIconComponent(menu.icon)" />
                </el-icon>
                <span>{{ menu.name }}</span>
              </template>
              <el-menu-item v-for="child in menu.children" :key="child.id" :index="child.path"
                v-show="hasPermission(child.code)">
                <el-icon>
                  <component :is="getIconComponent(child.icon)" />
                </el-icon>
                <span>{{ child.name }}</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 无子菜单的项目 -->
            <el-menu-item v-else :index="menu.path" v-show="hasPermission(menu.code)">
              <el-icon>
                <component :is="getIconComponent(menu.icon)" />
              </el-icon>
              <span>{{ menu.name }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-aside>

      <!-- 右侧内容区域 -->
      <el-container class="content-container">
        <!-- 面包屑导航 -->
        <el-header class="breadcrumb-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>首页</el-breadcrumb-item>
            <el-breadcrumb-item v-for="item in breadcrumbList" :key="item.path">
              {{ item.name }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </el-header>

        <!-- 主内容 -->
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowDown,
  Expand,
  Fold,
  Setting,
  User,
  House,
  Avatar,
  OfficeBuilding,
  Menu as MenuIcon,
  Reading,
  Grid,
  Check,
  TrendCharts,
  Service,
  Document,
  ChatDotRound,
  Coin,
  Wallet,
  Management,
  Trophy,
  Bell
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { useUserStore } from '@/stores/userStore'
import { useNoticeStore } from '@/stores/noticeStore'
import { useChatStore } from '@/stores/chatStore'
import { useSettingsStore } from '@/stores/settingsStore'
import type { Menu } from '@/api/user/type'
import { getImageUrl } from '@/utils/image'
import { getNoticeLocation } from '@/utils/noticeNavigate'
import { readNotice } from '@/api/notice'

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const router = useRouter()
const route = useRoute()

// Store
const userStore = useUserStore()
const noticeStore = useNoticeStore() // 使用 store
const chatStore = useChatStore()
const settingsStore = useSettingsStore()

// 状态管理
const sidebarCollapsed = ref(false)
const activeMenu = ref('')
const breadcrumbList = ref<Menu[]>([])
const activeMessageTab = ref('notice') // 消息中心默认选项卡

// 计算总未读数
const totalUnread = computed(() => {
  return (noticeStore.unreadCount || 0) + (chatStore.unreadTotal || 0)
})

// 跳转到通知列表
const goToNoticeList = () => {
  router.push('/work-notice/list')
}

const handleNoticeClick = (item: any) => {
  if (item?.id) {
    readNotice(item.id)
      .then(() => noticeStore.fetchUnread())
      .catch(() => {})
  }
  const loc = getNoticeLocation(item)
  if (loc) {
    router.push(loc)
  } else {
    router.push('/work-notice/list')
  }
}

// 跳转到聊天
const goToChat = () => {
  router.push('/chat')
}

// 点击会话跳转
const handleChatSessionClick = (session: any) => {
  chatStore.selectSession(session)
  router.push('/chat')
}

// 时间格式化
const formatTime = (time: string) => {
  if (!time) return ''
  const date = dayjs(time)
  const now = dayjs()
  if (date.isSame(now, 'day')) {
    return date.format('HH:mm')
  } else if (date.isSame(now.subtract(1, 'day'), 'day')) {
    return '昨天'
  } else {
    return date.format('MM-DD')
  }
}

// 图标映射
const iconMap: Record<string, any> = {
  setting: Setting,
  user: User,
  home: House,
  team: Avatar,
  apartment: OfficeBuilding,
  menu: MenuIcon,
  book: Reading,
  table: Grid,
  audit: Check,
  'bar-chart': TrendCharts,
  'customer-service': Service,
  form: Document,
  interaction: ChatDotRound,
  transaction: Coin,
  'money-collect': Wallet,
  calculator: Management,
  trophy: Trophy
}

// 获取图标组件
const getIconComponent = (iconName: string) => {
  return iconMap[iconName] || Setting
}

// 过滤菜单 - 只显示有权限的菜单
const filteredMenus = computed(() => {
  return userStore.userMenus.filter(menu => hasPermission(menu.code))
})

// 检查权限
const hasPermission = (permission: string) => {
  if (!permission) return true
  return userStore.hasPermission(permission)
}

// 切换侧边栏
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

// 处理菜单选择
const handleMenuSelect = (index: string) => {
  activeMenu.value = index
  // 更新面包屑
  updateBreadcrumb(index)
}

// 更新面包屑导航
const updateBreadcrumb = (path: string) => {
  const findMenuByPath = (menus: Menu[], targetPath: string): Menu[] => {
    for (const menu of menus) {
      if (menu.path === targetPath) {
        return [menu]
      }
      if (menu.children && menu.children.length > 0) {
        const childResult = findMenuByPath(menu.children, targetPath)
        if (childResult.length > 0) {
          return [menu, ...childResult]
        }
      }
    }
    return []
  }

  breadcrumbList.value = findMenuByPath(userStore.userMenus, path)
}

// 处理下拉菜单命令
const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 处理退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  })
}

// 监听路由变化，更新活动菜单
watch(
  () => route.path,
  (newPath) => {
    activeMenu.value = newPath
    updateBreadcrumb(newPath)
  },
  { immediate: true }
)

// 组件挂载时初始化
onMounted(async () => {
  // 如果用户未登录，重定向到登录页
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  // 初始化系统设置（应用已保存的主题和字体大小）
  settingsStore.initSettings()

  // 尝试将后端返回的子菜单注册为路由（如果对应视图存在）
  // 使用 Vite 的 import.meta.glob 列出所有视图文件作为候选
  const modules = import.meta.glob('/src/views/**/*.vue') as Record<string, () => Promise<any>>

  const capitalize = (s: string) => s ? s.charAt(0).toUpperCase() + s.slice(1) : s
  const pathToName = (p: string) => p.split('/').filter(Boolean).map(segment => capitalize(segment)).join('_')

  // 优先使用后端返回的 `component` 字段来定位视图文件（例如: "system/user/index"）
  const tryMatchComponent = (menuPath: string, backendComponent?: string) => {
    const candidates: string[] = []

    // 如果后端提供 component 字段，先尝试根据它解析
    if (backendComponent) {
      const normalized = backendComponent.replace(/^\/+/, '')
      candidates.push(`/src/views/${normalized}.vue`)
      candidates.push(`/src/views/${normalized}/index.vue`)
    }

    // 回退：根据 path 拼接常见文件名
    const segments = menuPath.split('/').filter(Boolean)
    if (segments.length > 0) {
      candidates.push(`/src/views/${segments.join('/')}.vue`)
      if (segments.length >= 2) {
        candidates.push(`/src/views/${segments[0]}/${capitalize(segments[1])}.vue`)
        candidates.push(`/src/views/${segments[0]}/${capitalize(segments[1])}Manage.vue`)
        candidates.push(`/src/views/${segments[0]}/${capitalize(segments[1])}/index.vue`)
      }
      if (segments.length > 2) {
        candidates.push(`/src/views/${segments.join('/')}/index.vue`)
      }
    }

    for (const c of candidates) {
      if (modules[c]) return modules[c]
    }
    return null
  }

  // 为每个菜单子项注册路由（避免重复注册）
  try {
    for (const menu of userStore.userMenus) {
      if (!menu.children || menu.children.length === 0) continue
      for (const child of menu.children) {
        if (!child.path) continue
        const exists = router.getRoutes().some(r => r.path === child.path)
        if (exists) continue

        const loader = tryMatchComponent(child.path, (child as any).component)
        const routeName = pathToName(child.path)
        if (loader) {
          router.addRoute('Layout', {
            path: child.path,
            name: routeName,
            component: loader
          })
        } else {
          // 如果找不到实现视图，注册一个占位组件以避免导航错误
          router.addRoute('Layout', {
            path: child.path,
            name: routeName,
            component: {
              template: `<div style="padding:24px">页面未实现：${child.name} (${child.path})</div>`
            }
          })
        }
      }
    }
  } catch (err) {
    console.warn('注册后端路由异常：', err)
  }

  // 设置默认活动菜单
  activeMenu.value = route.path
  updateBreadcrumb(route.path)

  // 设置默认活动菜单
  activeMenu.value = route.path
  updateBreadcrumb(route.path)

  // 禁用消息/聊天轮询：避免持续请求影响日志查看
  await noticeStore.fetchUnread()
  await chatStore.fetchSessionList()
})

onUnmounted(() => {
  noticeStore.stopPolling()
  chatStore.stopPolling()
})
</script>

<style scoped lang="scss">
.admin-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #ffffff;
  color: #1f1f1f;
  padding: 0;
  height: 64px;
  box-shadow: none;
  border-bottom: 1px solid #e0e2e0;

  .header-content {
    height: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 24px;

    .logo {
      h2 {
        margin: 0;
        color: #1f1f1f;
        font-size: 20px;
        font-weight: 400;
        font-family: 'Google Sans', sans-serif;
      }
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 12px;

      .header-icon {
        color: #444746;
        cursor: pointer;
        display: flex;
        align-items: center;
        padding: 8px;
        border-radius: 50%;
        margin-right: 4px;
        transition: background-color 0.2s;

        &:hover {
          background-color: rgba(0, 0, 0, 0.08);
          /* Ripple effect */
          color: #1f1f1f;
        }
      }

      .user-avatar {
        width: 32px;
        height: 32px;
      }

      .user-details {
        display: flex;
        flex-direction: column;
        align-items: flex-start;

        .username {
          font-size: 14px;
          font-weight: 500;
          color: #1f1f1f;
          line-height: 1.2;
        }

        .role {
          font-size: 12px;
          color: #444746;
          line-height: 1.2;
        }
      }

      .el-dropdown-link {
        color: #444746;
        cursor: pointer;
        display: flex;
        align-items: center;

        &:hover {
          color: #1f1f1f;
        }
      }
    }
  }
}

.main-container {
  flex: 1;
  overflow: hidden;
  background-color: #f8f9fa;
  /* M3 Surface */
}

.sidebar {
  background-color: #f8f9fa;
  /* Sidebar matches bg or slight contrast */
  border-right: none;
  /* No border in M3 usually */
  transition: width 0.2s;

  .sidebar-header {
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    padding: 0 16px;
    border-bottom: none;

    .collapse-btn {
      color: #444746;
      padding: 8px;
      border-radius: 50%;

      &:hover {
        background-color: rgba(0, 0, 0, 0.08);
      }
    }
  }

  .menu {
    border-right: none;
    height: calc(100vh - 128px);
    overflow-y: auto;
    background-color: transparent;

    // 隐藏滚动条但保留滚动功能
    scrollbar-width: none;
    /* Firefox */
    -ms-overflow-style: none;

    /* IE/Edge */
    &::-webkit-scrollbar {
      display: none;
    }

    :deep(.el-menu-item),
    :deep(.el-sub-menu__title) {
      height: 48px;
      line-height: 48px;
      margin: 4px 12px;
      border-radius: 24px;
      font-size: 14px;
      color: #444746;
      /* M3 On Surface Variant */

      &:hover {
        background-color: #f0f4f9;
        /* M3 Hover State */
      }

      &.is-active {
        background-color: #c2e7ff;
        /* M3 Primary Container */
        color: #001d35;
        /* M3 On Primary Container */
        font-weight: 600;

        &:hover {
          background-color: #c2e7ff;
          /* Keep active color on hover */
        }
      }
    }

    :deep(.el-sub-menu .el-menu-item) {
      height: 40px;
      line-height: 40px;
      margin: 2px 8px;
      padding-left: 50px !important;
      font-size: 13px;
    }
  }
}

.content-container {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.breadcrumb-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 24px;
  height: 48px;
  display: flex;
  align-items: center;
}

.main-content {
  flex: 1;
  background-color: #f5f5f5;
  padding: 24px;
  overflow-y: auto;

  // 隐藏滚动条但保留滚动功能
  scrollbar-width: none;
  /* Firefox */
  -ms-overflow-style: none;

  /* IE/Edge */
  &::-webkit-scrollbar {
    display: none;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .header .header-content {
    padding: 0 16px;

    .logo h2 {
      font-size: 16px;
    }

    .user-info .user-details {
      display: none;
    }
  }

  .sidebar {
    position: absolute;
    top: 64px;
    left: 0;
    z-index: 1000;
    height: calc(100vh - 64px);
    box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);

    &.el-aside--width-64px {
      width: 64px !important;
    }

    &.el-aside--width-200px {
      width: 200px !important;
    }
  }

  .main-content {
    padding: 16px;
  }

  .breadcrumb-header {
    padding: 0 16px;
  }
}
</style>

<style lang="scss">
.notice-popover {
  padding: 0 !important;

  .notice-list {
    .notice-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 16px;
      border-bottom: 1px solid #EBEEF5;

      span {
        font-weight: 600;
        color: #303133;
      }

      .text-link-btn {
        width: auto !important;
        height: auto !important;
        padding: 0 4px !important;
        border-radius: 4px !important;
        color: #409EFF !important;
      }
    }

    .notice-items {
      max-height: 300px;
      overflow-y: auto;

      .notice-item {
        padding: 12px 16px;
        cursor: pointer;
        transition: background-color 0.2s;
        border-bottom: 1px solid #EBEEF5;

        &:last-child {
          border-bottom: none;
        }

        &:hover {
          background-color: #F5F7FA;
        }

        .notice-title {
          font-size: 14px;
          color: #303133;
          margin-bottom: 4px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .notice-time {
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }

  .message-center {
    .message-tabs {
      .el-tabs__header {
        margin-bottom: 0;
        border-bottom: 1px solid #EBEEF5;
      }

      .el-tabs__nav-wrap::after {
        display: none;
      }

      .el-tabs__item {
        padding: 0 16px;
        height: 44px;
        line-height: 44px;
      }
    }
  }

  .tab-badge {
    margin-left: 6px;
    vertical-align: top;

    :deep(.el-badge__content) {
      font-size: 10px;
      padding: 0 4px;
      height: 16px;
      line-height: 16px;
    }
  }
}
</style>
