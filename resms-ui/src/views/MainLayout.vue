<template>
  <div class="personal-center">
    <!-- 顶部导航栏 -->
    <header class="header-mod">
      <div class="header-container">
        <div class="user-info">
          <div class="avatar">
            <el-avatar :icon="User" class="user-avatar" />
          </div>
          <div class="user-detail">
            <p class="greeting">晚上好，{{ userInfo.nickname }}</p>
            <p class="account-info">
              账户名: {{ userInfo.account }}
              <span class="role">用户</span>
              <span class="login-time">上次登录: {{ lastLoginTime }}</span>
            </p>
          </div>
        </div>
        <el-button type="primary" class="edit-profile">修改资料</el-button>
      </div>
    </header>

    <!-- 二级导航 -->
    <div class="header-sub-mod">
      <div class="sub-nav">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>首页</el-breadcrumb-item>
          <el-breadcrumb-item>个人中心</el-breadcrumb-item>
          <el-breadcrumb-item :to="currentPath">{{ currentTitle }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="container clearfix">
      <!-- 左侧导航 -->
      <aside class="side-menu">
        <ul class="menu-list">
          <li :class="{ active: activeMenu === 'message' }" @click="handleMenuClick('message')">
            <el-icon><Message /></el-icon>
            <span>消息</span>
          </li>
          <li :class="{ active: activeMenu === 'house-manage' }" @click="handleMenuClick('house-manage')">
            <el-icon><HomeFilled /></el-icon>
            <span>房源管理</span>
          </li>
          <li :class="{ active: activeMenu === 'qa' }" @click="handleMenuClick('qa')">
            <el-icon><HelpFilled /></el-icon>
            <span>问答</span>
          </li>
          <li :class="{ active: activeMenu === 'sell-house' }" @click="handleMenuClick('sell-house')">
            <el-icon><ShoppingCart /></el-icon>
            <span>卖房</span>
          </li>
          <li :class="{ active: activeMenu === 'comments' }" @click="handleMenuClick('comments')">
            <el-icon><ChatDotRound /></el-icon>
            <span>评论</span>
          </li>
          <li :class="{ active: activeMenu === 'profile' }" @click="handleMenuClick('profile')">
            <el-icon><User /></el-icon>
            <span>个人资料</span>
          </li>
          <li :class="{ active: activeMenu === 'view-record' }" @click="handleMenuClick('view-record')">
            <el-icon><Clock /></el-icon>
            <span>看房记录</span>
          </li>
        </ul>

        <div class="tool-list" style="padding: 12px 16px;">
          <p class="tool-title">小工具</p>
          <ul>
            <li @click="handleToolClick('estimate')">
              <el-icon><Menu /></el-icon>
              <span>估房价</span>
            </li>
            <li @click="handleToolClick('loan')">
              <el-icon><Money /></el-icon>
              <span>房贷计算器</span>
            </li>
          </ul>
        </div>

        <div class="account-operation">
          <el-button type="text" class="logout-btn" @click="handleLogout">
            <el-icon><CircleClose /></el-icon>
            <span>注销账号</span>
          </el-button>
        </div>
      </aside>

      <!-- 右侧内容区 -->
      <div class="right-content">
        <!-- 个人资料内容 -->
        <div v-if="activeMenu === 'profile'" class="profile-content">
          <div class="content-header">
            <h2>个人资料</h2>
          </div>
          
          <div class="profile-section">
            <h3>基本账号资料</h3>
            <el-form ref="profileForm" :model="formData" label-width="120px">
              <el-form-item label="头像">
                <div class="avatar-upload">
                  <el-avatar :icon="User" class="big-avatar" />
                  <el-button type="text" class="edit-btn">修改</el-button>
                </div>
              </el-form-item>
              
              <el-form-item label="账号">
                <div class="form-value">
                  {{ formData.account }}
                </div>
              </el-form-item>
              
              <el-form-item label="昵称">
                <div class="form-value with-action">
                  <span>{{ formData.nickname }}</span>
                  <el-button type="text" class="edit-btn" @click="handleEdit('nickname')">修改</el-button>
                </div>
              </el-form-item>
              
              <el-form-item label="邮箱">
                <div class="form-value with-action">
                  <span>{{ formData.email || '暂无提供' }}</span>
                  <el-button type="text" class="edit-btn" @click="handleEdit('email')">绑定</el-button>
                </div>
              </el-form-item>
              
              <el-form-item label="手机">
                <div class="form-value with-action">
                  <span>{{ formData.phone }}</span>
                  <el-button type="text" class="edit-btn" @click="handleEdit('phone')">修改</el-button>
                </div>
              </el-form-item>
              
              <el-form-item label="密码">
                <div class="form-value with-action">
                  <span>暂无提供</span>
                  <el-button type="text" class="edit-btn" @click="handleEdit('password')">修改</el-button>
                </div>
              </el-form-item>
            </el-form>
          </div>
          
          <div class="profile-section">
            <h3>扩充账号资料</h3>
            <el-form :model="formData" label-width="120px">
              <el-form-item label="微信">
                <div class="form-value with-action">
                  <span>{{ formData.wechat || '未绑定' }}</span>
                  <el-button type="text" class="edit-btn" @click="handleWechatAction">
                    {{ formData.wechat ? '解绑' : '绑定' }}
                  </el-button>
                </div>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <!-- 其他菜单内容区域 -->
        <div v-else class="other-content">
          <div class="content-header">
            <h2>{{ currentTitle }}</h2>
          </div>
          <div class="content-body">
            <p class="empty-tip">请选择左侧菜单查看对应内容</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { 
  User, Message, HomeFilled, HelpFilled, ShoppingCart, 
  ChatDotRound, Clock, Menu, Money, CircleClose 
} from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

// 定义用户信息类型
interface UserInfo {
  account: string;
  nickname: string;
  avatar?: string;
  email?: string;
  phone: string;
  wechat?: string;
}

// 模拟用户数据
const userInfo: UserInfo = reactive({
  account: 'mHsgcq',
  nickname: '雲',
  phone: '18178186323'
});

// 表单数据
const formData = reactive({...userInfo});

// 最后登录时间
const lastLoginTime = '2025-12-04 00:58:02';

// 菜单状态管理
const activeMenu = ref('profile');
const currentTitle = ref('个人资料');
const currentPath = ref('/profile');

// 处理菜单点击
const handleMenuClick = (menu: string) => {
  activeMenu.value = menu;
  
  // 根据菜单设置标题和路径
  const menuConfig: Record<string, {title: string; path: string}> = {
    message: { title: '消息', path: '/message' },
    'house-manage': { title: '房源管理', path: '/house-manage' },
    qa: { title: '问答', path: '/qa' },
    'sell-house': { title: '卖房', path: '/sell-house' },
    comments: { title: '评论', path: '/comments' },
    profile: { title: '个人资料', path: '/profile' },
    'view-record': { title: '看房记录', path: '/view-record' }
  };
  
  currentTitle.value = menuConfig[menu]?.title || '';
  currentPath.value = menuConfig[menu]?.path || '';
};

// 处理工具点击
const handleToolClick = (tool: string) => {
  const toolConfig: Record<string, string> = {
    estimate: '估房价工具',
    loan: '房贷计算器'
  };
  
  ElMessage.info(`打开${toolConfig[tool]}功能`);
};

// 处理编辑操作
const handleEdit = (field: string) => {
  const fieldConfig: Record<string, string> = {
    nickname: '昵称',
    email: '邮箱',
    phone: '手机',
    password: '密码'
  };
  
  ElMessageBox.prompt(`请输入新的${fieldConfig[field]}`, `修改${fieldConfig[field]}`, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputValue: formData[field as keyof UserInfo] || '',
    // 根据字段类型设置输入验证
    inputValidator: (value) => {
      if (!value) return `请输入${fieldConfig[field]}`;
      if (field === 'phone' && !/^1[3-9]\d{9}$/.test(value)) {
        return '请输入正确的手机号码';
      }
      if (field === 'email' && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
        return '请输入正确的邮箱地址';
      }
      return true;
    }
  }).then(({ value }) => {
    formData[field as keyof UserInfo] = value;
    userInfo[field as keyof UserInfo] = value;
    ElMessage.success(`${fieldConfig[field]}修改成功`);
  }).catch(() => {
    // 取消操作
  });
};

