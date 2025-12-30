<template>
    <div class="payment-container">
        <el-card class="search-card" shadow="never">
            <el-form :inline="true" :model="queryParams" class="demo-form-inline">
                <el-form-item label="收据编号">
                    <el-input v-model="queryParams.receiptNo" placeholder="请输入收据号" clearable />
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="queryParams.paymentStatus" placeholder="全部" clearable style="width: 120px">
                        <el-option label="待确认" :value="0" />
                        <el-option label="有效" :value="1" />
                        <el-option label="已作废" :value="2" />
                    </el-select>
                </el-form-item>
                <el-form-item label="收款日期">
                    <el-date-picker v-model="queryParams.dateRange" type="daterange" range-separator="至"
                        start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
                    <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                    <el-button type="success" icon="Plus" @click="openAddDialog" v-if="hasPermission('payment:add')">
                        新增收款
                    </el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <el-card class="table-card" shadow="never">
            <el-table v-loading="loading" :data="paymentList" border stripe>
                <el-table-column prop="receiptNo" label="收据编号" width="180" />
                <el-table-column label="收据凭证" width="100">
                    <template #default="{ row }">
                        <el-image v-if="row.proofUrl" :src="'/uploads' + row.proofUrl"
                            :preview-src-list="['/uploads' + row.proofUrl]" fit="cover"
                            style="width: 50px; height: 50px; cursor: pointer;" preview-teleported />
                        <span v-else style="color: #909399;">暂无</span>
                    </template>
                </el-table-column>
                <el-table-column prop="transactionNo" label="关联交易" width="160" />
                <el-table-column prop="dealPrice" label="成交价" width="120">
                    <template #default="{ row }">
                        <span style="font-weight: bold">{{ row.dealPrice == null ? '-' : `¥${row.dealPrice}` }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="customerName" label="客户" width="100" />
                <el-table-column prop="houseNo" label="房源" width="120" />
                <el-table-column prop="amount" label="金额(元)" width="120">
                    <template #default="{ row }">
                        <span style="color: #f56c6c; font-weight: bold">¥{{ row.amount }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="paymentType" label="款项类型" width="100">
                    <template #default="{ row }">
                        <el-tag>{{ getPaymentTypeText(row.paymentType) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="paymentStatus" label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag :type="getStatusType(row.paymentStatus)">
                            {{ row.paymentStatus === 0 ? '待确认' : row.paymentStatus === 1 ? '有效' : '已作废' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="paymentTime" label="收款时间" width="170" />
                <el-table-column prop="financeName" label="经办人" width="100" />
                <el-table-column label="操作" fixed="right" width="220" align="center">
                    <template #default="{ row }">
                        <div style="display: flex; align-items: center; justify-content: center; gap: 8px;">
                            <el-tooltip content="详情" placement="top">
                                <el-button link type="primary" :icon="View" @click="handleDetail(row)" />
                            </el-tooltip>
                            <el-tooltip content="编辑" placement="top"
                                v-if="row.paymentStatus === 0 && hasPermission('payment:edit')">
                                <el-button link type="primary" :icon="Edit" @click="handleEdit(row)" />
                            </el-tooltip>
                            <el-tooltip content="作废" placement="top"
                                v-if="row.paymentStatus !== 2 && hasPermission('payment:void')">
                                <el-button link type="danger" :icon="Close" @click="handleVoid(row)" />
                            </el-tooltip>
                            <el-tooltip content="确认" placement="top"
                                v-if="row.paymentStatus === 0 && hasPermission('payment:confirm')">
                                <el-button link type="success" :icon="Check" @click="handleConfirm(row)" />
                            </el-tooltip>
                        </div>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-container">
                <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize"
                    :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper"
                    @size-change="handleQuery" @current-change="handleQuery" />
            </div>
        </el-card>

        <el-dialog v-model="dialogVisible" title="新增收款" width="600px">
            <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
                <el-form-item label="关联交易" prop="transactionId">
                    <el-select v-model="form.transactionId" filterable remote placeholder="搜索交易编号"
                        :remote-method="remoteSearchTransaction" :loading="searchLoading">
                        <el-option v-for="item in transactionOptions" :key="item.id"
                            :label="item.transactionNo + ' (' + item.customerName + ')'" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="款项类型" prop="paymentType">
                    <el-radio-group v-model="form.paymentType">
                        <el-radio :label="1">定金</el-radio>
                        <el-radio :label="2">首付款</el-radio>
                        <el-radio :label="3">尾款</el-radio>
                        <el-radio :label="5">贷款</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="金额" prop="amount">
                    <el-input-number v-model="form.amount" :min="0" :precision="2" style="width: 100%" />
                </el-form-item>
                <el-form-item label="支付方式" prop="paymentMethod">
                    <el-select v-model="form.paymentMethod" placeholder="请选择">
                        <el-option label="银行转账" value="银行转账" />
                        <el-option label="现金" value="现金" />
                        <el-option label="POS刷卡" value="POS刷卡" />
                    </el-select>
                </el-form-item>
                <el-form-item label="付款人" prop="payerInfo">
                    <el-input v-model="form.payerInfo" placeholder="如非本人支付请备注" />
                </el-form-item>
                <el-form-item label="凭证图片">
                    <el-upload action="#" list-type="picture-card" :auto-upload="false" :file-list="proofFileList"
                        :on-change="handleProofChange" :on-remove="handleProofRemove" :limit="1" accept="image/*">
                        <el-icon>
                            <Plus />
                        </el-icon>
                        <template #tip>
                            <div class="el-upload__tip">上传凭证图片(jpg/png，最大5MB)</div>
                        </template>
                    </el-upload>
                    <div v-if="form.proofUrl" class="proof-preview">
                        <span>已上传: {{ form.proofUrl }}</span>
                    </div>
                </el-form-item>
                <el-form-item label="备注">
                    <el-input v-model="form.remark" type="textarea" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
            </template>
        </el-dialog>

        <el-dialog v-model="detailVisible" title="收款详情" width="700px">
            <el-descriptions border :column="2" v-if="currentDetail">
                <el-descriptions-item label="收据编号">{{ currentDetail.receiptNo }}</el-descriptions-item>
                <el-descriptions-item label="状态">
                    {{ currentDetail.paymentStatus === 0 ? '待确认' : '有效' }}
                </el-descriptions-item>
                <el-descriptions-item label="交易编号">{{ currentDetail.transactionNo }}</el-descriptions-item>
                <el-descriptions-item label="客户">{{ currentDetail.customerName }}</el-descriptions-item>
                <el-descriptions-item label="金额">¥{{ currentDetail.amount }}</el-descriptions-item>
                <el-descriptions-item label="经办人">{{ currentDetail.financeName }}</el-descriptions-item>
                <el-descriptions-item label="收据凭证" :span="2">
                    <el-image 
                        v-if="currentDetail.proofUrl" 
                        :src="'/uploads' + currentDetail.proofUrl"
                        :preview-src-list="['/uploads' + currentDetail.proofUrl]" 
                        fit="cover"
                        style="width: 100px; height: 100px; cursor: pointer;" 
                        preview-teleported 
                    />
                    <span v-else style="color: #909399;">暂无凭证</span>
                </el-descriptions-item>
            </el-descriptions>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Edit, Check, Close, View } from '@element-plus/icons-vue';
import { reqPaymentList, reqAddPayment, reqConfirmPayment, reqVoidPayment, reqUpdatePayment, reqUploadProof } from '@/api/payment';
import { reqTransactionList } from '@/api/transaction';
import type { UploadFile } from 'element-plus';
import type { PaymentQuery, PaymentVO, PaymentForm } from '@/api/payment/type';
import { useUserStore } from '@/stores/userStore'; // 用于权限检查

const userStore = useUserStore();
const hasPermission = (code: string) => userStore.hasPermission(code);

// 数据定义
const loading = ref(false);
const paymentList = ref<PaymentVO[]>([]);
const total = ref(0);
const queryParams = reactive<PaymentQuery>({
    pageNum: 1,
    pageSize: 10,
    receiptNo: '',
    paymentStatus: undefined,
    dateRange: undefined
});
// 定义编辑状态
const isEditMode = ref(false);
// 弹窗相关
const dialogVisible = ref(false);
const detailVisible = ref(false);
const submitLoading = ref(false);
const currentDetail = ref<PaymentVO | null>(null);
const formRef = ref();
const searchLoading = ref(false);
const transactionOptions = ref<any[]>([]); // 交易搜索结果
const proofFileList = ref<UploadFile[]>([]); // 凭证图片列表
const selectedProofFile = ref<File | null>(null); // 待上传的文件

const form = reactive<PaymentForm>({
    transactionId: null,
    paymentType: 1,
    flowType: 1,
    amount: 0,
    paymentMethod: '',
    paymentTime: '',
    proofUrl: '',
    payerInfo: '',
    remark: ''
});

const rules = {
    transactionId: [{ required: true, message: '请选择关联交易', trigger: 'change' }],
    amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
    paymentMethod: [{ required: true, message: '请选择支付方式', trigger: 'change' }]
};

// 方法
const getPaymentTypeText = (type: number) => {
    const map: Record<number, string> = { 1: '定金', 2: '首付款', 3: '尾款', 4: '中介费', 5: '贷款' };
    return map[type] || '未知';
};

const getStatusType = (status: number) => {
    return status === 0 ? 'warning' : status === 1 ? 'success' : 'info';
};

// 获取列表
const getList = async () => {
    loading.value = true;
    try {
        const res = await reqPaymentList(queryParams) as any;
        if (res.code === 200) {
            paymentList.value = res.data.records;
            total.value = res.data.total;
        }
    } finally {
        loading.value = false;
    }
};

const handleQuery = () => getList();
const resetQuery = () => {
    queryParams.receiptNo = '';
    queryParams.paymentStatus = undefined;
    queryParams.dateRange = undefined;
    getList();
};

// 凭证图片处理
const handleProofChange = (uploadFile: UploadFile) => {
    if (uploadFile.raw) {
        selectedProofFile.value = uploadFile.raw;
    }
};

const handleProofRemove = () => {
    selectedProofFile.value = null;
    form.proofUrl = '';
};


// 打开新增弹窗
const openAddDialog = () => {
    isEditMode.value = false;
    // 重置表单
    Object.assign(form, {
        id: undefined,
        transactionId: null,
        paymentType: 1,
        flowType: 1,
        amount: 0,
        paymentMethod: '',
        paymentTime: '',
        proofUrl: '',
        payerInfo: '',
        remark: ''
    });
    // 重置凭证图片
    proofFileList.value = [];
    selectedProofFile.value = null;
    dialogVisible.value = true;
};

// 点击编辑按钮
const handleEdit = (row: PaymentVO) => {
    isEditMode.value = true;
    // 回填数据
    Object.assign(form, {
        id: row.id,
        transactionId: row.transactionId,
        paymentType: row.paymentType,
        flowType: row.flowType,
        amount: row.amount,
        paymentMethod: row.paymentMethod,
        // 注意格式化时间，如果是 ISO 格式可能需要处理
        paymentTime: row.paymentTime,
        proofUrl: row.proofUrl,
        payerInfo: row.payerInfo,
        remark: row.remark
    });

    // 模拟回填下拉框显示（因为下拉框是远程搜索，需要手动设置初始值）
    transactionOptions.value = [{
        id: row.transactionId,
        transactionNo: row.transactionNo,
        customerName: row.customerName
    }];

    dialogVisible.value = true;
};

// 提交表单逻辑修改
const submitForm = async () => {
    await formRef.value.validate();
    submitLoading.value = true;
    try {
        // 确保 paymentTime 有值
        const submitData = { ...form };
        if (!submitData.paymentTime) {
            submitData.paymentTime = new Date().toISOString();
        }

        // 如果有选择的凭证图片，先上传
        if (selectedProofFile.value && submitData.transactionId) {
            try {
                console.log('开始上传凭证, transactionId:', submitData.transactionId);
                console.log('选择的文件:', selectedProofFile.value);
                const uploadRes = await reqUploadProof(selectedProofFile.value, submitData.transactionId) as any;
                console.log('上传响应:', uploadRes);
                // 检查多种可能的响应格式
                if (uploadRes.code === 200 && uploadRes.data) {
                    submitData.proofUrl = uploadRes.data;
                    console.log('上传成功, proofUrl:', submitData.proofUrl);
                } else if (uploadRes.status === true && uploadRes.data) {
                    // 另一种可能的响应格式
                    submitData.proofUrl = uploadRes.data;
                    console.log('上传成功(status), proofUrl:', submitData.proofUrl);
                } else if (typeof uploadRes === 'string') {
                    // 直接返回路径的情况
                    submitData.proofUrl = uploadRes;
                    console.log('上传成功(直接字符串), proofUrl:', submitData.proofUrl);
                } else {
                    console.warn('凭证上传响应格式异常:', uploadRes);
                    ElMessage.warning('凭证上传失败，将继续提交');
                }
            } catch (uploadError) {
                console.error('凭证上传异常', uploadError);
                ElMessage.warning('凭证上传异常，将继续提交');
            }
        }

        if (isEditMode.value) {
            // 编辑接口
            const res = await reqUpdatePayment(submitData) as any;
            if (res.code === 200) {
                ElMessage.success('修改成功');
                dialogVisible.value = false;
                getList();
            } else {
                ElMessage.error(res.message || '修改失败');
            }
        } else {
            // 新增接口
            const res = await reqAddPayment(submitData) as any;
            if (res.code === 200) {
                ElMessage.success('新增成功');
                dialogVisible.value = false;
                getList();
            } else {
                ElMessage.error(res.message || '新增失败');
            }
        }
    } catch (error: any) {
        console.error('提交失败', error);
        ElMessage.error(error?.response?.data?.message || error?.message || '操作失败');
    } finally {
        submitLoading.value = false;
    }
};

// 远程搜索交易（真实API调用）
const remoteSearchTransaction = async (query: string) => {
    if (query && query.length > 0) {
        searchLoading.value = true;
        try {
            const res = await reqTransactionList({
                pageNum: 1,
                pageSize: 20,
                transactionNo: query
            }) as any;
            if (res.code === 200 && res.data?.records) {
                transactionOptions.value = res.data.records.map((item: any) => ({
                    id: item.id,
                    transactionNo: item.transactionNo,
                    customerName: item.customerName,
                    dealPrice: item.dealPrice,
                    status: item.status
                }));
            }
        } catch (error) {
            console.error('搜索交易失败', error);
        } finally {
            searchLoading.value = false;
        }
    } else {
        transactionOptions.value = [];
    }
};


// 确认收款
const handleConfirm = (row: PaymentVO) => {
    ElMessageBox.confirm(`确认通过该笔 ${row.amount} 元的收款吗？`, '提示', {
        confirmButtonText: '通过',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        await reqConfirmPayment(row.id);
        ElMessage.success('操作成功');
        getList();
    });
};

// 作废收款
const handleVoid = (row: PaymentVO) => {
    ElMessageBox.prompt('请输入作废原因', '作废确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /\S/,
        inputErrorMessage: '原因不能为空'
    }).then(async ({ value }) => {
        await reqVoidPayment(row.id, value);
        ElMessage.success('已作废');
        getList();
    });
};

const handleDetail = (row: PaymentVO) => {
    currentDetail.value = row;
    detailVisible.value = true;
};

onMounted(() => {
    getList();
});
</script>

<style scoped lang="scss">
.payment-container {
    padding: 20px;

    .search-card {
        margin-bottom: 20px;
    }

    .pagination-container {
        margin-top: 20px;
        display: flex;
        justify-content: flex-end;
    }
}
</style>