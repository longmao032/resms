<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-card class="filter-container" shadow="never">
      <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="68px">
        <el-form-item label="客户ID" prop="customerId">
          <el-input
            v-model="queryParams.customerId"
            placeholder="请输入客户ID"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 240px">
            <el-option label="待确认" :value="0" />
            <el-option label="已预约" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约方式" prop="appointType">
          <el-select v-model="queryParams.appointType" placeholder="请选择预约方式" clearable style="width: 240px">
            <el-option label="销售录入" :value="1" />
            <el-option label="线上申请" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <el-card class="toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            :icon="Plus"
            @click="handleAdd"
          >新增预约</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never" class="table-card">
      <el-table v-loading="loading" :data="appointmentList" border>
        <el-table-column label="预约ID" prop="id" width="80" align="center" />
        <el-table-column label="客户姓名" prop="customerName" align="center" />
        <el-table-column label="客户电话" prop="customerPhone" align="center" />
        <el-table-column label="房源编号" prop="houseNo" align="center" />
        <el-table-column label="房源描述" prop="houseDesc" min-width="200" show-overflow-tooltip align="center">
          <template #default="scope">
            <span>{{ scope.row.houseDesc }}</span>
            <span v-if="scope.row.layout" style="margin-left: 5px;">{{ scope.row.layout }}</span>
            <el-tag v-if="scope.row.houseType" size="small" type="info" style="margin-left: 5px;">
              {{ formatHouseType(scope.row.houseType).replace(/[()]/g, '') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="销售顾问" prop="salesName" align="center" />
        <el-table-column label="预约时间" prop="viewTime" width="160" align="center" />
        <el-table-column label="状态" align="center" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="预约方式" align="center" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.appointType === 1 ? 'info' : 'success'">
              {{ scope.row.appointType === 1 ? '销售录入' : '线上申请' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="200" fixed="right">
          <template #default="scope">
            <!-- 待确认 -> 确认 -->
            <el-button
              v-if="scope.row.status === 0"
              type="text"
              icon="Check"
              @click="handleConfirm(scope.row)"
            >确认</el-button>
            
            <!-- 已预约 -> 完成 -->
            <el-button
              v-if="scope.row.status === 1"
              type="text"
              icon="Finished"
              @click="handleComplete(scope.row)"
            >完成</el-button>
            
            <!-- 待确认/已预约 -> 取消 -->
            <el-button
              v-if="scope.row.status === 0 || scope.row.status === 1"
              type="text"
              icon="Close"
              class="danger-text"
              @click="handleCancel(scope.row)"
            >取消</el-button>
            
             <el-popconfirm
              v-if="scope.row.status === 2 || scope.row.status === 3"
              title="确定删除这条预约记录吗？"
              @confirm="handleDelete(scope.row)"
            >
              <template #reference>
                <el-button type="text" icon="Delete" class="danger-text">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 新增对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="客户" prop="customerId">
          <el-select v-model="form.customerId" placeholder="请选择客户" filterable style="width: 100%">
            <el-option
              v-for="item in customerOptions"
              :key="item.id"
              :label="`${item.realName} (${item.id})`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="房源" prop="houseId">
          <el-select v-model="form.houseId" placeholder="请选择房源" filterable style="width: 100%">
            <el-option
              v-for="item in houseOptions"
              :key="item.id"
              :label="`${item.houseNo} ${item.layout ? '('+item.layout+')' : ''}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="销售" prop="salesId">
          <el-select 
            v-model="form.salesId" 
            placeholder="请选择销售" 
            filterable 
            style="width: 100%"
            :disabled="userStore.userInfo?.roleType === 3"
          >
            <el-option
              v-for="item in salesOptions"
              :key="item.id"
              :label="`${item.realName} (${item.id})`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="预约时间" prop="viewTime">
          <el-date-picker
            v-model="form.viewTime"
            type="datetime"
            placeholder="选择日期时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 完成反馈对话框 -->
    <el-dialog title="完成带看" v-model="completeOpen" width="500px" append-to-body>
      <el-form :model="completeForm" label-width="80px">
        <el-form-item label="客户反馈">
          <el-input type="textarea" v-model="completeForm.feedback" placeholder="请输入客户反馈" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitComplete">确 定</el-button>
          <el-button @click="completeOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 取消原因对话框 -->
    <el-dialog title="取消预约" v-model="cancelOpen" width="500px" append-to-body>
      <el-form :model="cancelForm" label-width="80px">
        <el-form-item label="取消原因">
          <el-input type="textarea" v-model="cancelForm.reason" placeholder="请输入取消原因" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitCancel">确 定</el-button>
          <el-button @click="cancelOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, toRefs } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Check, Close, Finished } from '@element-plus/icons-vue'
import { 
  getAppointmentPage, 
  addAppointment, 
  confirmAppointment, 
  completeAppointment, 
  cancelAppointment,
  type AppointmentQueryDTO,
  type AppointmentDTO,
  getCustomerList,
  getHouseList,
  getSalesList
} from '@/api/appointment'
import type { ViewRecord } from '@/api/viewRecord/type'
import { useUserStore } from '@/stores/userStore'

const userStore = useUserStore()
const loading = ref(true)
const total = ref(0)
const appointmentList = ref<ViewRecord[]>([])
const title = ref('')
const open = ref(false)
const completeOpen = ref(false)
const cancelOpen = ref(false)

// 下拉选项
const customerOptions = ref<any[]>([])
const houseOptions = ref<any[]>([])
const salesOptions = ref<any[]>([])

const queryParams = reactive<AppointmentQueryDTO>({
  pageNum: 1,
  pageSize: 10,
  customerId: undefined,
  status: undefined,
  appointType: undefined
})

const formRef = ref()
const form = reactive<AppointmentDTO>({})
const completeForm = reactive({ id: 0, feedback: '' })
const cancelForm = reactive({ id: 0, reason: '' })

const rules = {
  customerId: [{ required: true, message: '客户ID不能为空', trigger: 'blur' }],
  houseId: [{ required: true, message: '房源ID不能为空', trigger: 'blur' }],
  salesId: [{ required: true, message: '销售ID不能为空', trigger: 'blur' }],
  viewTime: [{ required: true, message: '预约时间不能为空', trigger: 'blur' }]
}

// 获取状态样式
const getStatusType = (status: number) => {
  const map: Record<number, string> = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: 'info'
  }
  return map[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: number) => {
  const map: Record<number, string> = {
    0: '待确认',
    1: '已预约',
    2: '已完成',
    3: '已取消'
  }
  return map[status] || '未知'
}

// 格式化房源类型
const formatHouseType = (type: number) => {
  const map: Record<number, string> = {
    1: '二手房',
    2: '新房',
    3: '租房'
  }
  if (!type) return ''
  return map[type] ? `(${map[type]})` : ''
}

// 获取下拉数据
const getOptions = async () => {
  try {
    const [cRes, hRes, sRes] = await Promise.all([
      getCustomerList(),
      getHouseList(),
      getSalesList()
    ])
    customerOptions.value = cRes.data.records || []
    // 房源列表接口返回的是 Map { list: [], total: ... }
    houseOptions.value = hRes.data.list || [] 
    salesOptions.value = sRes.data.records || []
  } catch (error) {
    console.error('获取下拉数据失败', error)
  }
}

// 查询列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getAppointmentPage(queryParams)
    appointmentList.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const resetQuery = () => {
  queryParams.customerId = undefined
  queryParams.status = undefined
  queryParams.appointType = undefined
  handleQuery()
}

// 新增
const handleAdd = () => {
  resetForm()
  open.value = true
  title.value = '新增预约'
  
  // 如果是销售顾问（角色ID=3），默认选中自己且不可修改
  if (userStore.userInfo?.roleType === 3) {
      form.salesId = userStore.userInfo.userId
  }
}

// 提交新增
const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      form.appointType = 1 // 默认为销售录入
      await addAppointment(form)
      ElMessage.success('新增成功')
      open.value = false
      getList()
    }
  })
}

