<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="名称" prop="name">
            <el-input v-model="queryParams.name" placeholder="请输入提示词名称" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="模型id" prop="modelId">
            <el-select v-model="queryParams.modelId" placeholder="请选择模型id" clearable>
              <el-option v-for="dict in base_model_list" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
          </el-form-item>

          <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
              <el-option v-for="dict in sys_common_status" :key="dict.value" :label="dict.label" :value="dict.value" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['system:promptsStore:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['system:promptsStore:edit']"
              >修改</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['system:promptsStore:remove']"
              >删除</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['system:promptsStore:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="promptsStoreList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="id" align="center" prop="id" v-if="true" />
        <el-table-column label="提示词名称" align="center" prop="name" />
        <el-table-column label="模型" align="center" prop="modelId">
          <template #default="scope">
            <dict-tag :options="base_model_list" :value="scope.row.modelId" />
          </template>
        </el-table-column>
        <el-table-column label="角色设定" align="center" prop="systemRole">
          <template #default="scope">
            <el-tooltip :content="scope.row.systemRole" placement="top" :show-after="500">
              <div class="ellipsis-text">{{ scope.row.systemRole }}</div>
            </el-tooltip>
          </template>
        </el-table-column>
        <!-- <el-table-column label="用户要求" align="center" prop="userPrompts">
          <template #default="scope">
            <el-tooltip :content="scope.row.userPrompts" placement="top" :show-after="500">
              <div class="ellipsis-text">{{ scope.row.userPrompts }}</div>
            </el-tooltip>
          </template>
        </el-table-column> -->
        <el-table-column label="模型类型" align="center" prop="talkCount">
          <template #default="scope">
            <el-tag :type="scope.row.talkCount === 1 ? 'success' : 'info'">
              {{ scope.row.talkCount === 1 ? '付费模型' : '免费模型' }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- <el-table-column label="随机性" align="center" prop="temperature" />
        <el-table-column label="核采样" align="center" prop="topP" />
        <el-table-column label="单次回复限制" align="center" prop="maxTokens" />
        <el-table-column label="话题新鲜度" align="center" prop="presencePenalty" />
        <el-table-column label="频率惩罚度" align="center" prop="frequencyPenalty" /> -->
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="sys_common_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:promptsStore:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:promptsStore:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改提示词商店对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="80%" append-to-body>
      <el-form ref="promptsStoreFormRef" :model="form" :rules="rules" label-width="310px">
        <el-form-item label="提示词名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入提示词名称" />
        </el-form-item>
        <el-form-item label="模型id" prop="modelId">
          <el-select v-model="form.modelId" placeholder="请选择模型id">
            <!-- <el-option v-for="dict in base_model_list" :key="dict.value" :label="dict.label" :value="parseInt(dict.value)"></el-option> -->
            <el-option v-for="dict in base_model_list" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色设定" prop="systemRole">
          <el-input v-model="form.systemRole" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <!-- <el-form-item label="用户要求" prop="userPrompts">
          <el-input v-model="form.userPrompts" type="textarea" :rows="10" placeholder="请输入内容" />
        </el-form-item> -->
        <el-form-item label="模型类型" prop="talkCount">
          <el-select v-model="form.talkCount" placeholder="请选择模型类型">
            <el-option label="付费模型" :value="1" />
            <el-option label="免费模型" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="单次回复限制" prop="maxTokens">
          <template #label>
            <span>
              <span style="font-size: 14px;">单次回复限制</span>
              <span class="desc-label" style="font-weight: normal; font-size: 12px;color: #999;">更多的上下文会使记忆更精确，但会消耗更多的额度</span>
            </span>
          </template>
          <el-input-number :min="4000" v-model="form.maxTokens" maxlength="5" show-word-limit="true" max="12800" placeholder="请输入上下文数量" />
        </el-form-item>
        <el-form-item label="随机性" prop="temperature">
          <template #label>
            <span>
              <span style="font-size: 14px;">随机性</span>
              <span class="desc-label" style="font-weight: normal; font-size: 12px;color: #999;">(temperature)值越大，回复越随机</span>
            </span>
          </template>
          <el-slider v-model="form.temperature" :min="0" :max="1" :step="0.1" :format-tooltip="formatTooltip" />
        </el-form-item>
        <el-form-item label="核采样" prop="topP">
          <template #label>
            <span>
              <span style="font-size: 14px;">核采样</span>
              <span class="desc-label" style="font-weight: normal; font-size: 12px;color: #999;">(top_p)与随机性类似，但不要和随机性一起更改</span>
            </span>
          </template>
          <el-slider v-model="form.topP" :min="0" :max="1" :step="0.1" :format-tooltip="formatTooltip" />
        </el-form-item>
        <el-form-item label="话题新鲜度" prop="presencePenalty">
          <template #label>
            <span>
              <span style="font-size: 14px;">话题新鲜度</span>
              <span class="desc-label" style="font-weight: normal; font-size: 12px;color: #999;">(presence_penalty)值越大，越有可能扩展到新话题</span>
            </span>
          </template>
          <el-slider v-model="form.presencePenalty" :min="0" :max="1" :step="0.1" :format-tooltip="formatTooltip" />
        </el-form-item>
        <el-form-item label="频率惩罚度" prop="frequencyPenalty">
          <template #label>
            <span>
              <span style="font-size: 14px;">频率惩罚度</span>
              <span class="desc-label" style="font-weight: normal; font-size: 12px;color: #999;"
                >(frequency_penalty)值越大，越有可能降低重复字词</span
              >
            </span>
          </template>
          <el-slider v-model="form.frequencyPenalty" :min="0" :max="1" :step="0.1" :format-tooltip="formatTooltip" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in sys_common_status" :key="dict.value" :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
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

<script setup name="PromptsStore" lang="ts">
import { listPromptsStore, getPromptsStore, delPromptsStore, addPromptsStore, updatePromptsStore } from '@/api/system/promptsStore';
import { PromptsStoreVO, PromptsStoreQuery, PromptsStoreForm } from '@/api/system/promptsStore/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_common_status, base_model_list } = toRefs<any>(proxy?.useDict('sys_common_status', 'base_model_list'));

const promptsStoreList = ref<PromptsStoreVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref(ElForm);
const promptsStoreFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: PromptsStoreForm = {
  id: undefined,
  name: undefined,
  modelId: undefined,
  systemRole: undefined,
  userPrompts: undefined,
  talkCount: undefined,
  temperature: undefined,
  topP: undefined,
  maxTokens: undefined,
  presencePenalty: undefined,
  frequencyPenalty: undefined,
  status: "0",
  remark: undefined
}
const data = reactive<PageData<PromptsStoreForm, PromptsStoreQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: undefined,
    modelId: undefined,
    systemRole: undefined,
    userPrompts: undefined,
    talkCount: undefined,
    temperature: undefined,
    topP: undefined,
    maxTokens: undefined,
    presencePenalty: undefined,
    frequencyPenalty: undefined,
    status: "0",
  },
  rules: {
    id: [
      { required: true, message: "id不能为空", trigger: "blur" }
    ],
    name: [
      { required: true, message: "提示词名称不能为空", trigger: "blur" }
    ],
    modelId: [
      { required: true, message: "模型id不能为空", trigger: "change" }
    ],
    systemRole: [
      { required: true, message: "角色设定不能为空", trigger: "blur" }
    ],
   
    talkCount: [
      { required: true, message: "模型类型不能为空", trigger: "change" }
    ],
    temperature: [
      { required: true, message: "随机性不能为空", trigger: "blur" }
    ],
    topP: [
      { required: true, message: "核采样不能为空", trigger: "blur" }
    ],
  
    // presencePenalty: [
    //   { required: true, message: "话题新鲜度不能为空", trigger: "blur" }
    // ],
    // frequencyPenalty: [
    //   { required: true, message: "频率惩罚度不能为空", trigger: "blur" }
    // ],
    status: [
      { required: true, message: "状态不能为空", trigger: "change" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

const formatTooltip = (val: number) => {
  return val.toFixed(1)
}

/** 查询提示词商店列表 */
const getList = async () => {
  loading.value = true;
  const res = await listPromptsStore(queryParams.value);
  promptsStoreList.value = res.rows;
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
  promptsStoreFormRef.value.resetFields();
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
const handleSelectionChange = (selection: PromptsStoreVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加提示词商店";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: PromptsStoreVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改提示词商店";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getPromptsStore(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
  });
}

/** 提交按钮 */
const submitForm = () => {
  promptsStoreFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updatePromptsStore(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addPromptsStore(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: PromptsStoreVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除提示词商店编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delPromptsStore(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('system/promptsStore/export', {
    ...queryParams.value
  }, `promptsStore_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>

<style>
/* 可以通过自定义样式来进一步调整样式 */
.el-form-item__label span {
  display: block;
}
.el-form-item{
  margin-top: 36px;
}
.el-form-item__label span{
  line-height: 16px;
  margin-top: 0px;
  float: right;
  width: 100%;
}

.ellipsis-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px; /* 根据需要调整宽度 */
}
</style>
