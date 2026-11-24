<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="微调名" prop="ftName">
            <el-input v-model="queryParams.ftName" placeholder="请输入微调名" clearable @keyup.enter="handleQuery" />
          </el-form-item>

          <!--          <el-form-item label="训练状态" prop="status">-->
          <!--            <el-select v-model="queryParams.status" placeholder="请选择训练状态" clearable>-->
          <!--              <el-option-->
          <!--                v-for="dict in training_status"-->
          <!--                :key="dict.value"-->
          <!--                :label="dict.label"-->
          <!--                :value="dict.value"-->
          <!--              />-->
          <!--            </el-select>-->
          <!--          </el-form-item>-->
          <el-form-item label="使用状态" prop="useStatus">
            <el-select v-model="queryParams.useStatus" placeholder="请选择使用状态" clearable>
              <el-option v-for="dict in use_status" :key="dict.value" :label="dict.label" :value="dict.value" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['system:tuneInfo:add']">创建精调模型</el-button>
          </el-col>
          <!--          <el-col :span="1.5">-->
          <!--            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['system:tuneInfo:edit']">修改</el-button>-->
          <!--          </el-col>-->
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['system:tuneInfo:remove']"
              >删除</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['system:tuneInfo:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="tuneInfoList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <!--        <el-table-column label="编码" align="center" prop="id" v-if="true" />-->
        <!--        <el-table-column label="微调ID" align="center" prop="ftId" />-->
        <!--        <el-table-column label="作业id" align="center" prop="ftJobId" />-->
        <!--        <el-table-column label="用户ID" align="center" prop="uid" />-->
        <el-table-column label="微调名" align="center" prop="ftName" />
        <el-table-column label="微调模型" align="center" prop="ftModelName" />
        <el-table-column label="基座模型" align="center" prop="baseModel" />
        <el-table-column label="创建时间" align="center" prop="createTime" width="210">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="训练状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="training_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="使用状态" align="center" prop="useStatus">
          <template #default="scope">
            <!--            <el-tooltip content="请注意：启动后该模型将成为主要使用对话模型！" placement="top">-->
            <el-switch
              v-model="scope.row.useStatus"
              class="ml-2"
              inline-prompt
              active-value="1"
              inactive-value="0"
              style="&#45;&#45;el-switch-on-color: #13ce66; &#45;&#45;el-switch-off-color: #ff4949"
              active-text="启用中"
              inactive-text="停用中"
              @change="handleStatusChange(scope.row)"
            ></el-switch>
            <!--            </el-tooltip>-->
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="210" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="primary" icon="DataAnalysis" @click="handleFileUpdate(scope.row)">数据处理</el-button>

            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:tuneInfo:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:tuneInfo:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改精调管理对话框 -->

    <el-dialog title="Create a fine-tuned model" v-model="dialog.visible" :before-close="handleStepClose" width="80%" append-to-body>
      <el-steps :active="active" finish-status="success" class="mb-4" :space="200" simple>
        <el-step title="填写信息" />
        <el-step title="数据处理" />
        <!--        <el-step title="上传数据"   />-->
      </el-steps>

      <el-form ref="tuneInfoFormRef" :model="form" :rules="rules" label-width="80px">
        <div v-show="active == 0" class="show-steps">
          <el-form-item label="模型别名" prop="ftName">
            <el-input v-model="form.ftName" placeholder="输入您微调的别名" style="width: 240px" />
          </el-form-item>
          <el-form-item label="基座模型" prop="baseModel">
            <el-select v-model="form.baseModel" placeholder="请选择基座模型" size="large" style="width: 240px">
              <el-option v-for="dict in base_model_finetune" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
          </el-form-item>
        </div>
      </el-form>

      <el-form ref="tuneFileDataFormRef" :model="fileForm" :rules="rules" label-width="80px">
        <div v-show="active == 1">
          <!--          <el-radio v-model="radio1" label="1" border size="medium">参考小红书</el-radio>-->
          <!--          <el-radio v-model="radio2" label="2" border size="medium">参考抖音</el-radio>-->
          <!--            :data ="{ ft_id:resultData.ftid as string}"-->

          <el-alert title="提示：1.上传数据；2.点击转化并检查文件转化后内容；3.上传文件内容；" type="success" close-text="知道了"> </el-alert>
          <!--            ::data ="{ ftid: resultData.value.ftId as string}"-->

          <el-upload
            ref="uploadRef"
            :limit="100"
            :data="uploadData()"
            accept=".pdf,.docx,.doc,.txt"
            :headers="upload.headers"
            :action="upload.url "
            :disabled="upload.isUploading"
            :on-progress="handleFileUploadProgress"
            :on-success="handleFileSuccess"
            :on-remove="handleRemove"
            :file-list="fileList"
            :auto-upload="true"
            drag
          >
            <el-icon class="el-icon--upload">
              <i-ep-upload-filled />
            </el-icon>
            <div class="el-upload__text">将文件拖到此处或<em>点击上传</em>,支持pdf、docx、ppt、txt、xls 等文件格式</div>
            <!--            <template #tip>-->
            <!--              <div class="text-left el-upload__tip" style="    color: #e81010;">-->
            <!--                <div class="el-upload__tip"> 请上传一个10MB以内的文件 ，支持 md、pdf、docx、txt、csv 等文件格式 </div>-->
            <!--              </div>-->
            <!--            </template>-->
          </el-upload>

          <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
              <el-button type="primary" plain icon="Refresh" @click="reflashFTstatus()">刷新训练状态</el-button>
            </el-col>

            <el-col :span="1.5">
              <i style="color: #ff4949;margin-right: 10px">*</i>
              <el-select v-model="selromptsStoreId" placeholder="请选择生成要求" style="width: 240px">
                <el-option v-for="item in promptsStoreOptions" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
              <el-button type="primary" icon="Setting" @click="$router.push('/modelList/promptsStore')" style="margin-left: 10px">
                前往配置转化
              </el-button>
            </el-col>
          </el-row>

          <el-card shadow="never">
            <el-table v-loading="fileloading" :data="tuneAttachList" @selection-change="handleSelectionChange">
              <el-table-column type="selection" width="55" align="center" />
              <!--        <el-table-column label="编码" align="center" prop="id" v-if="true" />-->
              <!--              <el-table-column label="微调id" align="center" prop="ftId" />-->
              <el-table-column label="文件名" align="center">
                <template #default="scope">
                  <el-link :href="`${scope.row.fileId}`" :underline="false" icon="el-icon-download" target="_blank">
                    <span class="el-icon-document"> {{scope.row.fileName}} </span>
                  </el-link>
                </template>
              </el-table-column>
              <!--              <el-table-column label="微调文件id" align="center" prop="ftFileId" />-->
              <!--              <el-table-column label="上传文件id" align="center" prop="fileId" />-->

              <el-table-column label="原文件内容" align="center">
                <template #default="scope">
                  <el-tooltip content="查看纯文本内容" placement="top">
                    <el-button link type="primary" icon="el-icon-view" @click="handleViewFileContent(scope.row.fileContent,scope.row.filename)">
                      查看内容
                    </el-button>
                  </el-tooltip>
                </template>
              </el-table-column>
              <el-table-column label="转化后内容" align="center">
                <template #default="scope">
                  <el-tooltip content="查看纯文本内容" placement="top">
                    <el-button link type="primary" icon="el-icon-view" @click="handleViewFtContent(scope.row)"> 查看内容 </el-button>
                  </el-tooltip>
                </template>
              </el-table-column>
              <!--              <el-table-column label="转化后内容" align="center" prop="ftContent" />-->
              <el-table-column label="消息" align="center" prop="messages">
                <template #default="scope">
                  <div v-if="scope.row.message==''">暂无消息</div>
                  <div v-if="scope.row.message!=''&&scope.row.message!=null">
                    {{scope.row.message}}
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="创建时间" align="center" prop="createTime">
                <template #default="scope">
                  <div v-if="scope.row.createTime==''">暂无</div>
                  <div v-if="scope.row.createTime!=''&&scope.row.createTime!=null">
                    {{scope.row.createTime}}
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="文件状态" align="center" prop="status">
                <template #default="scope">
                  <dict-tag :options="finetune_attach_status" :value="scope.row.status" />
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="200px" class-name="small-padding fixed-width">
                <template #default="scope">
                  <el-tooltip v-if="scope.row.status==0" content="转化" placement="top">
                    <el-button link type="primary" icon="Refresh" @click="handleFileContentToFtContent(scope.row)"
                      >{{scope.row.status ==="0"? "转化" : "重新转化"}}
                    </el-button>
                  </el-tooltip>
                  <el-tooltip v-if="scope.row.status==1||scope.row.status==98" content="训练" placement="top">
                    <el-button
                      link
                      type="primary"
                      icon="UploadFilled"
                      @click="uploadFinetuneFileData(scope.row)"
                      >{{scope.row.status ==="1"? "训练" : "重新训练"}}</el-button
                    >
                  </el-tooltip>
                  <el-tooltip
                    v-if="scope.row.status==0||scope.row.status==1||scope.row.status==98||scope.row.status==99||scope.row.status==97"
                    content="删除"
                    placement="top"
                  >
                    <el-button link type="primary" icon="Delete" @click="deleteTuneAttach(scope.row)"></el-button>
                  </el-tooltip>
                </template>
              </el-table-column>
            </el-table>

            <pagination
              v-show="filetotal>0"
              :total="filetotal"
              v-model:page="filequeryParams.pageNum"
              v-model:limit="filequeryParams.pageSize"
              @pagination="getFileList"
            />
          </el-card>
        </div>
      </el-form>

      <!--      <template #footer>-->
      <!--        <div class="dialog-footer">-->
      <!--          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>-->
      <!--          <el-button @click="cancel">取 消</el-button>-->
      <!--        </div>-->
      <!--      </template>-->

      <div class="footter custom-button">
        <el-button v-if="active <1" style="margin-top: 12px" @click="next">下一步</el-button>
        <el-button v-if="active > 0" style="margin-top: 12px" @click="pre">上一步</el-button>
        <!--        <el-button :loading="buttonLoading" type="primary" @click="submitForm">暂 存</el-button>-->
      </div>
    </el-dialog>

    <!--修改转化后内容的弹框-->
    <el-dialog v-model="dialogFormVisible" title="修改转化内容" width="500">
      <el-form :model="ftContentform">
        <el-form-item label="" prop="desc">
          <!--          <el-input v-model="ftContentform.ftContent" type="textarea" />-->
          <el-input v-model="ftContentform.ftContent" style="width: 840px" :rows="20" type="textarea" placeholder="ftcontent text" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="warning" @click="checkFtContentBtn" plain>重新检测</el-button>
          <el-button @click="dialogFormVisible = false">Cancel</el-button>
          <el-button type="primary" @click="saveFtContent"> save </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="TuneInfo" lang="ts">
import { listTuneInfo, getTuneInfo, delTuneInfo, addTuneInfo, updateTuneInfo,changeUseStatus,fileContentToFtContent,updateFtContent,uploadFinetuneData,getAttachStatus,checkFtContent,getAttachFtStatus } from '@/api/system/tuneInfo';
import { listTuneAttach, getTuneAttach, delTuneAttach, addTuneAttach, updateTuneAttach } from '@/api/system/tuneAttach';
import { listPromptsStore, getPromptsStore, delPromptsStore, addPromptsStore, updatePromptsStore } from '@/api/system/promptsStore';
import { TuneInfoVO, TuneInfoQuery, TuneInfoForm } from '@/api/system/tuneInfo/types';
import { TuneAttachVO, TuneAttachQuery, TuneAttachForm } from '@/api/system/tuneAttach/types';
import { PromptsStoreVO, PromptsStoreQuery, PromptsStoreForm } from '@/api/system/promptsStore/types';
import { ComponentInternalInstance, onMounted, onBeforeUnmount, ref } from 'vue';
import { DateModelType, ElTree, ElUpload, UploadFile,ElForm,ElMessage, ElMessageBox } from 'element-plus';
import {RoleVO} from "@/api/system/role/types";
import {changeRoleStatus} from "@/api/system/role";
import { AxiosResponse } from 'axios';
import { FormRules } from 'element-plus';
import type, { Action } from 'element-plus'
import {getToken} from "@/utils/auth";
const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { use_status, training_status, base_model_finetune } = toRefs<any>(proxy?.useDict('use_status', 'training_status', 'base_model_finetune'));
const { finetune_attach_status } = toRefs<any>(proxy?.useDict('finetune_attach_status'));
const tuneInfoList = ref<TuneInfoVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const fileloading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const ftids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const filetotal = ref(0);

const tuneAttachList = ref<TuneAttachVO[]>([]);
const uploadRef = ref(ElUpload);
const queryFormRef = ref(ElForm);
const tuneInfoFormRef = ref(ElForm);
const tuneFileDataFormRef = ref(ElForm);
let dialogFormVisible = ref(false);
const formLabelWidth = '140px'
const ftContentform = reactive({
  ftContent: ''
})

let showFtContentRow: TuneAttachVO | null = null;
let intervalId: number | null | undefined;
const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});
const switchuseStatus = ref(true);

