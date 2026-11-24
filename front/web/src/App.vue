<script setup lang="ts">
import { onMounted } from 'vue'
import { ConfigProvider } from 'ant-design-vue'
import { Toaster } from 'vue-sonner'
import { useAuthStore } from '@/stores'
import LoginModal from '@/components/common/LoginModal.vue'

const authStore = useAuthStore()

// 应用启动时初始化认证状态
onMounted(async () => {
  // 如果有 token，验证用户状态
  if (authStore.token) {
    try {
      // 先验证用户状态
      const isValid = await authStore.validateUserStatus()

      // if (isValid) {
      //   // 用户状态有效，获取会话信息
      //   await authStore.getSession()
      // }
    } catch (error) {
      console.error('Initialize auth failed:', error)
    }
  }
})
</script>

<template>
  <!-- 主要通过 CSS 变量来控制主题 -->
  <ConfigProvider>
    <router-view />

    <!-- 全局登录弹窗 -->
    <LoginModal />

    <!-- Vue Sonner Toast 组件 -->
    <Toaster
      position="top-right"
      :rich-colors="true"
      :close-button="true"
      :duration="3000"
      :expand="true"
      :visible-toasts="5"
    />
  </ConfigProvider>
</template>

<style scoped>
</style>
