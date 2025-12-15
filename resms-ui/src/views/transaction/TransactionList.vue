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
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="title">交易列表</span>
          <div class="btns">
            <el-button type="primary" :icon="Plus" plain @click="handleAdd">新建交易</el-button>
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
              {{ getDisplayStatusText(scope.row) }}
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
            <el-button link type="primary" size="small" :icon="View" @click="handleDetail(scope.row)">详情</el-button>
            <el-button link type="primary" size="small" :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
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
        <el-form-item label="交易编号">
          <el-input v-model="editForm.transactionNo" disabled />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="成交价格" prop="dealPrice">
              <el-input-number v-model="editForm.dealPrice" :min="0" :precision="2" :controls="false" style="width: 100%" :disabled="!canEditDealPrice || isFinalLocked">
                <template #prefix>￥</template>
              </el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="定金金额" prop="deposit">
              <el-input-number v-model="editForm.deposit" :min="0" :precision="2" :controls="false" style="width: 100%" :disabled="!canEditFinanceFields || isFinalLocked" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="首付金额" prop="downPayment">
              <el-input-number v-model="editForm.downPayment" :min="0" :precision="2" :controls="false" style="width: 100%" :disabled="!canEditFinanceFields || isFinalLocked" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="贷款金额" prop="loanAmount">
              <el-input-number v-model="editForm.loanAmount" :min="0" :precision="2" :controls="false" style="width: 100%" :disabled="!canEditFinanceFields || isFinalLocked" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="贷款状态" prop="loanStatus">
          <el-select v-model="editForm.loanStatus" placeholder="请选择" style="width: 100%" :disabled="!canEditFinanceFields || isFinalLocked">
            <el-option v-for="(label, val) in LoanStatusMap" :key="val" :label="label" :value="Number(val)" />
          </el-select>
        </el-form-item>

        <el-divider content-position="left">状态流转</el-divider>

        <el-form-item v-if="roleType === 1" label="交易状态" prop="status">
          <el-radio-group v-model="editForm.status" :disabled="isFinalLocked">
            <el-radio-button :value="0">待定金</el-radio-button>
            <el-radio-button :value="1">已定金</el-radio-button>
            <el-radio-button :value="2">已首付</el-radio-button>
            <el-radio-button :value="3">已过户</el-radio-button>
            <el-radio-button :value="4">已完成</el-radio-button>
            <el-radio-button :value="5">已取消</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="roleType === 1" label="经理审核" prop="managerAudit">
          <el-radio-group v-model="editForm.managerAudit" :disabled="isFinalLocked">
            <el-radio-button :value="0">待审核</el-radio-button>
            <el-radio-button :value="1">通过</el-radio-button>
            <el-radio-button :value="2">驳回</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="roleType === 3 && editForm.status === 0 && editForm.managerAudit === 0" label="经理审核">
          <el-button type="success" plain @click="submitManagerApprove(true)">通过</el-button>
          <el-button type="danger" plain @click="submitManagerApprove(false)">驳回</el-button>
        </el-form-item>

        <el-form-item v-if="roleType === 4 && canFinanceApplyFinish" label="财务确认">
          <el-button type="primary" plain @click="submitFinanceApplyFinish">申请完成</el-button>
        </el-form-item>

        <el-form-item v-if="roleType === 4 && isFinishAuditPending" label="财务确认">
          <el-tag type="warning">待管理员审核</el-tag>
        </el-form-item>

        <el-form-item v-if="roleType === 1 && isFinishAuditPending" label="完成审核">
          <el-button type="success" plain @click="submitAdminFinishApprove(true)">通过</el-button>
          <el-button type="danger" plain @click="submitAdminFinishApprove(false)">驳回</el-button>
        </el-form-item>

        <el-divider content-position="left">佣金</el-divider>

        <el-form-item v-if="commissionInfo" label="佣金状态">
          <el-tag :type="commissionInfo.status === 2 ? 'success' : (commissionInfo.status === 1 ? 'warning' : 'info')">
            {{ CommissionStatusMap[commissionInfo.status] || '未知' }}
          </el-tag>
        </el-form-item>
        <el-form-item v-if="commissionInfo" label="佣金金额">
          <span class="text-price">{{ formatCurrency(commissionInfo.amount as any) }}</span>
        </el-form-item>

        <el-form-item v-if="canCreateCommission" label="提成比例(%)">
          <div style="display: flex; align-items: center; gap: 12px;">
            <el-input-number v-model="commissionRateInput" :min="0" :precision="2" :controls="false" style="width: 140px" />
            <el-button type="primary" plain :loading="commissionCreating" @click="submitCreateCommission">生成佣金</el-button>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitEdit" :loading="submitLoading" :disabled="isFinalLocked">确 定</el-button>
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
                  <el-select v-model="addForm.salesId" placeholder="请选择销售" filterable style="width: 100%" :disabled="roleType === 2">
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
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitAdd" :loading="addLoading">创 建</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick, watch, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { View, Edit, Search, Refresh, Plus } from '@element-plus/icons-vue';
import { useUserStore } from '@/stores/userStore';
import { reqTransactionList, reqGetTransactionDetail, reqUpdateTransaction, reqAddTransaction, reqManagerApprove, reqFinanceApplyFinish, reqAdminFinishApprove } from '@/api/transaction';
import type { TransactionQuery, TransactionVO, TransactionForm, TransactionAddDTO } from '@/api/transaction/type';
import { getCustomerList, getHouseList, getSalesList } from '@/api/appointment';
import { reqGetCommissionByTransaction, reqCreateCommissionByTransaction } from '@/api/commission';
import type { CommissionVO } from '@/api/commission/type';

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
const FinishAuditMap: Record<number, string> = { 0: '未申请', 1: '待审核', 2: '已通过', 3: '已驳回' };
const CommissionStatusMap: Record<number, string> = { 0: '待核算', 1: '已核算', 2: '已发放' };