const upload = reactive<ImportOption>({
  // 是否显示弹出层
  open: false,
  // 弹出层标题
  title: "上传对话案例附件",
  // 是否禁用上传
  isUploading: false,

  // 设置上传的请求头部
  headers: { Authorization: "Bearer " + getToken() },
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/system/tuneInfo/upload"
})

/**文件上传中处理 */
const handleFileUploadProgress = () => {
  upload.isUploading = true;
}
const fileList =ref<Array<UploadFile>>([]);
/** 文件上传成功处理 */
const handleFileSuccess = (response: any, file: UploadFile) => {
  upload.open = false;
  upload.isUploading = false;
  uploadRef.value.handleRemove(file);

  ElMessageBox.alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "上传结果", { dangerouslyUseHTMLString: true });
  if(response.code==200){
    fileList.value.push(file);
  }
  getFileList();
}
const handleRemove = (file: any, fileList: any) => {
  // 这里可以处理文件移除后的逻辑

  // fileList.value.remove(file);
};

//文件列表
/** 查询微调附件列表 */
const getFileList = async () => {
  fileloading.value = true;
  const res = await listTuneAttach(filequeryParams.value);
  tuneAttachList.value = res.rows;
  filetotal.value = res.total;
  fileloading.value = false;
}




const initFormData: TuneInfoForm = {
  id: undefined,
  ftId: undefined,
  ftJobId: undefined,
  uid: undefined,
  ftName: undefined,
  ftModelName: undefined,
  baseModel: undefined,
  status: undefined,
  useStatus: undefined
}

