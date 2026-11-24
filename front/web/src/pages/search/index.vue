<template>
  <div class="search-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <a-breadcrumb>
        <a-breadcrumb-item>
          <HomeOutlined /> 首页
        </a-breadcrumb-item>
        <a-breadcrumb-item>搜索结果</a-breadcrumb-item>
      </a-breadcrumb>
    </div>

    <!-- 搜索区域 -->
    <div class="search-area">
      <GlobalSearch
        v-model:model-value="searchKeyword"
        :placeholder="`搜索 '${searchKeyword}'`"
        width="600px"
        :show-history="false"
        :auto-navigate="false"
        @search="handleGlobalSearch"
      />

      <a-button type="link" @click="showAdvanced = !showAdvanced">
        <FilterOutlined /> {{ showAdvanced ? '收起' : '高级搜索' }}
      </a-button>

      <button style="color: #1E6FFF;" @click="handleFirstHome">首页</button>
    </div>

    <!-- 高级搜索面板 -->
    <div v-if="showAdvanced" class="advanced-panel">
      <AdvancedSearch @search="handleAdvancedSearch" />
    </div>

    <!-- 搜索结果区域 -->
    <div class="search-results">
      <!-- 搜索信息 -->
      <div class="search-info">
        <div class="info-left">
          <span class="keyword">{{ searchKeyword }}</span>
          <span class="count">找到 {{ total }} 个结果</span>
          <span v-if="searchDuration" class="duration">(耗时 {{ searchDuration }}ms)</span>
        </div>

        <div class="info-right">
          <a-space>
            <span>排序:</span>
            <a-select
              v-model:value="sortBy"
              style="width: 120px"
              :options="SORT_BY_OPTIONS"
              @change="handleSortChange"
            />
            <a-select
              v-model:value="sortOrder"
              style="width: 100px"
              :options="SORT_ORDER_OPTIONS"
              @change="handleSortChange"
            />
          </a-space>
        </div>
      </div>

      <!-- 结果列表 -->
      <a-spin :spinning="loading" tip="搜索中...">
        <a-empty
          v-if="!loading && results.length === 0"
          description="未找到相关文件"
        >
          <a-button type="primary" @click="handleReset">
            重新搜索
          </a-button>
        </a-empty>

        <div v-else class="result-list">
          <div
            v-for="item in results"
            :key="item.id"
            class="result-item"
            @click="handleItemClick(item)"
          >
            <div class="item-icon">
              <component
                :is="getFileIcon(item)"
                :style="{ fontSize: '32px', color: getFileColor(item) }"
              />
            </div>

            <div class="item-content">
              <div class="item-name" v-html="item.highlightName || item.name"></div>
              <div class="item-meta">
                <span class="meta-item">
                  <FolderOutlined /> {{ item.path }}
                </span>
                <span class="meta-item">
                  <UserOutlined /> {{ item.creatorName }}
                </span>
                <span class="meta-item">
                  <ClockCircleOutlined /> {{ formatDate(item.createTime) }}
                </span>
                <span v-if="!item.isFolder" class="meta-item">
                  <FileOutlined /> {{ formatSize(item.size) }}
                </span>
              </div>
            </div>

            <div class="item-actions">
              <a-button
                v-if="!item.isFolder"
                type="link"
                size="small"
                @click.stop="handleOpen(item)"
              >
                打开
              </a-button>
              <a-button type="link" size="small" @click.stop="handleDownload(item)">
                下载
              </a-button>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div v-if="total > 0" class="pagination">
          <a-pagination
            v-model:current="pageNum"
            v-model:page-size="pageSize"
            :total="total"
            :show-size-changer="true"
            :show-total="(total) => `共 ${total} 条`"
            @change="handlePageChange"
          />
        </div>
      </a-spin>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  HomeOutlined,
  FilterOutlined,
  FolderOutlined,
  UserOutlined,
  ClockCircleOutlined,
  FileOutlined,
  FileWordOutlined,
  FileExcelOutlined,
  FilePptOutlined,
  FilePdfOutlined,
  FileImageOutlined,
  FileZipOutlined,
} from '@ant-design/icons-vue'
import GlobalSearch from '@/components/search/GlobalSearch.vue'
import AdvancedSearch from '@/components/search/AdvancedSearch.vue'
import { search } from '@/api/search'
import type { SearchResultItem, AdvancedSearchParams } from '@/types/search'
import { SortBy, SortOrder, SORT_BY_OPTIONS, SORT_ORDER_OPTIONS, highlightKeyword } from '@/types/search'
import { FILE_TYPE_COLORS } from '@/types/file'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()

const searchKeyword = ref('')
const showAdvanced = ref(false)
const loading = ref(false)
const results = ref<SearchResultItem[]>([])
const total = ref(0)
const searchDuration = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)
const sortBy = ref(SortBy.RELEVANCE)
const sortOrder = ref(SortOrder.DESC)
const advancedParams = ref<AdvancedSearchParams>()

// 获取文件图标
function getFileIcon(item: SearchResultItem) {
  if (item.isFolder) return FolderOutlined

  const iconMap: Record<string, any> = {
    docx: FileWordOutlined,
    doc: FileWordOutlined,
    xlsx: FileExcelOutlined,
    xls: FileExcelOutlined,
    pptx: FilePptOutlined,
    ppt: FilePptOutlined,
    pdf: FilePdfOutlined,
    jpg: FileImageOutlined,
    jpeg: FileImageOutlined,
    png: FileImageOutlined,
    gif: FileImageOutlined,
    zip: FileZipOutlined,
    rar: FileZipOutlined,
  }

  const fileType = item.fileType || item.type || ''
  return iconMap[fileType.toLowerCase()] || FileOutlined
}

