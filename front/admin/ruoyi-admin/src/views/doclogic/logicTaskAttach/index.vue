<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="逻辑任务" prop="lid">
            <el-input v-model="queryParams.lid" placeholder="请输入逻辑任务" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="文档" prop="docId">
            <el-input v-model="queryParams.docId" placeholder="请输入文档" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="文档地址" prop="docUrl">
            <el-input v-model="queryParams.docUrl" placeholder="请输入文档地址" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="文档名称" prop="docName">
            <el-input v-model="queryParams.docName" placeholder="请输入文档名称" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="" prop="createTime">
            <el-date-picker clearable
              v-model="queryParams.createTime"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择"
            />
          </el-form-item>
          <el-form-item label="" prop="createBy">
            <el-input v-model="queryParams.createBy" placeholder="请输入" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['doclogic:logicTaskAttach:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['doclogic:logicTaskAttach:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['doclogic:logicTaskAttach:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['doclogic:logicTaskAttach:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="logicTaskAttachList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="" align="center" prop="id" v-if="true" />
        <el-table-column label="逻辑任务" align="center" prop="lid" />
        <el-table-column label="文档" align="center" prop="docId" />
        <el-table-column label="文档地址" align="center" prop="docUrl" />
        <el-table-column label="文档名称" align="center" prop="docName" />
        <el-table-column label="文档类型" align="center" prop="docType" />
        <el-table-column label="" align="center" prop="createTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="" align="center" prop="createBy" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['doclogic:logicTaskAttach:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['doclogic:logicTaskAttach:remove']"></el-button>
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
    <!-- 添加或修改逻辑任务附件对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="logicTaskAttachFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="逻辑任务" prop="lid">
          <el-input v-model="form.lid" placeholder="请输入逻辑任务" />
        </el-form-item>
        <el-form-item label="文档" prop="docId">
          <el-input v-model="form.docId" placeholder="请输入文档" />
        </el-form-item>
        <el-form-item label="文档地址" prop="docUrl">
          <el-input v-model="form.docUrl" placeholder="请输入文档地址" />
        </el-form-item>
        <el-form-item label="文档名称" prop="docName">
          <el-input v-model="form.docName" placeholder="请输入文档名称" />
        </el-form-item>
        <el-form-item label="文档内容">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <el-form-item label="" prop="remark">
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

<script setup name="LogicTaskAttach" lang="ts">
import { listLogicTaskAttach, getLogicTaskAttach, delLogicTaskAttach, addLogicTaskAttach, updateLogicTaskAttach } from '@/api/doclogic/logicTaskAttach';
import { LogicTaskAttachVO, LogicTaskAttachQuery, LogicTaskAttachForm } from '@/api/doclogic/logicTaskAttach/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const logicTaskAttachList = ref<LogicTaskAttachVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref(ElForm);
const logicTaskAttachFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: LogicTaskAttachForm = {
  id: undefined,
  lid: undefined,
  docId: undefined,
  docUrl: undefined,
  docName: undefined,
  docType: undefined,
  content: undefined,
  remark: undefined
}
const data = reactive<PageData<LogicTaskAttachForm, LogicTaskAttachQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    lid: undefined,
    docId: undefined,
    docUrl: undefined,
    docName: undefined,
    docType: undefined,
    createTime: undefined,
    createBy: undefined,
  },
  rules: {
    id: [
      { required: true, message: "不能为空", trigger: "blur" }
    ],
    lid: [
      { required: true, message: "逻辑任务不能为空", trigger: "blur" }
    ],
    docId: [
      { required: true, message: "文档不能为空", trigger: "blur" }
    ],
    docUrl: [
      { required: true, message: "文档地址不能为空", trigger: "blur" }
    ],
    docName: [
      { required: true, message: "文档名称不能为空", trigger: "blur" }
    ],
    docType: [
      { required: true, message: "文档类型不能为空", trigger: "change" }
    ],
    content: [
      { required: true, message: "文档内容不能为空", trigger: "blur" }
    ],
    remark: [
      { required: true, message: "不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询逻辑任务附件列表 */
const getList = async () => {
  loading.value = true;
  const res = await listLogicTaskAttach(queryParams.value);
  logicTaskAttachList.value = res.rows;
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
  logicTaskAttachFormRef.value.resetFields();
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
const handleSelectionChange = (selection: LogicTaskAttachVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加逻辑任务附件";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: LogicTaskAttachVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改逻辑任务附件";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getLogicTaskAttach(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
  });
}

/** 提交按钮 */
const submitForm = () => {
  logicTaskAttachFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateLogicTaskAttach(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addLogicTaskAttach(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: LogicTaskAttachVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除逻辑任务附件编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delLogicTaskAttach(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('doclogic/logicTaskAttach/export', {
    ...queryParams.value
  }, `logicTaskAttach_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
