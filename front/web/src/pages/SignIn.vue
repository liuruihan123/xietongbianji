<template>
  <!-- page wrap -->
  <div class="section section--content">
    <div class="section__content">
      <!-- language switcher -->
      <div class="language-switcher-container">
        <LanguageSwitcher />
      </div>

      <!-- form -->
      <form @submit.prevent="onSubmit" class="form form--content">
        <div class="form__logo-wrap">
          <router-link
            :to="routeUtils.getLocalizedPath('/')"
            class="form__logo"
          >
            <img src="/logo-placeholder.svg" alt="协同编辑系统" />
          </router-link>
          <span class="form__tagline">协同编辑系统<br />协同编辑</span>
        </div>

        <!-- 表单验证错误提示 -->
        <div v-if="validator.hasErrors.value" class="form__errors">
          <div
            v-for="(error, field) in validator.errors.value"
            :key="field"
            class="form__error"
          >
            {{ error }}
          </div>
        </div>

        <div class="form__group">
          <input
            type="text"
            class="form__input"
            placeholder="用户名/邮箱/手机号"
            v-model="loginForm.userName"
            :class="{ 'form__input--error': validator.getFieldError('userName') }"
            @blur="validateField('userName')"
          />
        </div>

        <div class="form__group">
          <input
            type="password"
            class="form__input"
            placeholder="密码"
            v-model="loginForm.password"
            :class="{
              'form__input--error': validator.getFieldError('password'),
            }"
            @blur="validateField('password')"
          />
        </div>

        <!-- 记住密码选项 -->
        <div class="form__group form__group--checkbox">
          <label class="form__checkbox">
            <input
              type="checkbox"
              v-model="rememberPasswordHook.rememberPassword.value"
              class="form__checkbox-input"
            />
            <span class="form__checkbox-mark"></span>
            <span class="form__checkbox-text">记住密码</span>
          </label>
          <!-- <span class="form__checkbox-hint">仅在信任的设备上使用</span> -->
        </div>

        <button
          class="form__btn"
          type="submit"
          :disabled="authStore.loading || !isFormValid"
        >
          <span v-if="authStore.loading">登录中...</span>
          <span v-else>登录</span>
        </button>

        <span class="form__text form__text--center">
          还没有账号？
          <router-link :to="routeUtils.getLocalizedPath('/signup')">
            立即注册
          </router-link>
          <br />
          <router-link :to="routeUtils.getLocalizedPath('/email-login')">
            邮箱验证码登录
          </router-link>
          <br />
          <router-link :to="routeUtils.getLocalizedPath('/forgot-password')">
            忘记密码？
          </router-link>
        </span>

        <!-- design elements -->
        <span class="block-icon block-icon--purple">
          <i class="ti ti-login"></i>
        </span>
        <span class="screw screw--big-tr"></span>
        <span class="screw screw--big-bl"></span>
        <span class="screw screw--big-br"></span>
      </form>
      <!-- end form -->
    </div>

    <!-- animation background -->
    <div
      class="section__canvas section__canvas--full section__canvas--third"
      id="canvas3"
    ></div>
  </div>
  <!-- end page wrap -->

  <!-- 滑动验证码组件 -->
  <SlideCaptcha
    v-model:visible="captcha.state.visible"
    @success="handleCaptchaSuccess"
    @fail="captcha.onCaptchaFail"
    @close="handleCaptchaClose"
  />
</template>

<script setup lang="ts">
import { reactive, computed, onMounted } from "vue";
import { routeUtils } from "@/router";
import LanguageSwitcher from "@/components/common/LanguageSwitcher.vue";
import SlideCaptcha from "@/components/common/SlideCaptcha.vue";
import { useAuth } from "@/hooks/useAuth";
import { useAuthStore } from "@/stores";
import { useFormValidator } from "@/hooks/useAuthValidator";
import { useCaptcha } from "@/hooks/useCaptcha";
import { useRememberPassword } from "@/hooks/useRememberPassword";
import type { LoginForm } from "@/types/auth";

const { login } = useAuth();
const authStore = useAuthStore();
const validator = useFormValidator();
const captcha = useCaptcha(3); // 最多3次失败
const rememberPasswordHook = useRememberPassword();

const loginForm = reactive<LoginForm>({
  userName: "",
  password: "",
});

