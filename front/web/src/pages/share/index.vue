<template>
  <MainLayout
    :show-search="true"
    :show-notification="true"
    :show-sidebar-toggle="true"
    @sidebar-toggle="sidebarOpen = !sidebarOpen"
  >
    <div class="share-page bg-gray-50">
      <div class="flex">
        <!-- 左侧边栏 -->
        <aside :class="['fixed left-0 top-16 bottom-0 w-64 bg-white border-r border-gray-200 transform transition-transform duration-300 z-40 overflow-y-auto', sidebarOpen ? 'translate-x-0' : '-translate-x-full md:translate-x-0']">
          <div class="p-4">
            <!-- 返回按钮 -->
            <div class="mb-4">
              <button
                @click="handleGoBack"
                class="w-full flex items-center justify-center space-x-2 px-5 py-2 bg-white text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-50 hover:border-gray-400 shadow-sm hover:shadow transition-all"
              >
                <i class="fa fa-arrow-left text-base"></i>
                <span class="font-medium text-sm">返回首页</span>
              </button>
            </div>

            <!-- 分享导航 -->
            <div class="space-y-1 mb-6">
              <a
                v-for="item in navItems"
                :key="item.key"
                @click="handleNavClick(item)"
                :class="[
                  'flex items-center justify-between px-3 py-2.5 rounded-lg cursor-pointer transition-all',
                  activeNav === item.key
                    ? 'bg-blue-50 text-blue-600 font-medium'
                    : 'text-gray-700 hover:bg-gray-50'
                ]"
              >
                <div class="flex items-center space-x-3">
                  <i :class="['fa', item.icon, activeNav === item.key ? 'text-blue-600' : 'text-gray-400']"></i>
                  <span class="text-sm">{{ item.label }}</span>
                </div>
                <span v-if="item.count !== undefined" class="text-xs text-gray-400">{{ item.count }}</span>
              </a>
            </div>

            <!-- 快捷操作 -->
            <div class="border-t border-gray-200 pt-4">
              <button
                @click="handleRefresh"
                class="w-full flex items-center justify-center space-x-2 px-5 py-2 bg-gray-200 text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-300 hover:border-gray-400 shadow-sm hover:shadow transition-all"
              >
                <i class="fa fa-refresh text-base"></i>
                <span class="font-medium text-sm">刷新列表</span>
              </button>
            </div>
          </div>
        </aside>

        <!-- 主内容区 -->
        <main class="flex-1 md:ml-64 p-6">
          <!-- 面包屑导航 -->
          <div class="flex items-center justify-between mb-6">
            <div class="flex items-center space-x-2 text-sm">
              <i class="fa fa-share-alt text-gray-400"></i>
              <span class="text-gray-900 font-medium">我的分享</span>
              <i v-if="activeNav !== 'all'" class="fa fa-angle-right text-gray-400"></i>
              <span v-if="activeNav !== 'all'" class="text-gray-500">{{ getCurrentNavLabel() }}</span>
            </div>

            <!-- 搜索框 -->
            <div class="flex items-center space-x-3">
              <div class="relative">
                <input
                  v-model="searchKeyword"
                  type="text"
                  placeholder="搜索文件名或分享链接..."
                  class="pl-10 pr-4 py-2 w-80 text-sm bg-white border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                  @keyup.enter="handleSearch"
                >
                <i class="fa fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
              </div>
            </div>
          </div>

          <!-- 统计卡片 -->
          <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
            <div class="bg-white rounded-lg border border-gray-200 p-4 hover:shadow-md transition-shadow">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-xs text-gray-500 mb-1">全部分享</p>
                  <p class="text-2xl font-bold text-gray-900">{{ statistics.total }}</p>
                </div>
                <div class="w-12 h-12 rounded-lg bg-blue-50 flex items-center justify-center">
                  <i class="fa fa-share-alt text-blue-600 text-xl"></i>
                </div>
              </div>
            </div>

            <div class="bg-white rounded-lg border border-gray-200 p-4 hover:shadow-md transition-shadow">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-xs text-gray-500 mb-1">有效分享</p>
                  <p class="text-2xl font-bold text-green-600">{{ statistics.active }}</p>
                </div>
                <div class="w-12 h-12 rounded-lg bg-green-50 flex items-center justify-center">
                  <i class="fa fa-check-circle text-green-600 text-xl"></i>
                </div>
              </div>
            </div>

            <div class="bg-white rounded-lg border border-gray-200 p-4 hover:shadow-md transition-shadow">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-xs text-gray-500 mb-1">已过期</p>
                  <p class="text-2xl font-bold text-orange-600">{{ statistics.expired }}</p>
                </div>
                <div class="w-12 h-12 rounded-lg bg-orange-50 flex items-center justify-center">
                  <i class="fa fa-clock-o text-orange-600 text-xl"></i>
                </div>
              </div>
            </div>

            <div class="bg-white rounded-lg border border-gray-200 p-4 hover:shadow-md transition-shadow">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-xs text-gray-500 mb-1">已禁用</p>
                  <p class="text-2xl font-bold text-red-600">{{ statistics.disabled }}</p>
                </div>
                <div class="w-12 h-12 rounded-lg bg-red-50 flex items-center justify-center">
                  <i class="fa fa-ban text-red-600 text-xl"></i>
                </div>
              </div>
            </div>
          </div>

          <!-- 分享列表 -->
          <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
            <ShareList
              :shares="shares"
              :loading="loading"
              :total="total"
              @refresh="loadShares"
              @view-link="handleViewLink"
              @view-statistics="handleViewStatistics"
              @edit="handleEdit"
            />
          </div>
        </main>
      </div>

      <!-- 分享链接模态框 -->
      <ShareLinkModal
        ref="shareLinkModalRef"
        @view-statistics="handleViewStatistics"
      />

      <!-- 分享统计模态框 -->
      <ShareStatisticsModal ref="shareStatisticsModalRef" />

      <!-- 创建/编辑分享模态框 -->
      <CreateShareModal
        ref="createShareModalRef"
        :file-id="0"
        :file-name="''"
        @success="handleShareSuccess"
      />
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { routeUtils } from '@/router'
import MainLayout from '@/components/layout/MainLayout.vue'
import ShareList from '@/components/share/ShareList.vue'
import ShareLinkModal from '@/components/share/ShareLinkModal.vue'
import ShareStatisticsModal from '@/components/share/ShareStatisticsModal.vue'
import CreateShareModal from '@/components/share/CreateShareModal.vue'
import type { ShareInfo } from '@/types/share'
import { ShareStatus } from '@/types/share'
import { getMyShares } from '@/api/share'

