<template>
  <div class="editor-toolbar">
    <div class="toolbar-left">
      <a-button type="text" @click="$emit('back')">
        <ArrowLeftOutlined /> 返回
      </a-button>
      <a-divider type="vertical" />

      <!-- 可编辑的文档标题 -->
      <div class="document-title-wrapper">
        <a-typography-title
          :level="5"
          :content="fileNameWithoutExtension"
          :editable="{
            tooltip: '点击编辑文件名',
            triggerType: ['icon', 'text'],
            onChange: handleFileNameChangeAndRename,
          }"
          class="document-title"
        />
        <span v-if="fileExtension" class="file-extension">{{ fileExtension }}</span>
      </div>

      <!-- 收藏按钮 -->
      <a-button
        type="text"
        :loading="favoriteLoading"
        @click="handleToggleFavorite"
        class="favorite-btn"
      >
        <StarFilled v-if="isFavorited" class="star-icon favorited" />
        <StarOutlined v-else class="star-icon" />
      </a-button>
    </div>

    <div class="toolbar-right">
      <a-space>
        <!-- 最后保存时间 -->
        <span v-if="lastSaveTimeValue" class="last-save-time">
          <CheckCircleOutlined class="save-icon" />
          {{ formatSaveTime(lastSaveTimeValue) }}
        </span>

        <!-- 在线用户 -->
        <a-dropdown>
          <a-button>
            <TeamOutlined /> 在线 ({{ onlineUserCountValue }})
          </a-button>
          <template #overlay>
            <div style="width: 300px; max-height: 400px; overflow-y: auto; background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.15);">
              <OnlineUsers
                :users="onlineUsersValue"
                :current-user-id="currentUserId"
              />
            </div>
          </template>
        </a-dropdown>

        <!-- 版本历史按钮 -->
        <a-button @click="$emit('show-version-history')">
          <HistoryOutlined /> 版本历史
        </a-button>

        <!-- 邀请协作按钮 -->
        <!-- <a-button type="primary" @click="$emit('invite-collaboration')">
          <UserAddOutlined /> 邀请协作
        </a-button> -->

        <!-- 分享按钮 -->
        <a-button type="primary" @click="$emit('share')">
          <ShareAltOutlined /> 分享
        </a-button>
      </a-space>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  ArrowLeftOutlined,
  ShareAltOutlined,
  CheckCircleOutlined,
  TeamOutlined,
  StarOutlined,
  StarFilled,
  UserAddOutlined,
  HistoryOutlined,
} from '@ant-design/icons-vue'
import OnlineUsers from '@/components/collaboration/OnlineUsers.vue'
import { renameFile } from '@/api/file'
import { addFavorite, removeFavorite, checkFavorite } from '@/api/favorite'
import { FavoriteTargetType } from '@/types/favorite'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

// Props 定义
interface Props {
  documentTitle: string
  fileId: number | string
  workspaceId?: number | string
  autoSave: any  // 接受useAutoSave的返回值
  collaboration: any  // 接受useCollaboration的返回值
  currentUserId: number
}

const props = defineProps<Props>()

// 事件定义
const emit = defineEmits<{
  back: []
  share: []
  'invite-collaboration': []
  'title-changed': [newTitle: string]
  'show-version-history': []
}>()

// 状态
const isFavorited = ref(false)
const favoriteId = ref<number | string | null>(null) // 存储收藏ID用于取消收藏
const favoriteLoading = ref(false)
const isRenaming = ref(false) // 防止重复提交

// 计算属性:从autoSave和collaboration中提取值
const lastSaveTimeValue = computed(() => {
  // autoSave.lastSaveTime 是一个 Ref<Date | null>
  return props.autoSave?.lastSaveTime?.value || props.autoSave?.lastSaveTime
})

