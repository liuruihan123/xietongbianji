<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="订单编号" prop="orderNo">
            <el-input v-model="queryParams.orderNo" placeholder="请输入订单编号" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="订单名称" prop="orderName">
            <el-input v-model="queryParams.orderName" placeholder="请输入订单名称" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="支付状态" prop="paymentStatus">
            <el-select v-model="queryParams.paymentStatus" placeholder="请选择支付状态" clearable>
              <el-option v-for="dict in pay_state" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="支付方式" prop="paymentMethod">
            <el-select v-model="queryParams.paymentMethod" placeholder="请选择支付方式" clearable>
              <el-option v-for="dict in sys_model_billing" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="用户ID" prop="userId">
            <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </transition>

    <el-card shadow="never">
      <template #header>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['system:orders:remove']"
              >删除</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['system:orders:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="ordersList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="主键" align="center" prop="id" v-if="true" />
        <el-table-column label="订单编号" align="center" prop="orderNo" />
        <el-table-column label="订单名称" align="center" prop="orderName" />
        <el-table-column label="金额" align="center" prop="amount">
          <template #default="scope"> {{ scope.row.paymentMethod === 'stripePay' ? '$' : '￥' }}{{ scope.row.amount }} </template>
        </el-table-column>
        <el-table-column label="支付状态" align="center" prop="paymentStatus">
          <template #default="scope">
            <dict-tag :options="pay_state" :value="scope.row.paymentStatus" />
          </template>
        </el-table-column>
        <el-table-column label="支付方式" align="center" prop="paymentMethod">
          <template #default="scope">
            <dict-tag :options="sys_model_billing" :value="scope.row.paymentMethod" />
          </template>
        </el-table-column>
        <el-table-column label="用户ID" align="center" prop="userId" />
        <el-table-column label="创建时间" align="center" prop="createTime" width="180"></el-table-column>
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:orders:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:orders:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改支付订单对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="ordersFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="订单编号" prop="orderNo">
          <el-input v-model="form.orderNo" placeholder="请输入订单编号" />
        </el-form-item>
        <el-form-item label="订单名称" prop="orderName">
          <el-input v-model="form.orderName" placeholder="请输入订单名称" />
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input v-model="form.amount" placeholder="请输入金额" />
        </el-form-item>
        <el-form-item label="支付状态" prop="paymentStatus">
          <el-radio-group v-model="form.paymentStatus">
            <el-radio v-for="dict in pay_state" :key="dict.value" :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="支付方式" prop="paymentMethod">
          <el-radio-group v-model="form.paymentMethod">
            <el-radio v-for="dict in sys_model_billing" :key="dict.value" :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Orders" lang="ts">
import { listOrders, getOrders, delOrders, addOrders, updateOrders } from '@/api/system/orders';
import { OrdersVO, OrdersQuery, OrdersForm } from '@/api/system/orders/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_model_billing, pay_state } = toRefs<any>(proxy?.useDict('sys_model_billing', 'pay_state'));

const ordersList = ref<OrdersVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref(ElForm);
const ordersFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: OrdersForm = {
  id: undefined,
  orderNo: undefined,
  orderName: undefined,
  amount: undefined,
  paymentStatus: undefined,
  paymentMethod: undefined,
  userId: undefined,
  remark: undefined
}
const data = reactive<PageData<OrdersForm, OrdersQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    orderNo: undefined,
    orderName: undefined,
    paymentStatus: undefined,
    paymentMethod: undefined,
    userId: undefined,
  },
  rules: {
    id: [
      { required: true, message: "主键不能为空", trigger: "blur" }
    ],
    orderNo: [
      { required: true, message: "订单编号不能为空", trigger: "blur" }
    ],
    orderName: [
      { required: true, message: "订单名称不能为空", trigger: "blur" }
    ],
    amount: [
      { required: true, message: "金额不能为空", trigger: "blur" }
    ],
    paymentStatus: [
      { required: true, message: "支付状态不能为空", trigger: "change" }
    ],
    paymentMethod: [
      { required: true, message: "支付方式不能为空", trigger: "change" }
    ],
    userId: [
      { required: true, message: "用户ID不能为空", trigger: "blur" }
    ],
    remark: [
      { required: true, message: "备注不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询支付订单列表 */
const getList = async () => {
  loading.value = true;
  const res = await listOrders(queryParams.value);
  ordersList.value = res.rows;
  total.value = res.total;
  loading.value = false;
}

/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
}

/** 表单重置 */
const reset = () => {
  form.value = {...initFormData};
  ordersFormRef.value.resetFields();
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
const handleSelectionChange = (selection: OrdersVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加支付订单";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: OrdersVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改支付订单";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getOrders(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
  });
}

/** 提交按钮 */
const submitForm = () => {
  ordersFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateOrders(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addOrders(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: OrdersVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除支付订单编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delOrders(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('system/orders/export', {
    ...queryParams.value
  }, `orders_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
