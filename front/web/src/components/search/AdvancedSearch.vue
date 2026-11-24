<template>
  <div class="advanced-search">
    <a-form layout="vertical">
      <a-row :gutter="16">
        <!-- 文件类型 -->
        <a-col :span="12">
          <a-form-item label="文件类型">
            <a-select
              v-model:value="formData.fileTypes"
              mode="multiple"
              placeholder="选择文件类型"
              :options="FILE_TYPE_OPTIONS"
              allow-clear
            />
          </a-form-item>
        </a-col>

        <!-- 工作空间 -->
        <a-col :span="12">
          <a-form-item label="工作空间">
            <a-select
              v-model:value="formData.workspaceId"
              placeholder="选择工作空间"
              allow-clear
            >
              <a-select-option
                v-for="workspace in workspaces"
                :key="workspace.id"
                :value="workspace.id"
              >
                {{ workspace.name }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>

        <!-- 创建时间范围 -->
        <a-col :span="12">
          <a-form-item label="创建时间">
            <a-range-picker
              v-model:value="formData.createTimeRange"
              style="width: 100%"
              :placeholder="['开始时间', '结束时间']"
            />
          </a-form-item>
        </a-col>

        <!-- 更新时间范围 -->
        <a-col :span="12">
          <a-form-item label="更新时间">
            <a-range-picker
              v-model:value="formData.updateTimeRange"
              style="width: 100%"
              :placeholder="['开始时间', '结束时间']"
            />
          </a-form-item>
        </a-col>

        <!-- 文件大小范围 -->
        <a-col :span="12">
          <a-form-item label="文件大小(MB)">
            <a-input-group compact>
              <a-input-number
                v-model:value="formData.sizeRange[0]"
                :min="0"
                placeholder="最小"
                style="width: 50%"
              />
              <a-input-number
                v-model:value="formData.sizeRange[1]"
                :min="0"
                placeholder="最大"
                style="width: 50%"
              />
            </a-input-group>
          </a-form-item>
        </a-col>

        <!-- 创建者 -->
        <a-col :span="12">
          <a-form-item label="创建者">
            <a-select
              v-model:value="formData.creatorId"
              placeholder="选择创建者"
              allow-clear
              show-search
              :filter-option="filterUser"
            >
              <a-select-option
                v-for="user in users"
                :key="user.id"
                :value="user.id"
              >
                {{ user.name }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>

      <!-- 操作按钮 -->
      <a-form-item>
        <a-space>
          <a-button type="primary" @click="handleSearch">
            <SearchOutlined /> 搜索
          </a-button>
          <a-button @click="handleReset">
            <ReloadOutlined /> 重置
          </a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { FILE_TYPE_OPTIONS } from '@/types/search'
import type { AdvancedSearchParams } from '@/types/search'
import type { Dayjs } from 'dayjs'

const emit = defineEmits<{
  search: [params: AdvancedSearchParams]
}>()

// 表单数据
const formData = reactive<AdvancedSearchParams & {
  createTimeRange?: [Dayjs, Dayjs]
  updateTimeRange?: [Dayjs, Dayjs]
  sizeRange: [number | undefined, number | undefined]
}>({
  fileTypes: [],
  createTimeRange: undefined,
  updateTimeRange: undefined,
  sizeRange: [undefined, undefined],
  creatorId: undefined,
  workspaceId: undefined,
  folderId: undefined,
})

// 工作空间列表(模拟数据)
const workspaces = ref<Array<{ id: number; name: string }>>([])

// 用户列表(模拟数据)
const users = ref<Array<{ id: number; name: string }>>([])

// 用户筛选
function filterUser(input: string, option: any) {
  return option.children[0].toLowerCase().includes(input.toLowerCase())
}

// 执行搜索
function handleSearch() {
  const params: AdvancedSearchParams = {
    fileTypes: formData.fileTypes,
    createTimeRange: formData.createTimeRange
      ? [
          formData.createTimeRange[0].format('YYYY-MM-DD'),
          formData.createTimeRange[1].format('YYYY-MM-DD'),
        ]
      : undefined,
    updateTimeRange: formData.updateTimeRange
      ? [
          formData.updateTimeRange[0].format('YYYY-MM-DD'),
          formData.updateTimeRange[1].format('YYYY-MM-DD'),
        ]
      : undefined,
    sizeRange:
      formData.sizeRange[0] !== undefined || formData.sizeRange[1] !== undefined
        ? [formData.sizeRange[0] || 0, formData.sizeRange[1] || 0]
        : undefined,
    creatorId: formData.creatorId,
    workspaceId: formData.workspaceId,
    folderId: formData.folderId,
  }

  emit('search', params)
}

// 重置表单
function handleReset() {
  formData.fileTypes = []
  formData.createTimeRange = undefined
  formData.updateTimeRange = undefined
  formData.sizeRange = [undefined, undefined]
  formData.creatorId = undefined
  formData.workspaceId = undefined
  formData.folderId = undefined
}

// 加载工作空间列表
async function loadWorkspaces() {
  // TODO: 调用API获取工作空间列表
  workspaces.value = [
    { id: 1, name: '我的工作空间' },
    { id: 2, name: '团队工作空间' },
  ]
}

// 加载用户列表
async function loadUsers() {
  // TODO: 调用API获取用户列表
  users.value = [
    { id: 1, name: '张三' },
    { id: 2, name: '李四' },
    { id: 3, name: '王五' },
  ]
}

onMounted(() => {
  loadWorkspaces()
  loadUsers()
})
</script>

<style scoped lang="scss">
.advanced-search {
  padding: 16px;
  background: #fafafa;
  border-radius: 4px;
}
</style>