const data = reactive<PageData<TuneInfoForm, TuneInfoQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    ftId: undefined,
    ftJobId: undefined,
    uid: undefined,
    ftName: undefined,
    ftModelName: undefined,
    baseModel: undefined,
    status: undefined,
    useStatus: undefined
  },
  rules: {

    ftName: [
      { required: true, message: "微调名不能为空", trigger: "blur" }
    ],

    baseModel: [
      { required: true, message: "基座模型不能为空", trigger: "blur" }
    ]
  }
});


/**
 * 分页数据
 * T : 表单数据
 * D : 查询参数
 */
interface stepsPageData<T, D> {
  fileForm: T;
  filequeryParams: D;
  fileRules: FormRules;
}

const initFileFormData: TuneAttachForm = {
  id: undefined,
  ftId: undefined,
  ftFileId: undefined,
  promptsStoreId: undefined,
  ftJobId: undefined,
  fileId: undefined,
  status: undefined,
  fileContent: undefined,
  ftContent: undefined,
  messages: undefined
}
const filedata = reactive<stepsPageData<TuneAttachForm, TuneAttachQuery>>({
  fileForm: {...initFileFormData},
  filequeryParams: {
    pageNum: 1,
    pageSize: 10,
    ftId: undefined,
    ftFileId: undefined,
     promptsStoreId: undefined,
    ftJobId: undefined,
    fileId: undefined,
    status: undefined,
    fileContent: undefined,
    ftContent: undefined,
    messages: undefined
  },
  fileRules: {
    // id: [
    //   { required: true, message: "编码不能为空", trigger: "blur" }
    // ],
    // status: [
    //   { required: true, message: "文件状态不能为空", trigger: "change" }
    // ],
    // fileContent: [
    //   { required: true, message: "文件内容不能为空", trigger: "blur" }
    // ],
  }
});


