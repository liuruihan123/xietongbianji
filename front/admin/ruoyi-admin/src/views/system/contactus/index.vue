<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="用户留言" prop="userMessage">
            <el-input v-model="queryParams.userMessage" placeholder="请输入用户留言" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="创建时间" prop="createTime">
            <el-date-picker clearable v-model="queryParams.createTime" type="date" value-format="YYYY-MM-DD" placeholder="请选择创建时间" />
          </el-form-item>
          <el-form-item label="创建人账号" prop="createdUserName">
            <el-input v-model="queryParams.createdUserName" placeholder="请输入创建人账号" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="创建人" prop="createdBy">
            <el-input v-model="queryParams.createdBy" placeholder="请输入创建人" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['system:contactus:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['system:contactus:edit']"
              >修改</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['system:contactus:remove']"
              >删除</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['system:contactus:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="contactusList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="编号" align="center" prop="id" v-if="true" />
        <el-table-column label="渠道信息" align="center" prop="channel" />
        <el-table-column label="用户留言" align="center" prop="userMessage" />
        <el-table-column label="创建人账号" align="center" prop="createdUserName" />
        <el-table-column label="创建时间" align="center" prop="createTime" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:contactus:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:contactus:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改contactus对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="contactusFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="渠道信息" prop="channel">
          <el-input v-model="form.channel" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="用户留言" prop="userMessage">
          <el-input v-model="form.userMessage" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="创建人账号" prop="createdUserName">
          <el-input v-model="form.createdUserName" placeholder="请输入创建人账号" />
        </el-form-item>
        <el-form-item label="创建人" prop="createdBy">
          <el-input v-model="form.createdBy" placeholder="请输入创建人" />
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

<script setup name="Contactus" lang="ts">
import { listContactus, getContactus, delContactus, addContactus, updateContactus } from '@/api/system/contactus';
import { ContactusVO, ContactusQuery, ContactusForm } from '@/api/system/contactus/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const contactusList = ref<ContactusVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref(ElForm);
const contactusFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: ContactusForm = {
  id: undefined,
  channel: undefined,
  userMessage: undefined,
  createdUserName: undefined,
  createdBy: undefined,
}
const data = reactive<PageData<ContactusForm, ContactusQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userMessage: undefined,
    createTime: undefined,
    createdUserName: undefined,
    createdBy: undefined,
  },
  rules: {
    id: [
      { required: true, message: "编号不能为空", trigger: "blur" }
    ],
    channel: [
      { required: true, message: "渠道信息不能为空", trigger: "blur" }
    ],
    userMessage: [
      { required: true, message: "用户留言不能为空", trigger: "blur" }
    ],
    createdUserName: [
      { required: true, message: "创建人账号不能为空", trigger: "blur" }
    ],
    createdBy: [
      { required: true, message: "创建人不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询contactus列表 */
const getList = async () => {
  loading.value = true;
  const res = await listContactus(queryParams.value);
  contactusList.value = res.rows;
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
  contactusFormRef.value.resetFields();
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
const handleSelectionChange = (selection: ContactusVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加contactus";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: ContactusVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改contactus";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getContactus(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
  });
}

/** 提交按钮 */
const submitForm = () => {
  contactusFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateContactus(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addContactus(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: ContactusVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除contactus编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delContactus(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('system/contactus/export', {
    ...queryParams.value
  }, `contactus_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
