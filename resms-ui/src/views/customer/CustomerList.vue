<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="客户姓名">
          <el-input v-model="queryParams.realName" placeholder="请输入姓名" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="手机号码">
          <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="意向等级">
          <el-select v-model="queryParams.intentionLevel" placeholder="请选择" clearable style="width: 150px">
            <el-option label="高意向" :value="1" />
            <el-option label="中意向" :value="2" />
            <el-option label="低意向" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <div class="operation-bar">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增客户</el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        border
        style="width: 100%"
      >
        <el-table-column prop="customerNo" label="客户编号" width="140" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="intentionLevel" label="意向等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getIntentionTagType(row.intentionLevel)">
              {{ getIntentionLabel(row.intentionLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="demandAreaRegion" label="意向区域" />
        <el-table-column prop="demandLayout" label="意向户型" width="120" />
        <el-table-column prop="demandPrice" label="预算(元)" width="120">
           <template #default="{ row }">
             {{ row.demandPrice ? Number(row.demandPrice).toLocaleString() : '-' }}
           </template>
        </el-table-column>
        <el-table-column prop="source" label="来源" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180">
           <template #default="{ row }">
             {{ formatTime(row.createTime) }}
           </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增客户' : '编辑客户'"
      width="600px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="客户姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号码" maxlength="11" />
        </el-form-item>
        <el-form-item label="意向等级" prop="intentionLevel">
          <el-radio-group v-model="formData.intentionLevel">
            <el-radio :value="1">高</el-radio>
            <el-radio :value="2">中</el-radio>
            <el-radio :value="3">低</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="意向区域" prop="demandAreaRegion">
              <el-input v-model="formData.demandAreaRegion" placeholder="如：朝阳区" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="意向户型" prop="demandLayout">
              <el-input v-model="formData.demandLayout" placeholder="如：3室2厅" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="意向面积" prop="demandArea">
              <el-input-number v-model="formData.demandArea" :min="0" :precision="2" controls-position="right" style="width: 100%">
                 <template #suffix>㎡</template>
              </el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="意向价格" prop="demandPrice">
              <el-input-number v-model="formData.demandPrice" :min="0" :step="10000" controls-position="right" style="width: 100%">
                 <template #suffix>元</template>
              </el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="客户来源" prop="source">
          <el-select v-model="formData.source" placeholder="请选择来源" style="width: 100%">
            <el-option label="门店接待" value="门店接待" />
            <el-option label="线上咨询" value="线上咨询" />
            <el-option label="朋友推荐" value="朋友推荐" />
            <el-option label="广告投放" value="广告投放" />
          </el-select>
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="formData.idCard" placeholder="请输入身份证号（选填）" />
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
import { Search, Refresh, Plus } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { reqCustomerPage, reqSaveOrUpdateCustomer, reqDeleteCustomer } from '@/api/customer';
import type { Customer, CustomerQuery } from '@/api/customer/type';

// --- 状态定义 ---
const loading = ref(false);
const tableData = ref<Customer[]>([]);
const total = ref(0);
const queryParams = reactive<CustomerQuery>({
  pageNum: 1,
  pageSize: 10,
  realName: '',
  phone: '',
  intentionLevel: ''
});

const dialogVisible = ref(false);
const dialogType = ref<'add' | 'edit'>('add');
const submitLoading = ref(false);
const formRef = ref<FormInstance>();

const formData = reactive<Customer>({
  realName: '',
  phone: '',
  intentionLevel: 2, // 默认中
  source: '',
  demandArea: undefined,
  demandPrice: undefined,
  demandLayout: '',
  demandAreaRegion: '',
  idCard: ''
});

// 表单校验规则
const rules = reactive<FormRules>({
  realName: [{ required: true, message: '请输入客户姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  intentionLevel: [{ required: true, message: '请选择意向等级', trigger: 'change' }]
});

// --- 方法定义 ---

// 加载数据
const loadData = async () => {
  loading.value = true;
  try {
    // 如果intentionLevel为空字符串，转为undefined
    const params = {
      ...queryParams,
      intentionLevel: queryParams.intentionLevel === '' ? undefined : queryParams.intentionLevel
    };
    const res = await reqCustomerPage(params);
    if (res && res.data) {
      tableData.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (error) {
    console.error('加载客户列表失败:', error);
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

// 重置
const handleReset = () => {
  queryParams.realName = '';
  queryParams.phone = '';
  queryParams.intentionLevel = '';
  handleSearch();
};

// 显示新增弹窗
const handleAdd = () => {
  dialogType.value = 'add';
  resetFormState();
  dialogVisible.value = true;
};

// 显示编辑弹窗
const handleEdit = (row: Customer) => {
  dialogType.value = 'edit';
  // 复制数据到表单
  Object.assign(formData, row);
  dialogVisible.value = true;
};

// 重置表单状态
const resetFormState = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
  // 手动重置数据（因为resetFields只能重置有prop的字段）
  formData.id = undefined;
  formData.customerNo = undefined;
  formData.realName = '';
  formData.phone = '';
  formData.intentionLevel = 2;
  formData.demandArea = undefined;
  formData.demandPrice = undefined;
  formData.demandLayout = '';
  formData.demandAreaRegion = '';
  formData.source = '';
  formData.idCard = '';
};

// 弹窗关闭回调
const resetForm = () => {
  resetFormState();
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true;
      try {
        const res = await reqSaveOrUpdateCustomer(formData);
        if (res && res.status) {
          ElMessage.success(dialogType.value === 'add' ? '添加成功' : '修改成功');
          dialogVisible.value = false;
          loadData();
        }
      } catch (error) {
        console.error('保存客户信息失败:', error);
      } finally {
        submitLoading.value = false;
      }
    }
  });
};

// 删除客户
const handleDelete = (row: Customer) => {
  ElMessageBox.confirm(`确定要删除客户 "${row.realName}" 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      if (row.id) {
        const res = await reqDeleteCustomer(row.id);
        if (res && res.status) {
          ElMessage.success('删除成功');
          loadData();
        }
      }
    } catch (error) {
      console.error('删除客户失败:', error);
    }
  }).catch(() => {});
};

// --- 工具函数 ---

// 意向等级标签颜色
const getIntentionTagType = (level: number) => {
  const map: Record<number, string> = {
    1: 'danger',  // 高
    2: 'warning', // 中
    3: 'info'     // 低
  };
  return map[level] || '';
};

// 意向等级文字
const getIntentionLabel = (level: number) => {
  const map: Record<number, string> = {
    1: '高',
    2: '中',
    3: '低'
  };
  return map[level] || '未知';
};

// 简单的时间格式化
const formatTime = (timeStr?: string) => {
  if (!timeStr) return '-';
  return timeStr.replace('T', ' ');
};

// --- 生命周期 ---
onMounted(() => {
  loadData();
});
</script>

<style scoped lang="scss">
.page-container {
  padding: 20px;
  
  .search-card {
    margin-bottom: 20px;
    .search-form {
      .el-form-item {
        margin-bottom: 0;
        margin-right: 20px;
      }
    }
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


