<template>
  <div class="p-2">
    <!--分类数据-->
    <el-col :span="4" :xs="24">
      <div class="head-container"></div>
      <div class="head-container">
        <h5 class="mb-2">知识类型</h5>
        <el-menu default-active="2" class="el-menu-vertical-demo" @open="handleOpen" @close="handleClose">
          <!--          <el-menu-item index="2" @click="selectMenu">-->
          <!--            <el-icon><icon-menu /></el-icon>-->
          <!--            <span>Navigator Two</span>-->
          <!--          </el-menu-item>-->

          <el-menu-item :index="subItem.id+''" v-for="subItem in menulist" :key="subItem.id" @click="selectMenu(subItem.id)">
            <template #title>
              <el-icon><iconMenu /></el-icon>
              <span>{{subItem.name}}</span>
            </template>
          </el-menu-item>
        </el-menu>
      </div>
    </el-col>

    <el-col :span="20" :xs="24">
      <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
        <div class="search" v-show="showSearch">
          <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
            <el-form-item label="名称" prop="kname">
              <el-input v-model="queryParams.kname" placeholder="请输入知识库名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="描述" prop="description">
              <el-input v-model="queryParams.description" placeholder="请输入描述" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="创建时间" style="width: 308px">
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
            <!--          <el-col :span="1.5">-->
            <!--            <el-button type="primary" plain icon="Plus" @click="handleKnownStepAdd" v-hasPermi="['knowledge:knowledge_info:add']">新建知识库</el-button>-->
            <!--          </el-col>-->

            <el-col :span="1.5">
              <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['knowledge:knowledge_info:add']">新增</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['knowledge:knowledge_info:edit']"
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
                v-hasPermi="['knowledge:knowledge_info:remove']"
                >删除</el-button
              >
            </el-col>
            <el-col :span="1.5">
              <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['knowledge:knowledge_info:export']">导出</el-button>
            </el-col>
            <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
          </el-row>
        </template>

        <el-table v-loading="loading" :data="knowledge_infoList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="" align="center" prop="id" v-if="false" />
          <el-table-column label="知识库名称" align="center" prop="kname" />
          <el-table-column label="知识库分类" align="center" prop="knowledgeType.name" />
          <el-table-column label="描述" align="center" prop="description" />
          <el-table-column label="创建时间" align="center" prop="createTime" width="180">
            <template #default="scope">
              <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template #default="scope">
              <el-tooltip content="详情" placement="top">
                <el-button link type="primary" icon="Files" @click="handleAttach(scope.row)"></el-button>
              </el-tooltip>

              <el-tooltip content="修改" placement="top">
                <el-button
                  link
                  type="primary"
                  icon="Edit"
                  @click="handleUpdate(scope.row)"
                  v-hasPermi="['knowledge:knowledge_info:edit']"
                ></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top">
                <el-button
                  link
                  type="primary"
                  icon="Delete"
                  @click="handleDelete(scope.row)"
                  v-hasPermi="['knowledge:knowledge_info:remove']"
                ></el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>

        <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-card>

      <!--      :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body-->
      <el-dialog v-model="dialogVisible.visible" :title="dialogVisible.title" width="70%" :before-close="handleStepClose">
        <el-steps :active="active" finish-status="success" class="mb-4" :space="200" simple>
          <el-step title="创建知识库" />
          <el-step title="上传文件" />
          <el-step title="数据处理" />
        </el-steps>

        <el-form ref="dataForm" :model="form" label-position="left" label-width="80px" style="width: 400px;">
          <div v-show="active == 0" class="show-steps">
            <el-form-item label="知识名称" prop="kname">
              <el-input v-model="form.kname" placeholder="请输入知识库名称" />
            </el-form-item>

            <el-form-item label="类型" prop="typeId">
              <el-select v-model="form.typeId" placeholder="Select" size="large" style="width: 240px">
                <el-option v-for="item in menulist" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>

            <el-form-item label="描述" prop="description">
              <el-input v-model="form.description" placeholder="请输入描述" />
            </el-form-item>
          </div>
          <div v-show="active == 1">
            <!--            <el-upload-->
            <!--                  ref="uploadRef"-->
            <!--                  :limit="1"-->
            <!--                  :data ="{ kid: route.params.kid as string}"-->
            <!--                  accept=".md,.pdf,.docx,.txt,.csv"-->
            <!--                  :headers="upload.headers"-->
            <!--                  :action="upload.url "-->
            <!--                  :disabled="upload.isUploading"-->
            <!--                  :on-progress="handleFileUploadProgress"-->
            <!--                  :on-success="handleFileSuccess"-->
            <!--                  :auto-upload="true"-->
            <!--                  drag-->
            <!--                >-->
            <!--                  <el-icon class="el-icon&#45;&#45;upload">-->
            <!--                    <i-ep-upload-filled />-->
            <!--                  </el-icon>-->
            <!--                  <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>-->
            <!--                  <template #tip>-->
            <!--                    <div class="text-left el-upload__tip" style="    color: #e81010;">-->
            <!--                      <div class="el-upload__tip"> 请上传一个10MB以内的文件 ，支持 md、pdf、docx、txt、csv 等文件格式 </div>-->
            <!--                      <p> 温馨提醒： </p>-->
            <!--                      <p> 1. 如果文件较大，上传时间可能较长，请耐心等待。 </p>-->
            <!--                      <p> 2. 上传txt文本，请使用UTF8格式，以避免乱码。 </p>-->
            <!--                      <p> 3. 单次上传字数建议5万以下，解析1万字大约需要10秒。 </p>-->
            <!--                    </div>-->
            <!--                  </template>-->
            <!--                </el-upload>-->
          </div>
          <div v-show="active == 2"></div>
        </el-form>

        <div class="footter custom-button">
          <el-button v-if="active <2" style="margin-top: 12px" @click="next">下一步</el-button>
          <el-button v-if="active > 0" style="margin-top: 12px" @click="pre">上一步</el-button>
        </div>
      </el-dialog>

      <!-- 添加或修改知识库管理对话框 -->
      <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
        <el-form ref="knowledge_infoFormRef" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="名称" prop="kname">
            <el-input v-model="form.kname" placeholder="请输入知识库名称" />
          </el-form-item>

          <el-form-item label="类型" prop="typeId">
            <el-select v-model="form.typeId" placeholder="Select" size="large" style="width: 240px">
              <el-option v-for="item in menulist" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>

          <el-form-item label="描述" prop="description">
            <el-input v-model="form.description" placeholder="请输入描述" />
          </el-form-item>
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

