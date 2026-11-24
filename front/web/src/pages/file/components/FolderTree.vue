<template>
  <div class="folder-tree">
    <a-tree
      :tree-data="treeData"
      :selected-keys="selectedKeys"
      :expanded-keys="expandedKeys"
      @select="handleSelect"
      @expand="handleExpand"
    >
      <template #title="{ title, fileCount }">
        <span class="tree-node-title">
          <FolderOutlined />
          {{ title }}
          <span class="file-count" v-if="fileCount !== undefined">({{ fileCount }})</span>
        </span>
      </template>
    </a-tree>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { FolderOutlined } from '@ant-design/icons-vue'
import type { FolderTreeNode } from '@/types/file'

const props = defineProps<{
  folders: FolderTreeNode[]
  selectedFolderId?: number | null
}>()

const emit = defineEmits<{
  select: [folderId: number | null]
}>()

const expandedKeys = ref<number[]>([])
const selectedKeys = ref<number[]>([])

const treeData = computed(() => {
  return convertToTreeData(props.folders)
})

function convertToTreeData(folders: FolderTreeNode[]): any[] {
  return folders.map(folder => ({
    key: folder.id,
    title: folder.name,
    fileCount: folder.fileCount,
    children: folder.children ? convertToTreeData(folder.children) : undefined,
  }))
}

function handleSelect(keys: number[]) {
  const folderId = keys[0] || null
  selectedKeys.value = keys
  emit('select', folderId)
}

function handleExpand(keys: number[]) {
  expandedKeys.value = keys
}

watch(() => props.selectedFolderId, (id) => {
  selectedKeys.value = id !== null ? [id] : []
}, { immediate: true })
</script>

<style scoped lang="scss">
.folder-tree {
  padding: 16px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  .tree-node-title {
    display: flex;
    align-items: center;
    gap: 8px;

    .file-count {
      font-size: 12px;
      color: #999;
    }
  }
}
</style>

