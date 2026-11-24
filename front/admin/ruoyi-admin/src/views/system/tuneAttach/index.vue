<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="微调id" prop="ftId">
            <el-input v-model="queryParams.ftId" placeholder="请输入微调id" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="微调文件id" prop="ftFileId">
            <el-input v-model="queryParams.ftFileId" placeholder="请输入微调文件id" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="上传文件id" prop="fileId">
            <el-input v-model="queryParams.fileId" placeholder="请输入上传文件id" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="文件状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="请选择文件状态" clearable>
              <el-option v-for="dict in finetune_attach_status" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="错误消息" prop="messages">
            <el-input v-model="queryParams.messages" placeholder="请输入错误消息" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['system:tuneAttach:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['system:tuneAttach:edit']"
              >修改</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['system:tuneAttach:remove']"
              >删除</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['system:tuneAttach:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="tuneAttachList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <!--        <el-table-column label="编码" align="center" prop="id" v-if="true" />-->
        <el-table-column label="微调id" align="center" prop="ftId" />
        <el-table-column label="微调文件id" align="center" prop="ftFileId" />
        <el-table-column label="上传文件id" align="center" prop="fileId" />
        <el-table-column label="文件状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="finetune_attach_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="文件内容" align="center" prop="fileContent" />
        <el-table-column label="转化后内容" align="center" prop="ftContent" />
        <el-table-column label="错误消息" align="center" prop="messages" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:tuneAttach:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:tuneAttach:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改微调附件对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="tuneAttachFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="微调id" prop="ftId">
          <el-input v-model="form.ftId" placeholder="请输入微调id" />
        </el-form-item>
        <el-form-item label="微调文件id" prop="ftFileId">
          <el-input v-model="form.ftFileId" placeholder="请输入微调文件id" />
        </el-form-item>
        <el-form-item label="上传文件id" prop="fileId">
          <el-input v-model="form.fileId" placeholder="请输入上传文件id" />
        </el-form-item>
        <el-form-item label="文件状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in finetune_attach_status" :key="dict.value" :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="文件内容">
          <editor v-model="form.fileContent" :min-height="192" />
        </el-form-item>
        <el-form-item label="微调内容">
          <editor v-model="form.ftContent" :min-height="192" />
        </el-form-item>
        <el-form-item label="错误消息" prop="messages">
          <el-input v-model="form.messages" type="textarea" placeholder="请输入内容" />
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

<script setup name="TuneAttach" lang="ts">
import { listTuneAttach, getTuneAttach, delTuneAttach, addTuneAttach, updateTuneAttach } from '@/api/system/tuneAttach';
import { TuneAttachVO, TuneAttachQuery, TuneAttachForm } from '@/api/system/tuneAttach/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { finetune_attach_status } = toRefs<any>(proxy?.useDict('finetune_attach_status'));

const tuneAttachList = ref<TuneAttachVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref(ElForm);
const tuneAttachFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: TuneAttachForm = {
  id: undefined,
  ftId: undefined,
  promptsStoreId: undefined,
  ftFileId: undefined,
  fileId: undefined,
  status: undefined,
  fileContent: undefined,
  ftContent: undefined,
  messages: undefined
}
const data = reactive<PageData<TuneAttachForm, TuneAttachQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    ftId: undefined,
     promptsStoreId: undefined,
    ftFileId: undefined,
    fileId: undefined,
    status: undefined,
    fileContent: undefined,
    ftContent: undefined,
    messages: undefined
  },
  rules: {
    id: [
      { required: true, message: "编码不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "文件状态不能为空", trigger: "change" }
    ],
    fileContent: [
      { required: true, message: "文件内容不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询微调附件列表 */
const getList = async () => {
  loading.value = true;
  const res = await listTuneAttach(queryParams.value);
  tuneAttachList.value = res.rows;
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
  tuneAttachFormRef.value.resetFields();
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
const handleSelectionChange = (selection: TuneAttachVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加微调附件";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: TuneAttachVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改微调附件";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getTuneAttach(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
  });
}

/** 提交按钮 */
const submitForm = () => {
  tuneAttachFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateTuneAttach(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addTuneAttach(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: TuneAttachVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除微调附件编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delTuneAttach(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('system/tuneAttach/export', {
    ...queryParams.value
  }, `tuneAttach_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