<script setup name="Knowledge_info" lang="ts">
import { listKnowledge_info,listKnowledge_type, getKnowledge_info, delKnowledge_info, addKnowledge_info, updateKnowledge_info } from '@/api/knowledge/knowledge_info';
import { Knowledge_infoVO, Knowledge_infoQuery, Knowledge_infoForm,KnowledgeDelReq } from '@/api/knowledge/knowledge_info/types';
import { TypeVO, TypeForm, TypeQuery } from '@/api/system/type/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';
import {RoleVO} from "@/api/system/role/types";
import { ElMessageBox } from 'element-plus'
const router = useRouter();
const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const knowledge_infoList = ref<Knowledge_infoVO[]>([]);

const menulist=ref<TypeVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const daterangeCreateTime = ref([]);

const queryFormRef = ref(ElForm);
const knowledge_infoFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});


const dialogVisible = reactive<DialogOption>({
  visible: false,
  title: ''
});


const handleStepClose = (done: () => void) => {
  ElMessageBox.confirm('Are you sure to close this dialog?')
    .then(() => {
      done()
    })
    .catch(() => {
      // catch error
    })
}


const initFormData: Knowledge_infoForm = {
  kid: undefined,
  kname: undefined,
  typeId:undefined,
  description: undefined,
}

const typeQuery: TypeQuery = {
  name: '',
  status: '0',
  pageNum: 1,
  pageSize: 1000
}
const data = reactive<PageData<Knowledge_infoForm, Knowledge_infoQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    typeId:undefined,
    kname: undefined,
    description: undefined,
    createTime: undefined,
  },
  rules: {
    kname: [
      { required: true, message: "知识库名称不能为空", trigger: "blur" }
    ],
    description: [
      { required: true, message: "描述不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);


const handleOpen = (key: string, keyPath: string[]) => {
}
const handleClose = (key: string, keyPath: string[]) => {
}

const active = ref(0)

const next = () => {
  if (active.value++ > 2) active.value = 0
}
// 步骤条上一步的方法
const pre = () => {
  if (active.value-- < 2) active.value = 0
}

const getTypeList = async ()=>{
  loading.value = true;
  typeQuery.status='0';
  const res = await listKnowledge_type(typeQuery);
  // console.log("1111111",res)
  menulist.value = res.rows;
}

/** 查询知识库管理列表 */
const getList = async () => {
  loading.value = true;
  queryParams.value.params = {};
  if (null != daterangeCreateTime && null != daterangeCreateTime) {
    queryParams.value.params["beginCreateTime"] = daterangeCreateTime.value[0];
    queryParams.value.params["endCreateTime"] = daterangeCreateTime.value[1];
  }
  const res = await listKnowledge_info(queryParams.value);

  knowledge_infoList.value = res.rows;
  // knowledge_infoList.value = res;
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
  knowledge_infoFormRef.value.resetFields();
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
}

//选择的类型
const selectMenu = (id: any) => {
  queryParams.value.typeId = id;
  getList();
}

/** 重置按钮操作 */
const resetQuery = () => {
  daterangeCreateTime.value = [];
  queryFormRef.value.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
const handleSelectionChange = (selection: Knowledge_infoVO[]) => {
  ids.value = selection.map(item => item.id).filter((id): id is string | number => id !== undefined);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加知识库管理";
  nextTick(() => {
    reset();
  });
}

const handleKnownStepAdd = () =>{
  dialogVisible.visible = true;
  dialogVisible.title = "知识库创建";
  nextTick(() => {
    reset();
  });
}


/** 查看附件 */
const handleAttach = (row: Knowledge_infoVO) => {
  router.push("/knowledge/knowledgeAttach/attach/" + row.kid);
}


/** 修改按钮操作 */
const handleUpdate = (row?: Knowledge_infoVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改知识库管理";
  nextTick(async () => {
    reset();
    const _id = row?.id|| ids.value[0];
    const res = await getKnowledge_info(_id);
    loading.value = false;
    console.log(res)
    Object.assign(form.value, res.data);
  });
}

/** 提交按钮 */
const submitForm = () => {
  knowledge_infoFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateKnowledge_info(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addKnowledge_info(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
       getList();
       getTypeList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: Knowledge_infoVO) => {
  const kid = row?.kid;
  if (!kid) {
    return;
  }
  await proxy?.$modal.confirm('是否确认删除知识库管理为"' + row?.kname + '"的数据项？').finally(() => loading.value = false);
  const req: KnowledgeDelReq = {
    kid: kid.toString() // 确保 kid 是字符串类型
  }
  await delKnowledge_info(req);
  proxy?.$modal.msgSuccess("删除成功");
  getList();
  getTypeList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('knowledge/knowledge_info/export', {
    ...queryParams.value
  }, `knowledge_info_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getTypeList();
  getList();
});
</script>
<style>
[class*=el-col-]{
  float: left;
  padding-left: 7.5px;
  padding-right: 7.5px;
}
.custom-button{

}
.show-steps{
  padding: 40px;
}
.el-step__head.is-process{
  color: #1556d8;
  border-color: #1d5ad4;
}
</style>