const onlineUserCountValue = computed(() => {
  // collaboration.onlineUserCount 是一个 ComputedRef<number>
  const count = props.collaboration?.onlineUserCount?.value ?? props.collaboration?.onlineUserCount ?? 0
  console.log('EditorHeader - 在线用户数量计算:', {
    collaboration: !!props.collaboration,
    onlineUserCount: props.collaboration?.onlineUserCount,
    onlineUserCountValue: props.collaboration?.onlineUserCount?.value,
    finalCount: count
  })
  return count
})

const onlineUsersValue = computed(() => {
  // collaboration.onlineUsers 是一个 Ref<OnlineUser[]>
  const users = props.collaboration?.onlineUsers?.value || props.collaboration?.onlineUsers || []
  console.log('EditorHeader - 在线用户列表计算:', {
    collaboration: !!props.collaboration,
    onlineUsers: props.collaboration?.onlineUsers,
    onlineUsersValue: props.collaboration?.onlineUsers?.value,
    finalUsers: users,
    userCount: users.length
  })
  return users
})

// 计算属性:分离文件名和扩展名
const fileNameWithoutExtension = computed(() => {
  console.log('EditorHeader - documentTitle prop:', props.documentTitle)
  const lastDotIndex = props.documentTitle.lastIndexOf('.')
  if (lastDotIndex === -1) {
    return props.documentTitle
  }
  return props.documentTitle.substring(0, lastDotIndex)
})

const fileExtension = computed(() => {
  const lastDotIndex = props.documentTitle.lastIndexOf('.')
  if (lastDotIndex === -1) {
    return ''
  }
  return props.documentTitle.substring(lastDotIndex)
})

// 格式化保存时间
function formatSaveTime(time: Date): string {
  const now = dayjs()
  const saveTime = dayjs(time)
  const diffMinutes = now.diff(saveTime, 'minute')
  const diffHours = now.diff(saveTime, 'hour')
  const diffDays = now.diff(saveTime, 'day')

  if (diffMinutes < 1) {
    return '刚刚'
  } else if (diffMinutes < 60) {
    return `${diffMinutes}分钟前`
  } else if (diffHours < 24) {
    return `${diffHours}小时前`
  } else if (diffDays === 0) {
    return `今天 ${saveTime.format('HH:mm')}`
  } else if (diffDays === 1) {
    return `昨天 ${saveTime.format('HH:mm')}`
  } else {
    return saveTime.format('YYYY-MM-DD HH:mm')
  }
}

