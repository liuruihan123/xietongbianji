<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="法规目录" prop="library">
            <el-input v-model="queryParams.library" placeholder="请输入法规目录" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="Gid" prop="Gid">
            <el-input v-model="queryParams.Gid" placeholder="请输入Gid" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="标题" prop="Title">
            <el-input v-model="queryParams.Title" placeholder="请输入标题" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="发布部门" prop="IssueDepartment">
            <el-input v-model="queryParams.IssueDepartment" placeholder="请输入发布部门" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="批准部门" prop="RatifyDepartment">
            <el-input v-model="queryParams.RatifyDepartment" placeholder="请输入批准部门" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="发文字号" prop="DocumentNO">
            <el-input v-model="queryParams.DocumentNO" placeholder="请输入发文字号" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="批准日期" style="width: 308px">
            <el-date-picker
              v-model="daterangeRatifyDate"
              value-format="YYYY-MM-DD HH:mm:ss"
              type="daterange"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
            />
          </el-form-item>
          <el-form-item label="发布日期" style="width: 308px">
            <el-date-picker
              v-model="daterangeIssueDate"
              value-format="YYYY-MM-DD HH:mm:ss"
              type="daterange"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
            />
          </el-form-item>
          <el-form-item label="实施日期" style="width: 308px">
            <el-date-picker
              v-model="daterangeImplementDate"
              value-format="YYYY-MM-DD HH:mm:ss"
              type="daterange"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
            />
          </el-form-item>
          <el-form-item label="时效性" prop="TimelinessDic">
            <el-input v-model="queryParams.TimelinessDic" placeholder="请输入时效性" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="失效依据" prop="InvalidBasis">
            <el-input v-model="queryParams.InvalidBasis" placeholder="请输入失效依据" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="效力级别" prop="EffectivenessDic">
            <el-input v-model="queryParams.EffectivenessDic" placeholder="请输入效力级别" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="法规类别" prop="Category">
            <el-input v-model="queryParams.Category" placeholder="请输入法规类别" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="关键词" prop="Keywords">
            <el-input v-model="queryParams.Keywords" placeholder="请输入关键词" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="正文" prop="AllText">
            <el-input v-model="queryParams.AllText" placeholder="请输入正文" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['system:lawList:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['system:lawList:edit']"
              >修改</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['system:lawList:remove']"
              >删除</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['system:lawList:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="lawListList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="" align="center" prop="id" v-if="true" />
        <el-table-column label="法规目录" align="center" prop="library" />
        <el-table-column label="Gid" align="center" prop="Gid" />
        <el-table-column label="标题" align="center" prop="Title" />
        <el-table-column label="发布部门" align="center" prop="IssueDepartment" />
        <el-table-column label="批准部门" align="center" prop="RatifyDepartment" />
        <el-table-column label="发文字号" align="center" prop="DocumentNO" />
        <el-table-column label="批准日期" align="center" prop="RatifyDate" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.RatifyDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="发布日期" align="center" prop="IssueDate" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.IssueDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="实施日期" align="center" prop="ImplementDate" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.ImplementDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="时效性" align="center" prop="TimelinessDic" />
        <el-table-column label="失效依据" align="center" prop="InvalidBasis" />
        <el-table-column label="效力级别" align="center" prop="EffectivenessDic" />
        <el-table-column label="法规类别" align="center" prop="Category" />
        <el-table-column label="关键词" align="center" prop="Keywords" />
        <el-table-column label="专题分类" align="center" prop="TopicType" />
        <el-table-column label="正文" align="center" prop="AllText" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:lawList:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:lawList:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改法律条文对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="lawListFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="法规目录" prop="library">
          <el-input v-model="form.library" placeholder="请输入法规目录" />
        </el-form-item>
        <el-form-item label="Gid" prop="Gid">
          <el-input v-model="form.Gid" placeholder="请输入Gid" />
        </el-form-item>
        <el-form-item label="标题" prop="Title">
          <el-input v-model="form.Title" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="发布部门" prop="IssueDepartment">
          <el-input v-model="form.IssueDepartment" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="批准部门" prop="RatifyDepartment">
          <el-input v-model="form.RatifyDepartment" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="发文字号" prop="DocumentNO">
          <el-input v-model="form.DocumentNO" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="批准日期" prop="RatifyDate">
          <el-date-picker clearable v-model="form.RatifyDate" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择批准日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="发布日期" prop="IssueDate">
          <el-date-picker clearable v-model="form.IssueDate" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择发布日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="实施日期" prop="ImplementDate">
          <el-date-picker clearable v-model="form.ImplementDate" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择实施日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="时效性" prop="TimelinessDic">
          <el-input v-model="form.TimelinessDic" placeholder="请输入时效性" />
        </el-form-item>
        <el-form-item label="失效依据" prop="InvalidBasis">
          <el-input v-model="form.InvalidBasis" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="效力级别" prop="EffectivenessDic">
          <el-input v-model="form.EffectivenessDic" placeholder="请输入效力级别" />
        </el-form-item>
        <el-form-item label="法规类别" prop="Category">
          <el-input v-model="form.Category" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="关键词" prop="Keywords">
          <el-input v-model="form.Keywords" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="正文" prop="AllText">
          <el-input v-model="form.AllText" placeholder="请输入正文" />
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

<script setup name="LawList" lang="ts">
import { listLawList, getLawList, delLawList, addLawList, updateLawList } from '@/api/system/lawList';
import { LawListVO, LawListQuery, LawListForm } from '@/api/system/lawList/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const lawListList = ref<LawListVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const daterangeRatifyDate = ref([]);
const daterangeIssueDate = ref([]);
const daterangeImplementDate = ref([]);

const queryFormRef = ref(ElForm);
const lawListFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: LawListForm = {
  id: undefined,
  library: undefined,
  Gid: undefined,
  Title: undefined,
  IssueDepartment: undefined,
  RatifyDepartment: undefined,
  DocumentNO: undefined,
  RatifyDate: undefined,
  IssueDate: undefined,
  ImplementDate: undefined,
  TimelinessDic: undefined,
  InvalidBasis: undefined,
  EffectivenessDic: undefined,
  Category: undefined,
  Keywords: undefined,
  TopicType: undefined,
  AllText: undefined
}
const data = reactive<PageData<LawListForm, LawListQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    library: undefined,
    Gid: undefined,
    Title: undefined,
    IssueDepartment: undefined,
    RatifyDepartment: undefined,
    DocumentNO: undefined,
    RatifyDate: undefined,
    IssueDate: undefined,
    ImplementDate: undefined,
    TimelinessDic: undefined,
    InvalidBasis: undefined,
    EffectivenessDic: undefined,
    Category: undefined,
    Keywords: undefined,
    TopicType: undefined,
    AllText: undefined
  },
  rules: {
    id: [
      { required: true, message: "不能为空", trigger: "blur" }
    ],
    library: [
      { required: true, message: "法规目录不能为空", trigger: "blur" }
    ],
    Gid: [
      { required: true, message: "Gid不能为空", trigger: "blur" }
    ],
    Title: [
      { required: true, message: "标题不能为空", trigger: "blur" }
    ],
    IssueDepartment: [
      { required: true, message: "发布部门不能为空", trigger: "blur" }
    ],
    RatifyDepartment: [
      { required: true, message: "批准部门不能为空", trigger: "blur" }
    ],
    DocumentNO: [
      { required: true, message: "发文字号不能为空", trigger: "blur" }
    ],
    RatifyDate: [
      { required: true, message: "批准日期不能为空", trigger: "blur" }
    ],
    IssueDate: [
      { required: true, message: "发布日期不能为空", trigger: "blur" }
    ],
    ImplementDate: [
      { required: true, message: "实施日期不能为空", trigger: "blur" }
    ],
    TimelinessDic: [
      { required: true, message: "时效性不能为空", trigger: "blur" }
    ],
    InvalidBasis: [
      { required: true, message: "失效依据不能为空", trigger: "blur" }
    ],
    EffectivenessDic: [
      { required: true, message: "效力级别不能为空", trigger: "blur" }
    ],
    Category: [
      { required: true, message: "法规类别不能为空", trigger: "blur" }
    ],
    Keywords: [
      { required: true, message: "关键词不能为空", trigger: "blur" }
    ],
    TopicType: [
      { required: true, message: "专题分类不能为空", trigger: "change" }
    ],
    AllText: [
      { required: true, message: "正文不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询法律条文列表 */
const getList = async () => {
  loading.value = true;
  queryParams.value.params = {};
  if (daterangeRatifyDate.value?.length) {
    queryParams.value.params["beginRatifyDate"] = daterangeRatifyDate.value[0];
    queryParams.value.params["endRatifyDate"] = daterangeRatifyDate.value[1];
  }
  if (daterangeIssueDate.value?.length) {
    queryParams.value.params["beginIssueDate"] = daterangeIssueDate.value[0];
    queryParams.value.params["endIssueDate"] = daterangeIssueDate.value[1];
  }
  if (daterangeImplementDate.value?.length) {
    queryParams.value.params["beginImplementDate"] = daterangeImplementDate.value[0];
    queryParams.value.params["endImplementDate"] = daterangeImplementDate.value[1];
  }
  const res = await listLawList(queryParams.value);
  lawListList.value = res.rows;
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
  lawListFormRef.value.resetFields();
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
const resetQuery = () => {
  daterangeRatifyDate.value = [];
  daterangeIssueDate.value = [];
  daterangeImplementDate.value = [];
  queryFormRef.value.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
const handleSelectionChange = (selection: LawListVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加法律条文";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: LawListVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改法律条文";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getLawList(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
  });
}

/** 提交按钮 */
const submitForm = () => {
  lawListFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateLawList(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addLawList(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: LawListVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除法律条文编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delLawList(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('system/lawList/export', {
    ...queryParams.value
  }, `lawList_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
