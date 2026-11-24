<template>
  <a-auto-complete
    v-model:value="searchKeyword"
    :options="options"
    :style="{ width: width }"
    :dropdown-match-select-width="400"
    @search="handleSearch"
    @select="handleSelect"
  >
    <template #option="{ value, label, type, path }">
      <div class="search-option">
        <component
          :is="getFileIcon(type)"
          :style="{ fontSize: '16px', color: getFileColor(type) }"
        />
        <div class="option-content">
          <div class="option-name" v-html="label"></div>
          <div class="option-path">{{ path }}</div>
        </div>
      </div>
    </template>

    <a-input-search
      :placeholder="placeholder"
      :loading="loading"
      enter-button
      @search="handleSearchClick"
    >
      <template #prefix>
        <SearchOutlined />
      </template>
    </a-input-search>
  </a-auto-complete>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { SearchOutlined } from '@ant-design/icons-vue'
import {
  FolderOutlined,
  FileWordOutlined,
  FileExcelOutlined,
  FilePptOutlined,
  FilePdfOutlined,
  FileImageOutlined,
  FileZipOutlined,
  FileOutlined,
} from '@ant-design/icons-vue'
import { getSearchSuggestions, getSearchHistory } from '@/api/search'
import type { SearchSuggestion, SearchHistory } from '@/types/search'
import { highlightKeyword } from '@/types/search'
import { debounce } from 'lodash-es'

const props = withDefaults(
  defineProps<{
    placeholder?: string
    width?: string
    showHistory?: boolean
    modelValue?: string
    autoNavigate?: boolean
  }>(),
  {
    placeholder: '搜索文件...',
    width: '400px',
    showHistory: true,
    modelValue: '',
    autoNavigate: true,
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: string]
  search: [keyword: string]
}>()

const router = useRouter()

// 搜索关键词
const searchKeyword = ref(props.modelValue)
const loading = ref(false)
const suggestions = ref<SearchSuggestion[]>([])
const history = ref<SearchHistory[]>([])

// 监听 modelValue 变化
watch(() => props.modelValue, (val) => {
  searchKeyword.value = val
})

// 监听内部搜索关键词变化，同步到父组件
watch(searchKeyword, (val) => {
  emit('update:modelValue', val)
})

// 自动完成选项
const options = computed(() => {
  const result: any[] = []

  // 如果有搜索建议,显示建议
  if (suggestions.value.length > 0) {
    result.push({
      label: '搜索建议',
      options: suggestions.value.map((item) => ({
        value: item.id.toString(),
        label: highlightKeyword(item.name, searchKeyword.value),
        type: item.type,
        path: item.path,
        isFolder: item.isFolder,
        fileId: item.id,
      })),
    })
  }

  // 如果没有输入且显示历史,显示搜索历史
  if (!searchKeyword.value && props.showHistory && history.value.length > 0) {
    result.push({
      label: '搜索历史',
      options: history.value.slice(0, 5).map((item) => ({
        value: item.keyword,
        label: item.keyword,
        type: 'history',
        path: `${item.resultCount || 0} 个结果`,
      })),
    })
  }

  return result
})

// 获取文件图标
function getFileIcon(type: string) {
  if (type === 'history') return SearchOutlined

  const iconMap: Record<string, any> = {
    folder: FolderOutlined,
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

  return iconMap[type.toLowerCase()] || FileOutlined
}

// 获取文件颜色
function getFileColor(type: string) {
  if (type === 'history') return '#999'

  const colorMap: Record<string, string> = {
    folder: '#1890ff',
    docx: '#2b579a',
    doc: '#2b579a',
    xlsx: '#217346',
    xls: '#217346',
    pptx: '#d24726',
    ppt: '#d24726',
    pdf: '#f40f02',
  }

  return colorMap[type.toLowerCase()] || '#666'
}

// 搜索输入处理(防抖)
const handleSearch = debounce(async (value: string) => {
  if (!value.trim()) {
    suggestions.value = []
    return
  }

  try {
    loading.value = true
    const response = await getSearchSuggestions(value, 10)
    suggestions.value = (response as any).data || response as any
  } catch (error) {
    console.error('获取搜索建议失败:', error)
  } finally {
    loading.value = false
  }
}, 300)

// 选择搜索建议
function handleSelect(value: string, option: any) {
  if (option.type === 'history') {
    // 选择历史记录,执行搜索
    searchKeyword.value = value
    handleSearchClick(value)
  } else {
    // 选择文件,跳转到文件详情或编辑器
    if (option.isFolder) {
      message.info('打开文件夹功能开发中...')
    } else {
      router.push(`/editor/${option.fileId}`)
    }
  }
}

// 点击搜索按钮
function handleSearchClick(value?: string) {
  const keyword = value || searchKeyword.value

  if (!keyword.trim()) {
    message.warning('请输入搜索关键词')
    return
  }
  // 触发search事件
  emit('search', keyword)
  // 如果启用了自动导航，则跳转到搜索页面
  if (props.autoNavigate) {
    router.push({
      path: '/search',
      query: { q: keyword },
    })
  }
}

// 加载搜索历史
async function loadHistory() {
  try {
    const response = await getSearchHistory(10)
    history.value = response.data || []
  } catch (error) {
    console.error('加载搜索历史失败:', error)
    history.value = []
  }
}

// 初始化
loadHistory()
</script>

<style scoped lang="scss">
.search-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 4px 0;

  .option-content {
    flex: 1;
    overflow: hidden;

    .option-name {
      font-size: 14px;
      color: #333;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;

      :deep(mark) {
        background-color: #fff566;
        padding: 0 2px;
      }
    }

    .option-path {
      font-size: 12px;
      color: #999;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      margin-top: 2px;
    }
  }
}
</style>



