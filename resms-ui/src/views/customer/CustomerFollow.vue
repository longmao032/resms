<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="带看时间">
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
            end-placeholder="结束日期" value-format="YYYY-MM-DD HH:mm:ss" @change="handleDateChange" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">

      <el-table v-loading="loading" :data="tableData" border style="width: 100%">
        <el-table-column prop="viewTime" label="带看时间" width="170">
          <template #default="{ row }">{{ formatTime(row.viewTime) }}</template>
        </el-table-column>
        <el-table-column prop="customerName" label="客户姓名" width="120" />
        <el-table-column prop="customerPhone" label="客户电话" width="130" />
        <el-table-column prop="houseNo" label="房源编号" width="140" />
        <el-table-column prop="houseDesc" label="房源描述" show-overflow-tooltip />
        <el-table-column prop="customerFeedback" label="客户反馈" show-overflow-tooltip />
        <el-table-column prop="followAdvice" label="跟进建议" show-overflow-tooltip />
        <el-table-column prop="salesName" label="跟进人" width="100" />
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template #default="{ row }">
            <div style="display: flex; align-items: center; justify-content: center; gap: 8px;">
              <el-tooltip content="编辑" placement="top">
                <el-button link type="primary" :icon="Edit" @click="handleEdit(row)" />
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize"
          :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch" @current-change="loadData" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="编辑跟进" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="选择客户" prop="customerId">
          <el-select v-model="formData.customerId" placeholder="请搜索并选择客户" filterable remote
            :remote-method="searchCustomers" :loading="customerLoading" style="width: 100%">
            <el-option v-for="item in customerOptions" :key="item.id" :label="item.realName + ' (' + item.phone + ')'"
              :value="item.id!" />
          </el-select>
        </el-form-item>
        <el-form-item label="带看房源" prop="houseId">
          <el-select v-model="formData.houseId" placeholder="请搜索并选择房源" filterable remote :remote-method="searchHouses"
            :loading="houseLoading" style="width: 100%">
            <el-option v-for="item in houseOptions" :key="item.id" :label="item.houseNo + ' (' + item.layout + ')'"
              :value="item.id!" />
          </el-select>
        </el-form-item>
        <el-form-item label="带看时间" prop="viewTime">
          <el-date-picker v-model="formData.viewTime" type="datetime" placeholder="选择带看时间"
            value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="客户反馈" prop="customerFeedback">
          <el-input v-model="formData.customerFeedback" type="textarea" :rows="3" placeholder="客户对房源的评价，如价格、户型等" />
        </el-form-item>
        <el-form-item label="跟进建议" prop="followAdvice">
          <el-input v-model="formData.followAdvice" type="textarea" :rows="2" placeholder="后续跟进计划" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { Search, Refresh, Edit } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { reqViewRecordPage, reqUpdateViewRecord } from '@/api/viewRecord';
import { reqCustomerPage } from '@/api/customer'; // 复用客户接口
import { reqHousePage } from '@/api/house'; // 复用房源接口
import type { ViewRecord, ViewRecordQuery } from '@/api/viewRecord/type';
import { useUserStore } from '@/stores/userStore'; // 获取当前登录销售ID

// --- 状态定义 ---
const userStore = useUserStore();
const loading = ref(false);
const tableData = ref<ViewRecord[]>([]);
const total = ref(0);
const dateRange = ref([]); // 时间范围选择器绑定值

const queryParams = reactive<ViewRecordQuery>({
  pageNum: 1,
  pageSize: 10,
  customerId: undefined,
  houseId: undefined
});

// 弹窗相关
const dialogVisible = ref(false);
const dialogType = ref<'add' | 'edit'>('add');
const submitLoading = ref(false);
const formRef = ref<FormInstance>();

const formData = reactive<ViewRecord>({
  customerId: undefined as any,
  houseId: undefined as any,
  salesId: userStore.userInfo?.id ?? 0, // 默认为当前登录用户（若无则为0）
  viewTime: '',
  customerFeedback: '',
  followAdvice: ''
});

// 远程搜索相关状态
const customerLoading = ref(false);
const customerOptions = ref<any[]>([]);
const houseLoading = ref(false);
const houseOptions = ref<any[]>([]);

const rules = reactive<FormRules>({
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  houseId: [{ required: true, message: '请选择房源', trigger: 'change' }],
  viewTime: [{ required: true, message: '请选择时间', trigger: 'change' }]
});

// --- 方法 ---

