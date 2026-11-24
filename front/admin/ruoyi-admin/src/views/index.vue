<template>
  <div class="dashboard">
    <!-- 顶部卡片 -->
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="12" :lg="6" :xl="6" v-for="(item, index) in headerCards" :key="index">
        <el-card class="header-card" shadow="hover">
          <div class="card-header">
            <div class="card-left">
              <span class="card-title">{{ item.title }}</span>
              <el-tooltip content="指标说明">
                <i class="el-icon-info"></i>
              </el-tooltip>
            </div>
            <div class="card-right">
              <el-tag size="small" type="primary">{{ currentDateRange }}</el-tag>
            </div>
          </div>
          <div class="card-main">
            <!--头部-->
            <div class="card-value-container" v-if="item.chartType==='line'" style="display: flex; justify-content: space-between;">
              <div class="card-value">¥ {{ item.rmb }}</div>
              <div class="card-value">$ {{ item.usd }}</div>
            </div>
            <div class="card-value" v-else>{{ item.value }}</div>
            <!--中间-->
            <!-- <div class="currency-info" v-if="item.chartType==='line'">
              <span v-for="(currency, idx) in item.currencies" :key="idx" class="currency-item">
                {{ currency.name }}
                <span :class="['trend', currency.trend > 0 ? 'up' : 'down']">
                  {{ currency.value }}
                  <i :class="['el-icon-' + (currency.trend > 0 ? 'top' : 'bottom')]"></i>
                </span>
              </span>
            </div> -->
            <div class="mini-chart" :ref="'miniChart' + index"></div>
          </div>
          <!--底部-->

          <div class="sub-value">
            {{ item.subTitle }}
            <span v-if="item.chartType === 'line'">
              <span class="currency-rmb">¥{{ item.subValue.rmb }}</span>
              <span class="currency-divider">/</span>
              <span class="currency-usd">${{ item.subValue.usd }}</span>
            </span>
            <span v-else>{{ item.subValue }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :sm="24" :md="24" :lg="16" :xl="16">
        <el-card class="main-chart-card">
          <div class="chart-header">
            <div class="left">
              <el-tabs v-model="activeTab">
                <el-tab-pane label="销售额" name="sales"></el-tab-pane>
                <!-- <el-tab-pane label="对话数量" name="visits"></el-tab-pane> -->
              </el-tabs>
            </div>
            <div class="right">
              <el-radio-group v-model="timeRange" size="small" @change="handleTimeRangeChange">
                <el-radio-button label="today">今日</el-radio-button>
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
                <el-radio-button label="year">全年</el-radio-button>
              </el-radio-group>
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                size="small"
                value-format="YYYY-MM-DD"
                @change="handleDateRangeChange"
              ></el-date-picker>
            </div>
          </div>
          <div ref="mainChart" style="height: 350px"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="24" :lg="8" :xl="8">
        <el-card class="rank-card">
          <div class="chart-header">
            <span class="title">会员消费排行(统一换算人民币 )</span>
          </div>
          <div class="rank-list">
            <div v-for="(item, index) in rankList" :key="index" class="rank-item">
              <span :class="['rank-no', index < 3 ? 'top-' + (index + 1) : '']">
                {{ index + 1 }}
              </span>
              <span class="rank-name">{{ item.userName }}</span>
              <span class="rank-value">{{ item.totalAmount }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getPanelData } from '@/api/tool/panl';

export default {
  data() {
    return {
      headerCards: [],
      rankList: [],
      activeTab: 'sales',
      timeRange: 'month',
      dateRange: [],
      loading: false
    }
  },

  computed: {
    currentDateRange() {
      if (this.dateRange && this.dateRange.length === 2) {
        return `${this.dateRange[0]} 至 ${this.dateRange[1]}`;
      }

      switch(this.timeRange) {
        case 'today':
          return '今日';
        case 'week':
          return '本周';
        case 'month':
          return '本月';
        case 'year':
          return '全年';
        default:
          return '今日';
      }
    }
  },

  methods: {
    // 获取仪表盘数据
    async fetchDashboardData() {
      this.loading = true;
      try {
        let params = {
          type: this.timeRange
        };

        // 如果是日期选择器选择的时间范围
        if (this.dateRange && this.dateRange.length === 2) {
          params = {
            type: 'free',
            beginTime: this.dateRange[0],
            endTime: this.dateRange[1]
          };
        }

        const { data } = await getPanelData(params);
        this.transformDashboardData(data);
      } catch (error) {
        console.error('获取仪表盘数据失败:', error);
        // 可以添加错误提示
        this.$message.error('获取数据失败');
      } finally {
        this.loading = false;
      }
    },

    // 转换接口数据为组件所需格式
    transformDashboardData(data) {
      console.log(data);
      this.headerCards = [
        {
          title: '总销售额',
          value: `¥ ${data.sales.total.rmb}`,
          rmb: data.sales.total.rmb,
          usd: data.sales.total.usd,
          subTitle: '今日所得',
          subValue: {
            rmb: data.sales.today.rmb,
            usd: data.sales.today.usd
          },
          chartType: 'line',
          chartColor: '#FF8C00',
          chartData: {
            dates: data.sales.chartData.dates,
            rmb: data.sales.chartData.rmb,
            usd: data.sales.chartData.usd
          }
        },
        {
          title: '会员数',
          value: data.members.total,
          subTitle: '付费会员',
          subValue: data.members.paying,
          chartType: 'area',
          chartColor: '#975FE4',
          chartData: {
            dates: data.members.chartData.dates,
            values: data.members.chartData.values
          }
        },
        {
          title: '对话数量',
          value: data.conversations.total,
          subTitle: '今日对话',
          subValue: data.conversations.today,
          chartType: 'bar',
          chartColor: '#1890FF',
          chartData: {
            dates: data.conversations.chartData.dates,
            values: data.conversations.chartData.values
          }
        },
        {
          title: 'Token消耗',
          value: data.tokens.total,
          subTitle: '今日消耗',
          subValue: data.tokens.today,
          chartType: 'bar',
          chartColor: '#1890FF',
          chartData: {
            dates: data.tokens.chartData.dates,
            values: data.tokens.chartData.values
          }
        }
      ];

      this.rankList = data.rankList;

      // 初始化图表
      this.$nextTick(() => {
        this.initMiniCharts();
        this.initMainChart();
      });
    },

    // 处理时间范围变化
    handleTimeRangeChange(value) {
      this.dateRange = [];
      this.fetchDashboardData();
    },

    // 处理日期范围变化
    handleDateRangeChange(value) {
      if (value) {
        this.timeRange = '';
        this.fetchDashboardData();
      }
    },

    handleResize() {
      // 重新初始化图表大小
      this.$nextTick(() => {
        this.initMiniCharts()
        this.initMainChart()
      })
    },

    // 初始化小图表
    initMiniCharts() {
      this.headerCards.forEach((item, index) => {
        const chartDom = this.$refs[`miniChart${index}`][0];
        const chart = echarts.init(chartDom);

        let option;
        switch(item.chartType) {
          case 'line':
            option = {
              tooltip: {
                trigger: 'axis',
                formatter: function(params) {
                  const date = params[0].axisValue;
                  return `${date}<br/>` + params.map(param => {
                    const currency = param.seriesIndex === 0 ? '¥' : '$';
                    return `${param.seriesName}: ${currency}${param.data}`;
                  }).join('<br/>');
                }
              },
              grid: { top: 8, right: 8, bottom: 8, left: 8 },
              xAxis: {
                show: false,
                type: 'category',
                data: item.chartData.dates
              },
              yAxis: { show: false, type: 'value' },
              series: [
                {
                  name: 'RMB',
                  data: item.chartData.rmb,
                  type: 'line',
                  smooth: true,
                  symbolSize: 0,
                  lineStyle: { color: '#FF8C00', width: 2 }
                },
                {
                  name: 'USD',
                  data: item.chartData.usd,
                  type: 'line',
                  smooth: true,
                  symbolSize: 0,
                  lineStyle: { color: '#1890FF', width: 2 }
                }
              ]
            };
            break;
          case 'area':
            option = {
              tooltip: {
                trigger: 'axis',
                formatter: function(params) {
                  return `${params[0].axisValue}<br/>会员数: ${params[0].data}`;
                }
              },
              grid: { top: 8, right: 8, bottom: 8, left: 8 },
              xAxis: {
                show: false,
                type: 'category',
                data: item.chartData.dates
              },
              yAxis: { show: false, type: 'value' },
              series: [{
                data: item.chartData.values,
                type: 'line',
                smooth: true,
                areaStyle: {
                  color: {
                    type: 'linear',
                    x: 0, y: 0, x2: 0, y2: 1,
                    colorStops: [
                      { offset: 0, color: item.chartColor || '#975FE4' },
                      { offset: 1, color: 'rgba(151, 95, 228, 0.2)' }
                    ]
                  }
                },
                lineStyle: { width: 0 },
                symbol: 'none'
              }]
            };
            break;
          case 'bar':
            option = {
              tooltip: {
                trigger: 'axis',
                formatter: function(params) {
                  const title = item.title === 'Token消耗' ? 'Token消耗' : '对话数量';
                  return `${params[0].axisValue}<br/>${title}: ${params[0].data}`;
                }
              },
              grid: { top: 8, right: 8, bottom: 8, left: 8 },
              xAxis: {
                show: false,
                type: 'category',
                data: item.chartData.dates
              },
              yAxis: { show: false, type: 'value' },
              series: [{
                data: item.chartData.values,
                type: 'bar',
                barWidth: 4,
                itemStyle: {
                  color: item.chartColor || '#1890FF',
                  borderRadius: [2, 2, 0, 0]
                }
              }]
            };
            break;
        }

        if (option) {
          chart.setOption(option);
        }
      });
    },

    // 初始化主图表
    initMainChart() {
      const chartDom = this.$refs.mainChart;
      const chart = echarts.init(chartDom);

      const option = {
        tooltip: {
          trigger: 'axis',
          formatter: (params) => {
            const date = params[0].axisValue;
            if (this.activeTab === 'sales') {
              return `${date}<br/>RMB: ¥${params[0].data}<br/>USD: $${params[1].data}`;
            }
            return `${date}<br/>销售额: ${params[0].data}`;
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: this.headerCards[0].chartData.dates // 使用第一个卡片的日期数据
        },
        yAxis: {
          type: 'value'
        },
        series: this.getMainChartSeries()
      };

      chart.setOption(option);
    },

    // 获取主图表的系列数据
    getMainChartSeries() {
      if (this.activeTab === 'sales') {
        return [
          {
            name: 'RMB',
            type: 'line',
            smooth: true,
            data: this.headerCards[0].chartData.rmb,
            itemStyle: { color: '#FF8C00' }
          },
          {
            name: 'USD',
            type: 'line',
            smooth: true,
            data: this.headerCards[0].chartData.usd,
            itemStyle: { color: '#1890FF' }
          }
        ];
      } else {
        return [{
          type: 'line',
          smooth: true,
          data: this.headerCards[2].chartData.values, // 使用对话数量的数据
          itemStyle: { color: '#1890FF' }
        }];
      }
    },

    // 格式化日期
    formatDate(date) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    }
  },

  mounted() {
    this.fetchDashboardData();
    window.addEventListener('resize', this.handleResize);
  },

  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize);
  }
}
</script>

