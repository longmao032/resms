<template>
  <div class="page-container">
    <div class="page-header">
      <h2>用户管理</h2>
      <div class="header-actions">
        <el-button v-if="isAdmin" type="primary" @click="handleAdd">
          <el-icon>
            <Plus />
          </el-icon>
          新增用户
        </el-button>
        <el-button v-if="isAdmin" type="warning" @click="handleBatchEnable" :disabled="selectedUsers.length === 0">
          <el-icon>
            <Check />
          </el-icon>
          批量启用
        </el-button>
        <el-button v-if="isAdmin" type="danger" @click="handleBatchDisable" :disabled="selectedUsers.length === 0">
          <el-icon>
            <Close />
          </el-icon>
          批量禁用
        </el-button>
        <el-button v-if="isAdmin" type="danger" @click="handleBatchDelete" :disabled="selectedUsers.length === 0">
          <el-icon>
            <Delete />
          </el-icon>
          批量删除
        </el-button>
      </div>
    </div>

    <div class="page-content">
      <!-- 搜索表单 -->
      <el-card class="search-card">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="用户名">
            <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable style="width: 120px" />
          </el-form-item>
          <el-form-item label="真实姓名">
            <el-input v-model="searchForm.realName" placeholder="请输入真实姓名" clearable style="width: 120px" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable style="width: 130px" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="searchForm.email" placeholder="请输入邮箱" clearable style="width: 140px" />
          </el-form-item>
          <el-form-item label="角色">
            <el-select v-model="searchForm.roleType" placeholder="请选择角色" clearable style="width: 120px">
              <el-option v-for="role in roleOptions" :key="role.value" :label="role.label" :value="role.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 100px">
              <el-option label="正常" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon>
                <Search />
              </el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon>
                <Refresh />
              </el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 用户列表 -->
      <el-card>
        <template #header>
          <div class="card-header">
            <span>用户列表</span>
          </div>
        </template>

        <el-table :data="userList" style="width: 100%" @selection-change="handleSelectionChange" v-loading="loading">
          <el-table-column v-if="isAdmin" type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" min-width="120" />
          <el-table-column prop="realName" label="真实姓名" width="120" />
          <el-table-column prop="phone" label="手机号" width="120" />
          <el-table-column prop="email" label="邮箱" min-width="140" show-overflow-tooltip />
          <el-table-column prop="roleName" label="角色" width="100" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                {{ scope.row.statusText }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="170" />
          <el-table-column label="操作" fixed="right" :width="isAdmin ? 260 : 80">
            <template #default="scope">
              <div style="display: flex; align-items: center; justify-content: flex-start; gap: 4px;">
                <el-tooltip v-if="isAdmin" content="编辑" placement="top">
                  <el-button link type="primary" :icon="Edit" @click="handleEdit(scope.row)" />
                </el-tooltip>
                <el-tooltip content="聊天" placement="top">
                  <el-button link type="success" :icon="ChatDotRound" @click="handleChat(scope.row)" />
                </el-tooltip>
                <el-tooltip v-if="isAdmin" content="重置密码" placement="top">
                  <el-button link type="warning" :icon="Key" @click="handleResetPassword(scope.row)" />
                </el-tooltip>
                <el-tooltip v-if="isAdmin" :content="scope.row.status === 1 ? '禁用' : '启用'" placement="top">
                  <el-button link :type="scope.row.status === 1 ? 'danger' : 'success'"
                    :icon="scope.row.status === 1 ? Lock : Unlock" @click="handleToggleStatus(scope.row)"
                    :disabled="scope.row.id === 1" />
                </el-tooltip>
                <el-tooltip v-if="isAdmin" content="删除" placement="top">
                  <el-button link type="danger" :icon="Delete" @click="handleDelete(scope.row)"
                    :disabled="scope.row.id === 1" />
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
            :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
            @current-change="handleCurrentChange" />
        </div>
      </el-card>
    </div>

    <!-- 新增/编辑用户对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" @close="handleDialogClose">
      <el-form ref="userFormRef" :model="userForm" :rules="userFormRules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="userForm.username" placeholder="请输入用户名" :disabled="isEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="userForm.realName" placeholder="请输入真实姓名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" v-if="!isEdit">
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input v-model="userForm.password" type="password" placeholder="请输入密码" show-password />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="userForm.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 编辑时的密码修改区域 -->
        <el-row :gutter="20" v-if="isEdit">
          <el-col :span="24">
            <el-divider>密码修改（可选）</el-divider>
            <p class="password-tip">如果不需要修改密码，请留空此区域</p>
          </el-col>
          <el-col :span="12">
            <el-form-item label="新密码" prop="password">
              <el-input v-model="userForm.password" type="password" placeholder="请输入新密码" show-password />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input v-model="userForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="userForm.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userForm.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="角色" prop="roleType">
              <el-select v-model="userForm.roleType" placeholder="请选择角色" style="width: 100%">
                <el-option v-for="role in roleOptions" :key="role.value" :label="role.label" :value="role.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="userForm.status" placeholder="请选择状态" style="width: 100%"
                :disabled="userForm.id === 1">
                <el-option label="正常" :value="1" />
                <el-option label="禁用" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Search,
  Refresh,
  Check,
  Close,
  Delete,
  ChatDotRound,
  Edit,
  Key,
  Lock,
  Unlock
} from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import {
  reqUserPage,
  reqUserDetail,
  reqUserSave,
  reqUserUpdate,
  reqUserDelete,
  reqUserBatchDelete,
  reqUserStatusUpdate,
  reqUserPasswordReset,
  reqCheckUsername,
  reqCheckPhone
} from '@/api/user'
import { reqRoleAll } from '@/api/role'
import type {
  UserQueryParams,
  UserVO,
  UserPageVO,
  UserSaveParams,
  UserStatusParams,
  RoleOption
} from '@/api/user/type'
import type { RoleVO } from '@/api/role/type'
import { useChatStore } from '@/stores/chatStore'
import { useUserStore } from '@/stores/userStore'


