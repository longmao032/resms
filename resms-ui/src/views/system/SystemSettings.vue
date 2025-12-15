<template>
  <div class="settings-page">
    <el-card class="settings-card">
      <template #header>
        <div class="card-header">
          <el-icon><Setting /></el-icon>
          <span>系统设置</span>
        </div>
      </template>

      <el-form label-width="120px" class="settings-form">
        <!-- 界面偏好 -->
        <div class="section-title">
          <el-icon><Monitor /></el-icon>
          <span>界面偏好</span>
        </div>

        <!-- 主题切换 -->
        <el-form-item label="主题模式">
          <el-radio-group v-model="currentTheme" @change="handleThemeChange">
            <el-radio-button value="light">
              <el-icon><Sunny /></el-icon>
              浅色
            </el-radio-button>
            <el-radio-button value="dark">
              <el-icon><Moon /></el-icon>
              深色
            </el-radio-button>
          </el-radio-group>
        </el-form-item>

        <!-- 字体大小 -->
        <el-form-item label="字体大小">
          <el-radio-group v-model="currentFontSize" @change="handleFontSizeChange">
            <el-radio-button value="small">小</el-radio-button>
            <el-radio-button value="medium">中</el-radio-button>
            <el-radio-button value="large">大</el-radio-button>
          </el-radio-group>
          <div class="font-preview">
            <span :class="['preview-text', `preview-${currentFontSize}`]">字体预览效果</span>
          </div>
        </el-form-item>

        <el-divider />

        <!-- 通知设置 -->
        <div class="section-title">
          <el-icon><Bell /></el-icon>
          <span>通知设置</span>
        </div>

        <!-- 消息提醒开关 -->
        <el-form-item label="消息提醒">
          <el-switch
            v-model="notificationEnabled"
            @change="handleNotificationChange"
            active-text="开启"
            inactive-text="关闭"
          />
          <div class="setting-desc">开启后将在新消息到达时显示提醒</div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Setting, Monitor, Sunny, Moon, Bell } from '@element-plus/icons-vue'
import { useSettingsStore, type ThemeMode, type FontSize } from '@/stores/settingsStore'
import { ElMessage } from 'element-plus'

const settingsStore = useSettingsStore()

// 本地状态
const currentTheme = ref<ThemeMode>(settingsStore.themeMode)
const currentFontSize = ref<FontSize>(settingsStore.fontSize)
const notificationEnabled = ref(settingsStore.notificationEnabled)

// 主题切换
const handleThemeChange = (value: ThemeMode) => {
  settingsStore.setTheme(value)
  ElMessage.success(`已切换为${value === 'dark' ? '深色' : '浅色'}主题`)
}

// 字体大小切换
const handleFontSizeChange = (value: FontSize) => {
  settingsStore.setFontSize(value)
  const sizeText = { small: '小', medium: '中', large: '大' }
  ElMessage.success(`字体大小已设置为${sizeText[value]}`)
}

// 消息提醒切换
const handleNotificationChange = (value: boolean) => {
  settingsStore.setNotificationEnabled(value)
  ElMessage.success(`消息提醒已${value ? '开启' : '关闭'}`)
}

onMounted(() => {
  // 同步 store 中的值到本地状态
  currentTheme.value = settingsStore.themeMode
  currentFontSize.value = settingsStore.fontSize
  notificationEnabled.value = settingsStore.notificationEnabled
})
</script>

<style scoped lang="scss">
.settings-page {
  max-width: 800px;
  margin: 0 auto;
}

.settings-card {
  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 18px;
    font-weight: 600;
  }
}

.settings-form {
  padding: 20px 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #EBEEF5;
}

.font-preview {
  margin-top: 10px;
  padding: 12px 16px;
  background-color: #f5f7fa;
  border-radius: 4px;

  .preview-text {
    color: #606266;
  }

  .preview-small {
    font-size: 12px;
  }

  .preview-medium {
    font-size: 14px;
  }

  .preview-large {
    font-size: 16px;
  }
}

.setting-desc {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

:deep(.el-radio-button__inner) {
  display: flex;
  align-items: center;
  gap: 4px;
}

:deep(.el-divider) {
  margin: 30px 0;
}
</style>
