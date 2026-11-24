<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="业务类型" prop="busType">
            <el-select v-model="queryParams.busType" placeholder="请选择业务类型" clearable>
              <el-option v-for="dict in bus_type" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="名称" prop="name">
            <el-input v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['system:modelsetting:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['system:modelsetting:edit']"
              >修改</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['system:modelsetting:remove']"
              >删除</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['system:modelsetting:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="modelsettingList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <!--        <el-table-column label="id" align="center" prop="id" v-if="true" />-->
        <el-table-column label="业务类型" align="center" prop="busType">
          <template #default="scope">
            <dict-tag :options="bus_type" :value="scope.row.busType" />
          </template>
        </el-table-column>
        <el-table-column label="名称" align="center" prop="name" />
        <!--        <el-table-column label="知识库ID" align="center" prop="kid" />-->
        <el-table-column label="启用知识库" align="center" prop="useKnowledge">
          <template #default="scope">
            <dict-tag :options="use_knowledge" :value="scope.row.useKnowledge" />
          </template>
        </el-table-column>

        <el-table-column label="所用模型" align="center" prop="modelId">
          <template #default="scope">
            <span>{{scope.row.sysModel.modelDescribe}} @ {{scope.row.sysModel.modelName}} </span>
          </template>
        </el-table-column>

        <!--        <el-table-column label="角色设定" align="center" prop="systemRole" />-->
        <!--        <el-table-column label="上下文数量" align="center" prop="talkCount" />-->
        <el-table-column label="语音人物" align="center" prop="ttsVoice">
          <template #default="scope">
            <dict-tag :options="tts_voice" :value="scope.row.ttsVoice" />
          </template>
        </el-table-column>
        <!--        <el-table-column label="随机性" align="center" prop="temperature" />-->
        <!--        <el-table-column label="核采样" align="center" prop="topP" />-->
        <!--        <el-table-column label="单次回复限制" align="center" prop="maxTokens" />-->
        <!--        <el-table-column label="话题新鲜度" align="center" prop="presencePenalty" />-->
        <!--        <el-table-column label="频率惩罚度" align="center" prop="frequencyPenalty" />-->
        <!--        <el-table-column label="状态" align="center" prop="status" />-->
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:modelsetting:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:modelsetting:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改功能参数对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="900px" append-to-body>
      <el-form ref="modelsettingFormRef" :model="form" :rules="rules" label-width="200">
        <el-form-item label="名称" prop="name" label-width="100px">
          <el-input v-model="form.name" placeholder="输入配置名称" />
        </el-form-item>

        <el-form-item label="业务类型" prop="busType" label-width="100px">
          <el-radio-group v-model="form.busType">
            <el-radio v-for="dict in bus_type" :key="dict.value" :label="parseInt(dict.value)">{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="使用模型" prop="modelId" label-width="100px">
          <el-select v-model="form.modelId" placeholder="Select" size="large" style="width: 240px">
            <el-option v-for="item in modellist" :key="item.id" :label="item.modelDescribe" :value="item.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="启用知识库" prop="useKnowledge" label-width="100px">
          <el-radio-group v-model="form.useKnowledge">
            <el-radio v-for="dict in use_knowledge" :key="dict.value" :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="选择知识库" prop="kid" label-width="100px" v-if="form.useKnowledge == '0'">
          <el-select v-model="form.kid" multiple placeholder="请选择知识库" size="large" style="width: 600px">
            <el-option v-for="item in knowledgelist" :key="item.kid" :label="item.kname" :value="item.kid" />
          </el-select>
        </el-form-item>

        <el-form-item label="触发知识库字数" prop="shortNoOpen" label-width="100px" v-if="form.useKnowledge == '0'">
          <template #label>
            <div class="label-with-tooltip">
              <span>触发字数</span>
              <el-tooltip content="用户问题达到多少字符以上开启知识库以及问答模版，0为不限制。主要用于过滤用户简单语气词。" placement="top">
                <el-icon class="tooltip-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <el-input-number :min="0" v-model="form.shortNoOpen" placeholder="请输入触发字数" />
        </el-form-item>

        <el-form-item label="问答模版" prop="standardTemplateId" label-width="100px">
          <template #label>
            <div class="label-with-tooltip">
              <span>问答模版</span>
              <el-tooltip content="用于用户提问的二次包装，非必选项" placement="top">
                <el-icon class="tooltip-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <div class="template-select-wrapper">
            <el-select v-model="form.standardTemplateId" placeholder="请选择问答模版" clearable style="width: 500px" @change="handleTemplateChange">
              <el-option v-for="item in qaTemplateList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
            <el-button type="primary" :icon="Setting" @click="handleTemplateConfig">模版引用配置</el-button>
          </div>
        </el-form-item>

        <el-form-item label="角色设定" prop="systemRole" label-width="100px">
          <el-input v-model="form.systemRole" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="上下文数量" prop="talkCount" label-width="100px">
          <el-input-number :min="1" v-model="form.talkCount" maxlength="5" show-word-limit="true" max="50" placeholder="请输入上下文数量" />
        </el-form-item>

        <el-form-item label="语音人物" prop="ttsVoice" label-width="100px">
          <el-radio-group v-model="form.ttsVoice">
            <el-radio v-for="dict in tts_voice" :key="dict.value" :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="播放速度" prop="ttsSpeed" label-width="100px">
          <template #label>
            <div class="label-with-tooltip">
              <span>播放速度</span>
              <el-tooltip content="语音播放的速度默认是1，取值范围0.25——4.0" placement="top">
                <el-icon class="tooltip-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <el-slider v-model="form.ttsSpeed" :min="0.25" :max="4" :step="0.25" :format-tooltip="formatTooltip" />
        </el-form-item>
        <el-form-item label="单次回复限制" prop="maxTokens" label-width="100px">
          <template #label>
            <div class="label-with-tooltip">
              <span>单次回复限制</span>
              <el-tooltip content="用于限制生成文本的最大长度" placement="top">
                <el-icon class="tooltip-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <el-input-number :min="4000" v-model="form.maxTokens" maxlength="5" show-word-limit="true" max="12800" placeholder="请输入上下文数量" />
        </el-form-item>
        <el-form-item label="随机性" prop="temperature">
          <template #label>
            <div class="label-with-tooltip">
              <span>随机性</span>
              <el-tooltip content="(temperature)值越大，回复越随机" placement="top">
                <el-icon class="tooltip-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <el-slider v-model="form.temperature" :min="0" :max="1" :step="0.1" :format-tooltip="formatTooltip" />
        </el-form-item>

        <el-form-item label="核采样" prop="topP">
          <template #label>
            <div class="label-with-tooltip">
              <span>核采样</span>
              <el-tooltip content="(top_p)与随机性类似，但不要和随机性一起更改" placement="top">
                <el-icon class="tooltip-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <el-slider v-model="form.topP" :min="0" :max="1" :step="0.1" :format-tooltip="formatTooltip" />
        </el-form-item>
        <el-form-item label="话题新鲜度" prop="presencePenalty">
          <template #label>
            <div class="label-with-tooltip">
              <span>话题新鲜度</span>
              <el-tooltip content="(presence_penalty)值越大，越有可能扩展到新话题" placement="top">
                <el-icon class="tooltip-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <el-slider v-model="form.presencePenalty" :min="0" :max="1" :step="0.1" :format-tooltip="formatTooltip" />
        </el-form-item>

        <el-form-item label="频率惩罚度" prop="frequencyPenalty">
          <template #label>
            <div class="label-with-tooltip">
              <span>频率惩罚度</span>
              <el-tooltip content="(frequency_penalty)值越大，越有可能降低重复字词" placement="top">
                <el-icon class="tooltip-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <el-slider v-model="form.frequencyPenalty" :min="0" :max="1" :step="0.1" :format-tooltip="formatTooltip" />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="配置状态" clearable>
            <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
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

<script setup name="Modelsetting" lang="ts">
import { listModelsetting, getModelsetting, delModelsetting, addModelsetting, updateModelsetting,checkTemplate } from '@/api/system/modelsetting';
import { addModel, delModel, getModel, listModel, updateModel} from '@/api/model/index';
import { listKnowledge_info, getKnowledge_info} from '@/api/knowledge/knowledge_info';
import { listQaTemplate } from '@/api/system/qaTemplate';
import { QaTemplateVO } from '@/api/system/qaTemplate/types';

import { ModelsettingVO, ModelsettingQuery, ModelsettingForm } from '@/api/system/modelsetting/types';
import { Knowledge_infoVO, Knowledge_infoQuery, Knowledge_infoForm,KnowledgeDelReq } from '@/api/knowledge/knowledge_info/types';
import { ModelForm, ModelQuery, ModelNameVO } from '@/api/model/types';



import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';
import {TypeQuery, TypeVO} from "@/api/system/type/types";
import {listKnowledge_type} from "@/api/knowledge/knowledge_info";
import { Setting, QuestionFilled } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { bus_type, tts_voice, use_knowledge } = toRefs<any>(proxy?.useDict('bus_type', 'tts_voice', 'use_knowledge'));

const modelsettingList = ref<ModelsettingVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const { sys_normal_disable } = toRefs<any>(proxy?.useDict('sys_normal_disable'));

const modellist=ref<ModelNameVO[]>([]);
const knowledgelist=ref<Knowledge_infoVO[]>([]);

const queryFormRef = ref(ElForm);
const modelsettingFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: ModelsettingForm = {
  id: undefined,
  kid: undefined,
  useKnowledge: "1",
  busType: 0,
  modelId: undefined,
  name: undefined,
  systemRole: undefined,
  talkCount: undefined,
  ttsVoice: "alloy",
  ttsSpeed: 1,
  shortNoOpen: 0,
  standardTemplateId: undefined,
  temperature: undefined,
  topP: undefined,
  maxTokens: undefined,
  presencePenalty: undefined,
  frequencyPenalty: undefined,
  status: "0",
  remark: undefined
}
const data = reactive<PageData<ModelsettingForm, ModelsettingQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    kid: undefined,
    useKnowledge: undefined,
    busType: undefined,
    modelId: undefined,
    name: undefined,
    systemRole: undefined,
    talkCount: undefined,
    ttsVoice: undefined,
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
    // kid: [
    //   { required: true, message: "知识库ID不能为空", trigger: "change" }
    // ],
    useKnowledge: [
      { required: true, message: "启用知识库不能为空", trigger: "change" }
    ],
    busType: [
      { required: true, message: "业务类型不能为空", trigger: "change" }
    ],
    modelId: [
      { required: true, message: "模型id不能为空", trigger: "change" }
    ],
    name: [
      { required: true, message: "名称不能为空", trigger: "blur" }
    ],
    ttsVoice: [
      { required: true, message: "语音人物不能为空", trigger: "change" }
    ],
    // status: [
    //   { required: true, message: "状态不能为空", trigger: "change" }
    // ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询功能参数列表 */
const getList = async () => {
  loading.value = true;
  const res = await listModelsetting(queryParams.value);
  modelsettingList.value = res.rows;
  total.value = res.total;
  loading.value = false;
}
const formatTooltip = (val: number) => {
  return val.toFixed(1)
}
/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
}

/** 表单重置 */
const reset = () => {
  form.value = {...initFormData};
  modelsettingFormRef.value.resetFields();
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
  getModelList();
  getKnowledgelist();
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
const handleSelectionChange = (selection: ModelsettingVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
const modelQuery: ModelQuery = {
  pageNum: 1,
  pageSize: 100,
  modelName: undefined,
  modelDescribe: undefined,
  modelPrice: undefined,
  modelType: undefined,
};
const knowledgeParams:Knowledge_infoQuery = {
    pageNum: 1,
    pageSize: 100,
    typeId:undefined,
    kname: undefined,
    description: undefined,
    createTime: undefined,
};

const getModelList = async ()=>{
  loading.value = true;
  // modelQuery.status='0';
  const res = await listModel(modelQuery);
  modellist.value = res.rows;
}

const getKnowledgelist = async ()=>{
  loading.value = true;
  // modelQuery.status='0';
  const res = await listKnowledge_info(knowledgeParams);
  knowledgelist.value = res.rows;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加功能参数";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: ModelsettingVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改功能参数";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const res = await getModelsetting(_id);
    loading.value = false;

    if (res.data.kid) {
      if (typeof res.data.kid === 'string') {
        (res.data.kid as any) = res.data.kid.split(',').map(item => item.trim());
      }
    } else {
      (res.data.kid as any) = [];
    }

    console.log(res.data.kid)
    Object.assign(form.value, res.data);
  });
}

/** 提交按钮 */
const submitForm = () => {
  modelsettingFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;

      const submitData = { ...form.value };
      if (Array.isArray(submitData.kid) && submitData.kid.length > 0) {
        submitData.kid = submitData.kid.join(',');
      } else {
        submitData.kid = '';
      }

      if (form.value.id) {
        await updateModelsetting(submitData).finally(() => buttonLoading.value = false);
      } else {
        await addModelsetting(submitData).finally(() => buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;

      await getList();
      await getModelList();
      await getKnowledgelist();
        loading.value = false;
      console.log(loading.value)
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: ModelsettingVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除功能参数编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delModelsetting(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
  getModelList();
  getKnowledgelist();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('system/modelsetting/export', {
    ...queryParams.value
  }, `modelsetting_${new Date().getTime()}.xlsx`)
}

const qaTemplateList = ref<QaTemplateVO[]>([]);

const getQaTemplateList = async () => {
  const res = await listQaTemplate({
    pageNum: 1,
    pageSize: 100,
    status: '0',
    roleType: ''
  });
  qaTemplateList.value = res.rows;
}

const handleTemplateConfig = () => {
  proxy?.$router.push('/operate/qaTemplate');
}

const handleTemplateChange = async (value: number) => {
  if (form.value.useKnowledge === '1' && value) {
    try {
      const res = await checkTemplate(value);
      if (res.code !== 200) {
        // 如果验证失败，清空选择并显示错误信息
        form.value.standardTemplateId = undefined;
        proxy?.$modal.msgError(res.msg || '该模版不符合要求');
      }
    } catch (error) {
      form.value.standardTemplateId = undefined;
      proxy?.$modal.msgError('该模版不符合要求');
    }
  }
}

onMounted(() => {
  getList();
  getModelList();
  getKnowledgelist();
  getQaTemplateList();
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
.template-select-wrapper {
  display: flex;
  align-items: center;
  gap: 12px; /* 设置按钮和选择框之间的间距 */
}

/* 如果需要调整按钮样式可以添加以下内容 */
.template-select-wrapper .el-button {
  margin-left: 12px;
}

.label-with-tooltip {
  display: flex;
  align-items: center;
  gap: 4px;
}

.tooltip-icon {
  font-size: 14px;
  color: #909399;
  cursor: help;
}
</style>
