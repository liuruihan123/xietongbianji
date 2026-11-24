<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="套餐名称" prop="name">
            <el-input v-model="queryParams.name" placeholder="请输入套餐名称" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['system:packagePlan:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['system:packagePlan:edit']"
              >修改</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['system:packagePlan:remove']"
              >删除</el-button
            >
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['system:packagePlan:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="packagePlanList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="主键" align="center" prop="id" v-if="false" />
        <el-table-column label="套餐名称" align="center" prop="name" />
        <el-table-column label="套餐国内价格" align="center" prop="price" />
        <el-table-column label="套餐国外价格" align="center" prop="enPrice" />
        <el-table-column label="有效时间" align="center" prop="duration" />
         <el-table-column label="积分数" align="center" prop="pgPoints" />
        <el-table-column label="计划详情" align="center" prop="planDetail" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:packagePlan:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:packagePlan:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改套餐管理对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="700px" append-to-body>
      <el-form ref="packagePlanFormRef" :model="form" :rules="rules" label-width="180px">
        <el-form-item label="套餐名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入套餐名称" />
        </el-form-item>
        <el-form-item label="套餐英文名称" prop="enName">
          <el-input v-model="form.enName" placeholder="请输入套餐名称" />
        </el-form-item>

        <el-form-item label="套餐国内价格" prop="price">
          <el-input v-model="form.price" placeholder="请输入套餐国内价格" @input="validatePrice($event, 'price')" />
        </el-form-item>
        <el-form-item label="套餐国外价格" prop="enPrice">
          <el-input v-model="form.enPrice" placeholder="请输入套餐国外价格" @input="validatePrice($event, 'enPrice')" />
        </el-form-item>
        <el-form-item label="有效时间" prop="duration">
          <el-input v-model="form.duration" placeholder="请输入有效时间" @input="validateDuration">
            <template #append>月</template>
          </el-input>
        </el-form-item>


         <el-form-item label="积分数量" prop="pgPoints">
          <el-input v-model="form.pgPoints" placeholder="请输入积分数量" @input="validatePrice($event, 'pgPoints')" />
        </el-form-item>


        <el-form-item label="计划详情" prop="planDetail">
          <el-input v-model="form.planDetail" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="英文计划详情" prop="enPlanDetail">
          <el-input v-model="form.enPlanDetail" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
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

<script setup name="PackagePlan" lang="ts">
import { listPackagePlan, getPackagePlan, delPackagePlan, addPackagePlan, updatePackagePlan } from '@/api/system/packagePlan';
import { PackagePlanVO, PackagePlanQuery, PackagePlanForm } from '@/api/system/packagePlan/types';
import { ComponentInternalInstance } from 'vue';
import { ElForm } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const packagePlanList = ref<PackagePlanVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref(ElForm);
const packagePlanFormRef = ref(ElForm);

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: PackagePlanForm = {
  id: undefined,
  name: '',
  price: undefined,
  enPrice: undefined,
  enName: '',
  enPlanDetail: '',
  duration: '',
  pgPoints: undefined,
  planDetail: '',
  remark: ''
}
const data = reactive<PageData<PackagePlanForm, PackagePlanQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: undefined,
  },
  rules: {
    id: [
      { required: true, message: "主键不能为空", trigger: "blur" }
    ],
    name: [
      { required: true, message: "套餐名称不能为空", trigger: "blur" }
    ],
    price: [
      { required: true, message: "套餐价格不能为空", trigger: "blur" }
    ],
     enPrice: [
      { required: true, message: "套餐价格不能为空", trigger: "blur" }
    ],
    enName: [
      { required: true, message: "套餐名称不能为空", trigger: "blur" }
    ],
    duration: [
      { required: true, message: "有效时间不能为空", trigger: "blur" }
    ],
    pgPoints: [
      { required: true, message: "积分数量不能为空", trigger: "blur" }
    ],
    
    planDetail: [
      { required: true, message: "计划详情不能为空", trigger: "blur" }
    ],
    remark: [
      { required: true, message: "备注不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询套餐管理列表 */
const getList = async () => {
  loading.value = true;
  const res = await listPackagePlan(queryParams.value);
  packagePlanList.value = res.rows;
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
  packagePlanFormRef.value.resetFields();
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
const handleSelectionChange = (selection: PackagePlanVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  dialog.visible = true;
  dialog.title = "添加套餐管理";
  nextTick(() => {
    reset();
  });
}

/** 修改按钮操作 */
const handleUpdate = (row?: PackagePlanVO) => {
  loading.value = true
  dialog.visible = true;
  dialog.title = "修改套餐管理";
  nextTick(async () => {
    reset();
    const _id = row?.id || ids.value[0];
    console.log(row)
    console.log(_id)
    const res = await getPackagePlan(_id);
    loading.value = false;
    Object.assign(form.value, res.data);
  });
}

/** 提交按钮 */
const submitForm = () => {
  packagePlanFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updatePackagePlan(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addPackagePlan(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: PackagePlanVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除套餐管理编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delPackagePlan(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('system/packagePlan/export', {
    ...queryParams.value
  }, `packagePlan_${new Date().getTime()}.xlsx`)
}

const validatePrice = (value: string, field: 'price' | 'enPrice' | 'pgPoints') => {
  const reg = /^\d+(\.\d{0,2})?$/;
  if (!reg.test(value)) {
    form.value[field] = parseFloat(value.replace(/[^\d.]/g, '')) || 0;
  }
}

const validateDuration = (value: string) => {
  form.value.duration = value.replace(/[^\d]/g, '');
}

onMounted(() => {
  getList();
});
</script>
