<template>
  <!-- 遮罩层 -->
  <Transition name="fade">
    <div
      v-if="userStore.showLoginModal"
      class="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center p-4"
      @click.self="handleClose"
    >
      <!-- 弹框容器 -->
      <Transition name="slide-up">
        <div
          v-if="userStore.showLoginModal"
          class="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden"
          @click.stop
        >
          <!-- 头部 -->
          <div class="relative bg-gradient-to-r from-blue-600 to-blue-700 px-8 py-6">
            <button
              @click="handleClose"
              class="absolute top-4 right-4 w-8 h-8 flex items-center justify-center rounded-full text-white hover:bg-white hover:bg-opacity-20 transition-colors"
            >
              <i class="fa fa-times text-lg"></i>
            </button>
            <div class="flex items-center space-x-3">
              <div class="w-12 h-12 rounded-xl bg-white bg-opacity-20 flex items-center justify-center">
                <i class="fa fa-file-text-o text-white text-2xl"></i>
              </div>
              <div>
                <h2 class="text-2xl font-bold text-white">欢迎回来</h2>
                <p class="text-blue-100 text-sm mt-1">登录到 DocFlow</p>
              </div>
            </div>
          </div>

          <!-- 表单内容 -->
          <div class="px-8 py-6">
            <!-- 错误提示 -->
            <div v-if="validator.hasErrors.value" class="mb-4 p-4 bg-red-50 border border-red-200 rounded-lg">
              <div
                v-for="(error, field) in validator.errors.value"
                :key="field"
                class="text-sm text-red-600 flex items-center space-x-2"
              >
                <i class="fa fa-exclamation-circle"></i>
                <span>{{ error }}</span>
              </div>
            </div>

            <!-- 登录表单 -->
            <form @submit.prevent="onSubmit" class="space-y-5">
              <!-- 用户名输入 -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">用户名</label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <i class="fa fa-user text-gray-400"></i>
                  </div>
                  <input
                    v-model="loginForm.userName"
                    type="text"
                    placeholder="用户名/邮箱/手机号"
                    :class="[
                      'w-full pl-10 pr-4 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all',
                      validator.getFieldError('userName')
                        ? 'border-red-300 bg-red-50'
                        : 'border-gray-300 bg-white'
                    ]"
                    @blur="validateField('userName')"
                  />
                </div>
                <p v-if="validator.getFieldError('userName')" class="mt-1 text-sm text-red-600">
                  {{ validator.getFieldError('userName') }}
                </p>
              </div>

              <!-- 密码输入 -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">密码</label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <i class="fa fa-lock text-gray-400"></i>
                  </div>
                  <input
                    v-model="loginForm.password"
                    :type="showPassword ? 'text' : 'password'"
                    placeholder="请输入密码"
                    :class="[
                      'w-full pl-10 pr-12 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all',
                      validator.getFieldError('password')
                        ? 'border-red-300 bg-red-50'
                        : 'border-gray-300 bg-white'
                    ]"
                    @blur="validateField('password')"
                  />
                  <button
                    type="button"
                    @click="showPassword = !showPassword"
                    class="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-600"
                  >
                    <i :class="['fa', showPassword ? 'fa-eye-slash' : 'fa-eye']"></i>
                  </button>
                </div>
                <p v-if="validator.getFieldError('password')" class="mt-1 text-sm text-red-600">
                  {{ validator.getFieldError('password') }}
                </p>
              </div>

              <!-- 记住密码和忘记密码 -->
              <div class="flex items-center justify-between">
                <label class="flex items-center cursor-pointer">
                  <input
                    type="checkbox"
                    v-model="rememberPasswordHook.rememberPassword.value"
                    class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                  />
                  <span class="ml-2 text-sm text-gray-700">记住密码</span>
                </label>
                <a
                  @click="handleForgotPassword"
                  class="text-sm text-blue-600 hover:text-blue-700 cursor-pointer"
                >
                  忘记密码?
                </a>
              </div>

              <!-- 登录按钮 -->
              <button
                type="submit"
                :disabled="authStore.loading || !isFormValid"
                :class="[
                  'w-full py-3 px-4 rounded-lg font-medium text-white transition-all',
                  authStore.loading || !isFormValid
                    ? 'bg-gray-400 cursor-not-allowed'
                    : 'bg-blue-600 hover:bg-blue-700 shadow-lg hover:shadow-xl'
                ]"
              >
                <span v-if="authStore.loading" class="flex items-center justify-center space-x-2">
                  <i class="fa fa-spinner fa-spin"></i>
                  <span>登录中...</span>
                </span>
                <span v-else>登录</span>
              </button>

              <!-- 注册链接 -->
              <div class="text-center text-sm text-gray-600">
                还没有账号?
                <a
                  @click="handleSignUp"
                  class="text-blue-600 hover:text-blue-700 font-medium cursor-pointer"
                >
                  立即注册
                </a>
              </div>
            </form>
          </div>
        </div>
      </Transition>
    </div>
  </Transition>

  <!-- 滑动验证码组件 -->
  <SlideCaptcha
    v-model:visible="captcha.state.visible"
    @success="handleCaptchaSuccess"
    @fail="captcha.onCaptchaFail"
    @close="handleCaptchaClose"
  />
