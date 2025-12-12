<template>
  <div class="team-list-container">
    <el-card class="search-card" shadow="never">
      <el-form :model="queryParams" :inline="true" label-width="80px">
        <el-form-item label="团队名称">
          <el-input 
            v-model="queryParams.teamName" 
            placeholder="请输入团队名称" 
            clearable 
            style="width: 200px" 
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col :span="1.5">
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增团队</el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="teamList"
        border
        stripe
      >
        <el-table-column label="ID" prop="id" width="80" align="center" />
        <el-table-column label="团队名称" prop="teamName" min-width="150" />
        <el-table-column label="团队经理" width="150" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.managerName" type="warning">{{ row.managerName }}</el-tag>
            <span v-else class="text-gray">未分配</span>
          </template>
        </el-table-column>
        <el-table-column label="成员数量" prop="memberCount" width="120" align="center">
          <template #default="{ row }">
            <el-tag effect="plain" type="info">{{ row.memberCount }} 人</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180" align="center" />
        <el-table-column label="操作" width="250" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View" @click="handleView(row)">详情</el-button>
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="650px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        label-width="100px"
      >
        <el-form-item label="团队名称" prop="teamName">
          <el-input v-model="form.teamName" placeholder="请输入团队名称" />
        </el-form-item>
        
        <el-form-item label="团队经理" prop="managerId">
          <el-select 
            v-model="form.managerId" 
            placeholder="请选择销售经理" 
            clearable 
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="item in managerOptions"
              :key="item.id"
              :label="item.realName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="团队成员" prop="memberIds">
           <el-transfer
              v-model="form.memberIds"
              filterable
              filter-placeholder="搜索成员"
              :titles="['待选销售', '已选成员']"
              :data="salesOptions"
              :props="{ key: 'id', label: 'realName' }"
            />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog
      v-model="detailVisible"
      title="团队详情"
      width="500px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="团队名称">{{ currentDetail?.teamName }}</el-descriptions-item>
        <el-descriptions-item label="团队经理">
          {{ currentDetail?.managerName }} 
          <span v-if="currentDetail?.managerPhone" class="text-gray">({{ currentDetail?.managerPhone }})</span>
        </el-descriptions-item>
        <el-descriptions-item label="成员数量">{{ currentDetail?.memberCount }} 人</el-descriptions-item>
        <el-descriptions-item label="成员列表">
           <div v-if="currentDetail?.members && currentDetail.members.length > 0" class="member-tags">
              <el-tag 
                v-for="member in currentDetail.members" 
                :key="member.userId" 
                class="mr-2 mb-2"
              >
                {{ member.realName }}
              </el-tag>
           </div>
           <span v-else class="text-gray">暂无成员</span>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentDetail?.createTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="TeamList">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Delete, Search, Refresh, View, Edit } from '@element-plus/icons-vue'
import { getTeamList, getTeamById, addTeam, updateTeam, deleteTeam, getEnableUsers } from '@/api/team'
import type { TeamDetail, TeamFormData, TeamQueryParams, UserOption } from '@/api/team/type'

// 状态定义
const loading = ref(false)
const teamList = ref<TeamDetail[]>([])
const total = ref(0)
const submitLoading = ref(false)

// 对话框控制
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('')

// 选项数据
const managerOptions = ref<UserOption[]>([]) // 角色3
const salesOptions = ref<UserOption[]>([])   // 角色2
const currentDetail = ref<TeamDetail | null>(null)

// 表单引用
const formRef = ref<FormInstance>()

// 查询参数
const queryParams = reactive<TeamQueryParams>({
  pageNum: 1,
  pageSize: 10,
  teamName: undefined
})

// 表单数据
const form = reactive<TeamFormData>({
  id: undefined,
  teamName: '',
  managerId: undefined,
  memberIds: []
})

