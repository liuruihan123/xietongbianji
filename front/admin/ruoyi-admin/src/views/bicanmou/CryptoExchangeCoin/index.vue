<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="交易所代码" prop="exchangeCode">
            <el-input v-model="queryParams.exchangeCode" placeholder="请输入交易所代码" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="币种符号" prop="symbol">
            <el-input v-model="queryParams.symbol" placeholder="请输入币种符号" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="基础资产" prop="baseAsset">
            <el-input v-model="queryParams.baseAsset" placeholder="请输入基础资产" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="计价资产" prop="quoteAsset">
            <el-input v-model="queryParams.quoteAsset" placeholder="请输入计价资产" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="交易对" prop="tradingPair">
            <el-input v-model="queryParams.tradingPair" placeholder="请输入交易对" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="是否为新币" prop="isNew">
            <el-select v-model="queryParams.isNew" placeholder="请选择是否为新币" clearable>
              <el-option
                v-for="dict in is_yes_no"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="上线时间" prop="listingTime">
            <el-date-picker clearable
              v-model="queryParams.listingTime"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择上线时间"
            />
          </el-form-item>
          <el-form-item label="发现时间" prop="discoveredTime">
            <el-date-picker clearable
              v-model="queryParams.discoveredTime"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择发现时间"
            />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['bicanmou:CryptoExchangeCoin:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['bicanmou:CryptoExchangeCoin:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['bicanmou:CryptoExchangeCoin:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['bicanmou:CryptoExchangeCoin:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="CryptoExchangeCoinList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="主键ID" align="center" prop="id" v-if="true" />
        <el-table-column label="交易所代码" align="center" prop="exchangeCode" />
        <el-table-column label="币种符号" align="center" prop="symbol" />
        <el-table-column label="基础资产" align="center" prop="baseAsset" />
        <el-table-column label="计价资产" align="center" prop="quoteAsset" />
        <el-table-column label="交易对" align="center" prop="tradingPair" />
        <el-table-column label="交易状态" align="center" prop="status" />
        <el-table-column label="是否为新币" align="center" prop="isNew">
          <template #default="scope">
            <dict-tag :options="is_yes_no" :value="scope.row.isNew"/>
          </template>
        </el-table-column>
        <el-table-column label="上线时间" align="center" prop="listingTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.listingTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="发现时间" align="center" prop="discoveredTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.discoveredTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['bicanmou:CryptoExchangeCoin:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['bicanmou:CryptoExchangeCoin:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination
          v-show="total>0"
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
      />
    </el-card>
    <!-- 添加或修改交易所币种关联对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="CryptoExchangeCoinFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="交易所代码" prop="exchangeCode">
          <el-input v-model="form.exchangeCode" placeholder="请输入交易所代码" />
        </el-form-item>
        <el-form-item label="币种符号" prop="symbol">
          <el-input v-model="form.symbol" placeholder="请输入币种符号" />
        </el-form-item>
        <el-form-item label="基础资产" prop="baseAsset">
          <el-input v-model="form.baseAsset" placeholder="请输入基础资产" />
        </el-form-item>
        <el-form-item label="计价资产" prop="quoteAsset">
          <el-input v-model="form.quoteAsset" placeholder="请输入计价资产" />
        </el-form-item>
        <el-form-item label="交易对" prop="tradingPair">
          <el-input v-model="form.tradingPair" placeholder="请输入交易对" />
        </el-form-item>
        <el-form-item label="是否为新币" prop="isNew">
          <el-radio-group v-model="form.isNew">
            <el-radio
                v-for="dict in is_yes_no"
                :key="dict.value"
                :label="parseInt(dict.value)"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="上线时间" prop="listingTime">
          <el-date-picker clearable
            v-model="form.listingTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择上线时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="发现时间" prop="discoveredTime">
          <el-date-picker clearable
            v-model="form.discoveredTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择发现时间">
          </el-date-picker>
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

<script setup name="CryptoExchangeCoin" lang="ts">
import { listCryptoExchangeCoin, getCryptoExchangeCoin, delCryptoExchangeCoin, addCryptoExchangeCoin, updateCryptoExchangeCoin } from '@/api/bicanmou/CryptoExchangeCoin';
import { CryptoExchangeCoinVO, CryptoExchangeCoinQuery, CryptoExchangeCoinForm } from '@/api/bicanmou/CryptoExchangeCoin/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { is_yes_no } = toRefs<any>(proxy?.useDict('is_yes_no'));

const CryptoExchangeCoinList = ref<CryptoExchangeCoinVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref(ElForm);
const CryptoExchangeCoinFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: CryptoExchangeCoinForm = {
  id: undefined,
  exchangeCode: undefined,
  symbol: undefined,
  baseAsset: undefined,
  quoteAsset: undefined,
  tradingPair: undefined,
  status: undefined,
  isNew: undefined,
  listingTime: undefined,
  discoveredTime: undefined,
}
const data = reactive<PageData<CryptoExchangeCoinForm, CryptoExchangeCoinQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    exchangeCode: undefined,
    symbol: undefined,
    baseAsset: undefined,
    quoteAsset: undefined,
    tradingPair: undefined,
    status: undefined,
    isNew: undefined,
    listingTime: undefined,
    discoveredTime: undefined,
  },
  rules: {
    id: [
      { required: true, message: "主键ID不能为空", trigger: "blur" }
    ],
    exchangeCode: [
      { required: true, message: "交易所代码不能为空", trigger: "blur" }
    ],
    symbol: [
      { required: true, message: "币种符号不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询交易所币种关联列表 */
const getList = async () => {
  loading.value = true;
  const res = await listCryptoExchangeCoin(queryParams.value);
  CryptoExchangeCoinList.value = res.rows;
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
  CryptoExchangeCoinFormRef.value.resetFields();
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
const handleSelectionChange = (selection: CryptoExchangeCoinVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加交易所币种关联";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: CryptoExchangeCoinVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改交易所币种关联";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getCryptoExchangeCoin(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
  });
}

/** 提交按钮 */
const submitForm = () => {
  CryptoExchangeCoinFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateCryptoExchangeCoin(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addCryptoExchangeCoin(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: CryptoExchangeCoinVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除交易所币种关联编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delCryptoExchangeCoin(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('bicanmou/CryptoExchangeCoin/export', {
    ...queryParams.value
  }, `CryptoExchangeCoin_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
