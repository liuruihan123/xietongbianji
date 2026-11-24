<template>
  <div class="p-2">
    <!-- 添加业务类型侧边栏 -->
    <el-col :span="4" :xs="24">
      <div class="head-container">
        <h5 class="mb-2">业务类型</h5>
        <el-menu default-active="2" class="el-menu-vertical-demo" @open="handleOpen">
          <el-menu-item index="all" @click="selectMenu('')">
            <el-icon><iconMenu /></el-icon>
            <span>全部</span>
          </el-menu-item>
          <el-menu-item v-for="item in bus_type" :key="item.value" :index="item.value+''" @click="selectMenu(item.value)">
            <template #title>
              <el-icon><iconMenu /></el-icon>
              <span>{{item.label}}</span>
            </template>
          </el-menu-item>
        </el-menu>
      </div>
    </el-col>

    <!-- 修改主内容区域宽度 -->
    <el-col :span="20" :xs="24" style="width: 100%">
      <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
        <div class="search" v-show="showSearch">
          <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="100px">
            <el-form-item label="启用知识库" prop="useKnowledge">
              <el-select v-model="queryParams.useKnowledge" placeholder="请选择启用知识库" clearable>
                <el-option v-for="dict in use_status" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <!-- <el-form-item label="知识库" prop="kidIds">
              <el-input v-model="queryParams.kidIds" placeholder="请输入知识库ID(多选)" clearable @keyup.enter="handleQuery" />
            </el-form-item> -->
            <!-- <el-form-item label="业务类型" prop="busType">
              <el-select v-model="queryParams.busType" placeholder="请选择业务类型" clearable>
                <el-option v-for="dict in bus_type" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item> -->

            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
                <el-option v-for="dict in use_status" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <!-- <el-form-item label="创建者" prop="createdBy">
              <el-input v-model="queryParams.createdBy" placeholder="请输入创建者" clearable @keyup.enter="handleQuery" />
            </el-form-item> -->
            <!-- <el-form-item label="缺省字段1" prop="val1">
              <el-input v-model="queryParams.val1" placeholder="请输入缺省字段1" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="缺省字段1" prop="val2">
              <el-input v-model="queryParams.val2" placeholder="请输入缺省字段1" clearable @keyup.enter="handleQuery" />
            </el-form-item> -->
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
              <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['system:conversationStarters:add']">新增</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['system:conversationStarters:edit']"
                >修改</el-button
              >
            </el-col>
            <el-col :span="1.5">
              <el-button
                type="danger"
                plain
                icon="Delete"
                :disabled="multiple"
                @click="handleDelete()"
                v-hasPermi="['system:conversationStarters:remove']"
                >删除</el-button
              >
            </el-col>
            <el-col :span="1.5">
              <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['system:conversationStarters:export']"
                >导出</el-button
              >
            </el-col>
            <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
          </el-row>
        </template>

        <el-table v-loading="loading" :data="conversationStartersList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" align="center" />
          <!-- <el-table-column label="id" align="center" prop="id" v-if="true" /> -->
          <el-table-column label="中文标题" align="center" prop="titleCn" />
          <el-table-column label="英文标题" align="center" prop="titleEn" />
          <!-- <el-table-column label="启用知识库" align="center" prop="useKnowledge">
            <template #default="scope">
              <dict-tag :options="use_status" :value="scope.row.useKnowledge" />
            </template>
          </el-table-column>
          <el-table-column label="知识库(多选)" align="center" prop="kidIds" /> -->
          <el-table-column label="启用案例库" align="center" prop="useKnowledgeAttach">
            <template #default="scope">
              <dict-tag :options="use_status" :value="scope.row.useKnowledgeAttach" />
            </template>
          </el-table-column>
          <el-table-column label="业务类型" align="center" prop="busType">
            <template #default="scope">
              <dict-tag :options="bus_type" :value="scope.row.busType" />
            </template>
          </el-table-column>
          <el-table-column label="关联标签" align="center" prop="tags" />
          <el-table-column label="备注" align="center" prop="remark" />
          <el-table-column label="状态" align="center" prop="status">
            <template #default="scope">
              <dict-tag :options="use_status" :value="scope.row.status" />
            </template>
          </el-table-column>
          <!-- <el-table-column label="创建者" align="center" prop="createdBy" /> -->
          <!-- <el-table-column label="缺省字段1" align="center" prop="val1" />
          <el-table-column label="缺省字段1" align="center" prop="val2" /> -->
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template #default="scope">
              <el-tooltip content="修改" placement="top">
                <el-button
                  link
                  type="primary"
                  icon="Edit"
                  @click="handleUpdate(scope.row)"
                  v-hasPermi="['system:conversationStarters:edit']"
                ></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top">
                <el-button
                  link
                  type="primary"
                  icon="Delete"
                  @click="handleDelete(scope.row)"
                  v-hasPermi="['system:conversationStarters:remove']"
                ></el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>

        <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-card>
      <!-- 添加或修改对话开场对话框 -->
      <el-dialog :title="dialog.title" v-model="dialog.visible" append-to-body>
        <el-form ref="conversationStartersFormRef" :model="form" :rules="rules" label-width="180px">
          <el-form-item label="业务类型" prop="busType">
            <el-select v-model="form.busType" placeholder="请选择业务类型">
              <el-option v-for="dict in bus_type" :key="dict.value" :label="dict.label" :value="parseInt(dict.value)"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="中文标题" prop="titleCn">
            <el-input v-model="form.titleCn" type="textarea" placeholder="请输入内容" />
          </el-form-item>
          <el-form-item label="英文标题" prop="titleEn">
            <el-input v-model="form.titleEn" type="textarea" placeholder="请输入内容" />
          </el-form-item>
          <el-form-item label="本句是否启用知识库" prop="useKnowledge">
            <template #label>
              <div class="label-with-tooltip">
                <span>本句是否启用知识库</span>
                <el-tooltip content="如果使用案例库则建议不使用知识库，多文本情况容易造成混乱" placement="top">
                  <el-icon class="tooltip-icon"><QuestionFilled /></el-icon>
                </el-tooltip>
              </div>
            </template>
            <el-radio-group v-model="form.useKnowledge">
              <el-radio v-for="dict in use_knowledge" :key="dict.value" :label="dict.value">{{dict.label}}</el-radio>
            </el-radio-group>
          </el-form-item>
          <!-- <el-form-item label="关联知识库(多选)" prop="kidIds" v-if="form.useKnowledge == '1'">
            <el-select v-model="selectedKids" multiple placeholder="请选择知识库" style="width: 100%" @change="handleKidsChange">
              <el-option v-for="item in knowledgelist" :key="item.kid" :label="item.kname" :value="item.kid" />
            </el-select>
          </el-form-item> -->

          <el-form-item label="启用案例库" prop="useKnowledgeAttach">
            <template #label>
              <div class="label-with-tooltip">
                <span>启用案例库</span>
                <el-tooltip content="案例库有利于更好引导内容" placement="top">
                  <el-icon class="tooltip-icon"><QuestionFilled /></el-icon>
                </el-tooltip>
              </div>
            </template>
            <el-radio-group v-model="form.useKnowledgeAttach">
              <el-radio v-for="dict in use_status" :key="dict.value" :label="dict.value">{{dict.label}}</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="关联案例库(多选)" prop="knowledgeAttachIds" v-if="form.useKnowledgeAttach == '1'">
            <el-tree-select
              v-model="selectedknowledgeAttachIds"
              :data="qaCaseTreeList"
              multiple
              show-checkbox
              node-key="value"
              placeholder="请选择案例库"
              style="width: 100%"
              @update:model-value="handleKnowledgeAttachChange"
            />
          </el-form-item>

          <el-form-item label="案例库问答模版" prop="templateId" label-width="180px" v-if="form.useKnowledgeAttach == '1'">
            <template #label>
              <div class="label-with-tooltip">
                <span>案例库问答模版</span>
                <el-tooltip content="用于用户提问的二次包装，非必选项" placement="top">
                  <el-icon class="tooltip-icon"><QuestionFilled /></el-icon>
                </el-tooltip>
              </div>
            </template>
            <div class="template-select-wrapper">
              <el-select v-model="form.templateId" placeholder="请选择案例库问答模版" clearable style="width: 500px" @change="handleTemplateChange">
                <el-option v-for="item in qaTemplateList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
              <el-button type="primary" :icon="Setting" @click="handleTemplateConfig">模版引用配置</el-button>
            </div>
          </el-form-item>
          <el-form-item label="是否固定在开头" prop="templateFixed" v-if="form.useKnowledgeAttach == '1'">
            <template #label>
              <div class="label-with-tooltip">
                <span>是否固定在开头</span>
                <el-tooltip content="固定在开头会消耗更多token，不固定则仅用于本次内容的二次包装" placement="top">
                  <el-icon class="tooltip-icon"><QuestionFilled /></el-icon>
                </el-tooltip>
              </div>
            </template>
            <el-radio-group v-model="form.templateFixed">
              <el-radio v-for="dict in template_fixed" :key="dict.value" :label="dict.value">{{dict.label}}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="关联标签" prop="tags">
            <el-input
              v-model="inputTag"
              class="input-new-tag"
              placeholder="请输入标签，回车确认,此处标签用于加强关联知识库案例"
              @keyup.enter="handleInputConfirm"
              @blur="handleInputConfirm"
            />
            <div class="mt-1">
              <el-tag v-for="tag in tagList" :key="tag" class="mx-1" closable :disable-transitions="false" @close="handleClose(tag)">
                {{ tag }}
              </el-tag>
            </div>
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio v-for="dict in use_status" :key="dict.value" :label="dict.value">{{dict.label}}</el-radio>
            </el-radio-group>
          </el-form-item>
          <!-- <el-form-item label="创建者" prop="createdBy">
            <el-input v-model="form.createdBy" placeholder="请输入创建者" />
          </el-form-item> -->
          <!-- <el-form-item label="缺省字段1" prop="val1">
            <el-input v-model="form.val1" placeholder="请输入缺省字段1" />
          </el-form-item>
          <el-form-item label="缺省字段1" prop="val2">
            <el-input v-model="form.val2" placeholder="请输入缺省字段1" />
          </el-form-item> -->
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
            <el-button @click="cancel">取 消</el-button>
          </div>
        </template>
      </el-dialog>
    </el-col>
  </div>