// 重置表单
const resetForm = () => {
  form.id = undefined
  form.customerId = undefined
  form.houseId = undefined
  form.salesId = undefined
  form.viewTime = undefined
}

// 取消新增
const cancel = () => {
  open.value = false
  resetForm()
}

// 确认
const handleConfirm = async (row: ViewRecord) => {
  try {
    await ElMessageBox.confirm('确认接受该预约申请吗？', '提示', {
      type: 'warning'
    })
    await confirmAppointment(row.id!)
    ElMessage.success('确认成功')
    getList()
  } catch {}
}

// 完成
const handleComplete = (row: ViewRecord) => {
  completeForm.id = row.id!
  completeForm.feedback = ''
  completeOpen.value = true
}

const submitComplete = async () => {
  if (!completeForm.feedback) {
    ElMessage.warning('请输入客户反馈')
    return
  }
  await completeAppointment(completeForm.id, completeForm.feedback)
  ElMessage.success('操作成功')
  completeOpen.value = false
  getList()
}

// 取消预约
const handleCancel = (row: ViewRecord) => {
  cancelForm.id = row.id!
  cancelForm.reason = ''
  cancelOpen.value = true
}

const submitCancel = async () => {
  if (!cancelForm.reason) {
    ElMessage.warning('请输入取消原因')
    return
  }
  await cancelAppointment(cancelForm.id, cancelForm.reason)
  ElMessage.success('操作成功')
  cancelOpen.value = false
  getList()
}

// 删除（仅用于清理数据，实际业务可能不需要物理删除，这里假设调用通用删除接口）
const handleDelete = async (row: ViewRecord) => {
    // 这里暂时没有专门的删除接口，如果复用 viewRecord 的删除
    // await deleteViewRecord(row.id)
    // ElMessage.success('删除成功')
    // getList()
    ElMessage.warning('暂不支持删除')
}

onMounted(() => {
  getList()
  getOptions()
})
</script>

<style scoped>
.danger-text {
  color: #f56c6c;
}
.toolbar-card {
  margin-bottom: 20px;
}
.table-card {
  min-height: 500px;
}
</style>
