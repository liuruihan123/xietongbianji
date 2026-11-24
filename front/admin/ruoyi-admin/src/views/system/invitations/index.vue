<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="168px">
          <el-form-item label="邀请者账号" prop="inviterUserName">
            <el-input v-model="queryParams.inviterUserName" placeholder="请输入邀请者的用户账号" clearable @keyup.enter="handleQuery" />
          </el-form-item>

          <el-form-item label="被邀请者用户账号" prop="inviteeUserName">
            <el-input v-model="queryParams.inviteeUserName" placeholder="请输入被邀请者的用户账号" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="邀请码" prop="inviterCode">
            <el-input v-model="queryParams.inviterCode" placeholder="请输入邀请码" clearable @keyup.enter="handleQuery" />
          </el-form-item>

          <el-form-item label="邀请状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="请选择邀请状态" clearable>
              <el-option v-for="dict in invitations" :key="dict.value" :label="dict.label" :value="dict.value" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['system:invitations:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['system:invitations:edit']"
              >修改</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['system:invitations:remove']"
              >删除</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['system:invitations:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="invitationsList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="编号" align="center" prop="id" v-if="true" />
        <el-table-column label="邀请者的用户 ID" align="center" prop="inviterId" />
        <el-table-column label="邀请者的用户账号" align="center" prop="inviterUserName" />
        <el-table-column label="被邀请者的用户 ID" align="center" prop="inviteeId" />
        <el-table-column label="被邀请者的用户账号" align="center" prop="inviteeUserName" />
        <el-table-column label="邀请码" align="center" prop="inviterCode" />
        <el-table-column label="奖励天数" align="center" prop="rewardDays" />
        <el-table-column label="邀请状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="invitations" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:invitations:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:invitations:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改邀请管理对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="invitationsFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="邀请者的用户 ID" prop="inviterId">
          <el-input v-model="form.inviterId" placeholder="请输入邀请者的用户 ID" />
        </el-form-item>
        <el-form-item label="邀请者的用户账号" prop="inviterUserName">
          <el-input v-model="form.inviterUserName" placeholder="请输入邀请者的用户账号" />
        </el-form-item>
        <el-form-item label="被邀请者的用户 ID" prop="inviteeId">
          <el-input v-model="form.inviteeId" placeholder="请输入被邀请者的用户 ID" />
        </el-form-item>
        <el-form-item label="被邀请者的用户账号" prop="inviteeUserName">
          <el-input v-model="form.inviteeUserName" placeholder="请输入被邀请者的用户账号" />
        </el-form-item>
        <el-form-item label="邀请码" prop="inviterCode">
          <el-input v-model="form.inviterCode" placeholder="请输入邀请码" />
        </el-form-item>
        <el-form-item label="奖励天数" prop="rewardDays">
          <el-input v-model="form.rewardDays" placeholder="请输入奖励天数" />
        </el-form-item>
        <el-form-item label="邀请状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in invitations" :key="dict.value" :label="dict.value">{{dict.label}}</el-radio>
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

<script setup name="Invitations" lang="ts">
import { listInvitations, getInvitations, delInvitations, addInvitations, updateInvitations } from '@/api/system/invitations';
import { InvitationsVO, InvitationsQuery, InvitationsForm } from '@/api/system/invitations/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { invitations } = toRefs<any>(proxy?.useDict('invitations'));

const invitationsList = ref<InvitationsVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref(ElForm);
const invitationsFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: InvitationsForm = {
  id: undefined,
  inviterId: undefined,
  inviterUserName: undefined,
  inviteeId: undefined,
  inviteeUserName: undefined,
  inviterCode: undefined,
  rewardDays: undefined,
  status: undefined,
  remark: undefined
}
const data = reactive<PageData<InvitationsForm, InvitationsQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    inviterId: undefined,
    inviterUserName: undefined,
    inviteeId: undefined,
    inviteeUserName: undefined,
    inviterCode: undefined,
    rewardDays: undefined,
    status: undefined,
  },
  rules: {
    id: [
      { required: true, message: "编号不能为空", trigger: "blur" }
    ],
    inviterId: [
      { required: true, message: "邀请者的用户 ID不能为空", trigger: "blur" }
    ],
    inviterUserName: [
      { required: true, message: "邀请者的用户账号不能为空", trigger: "blur" }
    ],
    inviteeId: [
      { required: true, message: "被邀请者的用户 ID不能为空", trigger: "blur" }
    ],
    inviteeUserName: [
      { required: true, message: "被邀请者的用户账号不能为空", trigger: "blur" }
    ],
    inviterCode: [
      { required: true, message: "邀请码不能为空", trigger: "blur" }
    ],
    rewardDays: [
      { required: true, message: "奖励天数不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "邀请状态不能为空", trigger: "change" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询邀请管理列表 */
const getList = async () => {
  loading.value = true;
  const res = await listInvitations(queryParams.value);
  invitationsList.value = res.rows;
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
  invitationsFormRef.value.resetFields();
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
const handleSelectionChange = (selection: InvitationsVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加邀请管理";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: InvitationsVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改邀请管理";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getInvitations(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
  });
}

/** 提交按钮 */
const submitForm = () => {
  invitationsFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateInvitations(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addInvitations(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: InvitationsVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除邀请管理编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delInvitations(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('system/invitations/export', {
    ...queryParams.value
  }, `invitations_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
