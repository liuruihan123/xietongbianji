<template>
  <div class="system-page">
    <div class="page-header">
      <h2>系统配置</h2>
    </div>

    <a-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      :label-col="{ span: 4 }"
      :wrapper-col="{ span: 12 }"
    >
      <!-- 基本信息 -->
      <a-divider orientation="left">基本信息</a-divider>

      <a-form-item label="系统名称" name="systemName">
        <a-input v-model:value="formData.systemName" placeholder="请输入系统名称" />
      </a-form-item>

      <a-form-item label="系统Logo" name="logo">
        <a-input v-model:value="formData.logo" placeholder="请输入Logo URL" />
        <div v-if="formData.logo" class="image-preview">
          <img :src="formData.logo" alt="Logo" />
        </div>
      </a-form-item>

      <a-form-item label="网站图标" name="favicon">
        <a-input v-model:value="formData.favicon" placeholder="请输入Favicon URL" />
        <div v-if="formData.favicon" class="image-preview">
          <img :src="formData.favicon" alt="Favicon" />
        </div>
      </a-form-item>

      <a-form-item label="版权信息" name="copyright">
        <a-input v-model:value="formData.copyright" placeholder="请输入版权信息" />
      </a-form-item>

      <a-form-item label="ICP备案号" name="icp">
        <a-input v-model:value="formData.icp" placeholder="请输入ICP备案号" />
      </a-form-item>

      <!-- 主题配置 -->
      <a-divider orientation="left">主题配置</a-divider>

      <a-form-item label="主题色" name="primaryColor">
        <a-input v-model:value="formData.primaryColor" type="color" style="width: 100px" />
        <span style="margin-left: 12px">{{ formData.primaryColor }}</span>
      </a-form-item>

      <a-form-item label="布局模式" name="layout">
        <a-radio-group v-model:value="formData.layout">
          <a-radio
            v-for="option in LAYOUT_MODE_OPTIONS"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </a-radio>
        </a-radio-group>
      </a-form-item>

      <a-form-item label="固定头部" name="fixedHeader">
        <a-switch v-model:checked="formData.fixedHeader" />
      </a-form-item>

      <a-form-item label="固定侧边栏" name="fixedSidebar">
        <a-switch v-model:checked="formData.fixedSidebar" />
      </a-form-item>

      <!-- 功能开关 -->
      <a-divider orientation="left">功能开关</a-divider>

      <a-form-item label="启用注册" name="enableRegister">
        <a-switch v-model:checked="formData.enableRegister" />
      </a-form-item>

      <a-form-item label="启用验证码" name="enableCaptcha">
        <a-switch v-model:checked="formData.enableCaptcha" />
      </a-form-item>

      <a-form-item label="启用水印" name="enableWatermark">
        <a-switch v-model:checked="formData.enableWatermark" />
      </a-form-item>

      <!-- 操作按钮 -->
      <a-form-item :wrapper-col="{ offset: 4, span: 12 }">
        <a-space>
          <a-button type="primary" :loading="loading" @click="handleSubmit">保存</a-button>
          <a-button @click="handleReset">重置</a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { getSystemConfig, updateSystemConfig } from '@/api/settings'
import {
  LayoutMode,
  LAYOUT_MODE_OPTIONS,
  type SystemConfig,
  type UpdateSystemConfigParams,
} from '@/types/settings'

// 状态
const loading = ref(false)
const formRef = ref()

// 原始配置(用于重置)
const originalConfig = ref<SystemConfig>({
  systemName: '',
  logo: '',
  favicon: '',
  copyright: '',
  icp: '',
  primaryColor: '#1890ff',
  layout: LayoutMode.SIDE,
  fixedHeader: true,
  fixedSidebar: true,
  enableRegister: true,
  enableCaptcha: true,
  enableWatermark: false,
})

// 表单数据
const formData = reactive<SystemConfig>({
  systemName: '',
  logo: '',
  favicon: '',
  copyright: '',
  icp: '',
  primaryColor: '#1890ff',
  layout: LayoutMode.SIDE,
  fixedHeader: true,
  fixedSidebar: true,
  enableRegister: true,
  enableCaptcha: true,
  enableWatermark: false,
})

// 表单验证规则
const rules = {
  systemName: [{ required: true, message: '请输入系统名称', trigger: 'blur' }],
  primaryColor: [{ required: true, message: '请选择主题色', trigger: 'change' }],
}

/**
 * 加载系统配置
 */
async function loadSystemConfig() {
  try {
    const config = await getSystemConfig()
    originalConfig.value = { ...config }

    // 填充表单
    Object.assign(formData, config)
  } catch (error) {
    console.error('加载系统配置失败:', error)
  }
}

/**
 * 提交表单
 */
async function handleSubmit() {
  try {
    await formRef.value.validate()

    loading.value = true
    const params: UpdateSystemConfigParams = { ...formData }
    await updateSystemConfig(params)

    message.success('系统配置保存成功')
    await loadSystemConfig()
  } catch (error: any) {
    if (error.errorFields) {
      // 表单验证错误
      return
    }
    message.error('系统配置保存失败')
    console.error('系统配置保存失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 重置表单
 */
function handleReset() {
  Object.assign(formData, originalConfig.value)
  formRef.value?.clearValidate()
}

/**
 * 组件挂载
 */
onMounted(() => {
  loadSystemConfig()
})
</script>

<style scoped lang="scss">
.system-page {
  padding: 24px;

  .page-header {
    margin-bottom: 24px;

    h2 {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
    }
  }

  .image-preview {
    margin-top: 8px;

    img {
      max-width: 200px;
      max-height: 100px;
      border: 1px solid #d9d9d9;
      border-radius: 4px;
    }
  }
}
</style>