const {  filequeryParams,fileForm,fileRules } = toRefs(filedata);
const { queryParams, form, rules } = toRefs(data);
const selromptsStoreId = ref('');
/** 使用状态修改 */
const handleStatusChange = async (row: TuneInfoVO) => {
  console.log(row.useStatus)
  let text = row.useStatus === "0" ? "停用" : "启用";
  try {
    await proxy?.$modal.confirm('确认要"' + text + '""' + row.ftName + '"模型?');
    await changeUseStatus(row.id, row.useStatus);
    proxy?.$modal.msgSuccess(text + "成功");
  } catch {
    row.useStatus = row.useStatus === "0" ? "1" : "0";
  }
}

/** 文件内容转微调内容 */
const handleFileContentToFtContent = async (row: TuneAttachVO) => {
  if(selromptsStoreId.value ==''){
    ElMessageBox.alert("请选择生成要求", "操作提示", {
      confirmButtonText: 'OK',
      callback: (action: Action) => {
        return ;
      },
    })
    return ;
  }
  row.promptsStoreId = selromptsStoreId.value;
  console.log(selromptsStoreId.value)
  if( row.status !== "0"&&row.status !== "99"){
    ElMessageBox.alert("已转化内容不可在此转化", "tip", {
      confirmButtonText: 'OK',
      callback: (action: Action) => {

      },
    })
    return ;
  }
  //
  try {
    await proxy?.$modal.confirm('确认要转化该文件吗?');
    fileloading.value=true;
    await fileContentToFtContent(row.id,row.promptsStoreId).then((resp) => {
      fileloading.value=false
    })
    await getFileList();
    proxy?.$modal.msgSuccess( "任务提交成功，请等待任务完成");
    fileloading.value=false
    //开启循环检测
    startInterval(row);


  } catch {
    await getFileList();
    fileloading.value=false
  }
}
const startInterval = (row: TuneAttachVO) =>{
  // 每5秒查询一次
   intervalId = setInterval(() => {
       getAttachLoadingStatus(row);
  }, 5000);
}
const stopInterval = () =>{
  // 清除定时器
  if (intervalId) {
    clearInterval(intervalId);
    intervalId = null;
  }
}

