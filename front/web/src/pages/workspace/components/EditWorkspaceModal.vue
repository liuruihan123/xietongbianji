<template>
  <a-modal
    :visible="visible"
    title="编辑工作空间"
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
      :label-col="{ span: 5 }"
      :wrapper-col="{ span: 19 }"
    >
      <a-form-item label="工作空间名称" name="workspaceName">
        <a-input
          v-model:value="formData.workspaceName"
          placeholder="请输入工作空间名称"
          :maxlength="50"
          show-count
        />
      </a-form-item>

      <a-form-item label="工作空间类型">
        <a-input
          :value="workspaceTypeLabel"
          disabled
          class="readonly-input"
        />
      </a-form-item>

      <a-form-item label="工作空间描述" name="workspaceDesc">
        <a-textarea
          v-model:value="formData.workspaceDesc"
          placeholder="请输入工作空间描述(可选)"
          :rows="4"
          :maxlength="200"
          show-count
        />
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

      <a-form-item label="存储配额" name="maxStorage">
        <a-select
          v-model:value="formData.maxStorage"
          placeholder="请选择存储配额"
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
import { ref, reactive, watch, computed } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { useWorkspaceStore } from '@/stores/workspace'
import { STORAGE_QUOTA_OPTIONS, WORKSPACE_TYPE_OPTIONS, WorkspaceType } from '@/types/workspace'
import type { FormInstance } from 'ant-design-vue'
import type { Workspace } from '@/types/workspace'
import CoverSelectModal from './CoverSelectModal.vue'

const props = defineProps<{
  visible: boolean
  workspace: Workspace | null
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
  id: 0,
  workspaceName: '',
  workspaceType: WorkspaceType.PERSONAL,
  workspaceDesc: '',
  workspaceIcon: '',
  maxStorage: 1073741824,
})

const storageOptions = STORAGE_QUOTA_OPTIONS

// 获取工作空间类型的文本标签
const workspaceTypeLabel = computed(() => {
  const option = WORKSPACE_TYPE_OPTIONS.find(opt => opt.value === formData.workspaceType)
  return option ? option.label : '未知类型'
})

const rules = {
  workspaceName: [
    { required: true, message: '请输入工作空间名称', trigger: 'blur' },
    { min: 2, max: 50, message: '名称长度在 2 到 50 个字符', trigger: 'blur' },
  ],
  maxStorage: [
    { required: true, message: '请选择存储配额', trigger: 'change' },
  ],
}

async function handleSubmit() {
  try {
    await formRef.value?.validate()
    loading.value = true

    await workspaceStore.updateWorkspace(formData)
    
    message.success('工作空间更新成功')
    emit('success')
    handleCancel()
  } catch (error: any) {
    if (error.errorFields) return
    message.error(error.message || '更新失败')
  } finally {
    loading.value = false
  }
}

function handleCancel() {
  formRef.value?.resetFields()
  emit('update:visible', false)
}

function handleCoverSelect(cover: string) {
  formData.workspaceIcon = cover
  message.success('封面选择成功')
}

watch(() => props.workspace, (workspace) => {
  if (workspace) {
    formData.id = workspace.id
    formData.workspaceName = workspace.workspaceName
    formData.workspaceType = workspace.workspaceType
    formData.workspaceDesc = workspace.workspaceDesc || ''
    formData.workspaceIcon = workspace.workspaceIcon || ''
    formData.maxStorage = workspace.maxStorage
  }
}, { immediate: true })
</script>

<style scoped lang="scss">
.readonly-input {
  :deep(.ant-input[disabled]) {
    color: rgba(0, 0, 0, 0.85);
    background-color: #f5f5f5;
    cursor: not-allowed;
  }
}

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
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

