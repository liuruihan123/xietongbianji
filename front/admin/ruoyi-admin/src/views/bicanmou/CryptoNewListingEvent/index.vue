<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="交易所代码" prop="exchangeCode">
            <el-input v-model="queryParams.exchangeCode" placeholder="请输入交易所代码" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="交易所名称" prop="exchangeName">
            <el-input v-model="queryParams.exchangeName" placeholder="请输入交易所名称" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="币种符号" prop="symbol">
            <el-input v-model="queryParams.symbol" placeholder="请输入币种符号" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="交易对" prop="tradingPair">
            <el-input v-model="queryParams.tradingPair" placeholder="请输入交易对" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="基础资产" prop="baseAsset">
            <el-input v-model="queryParams.baseAsset" placeholder="请输入基础资产" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="计价资产" prop="quoteAsset">
            <el-input v-model="queryParams.quoteAsset" placeholder="请输入计价资产" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="上市价格" prop="listingPrice">
            <el-input v-model="queryParams.listingPrice" placeholder="请输入上市价格" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="市值" prop="marketCapUsd">
            <el-input v-model="queryParams.marketCapUsd" placeholder="请输入市值" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="24小时交易量" prop="volume24h">
            <el-input v-model="queryParams.volume24h" placeholder="请输入24小时交易量" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="发现时间" prop="discoveredTime">
            <el-date-picker clearable
              v-model="queryParams.discoveredTime"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择发现时间"
            />
          </el-form-item>
          <el-form-item label="上市时间" prop="listingTime">
            <el-date-picker clearable
              v-model="queryParams.listingTime"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择上市时间"
            />
          </el-form-item>
          <el-form-item label="通知状态" prop="notificationStatus">
            <el-select v-model="queryParams.notificationStatus" placeholder="请选择通知状态" clearable>
              <el-option
                v-for="dict in sys_notice_status"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="通知时间" prop="notificationTime">
            <el-date-picker clearable
              v-model="queryParams.notificationTime"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择通知时间"
            />
          </el-form-item>
          <el-form-item label="通知用户数量" prop="notificationUserCount">
            <el-input v-model="queryParams.notificationUserCount" placeholder="请输入通知用户数量" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="事件来源" prop="eventSource">
            <el-input v-model="queryParams.eventSource" placeholder="请输入事件来源" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="原始数据" prop="rawData">
            <el-input v-model="queryParams.rawData" placeholder="请输入原始数据" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
              <el-option
                v-for="dict in use_status"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['bicanmou:CryptoNewListingEvent:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['bicanmou:CryptoNewListingEvent:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['bicanmou:CryptoNewListingEvent:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['bicanmou:CryptoNewListingEvent:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="CryptoNewListingEventList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="主键ID" align="center" prop="id" v-if="true" />
        <el-table-column label="交易所代码" align="center" prop="exchangeCode" />
        <el-table-column label="交易所名称" align="center" prop="exchangeName" />
        <el-table-column label="币种符号" align="center" prop="symbol" />
        <el-table-column label="交易对" align="center" prop="tradingPair" />
        <el-table-column label="基础资产" align="center" prop="baseAsset" />
        <el-table-column label="计价资产" align="center" prop="quoteAsset" />
        <el-table-column label="交易状态" align="center" prop="tradingStatus" />
        <el-table-column label="上市价格" align="center" prop="listingPrice" />
        <el-table-column label="市值" align="center" prop="marketCapUsd" />
        <el-table-column label="24小时交易量" align="center" prop="volume24h" />
        <el-table-column label="发现时间" align="center" prop="discoveredTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.discoveredTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="上市时间" align="center" prop="listingTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.listingTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="通知状态" align="center" prop="notificationStatus">
          <template #default="scope">
            <dict-tag :options="sys_notice_status" :value="scope.row.notificationStatus"/>
          </template>
        </el-table-column>
        <el-table-column label="通知时间" align="center" prop="notificationTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.notificationTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="通知用户数量" align="center" prop="notificationUserCount" />
        <el-table-column label="事件类型" align="center" prop="eventType" />
        <el-table-column label="事件来源" align="center" prop="eventSource" />
        <el-table-column label="原始数据" align="center" prop="rawData" />
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="use_status" :value="scope.row.status"/>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['bicanmou:CryptoNewListingEvent:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['bicanmou:CryptoNewListingEvent:remove']"></el-button>
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
    <!-- 添加或修改新币上市事件对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="CryptoNewListingEventFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="交易所代码" prop="exchangeCode">
          <el-input v-model="form.exchangeCode" placeholder="请输入交易所代码" />
        </el-form-item>
        <el-form-item label="交易所名称" prop="exchangeName">
          <el-input v-model="form.exchangeName" placeholder="请输入交易所名称" />
        </el-form-item>
        <el-form-item label="币种符号" prop="symbol">
          <el-input v-model="form.symbol" placeholder="请输入币种符号" />
        </el-form-item>
        <el-form-item label="交易对" prop="tradingPair">
          <el-input v-model="form.tradingPair" placeholder="请输入交易对" />
        </el-form-item>
        <el-form-item label="基础资产" prop="baseAsset">
          <el-input v-model="form.baseAsset" placeholder="请输入基础资产" />
        </el-form-item>
        <el-form-item label="计价资产" prop="quoteAsset">
          <el-input v-model="form.quoteAsset" placeholder="请输入计价资产" />
        </el-form-item>
        <el-form-item label="上市价格" prop="listingPrice">
          <el-input v-model="form.listingPrice" placeholder="请输入上市价格" />
        </el-form-item>
        <el-form-item label="市值" prop="marketCapUsd">
          <el-input v-model="form.marketCapUsd" placeholder="请输入市值" />
        </el-form-item>
        <el-form-item label="24小时交易量" prop="volume24h">
          <el-input v-model="form.volume24h" placeholder="请输入24小时交易量" />
        </el-form-item>
        <el-form-item label="发现时间" prop="discoveredTime">
          <el-date-picker clearable
            v-model="form.discoveredTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择发现时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="上市时间" prop="listingTime">
          <el-date-picker clearable
            v-model="form.listingTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择上市时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="通知状态" prop="notificationStatus">
          <el-radio-group v-model="form.notificationStatus">
            <el-radio
                v-for="dict in sys_notice_status"
                :key="dict.value"
                :label="parseInt(dict.value)"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="通知时间" prop="notificationTime">
          <el-date-picker clearable
            v-model="form.notificationTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择通知时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="通知用户数量" prop="notificationUserCount">
          <el-input v-model="form.notificationUserCount" placeholder="请输入通知用户数量" />
        </el-form-item>
        <el-form-item label="事件来源" prop="eventSource">
          <el-input v-model="form.eventSource" placeholder="请输入事件来源" />
        </el-form-item>
        <el-form-item label="原始数据" prop="rawData">
            <el-input v-model="form.rawData" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
                v-for="dict in use_status"
                :key="dict.value"
                :label="parseInt(dict.value)"
            >{{dict.label}}</el-radio>
          </el-radio-group>
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

<script setup name="CryptoNewListingEvent" lang="ts">
import { listCryptoNewListingEvent, getCryptoNewListingEvent, delCryptoNewListingEvent, addCryptoNewListingEvent, updateCryptoNewListingEvent } from '@/api/bicanmou/CryptoNewListingEvent';
import { CryptoNewListingEventVO, CryptoNewListingEventQuery, CryptoNewListingEventForm } from '@/api/bicanmou/CryptoNewListingEvent/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_notice_status, use_status } = toRefs<any>(proxy?.useDict('sys_notice_status', 'use_status'));

const CryptoNewListingEventList = ref<CryptoNewListingEventVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref(ElForm);
const CryptoNewListingEventFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: CryptoNewListingEventForm = {
  id: undefined,
  exchangeCode: undefined,
  exchangeName: undefined,
  symbol: undefined,
  tradingPair: undefined,
  baseAsset: undefined,
  quoteAsset: undefined,
  tradingStatus: undefined,
  listingPrice: undefined,
  marketCapUsd: undefined,
  volume24h: undefined,
  discoveredTime: undefined,
  listingTime: undefined,
  notificationStatus: undefined,
  notificationTime: undefined,
  notificationUserCount: undefined,
  eventType: undefined,
  eventSource: undefined,
  rawData: undefined,
  status: undefined,
  remark: undefined
}
const data = reactive<PageData<CryptoNewListingEventForm, CryptoNewListingEventQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    exchangeCode: undefined,
    exchangeName: undefined,
    symbol: undefined,
    tradingPair: undefined,
    baseAsset: undefined,
    quoteAsset: undefined,
    tradingStatus: undefined,
    listingPrice: undefined,
    marketCapUsd: undefined,
    volume24h: undefined,
    discoveredTime: undefined,
    listingTime: undefined,
    notificationStatus: undefined,
    notificationTime: undefined,
    notificationUserCount: undefined,
    eventType: undefined,
    eventSource: undefined,
    rawData: undefined,
    status: undefined,
  },
  rules: {
    id: [
      { required: true, message: "主键ID不能为空", trigger: "blur" }
    ],
    exchangeCode: [
      { required: true, message: "交易所代码不能为空", trigger: "blur" }
    ],
    exchangeName: [
      { required: true, message: "交易所名称不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "状态不能为空", trigger: "change" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询新币上市事件列表 */
const getList = async () => {
  loading.value = true;
  const res = await listCryptoNewListingEvent(queryParams.value);
  CryptoNewListingEventList.value = res.rows;
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
  CryptoNewListingEventFormRef.value.resetFields();
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
const handleSelectionChange = (selection: CryptoNewListingEventVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加新币上市事件";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: CryptoNewListingEventVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改新币上市事件";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getCryptoNewListingEvent(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
  });
}

/** 提交按钮 */
const submitForm = () => {
  CryptoNewListingEventFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateCryptoNewListingEvent(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addCryptoNewListingEvent(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: CryptoNewListingEventVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除新币上市事件编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delCryptoNewListingEvent(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('bicanmou/CryptoNewListingEvent/export', {
    ...queryParams.value
  }, `CryptoNewListingEvent_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