//训练微调数据 uploadFinetuneData
const uploadFinetuneFileData = async (row: TuneAttachVO) => {
  if( row.status !== "1"&&row.status !== "98"){
    ElMessageBox.alert("已训练内容不可变更", "tip", {
      confirmButtonText: 'OK',
      callback: (action: Action) => {
        return false;
      },
    })
    return false;
  }
  //
  try {
    await proxy?.$modal.confirm('确认要训练该批数据吗?');
    fileloading.value=true;
    await uploadFinetuneData(row.id,row.ftId).then((resp) => {
      fileloading.value=false
    })
    getFileList();
    //开启循环检测
    startInterval(row);
    proxy?.$modal.msgSuccess( "任务提交成功");

  } catch {
    console.log('error')
  }
}






/** 查询精调管理列表 */
const getList = async () => {
  loading.value = true;
  fileloading.value=true;
  const res = await listTuneInfo(queryParams.value);
  tuneInfoList.value = res.rows;
  total.value = res.total;
  loading.value = false;
  fileloading.value = false;
}

/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
}

/** 表单重置 */
const reset = () => {
  form.value = {...initFormData};
  fileForm.value  = {...initFileFormData};
  tuneInfoFormRef.value.resetFields();
  tuneFileDataFormRef.value.resetFields();
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
const handleSelectionChange = (selection: TuneInfoVO[]) => {
  ids.value = selection.map(item => item.id);
  ftids.value = selection.map(item => item.ftId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}



let active = ref(0)

//操作方式 add  update
let optType = "add";
let resultData = ref<AxiosResponse | null>(null);

const next = () => {
  const activestepValue =active.value;
  let bformRef = null;
  switch (activestepValue) {
    case 0:
      bformRef = tuneInfoFormRef;
      break;
    case 1:
      bformRef = tuneFileDataFormRef;
      break;

      default:
        bformRef = tuneInfoFormRef;
        break;
  }
  if(activestepValue==1){

    if(fileList.value.length ==0){
      getFileList();
      openFileBox();
      return ;
    }
  }

    // tuneFileDataFormRef
  bformRef.value.validate(async (valid: boolean) => {
    if (valid) {
      //判断新增或者修改
      if(optType==='add'){
        submitForm();
      }else{
        updateTuneInfo(form.value);
      }
       getList();
      if (active.value++ > 1) active.value = 0
    }

  });

}
// 步骤条上一步的方法
const pre = () => {
  optType = "update";
  if (active.value-- < 1) active.value = 0
}


/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加精调管理";
  optType = "add";
  active = ref(0);
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: TuneInfoVO) => {
  loading.value = true
  fileloading.value = true
  dialog.visible = true;
  dialog.title = "修改精调管理";
  optType = "update";

  // active = ref(1);
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const _ftid = row?.ftId || ftids.value[0]
    const res = await getTuneInfo(_id);
    loading.value = false;
    fileloading.value = false;
    Object.assign(form.value, res.data);
  });
}