</template>

<script setup name="ConversationStarters" lang="ts">
import { listConversationStarters, getConversationStarters, delConversationStarters, addConversationStarters, updateConversationStarters } from '@/api/system/conversationStarters';
import { ConversationStartersVO, ConversationStartersQuery, ConversationStartersForm } from '@/api/system/conversationStarters/types';
import { listTreeQaCase } from '@/api/system/qaCase';
import { ComponentInternalInstance } from 'vue';
import { ElForm, ElTreeSelect } from 'element-plus';
import { listKnowledge_info } from '@/api/knowledge/knowledge_info';
import { listKnowledgeAttach } from '@/api/knowledge/knowledgeAttach';
import { Knowledge_infoVO } from '@/api/knowledge/knowledge_info/types';
import { KnowledgeAttachVO } from '@/api/knowledge/knowledgeAttach/types';
import { qaCaseVO } from '@/api/system/qaCase/types';
import { Setting, QuestionFilled } from '@element-plus/icons-vue';
import { listQaTemplate } from '@/api/system/qaTemplate';
import { QaTemplateVO } from '@/api/system/qaTemplate/types';
import { checkTemplate } from '@/api/system/modelsetting';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { use_status, bus_type,template_fixed,use_knowledge } = toRefs<any>(proxy?.useDict('use_status', 'bus_type', 'use_status','template_fixed','use_knowledge'));

