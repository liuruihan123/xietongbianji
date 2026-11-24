<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="标题" prop="title">
            <el-input v-model="queryParams.title" placeholder="请输入标题" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
              <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="来源文件名称" prop="sourceName">
            <el-input v-model="queryParams.sourceName" placeholder="请输入来源文件名称" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['system:qaCase:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['system:qaCase:edit']"
              >修改</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['system:qaCase:remove']"
              >删除</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['system:qaCase:export']">导出</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="info" plain icon="Upload" @click="handleImport" v-hasPermi="['system:qaCase:import']">导入</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="qaCaseList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="编号" align="center" prop="id" v-if="true" />
        <el-table-column label="标题" align="center" prop="title" />
        <el-table-column label="案例内容" align="center" prop="caseContent">
          <template #default="scope">
            <el-tooltip class="box-item" effect="dark" :content="scope.row.caseContent" placement="top">
              <span class="text-ellipsis">{{ scope.row.caseContent }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="来源文件" align="center" prop="sourceName">
          <template #default="scope">
            <el-tooltip v-if="scope.row.sourceUrl" content="点击即可下载文件" placement="top">
              <el-link type="primary" :href="scope.row.sourceUrl" target="_blank" :download="scope.row.sourceName">
                {{ scope.row.sourceName }}
              </el-link>
            </el-tooltip>
            <span v-else>{{ scope.row.sourceName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:qaCase:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:qaCase:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改对话案例对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="auto" append-to-body>
      <el-form ref="qaCaseFormRef" :model="form" :rules="rules" label-width="180px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="案例内容">
          <editor v-model="form.caseContent" :min-height="192" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="来源文件地址" prop="sourceUrl">
          <el-input v-model="form.sourceUrl" placeholder="请输入来源文件地址" />
        </el-form-item>
        <el-form-item label="来源文件名称" prop="sourceName">
          <el-input v-model="form.sourceName" placeholder="请输入来源文件名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 添加文件上传对话框 -->
    <el-dialog :title="upload.title" v-model="upload.open" width="500px" append-to-body>
      <el-upload
        ref="uploadRef"
        :limit="1"
        accept=".md,.pdf,.docx,.txt,.csv"
        :headers="upload.headers"
        :action="upload.url"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="true"
        drag
      >
        <el-icon class="el-icon--upload">
          <i-ep-upload-filled />
        </el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="text-left el-upload__tip" style="color: #e81010;">
            <div class="el-upload__tip">请上传一个10MB以内的docx文件</div>
            <p>温馨提醒：</p>
            <p>1. 一次只能上传一个文件。</p>
            <p>2. 如果一个文件存在多个案例，会以标题二作为分割。</p>
          </div>
        </template>
      </el-upload>
    </el-dialog>
  </div>
</template>

<script setup name="QaCase" lang="ts">
import { listQaCase, getQaCase, delQaCase, addQaCase, updateQaCase } from '@/api/system/qaCase';
import { QaCaseVO, QaCaseQuery, QaCaseForm } from '@/api/system/qaCase/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm, ElUpload, UploadFile } from 'element-plus';
import { getToken } from "@/utils/auth";

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_normal_disable } = toRefs<any>(proxy?.useDict('sys_normal_disable'));

const qaCaseList = ref<QaCaseVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref(ElForm);
const qaCaseFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const uploadRef = ref(ElUpload);

const upload = reactive<ImportOption>({
  open: false,
  title: "导入案例",
  isUploading: false,
  headers: { Authorization: "Bearer " + getToken() },
  url: import.meta.env.VITE_APP_BASE_API + "/system/qaCase/attach/upload"
});

const initFormData: QaCaseForm = {
  id: undefined,
  title: undefined,
  caseContent: undefined,
  status: undefined,
  remark: undefined,
  sourceUrl: undefined,
  sourceName: undefined,
  sourceId: undefined,
}
const data = reactive<PageData<QaCaseForm, QaCaseQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    title: undefined,
    status: undefined,
    sourceName: undefined,
    sourceId: undefined,
  },
  rules: {
    id: [
      { required: true, message: "编号不能为空", trigger: "blur" }
    ],
    title: [
      { required: true, message: "标题不能为空", trigger: "blur" }
    ],
    caseContent: [
      { required: true, message: "案例内容不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "状态不能为空", trigger: "change" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询对话案例列表 */
const getList = async () => {
  loading.value = true;
  const res = await listQaCase(queryParams.value);
  qaCaseList.value = res.rows;
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
  qaCaseFormRef.value.resetFields();
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
const handleSelectionChange = (selection: QaCaseVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加对话案例";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: QaCaseVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改对话案例";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getQaCase(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
  });
}

/** 提交按钮 */
const submitForm = () => {
  qaCaseFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateQaCase(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addQaCase(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: QaCaseVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除对话案例编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delQaCase(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('system/qaCase/export', {
    ...queryParams.value
  }, `qaCase_${new Date().getTime()}.xlsx`)
}

/** 导入按钮操作 */
const handleImport = () => {
  upload.title = "导入案例";
  upload.open = true;
};

/** 文件上传中处理 */
const handleFileUploadProgress = () => {
  upload.isUploading = true;
};

/** 文件上传成功处理 */
const handleFileSuccess = (response: any, file: UploadFile) => {
  upload.open = false;
  upload.isUploading = false;
  uploadRef.value.handleRemove(file);
  ElMessageBox.alert(
    "<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>",
    "上传结果",
    { dangerouslyUseHTMLString: true }
  );
  getList();
};

onMounted(() => {
  getList();
});
</script>

<style scoped>
.text-ellipsis {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: inline-block;
  width: 100%;
}
</style>