const userStore = useUserStore();
const roleType = computed(() => userStore.userInfo?.roleType);

const canEditDealPrice = computed(() => roleType.value === 1 || roleType.value === 2 || roleType.value === 3);
const canEditFinanceFields = computed(() => roleType.value === 1 || roleType.value === 4);

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

const route = useRoute();
const router = useRouter();
const routeOpening = ref(false);

const pickQueryValue = (val: unknown) => {
  if (Array.isArray(val)) return val[0];
  return val;
};

const clearQueryKeys = async (keys: string[]) => {
  const nextQuery: Record<string, any> = { ...(route.query as any) };
  keys.forEach((k) => {
    delete nextQuery[k];
  });
  try {
    await router.replace({ path: route.path, query: nextQuery });
  } catch (e) {
    console.error(e);
  }
};

const handleRouteOpen = async () => {
  if (routeOpening.value) return;
  const auditId = pickQueryValue(route.query.auditId);
  const editId = pickQueryValue(route.query.editId);
  const detailId = pickQueryValue(route.query.detailId);

  const keysToClear: string[] = [];
  let openType: 'audit' | 'edit' | 'detail' | null = null;
  let rawId: unknown = null;

  if (auditId) {
    openType = 'audit';
    rawId = auditId;
    keysToClear.push('auditId');
  } else if (editId) {
    openType = 'edit';
    rawId = editId;
    keysToClear.push('editId');
  } else if (detailId) {
    openType = 'detail';
    rawId = detailId;
    keysToClear.push('detailId');
  }

  if (!openType) return;

  const id = Number(rawId);
  routeOpening.value = true;
  try {
    if (!Number.isFinite(id) || id <= 0) {
      await clearQueryKeys(keysToClear);
      return;
    }

    if (openType === 'detail') {
      await handleDetail({ id } as TransactionVO);
    } else {
      await handleEdit({ id } as TransactionVO);
    }
  } finally {
    await clearQueryKeys(keysToClear);
    routeOpening.value = false;
  }
};

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
  managerAudit: 0,
  finishAudit: 0
});

const isFinalLocked = computed(() => editForm.status === 4 && editForm.finishAudit === 2);
const isFinishAuditPending = computed(() => editForm.finishAudit === 1);
const isFullyPaid = computed(() => {
  const deal = Number(editForm.dealPrice || 0);
  const paid = Number(editForm.deposit || 0) + Number(editForm.downPayment || 0) + Number(editForm.loanAmount || 0);
  return deal > 0 && paid >= deal;
});
const canFinanceApplyFinish = computed(() => {
  if (roleType.value !== 4) return false;
  if (!(editForm.status === 2 || editForm.status === 3)) return false;
  if (editForm.finishAudit === 1 || editForm.finishAudit === 2) return false;
  return isFullyPaid.value;
});

