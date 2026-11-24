<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="事件ID" prop="eventId">
            <el-input v-model="queryParams.eventId" placeholder="请输入事件ID" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="用户ID" prop="userId">
            <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="手机号码" prop="phoneNumber">
            <el-input v-model="queryParams.phoneNumber" placeholder="请输入手机号码" clearable @keyup.enter="handleQuery" />
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
          <el-form-item label="发送时间" prop="sendTime">
            <el-date-picker clearable
              v-model="queryParams.sendTime"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择发送时间"
            />
          </el-form-item>
          <el-form-item label="响应消息" prop="responseMessage">
            <el-input v-model="queryParams.responseMessage" placeholder="请输入响应消息" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="服务提供商" prop="provider">
            <el-input v-model="queryParams.provider" placeholder="请输入服务提供商" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="服务商消息ID" prop="providerMessageId">
            <el-input v-model="queryParams.providerMessageId" placeholder="请输入服务商消息ID" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="费用金额" prop="costAmount">
            <el-input v-model="queryParams.costAmount" placeholder="请输入费用金额" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="重试次数" prop="retryCount">
            <el-input v-model="queryParams.retryCount" placeholder="请输入重试次数" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['bicanmou:CryptoNotificationRecord:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['bicanmou:CryptoNotificationRecord:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['bicanmou:CryptoNotificationRecord:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['bicanmou:CryptoNotificationRecord:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="CryptoNotificationRecordList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="主键ID" align="center" prop="id" v-if="true" />
        <el-table-column label="事件ID" align="center" prop="eventId" />
        <el-table-column label="用户ID" align="center" prop="userId" />
        <el-table-column label="手机号码" align="center" prop="phoneNumber" />
        <el-table-column label="通知类型" align="center" prop="notificationType">
          <template #default="scope">
            <dict-tag :options="noti_type" :value="scope.row.notificationType ? scope.row.notificationType.split(',') : []"/>
          </template>
        </el-table-column>
        <el-table-column label="通知内容" align="center" prop="notificationContent" />
        <el-table-column label="通知状态" align="center" prop="notificationStatus">
          <template #default="scope">
            <dict-tag :options="noti_status" :value="scope.row.notificationStatus"/>
          </template>
        </el-table-column>
        <el-table-column label="发送时间" align="center" prop="sendTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.sendTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="响应消息" align="center" prop="responseMessage" />
        <el-table-column label="服务提供商" align="center" prop="provider" />
        <el-table-column label="服务商消息ID" align="center" prop="providerMessageId" />
        <el-table-column label="费用金额" align="center" prop="costAmount" />
        <el-table-column label="重试次数" align="center" prop="retryCount" />
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="use_status" :value="scope.row.status"/>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['bicanmou:CryptoNotificationRecord:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['bicanmou:CryptoNotificationRecord:remove']"></el-button>
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
    <!-- 添加或修改通知记录对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="CryptoNotificationRecordFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="事件ID" prop="eventId">
          <el-input v-model="form.eventId" placeholder="请输入事件ID" />
        </el-form-item>
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phoneNumber">
          <el-input v-model="form.phoneNumber" placeholder="请输入手机号码" />
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
        <el-form-item label="通知内容">
          <editor v-model="form.notificationContent" :min-height="192"/>
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
        <el-form-item label="发送时间" prop="sendTime">
          <el-date-picker clearable
            v-model="form.sendTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择发送时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="响应消息" prop="responseMessage">
            <el-input v-model="form.responseMessage" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="服务提供商" prop="provider">
          <el-input v-model="form.provider" placeholder="请输入服务提供商" />
        </el-form-item>
        <el-form-item label="服务商消息ID" prop="providerMessageId">
          <el-input v-model="form.providerMessageId" placeholder="请输入服务商消息ID" />
        </el-form-item>
        <el-form-item label="费用金额" prop="costAmount">
          <el-input v-model="form.costAmount" placeholder="请输入费用金额" />
        </el-form-item>
        <el-form-item label="重试次数" prop="retryCount">
          <el-input v-model="form.retryCount" placeholder="请输入重试次数" />
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

<script setup name="CryptoNotificationRecord" lang="ts">
import { listCryptoNotificationRecord, getCryptoNotificationRecord, delCryptoNotificationRecord, addCryptoNotificationRecord, updateCryptoNotificationRecord } from '@/api/bicanmou/CryptoNotificationRecord';
import { CryptoNotificationRecordVO, CryptoNotificationRecordQuery, CryptoNotificationRecordForm } from '@/api/bicanmou/CryptoNotificationRecord/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { use_status, noti_status, noti_type } = toRefs<any>(proxy?.useDict('use_status', 'noti_status', 'noti_type'));

const CryptoNotificationRecordList = ref<CryptoNotificationRecordVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref(ElForm);
const CryptoNotificationRecordFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: CryptoNotificationRecordForm = {
  id: undefined,
  eventId: undefined,
  userId: undefined,
  phoneNumber: undefined,
  notificationType: '',
  notificationContent: undefined,
  notificationStatus: undefined,
  sendTime: undefined,
  responseMessage: undefined,
  provider: undefined,
  providerMessageId: undefined,
  costAmount: undefined,
  retryCount: undefined,
  status: undefined,
  remark: undefined
}
const data = reactive<PageData<CryptoNotificationRecordForm, CryptoNotificationRecordQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    eventId: undefined,
    userId: undefined,
    phoneNumber: undefined,
    notificationType: undefined,
    notificationContent: undefined,
    notificationStatus: undefined,
    sendTime: undefined,
    responseMessage: undefined,
    provider: undefined,
    providerMessageId: undefined,
    costAmount: undefined,
    retryCount: undefined,
    status: undefined,
  },
  rules: {
    id: [
      { required: true, message: "主键ID不能为空", trigger: "blur" }
    ],
    eventId: [
      { required: true, message: "事件ID不能为空", trigger: "blur" }
    ],
    userId: [
      { required: true, message: "用户ID不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "状态不能为空", trigger: "change" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询通知记录列表 */
const getList = async () => {
  loading.value = true;
  const res = await listCryptoNotificationRecord(queryParams.value);
  CryptoNotificationRecordList.value = res.rows;
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
  CryptoNotificationRecordFormRef.value.resetFields();
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
const handleSelectionChange = (selection: CryptoNotificationRecordVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加通知记录";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: CryptoNotificationRecordVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改通知记录";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getCryptoNotificationRecord(_id);
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
  CryptoNotificationRecordFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      // notificationType已经是字符串，不需要join
      if (form.value.id) {
        await updateCryptoNotificationRecord(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addCryptoNotificationRecord(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: CryptoNotificationRecordVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除通知记录编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delCryptoNotificationRecord(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('bicanmou/CryptoNotificationRecord/export', {
    ...queryParams.value
  }, `CryptoNotificationRecord_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
