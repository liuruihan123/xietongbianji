<template>
  <div class="login-form">
    <a-form
      :model="formData"
      :rules="rules"
      @finish="handleSubmit"
      @finishFailed="handleSubmitFailed"
      layout="vertical"
      size="large"
    >
      <a-form-item name="userName" :label="t('auth.username')">
        <a-input
          v-model:value="formData.userName"
          :placeholder="t('auth.usernamePlaceholder', '请输入邮箱或手机号')"
          :prefix="UserOutlined"
        />
      </a-form-item>

      <a-form-item name="password" :label="t('auth.password')">
        <a-input-password
          v-model:value="formData.password"
          :placeholder="t('auth.passwordPlaceholder', '请输入密码')"
          :prefix="LockOutlined"
        />
      </a-form-item>

      <a-form-item>
        <div class="form-options">
          <a-checkbox v-model:checked="formData.rememberMe">
            {{ t('auth.rememberMe') }}
          </a-checkbox>
          <router-link :to="routeUtils.getLocalizedPath('/forgot')" class="forgot-link">
            {{ t('auth.forgotPassword') }}
          </router-link>
        </div>
      </a-form-item>

      <a-form-item>
        <a-button
          type="primary"
          html-type="submit"
          :loading="loading"
          :disabled="!isFormValid"
          block
        >
          {{ t('auth.login') }}
        </a-button>
      </a-form-item>

      <a-form-item>
        <div class="register-link">
          {{ t('auth.noAccount', '还没有账户？') }}
          <router-link :to="routeUtils.getLocalizedPath('/signup')">
            {{ t('auth.register') }}
          </router-link>
        </div>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, computed, ref } from 'vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { useAuth, useAuthValidator } from '@/hooks'
import { routeUtils } from '@/router'
import type { LoginForm } from '@/types/auth'

interface Props {
  redirectPath?: string
}

const props = withDefaults(defineProps<Props>(), {
  redirectPath: '/'
})

const emit = defineEmits<{
  success: [data: any]
  error: [error: any]
}>()

const { t } = useI18n()
const router = useRouter()
const { login } = useAuth()
const { getLoginRules } = useAuthValidator()

const loading = ref(false)

const formData = reactive<LoginForm & { rememberMe: boolean }>({
  userName: '',
  password: '',
  rememberMe: false
})

const rules = computed(() => {
  const loginRules = getLoginRules()
  return {
    userName: loginRules.userName.map(rule => ({
      required: rule.required,
      message: rule.message || t('validation.required'),
      validator: rule.validator ? (_rule: any, value: string) => {
        const result = rule.validator!(value)
        return result === true ? Promise.resolve() : Promise.reject(new Error(rule.message))
      } : undefined
    })),
    password: loginRules.password.map(rule => ({
      required: rule.required,
      min: rule.min,
      message: rule.message || (rule.min ? t('validation.minLength', { min: rule.min }) : t('validation.required'))
    }))
  }
})

const isFormValid = computed(() => {
  return formData.userName.trim() !== '' && formData.password.trim() !== ''
})

const handleSubmit = async () => {
  try {
    loading.value = true
    
    const loginData: LoginForm = {
      userName: formData.userName.trim(),
      password: formData.password
    }

    const result = await login(loginData)
    
    message.success(t('auth.loginSuccess'))
    emit('success', result)
    
    // 重定向到指定页面或首页
    const redirectTo = props.redirectPath || routeUtils.getLocalizedPath('/')
    router.push(redirectTo)
    
  } catch (error: any) {
    console.error('Login failed:', error)
    message.error(error.message || t('auth.loginFailed'))
    emit('error', error)
  } finally {
    loading.value = false
  }
}

const handleSubmitFailed = (errorInfo: any) => {
  console.log('Form validation failed:', errorInfo)
}
</script>

<style scoped>
.login-form {
  max-width: 400px;
  margin: 0 auto;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.forgot-link {
  color: #1890ff;
  text-decoration: none;
}

.forgot-link:hover {
  text-decoration: underline;
}

.register-link {
  text-align: center;
  color: #666;
}

.register-link a {
  color: #1890ff;
  text-decoration: none;
  margin-left: 4px;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>