// 表单有效性检查
const isFormValid = computed(() => {
  return (
    loginForm.userName.trim() !== "" &&
    loginForm.password.trim() !== "" &&
    !validator.hasErrors.value
  );
});

// 单个字段验证
const validateField = (field: string) => {
  switch (field) {
    case "userName":
      const userNameError = validator.validateRequired(loginForm.userName, "用户名");
      if (userNameError) {
        validator.setFieldError("userName", userNameError);
      } else {
        validator.clearFieldError("userName");
      }
      break;
    case "password":
      const passwordError = validator.validatePassword(loginForm.password);
      if (passwordError) {
        validator.setFieldError("password", passwordError);
      } else {
        validator.clearFieldError("password");
      }
      break;
  }
};

const onSubmit = async () => {
  // 验证表单
  if (!validator.validateLoginForm(loginForm)) {
    return;
  }

  // 检查是否需要验证码验证
  if (captcha.needsCaptcha()) {
    captcha.showCaptcha();
    return;
  }

  // 如果已经验证过，直接执行登录
  await executeLogin();
};

// 滑块验证码成功处理
const handleCaptchaSuccess = async () => {
  captcha.onCaptchaSuccess();
  await executeLogin();
};

// 滑块验证码关闭处理
const handleCaptchaClose = () => {
  captcha.onCaptchaClose();
};

// 实际执行登录
const executeLogin = async () => {
  try {
    await login(loginForm);

    // 登录成功后，根据记住密码选项保存凭据
    rememberPasswordHook.saveCredentials(
      loginForm.userName,
      loginForm.password,
      rememberPasswordHook.rememberPassword.value
    );

    // 重置验证码状态
    captcha.resetCaptcha();

    // 登录成功后，useAuth会自动处理重定向
  } catch (error: any) {
    console.error("Login failed:", error);
    // 重置验证码状态，允许重新尝试
    captcha.resetCaptcha();
    // 错误消息已经在store中处理了
  }
};

// 组件挂载时自动填充保存的凭据
onMounted(() => {
  rememberPasswordHook.autoFillForm(loginForm);
});
</script>

<style scoped>
.language-switcher-container {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 10;
}

/* 表单错误提示样式 */
.form__errors {
  margin-bottom: 1rem;
}

.form__error {
  color: #ff4d4f;
  font-size: 0.875rem;
  margin-bottom: 0.5rem;
  padding: 0.5rem;
  background: rgba(255, 77, 79, 0.1);
  border-radius: 4px;
  border-left: 3px solid #ff4d4f;
}

/* 输入框错误状态 */
.form__input--error {
  border-color: #ff4d4f !important;
  box-shadow: 0 0 0 2px rgba(255, 77, 79, 0.2) !important;
}

/* 按钮禁用状态 */
.form__btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  pointer-events: none;
}

/* 链接样式优化 */
.form__text--center a {
  color: var(--main-color, #00d4ff);
  text-decoration: none;
  transition: color 0.3s ease;
}

.form__text--center a:hover {
  color: #00b8e6;
  text-decoration: underline;
}

/* 记住密码复选框样式 */
.form__group--checkbox {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  margin-bottom: 1.5rem;
}

.form__checkbox {
  display: flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
}

.form__checkbox-input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
}

.form__checkbox-mark {
  position: relative;
  width: 18px;
  height: 18px;
  background: rgba(255, 255, 255, 0.1);
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 3px;
  margin-right: 0.75rem;
  transition: all 0.3s ease;
}

.form__checkbox-mark::after {
  content: '';
  position: absolute;
  display: none;
  left: 5px;
  top: 2px;
  width: 4px;
  height: 8px;
  border: solid #00d4ff;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.form__checkbox-input:checked + .form__checkbox-mark {
  background: rgba(0, 212, 255, 0.2);
  border-color: #00d4ff;
}

.form__checkbox-input:checked + .form__checkbox-mark::after {
  display: block;
}

.form__checkbox-text {
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.9rem;
  font-weight: 500;
}

.form__checkbox-hint {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.6);
  margin-left: 2.5rem;
  font-style: italic;
}

.form__checkbox:hover .form__checkbox-mark {
  border-color: rgba(0, 212, 255, 0.6);
  background: rgba(0, 212, 255, 0.1);
}
</style>