const conversationStartersList = ref<ConversationStartersVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref(ElForm);
const conversationStartersFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: ConversationStartersForm = {
  id: undefined,
  titleCn: undefined,
  titleEn: undefined,
  useKnowledge: '0',
  useKnowledgeAttach: '1',
  templateFixed: '1',
  kidIds: undefined,
  knowledgeAttachIds: undefined,
  busType: undefined,
  tags: undefined,
  remark: undefined,
  status: "1",
  createdBy: undefined,
  val1: undefined,
  val2: undefined,
  templateId: undefined
}
const data = reactive<PageData<ConversationStartersForm, ConversationStartersQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    useKnowledge: undefined,
    useKnowledgeAttach: undefined,
    templateFixed: undefined,
    kidIds: undefined,
    knowledgeAttachIds: undefined,
    busType: undefined,
    tags: undefined,
    status: undefined,
    createdBy: undefined,
    val1: undefined,
    val2: undefined
  },
  rules: {
    id: [
      { required: true, message: "id不能为空", trigger: "blur" }
    ],
    titleCn: [
      { required: true, message: "中文标题不能为空", trigger: "blur" }
    ],
    titleEn: [
      { required: true, message: "英文标题不能为空", trigger: "blur" }
    ],
    // useKnowledge: [
    //   { required: true, message: "启用知识库不能为空", trigger: "change" }
    // ],
    useKnowledgeAttach: [
      { required: true, message: "启用案例库不能为空", trigger: "change" }
    ],


    busType: [
      { required: true, message: "业务类型不能为空", trigger: "change" }
    ],
    // tags: [
    //   { required: true, message: "关联标签不能为空", trigger: "blur" }
    // ],
    status: [
      { required: true, message: "状态不能为空", trigger: "change" }
    ],
    templateId: [
      {
        required: true,
        validator: (rule: any, value: any, callback: any) => {
          if (form.value.knowledgeAttachIds && !value) {
            callback(new Error('当选择了案例库时，模版为必选项'));
          } else {
            callback();
          }
        },
        trigger: ['change', 'blur']
      }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

// 添加知识库列表数据
const knowledgelist = ref<Knowledge_infoVO[]>([]);
const qaCaseTreeList = ref<qaCaseVO[]>([]);
// 添加标签相关的响应式变量
const inputTag = ref('')
const tagList = ref<string[]>([])

// 添加选中知识库的响应式变量
const selectedKids = ref<(string | number)[]>([])
const selectedknowledgeAttachIds = ref<(string | number)[]>([])

// 添加模版列表相关的响应式变量
const qaTemplateList = ref<QaTemplateVO[]>([])

/** 查询对话开场表 */
const getList = async () => {
  loading.value = true;
  const res = await listConversationStarters(queryParams.value);
  conversationStartersList.value = res.rows;
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
  form.value = {...initFormData}
  selectedKids.value = [] // 重置选中的知识库
  selectedknowledgeAttachIds.value = [] // 重置选中的案例库
  tagList.value = [] // 重置标签列表
  conversationStartersFormRef.value.resetFields()
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
  getKnowledgelist(); // 添加获取知识库列表
  getQaCaseTreeList(); // 添加获取案例库列表
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
const handleSelectionChange = (selection: ConversationStartersVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加对话开场";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: ConversationStartersVO) => {
  loading.value = true;
  dialog.visible = true;
  dialog.title = "修改对话开场";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0];
    const res = await getConversationStarters(_id);
    if (res.data.knowledgeAttachIds) {
      if (typeof res.data.knowledgeAttachIds === 'string') {
        const idsArray = res.data.knowledgeAttachIds.split(',').map(item => item.trim());
        // res.data.knowledgeAttachIds = res.data.knowledgeAttachIds.split(',').map(item => item.trim());

        selectedknowledgeAttachIds.value = idsArray;
      }
    } else {
      res.data.knowledgeAttachIds = [];
    }
    console.log(res.data.knowledgeAttachIds)
    loading.value = false;
    Object.assign(form.value, res.data);

  });
}

