<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="模版名称" prop="name">
            <el-input v-model="queryParams.name" placeholder="请输入模版名称" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="模版用途" prop="temDescription">
            <el-select v-model="queryParams.temDescription" placeholder="请选择模版用途" clearable>
              <el-option label="第一遍检查" value="1" />
              <el-option label="第二遍检查" value="2" />
              <el-option label="免费检查" value="0" />
            </el-select>
          </el-form-item>

          <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
              <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['system:qaTemplate:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['system:qaTemplate:edit']"
              >修改</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['system:qaTemplate:remove']"
              >删除</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['system:qaTemplate:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="qaTemplateList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="编号" align="center" prop="id" v-if="true" />


        <el-table-column label="模版名称" align="center" prop="name" />
        <el-table-column label="模版用途" align="center" prop="temDescription">
          <template #default="scope">
            <span v-if="scope.row.temDescription === '1'">第一遍检查</span>
            <span v-else-if="scope.row.temDescription === '2'">第二遍检查</span>
            <span v-else-if="scope.row.temDescription === '0'">免费检查</span>
            <span v-else>{{ scope.row.temDescription }}</span>
          </template>
        </el-table-column>
        <el-table-column label="模版内容" align="center" prop="templateContent">
          <template #default="scope">
            <el-tooltip :content="scope.row.templateContent" placement="top" :show-after="200">
              <div class="template-content">
                <span v-html="formatTemplateContent(scope.row.templateContent)"></span>
              </div>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:qaTemplate:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:qaTemplate:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改问答模版对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="600px" append-to-body>
      <el-form ref="qaTemplateFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="模版名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入模版名称" />
        </el-form-item>

        <el-form-item label="模版用途" prop="temDescription">
          <el-select v-model="form.temDescription" placeholder="请选择模版用途">
            <el-option label="第一遍检查" value="1" />
            <el-option label="第二遍检查" value="2" />
            <el-option label="免费检查" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="模版内容">
          <highlight-textarea v-model="form.templateContent" placeholder="请输入模版内容" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
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

<script setup name="QaTemplate" lang="ts">
import { listQaTemplate, getQaTemplate, delQaTemplate, addQaTemplate, updateQaTemplate } from '@/api/system/qaTemplate';
import { QaTemplateVO, QaTemplateQuery, QaTemplateForm } from '@/api/system/qaTemplate/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';
import HighlightTextarea from '@/components/textarea/HighlightTextarea.vue'
const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_normal_disable } = toRefs<any>(proxy?.useDict('sys_normal_disable'));
const qaTemplateList = ref<QaTemplateVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref(ElForm);
const qaTemplateFormRef = ref<InstanceType<typeof ElForm>>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: QaTemplateForm = {
  id: undefined,
  name: undefined,
  temDescription: undefined,
  roleType: 'user',
  templateContent: undefined,
  remark: undefined,
  status: '0'
}
const data = reactive<PageData<QaTemplateForm, QaTemplateQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: undefined,
    temDescription: undefined,
    roleType: 'user',
    templateContent: undefined,
    status: undefined
  },
  rules: {
    id: [
      { required: true, message: "编号不能为空", trigger: "blur" }
    ],
    name: [
      { required: true, message: "模版名称不能为空", trigger: "blur" }
    ],
    temDescription: [
      { required: true, message: "模版用途不能为空", trigger: "change" }
    ],
    templateContent: [
      { required: true, message: "模版内容不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "状态不能为空", trigger: "change" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询问答模版列表 */
const getList = async () => {
  loading.value = true;
  const res = await listQaTemplate(queryParams.value);
  qaTemplateList.value = res.rows;
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
  if (qaTemplateFormRef.value) {
    qaTemplateFormRef.value.resetFields();
  }
  form.value.templateContent = '';
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
const handleSelectionChange = (selection: QaTemplateVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加问答模版";
}

/** 修改按钮操作 */
const handleUpdate = (row?: QaTemplateVO) => {
  reset();
  loading.value = true;
  dialog.visible = true;
  dialog.title = "修改问答模版";
  nextTick(async () => {
    const _id = row?.id || ids.value[0];
    const res = await getQaTemplate(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
  });
}



/** 提交按钮 */
const submitForm = () => {
  if (!qaTemplateFormRef.value) return;

  qaTemplateFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateQaTemplate(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addQaTemplate(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: QaTemplateVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除问答模版编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delQaTemplate(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('system/qaTemplate/export', {
    ...queryParams.value
  }, `qaTemplate_${new Date().getTime()}.xlsx`)
}

// 添加格式化模版内容的方法
const formatTemplateContent = (content: string) => {
  if (!content) return '';
  return content.replace(/\{\{(.*?)\}\}/g, '<span class="highlight-var">{{$1}}</span>');
}

onMounted(() => {
  getList();
});
</script>

<style scoped>
.template-content {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px; /* 可以根据需要调整宽度 */
}

.template-content :deep(.highlight-var) {
  color: #409EFF;
}

.cursor-help {
  cursor: help;
}
.ml-2 {
  margin-left: 8px;
}
</style>
