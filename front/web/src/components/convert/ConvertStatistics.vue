<template>
  <a-row :gutter="16">
    <a-col :xs="24" :sm="12" :md="6">
      <a-card :loading="loading">
        <a-statistic
          title="总任务数"
          :value="statistics.totalTasks"
          :value-style="{ color: '#1890ff' }"
        >
          <template #prefix>
            <FileTextOutlined />
          </template>
        </a-statistic>
      </a-card>
    </a-col>

    <a-col :xs="24" :sm="12" :md="6">
      <a-card :loading="loading">
        <a-statistic
          title="成功任务"
          :value="statistics.successTasks"
          :value-style="{ color: '#52c41a' }"
        >
          <template #prefix>
            <CheckCircleOutlined />
          </template>
        </a-statistic>
      </a-card>
    </a-col>

    <a-col :xs="24" :sm="12" :md="6">
      <a-card :loading="loading">
        <a-statistic
          title="失败任务"
          :value="statistics.failedTasks"
          :value-style="{ color: '#ff4d4f' }"
        >
          <template #prefix>
            <CloseCircleOutlined />
          </template>
        </a-statistic>
      </a-card>
    </a-col>

    <a-col :xs="24" :sm="12" :md="6">
      <a-card :loading="loading">
        <a-statistic
          title="成功率"
          :value="statistics.successRate"
          :precision="1"
          suffix="%"
          :value-style="{ color: '#faad14' }"
        >
          <template #prefix>
            <RiseOutlined />
          </template>
        </a-statistic>
      </a-card>
    </a-col>
  </a-row>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  FileTextOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  RiseOutlined,
} from '@ant-design/icons-vue'
import { getConvertStatistics } from '@/api/convert'
import type { ConvertStatistics } from '@/types/convert'

/**
 * 组件事件
 */
const emit = defineEmits<{
  loaded: [statistics: ConvertStatistics]
}>()

// 状态
const loading = ref(false)
const statistics = ref<ConvertStatistics>({
  totalTasks: 0,
  successTasks: 0,
  failedTasks: 0,
  processingTasks: 0,
  todayTasks: 0,
  successRate: 0,
})

/**
 * 加载统计信息
 */
async function loadStatistics() {
  loading.value = true
  try {
    statistics.value = await getConvertStatistics()
    emit('loaded', statistics.value)
  } catch (error) {
    console.error('加载统计信息失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 组件挂载
 */
onMounted(() => {
  loadStatistics()
})

/**
 * 暴露方法
 */
defineExpose({
  reload: loadStatistics,
  statistics,
})
</script>

<style scoped lang="scss">
.ant-card {
  margin-bottom: 16px;
}
</style>

