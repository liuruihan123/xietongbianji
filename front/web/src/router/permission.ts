import type { Router } from 'vue-router'
import { useAuthStoreWithout } from '@/stores'
import { useUserStore } from '@/stores/useUserStore'
import { routeUtils } from './index'

export function setupPageGuard(router: Router) {
  router.beforeEach(async (to, from, next) => {
    const authStore = useAuthStoreWithout()
    const userStore = useUserStore()

    // 检查路由是否需要认证
    if (to.meta?.requiresAuth) {
      // 如果用户未登录,显示登录弹窗
      if (!authStore.isAuthenticated) {
        // 记录目标路径
        userStore.setRedirectPath(to.fullPath)
        // 显示登录弹窗
        userStore.openLoginModal(to.fullPath)
        // 停留在当前页面
        next(false)
        return
      }

      // 如果用户已登录,验证用户状态
      try {
        const isValid = await authStore.validateUserStatus()
        if (!isValid) {
          // 用户状态无效,显示登录弹窗
          userStore.setRedirectPath(to.fullPath)
          userStore.openLoginModal(to.fullPath)
          next(false)
          return
        }
      } catch (error) {
        console.error('User validation failed:', error)
        // 验证失败,显示登录弹窗
        userStore.setRedirectPath(to.fullPath)
        userStore.openLoginModal(to.fullPath)
        next(false)
        return
      }
    }

    // 如果用户已登录且访问登录页,重定向到首页
    if (authStore.isAuthenticated && (to.name?.toString().includes('SignIn') || to.name?.toString().includes('SignUp'))) {
      const languagePrefix = routeUtils.getCurrentLanguagePrefix()
      next(`${languagePrefix}/`)
      return
    }

    next()
  })
}