const router = useRouter()

// 侧边栏状态
const sidebarOpen = ref(true)

// 导航项
const navItems = ref([
  { key: 'all', label: '全部分享', icon: 'fa-share-alt', count: 0 },
  { key: 'active', label: '有效分享', icon: 'fa-check-circle', count: 0 },
  { key: 'expired', label: '已过期', icon: 'fa-clock-o', count: 0 },
  { key: 'disabled', label: '已禁用', icon: 'fa-ban', count: 0 },
])

// 当前激活的导航
const activeNav = ref('all')

// 数据状态
const loading = ref(false)
const shares = ref<ShareInfo[]>([])
const total = ref(0)
const searchKeyword = ref('')
const statusFilter = ref<number | string>('')

// 统计数据
const statistics = computed(() => {
  const stats = {
    total: 0,
    active: 0,
    expired: 0,
    disabled: 0,
  }

  shares.value.forEach(share => {
    stats.total++
    // 根据新的数字状态值进行统计
    if (share.status === ShareStatus.ACTIVE || share.status === 0) {
      stats.active++
    } else if (share.status === ShareStatus.EXPIRED || share.status === 2) {
      stats.expired++
    } else if (share.status === ShareStatus.DISABLED || share.status === 1) {
      stats.disabled++
    }
  })

  // 更新导航项计数
  navItems.value[0].count = stats.total
  navItems.value[1].count = stats.active
  navItems.value[2].count = stats.expired
  navItems.value[3].count = stats.disabled

  return stats
})

const shareLinkModalRef = ref()
const shareStatisticsModalRef = ref()
const createShareModalRef = ref()

// 返回首页
function handleGoBack() {
  router.push(routeUtils.getLocalizedPath('/'))
}

// 加载分享列表
async function loadShares() {
  loading.value = true

  try {
    const result = await getMyShares({
      keyword: searchKeyword.value,
      status: statusFilter.value.toString(),
      pageNum: 1,
      pageSize: 100, // 加载更多以便统计
    })

    console.log('API返回结果:', result)

    // 根据实际返回的数据结构处理
    if (result.data) {
      // 如果有data字段，说明是ApiResponse<{rows, total}>格式
      shares.value = result.data.rows || []
      total.value = result.data.total || 0
    } else {
      // 如果没有data字段，说明直接返回{rows, total}格式
      shares.value = (result as any).rows || []
      total.value = (result as any).total || 0
    }
  } catch (error) {
    console.error('加载分享列表失败:', error)
    message.error('加载分享列表失败')
  } finally {
    loading.value = false
  }
}

// 处理导航点击
function handleNavClick(item: any) {
  activeNav.value = item.key

  // 根据导航设置状态筛选 (使用数字状态值)
  switch (item.key) {
    case 'all':
      statusFilter.value = ''
      break
    case 'active':
      statusFilter.value = ShareStatus.ACTIVE
      break
    case 'expired':
      statusFilter.value = ShareStatus.EXPIRED
      break
    case 'disabled':
      statusFilter.value = ShareStatus.DISABLED
      break
  }

  loadShares()
}

// 获取当前导航标签
function getCurrentNavLabel() {
  const item = navItems.value.find(item => item.key === activeNav.value)
  return item?.label || ''
}

function handleSearch() {
  loadShares()
}

function handleRefresh() {
  searchKeyword.value = ''
  statusFilter.value = ''
  activeNav.value = 'all'
  loadShares()
}

function handleViewLink(share: ShareInfo) {
  shareLinkModalRef.value?.open(share)
}

function handleViewStatistics(share: ShareInfo) {
  shareStatisticsModalRef.value?.open(share)
}

function handleEdit(share: ShareInfo) {
  createShareModalRef.value?.open(share)
}

function handleShareSuccess() {
  loadShares()
}

onMounted(() => {
  loadShares()
})
</script>

<style scoped>
.share-page {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* 自定义滚动条 */
aside::-webkit-scrollbar {
  width: 6px;
}

aside::-webkit-scrollbar-track {
  background: #f1f1f1;
}

aside::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 3px;
}

aside::-webkit-scrollbar-thumb:hover {
  background: #a0aec0;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .share-page {
    font-size: 14px;
  }
}
</style>