</template>

<script setup lang="ts">
import { reactive, computed, watch, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import SlideCaptcha from '@/components/common/SlideCaptcha.vue'
import { useAuth } from '@/hooks/useAuth'
import { useAuthStore } from '@/stores'
import { useUserStore } from '@/stores/useUserStore'
import { useFormValidator } from '@/hooks/useAuthValidator'
import { useCaptcha } from '@/hooks/useCaptcha'
import { useRememberPassword } from '@/hooks/useRememberPassword'
import { routeUtils } from '@/router'
import type { LoginForm } from '@/types/auth'

const router = useRouter()
const { login } = useAuth()
const authStore = useAuthStore()
const userStore = useUserStore()
const validator = useFormValidator()
const captcha = useCaptcha(3) // 最多3次失败
const rememberPasswordHook = useRememberPassword()

// 显示密码状态
const showPassword = ref(false)

const loginForm = reactive<LoginForm>({
  userName: '',
  password: '',
})

// 表单有效性检查
const isFormValid = computed(() => {
  return (
    loginForm.userName.trim() !== '' &&
    loginForm.password.trim() !== '' &&
    !validator.hasErrors.value
  )
})

// 单个字段验证
const validateField = (field: string) => {
  switch (field) {
    case 'userName':
      const userNameError = validator.validateRequired(loginForm.userName, '用户名')
      if (userNameError) {
        validator.setFieldError('userName', userNameError)
      } else {
        validator.clearFieldError('userName')
      }
      break
    case 'password':
      const passwordError = validator.validatePassword(loginForm.password)
      if (passwordError) {
        validator.setFieldError('password', passwordError)
      } else {
        validator.clearFieldError('password')
      }
      break
  }
}

const onSubmit = async () => {
  // 验证表单
  if (!validator.validateLoginForm(loginForm)) {
    return
  }

  // 检查是否需要验证码验证
  if (captcha.needsCaptcha()) {
    captcha.showCaptcha()
    return
  }

  // 如果已经验证过,直接执行登录
  await executeLogin()
}

// 滑块验证码成功处理
const handleCaptchaSuccess = async () => {
  captcha.onCaptchaSuccess()
  await executeLogin()
}

// 滑块验证码关闭处理
const handleCaptchaClose = () => {
  captcha.onCaptchaClose()
}

// 实际执行登录
const executeLogin = async () => {
  try {
    await login(loginForm)

    // 登录成功后,根据记住密码选项保存凭据
    rememberPasswordHook.saveCredentials(
      loginForm.userName,
      loginForm.password,
      rememberPasswordHook.rememberPassword.value
    )

    // 重置验证码状态
    captcha.resetCaptcha()

    // 关闭登录弹窗
    userStore.closeLoginModal()

    // 跳转到目标路径
    const targetPath = userStore.redirectPath || '/'
    router.push(targetPath)

    // 清空重定向路径
    userStore.setRedirectPath('/')
  } catch (error: any) {
    console.error('Login failed:', error)
    // 重置验证码状态,允许重新尝试
    captcha.resetCaptcha()
  }
}

// 关闭弹框
const handleClose = () => {
  userStore.closeLoginModal()
}

// 忘记密码
const handleForgotPassword = () => {
  userStore.closeLoginModal()
  router.push(routeUtils.getLocalizedPath('/forgot-password'))
}

// 注册
const handleSignUp = () => {
  userStore.closeLoginModal()
  router.push(routeUtils.getLocalizedPath('/signup'))
}

// 监听弹窗打开,自动填充保存的凭据
watch(
  () => userStore.showLoginModal,
  (visible) => {
    if (visible) {
      rememberPasswordHook.autoFillForm(loginForm)
      validator.clearAllErrors()
    }
  }
)

// 组件挂载时自动填充保存的凭据
onMounted(() => {
  rememberPasswordHook.autoFillForm(loginForm)
})
</script>

<style scoped>
/* 淡入淡出动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 滑入滑出动画 */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-from {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}

.slide-up-leave-to {
  opacity: 0;
  transform: translateY(-20px) scale(0.95);
}
</style>