// 加载数据
const loadData = async () => {
  loading.value = true;
  try {
    const res: any = await reqViewRecordPage(queryParams);
    if (res && res.status && res.data) {
      tableData.value = res.data.records || [];
      total.value = res.data.total || 0;
    } else {
      tableData.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('加载带看记录失败:', error);
    ElMessage.error('加载数据失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1;
  loadData();
};

const handleDateChange = (val: any) => {
  if (val) {
    queryParams.startTime = val[0];
    queryParams.endTime = val[1];
  } else {
    queryParams.startTime = undefined;
    queryParams.endTime = undefined;
  }
};

const handleReset = () => {
  queryParams.customerId = undefined;
  queryParams.houseId = undefined;
  dateRange.value = [];
  handleDateChange(null);
  handleSearch();
};

// 远程搜索客户
const searchCustomers = async (query: string) => {
  if (query) {
    customerLoading.value = true;
    try {
      // 复用客户分页接口进行搜索
      const res: any = await reqCustomerPage({ pageNum: 1, pageSize: 20, realName: query });
      if (res && res.status && res.data) {
        customerOptions.value = res.data.records || [];
      } else {
        customerOptions.value = [];
      }
    } catch (error) {
      console.error('搜索客户失败:', error);
    } finally {
      customerLoading.value = false;
    }
  } else {
    customerOptions.value = [];
  }
};

// 远程搜索房源
const searchHouses = async (query: string) => {
  if (query) {
    houseLoading.value = true;
    try {
      // 复用房源分页接口进行搜索，这里假访HouseQueryDTO有keyword字段或houseNo
      // 注意：这里可能需要后端支持keyword搜索，或者使用houseNo
      const res: any = await reqHousePage({ pageNum: 1, pageSize: 20, houseNo: query } as any);
      if (res && res.status && res.data) {
        houseOptions.value = res.data.records || [];
      } else {
        houseOptions.value = [];
      }
    } catch (error) {
      console.error('搜索房源失败:', error);
    } finally {
      houseLoading.value = false;
    }
  } else {
    houseOptions.value = [];
  }
};

// 初始化选项（编辑时需要预加载）
const initOptions = async (customerId?: number, houseId?: number) => {
  // 这里为了简化，实际应该通过ID获取详情接口，
  // 或者在搜索接口支持 id 列表查询。
  // 暂时通过空搜索触发默认列表（如果后端支持）或依赖用户重新搜索
  searchCustomers('');
  searchHouses('');
}

// handleAdd已删除 - 新增功能由预约看房完成后自动生成

const handleEdit = (row: ViewRecord) => {
  dialogType.value = 'edit';
  Object.assign(formData, row);
  // 预填充下拉框回显数据 (伪造一个选项以供回显，防止显示ID)
  customerOptions.value = [{ id: row.customerId, realName: row.customerName || '未知客户', phone: '' }];
  houseOptions.value = [{ id: row.houseId, houseNo: row.houseNo || '未知房源', layout: '' }];
  dialogVisible.value = true;
};

const resetFormState = () => {
  if (formRef.value) formRef.value.resetFields();
  formData.id = undefined;
  formData.customerId = undefined as any;
  formData.houseId = undefined as any;
  formData.customerFeedback = '';
  formData.followAdvice = '';
};

const resetForm = () => resetFormState();

const submitForm = async () => {
  if (!formRef.value) return;
  let valid = false;
  try {
    valid = await formRef.value.validate();
  } catch (err) {
    valid = false;
  }
  if (!valid) {
    ElMessage.warning('请完善表单信息');
    return;
  }

  submitLoading.value = true;
  try {
    const payload: ViewRecord = {
      id: formData.id,
      customerId: formData.customerId,
      houseId: formData.houseId,
      salesId: formData.salesId,
      viewTime: formData.viewTime,
      customerFeedback: formData.customerFeedback,
      followAdvice: formData.followAdvice
    };

    const resAny: any = await reqUpdateViewRecord(payload);
    const apiResp = resAny?.data ?? resAny;

    const ok = apiResp === true
      || apiResp?.status === true
      || apiResp?.code === 200
      || apiResp?.success === true;

    if (ok) {
      ElMessage.success('操作成功');
      dialogVisible.value = false;
      loadData();
      return;
    }

    ElMessage.error(apiResp?.message || '操作失败');
  } catch (error) {
    console.error('保存带看记录失败:', error);
    ElMessage.error('保存失败，请稍后重试');
  } finally {
    submitLoading.value = false;
  }
};

const formatTime = (time: string) => {
  return time ? time.replace('T', ' ') : '-';
};

onMounted(() => {
  loadData();
});
</script>

<style scoped lang="scss">
.page-container {
  padding: 20px;

  .search-card {
    margin-bottom: 20px;
  }

  .table-card {
    .operation-bar {
      margin-bottom: 15px;
    }

    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>
