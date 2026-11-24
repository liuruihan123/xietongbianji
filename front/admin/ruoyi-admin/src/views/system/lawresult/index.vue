<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="处罚文书编号" prop="Gid">
            <el-input v-model="queryParams.Gid" placeholder="请输入处罚文书编号" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="时间" style="width: 308px">
            <el-date-picker
              v-model="daterangePunishmentDate"
              value-format="YYYY-MM-DD HH:mm:ss"
              type="daterange"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
            />
          </el-form-item>
          <el-form-item label="地点" prop="LawRegional">
            <el-input v-model="queryParams.LawRegional" placeholder="请输入地点" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="引用的法律" prop="AccordingLaw">
            <el-input v-model="queryParams.AccordingLaw" placeholder="请输入引用的法律" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="1具体情节" prop="reasonText">
            <el-input v-model="queryParams.reasonText" placeholder="请输入1具体情节" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="A 预测的结果" prop="aijudgment">
            <el-input v-model="queryParams.aijudgment" placeholder="请输入A 预测的结果" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="真实判决结果" prop="realjudgment">
            <el-input v-model="queryParams.realjudgment" placeholder="请输入真实判决结果" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['system:lawresult:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['system:lawresult:edit']"
              >修改</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['system:lawresult:remove']"
              >删除</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['system:lawresult:export']">导出</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="info" plain icon="VideoPlay" @click="handleStart" v-hasPermi="['system:lawresult:start']">任务开始</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="lawresultList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="编号" align="center" prop="id" v-if="true" />
        <el-table-column label="处罚文书编号" align="center" prop="Gid" />
        <el-table-column label="时间" align="center" prop="PunishmentDate" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.PunishmentDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="地点" align="center" prop="LawRegional" />
        <el-table-column label="处罚类别" align="center" prop="PunishmentType" />
        <el-table-column label="引用的法律" align="center" prop="AccordingLaw" />
        <el-table-column label="1具体情节" align="center" prop="reasonText" />
        <el-table-column label="A 预测的结果" align="center" prop="aijudgment" />
        <el-table-column label="真实判决结果" align="center" prop="realjudgment" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:lawresult:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:lawresult:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改判案结果对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="lawresultFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="处罚文书编号" prop="Gid">
          <el-input v-model="form.Gid" placeholder="请输入处罚文书编号" />
        </el-form-item>
        <el-form-item label="时间" prop="PunishmentDate">
          <el-date-picker clearable v-model="form.PunishmentDate" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="地点" prop="LawRegional">
          <el-input v-model="form.LawRegional" placeholder="请输入地点" />
        </el-form-item>
        <el-form-item label="引用的法律" prop="AccordingLaw">
          <el-input v-model="form.AccordingLaw" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="1具体情节" prop="reasonText">
          <el-input v-model="form.reasonText" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="A 预测的结果" prop="aijudgment">
          <el-input v-model="form.aijudgment" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="真实判决结果" prop="realjudgment">
          <el-input v-model="form.realjudgment" type="textarea" placeholder="请输入内容" />
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

<script setup name="Lawresult" lang="ts">
import { listLawresult, getLawresult, delLawresult, addLawresult, updateLawresult } from '@/api/system/lawresult';
import { LawresultVO, LawresultQuery, LawresultForm } from '@/api/system/lawresult/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';
import request from '@/utils/request';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const lawresultList = ref<LawresultVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const daterangePunishmentDate = ref([]);

const queryFormRef = ref(ElForm);
const lawresultFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: LawresultForm = {
  id: undefined,
  Gid: undefined,
  PunishmentDate: undefined,
  LawRegional: undefined,
  PunishmentType: undefined,
  AccordingLaw: undefined,
  reasonText: undefined,
  aijudgment: undefined,
  realjudgment: undefined
}
const data = reactive<PageData<LawresultForm, LawresultQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    Gid: undefined,
    PunishmentDate: undefined,
    LawRegional: undefined,
    PunishmentType: undefined,
    AccordingLaw: undefined,
    reasonText: undefined,
    aijudgment: undefined,
    realjudgment: undefined
  },
  rules: {
    id: [
      { required: true, message: "编号不能为空", trigger: "blur" }
    ],
    Gid: [
      { required: true, message: "处罚文书编号不能为空", trigger: "blur" }
    ],
    PunishmentDate: [
      { required: true, message: "时间不能为空", trigger: "blur" }
    ],
    LawRegional: [
      { required: true, message: "地点不能为空", trigger: "blur" }
    ],
    PunishmentType: [
      { required: true, message: "处罚类别不能为空", trigger: "change" }
    ],
    AccordingLaw: [
      { required: true, message: "引用的法律不能为空", trigger: "blur" }
    ],
    reasonText: [
      { required: true, message: "1具体情节不能为空", trigger: "blur" }
    ],
    aijudgment: [
      { required: true, message: "A 预测的结果不能为空", trigger: "blur" }
    ],
    realjudgment: [
      { required: true, message: "真实判决结果不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询判案结果列表 */
const getList = async () => {
  loading.value = true;
  queryParams.value.params = {};

  const res = await listLawresult(queryParams.value);
  lawresultList.value = res.rows;
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
  lawresultFormRef.value.resetFields();
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
const resetQuery = () => {
  daterangePunishmentDate.value = [];
  queryFormRef.value.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
const handleSelectionChange = (selection: LawresultVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加判案结果";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: LawresultVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改判案结果";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getLawresult(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
  });
}

/** 提交按钮 */
const submitForm = () => {
  lawresultFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateLawresult(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addLawresult(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: LawresultVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除判案结果编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delLawresult(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('system/lawresult/export', {
    ...queryParams.value
  }, `lawresult_${new Date().getTime()}.xlsx`)
}

/** 任务开始按钮操作 */
const handleStart = async () => {
  try {
    loading.value = true;
    await request.post('/system/lawresult/start');
    proxy?.$modal.msgSuccess("任务启动成功");
  } catch (error) {
    console.error('任务启动失败:', error);
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  getList();
});
</script>
