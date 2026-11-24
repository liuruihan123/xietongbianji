<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="知识库ID" prop="kid">
            <el-input v-model="queryParams.kid" placeholder="请输入知识库ID" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="文档名称" prop="docName">
            <el-input v-model="queryParams.docName" placeholder="请输入文档名称" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="创建人" prop="createBy">
            <el-input v-model="queryParams.createBy" placeholder="请输入创建人" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['knowledge:knowledgeAttach:add']">新增附件</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['knowledge:knowledgeAttach:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['knowledge:knowledgeAttach:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['knowledge:knowledgeAttach:export']">导出</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Close" @click="handleClose">关闭</el-button>
          </el-col>

          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="knowledgeAttachList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
<!--        <el-table-column label="编号" align="center" prop="id" v-if="true" />-->
<!--        <el-table-column label="知识库ID" align="center" prop="kid" />-->
        <el-table-column label="文档编码" align="center" prop="docId" />
        <el-table-column label="文档名称" align="center" prop="docName" />
        <el-table-column label="文档类型" align="center" prop="docType" />
<!--        <el-table-column label="文档内容" align="center" prop="content" />-->
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="知识片段" placement="top">
              <el-button link type="primary" icon="Grid" @click="handleFragment(scope.row)" v-hasPermi="['knowledge:knowledgeAttach:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除附件" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['knowledge:knowledgeAttach:remove']"></el-button>
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
    <!-- 添加或修改知识库文件对话框 -->
    <el-dialog :title="upload.title" v-model="upload.open" width="500px" append-to-body>
<!--      <el-form ref="knowledgeAttachFormRef" :model="form" :rules="rules" label-width="80px">-->
      <el-upload
        ref="uploadRef"
        :limit="1"
        :data ="{ kid: route.params.kid as string}"
        accept=".md,.pdf,.docx,.txt,.csv"
        :headers="upload.headers"
        :action="upload.url "
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
          <div class="text-left el-upload__tip" style="    color: #e81010;">
            <div class="el-upload__tip"> 请上传一个10MB以内的文件 ，支持 md、pdf、docx、txt、csv 等文件格式 </div>
            <p> 温馨提醒： </p>
            <p> 1. 如果文件较大，上传时间可能较长，请耐心等待。 </p>
            <p> 2. 上传txt文本，请使用UTF8格式，以避免乱码。 </p>
            <p> 3. 单次上传字数建议5万以下，解析1万字大约需要10秒。 </p>
          </div>
        </template>
        </el-upload>

    </el-dialog>
  </div>
</template>

<script setup name="KnowledgeAttach" lang="ts">
import { listKnowledgeAttach, getKnowledgeAttach, delKnowledgeAttach, addKnowledgeAttach, updateKnowledgeAttach } from '@/api/knowledge/knowledgeAttach';

import { KnowledgeAttachVO, KnowledgeAttachQuery, KnowledgeAttachForm } from '@/api/knowledge/knowledgeAttach/types';
import { ComponentInternalInstance } from 'vue';
import { DateModelType, ElTree, ElUpload, UploadFile, ElForm } from 'element-plus';
import {getToken} from "@/utils/auth";
import {Knowledge_infoVO} from "@/api/knowledge/knowledge_info/types";
const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const knowledgeAttachList = ref<KnowledgeAttachVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref(ElForm);
// const knowledgeAttachFormRef = ref(ElForm);
const uploadRef = ref(ElUpload);

const upload = reactive<ImportOption>({
  // 是否显示弹出层
  open: false,
  // 弹出层标题
  title: "上传附件",
  // 是否禁用上传
  isUploading: false,

  // 设置上传的请求头部
  headers: { Authorization: "Bearer " + getToken() },
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/knowledge/attach/upload"
})

/**文件上传中处理 */
const handleFileUploadProgress = () => {
  upload.isUploading = true;
}
/** 文件上传成功处理 */
const handleFileSuccess = (response: any, file: UploadFile) => {
  upload.open = false;
  upload.isUploading = false;
  uploadRef.value.handleRemove(file);
  ElMessageBox.alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "上传结果", { dangerouslyUseHTMLString: true });
  getList();
}

/** 查看知识片段 */
const handleFragment = (row: KnowledgeAttachVO) => {
  router.push("/knowledge/knowledgeFragment/fragment/" + row.kid+"/"+row.docId);
}


const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: KnowledgeAttachForm = {
  docName: undefined,
  content: undefined,
}
const data = reactive<PageData<KnowledgeAttachForm, KnowledgeAttachQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    kid: route.params.kid as string ,
    docName: undefined,
    content: undefined,
    createBy: undefined
  },
  rules: {
    docName: [
      // { required: true, message: "文档名称不能为空", trigger: "blur" }
    ],
    content: [
      // { required: true, message: "文档内容不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询知识库文件列表 */
const getList = async () => {
  loading.value = true;
  const res = await listKnowledgeAttach(queryParams.value);
  knowledgeAttachList.value = res.rows;
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
  // knowledgeAttachFormRef.value.resetFields();
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
const handleSelectionChange = (selection: KnowledgeAttachVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  // dialog.visible = true;
  // dialog.title = "添加知识库文件";
  upload.title = "添加知识库文件";
  upload.open = true;
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: KnowledgeAttachVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改知识库文件";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getKnowledgeAttach(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
  });
}

// 返回按钮
const handleClose = () => {
  const obj = { path: "/operate/knowledge_info" };
  proxy?.$tab.closeOpenPage(obj);
}


/** 提交按钮 */
// const submitForm = () => {
//   knowledgeAttachFormRef.value.validate(async (valid: boolean) => {
//     if (valid) {
//       buttonLoading.value = true;
//       if (form.value.id) {
//         await updateKnowledgeAttach(form.value).finally(() =>  buttonLoading.value = false);
//       } else {
//         await addKnowledgeAttach(form.value).finally(() =>  buttonLoading.value = false);
//       }
//       proxy?.$modal.msgSuccess("修改成功");
//       dialog.visible = false;
//       await getList();
//     }
//   });
// }

/** 删除按钮操作 */
const handleDelete = async (row?: KnowledgeAttachVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除知识库文件编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delKnowledgeAttach(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('knowledge/knowledgeAttach/export', {
    ...queryParams.value
  }, `knowledgeAttach_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
