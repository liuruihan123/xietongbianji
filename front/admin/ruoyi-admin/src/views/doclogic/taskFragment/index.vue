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
          <el-form-item label="文档片段" prop="fid">
            <el-input v-model="queryParams.fid" placeholder="请输入文档片段" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="片段所属页码" prop="fPage">
            <el-input v-model="queryParams.fPage" placeholder="请输入片段所属页码" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="片段索引下标" prop="idx">
            <el-input v-model="queryParams.idx" placeholder="请输入片段索引下标" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="" style="width: 308px">
            <el-date-picker
                v-model="daterangeCreateTime"
                value-format="YYYY-MM-DD HH:mm:ss"
                type="daterange"
                range-separator="-"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['doclogic:taskFragment:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['doclogic:taskFragment:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['doclogic:taskFragment:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['doclogic:taskFragment:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="taskFragmentList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="编号" align="center" prop="id" v-if="false" />
        <el-table-column label="逻辑任务" align="center" prop="lid" />
        <el-table-column label="文档" align="center" prop="docId" />
        <el-table-column label="文档片段" align="center" prop="fid" />
        <el-table-column label="片段所属页码" align="center" prop="fPage" />
        <el-table-column label="片段索引下标" align="center" prop="idx" />
        <el-table-column label="片段文档内容" align="center" prop="content" />
        <el-table-column label="" align="center" prop="createTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="" align="center" prop="createBy" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['doclogic:taskFragment:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['doclogic:taskFragment:remove']"></el-button>
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
    <!-- 添加或修改文档片段对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="taskFragmentFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="逻辑任务" prop="lid">
          <el-input v-model="form.lid" placeholder="请输入逻辑任务" />
        </el-form-item>
        <el-form-item label="文档" prop="docId">
          <el-input v-model="form.docId" placeholder="请输入文档" />
        </el-form-item>
        <el-form-item label="文档片段" prop="fid">
          <el-input v-model="form.fid" placeholder="请输入文档片段" />
        </el-form-item>
        <el-form-item label="片段所属页码" prop="fPage">
          <el-input v-model="form.fPage" placeholder="请输入片段所属页码" />
        </el-form-item>
        <el-form-item label="片段索引下标" prop="idx">
          <el-input v-model="form.idx" placeholder="请输入片段索引下标" />
        </el-form-item>
        <el-form-item label="片段文档内容">
          <editor v-model="form.content" :min-height="192"/>
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

<script setup name="TaskFragment" lang="ts">
import { listTaskFragment, getTaskFragment, delTaskFragment, addTaskFragment, updateTaskFragment } from '@/api/doclogic/taskFragment';
import { TaskFragmentVO, TaskFragmentQuery, TaskFragmentForm } from '@/api/doclogic/taskFragment/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const taskFragmentList = ref<TaskFragmentVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const daterangeCreateTime = ref([]);

const queryFormRef = ref(ElForm);
const taskFragmentFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: TaskFragmentForm = {
  lid: undefined,
  docId: undefined,
  fid: undefined,
  fPage: undefined,
  idx: undefined,
  content: undefined,
}
const data = reactive<PageData<TaskFragmentForm, TaskFragmentQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    lid: undefined,
    docId: undefined,
    fid: undefined,
    fPage: undefined,
    idx: undefined,
    createTime: undefined,
    createBy: undefined
  },
  rules: {
    lid: [
      { required: true, message: "逻辑任务不能为空", trigger: "blur" }
    ],
    docId: [
      { required: true, message: "文档不能为空", trigger: "blur" }
    ],
    fid: [
      { required: true, message: "文档片段不能为空", trigger: "blur" }
    ],
    fPage: [
      { required: true, message: "片段所属页码不能为空", trigger: "blur" }
    ],
    idx: [
      { required: true, message: "片段索引下标不能为空", trigger: "blur" }
    ],
    content: [
      { required: true, message: "片段文档内容不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询文档片段列表 */
const getList = async () => {
  loading.value = true;
  queryParams.value.params = {};
  if (null != daterangeCreateTime.value && daterangeCreateTime.value.length === 2) {
    queryParams.value.params["beginCreateTime"] = daterangeCreateTime.value[0];
    queryParams.value.params["endCreateTime"] = daterangeCreateTime.value[1];
  }
  const res = await listTaskFragment(queryParams.value);
  taskFragmentList.value = res.rows;
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
  taskFragmentFormRef.value.resetFields();
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
const handleSelectionChange = (selection: TaskFragmentVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加文档片段";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: TaskFragmentVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改文档片段";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getTaskFragment(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
  });
}

/** 提交按钮 */
const submitForm = () => {
  taskFragmentFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateTaskFragment(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addTaskFragment(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: TaskFragmentVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除文档片段编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delTaskFragment(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('doclogic/taskFragment/export', {
    ...queryParams.value
  }, `taskFragment_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
