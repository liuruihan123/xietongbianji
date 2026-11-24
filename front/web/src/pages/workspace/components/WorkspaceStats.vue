<template>
  <div class="workspace-stats">
    <a-row :gutter="16">
      <a-col :span="8">
        <a-card class="stat-card">
          <a-statistic
            title="文件数"
            :value="fileCount"
            :prefix="h(FileOutlined)"
            :value-style="{ color: '#1890ff' }"
          />
        </a-card>
      </a-col>

      <a-col :span="8">
        <a-card class="stat-card">
          <a-statistic
            title="成员数"
            :value="memberCount"
            :prefix="h(TeamOutlined)"
            :value-style="{ color: '#52c41a' }"
          />
        </a-card>
      </a-col>

      <a-col :span="8">
        <a-card class="stat-card">
          <a-statistic
            title="文件夹数"
            :value="folderCount"
            :prefix="h(FolderOutlined)"
            :value-style="{ color: '#faad14' }"
          />
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { h } from 'vue'
import { FileOutlined, TeamOutlined, FolderOutlined } from '@ant-design/icons-vue'

defineProps<{
  fileCount: number
  memberCount: number
  folderCount: number
}>()
</script>

<style scoped lang="scss">
.workspace-stats {
  .stat-card {
    text-align: center;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
      transform: translateY(-2px);
    }
  }
}
</style>

