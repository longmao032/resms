<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="filter-container">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="通知标题" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryForm.noticeType" placeholder="全部类型" clearable>
            <el-option label="系统通知" :value="1" />
            <el-option label="任务分配" :value="2" />
            <el-option label="交易提醒" :value="3" />
            <el-option label="佣金通知" :value="4" />
            <el-option label="团队通知" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.isRead" placeholder="全部状态" clearable>
            <el-option label="未读" :value="0" />
            <el-option label="已读" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button v-if="canSend" type="success" @click="handleSend">发布通知</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 通知列表 -->
    <el-card shadow="never" class="list-container">
      <div v-loading="loading">
        <div v-if="noticeList.length === 0" class="empty-data">
          <el-empty description="暂无通知" />
        </div>
        
        <div v-else class="notice-list">
          <div v-for="item in noticeList" :key="item.id" 
               class="notice-item" 
               :class="{ 'unread': !item.isRead }"
               @click="handleDetail(item)">
            
            <div class="notice-main">
              <div class="notice-header">
                <el-tag :type="getTypeTag(item.noticeType)" size="small" class="type-tag">
                  {{ getTypeLabel(item.noticeType) }}
                </el-tag>
                <span class="notice-title">{{ item.noticeTitle }}</span>
                <span class="notice-time">{{ formatTime(item.sendTime) }}</span>
              </div>
              
              <div class="notice-body">
                <div class="content-preview">{{ item.noticeContent }}</div>
              </div>
              
              <div class="notice-footer">
                <span class="sender">
                  <el-icon><User /></el-icon> {{ item.senderName }}
                </span>
                <span class="priority" :class="'p-' + item.priority">
                  <el-icon><Warning /></el-icon> {{ getPriorityLabel(item.priority) }}
                </span>
                <span v-if="canWithdraw(item)" class="action-btn">
                  <el-button type="link" size="small" @click.stop="handleWithdraw(item)">撤回</el-button>
                </span>
                 <span v-if="canManage" class="action-btn">
                  <el-button type="link" size="small" @click.stop="handleDelete(item)">删除</el-button>
                </span>
              </div>
            </div>

            <div class="notice-action" v-if="!item.isRead">
              <div class="red-dot"></div>
            </div>
          </div>
        </div>
      </div>


      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryForm.pageNum"
          v-model:page-size="queryForm.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>
    </el-card>

    <!-- 发送弹窗 -->
    <NoticeForm v-model="formVisible" @success="handleQuery" />

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      :title="currentNotice.noticeTitle"
      width="50%"
      destroy-on-close
    >
      <div class="notice-detail">
        <div class="detail-header">
          <span class="time">发送时间：{{ formatTime(currentNotice.sendTime) }}</span>
          <span class="sender">发送人：{{ currentNotice.senderName }}</span>
        </div>
        <div class="detail-content">
          {{ currentNotice.noticeContent }}
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailVisible = false">关闭</el-button>
          <el-button 
            v-if="currentNotice.routerPath" 
            type="primary" 
            @click="handleJump(currentNotice)"
          >
            前往处理
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { getNoticeList, readNotice, deleteNotice } from '@/api/notice'
import { useUserStore } from '@/stores/userStore'
import { useNoticeStore } from '@/stores/noticeStore'
import { User, Warning } from '@element-plus/icons-vue'
import NoticeForm from '@/views/work-notice/components/NoticeForm.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useRouter } from 'vue-router'

const router = useRouter()

// Store
const userStore = useUserStore()
const noticeStore = useNoticeStore()
const userInfo = computed(() => userStore.userInfo || ({} as any))
const roleType = computed(() => userInfo.value.roleType)

// Data
const loading = ref(false)
const formVisible = ref(false)
const detailVisible = ref(false)
const currentNotice = ref<any>({})
const noticeList = ref<any[]>([])
const total = ref(0)
const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  noticeType: undefined,
  isRead: undefined
})