const handleFileUpdate = async (row?: TuneInfoVO) => {
  loading.value = true
  fileloading.value = true
  dialog.visible = true;
  dialog.title = "精调管理";
  filequeryParams.value.ftId = row?.ftId;

  const _id = row?.id || ids.value[0];

  resultData.value = await getTuneInfo(_id);

  active = ref(1);
  getFileList();
  optType = "update";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0]
    const _ftid = row?.ftId || ftids.value[0]
    const res = await getTuneInfo(_id);
    loading.value = false;
    fileloading.value = false;
    Object.assign(form.value, res.data);
  });

}
const handleViewFileContent = (fileContent?:string,filename?:string) => {

  ElMessageBox.alert(fileContent, filename, {
    // if you want to disable its autofocus
    // autofocus: false,
    // dangerouslyUseHTMLString:true,
    confirmButtonText: 'OK',
    customClass:'custom-message-box',
    callback: (action: Action) => {

    },
  })
}

/** 修改转化后的内容 */
const saveFtContent = async () => {
  if (!showFtContentRow) {
    return;
  }

  if (showFtContentRow.status !== "1" && showFtContentRow.status !== "99") {
    ElMessageBox.alert("内容状态暂时无法修改", "tip", {
      confirmButtonText: 'OK',
      callback: (action: Action) => {

      },
    })
  }
  try {
    await proxy?.$modal.confirm('确认要修改该吗?');
    showFtContentRow.ftContent = ftContentform.ftContent;
    await updateFtContent(showFtContentRow.id, showFtContentRow.ftContent);
    proxy?.$modal.msgSuccess( "操作成功");
  } catch {
    console.log('error')
  }
}

// checkFtContentBtn
/** 修改转化后的内容 */
const checkFtContentBtn = async () => {
 if (!showFtContentRow) {
    return;
  }
  try {
    await proxy?.$modal.confirm('确认要检测内容吗?');
    showFtContentRow.ftContent = ftContentform.ftContent;
    await checkFtContent(showFtContentRow.id, showFtContentRow.ftContent);
    proxy?.$modal.msgSuccess( "操作成功");
    dialogFormVisible.value = false;
    ElMessageBox.alert('请注意查看文件状态，以及消息，以确保是否成功');
    getFileList();
  } catch {
  }
}

const reflashFTstatus  = async () => {
  try {
    await proxy?.$modal.confirm('确认要刷新训练状态吗?');
    let handleNumData =  await getAttachFtStatus(filequeryParams.value.ftId as string);
    if(Number(handleNumData.data) === 0){
      proxy?.$modal.msgWarning("暂无进行中的状态需要刷新");
    }else{
      proxy?.$modal.msgSuccess( "操作成功");
      dialogFormVisible.value = false;
      ElMessageBox.alert('请注意查看文件状态，以及消息，以确保是否成功');
      getFileList();
    }
  } catch {
    console.log('error')
  }

}




const handleViewFtContent = (row: TuneAttachVO) => {
  showFtContentRow = row;
  if(row.ftContent==''||row.ftContent==null){
    ElMessageBox.alert("暂未找到转化内容", "", {
      confirmButtonText: 'OK',
    })
  }else{
    dialogFormVisible.value = true;
    ftContentform.ftContent = row.ftContent;

  }

}
// getAttachStatus

