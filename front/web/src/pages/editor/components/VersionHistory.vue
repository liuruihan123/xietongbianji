<template>
  <div class="version-history">
    <div class="header">
      <h3>版本历史</h3>
      <a-button type="primary" @click="handleCreateVersion">
        <PlusOutlined /> 创建版本
      </a-button>
    </div>

    <a-spin :spinning="loading" tip="加载中...">
      <a-empty v-if="!loading && versions.length === 0" description="暂无版本历史" />

      <a-timeline v-else>
        <a-timeline-item
          v-for="version in versions"
          :key="version.id"
          :color="version.isCurrent === 1 ? 'green' : 'blue'"
        >
          <template #dot>
            <CheckCircleOutlined v-if="version.isCurrent === 1" />
            <ClockCircleOutlined v-else />
          </template>

          <div class="version-item">
            <div class="version-header">
              <div class="version-info">
                <span class="version-number">版本 {{ version.versionNumber }}</span>
                <a-tag v-if="version.isCurrent === 1" color="green">
                  当前版本
                </a-tag>
                <a-tag v-if="version.isMajor === 1" color="blue">
                  主要版本
                </a-tag>
              </div>
              <div class="version-time">
                {{ formatDate(version.createTime) }}
              </div>
            </div>

            <div class="version-creator">
              <a-avatar size="small" :style="{ backgroundColor: '#1890ff' }">
                {{ version.createdName?.charAt(0) || 'U' }}
              </a-avatar>
              <span>{{ version.createdName || '未知用户' }}</span>
            </div>

            <div v-if="version.changeDescription" class="version-remark">
              {{ version.changeDescription }}
            </div>

            <div class="version-meta">
              <div class="version-size">
                文件大小: {{ formatSize(version.fileSize) }}
              </div>
              <div v-if="version.changeType" class="version-change-type">
                变更类型: {{ getChangeTypeText(version.changeType) }}
              </div>
            </div>

            <div class="version-actions">
              <a-space>
                <a-button
                  v-if="version.isCurrent !== 1"
                  type="link"
                  size="small"
                  @click="handleRestore(version)"
                >
                  <RollbackOutlined /> 恢复此版本
                </a-button>
                <a-button type="link" size="small" @click="handleDownload(version)">
                  <DownloadOutlined /> 下载
                </a-button>
                <a-popconfirm
                  v-if="version.isCurrent !== 1"
                  title="确定要删除此版本吗?"
                  ok-text="确定"
                  cancel-text="取消"
                  @confirm="handleDelete(version)"
                >
                  <a-button type="link" size="small" danger>
                    <DeleteOutlined /> 删除
                  </a-button>
                </a-popconfirm>
              </a-space>
            </div>
          </div>
        </a-timeline-item>
      </a-timeline>
    </a-spin>

    <!-- 创建版本对话框 -->
    <a-modal
      v-model:visible="showCreateModal"
      title="创建新版本"
      @ok="handleConfirmCreate"
    >
      <a-form :label-col="{ span: 4 }">
        <a-form-item label="版本备注">
          <a-textarea
            v-model:value="versionRemark"
            placeholder="请输入版本备注(可选)"
            :rows="4"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import {
  PlusOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  RollbackOutlined,
  DownloadOutlined,
  DeleteOutlined,
} from '@ant-design/icons-vue'
import {
  getVersionList,
  createVersion,
  rollbackVersion,
  deleteVersion,
  downloadVersionFile,
  type VersionInfo,
} from '@/api/version'
import dayjs from 'dayjs'

const props = defineProps<{
  fileId: number | string
  currentVersion?: number
}>()

const emit = defineEmits<{
  restore: [version: VersionInfo]
  refresh: []
}>()

const loading = ref(false)
const versions = ref<VersionInfo[]>([])
const showCreateModal = ref(false)
const versionRemark = ref('')
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)

// 计算当前版本号
const currentVersionNumber = computed(() => {
  const current = versions.value.find(v => v.isCurrent === 1)
  return current?.versionNumber
})