const commissionInfo = ref<CommissionVO | null>(null);
const commissionRateInput = ref<number>(3);
const commissionCreating = ref(false);
const canCreateCommission = computed(() => {
  if (!(roleType.value === 1 || roleType.value === 4)) return false;
  if (!(editForm.status === 4 && editForm.finishAudit === 2)) return false;
  return !commissionInfo.value;
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
  addForm.salesId = roleType.value === 2 ? (userStore.userInfo as any)?.userId : (undefined as any);
  addForm.dealPrice = 0;
  addForm.deposit = 0;
  addForm.downPayment = 0;
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

const getDisplayStatusText = (row: TransactionVO) => {
  const base = TransactionStatusMap[row.status] || '未知';
  if (row.status === 0) {
    if (row.managerAudit === 2) return `${base}(驳回)`;
    if (row.managerAudit === 1) return `${base}(通过)`;
    if (row.managerAudit === 0) return `${base}(待审核)`;
  }
  if ((row.status === 2 || row.status === 3) && row.finishAudit != null) {
    if (row.finishAudit === 1) return `${base}(待完成审核)`;
    if (row.finishAudit === 3) return `${base}(完成驳回)`;
  }
  if (row.status === 4 && row.finishAudit != null && row.finishAudit !== 2) {
    return `${base}(${FinishAuditMap[row.finishAudit] || '未知'})`;
  }
  return base;
};

const fetchCommissionInfo = async (transactionId: number) => {
  try {
    const res = await reqGetCommissionByTransaction(transactionId) as any;
    if (res.code === 200) {
      commissionInfo.value = res.data || null;
    }
  } catch (e) {
    console.error(e);
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
    editForm.finishAudit = data.finishAudit ?? 0;

    editVisible.value = true;
    commissionInfo.value = null;
    await fetchCommissionInfo(editForm.id);
    nextTick(() => {
      editFormRef.value?.clearValidate();
    });
  } else {
    ElMessage.error(res.message || '获取数据失败');
  }
};

const refreshCurrentEdit = async () => {
  if (!editForm.id) return;
  const res = await reqGetTransactionDetail(editForm.id) as any;
  if (res.code === 200) {
    const data = res.data;
    editForm.transactionNo = data.transactionNo;
    editForm.dealPrice = data.dealPrice;
    editForm.deposit = data.deposit;
    editForm.downPayment = data.downPayment;
    editForm.loanAmount = data.loanAmount;
    editForm.loanStatus = data.loanStatus;
    editForm.status = data.status;
    editForm.managerAudit = data.managerAudit;
    editForm.finishAudit = data.finishAudit ?? 0;
    await fetchCommissionInfo(editForm.id);
  }
};

const submitManagerApprove = async (approved: boolean) => {
  if (!editForm.id) return;
  let reason: string | undefined;
  if (!approved) {
    try {
      const { value } = await ElMessageBox.prompt('请输入驳回原因', '驳回', { confirmButtonText: '确定', cancelButtonText: '取消' });
      reason = value;
    } catch {
      return;
    }
  }
  const res = await reqManagerApprove(editForm.id, approved, reason) as any;
  if (res.code === 200) {
    ElMessage.success('操作成功');
    await refreshCurrentEdit();
    getList();
  } else {
    ElMessage.error(res.message || '操作失败');
  }
};

const submitFinanceApplyFinish = async () => {
  if (!editForm.id) return;
  const res = await reqFinanceApplyFinish(editForm.id) as any;
  if (res.code === 200) {
    ElMessage.success('已提交完成申请');
    await refreshCurrentEdit();
    getList();
  } else {
    ElMessage.error(res.message || '提交失败');
  }
};

const submitAdminFinishApprove = async (approved: boolean) => {
  if (!editForm.id) return;
  let reason: string | undefined;
  if (!approved) {
    try {
      const { value } = await ElMessageBox.prompt('请输入驳回原因', '驳回', { confirmButtonText: '确定', cancelButtonText: '取消' });
      reason = value;
    } catch {
      return;
    }
  }
  const res = await reqAdminFinishApprove(editForm.id, approved, reason) as any;
  if (res.code === 200) {
    ElMessage.success('操作成功');
    await refreshCurrentEdit();
    getList();
  } else {
    ElMessage.error(res.message || '操作失败');
  }
};

const submitCreateCommission = async () => {
  if (!editForm.id) return;
  commissionCreating.value = true;
  try {
    const res = await reqCreateCommissionByTransaction(editForm.id, Number(commissionRateInput.value || 0)) as any;
    if (res.code === 200) {
      ElMessage.success('生成佣金成功');
      await fetchCommissionInfo(editForm.id);
    } else {
      ElMessage.error(res.message || '生成失败');
    }
  } finally {
    commissionCreating.value = false;
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

const formatCurrency = (value?: number | null | string) => {
  if (value === undefined || value === null) return '-';
  const n = typeof value === 'number' ? value : Number(value);
  if (!Number.isFinite(n)) return '-';
  return `¥ ${n.toLocaleString()}`;
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
  handleRouteOpen();
});

watch(
  () => route.query,
  () => {
    handleRouteOpen();
  },
  { deep: true }
);
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