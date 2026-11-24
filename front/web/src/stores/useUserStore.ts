/**
 * 用户状态管理 Store
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo } from '@/types/auth'

export const useUserStore = defineStore('user', () => {
  // 状态
  const userInfo = ref<UserInfo | null>(null)
  const token = ref<string>('')
  const isLoggedIn = ref<boolean>(false)

  // 登录弹窗状态
  const showLoginModal = ref<boolean>(false)
  const redirectPath = ref<string>('/')

  // 计算属性
  const userId = computed(() => userInfo.value?.id || userInfo.value?.userId)
  const userName = computed(() => userInfo.value?.userName)
  const email = computed(() => userInfo.value?.email)
  const avatar = computed(() => userInfo.value?.avatar)

  // 方法
  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
    isLoggedIn.value = true
  }

  const setToken = (newToken: string) => {
    token.value = newToken
  }

  const clearUserInfo = () => {
    userInfo.value = null
    token.value = ''
    isLoggedIn.value = false
  }

  const updateUserInfo = (updates: Partial<UserInfo>) => {
    if (userInfo.value) {
      userInfo.value = { ...userInfo.value, ...updates }
    }
  }

  const logout = () => {
    clearUserInfo()
  }

  // 登录弹窗相关方法
  const openLoginModal = (targetPath?: string) => {
    showLoginModal.value = true
    if (targetPath) {
      redirectPath.value = targetPath
    }
  }

  const closeLoginModal = () => {
    showLoginModal.value = false
  }

  const setRedirectPath = (path: string) => {
    redirectPath.value = path
  }

  return {
    // 状态
    userInfo,
    token,
    isLoggedIn,
    showLoginModal,
    redirectPath,

    // 计算属性
    userId,
    userName,
    email,
    avatar,

    // 方法
    setUserInfo,
    setToken,
    clearUserInfo,
    updateUserInfo,
    logout,
    openLoginModal,
    closeLoginModal,
    setRedirectPath,
  }
})