// 文件名改变并重命名（onChange 在确认时触发：回车或失焦）
async function handleFileNameChangeAndRename(newNameWithoutExt: string) {
  console.log('handleFileNameChangeAndRename 被调用:', newNameWithoutExt)

  // 防止重复提交
  if (isRenaming.value) {
    console.log('正在重命名中，跳过本次请求')
    return
  }

  if (!newNameWithoutExt || !newNameWithoutExt.trim()) {
    message.error('文件名不能为空')
    return
  }

  const trimmedName = newNameWithoutExt.trim()

  // 检查文件名是否包含非法字符
  const invalidChars = /[\\/:*?"<>|]/
  if (invalidChars.test(trimmedName)) {
    message.error('文件名不能包含以下字符: \\ / : * ? " < > |')
    return
  }

  // 拼接扩展名,得到完整的新文件名
  const fullNewName = trimmedName + fileExtension.value

  // 如果文件名没有改变,不需要调用API
  if (fullNewName === props.documentTitle) {
    return
  }

  try {
    isRenaming.value = true
    console.log('重命名文件:', {
      fileId: props.fileId,
      oldName: props.documentTitle,
      newName: fullNewName,
    })

    // 调用重命名API
    await renameFile({
      id: props.fileId,
      name: fullNewName,
    })

    message.success('文件重命名成功')

    // 通知父组件标题已更改
    emit('title-changed', fullNewName)
  } catch (error: any) {
    console.error('重命名文件失败:', error)
    message.error(error.message || '重命名文件失败')
  } finally {
    isRenaming.value = false
  }
}

// 切换收藏状态
async function handleToggleFavorite() {
  if (!props.workspaceId) {
    message.warning('缺少工作空间信息,无法收藏')
    return
  }

  console.log('[EditorHeader] 切换收藏触发', { fileId: props.fileId, workspaceId: props.workspaceId, isFavorited: isFavorited.value, favoriteId: favoriteId.value })
  favoriteLoading.value = true
  try {
    if (isFavorited.value && favoriteId.value) {
      // 取消收藏 - 弹出确认提示
      const confirmed = window.confirm('是否取消收藏该文件？')
      if (!confirmed) {
        favoriteLoading.value = false
        console.log('[EditorHeader] 用户取消了取消收藏确认')
        return
      }

      await removeFavorite(favoriteId.value)
      isFavorited.value = false
      favoriteId.value = null
      console.log('[EditorHeader] 取消收藏成功')
      message.success('已取消收藏')
    } else {
      // 添加收藏 - 保持原始ID格式,避免大整数精度丢失
      await addFavorite({
        targetType: FavoriteTargetType.FILE,
        targetId: props.fileId,
        workspaceId: props.workspaceId,
      })
      console.log('[EditorHeader] 添加收藏成功, 将检查收藏状态')
      // 重新检查收藏状态以获取favoriteId
      await checkFavoriteStatus()
      message.success('已添加到收藏')
    }
  } catch (error: any) {
    console.error('收藏操作失败:', error)
    message.error(error.message || '收藏操作失败')
  } finally {
    favoriteLoading.value = false
  }
}

// 检查收藏状态
async function checkFavoriteStatus() {
  try {
    console.log('[EditorHeader] 开始检查收藏状态', { fileId: props.fileId })
    // 使用 checkFavorite API 查询收藏状态
    const response = await checkFavorite({
      targetType: FavoriteTargetType.FILE,
      targetId: props.fileId,
    })
    console.log('[EditorHeader] checkFavorite 响应:', response)

    const result = response.data
    if (result && result.isFavorited && result.favoriteId) {
      // 文件已被收藏
      isFavorited.value = true
      favoriteId.value = result.favoriteId
      console.log('[EditorHeader] 检查结果：已收藏', { favoriteId: favoriteId.value })
    } else {
      // 文件未被收藏
      isFavorited.value = false
      favoriteId.value = null
      console.log('[EditorHeader] 检查结果：未收藏')
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error)
    // 出错时默认为未收藏
    isFavorited.value = false
    favoriteId.value = null
  }
}

// 组件挂载时检查收藏状态
onMounted(() => {
  checkFavoriteStatus()
})
</script>

<style scoped lang="scss">
.editor-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  z-index: 10;
  flex-shrink: 0; // 防止工具栏被压缩

  .toolbar-left {
    display: flex;
    align-items: center;
    gap: 12px;

    .document-title-wrapper {
      display: flex;
      align-items: center;
      gap: 4px;

      .document-title {
        margin: 0;
        font-size: 16px;
        font-weight: 600;

        :deep(.ant-typography) {
          margin: 0;
        }
      }

      .file-extension {
        font-size: 16px;
        font-weight: 600;
        color: #666;
      }
    }

    .favorite-btn {
      padding: 4px 8px;
      border-radius: 4px;
      transition: all 0.3s;

      &:hover {
        background-color: #fff7e6;
      }

      .star-icon {
        font-size: 18px;
        color: #d9d9d9;
        transition: all 0.3s;

        &.favorited {
          color: #faad14;
        }

        &:hover {
          color: #faad14;
        }
      }

      // 已收藏状态的按钮样式
      &:has(.star-icon.favorited) {
        background-color: #fff7e6;

        &:hover {
          background-color: #ffe7ba;
        }
      }
    }
  }

  .toolbar-right {
    .last-save-time {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 13px;
      color: #666;

      .save-icon {
        color: #52c41a;
        font-size: 14px;
      }
    }
  }
}
</style>

