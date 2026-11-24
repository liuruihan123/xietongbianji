<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="逻辑任务" prop="lid">
            <el-input v-model="queryParams.lid" placeholder="请输入逻辑任务" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="用户" prop="uid">
            <el-input v-model="queryParams.uid" placeholder="请输入用户" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="逻辑任务名称" prop="lname">
            <el-input v-model="queryParams.lname" placeholder="请输入逻辑任务名称" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="总字数" prop="totalWords">
            <el-input v-model="queryParams.totalWords" placeholder="请输入总字数" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="片段字数" prop="segmentSize">
            <el-input v-model="queryParams.segmentSize" placeholder="请输入片段字数" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="描述" prop="description">
            <el-input v-model="queryParams.description" placeholder="请输入描述" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="任务类型id" prop="typeId">
            <el-input v-model="queryParams.typeId" placeholder="请输入任务类型id" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="菜单状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="请选择菜单状态" clearable>
              <el-option
                v-for="dict in task_status"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="状态说明" prop="taskInfo">
            <el-input v-model="queryParams.taskInfo" placeholder="请输入状态说明" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['doclogic:taskInfo:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['doclogic:taskInfo:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['doclogic:taskInfo:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['doclogic:taskInfo:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="taskInfoList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="" align="center" prop="id" v-if="true" />
        <el-table-column label="逻辑任务" align="center" prop="lid" />
        <el-table-column label="用户" align="center" prop="uid" />
        <el-table-column label="逻辑任务名称" align="center" prop="lname" />
        <el-table-column label="总字数" align="center" prop="totalWords" />
        <el-table-column label="片段字数" align="center" prop="segmentSize" />
        <el-table-column label="描述" align="center" prop="description" />
        <el-table-column label="任务类型id" align="center" prop="typeId" />
        <el-table-column label="菜单状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="task_status" :value="scope.row.status"/>
          </template>
        </el-table-column>
        <el-table-column label="状态说明" align="center" prop="taskInfo" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['doclogic:taskInfo:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['doclogic:taskInfo:remove']"></el-button>
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
    <!-- 添加或修改逻辑任务对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="taskInfoFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="逻辑任务" prop="lid">
          <el-input v-model="form.lid" placeholder="请输入逻辑任务" />
        </el-form-item>
        <el-form-item label="用户" prop="uid">
          <el-input v-model="form.uid" placeholder="请输入用户" />
        </el-form-item>
        <el-form-item label="逻辑任务名称" prop="lname">
          <el-input v-model="form.lname" placeholder="请输入逻辑任务名称" />
        </el-form-item>
        <el-form-item label="总字数" prop="totalWords">
          <el-input v-model="form.totalWords" placeholder="请输入总字数" />
        </el-form-item>
        <el-form-item label="片段字数" prop="segmentSize">
          <el-input v-model="form.segmentSize" placeholder="请输入片段字数" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
            <el-input v-model="form.description" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="任务类型id" prop="typeId">
          <el-input v-model="form.typeId" placeholder="请输入任务类型id" />
        </el-form-item>
        <el-form-item label="菜单状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
                v-for="dict in task_status"
                :key="dict.value"
                :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态说明" prop="taskInfo">
            <el-input v-model="form.taskInfo" type="textarea" placeholder="请输入内容" />
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

<script setup name="TaskInfo" lang="ts">
import { listTaskInfo, getTaskInfo, delTaskInfo, addTaskInfo, updateTaskInfo } from '@/api/doclogic/taskInfo';
import { TaskInfoVO, TaskInfoQuery, TaskInfoForm } from '@/api/doclogic/taskInfo/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { task_status } = toRefs<any>(proxy?.useDict('task_status'));

const taskInfoList = ref<TaskInfoVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref(ElForm);
const taskInfoFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: TaskInfoForm = {
  id: undefined,
  lid: undefined,
  uid: undefined,
  lname: undefined,
  totalWords: undefined,
  segmentSize: undefined,
  description: undefined,
  typeId: undefined,
  status: undefined,
  taskInfo: undefined
}
const data = reactive<PageData<TaskInfoForm, TaskInfoQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    lid: undefined,
    uid: undefined,
    lname: undefined,
    totalWords: undefined,
    segmentSize: undefined,
    description: undefined,
    typeId: undefined,
    status: undefined,
    taskInfo: undefined
  },
  rules: {
    id: [
      { required: true, message: "不能为空", trigger: "blur" }
    ],
    lid: [
      { required: true, message: "逻辑任务不能为空", trigger: "blur" }
    ],
    uid: [
      { required: true, message: "用户不能为空", trigger: "blur" }
    ],
    lname: [
      { required: true, message: "逻辑任务名称不能为空", trigger: "blur" }
    ],
    segmentSize: [
      { required: true, message: "片段字数不能为空", trigger: "blur" }
    ],
    typeId: [
      { required: true, message: "任务类型id不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "菜单状态不能为空", trigger: "change" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询逻辑任务列表 */
const getList = async () => {
  loading.value = true;
  const res = await listTaskInfo(queryParams.value);
  taskInfoList.value = res.rows;
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
  taskInfoFormRef.value.resetFields();
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
const handleSelectionChange = (selection: TaskInfoVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加逻辑任务";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: TaskInfoVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改逻辑任务";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getTaskInfo(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
  });
}

/** 提交按钮 */
const submitForm = () => {
  taskInfoFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateTaskInfo(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addTaskInfo(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: TaskInfoVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除逻辑任务编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delTaskInfo(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('doclogic/taskInfo/export', {
    ...queryParams.value
  }, `taskInfo_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
