<template>
  <a-modal
    v-model:visible="visible"
    :title="isEdit ? '编辑分享' : '创建分享'"
    width="600px"
    ok-text="确定"
    cancel-text="取消"
    @ok="handleSubmit"
    @cancel="handleCancel"
  >
    <a-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 18 }"
    >
      <!-- 文件信息 -->
      <a-form-item label="分享文件">
        <div class="file-info">
          <FileOutlined />
          <span>{{ fileName }}</span>
        </div>
      </a-form-item>

      <!-- 有效期 -->
      <a-form-item label="有效期" name="expireType">
        <a-select v-model:value="formData.expireType" :options="expireTypeOptions" />
      </a-form-item>

      <!-- 自定义过期时间 -->
      <a-form-item
        v-if="formData.expireType === ShareExpireType.CUSTOM"
        label="过期时间"
        name="expireTime"
      >
        <a-date-picker
          v-model:value="formData.expireTime"
          show-time
          format="YYYY-MM-DD HH:mm:ss"
          :disabled-date="disabledDate"
          style="width: 100%"
        />
      </a-form-item>

      <!-- 访问密码 -->
      <a-form-item label="访问密码">
        <a-input
          v-model:value="formData.sharePassword"
          placeholder="留空则无需密码"
          :maxlength="6"
          allow-clear
        >
          <template #suffix>
            <a-button type="link" size="small" @click="generatePassword">
              随机生成
            </a-button>
          </template>
        </a-input>
      </a-form-item>

      <!-- 权限设置 -->
      <a-form-item label="权限设置">
        <div class="permission-config">
          <!-- 快速模板 -->
          <div class="quick-templates">
            <a-space>
              <a-button size="small" @click="applyTemplate('READONLY')">
         只读
              </a-button>
              <a-button size="small" @click="applyTemplate('PREVIEW')">
                👁️ 预览
              </a-button>
              <a-button size="small" @click="applyTemplate('EDIT')">
                ✏️ 编辑
              </a-button>
              <a-button size="small" @click="applyTemplate('FULL')">
                ⭐ 完全权限
              </a-button>
            </a-space>
          </div>

          <!-- 自定义权限 -->
          <div class="custom-permissions" style="    margin-top: 29px;">
            <a-checkbox
              v-model:checked="formData.allowDownloadChecked"
              @change="handlePermissionChange"
            >
              允许下载
            </a-checkbox>
            <a-checkbox
              v-model:checked="formData.allowPrintChecked"
              @change="handlePermissionChange"
            >
              允许打印
            </a-checkbox>
            <a-checkbox
              v-model:checked="formData.allowCopyChecked"
              @change="handlePermissionChange"
            >
              允许复制
            </a-checkbox>
            <a-checkbox
              v-model:checked="formData.allowEditChecked"
              @change="handleEditChange"
            >
              允许编辑
            </a-checkbox>
            <!-- <a-checkbox
              v-model:checked="formData.allowCommentChecked"
              :disabled="!formData.allowEditChecked"
              @change="handlePermissionChange"
            >
              允许评论
            </a-checkbox> -->
          </div>

        
        </div>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { message } from 'ant-design-vue'
import { FileOutlined } from '@ant-design/icons-vue'
import type { FormInstance } from 'ant-design-vue'
import type { Dayjs } from 'dayjs'
import dayjs from 'dayjs'
import {
  SharePermission,
  ShareExpireType,
  EXPIRE_TYPE_OPTIONS,
  PermissionTemplate,
  getPermissionTemplateConfig,
} from '@/types/share'
import type { CreateShareRequest, ShareInfo } from '@/types/share'
import { createShare, updateShare } from '@/api/share'

const props = defineProps<{
  fileId: number
  fileName: string
  shareInfo?: ShareInfo
}>()

const emit = defineEmits<{
  success: [shareInfo: ShareInfo]
  cancel: []
}>()