// 处理微信绑定/解绑
const handleWechatAction = () => {
  if (formData.wechat) {
    ElMessageBox.confirm(
      '确定要解绑微信账号吗？',
      '解绑确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    ).then(() => {
      formData.wechat = '';
      ElMessage.success('微信解绑成功');
    }).catch(() => {
      // 取消操作
    });
  } else {
    ElMessage.info('打开微信扫码绑定');
    // 模拟绑定成功
    setTimeout(() => {
      formData.wechat = 'wxid_xxxxxx';
      ElMessage.success('微信绑定成功');
    }, 1500);
  }
};

// 处理注销账号
const handleLogout = () => {
  ElMessageBox.confirm(
    '确定要注销账号吗？此操作不可恢复！',
    '注销确认',
    {
      confirmButtonText: '确定注销',
      cancelButtonText: '取消',
      type: 'error',
    }
  ).then(() => {
    ElMessage.success('账号注销成功');
    // 实际应用中这里会跳转到登录页
  }).catch(() => {
    // 取消操作
  });
};
</script>

<style scoped>
.personal-center {
  min-height: 100vh;
  background-color: #f5f7fa;
  color: #333;
  font-size: 14px;
}

/* 顶部导航样式 */
.header-mod {
  background-color: #fff;
  padding: 15px 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.header-container {
  width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
}

.avatar {
  margin-right: 15px;
}

.user-avatar {
  width: 50px;
  height: 50px;
  background-color: #42b983;
}

.greeting {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 5px;
}

.account-info {
  color: #666;
  font-size: 12px;
}

.role {
  margin: 0 10px;
  padding: 2px 8px;
  background-color: #f0f9f4;
  color: #42b983;
  border-radius: 12px;
  font-size: 12px;
}

.login-time {
  margin-left: 10px;
}

.edit-profile {
  background-color: #42b983;
  border-color: #42b983;
}

.edit-profile:hover {
  background-color: #359e6d;
  border-color: #359e6d;
}

/* 二级导航样式 */
.header-sub-mod {
  background-color: #f5f7fa;
  padding: 10px 0;
  border-bottom: 1px solid #e5e9f2;
}

.sub-nav {
  width: 1200px;
  margin: 0 auto;
}

/* 主内容区样式 */
.container {
  width: 1200px;
  margin: 20px auto;
  display: flex;
  gap: 20px;
}

.clearfix::after {
  content: "";
  display: table;
  clear: both;
}

/* 左侧菜单样式 */
.side-menu {
  width: 220px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  float: left;
}

.menu-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.menu-list li {
  padding: 12px 20px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.2s;
}

.menu-list li:hover {
  background-color: #f5f7fa;
}

.menu-list li.active {
  background-color: #f0f9f4;
  border-left: 3px solid #42b983;
  color: #42b983;
}

.menu-list li el-icon {
  margin-right: 10px;
  width: 18px;
  height: 18px;
}

.tool-title {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
  padding-left: 4px;
}

.tool-list ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tool-list li {
  padding: 8px 20px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.2s;
}

.tool-list li:hover {
  background-color: #f5f7fa;
}

.tool-list li el-icon {
  margin-right: 10px;
  width: 16px;
  height: 16px;
}

.account-operation {
  padding: 15px 20px;
  border-top: 1px solid #e5e9f2;
  margin-top: 10px;
}

.logout-btn {
  color: #f56c6c;
  display: flex;
  align-items: center;
  padding: 5px 0;
  width: 100%;
  justify-content: flex-start;
}

.logout-btn el-icon {
  margin-right: 10px;
}

/* 右侧内容区样式 */
.right-content {
  flex: 1;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  float: right;
  min-height: 500px;
}

.content-header {
  padding: 15px 20px;
  border-bottom: 1px solid #e5e9f2;
}

.content-header h2 {
  font-size: 18px;
  margin: 0;
  font-weight: 500;
}

/* 个人资料内容样式 */
.profile-content {
  padding: 20px;
}

.profile-section {
  margin-bottom: 30px;
}

.profile-section h3 {
  font-size: 16px;
  margin: 0 0 15px 0;
  color: #666;
  padding-bottom: 8px;
  border-bottom: 1px solid #e5e9f2;
}

.el-form {
  margin-top: 20px;
}

.el-form-item {
  margin-bottom: 20px;
}

.avatar-upload {
  display: flex;
  align-items: center;
}

.big-avatar {
  width: 100px;
  height: 100px;
  background-color: #42b983;
  margin-right: 20px;
}

.form-value {
  line-height: 40px;
  color: #333;
}

.with-action {
  display: flex;
  align-items: center;
  gap: 15px;
}

.edit-btn {
  color: #42b983;
  padding: 0;
  height: auto;
}

.edit-btn:hover {
  color: #359e6d;
}

/* 其他内容区域样式 */
.other-content .content-body {
  padding: 50px;
  text-align: center;
}

.empty-tip {
  color: #999;
  font-size: 16px;
  padding: 30px 0;
}

/* 响应式处理 */
@media (max-width: 1200px) {
  .header-container, .sub-nav, .container {
    width: 96%;
    padding: 0 2%;
  }
}

@media (max-width: 768px) {
  .container {
    flex-direction: column;
  }
  
  .side-menu {
    width: 100%;
    margin-bottom: 20px;
  }
  
  .menu-list {
    display: flex;
    overflow-x: auto;
    flex-wrap: nowrap;
  }
  
  .menu-list li {
    flex: 0 0 auto;
    border-left: none;
    border-bottom: 3px solid transparent;
  }
  
  .menu-list li.active {
    border-left: none;
    border-bottom: 3px solid #42b983;
  }
  
  .tool-list, .account-operation {
    display: none;
  }
}
</style>