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
          <el-form-item label="交易所中文名称" prop="exchangeNameCn">
            <el-input v-model="queryParams.exchangeNameCn" placeholder="请输入交易所中文名称" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="API基础URL" prop="apiBaseUrl">
            <el-input v-model="queryParams.apiBaseUrl" placeholder="请输入API基础URL" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="API端点" prop="apiEndpoint">
            <el-input v-model="queryParams.apiEndpoint" placeholder="请输入API端点" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="API密钥" prop="apiKey">
            <el-input v-model="queryParams.apiKey" placeholder="请输入API密钥" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="API密钥密码" prop="apiSecret">
            <el-input v-model="queryParams.apiSecret" placeholder="请输入API密钥密码" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="每分钟请求限制" prop="rateLimitPerMinute">
            <el-input v-model="queryParams.rateLimitPerMinute" placeholder="请输入每分钟请求限制" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="每秒请求限制" prop="rateLimitPerSecond">
            <el-input v-model="queryParams.rateLimitPerSecond" placeholder="请输入每秒请求限制" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="是否需要认证" prop="needAuth">
            <el-input v-model="queryParams.needAuth" placeholder="请输入是否需要认证" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="交易所梯队" prop="tier">
            <el-select v-model="queryParams.tier" placeholder="请选择交易所梯队" clearable>
              <el-option
                v-for="dict in exchange_tier"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="检查间隔" prop="checkIntervalSeconds">
            <el-input v-model="queryParams.checkIntervalSeconds" placeholder="请输入检查间隔" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="是否启用" prop="enabled">
            <el-select v-model="queryParams.enabled" placeholder="请选择是否启用" clearable>
              <el-option
                v-for="dict in is_enable"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
              <el-option
                v-for="dict in is_yes_no"
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['bicanmou:exchangeConfig:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['bicanmou:exchangeConfig:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['bicanmou:exchangeConfig:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['bicanmou:exchangeConfig:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="exchangeConfigList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="主键ID" align="center" prop="id" v-if="true" />
        <el-table-column label="交易所代码" align="center" prop="exchangeCode" />
        <el-table-column label="交易所名称" align="center" prop="exchangeName" />
        <el-table-column label="交易所中文名称" align="center" prop="exchangeNameCn" />
        <el-table-column label="API基础URL" align="center" prop="apiBaseUrl" />
        <el-table-column label="API端点" align="center" prop="apiEndpoint" />
        <el-table-column label="API密钥" align="center" prop="apiKey" />
        <el-table-column label="API密钥密码" align="center" prop="apiSecret" />
        <el-table-column label="每分钟请求限制" align="center" prop="rateLimitPerMinute" />
        <el-table-column label="每秒请求限制" align="center" prop="rateLimitPerSecond" />
        <el-table-column label="是否需要认证" align="center" prop="needAuth" />
        <el-table-column label="交易所梯队" align="center" prop="tier">
          <template #default="scope">
            <dict-tag :options="exchange_tier" :value="scope.row.tier"/>
          </template>
        </el-table-column>
        <el-table-column label="检查间隔" align="center" prop="checkIntervalSeconds" />
        <el-table-column label="是否启用" align="center" prop="enabled">
          <template #default="scope">
            <dict-tag :options="is_enable" :value="scope.row.enabled"/>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="is_yes_no" :value="scope.row.status"/>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['bicanmou:exchangeConfig:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['bicanmou:exchangeConfig:remove']"></el-button>
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
    <!-- 添加或修改交易所配置对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="exchangeConfigFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="交易所代码" prop="exchangeCode">
          <el-input v-model="form.exchangeCode" placeholder="请输入交易所代码" />
        </el-form-item>
        <el-form-item label="交易所名称" prop="exchangeName">
          <el-input v-model="form.exchangeName" placeholder="请输入交易所名称" />
        </el-form-item>
        <el-form-item label="交易所中文名称" prop="exchangeNameCn">
          <el-input v-model="form.exchangeNameCn" placeholder="请输入交易所中文名称" />
        </el-form-item>
        <el-form-item label="API基础URL" prop="apiBaseUrl">
          <el-input v-model="form.apiBaseUrl" placeholder="请输入API基础URL" />
        </el-form-item>
        <el-form-item label="API端点" prop="apiEndpoint">
          <el-input v-model="form.apiEndpoint" placeholder="请输入API端点" />
        </el-form-item>
        <el-form-item label="API密钥" prop="apiKey">
          <el-input v-model="form.apiKey" placeholder="请输入API密钥" />
        </el-form-item>
        <el-form-item label="API密钥密码" prop="apiSecret">
          <el-input v-model="form.apiSecret" placeholder="请输入API密钥密码" />
        </el-form-item>
        <el-form-item label="每分钟请求限制" prop="rateLimitPerMinute">
          <el-input v-model="form.rateLimitPerMinute" placeholder="请输入每分钟请求限制" />
        </el-form-item>
        <el-form-item label="每秒请求限制" prop="rateLimitPerSecond">
          <el-input v-model="form.rateLimitPerSecond" placeholder="请输入每秒请求限制" />
        </el-form-item>
        <el-form-item label="是否需要认证" prop="needAuth">
          <el-input v-model="form.needAuth" placeholder="请输入是否需要认证" />
        </el-form-item>
        <el-form-item label="交易所梯队" prop="tier">
          <el-radio-group v-model="form.tier">
            <el-radio
                v-for="dict in exchange_tier"
                :key="dict.value"
                :label="parseInt(dict.value)"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="检查间隔" prop="checkIntervalSeconds">
          <el-input v-model="form.checkIntervalSeconds" placeholder="请输入检查间隔" />
        </el-form-item>
        <el-form-item label="是否启用" prop="enabled">
          <el-radio-group v-model="form.enabled">
            <el-radio
                v-for="dict in is_enable"
                :key="dict.value"
                :label="parseInt(dict.value)"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
                v-for="dict in is_yes_no"
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

<script setup name="ExchangeConfig" lang="ts">
import { listExchangeConfig, getExchangeConfig, delExchangeConfig, addExchangeConfig, updateExchangeConfig } from '@/api/bicanmou/exchangeConfig';
import { ExchangeConfigVO, ExchangeConfigQuery, ExchangeConfigForm } from '@/api/bicanmou/exchangeConfig/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { is_yes_no, is_enable, exchange_tier } = toRefs<any>(proxy?.useDict('is_yes_no', 'is_enable', 'exchange_tier'));

const exchangeConfigList = ref<ExchangeConfigVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const daterangeCreateTime = ref([]);

const queryFormRef = ref(ElForm);
const exchangeConfigFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: ExchangeConfigForm = {
  id: undefined,
  exchangeCode: undefined,
  exchangeName: undefined,
  exchangeNameCn: undefined,
  apiBaseUrl: undefined,
  apiEndpoint: undefined,
  apiKey: undefined,
  apiSecret: undefined,
  rateLimitPerMinute: undefined,
  rateLimitPerSecond: undefined,
  needAuth: undefined,
  tier: undefined,
  checkIntervalSeconds: undefined,
  enabled: undefined,
  status: undefined,
  remark: undefined
}
const data = reactive<PageData<ExchangeConfigForm, ExchangeConfigQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    exchangeCode: undefined,
    exchangeName: undefined,
    exchangeNameCn: undefined,
    apiBaseUrl: undefined,
    apiEndpoint: undefined,
    apiKey: undefined,
    apiSecret: undefined,
    rateLimitPerMinute: undefined,
    rateLimitPerSecond: undefined,
    needAuth: undefined,
    tier: undefined,
    checkIntervalSeconds: undefined,
    enabled: undefined,
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
    exchangeNameCn: [
      { required: true, message: "交易所中文名称不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询交易所配置列表 */
const getList = async () => {
  loading.value = true;
  queryParams.value.params = {};
  if (null != daterangeCreateTime.value && daterangeCreateTime.value.length === 2) {
    queryParams.value.params["beginCreateTime"] = daterangeCreateTime.value[0];
    queryParams.value.params["endCreateTime"] = daterangeCreateTime.value[1];
  }
  const res = await listExchangeConfig(queryParams.value);
  exchangeConfigList.value = res.rows;
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
  exchangeConfigFormRef.value.resetFields();
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
const resetQuery = () => {
  daterangeCreateTime.value = [];
  queryFormRef.value.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
const handleSelectionChange = (selection: ExchangeConfigVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加交易所配置";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: ExchangeConfigVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改交易所配置";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getExchangeConfig(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
  });
}

/** 提交按钮 */
const submitForm = () => {
  exchangeConfigFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateExchangeConfig(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addExchangeConfig(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: ExchangeConfigVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除交易所配置编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delExchangeConfig(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('bicanmou/exchangeConfig/export', {
    ...queryParams.value
  }, `exchangeConfig_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
