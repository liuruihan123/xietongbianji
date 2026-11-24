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
          <el-form-item label="错误级别" prop="lvTag">
            <el-select v-model="queryParams.lvTag" placeholder="请选择错误级别" clearable>
              <el-option
                v-for="dict in lv_tag"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['doclogic:taskLog:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['doclogic:taskLog:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['doclogic:taskLog:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['doclogic:taskLog:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="taskLogList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="" align="center" prop="id" v-if="true" />
        <el-table-column label="逻辑任务" align="center" prop="lid" />
        <el-table-column label="文档" align="center" prop="docId" />
        <el-table-column label="文档片段" align="center" prop="fid" />
        <el-table-column label="错误级别" align="center" prop="lvTag">
          <template #default="scope">
            <dict-tag :options="lv_tag" :value="scope.row.lvTag"/>
          </template>
        </el-table-column>
        <el-table-column label="分析内容" align="center" prop="logContent" />
        <el-table-column label="" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['doclogic:taskLog:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['doclogic:taskLog:remove']"></el-button>
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
    <!-- 添加或修改分析日志对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="taskLogFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="逻辑任务" prop="lid">
          <el-input v-model="form.lid" placeholder="请输入逻辑任务" />
        </el-form-item>
        <el-form-item label="文档" prop="docId">
          <el-input v-model="form.docId" placeholder="请输入文档" />
        </el-form-item>
        <el-form-item label="文档片段" prop="fid">
          <el-input v-model="form.fid" placeholder="请输入文档片段" />
        </el-form-item>
        <el-form-item label="错误级别" prop="lvTag">
          <el-select v-model="form.lvTag" placeholder="请选择错误级别">
            <el-option
                v-for="dict in lv_tag"
                :key="dict.value"
                :label="dict.label"
                :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="分析内容">
          <editor v-model="form.logContent" :min-height="192"/>
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

<script setup name="TaskLog" lang="ts">
import { listTaskLog, getTaskLog, delTaskLog, addTaskLog, updateTaskLog } from '@/api/doclogic/taskLog';
import { TaskLogVO, TaskLogQuery, TaskLogForm } from '@/api/doclogic/taskLog/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { lv_tag } = toRefs<any>(proxy?.useDict('lv_tag'));

const taskLogList = ref<TaskLogVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const daterangeCreateTime = ref([]);

const queryFormRef = ref(ElForm);
const taskLogFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: TaskLogForm = {
  id: undefined,
  lid: undefined,
  docId: undefined,
  fid: undefined,
  lvTag: undefined,
  logContent: undefined,
  remark: undefined
}
const data = reactive<PageData<TaskLogForm, TaskLogQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    lid: undefined,
    docId: undefined,
    fid: undefined,
    lvTag: undefined,
    logContent: undefined,
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
    fid: [
      { required: true, message: "文档片段不能为空", trigger: "blur" }
    ],
    lvTag: [
      { required: true, message: "错误级别不能为空", trigger: "change" }
    ],
    logContent: [
      { required: true, message: "分析内容不能为空", trigger: "blur" }
    ],
    remark: [
      { required: true, message: "不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询分析日志列表 */
const getList = async () => {
  loading.value = true;
  queryParams.value.params = {};
  if (null != daterangeCreateTime.value && daterangeCreateTime.value.length === 2) {
    queryParams.value.params["beginCreateTime"] = daterangeCreateTime.value[0];
    queryParams.value.params["endCreateTime"] = daterangeCreateTime.value[1];
  }
  const res = await listTaskLog(queryParams.value);
  taskLogList.value = res.rows;
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
  taskLogFormRef.value.resetFields();
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
const handleSelectionChange = (selection: TaskLogVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加分析日志";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: TaskLogVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改分析日志";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getTaskLog(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
  });
}

/** 提交按钮 */
const submitForm = () => {
  taskLogFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateTaskLog(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addTaskLog(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: TaskLogVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除分析日志编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delTaskLog(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('doclogic/taskLog/export', {
    ...queryParams.value
  }, `taskLog_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
