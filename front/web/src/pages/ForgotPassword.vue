<template>
  <!-- page wrap -->
  <div class="section section--content">
    <div class="section__content">
      <!-- language switcher -->
      <div class="language-switcher-container">
        <LanguageSwitcher />
      </div>

      <!-- form -->
      <form
        v-if="!emailSent"
        @submit.prevent="onSubmit"
        class="form form--content"
      >
        <div class="form__logo-wrap">
          <router-link
            :to="routeUtils.getLocalizedPath('/')"
            class="form__logo"
          >
            <img src="/logo-placeholder.svg" alt="协同编辑系统" />
          </router-link>
          <span class="form__tagline">协同编辑系统<br />协同编辑</span>
        </div>

        <div class="form__title">
          <h2>重置密码</h2>
          <p>输入您的邮箱地址，我们将发送重置密码的链接给您。</p>
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
            type="email"
            class="form__input"
            placeholder="邮箱地址"
            v-model="forgotForm.email"
            :class="{ 'form__input--error': validator.getFieldError('email') }"
            @blur="validateField('email')"
          />
        </div>

        <button
          class="form__btn"
          type="submit"
          :disabled="authStore.loading || !isFormValid"
        >
          <span v-if="authStore.loading">发送中...</span>
          <span v-else>发送重置链接</span>
        </button>

        <span class="form__text form__text--center">
          我们将向您的邮箱发送密码重置链接
        </span>

        <div class="form__links">
          <router-link :to="routeUtils.getLocalizedPath('/signin')">
            返回登录
          </router-link>
          <span class="form__separator">|</span>
          <router-link :to="routeUtils.getLocalizedPath('/signup')">
            创建账号
          </router-link>
        </div>

        <!-- design elements -->
        <span class="block-icon block-icon--purple">
          <i class="ti ti-lock"></i>
        </span>
        <span class="screw screw--big-tr"></span>
        <span class="screw screw--big-bl"></span>
        <span class="screw screw--big-br"></span>
      </form>
      <!-- end form -->

      <!-- success message -->
      <div v-else class="form form--content form--success">
        <div class="form__logo-wrap">
          <router-link
            :to="routeUtils.getLocalizedPath('/')"
            class="form__logo"
          >
            <img src="/logo-placeholder.svg" alt="协同编辑系统" />
          </router-link>
          <span class="form__tagline">协同编辑系统<br />协同编辑</span>
        </div>

        <div class="form__success-content">
          <div class="form__success-icon">
            <i class="ti ti-mail-check"></i>
          </div>

          <h2 class="form__success-title">请检查您的邮箱</h2>

          <p class="form__success-text">
            我们已向
            <strong>{{ forgotForm.email }}</strong> 发送了密码重置链接
          </p>

          <p class="form__success-text">
            请检查您的邮箱并点击链接重置密码。链接将在24小时后过期。
          </p>

          <div class="form__success-actions">
            <button
              class="form__btn"
              type="button"
              @click="resendEmail"
              :disabled="resending"
            >
              <span v-if="resending">重新发送中...</span>
              <span v-else>重新发送邮件</span>
            </button>

            <router-link
              :to="routeUtils.getLocalizedPath('/signin')"
              class="form__link"
            >
              返回登录
            </router-link>
          </div>
        </div>

        <!-- design elements -->
        <span class="block-icon block-icon--green">
          <i class="ti ti-check"></i>
        </span>
        <span class="screw screw--big-tr"></span>
        <span class="screw screw--big-bl"></span>
        <span class="screw screw--big-br"></span>
      </div>
      <!-- end success message -->
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
import { ref, reactive, computed } from "vue";
import { routeUtils } from "@/router";
import LanguageSwitcher from "@/components/common/LanguageSwitcher.vue";
import SlideCaptcha from "@/components/common/SlideCaptcha.vue";
import { useAuth } from "@/hooks/useAuth";
import { useAuthStore } from "@/stores";
import { useCaptcha } from "@/hooks/useCaptcha";
import { useFormValidator } from "@/hooks/useAuthValidator";
import type { ForgotPasswordForm } from "@/types/auth";

const { forgotPassword } = useAuth();
const authStore = useAuthStore();
const validator = useFormValidator();

// 验证码相关
const captcha = useCaptcha(3); // 最多3次失败
const pendingAction = ref<'sendReset' | 'resend' | null>(null);

const resending = ref(false);
const emailSent = ref(false);

