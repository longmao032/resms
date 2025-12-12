<template>
  <div class="app-container">
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" ref="queryFormRef">
        <el-form-item label="交易编号" prop="transactionNo">
          <el-input 
            v-model="queryParams.transactionNo" 
            placeholder="请输入交易编号" 
            clearable 
            @keyup.enter="handleQuery" 
          />
        </el-form-item>
        <el-form-item label="客户姓名" prop="customerName">
          <el-input 
            v-model="queryParams.customerName" 
            placeholder="请输入客户姓名" 
            clearable 
            @keyup.enter="handleQuery" 
          />
        </el-form-item>
        <el-form-item label="交易状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 150px">
            <el-option 
              v-for="(label, value) in TransactionStatusMap" 
              :key="value" 
              :label="label" 
              :value="Number(value)" 
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="title">交易列表</span>
          <div class="btns">
            <el-button type="primary" icon="Plus" plain @click="handleAdd">新建交易</el-button>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="tableData" border stripe highlight-current-row>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="transactionNo" label="交易编号" min-width="140" show-overflow-tooltip />
        <el-table-column prop="houseNo" label="房源编号" width="120" show-overflow-tooltip />
        <el-table-column prop="unitName" label="户型" width="120" show-overflow-tooltip />
        <el-table-column prop="customerName" label="客户姓名" width="100" />
        <el-table-column prop="salesName" label="销售顾问" width="100" />
        
        <el-table-column prop="dealPrice" label="成交价格" min-width="120" align="right">
          <template #default="scope">
            {{ formatCurrency(scope.row.dealPrice) }}
          </template>
        </el-table-column>

        <el-table-column prop="status" label="交易状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.status)">
              {{ TransactionStatusMap[scope.row.status] || '未知' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="managerAudit" label="审核" width="100" align="center">
          <template #default="scope">
            <el-tag effect="plain" :type="getAuditTag(scope.row.managerAudit)">
              {{ AuditStatusMap[scope.row.managerAudit] || '未知' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" width="160" />

        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="scope">
            <el-button link type="primary" size="small" icon="View" @click="handleDetail(scope.row)">详情</el-button>
            <el-button link type="primary" size="small" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="getList"
          @current-change="getList"
        />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="交易详情" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="交易编号">{{ detailData.transactionNo }}</el-descriptions-item>
        <el-descriptions-item label="房源编号">{{ detailData.houseNo }}</el-descriptions-item>
        
        <el-descriptions-item label="客户姓名">{{ detailData.customerName }}</el-descriptions-item>
        <el-descriptions-item label="客户电话">{{ detailData.customerPhone }}</el-descriptions-item>
        
        <el-descriptions-item label="销售顾问">{{ detailData.salesName }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        
        <el-descriptions-item label="成交价格">
          <span class="text-price">{{ formatCurrency(detailData.dealPrice) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="定金金额">{{ formatCurrency(detailData.deposit) }}</el-descriptions-item>
        
        <el-descriptions-item label="首付金额">{{ formatCurrency(detailData.downPayment) }}</el-descriptions-item>
        <el-descriptions-item label="贷款金额">{{ formatCurrency(detailData.loanAmount) }}</el-descriptions-item>
        
        <el-descriptions-item label="尾款金额" :span="2">
          <span class="text-final-payment">{{ formatCurrency(detailData.finalPayment) }}</span>
          <el-text type="info" size="small" style="margin-left: 8px;">(成交价 - 定金 - 首付 - 贷款)</el-text>
        </el-descriptions-item>
        
        <el-descriptions-item label="交易状态">
          <el-tag :type="getStatusTag(detailData.status)">{{ TransactionStatusMap[detailData.status] }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="贷款状态">
          <el-tag effect="plain" :type="getLoanTag(detailData.loanStatus)">{{ LoanStatusMap[detailData.loanStatus] }}</el-tag>
        </el-descriptions-item>
        
        <el-descriptions-item label="经理审核" :span="2">
           <el-tag :type="getAuditTag(detailData.managerAudit)">{{ AuditStatusMap[detailData.managerAudit] }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
      
      <template #footer>
        <el-button @click="detailVisible = false">关 闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="editVisible" title="编辑交易信息" width="650px" append-to-body destroy-on-close>
      <el-form :model="editForm" ref="editFormRef" :rules="rules" label-width="110px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="交易编号">
              <el-input v-model="editForm.transactionNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="成交价格" prop="dealPrice">
              <el-input-number 
                v-model="editForm.dealPrice" 
                :min="0" :precision="2" 
                style="width: 100%" 
                :controls="false"
              >
                 <template #prefix>￥</template>
              </el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="定金金额" prop="deposit">
              <el-input-number 
                v-model="editForm.deposit" 
                :min="0" :precision="2" 
                style="width: 100%" 
                :controls="false"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="首付金额" prop="downPayment">
              <el-input-number 
                v-model="editForm.downPayment" 
                :min="0" :precision="2" 
                style="width: 100%" 
                :controls="false"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="贷款金额" prop="loanAmount">
               <el-input-number 
                v-model="editForm.loanAmount" 
                :min="0" :precision="2" 
                style="width: 100%" 
                :controls="false"
              />
            </el-form-item>
          </el-col>
           <el-col :span="12">
            <el-form-item label="贷款状态" prop="loanStatus">
              <el-select v-model="editForm.loanStatus" placeholder="请选择">
                <el-option v-for="(label, val) in LoanStatusMap" :key="val" :label="label" :value="Number(val)" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">状态流转</el-divider>

        <el-form-item label="交易状态" prop="status">
          <el-radio-group v-model="editForm.status">
            <el-radio-button :value="0">待定金</el-radio-button>
            <el-radio-button :value="1">已定金</el-radio-button>
            <el-radio-button :value="2">已首付</el-radio-button>
            <el-radio-button :value="3">已过户</el-radio-button>
            <el-radio-button :value="4">已完成</el-radio-button>
            <el-radio-button :value="5">已取消</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="经理审核" prop="managerAudit">
          <el-radio-group v-model="editForm.managerAudit">
            <el-radio :value="0">待审核</el-radio>
            <el-radio :value="1" border class="radio-success">通过</el-radio>
            <el-radio :value="2" border class="radio-danger">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitEdit" :loading="submitLoading">确 定</el-button>
      </template>
    </el-dialog>

    <!-- 新增对话框 -->
    <el-dialog v-model="addVisible" title="新建交易" width="650px" append-to-body destroy-on-close>
      <el-form :model="addForm" ref="addFormRef" :rules="addRules" label-width="110px">
         <el-row :gutter="20">
             <el-col :span="12">
                <el-form-item label="房源" prop="houseId">
                  <el-select v-model="addForm.houseId" placeholder="请选择房源" filterable style="width: 100%">
                    <el-option
                      v-for="item in houseOptions"
                      :key="item.id"
                      :label="`${item.houseNo} ${item.layout ? '('+item.layout+')' : ''}`"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
             </el-col>
             <el-col :span="12">
                 <el-form-item label="客户" prop="customerId">
                  <el-select v-model="addForm.customerId" placeholder="请选择客户" filterable style="width: 100%">
                    <el-option
                      v-for="item in customerOptions"
                      :key="item.id"
                      :label="`${item.realName} (${item.id})`"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
             </el-col>
         </el-row>
         
         <el-row :gutter="20">
             <el-col :span="12">
                 <el-form-item label="销售" prop="salesId">
                  <el-select v-model="addForm.salesId" placeholder="请选择销售" filterable style="width: 100%">
                    <el-option
                      v-for="item in salesOptions"
                      :key="item.id"
                      :label="`${item.realName} (${item.id})`"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
             </el-col>
             <el-col :span="12">
                <el-form-item label="成交价格" prop="dealPrice">
                  <el-input-number 
                    v-model="addForm.dealPrice" 
                    :min="0" :precision="2" 
                    style="width: 100%" 
                    :controls="false"
                  >
                     <template #prefix>￥</template>
                  </el-input-number>
                </el-form-item>
             </el-col>
         </el-row>

         <el-row :gutter="20">
            <el-col :span="8">
                 <el-form-item label="定金" prop="deposit">
                  <el-input-number v-model="addForm.deposit" :min="0" :precision="2" style="width: 100%" :controls="false" />
                </el-form-item>
            </el-col>
             <el-col :span="8">
                 <el-form-item label="首付" prop="downPayment">
                  <el-input-number v-model="addForm.downPayment" :min="0" :precision="2" style="width: 100%" :controls="false" />
                </el-form-item>
            </el-col>
             <el-col :span="8">
                 <el-form-item label="贷款" prop="loanAmount">
                  <el-input-number v-model="addForm.loanAmount" :min="0" :precision="2" style="width: 100%" :controls="false" />
                </el-form-item>
            </el-col>
         </el-row>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitAdd" :loading="addLoading">创 建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue';
import { ElMessage, type FormInstance } from 'element-plus';
import { reqTransactionList, reqGetTransactionDetail, reqUpdateTransaction, reqAddTransaction } from '@/api/transaction';
import type { TransactionQuery, TransactionVO, TransactionForm, TransactionAddDTO } from '@/api/transaction/type';
import { getCustomerList, getHouseList, getSalesList } from '@/api/appointment';

// ================= 常量定义 =================
const TransactionStatusMap: Record<number, string> = {
  0: '待付定金', 1: '已付定金', 2: '已付首付', 3: '已过户', 4: '已完成', 5: '已取消'
};
const LoanStatusMap: Record<number, string> = {
  0: '未申请', 1: '审核中', 2: '已放款', 3: '未通过'
};
const AuditStatusMap: Record<number, string> = {
  0: '待审核', 1: '已通过', 2: '已驳回'
};

// ================= 状态定义 =================
const loading = ref(false);
const total = ref(0);
const tableData = ref<TransactionVO[]>([]);
const queryFormRef = ref<FormInstance>();

// 查询参数
const queryParams = reactive<TransactionQuery>({
  pageNum: 1,
  pageSize: 10,
  transactionNo: '',
  customerName: '',
  status: undefined
});

// 详情相关
const detailVisible = ref(false);
const detailData = ref<TransactionVO>({} as TransactionVO);

// 编辑相关
const editVisible = ref(false);
const submitLoading = ref(false);
const editFormRef = ref<FormInstance>();
const editForm = reactive<TransactionForm>({
  id: 0,
  transactionNo: '',
  dealPrice: 0,
  deposit: 0,
  downPayment: 0,
  loanAmount: 0,
  loanStatus: 0,
  status: 0,
  managerAudit: 0
});

// 表单校验规则
const rules = {
  dealPrice: [{ required: true, message: '成交价格不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '请选择交易状态', trigger: 'change' }]
};

// 新增相关
const addVisible = ref(false);
const addLoading = ref(false);
const addFormRef = ref<FormInstance>();
const addForm = reactive<TransactionAddDTO>({
  houseId: undefined as any,
  customerId: undefined as any,
  salesId: undefined as any,
  dealPrice: 0,
  deposit: 0,
  depositTime: undefined,
  downPayment: 0,
  downPaymentTime: undefined,
  loanAmount: 0,
  loanStatus: 0
});
const addRules = {
  houseId: [{ required: true, message: '请选择房源', trigger: 'change' }],
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  salesId: [{ required: true, message: '请选择销售', trigger: 'change' }],
  dealPrice: [{ required: true, message: '请输入成交价格', trigger: 'blur' }]
};

// 下拉选项
const customerOptions = ref<any[]>([]);
const houseOptions = ref<any[]>([]);
const salesOptions = ref<any[]>([]);

// 获取下拉数据
const getOptions = async () => {
    if (customerOptions.value.length > 0) return; // cache
  try {
    const [cRes, hRes, sRes] = await Promise.all([
      getCustomerList(),
      getHouseList(),
      getSalesList()
    ])
    customerOptions.value = cRes.data.records || []
    houseOptions.value = hRes.data.list || hRes.data.records || [] // handle different response structure
    salesOptions.value = sRes.data.records || []
  } catch (error) {
    console.error('获取下拉数据失败', error)
  }
}

// 打开新增
const handleAdd = () => {
    addVisible.value = true;
    getOptions();
    // Reset form
    addForm.houseId = undefined as any;
    addForm.customerId = undefined as any;
    addForm.salesId = undefined as any;
    addForm.dealPrice = 0;
    addForm.deposit = 0;
    addForm.loanAmount = 0;
    
    nextTick(() => {
        addFormRef.value?.clearValidate();
    })
}

// 提交新增
const submitAdd = async () => {
  if (!addFormRef.value) return;
  await addFormRef.value.validate(async (valid) => {
    if (valid) {
      addLoading.value = true;
      try {
        const res = await reqAddTransaction(addForm) as any;
        if (res.code === 200) {
          ElMessage.success('新建交易成功');
          addVisible.value = false;
          getList(); // 刷新列表
        } else {
          ElMessage.error(res.message || '新建失败');
        }
      } catch (error) {
        console.error(error);
      } finally {
        addLoading.value = false;
      }
    }
  });
};

// ================= 方法逻辑 =================

// 1. 获取列表
const getList = async () => {
  loading.value = true;
  try {
    const res = await reqTransactionList(queryParams) as any;
    if (res.code === 200) {
      tableData.value = res.data.records;
      total.value = res.data.total;
    } else {
      ElMessage.error(res.message || '获取列表失败');
    }
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

// 2. 搜索
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

// 3. 重置
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  queryParams.status = undefined;
  handleQuery();
};

// 4. 查看详情
const handleDetail = async (row: TransactionVO) => {
  const res = await reqGetTransactionDetail(row.id) as any;
  if (res.code === 200) {
    detailData.value = res.data;
    detailVisible.value = true;
  } else {
    ElMessage.error(res.message || '获取详情失败');
  }
};

// 5. 打开编辑
const handleEdit = async (row: TransactionVO) => {
  const res = await reqGetTransactionDetail(row.id) as any;
  if (res.code === 200) {
    const data = res.data;
    // 赋值给表单
    editForm.id = data.id;
    editForm.transactionNo = data.transactionNo;
    editForm.dealPrice = data.dealPrice;
    editForm.deposit = data.deposit;
    editForm.downPayment = data.downPayment;
    editForm.loanAmount = data.loanAmount;
    editForm.loanStatus = data.loanStatus;
    editForm.status = data.status;
    editForm.managerAudit = data.managerAudit;

    editVisible.value = true;
    nextTick(() => {
      editFormRef.value?.clearValidate();
    });
  } else {
    ElMessage.error(res.message || '获取数据失败');
  }
};

// 6. 提交编辑
const submitEdit = async () => {
  if (!editFormRef.value) return;
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true;
      try {
        const res = await reqUpdateTransaction(editForm) as any;
        if (res.code === 200) {
          ElMessage.success('更新成功');
          editVisible.value = false;
          getList(); // 刷新列表
        } else {
          ElMessage.error(res.message || '更新失败');
        }
      } catch (error) {
        console.error(error);
      } finally {
        submitLoading.value = false;
      }
    }
  });
};

// ================= 辅助函数 =================

const formatCurrency = (value?: number | null) => {
  if (value === undefined || value === null) return '-';
  return `¥ ${value.toLocaleString()}`;
};

const getStatusTag = (status: number) => {
  const map: Record<number, string> = {
    0: 'info', 1: 'warning', 2: 'primary', 3: 'primary', 4: 'success', 5: 'danger'
  };
  return map[status] || '';
};

const getAuditTag = (status: number) => {
  const map: Record<number, string> = { 0: 'info', 1: 'success', 2: 'danger' };
  return map[status] || '';
};

const getLoanTag = (status: number) => {
  const map: Record<number, string> = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' };
  return map[status] || '';
};

// 初始化
onMounted(() => {
  getList();
});
</script>

<style scoped lang="scss">
.app-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
.table-card {
  min-height: 500px;
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .title {
      font-weight: bold;
      font-size: 16px;
    }
  }
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
.text-price {
  color: #f56c6c;
  font-weight: bold;
}
.text-final-payment {
  color: #e6a23c;
  font-weight: bold;
  font-size: 15px;
}
.radio-success {
  :deep(.el-radio__input.is-checked + .el-radio__label) {
    color: #67c23a;
  }
  :deep(.el-radio__input.is-checked .el-radio__inner) {
    border-color: #67c23a;
    background: #67c23a;
  }
}
.radio-danger {
  :deep(.el-radio__input.is-checked + .el-radio__label) {
    color: #f56c6c;
  }
  :deep(.el-radio__input.is-checked .el-radio__inner) {
    border-color: #f56c6c;
    background: #f56c6c;
  }
}
</style>