const getAttachLoadingStatus = async (row: TuneAttachVO) => {
  // getAttachStatus
    const res = await getAttachStatus(row.id);

    if(res.data.status!="4"&&res.data.status!="3"){
      await getFileList();
      await  stopInterval();
    }

}





/** 提交按钮 */
const submitForm = () => {
  //根据步骤判断保存
  const activestepValue =active.value;

  tuneInfoFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateTuneInfo(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        // await addTuneInfo(form.value).finally(() =>  buttonLoading.value = false);

        try {
          // 尝试获取数据并存储在resultData中
          resultData.value = await addTuneInfo(form.value);


          filequeryParams.value.ftId = resultData.value.data.ftId;
        } catch (error) {
          // 错误处理
          console.error('An error occurred:', error);
        } finally {
          // 无论成功或失败，都会执行
          buttonLoading.value = false;
        }


      }
      proxy?.$modal.msgSuccess("提交成功");
      getFileList();
      // dialog.visible = false;
      await getList();
    }
  });
}

const uploadData = () => {
  // 这里我们检查 resultData.value 是否为 AxiosResponse 对象，并提取我们需要的数据
  if (resultData.value && resultData.value.data) {
    return {ftid:resultData.value.data.ftId};
  }
  return {ftid:null}; // 如果没有数据，返回一个空对象
};

const handleStepClose = (done: () => void) => {
  ElMessageBox.confirm('Are you sure to close this dialog?')
    .then(() => {
      done()
    })
    .catch(() => {
      // catch error
    })
}

/** 删除按钮操作 */
const handleDelete = async (row?: TuneInfoVO) => {
  const _ids = row?.id || ids.value;
  const _ftids = row?.ftId || ftids.value;
  await proxy?.$modal.confirm('是否确认删除精调为"' + _ftids + '"的数据项？').finally(() => loading.value = false);
  await delTuneInfo(_ftids);
  proxy?.$modal.msgSuccess("删除成功");
   getList();
}
/** 删除附件按钮操作 */
const deleteTuneAttach =  async (row?: TuneAttachVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除附件为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delTuneAttach(_ids);
  proxy?.$modal.msgSuccess("删除成功");
   getFileList();
}



/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('system/tuneInfo/export', {
    ...queryParams.value
  }, `tuneInfo_${new Date().getTime()}.xlsx`)
}

// 在 setup 函数内添加
// const promptsStoreOptions = ref([]);
const promptsStoreOptions=ref<PromptsStoreVO[]>([]);
// 在 setup 函数中添加获取选项的方法
// const getPromptsStoreOptions = async () => {
//   // 假设有一个 API 调用来获取选项
//   const res = await listPromptsStore();
//   promptsStoreOptions.value = res.rows;
// };
const promptsStoreQuery: PromptsStoreQuery = {
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
};
const getPromptsStoreOptions = async ()=>{
  loading.value = true;
  // modelQuery.status='0';
  const res = await listPromptsStore(promptsStoreQuery);
  promptsStoreOptions.value = res.rows;
}




onMounted(() => {
  getList();
  getPromptsStoreOptions();
});

onBeforeUnmount(() => {
  // 组件销毁前清除定时器
  if (intervalId) {
    clearInterval(intervalId);
  }
});

const openFileBox = () => {
  ElMessageBox.confirm(
    'You have not uploaded any attachments yet!',
    'Warning',
    {
      confirmButtonText: 'OK',
      cancelButtonText: 'Cancel',
      type: 'warning',
    }
  )
    .then(() => {
      ElMessage({
        type: 'success',
        message: 'ok',
      })
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: 'ok',
      })
    })
}
</script>

<style>
[class*=el-col-]{
  float: left;
  padding-left: 7.5px;
  padding-right: 7.5px;
}
.custom-button{
  width: '100%';
  border-top: solid 1px;
  border-block-color: #eee;
  text-align: left;
}
.show-steps{
  padding: 40px;
}
.el-step__head.is-process{
  color: #1556d8;
  border-color: #1d5ad4;
}
.custom-message-box {
  min-width: 90%;/* 设置宽度 */
  height: 60%; /* 设置高度 */
  overflow-y: auto;
  /* 你可以添加更多的样式 */
}
</style>