<style scoped>
.dashboard {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: 100vh;
}

.header-card {
  margin-bottom: 20px;
  height: auto;
  min-height: 180px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.card-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-right {
  .el-tag {
    margin-left: 8px;
    min-width: 80px;
    text-align: center;
  }
}

.card-title {
  color: rgba(0, 0, 0, 0.45);
  font-size: 14px;
}

.card-main {
  padding: 8px 0;
}

.card-value {
  font-size: 30px;
  font-weight: 500;
  color: rgba(0, 0, 0, 0.85);
}

.currency-info {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
}

.currency-item {
  font-size: 14px;
  color: rgba(0, 0, 0, 0.65);
}

.trend {
  margin-left: 4px;
  display: inline-flex;
  align-items: center;
}

.trend.up {
  color: #f5222d;
}

.trend.down {
  color: #52c41a;
}

.sub-value {
  font-size: 14px;
  color: rgba(0, 0, 0, 0.65);
  border-top: 1px solid rgba(5, 5, 5, 0.06);
  margin-top: 4px;
  padding-top: 5px;
}

.currency-rmb, .currency-usd {
  font-weight: 500;
}

.currency-divider {
  margin: 0 8px;
  color: rgba(0, 0, 0, 0.45);
}

.mini-chart {
  /* position: absolute; */
  /* bottom: 0;
  left: 0;
  right: 0; */

  height: 46px;
}

.progress-bar {
  height: 4px;
  background: #f0f0f0;
  border-radius: 2px;
  overflow: hidden;
}

.progress {
  height: 100%;
  background: linear-gradient(to right, #1890FF, #52c41a);
  transition: width 0.3s ease;
}

.chart-row {
  margin-top: 20px;
}

.chart-header {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.chart-header .left {
  display: flex;
  align-items: center;
}

.chart-header .right {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 16px;
}

.chart-header .title {
  font-size: 16px;
  font-weight: 500;
  margin-right: 16px;
}

.chart-header :deep(.el-radio-group) {
  line-height: 1;
}

.chart-header :deep(.el-date-editor) {
  margin: 0;
}

.chart-header :deep(.el-date-editor.el-input) {
  vertical-align: middle;
}

.rank-list {
  margin-top: 24px;
}

.rank-item {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  padding: 6px 0;
}

.rank-no {
  width: 20px;
  margin-right: 16px;
  font-weight: 600;
}

.rank-no.top-1 { color: #f5222d; }
.rank-no.top-2 { color: #fa8c16; }
.rank-no.top-3 { color: #faad14; }

.rank-name {
  flex: 1;
  color: rgba(0, 0, 0, 0.65);
}

.rank-value {
  color: rgba(0, 0, 0, 0.85);
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  .dashboard {
    padding: 12px;
  }

  .chart-header {
    flex-direction: column;
    align-items: stretch;
  }

  .chart-header .right {
    flex-direction: column;
    width: 100%;
  }

  .chart-header .right .el-radio-group {
    width: 100%;
    display: flex;
    justify-content: space-between;
  }

  .chart-header .right .el-date-editor {
    width: 100%;
  }

  .card-header {
    flex-direction: column;
    gap: 8px;
  }

  .card-value-container {
    flex-direction: column;
    align-items: center;
    gap: 8px;
  }
}

/* 平板适配 */
@media screen and (min-width: 769px) and (max-width: 1200px) {
  .chart-header .right {
    flex-wrap: wrap;
    gap: 12px;
  }
}

/* 确保图表容器响应式 */
.main-chart-card, .rank-card {
  margin-bottom: 20px;
}

:deep(.el-card__body) {
  padding: 16px;
}

/* 优化小屏幕下的卡片样式 */
@media screen and (max-width: 576px) {
  :deep(.el-card__body) {
    padding: 12px;
  }

  .card-value {
    font-size: 24px;
  }

  .mini-chart {
    height: 40px;
  }
}
</style>
