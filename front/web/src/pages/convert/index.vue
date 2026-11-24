<template>
  <div class="convert-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>文档转换</h2>
    </div>

    <!-- 统计卡片 -->
    <ConvertStatistics ref="statisticsRef" @loaded="onStatisticsLoaded" />

    <!-- 工具栏 -->
    <div class="toolbar">
      <a-space>
        <a-button type="primary" @click="handleCreate">
          <template #icon><PlusOutlined /></template>
          创建转换任务
        </a-button>

        <a-input-search
          v-model:value="searchParams.sourceFileName"
          placeholder="搜索源文件名"
          style="width: 250px"
          @search="handleSearch"
        />

        <a-select
          v-model:value="searchParams.status"
          placeholder="任务状态"
          style="width: 120px"
          @change="handleSearch"
        >
          <a-select-option
            v-for="option in CONVERT_STATUS_OPTIONS"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </a-select-option>
        </a-select>

        <a-button @click="handleRefresh">
          <template #icon><ReloadOutlined /></template>
          刷新
        </a-button>
      </a-space>
    </div>

    <!-- 任务列表 -->
    <ConvertTaskList
      :tasks="tasks"
      :loading="loading"
      @refresh="loadTasks"
      @download="handleDownload"
      @retry="handleRetry"
      @cancel="handleCancel"
      @delete="handleDelete"
    />

    <!-- 分页 -->
    <div class="pagination">
      <a-pagination
        v-model:current="searchParams.page"
        v-model:page-size="searchParams.pageSize"
        :total="total"
        :show-size-changer="true"
        :show-total="(total) => `共 ${total} 条`"
        @change="handlePageChange"
      />
    </div>

    <!-- 创建转换任务模态框 -->
    <CreateConvertModal
      ref="createModalRef"
      :source-file-id="selectedFile?.id"
      :source-file-name="selectedFile?.name"
      @success="handleCreateSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { PlusOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import ConvertStatistics from '@/components/convert/ConvertStatistics.vue'
import ConvertTaskList from '@/components/convert/ConvertTaskList.vue'
import CreateConvertModal from '@/components/convert/CreateConvertModal.vue'
import { getConvertTaskList } from '@/api/convert'
import {
  ConvertStatus,
  CONVERT_STATUS_OPTIONS,
  type ConvertTask,
  type ConvertStatistics as ConvertStatisticsType,
} from '@/types/convert'

// 状态
const loading = ref(false)
const tasks = ref<ConvertTask[]>([])
const total = ref(0)
const statisticsRef = ref()
const createModalRef = ref()
const selectedFile = ref<{ id: number; name: string }>()

// 搜索参数
const searchParams = ref({
  page: 1,
  pageSize: 10,
  status: '' as ConvertStatus | '',
  sourceFileName: '',
})

// 轮询定时器
let pollingTimer: number | undefined

/**
 * 加载任务列表
 */
async function loadTasks() {
  loading.value = true
  try {
    const params = {
      ...searchParams.value,
      status: searchParams.value.status || undefined,
    }
    const result = await getConvertTaskList(params)
    tasks.value = result.rows
    total.value = result.total

    // 如果有转换中的任务,启动轮询
    const hasProcessingTasks = tasks.value.some((t) => t.status === ConvertStatus.PROCESSING)
    if (hasProcessingTasks) {
      startPolling()
    } else {
      stopPolling()
    }
  } catch (error) {
    console.error('加载任务列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 启动轮询
 */
function startPolling() {
  if (pollingTimer) return

  pollingTimer = window.setInterval(() => {
    loadTasks()
  }, 3000) // 每3秒轮询一次
}

/**
 * 停止轮询
 */
function stopPolling() {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = undefined
  }
}

/**
 * 统计信息加载完成
 */
function onStatisticsLoaded(statistics: ConvertStatisticsType) {
  console.log('统计信息:', statistics)
}

/**
 * 创建转换任务
 */
function handleCreate() {
  // 这里可以打开文件选择器,暂时留空
  selectedFile.value = undefined
  createModalRef.value?.open()
}

/**
 * 创建成功
 */
function handleCreateSuccess() {
  loadTasks()
  statisticsRef.value?.reload()
}

/**
 * 搜索
 */
function handleSearch() {
  searchParams.value.page = 1
  loadTasks()
}

/**
 * 刷新
 */
function handleRefresh() {
  loadTasks()
  statisticsRef.value?.reload()
}

/**
 * 分页变化
 */
function handlePageChange() {
  loadTasks()
}

/**
 * 下载
 */
function handleDownload(task: ConvertTask) {
  console.log('下载任务:', task)
}

/**
 * 重试
 */
function handleRetry(task: ConvertTask) {
  console.log('重试任务:', task)
}

/**
 * 取消
 */
function handleCancel(task: ConvertTask) {
  console.log('取消任务:', task)
}

/**
 * 删除
 */
function handleDelete(task: ConvertTask) {
  console.log('删除任务:', task)
}

/**
 * 组件挂载
 */
onMounted(() => {
  loadTasks()
})

/**
 * 组件卸载
 */
onUnmounted(() => {
  stopPolling()
})
</script>

<style scoped lang="scss">
.convert-page {
  padding: 24px;

  .page-header {
    margin-bottom: 24px;

    h2 {
      margin: 0;
      font-size: 24px;
      font-weight: 600;
    }
  }

  .toolbar {
    margin: 24px 0;
  }

  .pagination {
    margin-top: 24px;
    text-align: right;
  }
}
</style>