// Permissions
const canSend = computed(() => [1, 3, 4].includes(roleType.value)) // Admin, Manager, Finance
const canManage = computed(() => roleType.value === 1) // Admin only for delete?

// Methods
const canWithdraw = (item: any) => {
  if (roleType.value === 1) return true // Admin
  if (item.senderId === userInfo.value.id) return true // Self
  if (roleType.value === 3 && item.receiverType === 3) return true // Manager & Team Notice
  if (roleType.value === 4 && item.noticeType === 4) return true // Finance & Commission Notice
  return false
}

const handleWithdraw = async (item: any) => {
  try {
    await ElMessageBox.confirm('确定要撤回这条通知吗？', '提示', { type: 'warning' })
    await request({
      url: `/work-notice/withdraw/${item.id}`,
      method: 'post'
    })
    ElMessage.success('撤回成功')
    handleQuery()
  } catch {}
}

const handleDelete = async (item: any) => {
  try {
     await ElMessageBox.confirm('确定要删除这条通知吗？', '警告', { type: 'error' })
     await deleteNotice(item.id)
     ElMessage.success('删除成功')
     handleQuery()
     // 如果删除的是未读消息，也需要刷新状态
     noticeStore.fetchUnread()
  } catch {}
}

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getNoticeList(queryForm)
    if (res.data) {
      noticeList.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.pageNum = 1
  queryForm.keyword = ''
  queryForm.noticeType = undefined
  queryForm.isRead = undefined
  handleQuery()
}

const handleSend = () => {
  formVisible.value = true
}

const handleDetail = async (item: any) => {
  currentNotice.value = item
  detailVisible.value = true
  
  if (!item.isRead) {
    try {
      await readNotice(item.id)
      item.isRead = 1
      // Notify store to update unread count
      noticeStore.fetchUnread()
    } catch (e) {
      console.error(e)
    }
  }
}

const handleJump = (item: any) => {
  detailVisible.value = false
  if (item.routerPath) {
    router.push(item.routerPath)
  }
}

// Utils
const getTypeTag = (type: number) => {
  const map: Record<number, string> = { 1: 'danger', 2: 'warning', 3: 'primary', 4: 'success', 5: 'info' }
  return map[type] || 'info'
}

const getTypeLabel = (type: number) => {
  const map: Record<number, string> = { 1: '系统通知', 2: '任务分配', 3: '交易提醒', 4: '佣金通知', 5: '团队通知' }
  return map[type] || '未知'
}

const getPriorityLabel = (p: number) => {
  return p === 1 ? '紧急' : p === 2 ? '普通' : '低'
}

const formatTime = (time: string) => {
  return time ? time.replace('T', ' ') : ''
}

// Lifecycle
onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.filter-container {
  margin-bottom: 20px;
}
.notice-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.notice-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  background-color: #fff;
}
.notice-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}
.notice-item.unread {
  background-color: #fdf6ec; 
  border-color: #faecd8;
}

.notice-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}
.type-tag {
  margin-right: 10px;
}
.notice-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-right: 15px;
}
.notice-time {
  font-size: 12px;
  color: #909399;
}
.content-preview {
  color: #606266;
  font-size: 14px;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 800px;
}
.notice-footer {
  display: flex;
  gap: 20px;
  font-size: 12px;
  color: #909399;
}
.notice-footer span {
  display: flex;
  align-items: center;
  gap: 4px;
}
.priority.p-1 { color: #f56c6c; }

.red-dot {
  width: 8px;
  height: 8px;
  background: #f56c6c;
  border-radius: 50%;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* Detail Dialog Styles */
.detail-header {
  display: flex;
  gap: 20px;
  color: #909399;
  font-size: 13px;
  margin-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 10px;
}
.detail-content {
  font-size: 15px;
  line-height: 1.6;
  color: #303133;
  min-height: 100px;
  white-space: pre-wrap; /* Preserve line breaks */
}
</style>
