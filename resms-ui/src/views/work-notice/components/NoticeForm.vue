<template>
  <el-dialog
    :title="title"
    v-model="visible"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" label-width="80px" ref="formRef" :rules="rules">
      <el-form-item label="标题" prop="noticeTitle">
        <el-input v-model="form.noticeTitle" placeholder="请输入通知标题" />
      </el-form-item>
      <el-form-item label="类型" prop="noticeType">
        <el-select v-model="form.noticeType" placeholder="请选择类型">
          <el-option label="系统通知" :value="1" />
          <el-option label="任务分配" :value="2" />
          <el-option label="交易提醒" :value="3" />
          <el-option label="佣金通知" :value="4" />
          <el-option label="团队通知" :value="5" />
        </el-select>
      </el-form-item>
      <el-form-item label="优先级" prop="priority">
        <el-radio-group v-model="form.priority">
          <el-radio :label="1">紧急</el-radio>
          <el-radio :label="2">普通</el-radio>
          <el-radio :label="3">低</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="接收范围" prop="receiverType">
        <el-radio-group v-model="form.receiverType" :disabled="roleType !== 1 && roleType !== 4">
          <el-radio :label="4" v-if="roleType === 1 || roleType === 4">全体成员</el-radio>
          <el-radio :label="3" v-if="roleType === 1 || roleType === 3 || roleType === 4">本部门</el-radio>
          <el-radio :label="2" v-if="roleType === 1 || roleType === 4">指定角色</el-radio>
          <el-radio :label="1" v-if="roleType === 1 || roleType === 4">指定用户</el-radio> 
        </el-radio-group>
      </el-form-item>
      <!-- 接收人选择器 -->
      <el-form-item label="选择用户" prop="receiverIds" v-if="form.receiverType === 1">
        <el-select
          v-model="selectedUserIds"
          multiple
          filterable
          placeholder="请选择用户"
          style="width: 100%"
        >
          <el-option
            v-for="item in userOptions"
            :key="item.id"
            :label="item.realName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="选择角色" prop="receiverIds" v-if="form.receiverType === 2">
        <el-select
          v-model="selectedRoleIds"
          multiple
          placeholder="请选择角色"
          style="width: 100%"
        >
          <el-option
            v-for="item in roleOptions"
            :key="item.id"
            :label="item.roleName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="选择团队" prop="receiverIds" v-if="form.receiverType === 3">
        <el-select
          v-model="selectedTeamIds"
          multiple
          placeholder="请选择团队"
          style="width: 100%"
        >
          <el-option
            v-for="item in teamOptions"
            :key="item.id"
            :label="item.teamName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="内容" prop="noticeContent">
        <el-input
          type="textarea"
          v-model="form.noticeContent"
          :rows="6"
          placeholder="请输入通知内容"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">发送</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed, onMounted } from 'vue'
import { sendNotice, type WorkNotice } from '@/api/notice'
import { getTeamList } from '@/api/team'
import { reqRoleAll } from '@/api/role'
import { reqUserPage } from '@/api/user' // Use page for now, ideally list
import { useUserStore } from '@/stores/userStore'
import { ElMessage } from 'element-plus'

const props = defineProps<{
  modelValue: boolean
}>()

const emit = defineEmits(['update:modelValue', 'success'])

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo || ({} as any))
const roleType = computed(() => userInfo.value.roleType)

const visible = ref(false)
const title = ref('发布通知')
const formRef = ref()

const form = reactive<WorkNotice>({
  noticeTitle: '',
  noticeContent: '',
  noticeType: 1,
  priority: 2,
  receiverType: 4,
  receiverIds: '[]'
})

// Options
const userOptions = ref<any[]>([])
const roleOptions = ref<any[]>([])
const teamOptions = ref<any[]>([])

// Temporary bindings for Selects
const selectedUserIds = ref<number[]>([])
const selectedRoleIds = ref<number[]>([])
const selectedTeamIds = ref<number[]>([])

// Fetch Data
const fetchData = async () => {
    // Roles
    try {
        const res = await reqRoleAll()
        roleOptions.value = res.data
    } catch {}
    
    // Teams (Fetch All)
    try {
        const res = await getTeamList({ pageNum: 1, pageSize: 1000 })
        if (res.data?.records) {
            teamOptions.value = res.data.records
        }
    } catch {}

    // Users (Fetch All - might be heavy, but ok for now)
    try {
        const res = await reqUserPage({ pageNum: 1, pageSize: 1000 })
        if (res.data?.records) {
            userOptions.value = res.data.records
        }
    } catch {}
}

// Watchers to sync arrays to string
watch(selectedUserIds, (val) => form.receiverIds = JSON.stringify(val))
watch(selectedRoleIds, (val) => form.receiverIds = JSON.stringify(val))
watch(selectedTeamIds, (val) => form.receiverIds = JSON.stringify(val))

// Watch receiver type change to reset selections
watch(() => form.receiverType, (newType) => {
    // Don't reset if initializing defaults for Manager/Finance unless manually changed?
    // For now simple reset to avoid dirty state, but be careful with Admin defaults.
    // Actually Admin defaults to 'All' (4), so no IDs needed.
    if (newType === 4 || !newType) {
        form.receiverIds = '[]'
    } else {
         // If switching to a specific type, ensure we have data
         // (Data is fetched on mount/open)
        if (newType === 1) selectedUserIds.value = []
        if (newType === 2) selectedRoleIds.value = []
        if (newType === 3) selectedTeamIds.value = []
         
         // Special Defaults Re-apply?
         // If Finance switches to Roles, maybe re-select Sales? 
         // Let's rely on manual selection for specific overrides, 
         // but initial open logic handles the defaults.
    }
})

// Dynamic Rules
const rules = {
  noticeTitle: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  noticeContent: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  receiverIds: [{ 
      validator: (rule: any, value: any, callback: any) => {
          if (form.receiverType !== 4 && (!value || value === '[]')) {
              callback(new Error('请选择接收对象'))
          } else {
              callback()
          }
      }, 
      trigger: 'change' 
  }]
}

// Watch visibility to initialize defaults based on role
watch(() => props.modelValue, async (val) => {
  visible.value = val
  if (val) {
    // Reset selections
    selectedUserIds.value = []
    selectedRoleIds.value = []
    selectedTeamIds.value = []
  
    const rType = roleType.value as number
    
    // Initialize Data
    await fetchData()

    if (rType === 3) {
      // Manager: Send to Team Default
      form.receiverType = 3
      const uInfo = userInfo.value as any
      const myTeam = teamOptions.value.find((t: any) => t.managerId === uInfo.id)
      if (myTeam) {
        selectedTeamIds.value = [myTeam.id] // Sync setter will update form.receiverIds
      }
    } else if (rType === 4) {
      // Finance: Default to Sales Roles
      form.noticeType = 4 
      form.receiverType = 2
      selectedRoleIds.value = [2, 3] // Sales & Manager
    } else {
      // Admin
      form.receiverType = 4 // All
      form.receiverIds = '[]'
    }
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const handleClose = () => {
  visible.value = false
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        await sendNotice(form)
        ElMessage.success('发送成功')
        visible.value = false
        emit('success')
        formRef.value.resetFields()
      } catch (error) {
        console.error(error)
      }
    }
  })
}
</script>
