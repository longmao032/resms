<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 左侧个人信息展示 -->
      <el-col :span="8" :xs="24">
        <el-card class="box-card">
          <template #header>
            <div class="clearfix">
              <span>个人信息</span>
            </div>
          </template>
          <div class="user-profile">
            <div class="box-center">
              <el-avatar 
                :size="100" 
                :src="getImageUrl(userStore.userInfo?.avatar)" 
                class="user-avatar"
              >
                {{ userStore.userInfo?.realName?.charAt(0) || userStore.userInfo?.username?.charAt(0) }}
              </el-avatar>
              <div class="user-name">{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</div>
              <div class="user-role">{{ getRoleName(userStore.userInfo?.roleType) }}</div>
            </div>
            
            <div class="user-bio">
              <div class="user-bio-section">
                <div class="user-bio-section-header">
                  <el-icon><User /></el-icon>
                  <span>用户名</span>
                </div>
                <div class="user-bio-section-body">
                  {{ userStore.userInfo?.username }}
                </div>
              </div>
              
              <div class="user-bio-section">
                <div class="user-bio-section-header">
                  <el-icon><Phone /></el-icon>
                  <span>手机号码</span>
                </div>
                <div class="user-bio-section-body">
                  {{ userStore.userInfo?.phone || '未绑定' }}
                </div>
              </div>
              
              <div class="user-bio-section">
                <div class="user-bio-section-header">
                  <el-icon><Message /></el-icon>
                  <span>邮箱地址</span>
                </div>
                <div class="user-bio-section-body">
                  {{ userStore.userInfo?.email || '未绑定' }}
                </div>
              </div>

              <div class="user-bio-section" v-if="userStore.userInfo?.remark">
                <div class="user-bio-section-header">
                  <el-icon><Collection /></el-icon>
                  <span>简介</span>
                </div>
                <div class="user-bio-section-body">
                  {{ userStore.userInfo.remark }}
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧设置面板 -->
      <el-col :span="16" :xs="24">
        <el-card class="box-card">
          <template #header>
            <div class="clearfix">
              <span>基本资料</span>
            </div>
          </template>
          
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本资料" name="userinfo">
              <UserInfo />
            </el-tab-pane>
            <el-tab-pane label="修改密码" name="password">
              <ChangePassword />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { User, Phone, Message, Collection } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/userStore'
import UserInfo from './components/UserInfo.vue'
import ChangePassword from './components/ChangePassword.vue'

const route = useRoute()
const userStore = useUserStore()
const activeTab = ref('userinfo')

// 根据路由设置活动标签
const updateTabFromRoute = () => {
  if (route.path === '/profile/password') {
    activeTab.value = 'password'
  } else {
    activeTab.value = 'userinfo'
  }
}

// 初始化和监听路由变化
updateTabFromRoute()
watch(() => route.path, updateTabFromRoute)

// 图片 URL 处理
const getImageUrl = (url: string | undefined) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  if (url.startsWith('/uploads')) return `http://localhost:8080${url}`
  if (url.startsWith('/')) return `http://localhost:8080/uploads${url}`
  return `http://localhost:8080/uploads/${url}`
}

// 角色名称映射
const getRoleName = (roleType: number | undefined) => {
  const roleMap: Record<number, string> = {
    1: '系统管理员',
    2: '销售顾问',
    3: '销售经理'
  }
  return roleMap[roleType as number] || '未知角色'
}
</script>

<style scoped lang="scss">
.app-container {
  padding: 20px;
}

.box-center {
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  
  .user-avatar {
    margin-bottom: 20px;
  }
  
  .user-name {
    font-weight: bold;
    font-size: 24px;
    margin-bottom: 5px;
  }
  
  .user-role {
    color: #909399;
    font-size: 14px;
  }
}

.user-bio {
  margin-top: 20px;
  color: #606266;
  
  .user-bio-section {
    font-size: 14px;
    padding: 15px 0;
    
    .user-bio-section-header {
      border-bottom: 1px solid #dfe6ec;
      padding-bottom: 10px;
      margin-bottom: 10px;
      font-weight: bold;
      display: flex;
      align-items: center;
      gap: 5px;
    }
    
    .user-bio-section-body {
      color: #606266;
    }
  }
}
</style>
