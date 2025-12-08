<template>
  <el-header class="header-custom">
    <div class="header-inner">
      <RouterLink to="/">
        <div class="logo-area">
          <el-icon class="logo-icon"><House /></el-icon>
          <span class="logo-text">房产网</span>
        </div>
      </RouterLink>
      
      <CitySelector :current-city="currentCity" @city-change="handleCityChange"/>
      
      <el-menu
        :default-active="activeMenu"
        class="nav-menu"
        mode="horizontal"
        background-color="transparent"
        text-color="#606266"
        active-text-color="#409EFF"
        :router="true"
      >
        <el-menu-item index="/" route="/">首页</el-menu-item>
        <el-menu-item index="/projects" route="/projects">本地新房</el-menu-item>
        <el-menu-item index="/second-hand" route="/second-hand">二手房</el-menu-item>
      </el-menu>

      <div class="user-area">
        <!-- 未登录状态 -->
        <RouterLink v-if="!isLoggedIn" to="/login">
          <el-icon><User /></el-icon>
          <span>登录/注册</span>
        </RouterLink>
        
        <!-- 已登录状态 -->
        <el-dropdown v-else>
          <div class="user-info" @click="$event.preventDefault()">
            <span class="user-name">{{  userInfo?.username }}</span>
            <el-avatar :src="userInfo?.avatar" size="small" class="user-avatar">
              {{ userInfo?.realName?.charAt(0) || 'U' }}
            </el-avatar>
          </div>
          
          <template #dropdown>
            <el-dropdown-menu>
               <el-dropdown-item @click="handleMianLayout">个人中心</el-dropdown-item>
                <el-dropdown-item @click="handleMessage">我的消息</el-dropdown-item>
                <el-dropdown-item v-if="userInfo?.roleType !== 5" @click="handleWorkbench">进入工作台</el-dropdown-item>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </el-header>
</template>

<script setup lang="ts">
import { ref, watch } from "vue"
import { useRoute,RouterLink } from "vue-router"
import { House, User, ArrowDown } from '@element-plus/icons-vue'
import CitySelector from '@/components/house/CitySelector.vue'
import { useUserStore } from '@/stores/userStore'
import router from "@/router"

interface Props {
  currentCity: string
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'city-change': [city: string]
}>()

// 获取用户store
const userStore = useUserStore()
const { userInfo, isLoggedIn, logout } = userStore

const route = useRoute()
const activeMenu = ref('/')

// 根据当前路由更新激活菜单
const updateActiveMenu = (path: string) => {
  if (path === '/') {
    activeMenu.value = '/'
  } else if (path.startsWith('/projects')) {
    activeMenu.value = '/projects'
  } else if (path.startsWith('/second-hand')) {
    activeMenu.value = '/second-hand'
  } else {
    // 其他路由可以设置默认激活项
    activeMenu.value = '/'
  }
}

// 监听路由变化，更新激活菜单
watch(() => route.path, (newPath) => {
  updateActiveMenu(newPath)
}, { immediate: true })

const handleCityChange = (city: string) => {
  emit('city-change', city)
}

// 处理退出登录
const handleLogout = () => {
  logout()
}

const handleMianLayout = () => {
  router.push('/main-layout')
}

const handleMessage = () => {
  router.push('/message')
}

const handleWorkbench = () => {
  router.push('/workbench')
}
</script>

<style lang="scss" scoped>
.header-custom {
  padding: 0;
  height: 64px;
  border-bottom: 1px solid #e8e8e8;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  
  .header-inner {
    display: flex;
    align-items: center;
    justify-content: space-between;
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
    height: 100%;
  }
}

a {
  text-decoration: none;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .logo-icon {
    font-size: 28px;
    color: #1890ff;
  }
  
  .logo-text {
    font-size: 20px;
    font-weight: 600;
    color: #1f2937;
  }
}

.nav-menu {
  flex: 1;
  justify-content: center;
  border: none;
  
  .el-menu-item {
    padding: 0 20px;
    font-size: 16px;
    font-weight: 500;
    
    &:hover {
      background: transparent;
      color: #1890ff;
    }
    
    &.is-active {
      border-bottom: 2px solid #1890ff;
    }
  }
}

.user-area {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s;
  
  &:hover {
    background-color: #f5f5f5;
  }
  
  .el-icon {
    color: #666;
  }
  
  span {
    color: #333;
    font-weight: 500;
  }
  
  .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 4px 0;
    
    .user-avatar {
      margin-right: 4px;
    }
    
    .user-name {
      font-size: 14px;
      font-weight: 500;
      color: #333;
    }
    
    .el-icon {
      font-size: 12px;
      color: #999;
    }
  }
}

@media (max-width: 768px) {
  .header-inner {
    padding: 0 16px;
  }
  
  .nav-menu {
    display: none;
  }
}
</style>