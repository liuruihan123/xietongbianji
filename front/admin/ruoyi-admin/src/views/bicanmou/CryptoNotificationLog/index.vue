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
          <el-form-item label="交易对" prop="tradingPair">
            <el-input v-model="queryParams.tradingPair" placeholder="请输入交易对" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="通知状态" prop="notificationStatus">
            <el-select v-model="queryParams.notificationStatus" placeholder="请选择通知状态" clearable>
              <el-option
                v-for="dict in noti_status"
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['bicanmou:CryptoNotificationLog:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['bicanmou:CryptoNotificationLog:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['bicanmou:CryptoNotificationLog:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['bicanmou:CryptoNotificationLog:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="CryptoNotificationLogList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="主键ID" align="center" prop="id" v-if="true" />
        <el-table-column label="交易所代码" align="center" prop="exchangeCode" />
        <el-table-column label="币种符号" align="center" prop="symbol" />
        <el-table-column label="交易对" align="center" prop="tradingPair" />
        <el-table-column label="通知类型" align="center" prop="notificationType">
          <template #default="scope">
            <dict-tag :options="noti_type" :value="scope.row.notificationType ? scope.row.notificationType.split(',') : []"/>
          </template>
        </el-table-column>
        <el-table-column label="通知状态" align="center" prop="notificationStatus">
          <template #default="scope">
            <dict-tag :options="noti_status" :value="scope.row.notificationStatus"/>
          </template>
        </el-table-column>
        <el-table-column label="通知内容" align="center" prop="notificationContent" />
        <el-table-column label="通知时间" align="center" prop="notificationTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.notificationTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['bicanmou:CryptoNotificationLog:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['bicanmou:CryptoNotificationLog:remove']"></el-button>
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
    <!-- 添加或修改新币通知记录对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="CryptoNotificationLogFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="交易所代码" prop="exchangeCode">
          <el-input v-model="form.exchangeCode" placeholder="请输入交易所代码" />
        </el-form-item>
        <el-form-item label="币种符号" prop="symbol">
          <el-input v-model="form.symbol" placeholder="请输入币种符号" />
        </el-form-item>
        <el-form-item label="交易对" prop="tradingPair">
          <el-input v-model="form.tradingPair" placeholder="请输入交易对" />
        </el-form-item>
        <el-form-item label="通知类型" prop="notificationType">
          <el-checkbox-group v-model="form.notificationType">
            <el-checkbox
                v-for="dict in noti_type"
                :key="dict.value"
                :label="dict.value">
                {{dict.label}}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="通知状态" prop="notificationStatus">
          <el-radio-group v-model="form.notificationStatus">
            <el-radio
                v-for="dict in noti_status"
                :key="dict.value"
                :label="parseInt(dict.value)"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="通知内容">
          <editor v-model="form.notificationContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="通知时间" prop="notificationTime">
          <el-date-picker clearable
            v-model="form.notificationTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择通知时间">
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

<script setup name="CryptoNotificationLog" lang="ts">
import { listCryptoNotificationLog, getCryptoNotificationLog, delCryptoNotificationLog, addCryptoNotificationLog, updateCryptoNotificationLog } from '@/api/bicanmou/CryptoNotificationLog';
import { CryptoNotificationLogVO, CryptoNotificationLogQuery, CryptoNotificationLogForm } from '@/api/bicanmou/CryptoNotificationLog/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { noti_status, noti_type } = toRefs<any>(proxy?.useDict('noti_status', 'noti_type'));

const CryptoNotificationLogList = ref<CryptoNotificationLogVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref(ElForm);
const CryptoNotificationLogFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: CryptoNotificationLogForm = {
  id: undefined,
  exchangeCode: undefined,
  symbol: undefined,
  tradingPair: undefined,
  notificationType: '',
  notificationStatus: undefined,
  notificationContent: undefined,
  notificationTime: undefined,
}
const data = reactive<PageData<CryptoNotificationLogForm, CryptoNotificationLogQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    exchangeCode: undefined,
    symbol: undefined,
    tradingPair: undefined,
    notificationType: undefined,
    notificationStatus: undefined,
    notificationContent: undefined,
    notificationTime: undefined,
  },
  rules: {
    id: [
      { required: true, message: "主键ID不能为空", trigger: "blur" }
    ],
    exchangeCode: [
      { required: true, message: "交易所代码不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询新币通知记录列表 */
const getList = async () => {
  loading.value = true;
  const res = await listCryptoNotificationLog(queryParams.value);
  CryptoNotificationLogList.value = res.rows;
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
  CryptoNotificationLogFormRef.value.resetFields();
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
const handleSelectionChange = (selection: CryptoNotificationLogVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加新币通知记录";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: CryptoNotificationLogVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改新币通知记录";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getCryptoNotificationLog(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
    // 如果notificationType是字符串且包含逗号，则转换为数组用于多选
    if (form.value.notificationType && typeof form.value.notificationType === 'string') {
      // 保持为字符串，不需要转换
    }
  });
}

/** 提交按钮 */
const submitForm = () => {
  CryptoNotificationLogFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      // notificationType已经是字符串，不需要join
      if (form.value.id) {
        await updateCryptoNotificationLog(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addCryptoNotificationLog(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: CryptoNotificationLogVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除新币通知记录编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delCryptoNotificationLog(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('bicanmou/CryptoNotificationLog/export', {
    ...queryParams.value
  }, `CryptoNotificationLog_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