/** 提交按钮 */
const submitForm = () => {
  conversationStartersFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true
      // 确保 kidIds 是字符串形式
      if (Array.isArray(form.value.kidIds)) {
        form.value.kidIds = form.value.kidIds.join(',')
      }

      try {
        if (form.value.id) {
          await updateConversationStarters(form.value)
        } else {
          await addConversationStarters(form.value)
        }
        proxy?.$modal.msgSuccess("操作成功")
        dialog.visible = false
        await getList()
      } catch (error) {
        console.error(error)
      } finally {
        buttonLoading.value = false
      }
    }
  })
}

/** 删除按钮操作 */
const handleDelete = async (row?: ConversationStartersVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除对话开场编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delConversationStarters(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('system/conversationStarters/export', {
    ...queryParams.value
  }, `conversationStarters_${new Date().getTime()}.xlsx`)
}

// 添加菜单处理方法
const handleOpen = (key: string, keyPath: string[]) => {
}

// const handleClose = (key: string, keyPath: string[]) => {
// }

// 选择业务类型
const selectMenu = (type: string | number) => {
  queryParams.value.busType = typeof type === 'string' ? Number(type) : type;
  getList();
}

// 添加获取知识库列表方法
const getKnowledgelist = async () => {
  const res = await listKnowledge_info({
    pageNum: 1,
    pageSize: 100
  });
  knowledgelist.value = res.rows;
  console.log(knowledgelist.value)
}

// 修改获取案例库列表方法
const getQaCaseTreeList = async () => {
  try {
    const res = await listTreeQaCase();
    console.log(res)
    qaCaseTreeList.value = Array.isArray(res) ? res : [];
    console.log('案例库树形数据:', qaCaseTreeList.value); // 添加日志查看树形数据
  } catch (error) {
    console.error('获取案例库列表失败:', error);
    qaCaseTreeList.value = [];
  }
}

// 处理标签输入确认
const handleInputConfirm = () => {
  if (inputTag.value) {
    if (!tagList.value.includes(inputTag.value)) {
      tagList.value.push(inputTag.value)
      // 更新form中的tags字段，用逗号分隔
      form.value.tags = tagList.value.join(',')
    }
  }
  inputTag.value = ''
}

// 处理标签删除
const handleClose = (tag: string) => {
  tagList.value = tagList.value.filter((t) => t !== tag)
  // 更新form中的tags字段
  form.value.tags = tagList.value.join(',')
}

// 修改案例库选择变化处理方法
const handleKnowledgeAttachChange = (val: (string | number)[]) => {
  console.log('handleKnowledgeAttachChange 被调用，val:', val); // 这里的 val 是选中的值数组

  // 直接处理 val，因为它应该是数组
  selectedknowledgeAttachIds.value = val; // 更新选中的值
  form.value.knowledgeAttachIds = val.join(','); // 更新表单中的值
}

// 添加获取模版列表方法
const getQaTemplateList = async () => {
  const res = await listQaTemplate({
    pageNum: 1,
    pageSize: 100,
    status: '0',
    roleType: ''
  });
  qaTemplateList.value = res.rows;
}

// 添加模版相关的处理方法
const handleTemplateConfig = () => {
  proxy?.$router.push('/operate/qaTemplate')
}

const handleTemplateChange = async (value: number) => {
  console.log(form.value.useKnowledge)
  if (form.value.useKnowledge === '1' && value) {
    try {
      const res = await checkTemplate(value)
      if (res.code !== 200) {
        form.value.templateId = undefined
        proxy?.$modal.msgError(res.msg || '该模版不符合要求')
      }
    } catch (error) {
      form.value.templateId = undefined
      proxy?.$modal.msgError('该模版不符合要求')
    }
  }
}

onMounted(() => {
  getList();
  getKnowledgelist(); // 添加获取知识库列表
  getQaCaseTreeList(); // 添加获取案例库列表
  getQaTemplateList(); // 添加获取模版列表
});
</script>

<style>
[class*=el-col-]{
  float: left;
  padding-left: 7.5px;
  padding-right: 7.5px;
}

.head-container {
  padding: 10px;
  background-color: #fff;
  border-radius: 4px;
  margin-bottom: 20px;
}

.input-new-tag {
  width: 100%;
  margin-bottom: 10px;
}

.el-tag {
  margin-right: 10px;
  margin-bottom: 10px;
}

.template-select-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

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