const visible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const formData = reactive<CreateShareRequest & {
  expireTime?: Dayjs
  allowDownloadChecked?: boolean
  allowPrintChecked?: boolean
  allowCopyChecked?: boolean
  allowEditChecked?: boolean
  allowCommentChecked?: boolean
}>({
  fileId: props.fileId,
  permissions: [SharePermission.VIEW],
  expireType: ShareExpireType.SEVEN_DAYS,
  expireTime: undefined,
  sharePassword: '',
  // 权限字段默认值为1（允许）
  allowDownload: 1,
  allowPrint: 1,
  allowCopy: 1,
  allowEdit: 1,
  allowComment: 1,
  // 复选框状态
  allowDownloadChecked: true,
  allowPrintChecked: true,
  allowCopyChecked: true,
  allowEditChecked: true,
  allowCommentChecked: true,
})

const expireTypeOptions = EXPIRE_TYPE_OPTIONS

const rules = {
  expireType: [{ required: true, message: '请选择有效期' }],
  expireTime: [{ required: true, message: '请选择过期时间' }],
}

function open(shareInfo?: ShareInfo) {
  visible.value = true
  isEdit.value = !!shareInfo

  if (shareInfo) {
    formData.permissions = shareInfo.permissions
    formData.expireType = shareInfo.expireType
    formData.expireTime = shareInfo.expireTime ? dayjs(shareInfo.expireTime) : undefined
    formData.sharePassword = shareInfo.sharePassword || ''

    // 加载权限配置（默认值为1）
    formData.allowDownload = shareInfo.allowDownload ?? 1
    formData.allowPrint = shareInfo.allowPrint ?? 1
    formData.allowCopy = shareInfo.allowCopy ?? 1
    formData.allowEdit = shareInfo.allowEdit ?? 1
    formData.allowComment = shareInfo.allowComment ?? 1

    // 同步复选框状态
    formData.allowDownloadChecked = formData.allowDownload === 1
    formData.allowPrintChecked = formData.allowPrint === 1
    formData.allowCopyChecked = formData.allowCopy === 1
    formData.allowEditChecked = formData.allowEdit === 1
    formData.allowCommentChecked = formData.allowComment === 1
  } else {
    resetForm()
  }
}

function resetForm() {
  formData.permissions = [SharePermission.VIEW]
  formData.expireType = ShareExpireType.SEVEN_DAYS
  formData.expireTime = undefined
  formData.sharePassword = ''

  // 重置权限为默认值（全部允许）
  formData.allowDownload = 1
  formData.allowPrint = 1
  formData.allowCopy = 1
  formData.allowEdit = 1
  formData.allowComment = 1

  // 重置复选框状态
  formData.allowDownloadChecked = true
  formData.allowPrintChecked = true
  formData.allowCopyChecked = true
  formData.allowEditChecked = true
  formData.allowCommentChecked = true
}

