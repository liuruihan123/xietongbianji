<template>
  <header class="fixed top-0 left-0 right-0 bg-white border-b border-gray-200 z-50 shadow-sm">
    <div class="flex items-center justify-between px-6 h-16">
      <!-- 左侧区域 -->
      <div class="flex items-center space-x-4">
        <!-- 侧边栏切换按钮(移动端) -->
        <button
          v-if="showSidebarToggle"
          @click="$emit('sidebar-toggle')"
          class="p-2 rounded-md text-gray-600 hover:bg-gray-100 md:hidden"
        >
          <i class="fa fa-bars text-lg"></i>
        </button>

        <!-- Logo -->
        <div
          class="flex items-center space-x-2 cursor-pointer"
          @click="router.push(routeUtils.getLocalizedPath('/'))"
        >
          <div class="w-9 h-9 rounded-lg bg-blue-600 flex items-center justify-center shadow-sm">
            <i class="fa fa-file-text-o text-white text-lg"></i>
          </div>
          <h1 class="text-xl font-bold text-gray-900">{{ title || '协同编辑系统' }}</h1>
        </div>
      </div>

      <!-- 中间搜索框 -->
      <div v-if="showSearch" class="hidden md:flex items-center flex-1 max-w-2xl mx-8">
        <div class="relative w-full">
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索文件和文件夹..."
            class="pl-10 pr-4 py-2.5 w-full text-sm bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
            @keyup.enter="handleSearch"
          >
          <i class="fa fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
        </div>
      </div>

      <!-- 右侧区域 -->
      <div class="flex items-center space-x-2">
        <!-- 通知按钮 -->
        <!-- <button
          v-if="showNotification"
          @click="$emit('notification-click')"
          class="p-2.5 rounded-lg text-gray-600 hover:bg-gray-100 relative transition-colors"
        >
          <i class="fa fa-bell-o text-lg"></i>
          <span
            v-if="isAuthenticated"
            class="absolute top-1.5 right-1.5 w-2 h-2 bg-red-500 rounded-full"
          ></span>
        </button> -->

        <!-- 未登录状态 -->
        <div v-if="!isAuthenticated" class="flex items-center space-x-2 ml-2">
          <button
            @click="handleLogin"
            class="px-4 py-2 text-sm font-medium text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
          >
            登录
          </button>
          <button
            @click="handleSignUp"
            class="px-4 py-2 text-sm font-medium bg-blue-600 text-white hover:bg-blue-700 rounded-lg shadow-sm transition-colors"
          >
            注册
          </button>
        </div>

        <!-- 已登录状态 -->
        <div v-else class="relative group ml-2">
          <button class="flex items-center space-x-2 px-2 py-1.5 rounded-lg hover:bg-gray-100 transition-colors">
            <LocalAvatar
              :src="userInfo?.avatar"
              :name="userInfo?.nickName || 'User'"
              :size="32"
              class="border-2 border-gray-200"
            />
            <span class="hidden md:inline-block text-sm font-medium text-gray-700">
              <!-- {{ userInfo }} -->
              {{ userInfo?.nickName || '用户' }}
            </span>
            <i class="fa fa-angle-down text-gray-500 text-sm"></i>
          </button>

          <!-- 下拉菜单 -->
          <div class="absolute right-0 mt-2 w-56 bg-white rounded-lg shadow-lg border border-gray-200 py-2 z-50 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all">
            <a
              @click="router.push(routeUtils.getLocalizedPath('/'))"
              class="flex items-center px-4 py-2.5 text-sm text-gray-700 hover:bg-gray-50 cursor-pointer transition-colors"
            >
              <i class="fa fa-briefcase mr-3 text-gray-400"></i>
              我的工作空间
            </a>
            <a
              @click="router.push(routeUtils.getLocalizedPath('/share'))"
              class="flex items-center px-4 py-2.5 text-sm text-gray-700 hover:bg-gray-50 cursor-pointer transition-colors"
            >
              <i class="fa fa-share-alt mr-3 text-gray-400"></i>
              我的分享
            </a>
            <!-- <a
              @click="router.push(routeUtils.getLocalizedPath('/convert'))"
              class="flex items-center px-4 py-2.5 text-sm text-gray-700 hover:bg-gray-50 cursor-pointer transition-colors"
            >
              <i class="fa fa-exchange mr-3 text-gray-400"></i>
              文档转换
            </a> -->
            <!-- <a
              @click="router.push(routeUtils.getLocalizedPath('/settings'))"
              class="flex items-center px-4 py-2.5 text-sm text-gray-700 hover:bg-gray-50 cursor-pointer transition-colors"
            >
              <i class="fa fa-cog mr-3 text-gray-400"></i>
              系统设置
            </a> -->
            <div class="border-t border-gray-200 my-2"></div>
            <a
              @click="handleLogout"
              class="flex items-center px-4 py-2.5 text-sm text-red-600 hover:bg-red-50 cursor-pointer transition-colors"
            >
              <i class="fa fa-sign-out mr-3"></i>
              退出登录
            </a>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/modules/auth'
import { useUserStore } from '@/stores/useUserStore'
import { message } from 'ant-design-vue'
import { routeUtils } from '@/router'
import LocalAvatar from '@/components/common/LocalAvatar.vue'

// Props 定义
interface Props {
  showSearch?: boolean
  showNotification?: boolean
  showSidebarToggle?: boolean
  title?: string
}

const props = withDefaults(defineProps<Props>(), {
  showSearch: false,
  showNotification: false,
  showSidebarToggle: false,
  title: ''
})

// Emits 定义
const emit = defineEmits<{
  'search': [keyword: string]
  'notification-click': []
  'sidebar-toggle': []
}>()

const router = useRouter()
const authStore = useAuthStore()
const userStore = useUserStore()

// 响应式数据
const searchKeyword = ref('')

// 计算属性
const isAuthenticated = computed(() => authStore.isAuthenticated)
const userInfo = computed(() => authStore.userInfo)

// 搜索处理
function handleSearch() {
  if (searchKeyword.value.trim()) {
    emit('search', searchKeyword.value)
    router.push(routeUtils.getLocalizedPath(`/search?q=${encodeURIComponent(searchKeyword.value)}`))
  }
}

// 退出登录
async function handleLogout() {
  try {
    await authStore.logout()
    message.success('已退出登录')
    router.push(routeUtils.getLocalizedPath('/'))
  } catch (error) {
    message.error('退出登录失败')
  }
}

// 打开登录弹框
function handleLogin() {
  userStore.openLoginModal()
}

// 打开注册页面
function handleSignUp() {
  router.push(routeUtils.getLocalizedPath('/signup'))
}
</script>

<style scoped>
/* Tailwind CSS 已经提供了大部分样式,这里只添加必要的自定义样式 */
</style>
