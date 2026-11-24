<template>
  <a-modal
    v-model:visible="visible"
    title="分享统计"
    width="800px"
    :footer="null"
  >
    <a-spin :spinning="loading">
      <div v-if="statistics" class="statistics-content">
        <!-- 统计卡片 -->
        <a-row :gutter="16" class="stats-cards">
          <a-col :span="6">
            <a-card>
              <a-statistic
                title="总访问次数"
                :value="statistics.totalViews"
                :value-style="{ color: '#1890ff' }"
              >
                <template #prefix>
                  <EyeOutlined />
                </template>
              </a-statistic>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card>
              <a-statistic
                title="总下载次数"
                :value="statistics.totalDownloads"
                :value-style="{ color: '#52c41a' }"
              >
                <template #prefix>
                  <DownloadOutlined />
                </template>
              </a-statistic>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card>
              <a-statistic
                title="今日访问"
                :value="statistics.todayViews"
                :value-style="{ color: '#faad14' }"
              >
                <template #prefix>
                  <EyeOutlined />
                </template>
              </a-statistic>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card>
              <a-statistic
                title="今日下载"
                :value="statistics.todayDownloads"
                :value-style="{ color: '#13c2c2' }"
              >
                <template #prefix>
                  <DownloadOutlined />
                </template>
              </a-statistic>
            </a-card>
          </a-col>
        </a-row>

        <!-- 访问趋势图 -->
        <a-card title="访问趋势(最近7天)" class="trend-card">
          <div ref="viewTrendChart" style="height: 300px"></div>
        </a-card>

        <!-- 下载趋势图 -->
        <a-card title="下载趋势(最近7天)" class="trend-card">
          <div ref="downloadTrendChart" style="height: 300px"></div>
        </a-card>

        <!-- 访问地区分布 -->
        <a-card title="访问地区分布" class="location-card">
          <a-table
            :columns="locationColumns"
            :data-source="statistics.locationDistribution"
            :pagination="false"
            size="small"
          >
            <template #bodyCell="{ column, record, index }">
              <template v-if="column.key === 'index'">
                {{ index + 1 }}
              </template>
              <template v-else-if="column.key === 'percentage'">
                {{ getPercentage(record.count) }}%
              </template>
            </template>
          </a-table>
        </a-card>
      </div>
    </a-spin>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { message } from 'ant-design-vue'
import { EyeOutlined, DownloadOutlined } from '@ant-design/icons-vue'
import type { ShareInfo, ShareStatistics } from '@/types/share'
import { getShareStatistics } from '@/api/share'

const visible = ref(false)
const loading = ref(false)
const statistics = ref<ShareStatistics>()
const viewTrendChart = ref<HTMLElement>()
const downloadTrendChart = ref<HTMLElement>()

const locationColumns = [
  { title: '排名', key: 'index', width: 80 },
  { title: '地区', dataIndex: 'location' },
  { title: '访问次数', dataIndex: 'count', width: 120 },
  { title: '占比', key: 'percentage', width: 100 },
]

async function open(shareInfo: ShareInfo) {
  visible.value = true
  loading.value = true

  try {
    statistics.value = await getShareStatistics(shareInfo.id)
    
    nextTick(() => {
      renderCharts()
    })
  } catch (error) {
    message.error('获取统计数据失败')
  } finally {
    loading.value = false
  }
}

function renderCharts() {
  if (!statistics.value) return

  // 这里应该使用图表库(如ECharts)来渲染图表
  // 由于项目中可能没有安装图表库,这里只是占位
  console.log('渲染访问趋势图:', statistics.value.viewTrend)
  console.log('渲染下载趋势图:', statistics.value.downloadTrend)
  
  // 示例:使用ECharts渲染
  // import * as echarts from 'echarts'
  // const viewChart = echarts.init(viewTrendChart.value)
  // viewChart.setOption({
  //   xAxis: { type: 'category', data: statistics.value.viewTrend.map(d => d.date) },
  //   yAxis: { type: 'value' },
  //   series: [{ data: statistics.value.viewTrend.map(d => d.count), type: 'line' }]
  // })
}

function getPercentage(count: number): string {
  if (!statistics.value) return '0'
  const total = statistics.value.totalViews
  return total > 0 ? ((count / total) * 100).toFixed(1) : '0'
}

defineExpose({
  open,
})
</script>

<style scoped lang="scss">
.statistics-content {
  .stats-cards {
    margin-bottom: 24px;
  }

  .trend-card,
  .location-card {
    margin-bottom: 16px;

    &:last-child {
      margin-bottom: 0;
    }
  }
}
</style>