// 获取文件颜色
function getFileColor(item: SearchResultItem) {
  if (item.isFolder) return '#1890ff'
  const fileType = item.fileType || item.type || ''
  return FILE_TYPE_COLORS[fileType.toLowerCase()] || '#666'
}

// 格式化日期
function formatDate(date: string) {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

// 格式化文件大小
function formatSize(bytes: number) {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

// 处理全局搜索组件的搜索事件
function handleGlobalSearch(keyword: string) {
  if (!keyword.trim()) {
    message.warning('请输入搜索关键词')
    return
  }
  // 更新searchKeyword
  searchKeyword.value = keyword
  // 更新URL
  router.replace({
    path: '/search',
    query: { q: keyword },
  })
  // 重置页码并执行搜索
  pageNum.value = 1
  performSearch()
}

// 返回首页
function handleFirstHome() {
  router.push('/')
}

// 执行搜索
async function performSearch() {
  if (!searchKeyword.value.trim()) {
    message.warning('请输入搜索关键词')
    return
  }

  loading.value = true

  try {
    const response = await search({
      keyword: searchKeyword.value,
      sortBy: sortBy.value,
      sortOrder: sortOrder.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...advancedParams.value,
    })

    // 注意: 搜索接口返回格式为 {code, msg, rows, total}, 不是 {code, msg, data: {rows, total}}
    results.value = response.rows.map((item) => ({
      ...item,
      // 添加兼容字段,映射后端字段名到前端使用的字段名
      name: item.fileName,
      type: item.fileType,
      size: item.fileSize,
      path: item.locationPath || item.filePath, // 使用位置路径
      creatorName: item.ownerName, // 映射所有者名称到创建者名称
      creatorId: item.ownerId,
      highlightName: highlightKeyword(item.fileName, searchKeyword.value),
    }))
    total.value = response.total
    searchDuration.value = 0 // 后端未返回duration字段

    // 后端会自动记录搜索历史,无需手动添加
  } catch (error) {
    console.error('搜索失败:', error)
    message.error('搜索失败')
  } finally {
    loading.value = false
  }
}

// 高级搜索
function handleAdvancedSearch(params: AdvancedSearchParams) {
  advancedParams.value = params
  pageNum.value = 1
  performSearch()
}

// 排序变化
function handleSortChange() {
  pageNum.value = 1
  performSearch()
}

// 分页变化
function handlePageChange() {
  performSearch()
}

// 重置搜索
function handleReset() {
  showAdvanced.value = false
  advancedParams.value = undefined
  sortBy.value = SortBy.RELEVANCE
  sortOrder.value = SortOrder.DESC
  pageNum.value = 1
}

// 点击结果项
function handleItemClick(item: SearchResultItem) {
  if (item.isFolder) {
    router.push(`/workspace/${item.workspaceId}/files?folderId=${item.id}`)
  } else {
    router.push(`/editor/${item.id}`)
  }
}

// 打开文件
function handleOpen(item: SearchResultItem) {
  router.push(`/editor/${item.id}`)
}

// 下载文件
function handleDownload(item: SearchResultItem) {
  message.info('下载功能开发中...')
}

// 监听路由变化
watch(
  () => route.query.q,
  (newKeyword) => {
    if (newKeyword && typeof newKeyword === 'string') {
      searchKeyword.value = newKeyword
      pageNum.value = 1
      performSearch()
    }
  },
  { immediate: true }
)

onMounted(() => {
  const keyword = route.query.q
  if (keyword && typeof keyword === 'string') {
    searchKeyword.value = keyword
    performSearch()
  }
})
</script>

<style scoped lang="scss">
.search-page {
  min-height: 100vh;
  background: #f5f5f5;

  .page-header {
    padding: 24px;
    background: white;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }

  .search-area {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 16px;
    padding: 32px 24px;
    background: white;
    margin-bottom: 16px;
  }

  .advanced-panel {
    padding: 0 24px 16px;
    background: white;
    margin-bottom: 16px;
  }

  .search-results {
    padding: 24px;
    background: white;
    margin: 0 24px 24px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    .search-info {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-bottom: 16px;
      border-bottom: 1px solid #f0f0f0;
      margin-bottom: 16px;

      .info-left {
        display: flex;
        align-items: center;
        gap: 12px;

        .keyword {
          font-size: 18px;
          font-weight: 500;
          color: #1890ff;
        }

        .count {
          color: #666;
        }

        .duration {
          color: #999;
          font-size: 12px;
        }
      }
    }

    .result-list {
      .result-item {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 16px;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          background: #f5f5f5;
        }

        .item-icon {
          flex-shrink: 0;
        }

        .item-content {
          flex: 1;
          overflow: hidden;

          .item-name {
            font-size: 16px;
            font-weight: 500;
            color: #333;
            margin-bottom: 8px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;

            :deep(mark) {
              background-color: #fff566;
              padding: 0 2px;
            }
          }

          .item-meta {
            display: flex;
            gap: 16px;
            font-size: 12px;
            color: #999;

            .meta-item {
              display: flex;
              align-items: center;
              gap: 4px;
            }
          }
        }

        .item-actions {
          flex-shrink: 0;
        }
      }
    }

    .pagination {
      display: flex;
      justify-content: center;
      margin-top: 24px;
    }
  }
}
</style>