// 表单校验规则
const rules: FormRules = {
  teamName: [
    { required: true, message: '请输入团队名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  managerId: [
    { required: true, message: '请选择团队经理', trigger: 'change' }
  ]
}

// 生命周期
onMounted(() => {
  loadTeamList()
})

// 加载团队列表
const loadTeamList = async () => {
  loading.value = true
  try {
    const res = await getTeamList(queryParams)
    // 兼容 HouseList.vue 中的响应处理逻辑
    const anyRes: any = res
    const apiResp = anyRes.data?.data ? anyRes.data : anyRes.data ?? anyRes
    const statusFlag = anyRes.data?.status ?? anyRes.status ?? apiResp.status

    if (statusFlag) {
      const page = apiResp.data ?? apiResp
      // 处理后端可能返回 page.records 或 page.list 的情况
      teamList.value = page.list || page.records || []
      total.value = page.total || 0
    } else {
      ElMessage.error((apiResp && apiResp.message) || '查询失败')
    }
  } catch (error) {
    console.error('加载团队列表失败:', error)
    ElMessage.error('加载数据失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 加载人员选项
const loadUserOptions = async () => {
  try {
    // 并发请求经理(3)和销售(2)列表
    const [managersRes, salesRes] = await Promise.all([
      getEnableUsers(3),
      getEnableUsers(2)
    ])
    
    // 处理经理列表
    const mRes: any = managersRes
    if (mRes.data?.status || mRes.status) {
       managerOptions.value = mRes.data?.data || mRes.data || []
    }
    
    // 处理销售列表
    const sRes: any = salesRes
    if (sRes.data?.status || sRes.status) {
       salesOptions.value = sRes.data?.data || sRes.data || []
    }

  } catch (error) {
    console.error('加载人员选项失败', error)
    ElMessage.warning('人员列表加载失败')
  }
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  loadTeamList()
}

// 重置
const handleReset = () => {
  queryParams.teamName = undefined
  queryParams.pageNum = 1
  queryParams.pageSize = 10
  loadTeamList()
}

// 分页变化
const handleSizeChange = () => {
  queryParams.pageNum = 1
  loadTeamList()
}

const handlePageChange = () => {
  loadTeamList()
}

// 重置表单
const resetForm = () => {
  form.id = undefined
  form.teamName = ''
  form.managerId = undefined
  form.memberIds = []
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 新增按钮
const handleAdd = () => {
  resetForm()
  loadUserOptions()
  dialogTitle.value = '新增团队'
  dialogVisible.value = true
}

// 编辑按钮
const handleEdit = async (row: TeamDetail) => {
  resetForm()
  loadUserOptions()
  dialogTitle.value = '编辑团队'
  
  try {
    const res = await getTeamById(row.id)
    const anyRes: any = res
    if (anyRes.data?.status || anyRes.status) {
       const data = anyRes.data?.data || anyRes.data
       form.id = data.id
       form.teamName = data.teamName
       form.managerId = data.managerId
       // 将成员对象数组转换为ID数组用于穿梭框回显
       form.memberIds = data.members ? data.members.map((m: any) => m.userId) : []
       dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const apiCall = form.id ? updateTeam : addTeam
        const res = await apiCall(form)
        
        const anyRes: any = res
        const apiResp = anyRes.data?.data ? anyRes.data : anyRes.data ?? anyRes
        const statusFlag = anyRes.data?.status ?? anyRes.status ?? apiResp.status
        
        if (statusFlag) {
          ElMessage.success(form.id ? '更新成功' : '新增成功')
          dialogVisible.value = false
          loadTeamList()
        } else {
          ElMessage.error((apiResp && apiResp.message) || '操作失败')
        }
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error('系统异常，请稍后重试')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 查看详情
const handleView = async (row: TeamDetail) => {
  try {
    const res = await getTeamById(row.id)
    const anyRes: any = res
    if (anyRes.data?.status || anyRes.status) {
       currentDetail.value = anyRes.data?.data || anyRes.data
       detailVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

// 删除
const handleDelete = async (row: TeamDetail) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除团队「${row.teamName}」吗？如果团队内尚有成员将无法删除。`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const res = await deleteTeam(row.id)
    const anyRes: any = res
    const apiResp = anyRes.data?.data ? anyRes.data : anyRes.data ?? anyRes
    const statusFlag = anyRes.data?.status ?? anyRes.status ?? apiResp.status

    if (statusFlag) {
      ElMessage.success('删除成功')
      loadTeamList()
    } else {
      ElMessage.error((apiResp && apiResp.message) || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error(error.message || '删除失败，请稍后重试')
    }
  }
}
</script>

<style scoped lang="scss">
.team-list-container {
  padding: 20px;

  .search-card,
  .toolbar-card,
  .table-card {
    margin-bottom: 20px;
  }

  .text-gray {
    color: #909399;
    font-size: 12px;
  }
  
  .member-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .mr-2 {
    margin-right: 8px;
  }
  
  .mb-2 {
    margin-bottom: 8px;
  }

  :deep(.el-card__body) {
    padding: 16px;
  }
  
  /* 调整穿梭框样式以适应弹窗 */
  :deep(.el-transfer-panel) {
    width: 200px;
  }
}
</style>