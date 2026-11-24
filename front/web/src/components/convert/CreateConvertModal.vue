<template>
  <a-modal
    v-model:visible="visible"
    title="创建转换任务"
    :confirm-loading="loading"
    @ok="handleSubmit"
    @cancel="handleCancel"
  >
    <a-form ref="formRef" :model="formData" :rules="rules" :label-col="{ span: 6 }">
      <!-- 源文件 -->
      <a-form-item label="源文件" name="sourceFileId">
        <a-input :value="props.sourceFileName" disabled />
      </a-form-item>

      <!-- 转换格式 -->
      <a-form-item label="转换格式" name="convertOption">
        <a-select
          v-model:value="formData.convertOption"
          placeholder="请选择转换格式"
          @change="handleConvertOptionChange"
        >
          <a-select-option
            v-for="option in availableOptions"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <!-- 高级配置 -->
      <a-collapse v-if="showAdvancedConfig" style="margin-bottom: 16px">
        <a-collapse-panel key="1" header="高级配置">
          <!-- PDF转图片配置 -->
          <template v-if="isPdfToImage">
            <a-form-item label="图片质量" :label-col="{ span: 8 }">
              <a-slider
                v-model:value="formData.config.imageQuality"
                :min="1"
                :max="100"
                :marks="{ 1: '低', 50: '中', 100: '高' }"
              />
            </a-form-item>

            <a-form-item label="图片DPI" :label-col="{ span: 8 }">
              <a-input-number
                v-model:value="formData.config.imageDpi"
                :min="72"
                :max="600"
                :step="1"
                style="width: 100%"
              />
            </a-form-item>

            <a-form-item label="页面范围" :label-col="{ span: 8 }">
              <a-input
                v-model:value="formData.config.pageRange"
                placeholder="例如: 1-5,8,10-12"
              />
            </a-form-item>
          </template>

          <!-- Office转PDF配置 -->
          <template v-if="isOfficeToPdf">
            <a-form-item label="保留书签" :label-col="{ span: 8 }">
              <a-switch v-model:checked="formData.config.keepBookmarks" />
            </a-form-item>

            <a-form-item label="保留超链接" :label-col="{ span: 8 }">
              <a-switch v-model:checked="formData.config.keepHyperlinks" />
            </a-form-item>
          </template>
        </a-collapse-panel>
      </a-collapse>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { message } from 'ant-design-vue'
import { createConvertTask } from '@/api/convert'
import {
  getAvailableConvertOptions,
  ConvertType,
  type TargetFormat,
  type ConvertFormatOption,
} from '@/types/convert'

/**
 * 组件属性
 */
interface Props {
  /** 源文件ID */
  sourceFileId?: number
  /** 源文件名 */
  sourceFileName?: string
}

const props = withDefaults(defineProps<Props>(), {
  sourceFileId: 0,
  sourceFileName: '',
})

/**
 * 组件事件
 */
const emit = defineEmits<{
  success: []
  cancel: []
}>()

// 状态
const visible = ref(false)
const loading = ref(false)
const formRef = ref()

// 表单数据
const formData = ref({
  sourceFileId: 0,
  convertOption: '',
  targetFormat: '' as TargetFormat,
  config: {
    imageQuality: 85,
    imageDpi: 150,
    pageRange: '',
    keepBookmarks: true,
    keepHyperlinks: true,
  },
})

// 表单验证规则
const rules = {
  sourceFileId: [{ required: true, message: '请选择源文件', trigger: 'change' }],
  convertOption: [{ required: true, message: '请选择转换格式', trigger: 'change' }],
}

// 可用的转换选项
const availableOptions = ref<ConvertFormatOption[]>([])

// 当前选中的转换选项
const selectedOption = computed(() => {
  return availableOptions.value.find((opt) => opt.value === formData.value.convertOption)
})

// 是否显示高级配置
const showAdvancedConfig = computed(() => {
  return isPdfToImage.value || isOfficeToPdf.value
})

// 是否是PDF转图片
const isPdfToImage = computed(() => {
  return selectedOption.value?.convertType === ConvertType.PDF_TO_IMAGE
})

// 是否是Office转PDF
const isOfficeToPdf = computed(() => {
  return selectedOption.value?.convertType === ConvertType.OFFICE_TO_PDF
})

/**
 * 监听源文件变化
 */
watch(
  () => props.sourceFileName,
  (newVal) => {
    if (newVal) {
      availableOptions.value = getAvailableConvertOptions(newVal)
    }
  },
  { immediate: true }
)

/**
 * 打开模态框
 */
function open() {
  visible.value = true
  formData.value.sourceFileId = props.sourceFileId || 0
  formData.value.convertOption = ''
  formData.value.targetFormat = '' as TargetFormat
  formRef.value?.resetFields()
}

/**
 * 处理转换选项变化
 */
function handleConvertOptionChange() {
  if (selectedOption.value) {
    formData.value.targetFormat = selectedOption.value.target
  }
}

/**
 * 提交表单
 */
async function handleSubmit() {
  try {
    await formRef.value.validate()

    loading.value = true
    await createConvertTask({
      sourceFileId: formData.value.sourceFileId,
      targetFormat: formData.value.targetFormat,
      config: formData.value.config,
    })

    message.success('转换任务创建成功')
    visible.value = false
    emit('success')
  } catch (error: any) {
    if (error.errorFields) {
      // 表单验证错误
      return
    }
    message.error('创建转换任务失败')
    console.error('创建转换任务失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 取消
 */
function handleCancel() {
  visible.value = false
  emit('cancel')
}

/**
 * 暴露方法
 */
defineExpose({
  open,
})
</script>

<style scoped lang="scss">
// 样式
</style>

