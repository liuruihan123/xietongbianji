<template>
  <div class="workspace-card" @click="handleClick">
    <!-- 封面区域 -->
    <div class="card-cover" :class="`cover-type-${workspace.workspaceType}`">
      <img
        v-if="workspace.workspaceIcon"
        :src="workspace.workspaceIcon"
        :alt="workspace.workspaceName"
        class="cover-image"
      />
      <div v-else class="cover-placeholder"></div>

      <!-- 工作空间类型标签 -->
      <div class="workspace-type-badge">
        <template v-if="workspace.workspaceType === 1">
          <i class="fa fa-user"></i> 个人
        </template>
        <template v-else-if="workspace.workspaceType === 2">
          <i class="fa fa-building"></i> 部门
        </template>
        <template v-else-if="workspace.workspaceType === 3">
          <i class="fa fa-users"></i> 团队
        </template>
      </div>

      <!-- 操作按钮 -->
      <a-dropdown :trigger="['click']" placement="bottomRight" @click.stop>
        <div class="action-btn">
          <i class="fa fa-ellipsis-h"></i>
        </div>
        <template #overlay>
          <a-menu>
            <a-menu-item key="rename" @click="handleRename">
              <i class="fa fa-edit"></i> 重命名
            </a-menu-item>
            <a-menu-item key="edit" @click="handleEdit">
              <i class="fa fa-info-circle"></i> 编辑信息
            </a-menu-item>
            <a-menu-divider />
            <a-menu-item key="delete" danger @click="handleDelete">
              <i class="fa fa-trash"></i> 删除
            </a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
    </div>

    <!-- 信息区域 -->
    <div class="card-info">
      <!-- 工作空间名称 -->
      <div class="workspace-name">{{ workspace.workspaceName }}</div>

      <!-- 工作空间描述 -->
      <div class="workspace-desc">
        <span v-if="workspace.workspaceDesc" class="desc-text">{{ workspace.workspaceDesc }}</span>
        <span v-else class="desc-placeholder">还没有添加描述</span>
      </div>

      <!-- 默认空间标识 -->
      <div class="badges-container">
        <div v-if="workspace.isDefault" class="default-badge">
          <i class="fa fa-star"></i> 默认空间
        </div>
        <!-- 部门名称显示 -->
        <div v-if="workspace.workspaceType === 2 && workspace.deptName" class="dept-badge">
          <i class="fa fa-sitemap"></i> {{ workspace.deptName }}
        </div>
      </div>

      <!-- 底部信息 -->
      <div class="card-footer">
        <div class="footer-item">
          <i class="fa fa-user"></i>
          <span>{{ workspace.ownerNickName || workspace.ownerName || '未知' }}</span>
        </div>
        <div class="footer-item">
          <i class="fa fa-users"></i>
          <span>{{ workspace.memberCount || 0 }} 人</span>
        </div>
        <div class="footer-item">
          <i class="fa fa-file"></i>
          <span>{{ workspace.contentCount || 0 }} 项</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Workspace } from '@/types/workspace'

const props = defineProps<{
  workspace: Workspace
}>()

const emit = defineEmits<{
  click: [workspace: Workspace]
  share: [workspace: Workspace]
  rename: [workspace: Workspace]
  edit: [workspace: Workspace]
  delete: [workspace: Workspace]
}>()

/**
 * 格式化文件大小
 */
function formatSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i]
}

function handleClick() {
  emit('click', props.workspace)
}

function handleShare() {
  emit('share', props.workspace)
}

function handleRename() {
  emit('rename', props.workspace)
}

function handleEdit() {
  emit('edit', props.workspace)
}

function handleDelete() {
  emit('delete', props.workspace)
}
</script>

<style scoped lang="scss">
.workspace-card {
  width: 250px;
  height: 190px;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  border: 1px solid #e8e8e8;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    border-color: #1890ff;
  }

  .card-cover {
    position: relative;
    height: 110px;
    flex-shrink: 0;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

    &.cover-type-1 {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    &.cover-type-2 {
      background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
    }
    &.cover-type-3 {
      background: linear-gradient(135deg, #eb3349 0%, #f45c43 100%);
    }

    .cover-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .cover-placeholder {
      width: 100%;
      height: 100%;
    }

    .workspace-type-badge {
      position: absolute;
      top: 8px;
      left: 8px;
      display: inline-flex;
      align-items: center;
      gap: 4px;
      padding: 2px 8px;
      background: rgba(255, 255, 255, 0.9);
      color: #333;
      font-size: 12px;
      border-radius: 4px;
      font-weight: 500;

      i {
        font-size: 11px;
      }
    }

    .action-btn {
      position: absolute;
      top: 8px;
      right: 8px;
      width: 28px;
      height: 28px;
      border-radius: 4px;
      background: rgba(255, 255, 255, 0.9);
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.2s;
      color: #666;

      &:hover {
        background: white;
        color: #1890ff;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
      }

      i {
        font-size: 14px;
      }
    }
  }

  .card-info {
    flex: 1;
    padding: 12px;
    display: flex;
    flex-direction: column;
    gap: 6px;

    .workspace-name {
      font-size: 14px;
      font-weight: 600;
      color: #333;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      line-height: 1.4;
    }

    .workspace-desc {
      font-size: 12px;
      line-height: 1.4;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      margin-top: -2px;

      .desc-text {
        color: #666;
      }

      .desc-placeholder {
        color: #999;
        font-style: italic;
      }
    }

    .badges-container {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
    }

    .default-badge {
      display: inline-flex;
      align-items: center;
      gap: 4px;
      padding: 2px 8px;
      background: #e6f7ff;
      color: #1890ff;
      font-size: 12px;
      border-radius: 4px;
      width: fit-content;

      i {
        font-size: 10px;
      }
    }

    .dept-badge {
      display: inline-flex;
      align-items: center;
      gap: 4px;
      padding: 2px 8px;
      background: #f6ffed;
      color: #52c41a;
      font-size: 12px;
      border-radius: 4px;
      width: fit-content;

      i {
        font-size: 10px;
      }
    }

    .card-footer {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-top: auto;
      font-size: 12px;
      color: #666;
      gap: 8px;

      .footer-item {
        display: flex;
        align-items: center;
        gap: 4px;
        flex: 1;
        min-width: 0;

        i {
          flex-shrink: 0;
          color: #999;
          font-size: 12px;
        }

        span {
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          color: #666;
        }
      }
    }
  }
}
</style>