async function fetchVersions() {
  loading.value = true
  try {
    const response = await getVersionList(props.fileId, pageNum.value, pageSize.value)
    console.log('版本历史响应:', response)
    // 响应拦截器已经返回了data对象，不需要再访问.data
    versions.value = response.rows || []
    total.value = response.total || 0
    console.log('版本列表:', versions.value)
    console.log('版本总数:', total.value)
  } catch (error: any) {
    console.error('获取版本历史失败:', error)
    message.error(error.message || '获取版本历史失败')
  } finally {
    loading.value = false
  }
}

function handleCreateVersion() {
  showCreateModal.value = true
  versionRemark.value = ''
}

async function handleConfirmCreate() {
  try {
    await createVersion({
      fileId: props.fileId,
      versionDesc: versionRemark.value,
      changeType: 2, // 2-编辑
      isMajor: 0,
    })
    message.success('创建版本成功')
    showCreateModal.value = false
    await fetchVersions()
    emit('refresh')
  } catch (error: any) {
    console.error('创建版本失败:', error)
    message.error(error.message || '创建版本失败')
  }
}

async function handleRestore(version: VersionInfo) {
  try {
    console.log('开始版本恢复: versionId=', version.id)

    const response = await rollbackVersion(version.id)
    console.log('版本恢复响应:', response)

    // 获取后端返回的新documentKey（如果有）
    if ((response as any)?.documentKey) {
      console.log('后端返回新documentKey:', (response as any).documentKey)
    }

    message.success('恢复版本成功')

    // 重新加载版本列表
    await fetchVersions()

    // 通知父组件执行编辑器刷新
    emit('refresh')

  } catch (error: any) {
    console.error('恢复版本失败:', error)
    message.error(error.message || '恢复版本失败')
  }
}

async function handleDownload(version: VersionInfo) {
  try {
    // 使用带认证的下载函数
    await downloadVersionFile(version.id)
    message.success('下载成功')
  } catch (error: any) {
    console.error('下载版本失败:', error)
    message.error(error.message || '下载失败')
  }
}

async function handleDelete(version: VersionInfo) {
  if (version.isCurrent === 1) {
    message.warning('不能删除当前版本')
    return
  }

  try {
    await deleteVersion(version.id)
    message.success('删除版本成功')
    await fetchVersions()
  } catch (error: any) {
    console.error('删除版本失败:', error)
    message.error(error.message || '删除版本失败')
  }
}

function formatDate(date: string): string {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

function formatSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i]
}

function getChangeTypeText(changeType: number): string {
  const types: Record<number, string> = {
    1: '创建',
    2: '编辑',
    3: '上传',
    4: '替换',
  }
  return types[changeType] || '未知'
}

onMounted(() => {
  fetchVersions()
})

defineExpose({
  refresh: fetchVersions,
})
</script>

<style scoped lang="scss">
.version-history {
  padding: 24px;

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    h3 {
      margin: 0;
    }
  }

  .version-item {
    padding: 12px;
    background: #fafafa;
    border-radius: 8px;

    .version-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;

      .version-info {
        display: flex;
        align-items: center;
        gap: 8px;

        .version-number {
          font-weight: 600;
          font-size: 16px;
        }
      }

      .version-time {
        color: #999;
        font-size: 14px;
      }
    }

    .version-creator {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;
      font-size: 14px;
    }

    .version-remark {
      margin-bottom: 8px;
      padding: 8px;
      background: white;
      border-radius: 4px;
      font-size: 14px;
      color: #666;
    }

    .version-meta {
      margin-bottom: 12px;

      .version-change-type {
        font-size: 12px;
        color: #666;
        margin-top: 4px;
      }
    }


    .version-size {
      margin-bottom: 12px;
      font-size: 12px;
      color: #999;
    }

    .version-actions {
      border-top: 1px solid #e8e8e8;
      padding-top: 12px;
    }
  }
}
</style>