const router = useRouter()
const chatStore = useChatStore()
const userStore = useUserStore()

const isAdmin = computed(() => userStore.userInfo?.roleType === 1)

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const userList = ref<UserVO[]>([])
const selectedUsers = ref<UserVO[]>([])

// ... (search form and other logic remains same)

// 发起聊天
const handleChat = async (row: UserVO) => {
  if (row.id === userStore.userInfo?.id) {
    ElMessage.warning('不能和自己聊天')
    return
  }
  try {
    await chatStore.startPrivateChat(row.id)
    router.push('/chat')
  } catch (error) {
    console.error('发起聊天失败', error)
    ElMessage.error('无法发起会话')
  }
}

// ... (rest of the script)

// 搜索表单
const searchForm = reactive<UserQueryParams>({
  pageNum: 1,
  pageSize: 10,
  username: '',
  realName: '',
  phone: '',
  email: '',
  roleType: undefined,
  status: undefined,
  createTimeBegin: '',
  createTimeEnd: ''
})

// 角色选项 - 从后端读取，确保与 tb_role.id 对齐
const roleOptions = ref<RoleOption[]>([])

// 获取角色选项
const getRoleOptions = async () => {
  try {
    const res: any = await reqRoleAll()
    // request.ts 已经返回 response.data（通常是 ResponseResult），这里兼容两种结构：
    // 1) ResponseResult<{...}>: { code, status, data: RoleVO[] }
    // 2) 直接数组 RoleVO[]（少数情况下）
    const roles: RoleVO[] = Array.isArray(res) ? res : (Array.isArray(res?.data) ? res.data : [])

    roleOptions.value = roles
        .slice()
        .sort((a: any, b: any) => (a.id ?? 0) - (b.id ?? 0))
        .map((role: any) => ({
        value: role.id,
        label: role.roleName
      }))

    // 新增用户时，若默认值不在选项内则选择第一个角色
    const hasDefault = roleOptions.value.some(r => r.value === userForm.roleType)
    if (!isEdit.value && roleOptions.value.length > 0 && (!userForm.roleType || !hasDefault)) {
      userForm.roleType = roleOptions.value[0]?.value as any
    }
  } catch (error) {
    console.error('获取角色选项失败', error)
  }
}

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const isEdit = ref(false)
const userFormRef = ref<FormInstance>()

