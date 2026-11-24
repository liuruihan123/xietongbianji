<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="交易所代码" prop="exchangeCode">
            <el-input v-model="queryParams.exchangeCode" placeholder="请输入交易所代码" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="检查状态" prop="checkStatus">
            <el-select v-model="queryParams.checkStatus" placeholder="请选择检查状态" clearable>
              <el-option
                v-for="dict in success_fail"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="响应时间" prop="responseTimeMs">
            <el-input v-model="queryParams.responseTimeMs" placeholder="请输入响应时间" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="总币种数量" prop="totalCoinsCount">
            <el-input v-model="queryParams.totalCoinsCount" placeholder="请输入总币种数量" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="新币数量" prop="newCoinsCount">
            <el-input v-model="queryParams.newCoinsCount" placeholder="请输入新币数量" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="错误信息" prop="errorMessage">
            <el-input v-model="queryParams.errorMessage" placeholder="请输入错误信息" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="错误代码" prop="errorCode">
            <el-input v-model="queryParams.errorCode" placeholder="请输入错误代码" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="HTTP状态码" prop="httpStatusCode">
            <el-input v-model="queryParams.httpStatusCode" placeholder="请输入HTTP状态码" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="请求URL" prop="requestUrl">
            <el-input v-model="queryParams.requestUrl" placeholder="请输入请求URL" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="请求头信息" prop="requestHeaders">
            <el-input v-model="queryParams.requestHeaders" placeholder="请输入请求头信息" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="响应头信息" prop="responseHeaders">
            <el-input v-model="queryParams.responseHeaders" placeholder="请输入响应头信息" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="响应体大小" prop="responseSizeBytes">
            <el-input v-model="queryParams.responseSizeBytes" placeholder="请输入响应体大小" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="重试次数" prop="retryCount">
            <el-input v-model="queryParams.retryCount" placeholder="请输入重试次数" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="检查开始时间" prop="checkStartTime">
            <el-date-picker clearable
              v-model="queryParams.checkStartTime"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择检查开始时间"
            />
          </el-form-item>
          <el-form-item label="检查结束时间" prop="checkEndTime">
            <el-date-picker clearable
              v-model="queryParams.checkEndTime"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择检查结束时间"
            />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['bicanmou:CryptoMonitoringLog:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['bicanmou:CryptoMonitoringLog:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['bicanmou:CryptoMonitoringLog:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['bicanmou:CryptoMonitoringLog:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="CryptoMonitoringLogList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="主键ID" align="center" prop="id" v-if="true" />
        <el-table-column label="交易所代码" align="center" prop="exchangeCode" />
        <el-table-column label="监控类型" align="center" prop="monitorType" />
        <el-table-column label="检查状态" align="center" prop="checkStatus">
          <template #default="scope">
            <dict-tag :options="success_fail" :value="scope.row.checkStatus"/>
          </template>
        </el-table-column>
        <el-table-column label="响应时间" align="center" prop="responseTimeMs" />
        <el-table-column label="总币种数量" align="center" prop="totalCoinsCount" />
        <el-table-column label="新币数量" align="center" prop="newCoinsCount" />
        <el-table-column label="错误信息" align="center" prop="errorMessage" />
        <el-table-column label="错误代码" align="center" prop="errorCode" />
        <el-table-column label="HTTP状态码" align="center" prop="httpStatusCode" />
        <el-table-column label="请求URL" align="center" prop="requestUrl" />
        <el-table-column label="请求头信息" align="center" prop="requestHeaders" />
        <el-table-column label="响应头信息" align="center" prop="responseHeaders" />
        <el-table-column label="响应体大小" align="center" prop="responseSizeBytes" />
        <el-table-column label="重试次数" align="center" prop="retryCount" />
        <el-table-column label="检查开始时间" align="center" prop="checkStartTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.checkStartTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="检查结束时间" align="center" prop="checkEndTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.checkEndTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="use_status" :value="scope.row.status"/>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['bicanmou:CryptoMonitoringLog:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['bicanmou:CryptoMonitoringLog:remove']"></el-button>
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
    <!-- 添加或修改监控日志对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="CryptoMonitoringLogFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="交易所代码" prop="exchangeCode">
          <el-input v-model="form.exchangeCode" placeholder="请输入交易所代码" />
        </el-form-item>
        <el-form-item label="检查状态" prop="checkStatus">
          <el-radio-group v-model="form.checkStatus">
            <el-radio
                v-for="dict in success_fail"
                :key="dict.value"
                :label="parseInt(dict.value)"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="响应时间" prop="responseTimeMs">
          <el-input v-model="form.responseTimeMs" placeholder="请输入响应时间" />
        </el-form-item>
        <el-form-item label="总币种数量" prop="totalCoinsCount">
          <el-input v-model="form.totalCoinsCount" placeholder="请输入总币种数量" />
        </el-form-item>
        <el-form-item label="新币数量" prop="newCoinsCount">
          <el-input v-model="form.newCoinsCount" placeholder="请输入新币数量" />
        </el-form-item>
        <el-form-item label="错误信息" prop="errorMessage">
            <el-input v-model="form.errorMessage" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="错误代码" prop="errorCode">
          <el-input v-model="form.errorCode" placeholder="请输入错误代码" />
        </el-form-item>
        <el-form-item label="HTTP状态码" prop="httpStatusCode">
          <el-input v-model="form.httpStatusCode" placeholder="请输入HTTP状态码" />
        </el-form-item>
        <el-form-item label="请求URL" prop="requestUrl">
            <el-input v-model="form.requestUrl" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="请求头信息" prop="requestHeaders">
            <el-input v-model="form.requestHeaders" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="响应头信息" prop="responseHeaders">
            <el-input v-model="form.responseHeaders" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="响应体大小" prop="responseSizeBytes">
          <el-input v-model="form.responseSizeBytes" placeholder="请输入响应体大小" />
        </el-form-item>
        <el-form-item label="重试次数" prop="retryCount">
          <el-input v-model="form.retryCount" placeholder="请输入重试次数" />
        </el-form-item>
        <el-form-item label="检查开始时间" prop="checkStartTime">
          <el-date-picker clearable
            v-model="form.checkStartTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择检查开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="检查结束时间" prop="checkEndTime">
          <el-date-picker clearable
            v-model="form.checkEndTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择检查结束时间">
          </el-date-picker>
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

<script setup name="CryptoMonitoringLog" lang="ts">
import { listCryptoMonitoringLog, getCryptoMonitoringLog, delCryptoMonitoringLog, addCryptoMonitoringLog, updateCryptoMonitoringLog } from '@/api/bicanmou/CryptoMonitoringLog';
import { CryptoMonitoringLogVO, CryptoMonitoringLogQuery, CryptoMonitoringLogForm } from '@/api/bicanmou/CryptoMonitoringLog/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { use_status, success_fail } = toRefs<any>(proxy?.useDict('use_status', 'success_fail'));

const CryptoMonitoringLogList = ref<CryptoMonitoringLogVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref(ElForm);
const CryptoMonitoringLogFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: CryptoMonitoringLogForm = {
  id: undefined,
  exchangeCode: undefined,
  monitorType: undefined,
  checkStatus: undefined,
  responseTimeMs: undefined,
  totalCoinsCount: undefined,
  newCoinsCount: undefined,
  errorMessage: undefined,
  errorCode: undefined,
  httpStatusCode: undefined,
  requestUrl: undefined,
  requestHeaders: undefined,
  responseHeaders: undefined,
  responseSizeBytes: undefined,
  retryCount: undefined,
  checkStartTime: undefined,
  checkEndTime: undefined,
  status: undefined,
  remark: undefined
}
const data = reactive<PageData<CryptoMonitoringLogForm, CryptoMonitoringLogQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    exchangeCode: undefined,
    monitorType: undefined,
    checkStatus: undefined,
    responseTimeMs: undefined,
    totalCoinsCount: undefined,
    newCoinsCount: undefined,
    errorMessage: undefined,
    errorCode: undefined,
    httpStatusCode: undefined,
    requestUrl: undefined,
    requestHeaders: undefined,
    responseHeaders: undefined,
    responseSizeBytes: undefined,
    retryCount: undefined,
    checkStartTime: undefined,
    checkEndTime: undefined,
    status: undefined,
  },
  rules: {
    id: [
      { required: true, message: "主键ID不能为空", trigger: "blur" }
    ],
    exchangeCode: [
      { required: true, message: "交易所代码不能为空", trigger: "blur" }
    ],
    monitorType: [
      { required: true, message: "监控类型不能为空", trigger: "change" }
    ],
    remark: [
      { required: true, message: "备注不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询监控日志列表 */
const getList = async () => {
  loading.value = true;
  const res = await listCryptoMonitoringLog(queryParams.value);
  CryptoMonitoringLogList.value = res.rows;
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
  CryptoMonitoringLogFormRef.value.resetFields();
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
const handleSelectionChange = (selection: CryptoMonitoringLogVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加监控日志";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: CryptoMonitoringLogVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改监控日志";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getCryptoMonitoringLog(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
  });
}

/** 提交按钮 */
const submitForm = () => {
  CryptoMonitoringLogFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateCryptoMonitoringLog(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addCryptoMonitoringLog(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: CryptoMonitoringLogVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除监控日志编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delCryptoMonitoringLog(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('bicanmou/CryptoMonitoringLog/export', {
    ...queryParams.value
  }, `CryptoMonitoringLog_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