const forgotForm = reactive<ForgotPasswordForm>({
  email: "",
});

// 表单有效性检查
const isFormValid = computed(() => {
  return forgotForm.email.trim() !== "" && !validator.hasErrors.value;
});

// 单个字段验证
const validateField = (field: string) => {
  switch (field) {
    case "email":
      const emailError = validator.validateEmail(forgotForm.email);
      if (emailError) {
        validator.setFieldError("email", emailError);
      } else {
        validator.clearFieldError("email");
      }
      break;
  }
};

// 提交表单
const onSubmit = async () => {
  // 验证表单
  if (!validator.validateForgotPasswordForm(forgotForm)) {
    return;
  }

  // 检查是否需要验证码验证
  if (captcha.needsCaptcha()) {
    pendingAction.value = 'sendReset';
    captcha.showCaptcha();
    return;
  }

  // 如果已经验证过，直接执行
  await executeSendReset();
};

// 重新发送邮件
const resendEmail = async () => {
  // 检查是否需要验证码验证
  if (captcha.needsCaptcha()) {
    pendingAction.value = 'resend';
    captcha.showCaptcha();
    return;
  }

  // 如果已经验证过，直接执行
  await executeResend();
};

// 验证码成功处理
const handleCaptchaSuccess = async () => {
  captcha.onCaptchaSuccess();

  // 根据待执行的操作执行相应的逻辑
  if (pendingAction.value === 'sendReset') {
    await executeSendReset();
  } else if (pendingAction.value === 'resend') {
    await executeResend();
  }

  pendingAction.value = null;
};

// 验证码关闭处理
const handleCaptchaClose = () => {
  captcha.onCaptchaClose();
  pendingAction.value = null;
};

// 实际执行发送重置密码邮件
const executeSendReset = async () => {
  try {
    await forgotPassword(forgotForm);
    emailSent.value = true;
    captcha.resetCaptcha(); // 发送成功后重置验证码状态
  } catch (error: any) {
    console.error("Forgot password failed:", error);
    // 错误消息已经在store中处理了
    captcha.resetCaptcha(); // 发送失败后重置验证码
  }
};

// 实际执行重新发送邮件
const executeResend = async () => {
  resending.value = true;

  try {
    await forgotPassword(forgotForm);
    captcha.resetCaptcha(); // 重新发送成功后重置验证码状态
  } catch (error: any) {
    console.error("Resend email failed:", error);
    captcha.resetCaptcha(); // 重新发送失败后重置验证码
  } finally {
    resending.value = false;
  }
};
</script>

<style scoped>
.language-switcher-container {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 10;
}

/* 标题样式 */
.form__title {
  text-align: center;
  margin-bottom: 2rem;
}

.form__title h2 {
  color: #fff;
  font-size: 1.8rem;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.form__title p {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.9rem;
  line-height: 1.5;
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

/* 链接样式 */
.form__links {
  text-align: center;
  margin-top: 1.5rem;
}

.form__links a {
  color: var(--main-color, #00d4ff);
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.3s ease;
}

.form__links a:hover {
  color: #00b8e6;
  text-decoration: underline;
}

.form__separator {
  color: rgba(255, 255, 255, 0.5);
  margin: 0 1rem;
}

.form__text--center a,
.form__group--checkbox a {
  color: var(--main-color, #00d4ff);
  text-decoration: none;
  transition: color 0.3s ease;
}

.form__text--center a:hover,
.form__group--checkbox a:hover {
  color: #00b8e6;
  text-decoration: underline;
}

/* 成功页面样式 */
.form--success {
  display: block;
}

.form__success-content {
  text-align: center;
  padding: 2rem 0;
}

.form__success-icon {
  font-size: 4rem;
  color: var(--main-color, #00d4ff);
  margin-bottom: 1.5rem;
}

.form__success-title {
  color: #fff;
  font-size: 1.8rem;
  margin-bottom: 1rem;
  font-weight: 600;
}

.form__success-text {
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 1rem;
  line-height: 1.6;
  font-size: 0.95rem;
}

.form__success-text strong {
  color: var(--main-color, #00d4ff);
  font-weight: 600;
}

.form__success-actions {
  margin-top: 2rem;
}

.form__link {
  display: inline-block;
  margin-top: 1rem;
  color: var(--main-color, #00d4ff);
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.3s ease;
}

.form__link:hover {
  color: #00b8e6;
  text-decoration: underline;
}

.block-icon--green {
  background: #52c41a;
}
</style>
