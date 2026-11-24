<template>
  <div class="settings-page">
    <div class="settings-container">
      <!-- 侧边菜单 -->
      <div class="settings-sidebar">
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="inline"
          :style="{ height: '100%', borderRight: 0 }"
          @click="handleMenuClick"
        >
          <a-menu-item key="profile">
            <template #icon><UserOutlined /></template>
            个人中心
          </a-menu-item>
          <a-menu-item key="system">
            <template #icon><SettingOutlined /></template>
            系统配置
          </a-menu-item>
          <a-menu-item key="logs">
            <template #icon><FileTextOutlined /></template>
            操作日志
          </a-menu-item>
        </a-menu>
      </div>

      <!-- 内容区域 -->
      <div class="settings-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { UserOutlined, SettingOutlined, FileTextOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()

// 选中的菜单项
const selectedKeys = ref<string[]>(['profile'])

/**
 * 监听路由变化,更新选中的菜单项
 */
watch(
  () => route.path,
  (newPath) => {
    const pathParts = newPath.split('/')
    const lastPart = pathParts[pathParts.length - 1]
    selectedKeys.value = [lastPart]
  },
  { immediate: true }
)

/**
 * 菜单点击事件
 */
function handleMenuClick({ key }: { key: string }) {
  router.push(`/settings/${key}`)
}
</script>

<style scoped lang="scss">
.settings-page {
  height: 100%;
  padding: 24px;
  background-color: #f0f2f5;

  .settings-container {
    display: flex;
    height: 100%;
    background-color: #fff;
    border-radius: 4px;
    overflow: hidden;

    .settings-sidebar {
      width: 200px;
      flex-shrink: 0;
      border-right: 1px solid #f0f0f0;
    }

    .settings-content {
      flex: 1;
      overflow-y: auto;
    }
  }
}
</style>

