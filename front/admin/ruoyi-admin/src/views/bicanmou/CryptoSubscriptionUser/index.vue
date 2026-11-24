<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="用户ID" prop="userId">
            <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="用户名" prop="username">
            <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="手机号码" prop="phoneNumber">
            <el-input v-model="queryParams.phoneNumber" placeholder="请输入手机号码" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="邮箱地址" prop="email">
            <el-input v-model="queryParams.email" placeholder="请输入邮箱地址" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="订阅的交易所" prop="subscribedExchanges">
            <el-input v-model="queryParams.subscribedExchanges" placeholder="请输入订阅的交易所" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="是否启用通知" prop="notificationEnabled">
            <el-select v-model="queryParams.notificationEnabled" placeholder="请选择是否启用通知" clearable>
              <el-option
                v-for="dict in is_enable"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="最小市值过滤" prop="minMarketCapFilter">
            <el-input v-model="queryParams.minMarketCapFilter" placeholder="请输入最小市值过滤" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="是否排除稳定币" prop="excludeStablecoins">
            <el-input v-model="queryParams.excludeStablecoins" placeholder="请输入是否排除稳定币" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="是否排除测试币" prop="excludeTestCoins">
            <el-input v-model="queryParams.excludeTestCoins" placeholder="请输入是否排除测试币" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="订阅开始时间" prop="subscriptionStartTime">
            <el-date-picker clearable
              v-model="queryParams.subscriptionStartTime"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择订阅开始时间"
            />
          </el-form-item>
          <el-form-item label="订阅结束时间" prop="subscriptionEndTime">
            <el-date-picker clearable
              v-model="queryParams.subscriptionEndTime"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择订阅结束时间"
            />
          </el-form-item>
          <el-form-item label="订阅状态" prop="subscriptionStatus">
            <el-select v-model="queryParams.subscriptionStatus" placeholder="请选择订阅状态" clearable>
              <el-option
                v-for="dict in sub_status"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="最后通知时间" prop="lastNotificationTime">
            <el-date-picker clearable
              v-model="queryParams.lastNotificationTime"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择最后通知时间"
            />
          </el-form-item>
          <el-form-item label="通知次数统计" prop="notificationCount">
            <el-input v-model="queryParams.notificationCount" placeholder="请输入通知次数统计" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
              <el-option
                v-for="dict in is_enable"
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['bicanmou:CryptoSubscriptionUser:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['bicanmou:CryptoSubscriptionUser:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['bicanmou:CryptoSubscriptionUser:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['bicanmou:CryptoSubscriptionUser:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="CryptoSubscriptionUserList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="主键ID" align="center" prop="id" v-if="true" />
        <el-table-column label="用户ID" align="center" prop="userId" />
        <el-table-column label="用户名" align="center" prop="username" />
        <el-table-column label="手机号码" align="center" prop="phoneNumber" />
        <el-table-column label="邮箱地址" align="center" prop="email" />
        <el-table-column label="订阅的交易所" align="center" prop="subscribedExchanges" />
        <el-table-column label="通知方式" align="center" prop="notificationMethod">
          <template #default="scope">
            <dict-tag :options="noti_type" :value="scope.row.notificationMethod ? scope.row.notificationMethod.split(',') : []"/>
          </template>
        </el-table-column>
        <el-table-column label="是否启用通知" align="center" prop="notificationEnabled">
          <template #default="scope">
            <dict-tag :options="is_enable" :value="scope.row.notificationEnabled"/>
          </template>
        </el-table-column>
        <el-table-column label="最小市值过滤" align="center" prop="minMarketCapFilter" />
        <el-table-column label="是否排除稳定币" align="center" prop="excludeStablecoins" />
        <el-table-column label="是否排除测试币" align="center" prop="excludeTestCoins" />
        <el-table-column label="订阅开始时间" align="center" prop="subscriptionStartTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.subscriptionStartTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订阅结束时间" align="center" prop="subscriptionEndTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.subscriptionEndTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订阅状态" align="center" prop="subscriptionStatus">
          <template #default="scope">
            <dict-tag :options="sub_status" :value="scope.row.subscriptionStatus"/>
          </template>
        </el-table-column>
        <el-table-column label="最后通知时间" align="center" prop="lastNotificationTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.lastNotificationTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="通知次数统计" align="center" prop="notificationCount" />
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="is_enable" :value="scope.row.status"/>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['bicanmou:CryptoSubscriptionUser:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['bicanmou:CryptoSubscriptionUser:remove']"></el-button>
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
    <!-- 添加或修改订阅用户对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="CryptoSubscriptionUserFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phoneNumber">
          <el-input v-model="form.phoneNumber" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="邮箱地址" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱地址" />
        </el-form-item>
        <el-form-item label="订阅的交易所" prop="subscribedExchanges">
            <el-input v-model="form.subscribedExchanges" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="通知方式" prop="notificationMethod">
          <el-checkbox-group v-model="form.notificationMethod">
            <el-checkbox
                v-for="dict in noti_type"
                :key="dict.value"
                :label="dict.value">
                {{dict.label}}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="是否启用通知" prop="notificationEnabled">
          <el-radio-group v-model="form.notificationEnabled">
            <el-radio
                v-for="dict in is_enable"
                :key="dict.value"
                :label="parseInt(dict.value)"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="最小市值过滤" prop="minMarketCapFilter">
          <el-input v-model="form.minMarketCapFilter" placeholder="请输入最小市值过滤" />
        </el-form-item>
        <el-form-item label="是否排除稳定币" prop="excludeStablecoins">
          <el-input v-model="form.excludeStablecoins" placeholder="请输入是否排除稳定币" />
        </el-form-item>
        <el-form-item label="是否排除测试币" prop="excludeTestCoins">
          <el-input v-model="form.excludeTestCoins" placeholder="请输入是否排除测试币" />
        </el-form-item>
        <el-form-item label="订阅开始时间" prop="subscriptionStartTime">
          <el-date-picker clearable
            v-model="form.subscriptionStartTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择订阅开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="订阅结束时间" prop="subscriptionEndTime">
          <el-date-picker clearable
            v-model="form.subscriptionEndTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择订阅结束时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="订阅状态" prop="subscriptionStatus">
          <el-radio-group v-model="form.subscriptionStatus">
            <el-radio
                v-for="dict in sub_status"
                :key="dict.value"
                :label="parseInt(dict.value)"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="最后通知时间" prop="lastNotificationTime">
          <el-date-picker clearable
            v-model="form.lastNotificationTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择最后通知时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="通知次数统计" prop="notificationCount">
          <el-input v-model="form.notificationCount" placeholder="请输入通知次数统计" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
                v-for="dict in is_enable"
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

<script setup name="CryptoSubscriptionUser" lang="ts">
import { listCryptoSubscriptionUser, getCryptoSubscriptionUser, delCryptoSubscriptionUser, addCryptoSubscriptionUser, updateCryptoSubscriptionUser } from '@/api/bicanmou/CryptoSubscriptionUser';
import { CryptoSubscriptionUserVO, CryptoSubscriptionUserQuery, CryptoSubscriptionUserForm } from '@/api/bicanmou/CryptoSubscriptionUser/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sub_status, is_enable, noti_type } = toRefs<any>(proxy?.useDict('sub_status', 'is_enable', 'noti_type'));

const CryptoSubscriptionUserList = ref<CryptoSubscriptionUserVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref(ElForm);
const CryptoSubscriptionUserFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: CryptoSubscriptionUserForm = {
  id: undefined,
  userId: undefined,
  username: undefined,
  phoneNumber: undefined,
  email: undefined,
  subscribedExchanges: undefined,
  notificationMethod: '',
  notificationEnabled: undefined,
  minMarketCapFilter: undefined,
  excludeStablecoins: undefined,
  excludeTestCoins: undefined,
  subscriptionStartTime: undefined,
  subscriptionEndTime: undefined,
  subscriptionStatus: undefined,
  lastNotificationTime: undefined,
  notificationCount: undefined,
  status: undefined,
  remark: undefined
}
const data = reactive<PageData<CryptoSubscriptionUserForm, CryptoSubscriptionUserQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userId: undefined,
    username: undefined,
    phoneNumber: undefined,
    email: undefined,
    subscribedExchanges: undefined,
    notificationMethod: undefined,
    notificationEnabled: undefined,
    minMarketCapFilter: undefined,
    excludeStablecoins: undefined,
    excludeTestCoins: undefined,
    subscriptionStartTime: undefined,
    subscriptionEndTime: undefined,
    subscriptionStatus: undefined,
    lastNotificationTime: undefined,
    notificationCount: undefined,
    status: undefined,
  },
  rules: {
    id: [
      { required: true, message: "主键ID不能为空", trigger: "blur" }
    ],
    userId: [
      { required: true, message: "用户ID不能为空", trigger: "blur" }
    ],
    username: [
      { required: true, message: "用户名不能为空", trigger: "blur" }
    ],
    notificationMethod: [
      { required: true, message: "通知方式不能为空", trigger: "blur" }
    ],
    notificationEnabled: [
      { required: true, message: "是否启用通知不能为空", trigger: "change" }
    ],
    subscriptionStatus: [
      { required: true, message: "订阅状态不能为空", trigger: "change" }
    ],
    status: [
      { required: true, message: "状态不能为空", trigger: "change" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询订阅用户列表 */
const getList = async () => {
  loading.value = true;
  const res = await listCryptoSubscriptionUser(queryParams.value);
  CryptoSubscriptionUserList.value = res.rows;
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
  CryptoSubscriptionUserFormRef.value.resetFields();
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
const handleSelectionChange = (selection: CryptoSubscriptionUserVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加订阅用户";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: CryptoSubscriptionUserVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改订阅用户";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getCryptoSubscriptionUser(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
    // 如果notificationMethod是字符串且包含逗号，则转换为数组用于多选
    if (form.value.notificationMethod && typeof form.value.notificationMethod === 'string') {
      // 保持为字符串，不需要转换
    }
  });
}

/** 提交按钮 */
const submitForm = () => {
  CryptoSubscriptionUserFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      // notificationMethod已经是字符串，不需要join
      if (form.value.id) {
        await updateCryptoSubscriptionUser(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addCryptoSubscriptionUser(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: CryptoSubscriptionUserVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除订阅用户编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delCryptoSubscriptionUser(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('bicanmou/CryptoSubscriptionUser/export', {
    ...queryParams.value
  }, `CryptoSubscriptionUser_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