function generatePassword() {
  const chars = 'abcdefghijklmnopqrstuvwxyz0123456789'
  let password = ''
  for (let i = 0; i < 6; i++) {
    password += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  formData.sharePassword = password
}

function disabledDate(current: Dayjs) {
  return current && current < dayjs().startOf('day')
}

// 应用权限模板
function applyTemplate(template: string) {
  const config = getPermissionTemplateConfig(template as PermissionTemplate)

  formData.allowDownload = config.allowDownload
  formData.allowPrint = config.allowPrint
  formData.allowCopy = config.allowCopy
  formData.allowEdit = config.allowEdit
  formData.allowComment = config.allowComment

  // 同步复选框状态
  formData.allowDownloadChecked = config.allowDownload === 1
  formData.allowPrintChecked = config.allowPrint === 1
  formData.allowCopyChecked = config.allowCopy === 1
  formData.allowEditChecked = config.allowEdit === 1
  formData.allowCommentChecked = config.allowComment === 1
}

// 权限变更处理
function handlePermissionChange() {
  // 同步复选框状态到权限字段
  formData.allowDownload = formData.allowDownloadChecked ? 1 : 0
  formData.allowPrint = formData.allowPrintChecked ? 1 : 0
  formData.allowCopy = formData.allowCopyChecked ? 1 : 0
  formData.allowEdit = formData.allowEditChecked ? 1 : 0
  formData.allowComment = formData.allowCommentChecked ? 1 : 0
}

// 编辑权限变更处理（联动评论权限）
function handleEditChange() {
  if (!formData.allowEditChecked) {
    // 禁止编辑时自动禁止评论
    formData.allowCommentChecked = false
  }
  handlePermissionChange()
}

async function handleSubmit() {
  try {
    await formRef.value?.validate()

    // 提交前同步一次勾选状态到数值字段（0/1）
    handlePermissionChange()

    console.log('========== 准备创建分享 ==========')
    console.log('表单数据 formData:', {
      expireType: formData.expireType,
      expireTime: formData.expireTime?.format('YYYY-MM-DD HH:mm:ss'),
      sharePassword: formData.sharePassword,
      allowDownload: formData.allowDownload,
      allowPrint: formData.allowPrint,
      allowCopy: formData.allowCopy,
      allowEdit: formData.allowEdit,
      allowComment: formData.allowComment,
    })

    // 根据编辑模式分别构造请求数据，避免类型转换错误
    const result = isEdit.value
      ? await updateShare({
          id: props.shareInfo!.id,
          permissions: formData.permissions,
          expireType: formData.expireType,
          // 只有在选择自定义有效期时才发送 expireTime
          expireTime: formData.expireType === ShareExpireType.CUSTOM
            ? formData.expireTime?.format('YYYY-MM-DD HH:mm:ss')
            : undefined,
          // 只有在密码不为空时才发送 sharePassword
          sharePassword: formData.sharePassword?.trim() || undefined,
          // 细粒度权限
          allowDownload: formData.allowDownload,
          allowPrint: formData.allowPrint,
          allowCopy: formData.allowCopy,
          allowEdit: formData.allowEdit,
          allowComment: formData.allowComment,
        })
      : await createShare({
          fileId: props.fileId,
          permissions: formData.permissions,
          expireType: formData.expireType,
          // 只有在选择自定义有效期时才发送 expireTime
          expireTime: formData.expireType === ShareExpireType.CUSTOM
            ? formData.expireTime?.format('YYYY-MM-DD HH:mm:ss')
            : undefined,
          // 只有在密码不为空时才发送 sharePassword
          sharePassword: formData.sharePassword?.trim() || undefined,
          // 细粒度权限
          allowDownload: formData.allowDownload,
          allowPrint: formData.allowPrint,
          allowCopy: formData.allowCopy,
          allowEdit: formData.allowEdit,
          allowComment: formData.allowComment,
        })

    console.log('========== API 返回结果 ==========')
    console.log('完整返回数据:', JSON.stringify(result, null, 2))
    console.log('result 类型:', typeof result)
    console.log('result.data 存在吗?', 'data' in result)

    // 检查返回数据结构
    const shareInfo: ShareInfo = result.data as ShareInfo
    console.log('实际的 ShareInfo:', JSON.stringify(shareInfo, null, 2))
    console.log('返回的 password 字段:', shareInfo.sharePassword)
    console.log('返回的 expireTime 字段:', shareInfo.expireTime)

    message.success(isEdit.value ? '分享更新成功' : '分享创建成功')
    emit('success', shareInfo)
    visible.value = false
  } catch (error) {
    console.error('========== 创建分享失败 ==========')
    console.error('错误详情:', error)
  }
}

function handleCancel() {
  visible.value = false
  emit('cancel')
}

defineExpose({
  open,
})
</script>

<style scoped lang="scss">
.file-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #f5f5f5;
  border-radius: 4px;
}
</style>