const userForm = reactive<UserSaveParams>({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  email: '',
  avatar: '',
  roleType: 5,
  status: 1,
  id: undefined
})

// 表单验证规则
const userFormRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在3到50个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    {
      validator: (rule: any, value: string, callback: any) => {
        // 新增时密码必填，编辑时可选
        if (!isEdit.value && !value) {
          callback(new Error('请输入密码'))
        } else if (value && value.length < 6) {
          callback(new Error('密码长度不能少于6个字符'))
        } else if (value && value.length > 100) {
          callback(new Error('密码长度不能超过100个字符'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    {
      validator: (rule: any, value: string, callback: any) => {
        // 新增时确认密码必填，编辑时如果输入了密码则需要确认密码
        if (!isEdit.value && !value) {
          callback(new Error('请再次输入密码'))
        } else if (value && value !== userForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { max: 50, message: '真实姓名长度不能超过50个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  roleType: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
})

// 获取用户列表
const getUserList = async () => {
  try {
    loading.value = true
    const params = {
      ...searchForm,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    const response = await reqUserPage(params)
    if (response.code === 200) {
      const pageData: UserPageVO = response.data
      userList.value = pageData.records
      total.value = pageData.total
    } else {
      ElMessage.error(response.message || '获取用户列表失败')
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getUserList()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    username: '',
    realName: '',
    phone: '',
    email: '',
    roleType: undefined,
    status: undefined,
    createTimeBegin: '',
    createTimeEnd: ''
  })
  currentPage.value = 1
  getUserList()
}

// 分页大小变化
const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
  getUserList()
}

// 页码变化
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  getUserList()
}

// 选择变化
const handleSelectionChange = (selection: UserVO[]) => {
  selectedUsers.value = selection
}

// 新增用户
const handleAdd = () => {
  dialogTitle.value = '新增用户'
  isEdit.value = false
  Object.assign(userForm, {
    username: '',
    password: '',
    confirmPassword: '',
    realName: '',
    phone: '',
    email: '',
    avatar: '',
    roleType: 5,
    status: 1
  })
  dialogVisible.value = true
}

// 编辑用户
const handleEdit = async (row: UserVO) => {
  try {
    const response = await reqUserDetail(row.id)
    if (response.code === 200) {
      dialogTitle.value = '编辑用户'
      isEdit.value = true
      const userData = response.data
      Object.assign(userForm, {
        id: userData.id,
        username: userData.username,
        password: '', // 编辑时不预填密码
        confirmPassword: '', // 编辑时不预填确认密码
        realName: userData.realName,
        phone: userData.phone,
        email: userData.email,
        avatar: userData.avatar,
        roleType: userData.roleType,
        status: userData.status
      })
      dialogVisible.value = true
    } else {
      ElMessage.error(response.data.message || '获取用户信息失败')
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败')
    console.error(error)
  }
}

// 删除用户
const handleDelete = async (row: UserVO) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${row.realName}" 吗？此操作不可恢复！`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await reqUserDelete(row.id)
    if (response.code === 200) {
      ElMessage.success('删除用户成功')
      getUserList()
    } else {
      ElMessage.error(response.message || '删除用户失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除用户失败')
      console.error(error)
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    const userIds = selectedUsers.value.map(user => user.id)
    await ElMessageBox.confirm(
      `确定要删除选中的 ${userIds.length} 个用户吗？此操作不可恢复！`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await reqUserBatchDelete({ userIds: userIds })
    if (response.code === 200) {
      ElMessage.success('批量删除用户成功')
      selectedUsers.value = []
      getUserList()
    } else {
      ElMessage.error(response.message || '批量删除用户失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除用户失败')
      console.error(error)
    }
  }
}

// 批量启用
const handleBatchEnable = async () => {
  await handleBatchStatusUpdate(1, '批量启用用户成功')
}

// 批量禁用
const handleBatchDisable = async () => {
  await handleBatchStatusUpdate(0, '批量禁用用户成功')
}

// 批量状态更新
const handleBatchStatusUpdate = async (status: number, successMessage: string) => {
  try {
    const userIds = selectedUsers.value.map(user => user.id)
    const params: UserStatusParams = {
      userIds,
      status
    }

    const response = await reqUserStatusUpdate(params)
    if (response.code === 200) {
      ElMessage.success(successMessage)
      selectedUsers.value = []
      getUserList()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
    console.error(error)
  }
}

// 切换用户状态
const handleToggleStatus = async (row: UserVO) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    const params: UserStatusParams = {
      userIds: [row.id],
      status: newStatus
    }

    const response = await reqUserStatusUpdate(params)
    if (response.code === 200) {
      ElMessage.success(`${newStatus === 1 ? '启用' : '禁用'}用户成功`)
      getUserList()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
    console.error(error)
  }
}

// 重置密码
const handleResetPassword = async (row: UserVO) => {
  try {
    await ElMessageBox.confirm(
      `确定要重置用户 "${row.realName}" 的密码吗？重置后密码为：123456`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await reqUserPasswordReset(row.id)
    if (response.code === 200) {
      ElMessage.success('重置密码成功')
    } else {
      ElMessage.error(response.message || '重置密码失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重置密码失败')
      console.error(error)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!userFormRef.value) return

  try {
    await userFormRef.value.validate()
    submitLoading.value = true

    let submitData = { ...userForm }

    // 编辑模式下，如果密码为空，则不传递密码字段，避免覆盖现有密码
    if (isEdit.value && !submitData.password) {
      delete submitData.password
      delete submitData.confirmPassword
    }

    let response
    if (isEdit.value) {
      response = await reqUserUpdate(submitData)
    } else {
      response = await reqUserSave(submitData)
    }

    if (response.code === 200) {
      ElMessage.success(`${isEdit.value ? '编辑' : '新增'}用户成功`)
      dialogVisible.value = false
      getUserList()
    } else {
      ElMessage.error(response.message || `${isEdit.value ? '编辑' : '新增'}用户失败`)
    }
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

// 关闭对话框
const handleDialogClose = () => {
  userFormRef.value?.resetFields()
  dialogVisible.value = false
}

// 组件挂载
onMounted(() => {
  getUserList()
  getRoleOptions()
})
</script>

<style scoped lang="scss">
.page-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  h2 {
    margin: 0;
    color: #333;
  }

  .header-actions {
    display: flex;
    gap: 10px;
  }
}

.search-card {
  margin-bottom: 20px;

  .search-form {
    .el-form-item {
      margin-bottom: 16px;
    }
  }
}

.page-content {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
}

.password-tip {
  color: #909399;
  font-size: 12px;
  margin: 0;
  margin-bottom: 16px;
}

// 响应式设计
@media (max-width: 768px) {
  .page-container {
    padding: 10px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;

    .header-actions {
      width: 100%;
      justify-content: flex-end;
    }
  }

  .search-card .search-form {
    .el-form-item {
      display: block;
      margin-right: 0;

      .el-input,
      .el-select {
        width: 100% !important;
      }
    }
  }

  .el-table {
    font-size: 12px;

    .el-table__cell {
      padding: 8px 0;
    }
  }
}

@media (max-width: 480px) {
  .header-actions {
    flex-direction: column;
    width: 100%;

    .el-button {
      width: 100%;
      margin-bottom: 5px;
    }
  }

  .el-dialog {
    width: 95% !important;
    margin: 5vh auto;
  }
}
</style>
