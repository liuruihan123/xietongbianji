<template>
  <a-modal
    :visible="open"
    title="邀请成员"
    :confirm-loading="loading"
    width="600px"
    @ok="handleSubmit"
    @cancel="handleCancel"
    okText="确定"
    cancelText="取消"
  >
    <a-form
      ref="formRef"
      :model="formData"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 18 }"
    >
      <!-- 搜索用户 -->
      <a-form-item label="搜索用户">
        <a-select
          v-model:value="formData.userId"
          show-search
          placeholder="请输入用户名或昵称搜索"
          :filter-option="false"
          :not-found-content="searching ? '搜索中...' : '暂无用户'"
          @search="handleSearch"
          @change="handleUserSelect"
        >
          <a-select-option
            v-for="user in userOptions"
            :key="user.userId"
            :value="user.userId"
          >
            <div class="user-option">
              <a-avatar :size="24" :style="{ backgroundColor: '#1890ff' }">
                {{ (user.nickName || user.userName)?.charAt(0) || 'U' }}
              </a-avatar>
              <div class="user-info">
                <div class="user-name">{{ user.nickName || user.userName }}</div>
                <div class="user-account">{{ user.userName }}</div>
              </div>
            </div>
          </a-select-option>
        </a-select>
      </a-form-item>

      <!-- 已选用户信息预览 -->
      <div v-if="selectedUser" class="selected-user-preview">
        <a-divider>已选择用户</a-divider>
        <div class="preview-card">
          <a-avatar :size="48" :style="{ backgroundColor: '#1890ff' }">
            {{ (selectedUser.nickName || selectedUser.userName)?.charAt(0) || 'U' }}
          </a-avatar>
          <div class="preview-info">
            <div class="preview-name">{{ selectedUser.nickName || selectedUser.userName }}</div>
            <div class="preview-account">{{ selectedUser.userName }}</div>
            <div v-if="selectedUser.email" class="preview-email">
              {{ selectedUser.email }}
            </div>
          </div>
        </div>
      </div>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { message } from 'ant-design-vue'
import { searchUsers, inviteMember } from '@/api/workspace'
import type { FormInstance } from 'ant-design-vue'

const props = defineProps<{
  visible: boolean
  workspaceId: number
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  success: []
}>()

// 内部状态，用于 a-modal 的 :visible
const open = ref(false)

const formRef = ref<FormInstance>()
const loading = ref(false)
const searching = ref(false)
const userOptions = ref<any[]>([])
const selectedUser = ref<any>(null)

const formData = reactive({
  userId: undefined as number | undefined,
  role: 'member' as string,
})

let searchTimeout: number | null = null

// 监听 visible prop 变化，同步到内部 open 状态
watch(() => props.visible, (val) => {
  open.value = val
  if (!val) {
    resetForm()
  }
})

// 监听内部 open 状态变化，同步到父组件
watch(open, (val) => {
  emit('update:visible', val)
})

function resetForm() {
  formRef.value?.resetFields()
  formData.userId = undefined
  userOptions.value = []
  selectedUser.value = null
}

/**
 * 搜索用户
 */
async function handleSearch(keyword: string) {
  if (!keyword.trim()) {
    userOptions.value = []
    return
  }

  // 防抖
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }

  searching.value = true

  searchTimeout = window.setTimeout(async () => {
    try {
      console.log('开始搜索用户, keyword:', keyword)
      const response = await searchUsers(keyword)
      console.log('搜索用户响应:', response)
      userOptions.value = response.rows || response.data?.rows || []
      console.log('userOptions:', userOptions.value)
    } catch (error) {
      console.error('搜索用户失败:', error)
      message.error('搜索用户失败')
    } finally {
      searching.value = false
    }
  }, 300)
}

/**
 * 选择用户
 */
function handleUserSelect(userId: number) {
  console.log('选择用户, userId:', userId)
  selectedUser.value = userOptions.value.find(u => u.userId === userId) || null
  console.log('selectedUser:', selectedUser.value)
}

/**
 * 提交邀请
 */
async function handleSubmit() {
  if (!formData.userId) {
    message.warning('请选择要邀请的用户')
    return
  }
  if (!props.workspaceId) {
    message.warning('工作空间ID不存在')
    return
  }

  loading.value = true
  try {
    await inviteMember({
      workspaceId: props.workspaceId,
      userId: formData.userId,
      role: 'member',
    })
    message.success('邀请成功')
    emit('success')
    handleCancel()
  } catch (error: any) {
    message.error(error.message || '邀请失败')
  } finally {
    loading.value = false
  }
}

/**
 * 取消
 */
function handleCancel() {
  open.value = false
}
</script>

<style scoped lang="scss">
.user-option {
  display: flex;
  align-items: center;
  gap: 12px;

  .user-info {
    .user-name {
      font-weight: 500;
      color: #333;
      font-size: 14px;
    }
    .user-account {
      font-size: 12px;
      color: #999;
      margin-top: 2px;
    }
  }
}

.selected-user-preview {
  .preview-card {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px;
    background: #f5f5f5;
    border-radius: 8px;

    .preview-info {
      .preview-name {
        font-weight: 600;
        color: #333;
        font-size: 16px;
      }
      .preview-account {
        font-size: 13px;
        color: #666;
        margin-top: 4px;
      }
      .preview-email {
        font-size: 12px;
        color: #999;
        margin-top: 2px;
      }
    }
  }
}
</style>
