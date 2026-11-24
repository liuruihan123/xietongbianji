<template>
  <a-modal
    :visible="visible"
    title="创建工作空间"
    :confirm-loading="loading"
    :destroy-on-close="true"
    :mask-closable="false"
    width="600px"
    @ok="handleSubmit"
    @cancel="handleCancel"
    okText="确定"
    cancelText="取消"
  >
    <a-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 18 }"
    >
      <!-- 工作空间类型 - 必填 -->
      <a-form-item label="工作空间类型" name="workspaceType">
        <a-select
          v-model:value="formData.workspaceType"
          placeholder="请选择工作空间类型"
          @change="handleTypeChange"
        >
          <a-select-option
            v-for="option in workspaceTypeOptions"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <!-- 工作空间名称 - 必填 -->
      <a-form-item label="工作空间名称" name="workspaceName">
        <a-input
          v-model:value="formData.workspaceName"
          placeholder="请输入工作空间名称"
          :maxlength="100"
          show-count
        />
      </a-form-item>

      <!-- 部门空间提示 -->
      <a-form-item
        v-if="formData.workspaceType === WorkspaceType.DEPARTMENT"
        label="部门空间"
      >
        <div class="dept-tip">
          <a-alert
            message="部门空间说明"
            description="部门空间将自动关联您所在的部门，同部门的用户可以查看此工作空间。"
            type="info"
            show-icon
          />
        </div>
      </a-form-item>

      <!-- 工作空间描述 - 可选 -->
      <a-form-item label="工作空间描述" name="workspaceDesc">
        <a-textarea
          v-model:value="formData.workspaceDesc"
          placeholder="请输入工作空间描述(可选)"
          :rows="4"
          :maxlength="500"
          show-count
        />
      </a-form-item>

      <!-- 存储配额 - 可选 -->
      <a-form-item label="存储配额" name="maxStorage">
        <a-select
          v-model:value="formData.maxStorage"
          placeholder="请选择存储配额(可选)"
          allow-clear
        >
          <a-select-option
            v-for="option in storageOptions"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <!-- 工作空间颜色 - 可选 -->
      <a-form-item label="工作空间颜色" name="workspaceColor">
        <div class="color-picker-wrapper">
          <input
            v-model="formData.workspaceColor"
            type="color"
            class="color-picker"
          />
          <a-input
            v-model:value="formData.workspaceColor"
            placeholder="请选择工作空间颜色(可选)"
            :maxlength="20"
            style="flex: 1; margin-left: 12px"
          />
        </div>
      </a-form-item>

      <!-- 工作空间封面 - 可选 -->
      <a-form-item label="工作空间封面" name="workspaceIcon">
        <div class="cover-selector">
          <div v-if="formData.workspaceIcon" class="cover-preview" @click="showCoverSelectModal = true">
            <img :src="formData.workspaceIcon" alt="封面预览" class="preview-image" />
            <div class="cover-overlay">
              <span>点击更换封面</span>
            </div>
          </div>
          <a-button v-else type="dashed" block @click="showCoverSelectModal = true">
            <PlusOutlined />
            选择封面
          </a-button>
        </div>
        <div class="upload-tip">从预设封面中选择</div>
      </a-form-item>
    </a-form>

    <!-- 封面选择弹窗 -->
    <CoverSelectModal
      v-model:visible="showCoverSelectModal"
      :current-cover="formData.workspaceIcon"
      @select="handleCoverSelect"
    />
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { useWorkspaceStore } from '@/stores/workspace'
import { STORAGE_QUOTA_OPTIONS, WORKSPACE_TYPE_OPTIONS, WorkspaceType } from '@/types/workspace'
import type { FormInstance } from 'ant-design-vue'
import CoverSelectModal from './CoverSelectModal.vue'

const props = defineProps<{
  visible: boolean
  defaultWorkspaceType?: WorkspaceType
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  success: []
}>()

const workspaceStore = useWorkspaceStore()
const formRef = ref<FormInstance>()
const loading = ref(false)
const showCoverSelectModal = ref(false)

const formData = reactive({
  workspaceName: '',
  workspaceType: WorkspaceType.PERSONAL, // 默认个人工作空间
  workspaceDesc: '',
  workspaceIcon: '',
  workspaceColor: '#1890ff', // 默认蓝色
  maxStorage: undefined as number | undefined,
})

const storageOptions = STORAGE_QUOTA_OPTIONS
const workspaceTypeOptions = WORKSPACE_TYPE_OPTIONS

const rules = {
  workspaceType: [
    { required: true, message: '请选择工作空间类型', trigger: 'change' },
  ],
  workspaceName: [
    { required: true, message: '请输入工作空间名称', trigger: 'blur' },
    { min: 2, max: 100, message: '名称长度在 2 到 100 个字符', trigger: 'blur' },
  ],
  workspaceDesc: [
    { max: 500, message: '描述长度不能超过 500 个字符', trigger: 'blur' },
  ],
}

/**
 * 工作空间类型变化处理
 */
function handleTypeChange(value: WorkspaceType) {
  // 类型变化时的处理逻辑
}

/**
 * 处理封面选择
 */
function handleCoverSelect(cover: string) {
  formData.workspaceIcon = cover
  message.success('封面选择成功')
}

/**
 * 提交表单
 */
async function handleSubmit() {
  try {
    await formRef.value?.validate()
    loading.value = true

    await workspaceStore.createWorkspace(formData)

    message.success('工作空间创建成功')
    emit('success')
    handleCancel()
  } catch (error: any) {
    if (error.errorFields) {
      // 表单验证失败
      return
    }
    message.error(error.message || '创建失败')
  } finally {
    loading.value = false
  }
}

/**
 * 取消
 */
function handleCancel() {
  formRef.value?.resetFields()
  formData.workspaceName = ''
  formData.workspaceType = WorkspaceType.PERSONAL
  formData.workspaceDesc = ''
  formData.workspaceIcon = ''
  formData.workspaceColor = '#1890ff'
  formData.maxStorage = undefined
  emit('update:visible', false)
}

/**
 * 监听visible变化
 */
watch(() => props.visible, (val) => {
  if (val) {
    // 弹框打开时,根据传入的默认工作空间类型设置表单
    formData.workspaceType = props.defaultWorkspaceType || WorkspaceType.PERSONAL
  } else {
    handleCancel()
  }
})
</script>

<style scoped lang="scss">
.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}

.dept-tip {
  width: 100%;
}

.color-picker-wrapper {
  display: flex;
  align-items: center;

  .color-picker {
    width: 50px;
    height: 32px;
    border: 1px solid #d9d9d9;
    border-radius: 4px;
    cursor: pointer;

    &:hover {
      border-color: #40a9ff;
    }
  }
}

.cover-selector {
  .cover-preview {
    position: relative;
    width: 100%;
    aspect-ratio: 16 / 9;
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
    border: 1px solid #d9d9d9;
    transition: all 0.3s;

    &:hover {
      border-color: #1890ff;

      .cover-overlay {
        opacity: 1;
      }
    }

    .preview-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .cover-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 14px;
      opacity: 0;
      transition: opacity 0.3s;
    }
  }
}
</style>

