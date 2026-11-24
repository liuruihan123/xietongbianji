<template>
  <div class="logs-page">
    <div class="page-header">
      <h2>操作日志</h2>
    </div>

    <!-- 工具栏 -->
    <div class="toolbar">
      <a-space>
        <a-input-search
          v-model:value="searchParams.username"
          placeholder="搜索用户名"
          style="width: 200px"
          @search="handleSearch"
        />

        <a-input
          v-model:value="searchParams.module"
          placeholder="模块"
          style="width: 150px"
          @press-enter="handleSearch"
        />

        <a-select
          v-model:value="searchParams.status"
          placeholder="状态"
          style="width: 120px"
          @change="handleSearch"
        >
          <a-select-option
            v-for="option in LOG_STATUS_OPTIONS"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </a-select-option>
        </a-select>

        <a-range-picker
          v-model:value="dateRange"
          :show-time="true"
          format="YYYY-MM-DD HH:mm:ss"
          @change="handleDateRangeChange"
        />

        <a-button @click="handleSearch">
          <template #icon><SearchOutlined /></template>
          搜索
        </a-button>

        <a-button @click="handleReset">
          <template #icon><ReloadOutlined /></template>
          重置
        </a-button>

        <a-popconfirm
          title="确定要清空所有日志吗?"
          ok-text="确定"
          cancel-text="取消"
          @confirm="handleClearLogs"
        >
          <a-button danger>
            <template #icon><DeleteOutlined /></template>
            清空日志
          </a-button>
        </a-popconfirm>
      </a-space>
    </div>

    <!-- 日志列表 -->
    <a-table
      :columns="columns"
      :data-source="logs"
      :loading="loading"
      :pagination="false"
      row-key="id"
      :scroll="{ x: 1500 }"
    >
      <!-- 状态 -->
      <template #status="{ record }">
        <a-tag :color="getLogStatusColor(record.status)">
          {{ getLogStatusText(record.status) }}
        </a-tag>
      </template>

      <!-- 操作时间 -->
      <template #createTime="{ record }">
        {{ formatDate(record.createTime) }}
      </template>

      <!-- 操作 -->
      <template #action="{ record }">
        <a-space>
          <a-button type="link" size="small" @click="handleViewDetail(record)">查看</a-button>
          <a-popconfirm
            title="确定要删除这条日志吗?"
            ok-text="确定"
            cancel-text="取消"
            @confirm="handleDelete(record)"
          >
            <a-button type="link" size="small" danger>删除</a-button>
          </a-popconfirm>
        </a-space>
      </template>
    </a-table>

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

    <!-- 详情模态框 -->
    <a-modal
      v-model:visible="detailVisible"
      title="日志详情"
      :footer="null"
      width="800px"
    >
      <a-descriptions v-if="currentLog" :column="2" bordered>
        <a-descriptions-item label="用户名">{{ currentLog.username }}</a-descriptions-item>
        <a-descriptions-item label="模块">{{ currentLog.module }}</a-descriptions-item>
        <a-descriptions-item label="操作">{{ currentLog.operation }}</a-descriptions-item>
        <a-descriptions-item label="请求方法">{{ currentLog.method }}</a-descriptions-item>
        <a-descriptions-item label="IP地址">{{ currentLog.ip }}</a-descriptions-item>
        <a-descriptions-item label="地理位置">{{ currentLog.location }}</a-descriptions-item>
        <a-descriptions-item label="浏览器">{{ currentLog.browser }}</a-descriptions-item>
        <a-descriptions-item label="操作系统">{{ currentLog.os }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="getLogStatusColor(currentLog.status)">
            {{ getLogStatusText(currentLog.status) }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="操作时间">
          {{ formatDate(currentLog.createTime) }}
        </a-descriptions-item>
        <a-descriptions-item label="请求参数" :span="2">
          <pre>{{ currentLog.params }}</pre>
        </a-descriptions-item>
        <a-descriptions-item label="返回结果" :span="2">
          <pre>{{ currentLog.result }}</pre>
        </a-descriptions-item>
        <a-descriptions-item v-if="currentLog.errorMsg" label="错误信息" :span="2">
          <pre class="error-msg">{{ currentLog.errorMsg }}</pre>
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { SearchOutlined, ReloadOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import {
  getOperationLogList,
  deleteOperationLog,
  clearOperationLogs,
} from '@/api/settings'
import {
  LogStatus,
  LOG_STATUS_OPTIONS,
  getLogStatusText,
  getLogStatusColor,
  type OperationLog,
} from '@/types/settings'
import dayjs, { type Dayjs } from 'dayjs'

// 状态
const loading = ref(false)
const logs = ref<OperationLog[]>([])
const total = ref(0)
const detailVisible = ref(false)
const currentLog = ref<OperationLog>()
const dateRange = ref<[Dayjs, Dayjs] | null>(null)

// 搜索参数
const searchParams = ref({
  page: 1,
  pageSize: 10,
  username: '',
  module: '',
  status: '' as LogStatus | '',
  startTime: '',
  endTime: '',
})

// 表格列配置
const columns = [
  {
    title: '用户名',
    dataIndex: 'username',
    key: 'username',
    width: 120,
  },
  {
    title: '模块',
    dataIndex: 'module',
    key: 'module',
    width: 120,
  },
  {
    title: '操作',
    dataIndex: 'operation',
    key: 'operation',
    width: 150,
  },
  {
    title: '请求方法',
    dataIndex: 'method',
    key: 'method',
    width: 120,
  },
  {
    title: 'IP地址',
    dataIndex: 'ip',
    key: 'ip',
    width: 140,
  },
  {
    title: '浏览器',
    dataIndex: 'browser',
    key: 'browser',
    width: 150,
    ellipsis: true,
  },
  {
    title: '操作系统',
    dataIndex: 'os',
    key: 'os',
    width: 120,
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
    slots: { customRender: 'status' },
  },
  {
    title: '操作时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
    slots: { customRender: 'createTime' },
  },
  {
    title: '操作',
    key: 'action',
    width: 150,
    fixed: 'right',
    slots: { customRender: 'action' },
  },
]

/**
 * 格式化日期
 */
function formatDate(date: string): string {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

/**
 * 加载日志列表
 */
async function loadLogs() {
  loading.value = true
  try {
    const params = {
      ...searchParams.value,
      status: searchParams.value.status || undefined,
    }
    const result = await getOperationLogList(params)
    logs.value = result.rows
    total.value = result.total
  } catch (error) {
    console.error('加载日志列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
function handleSearch() {
  searchParams.value.page = 1
  loadLogs()
}

/**
 * 重置
 */
function handleReset() {
  searchParams.value = {
    page: 1,
    pageSize: 10,
    username: '',
    module: '',
    status: '',
    startTime: '',
    endTime: '',
  }
  dateRange.value = null
  loadLogs()
}

/**
 * 日期范围变化
 */
function handleDateRangeChange(dates: [Dayjs, Dayjs] | null) {
  if (dates && dates.length === 2) {
    searchParams.value.startTime = dates[0].format('YYYY-MM-DD HH:mm:ss')
    searchParams.value.endTime = dates[1].format('YYYY-MM-DD HH:mm:ss')
  } else {
    searchParams.value.startTime = ''
    searchParams.value.endTime = ''
  }
}

/**
 * 分页变化
 */
function handlePageChange() {
  loadLogs()
}

/**
 * 查看详情
 */
function handleViewDetail(log: OperationLog) {
  currentLog.value = log
  detailVisible.value = true
}

/**
 * 删除日志
 */
async function handleDelete(log: OperationLog) {
  try {
    await deleteOperationLog(log.id)
    message.success('删除成功')
    loadLogs()
  } catch (error) {
    message.error('删除失败')
  }
}

/**
 * 清空日志
 */
async function handleClearLogs() {
  try {
    await clearOperationLogs()
    message.success('清空成功')
    loadLogs()
  } catch (error) {
    message.error('清空失败')
  }
}

/**
 * 组件挂载
 */
onMounted(() => {
  loadLogs()
})
</script>

<style scoped lang="scss">
.logs-page {
  padding: 24px;

  .page-header {
    margin-bottom: 24px;

    h2 {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
    }
  }

  .toolbar {
    margin-bottom: 16px;
  }

  .pagination {
    margin-top: 24px;
    text-align: right;
  }

  pre {
    margin: 0;
    padding: 8px;
    background-color: #f5f5f5;
    border-radius: 4px;
    max-height: 200px;
    overflow: auto;
    font-size: 12px;
    line-height: 1.5;

    &.error-msg {
      color: #ff4d4f;
      background-color: #fff2f0;
    }
  }
}
</style>

