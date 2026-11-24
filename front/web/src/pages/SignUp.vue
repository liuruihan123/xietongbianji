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
            type="email"
            class="form__input"
            placeholder="邮箱地址"
            v-model="registerForm.userName"
            :class="{ 'form__input--error': validator.getFieldError('userName') }"
            @blur="validateField('userName')"
          />
        </div>

        <div class="form__group">
          <input
            type="password"
            class="form__input"
            placeholder="密码（至少8位）"
            v-model="registerForm.password"
            :class="{
              'form__input--error': validator.getFieldError('password'),
            }"
            @input="onPasswordChange"
            @blur="validateField('password')"
          />
          <!-- 密码强度指示器 -->
          <div v-if="registerForm.password" class="password-strength">
            <div class="password-strength__bar">
              <div
                class="password-strength__fill"
                :class="`password-strength__fill--${passwordStrength.level}`"
                :style="{ width: passwordStrength.percentage + '%' }"
              ></div>
            </div>
            <span class="password-strength__text">
              强度：{{
                passwordStrength.level === "weak"
                  ? "弱"
                  : passwordStrength.level === "medium"
                  ? "中等"
                  : "强"
              }}
              <span
                v-if="passwordStrength.feedback.length > 0"
                class="password-feedback"
              >
                (建议：{{ passwordStrength.feedback.join("、") }})
              </span>
            </span>
          </div>
        </div>

        <div class="form__group">
          <input
            type="password"
            class="form__input"
            placeholder="确认密码"
            v-model="registerForm.confirmPassword"
            :class="{
              'form__input--error': validator.getFieldError('confirmPassword'),
            }"
            @blur="validateField('confirmPassword')"
          />
        </div>

        <div class="form__group">
          <div class="form__input-group">
            <input
              type="text"
              class="form__input form__input--code"
              placeholder="邮箱验证码"
              v-model="registerForm.verificationCode"
              :class="{
                'form__input--error':
                  validator.getFieldError('verificationCode'),
              }"
              @blur="validateField('verificationCode')"
            />
            <button
              type="button"
              class="form__code-btn"
              :disabled="!canSendCode || sendingCode"
              @click="sendVerificationCode"
            >
              {{ codeButtonText }}
            </button>
          </div>
        </div>

        <div class="form__group form__group--checkbox">
          <input
            id="agreeTerms"
            name="agreeTerms"
            type="checkbox"
            v-model="agreeToTerms"
          />
          <label for="agreeTerms">
            我已阅读并同意
            <router-link :to="routeUtils.getLocalizedPath('/privacy')">
              隐私政策
            </router-link>
            和服务条款
          </label>
        </div>

        <button
          class="form__btn"
          type="submit"
          :disabled="authStore.loading || !isFormValid"
        >
          <span v-if="authStore.loading">注册中...</span>
          <span v-else>注册</span>
        </button>

        <span class="form__text form__text--center">
          已有账号？
          <router-link :to="routeUtils.getLocalizedPath('/signin')">
            立即登录
          </router-link>
        </span>

        <!-- design elements -->
        <span class="block-icon block-icon--purple">
          <i class="ti ti-user-plus"></i>
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
import { ref, reactive, computed } from "vue";
import { routeUtils } from "@/router";
import LanguageSwitcher from "@/components/common/LanguageSwitcher.vue";
import SlideCaptcha from "@/components/common/SlideCaptcha.vue";
import { useAuth } from "@/hooks/useAuth";
import { useAuthStore } from "@/stores";
import { useCaptcha } from "@/hooks/useCaptcha";
import {
  useFormValidator,
  usePasswordStrength,
} from "@/hooks/useAuthValidator";
import type { RegisterForm } from "@/types/auth";

const { register, sendEmailCode } = useAuth();
const authStore = useAuthStore();
const validator = useFormValidator();
const { calculatePasswordStrength } = usePasswordStrength();

// 验证码相关
const captcha = useCaptcha(3); // 最多3次失败
const pendingAction = ref<'sendCode' | 'register' | null>(null);

const sendingCode = ref(false);
const countdown = ref(0);
const agreeToTerms = ref(false);

const registerForm = reactive<RegisterForm>({
  userName: "",
  password: "",
  confirmPassword: "",
  verificationCode: "",
});

// 密码强度检测
const passwordStrength = computed(() => {
  return calculatePasswordStrength(registerForm.password);
});

// 是否可以发送验证码
const canSendCode = computed(() => {
  return (
    registerForm.userName.trim() !== "" &&
    !validator.getFieldError("userName") &&
    countdown.value === 0
  );
});

// 验证码按钮文字
const codeButtonText = computed(() => {
  if (countdown.value > 0) {
    return `${countdown.value}s`;
  }
  return sendingCode.value ? "发送中..." : "发送验证码";
});

// 表单有效性检查
const isFormValid = computed(() => {
  return (
    registerForm.userName.trim() !== "" &&
    registerForm.password.trim() !== "" &&
    registerForm.confirmPassword.trim() !== "" &&
    registerForm.verificationCode.trim() !== "" &&
    agreeToTerms.value &&
    passwordStrength.value.level !== "weak" &&
    !validator.hasErrors.value
  );
});

// 单个字段验证
const validateField = (field: string) => {
  switch (field) {
    case "userName":
      const emailError = validator.validateEmail(registerForm.userName);
      if (emailError) {
        validator.setFieldError("userName", emailError);
      } else {
        validator.clearFieldError("userName");
      }
      break;
    case "password":
      const passwordError = validator.validatePassword(
        registerForm.password,
        8
      );
      if (passwordError) {
        validator.setFieldError("password", passwordError);
      } else {
        validator.clearFieldError("password");
      }
      break;
    case "confirmPassword":
      const confirmPasswordError = validator.validateConfirmPassword(
        registerForm.password,
        registerForm.confirmPassword
      );
      if (confirmPasswordError) {
        validator.setFieldError("confirmPassword", confirmPasswordError);
      } else {
        validator.clearFieldError("confirmPassword");
      }
      break;
    case "verificationCode":
      const codeError = validator.validateVerificationCode(
        registerForm.verificationCode
      );
      if (codeError) {
        validator.setFieldError("verificationCode", codeError);
      } else {
        validator.clearFieldError("verificationCode");
      }
      break;
  }
};

// 密码输入时的实时验证
const onPasswordChange = () => {
  validateField("password");
  // 如果确认密码已输入，也重新验证确认密码
  if (registerForm.confirmPassword) {
    validateField("confirmPassword");
  }
};

// 发送验证码
const sendVerificationCode = async () => {
  if (!canSendCode.value) {
    return;
  }

  // 检查是否需要验证码验证
  if (captcha.needsCaptcha()) {
    pendingAction.value = 'sendCode';
    captcha.showCaptcha();
    return;
  }

  // 如果已经验证过，直接执行
  await executeSendCode();
};

// 提交注册
const onSubmit = async () => {
  // 验证表单
  if (!validator.validateRegisterForm(registerForm)) {
    return;
  }

  if (!agreeToTerms.value) {
    validator.setFieldError("agreeToTerms", "请同意隐私政策和服务条款");
    return;
  }

  // 检查是否需要验证码验证
  if (captcha.needsCaptcha()) {
    pendingAction.value = 'register';
    captcha.showCaptcha();
    return;
  }

  // 如果已经验证过，直接执行
  await executeRegister();
};

// 验证码成功处理
const handleCaptchaSuccess = async () => {
  captcha.onCaptchaSuccess();

  // 根据待执行的操作执行相应的逻辑
  if (pendingAction.value === 'sendCode') {
    await executeSendCode();
  } else if (pendingAction.value === 'register') {
    await executeRegister();
  }

  pendingAction.value = null;
};

// 验证码关闭处理
const handleCaptchaClose = () => {
  captcha.onCaptchaClose();
  pendingAction.value = null;
};

// 实际执行发送验证码
const executeSendCode = async () => {
  if (!canSendCode.value) {
    return;
  }

  sendingCode.value = true;

  try {
    await sendEmailCode(registerForm.userName);

    // 开始倒计时
    countdown.value = 60;
    const timer = setInterval(() => {
      countdown.value--;
      if (countdown.value <= 0) {
        clearInterval(timer);
      }
    }, 1000);

    // 重置验证码状态，下次发送时需要重新验证
    captcha.resetCaptcha();
  } catch (error: any) {
    console.error("Send verification code failed:", error);
  } finally {
    sendingCode.value = false;
  }
};

// 实际执行注册
const executeRegister = async () => {
  try {
    await register(registerForm);
    // 注册成功后，useAuth会自动跳转到登录页
    captcha.resetCaptcha(); // 注册成功后重置验证码状态
  } catch (error: any) {
    console.error("Registration failed:", error);
    // 错误消息已经在store中处理了
    // 注册失败后重置验证码，下次注册需要重新验证
    captcha.resetCaptcha();
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

/* 验证码输入框组合 */
.form__input-group {
  display: flex;
  gap: 0.5rem;
}

.form__input--code {
  flex: 1;
}

.form__code-btn {
  background: var(--main-color, #00d4ff);
  color: #000;
  border: none;
  padding: 0 1rem;
  border-radius: 5px;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  min-width: 100px;
}

.form__code-btn:hover:not(:disabled) {
  background: #00b8e6;
  transform: translateY(-1px);
}

.form__code-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* 密码强度指示器 */
.password-strength {
  margin-top: 0.5rem;
}

.password-strength__bar {
  width: 100%;
  height: 4px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
  overflow: hidden;
}

.password-strength__fill {
  height: 100%;
  transition: all 0.3s ease;
  border-radius: 2px;
}

.password-strength__fill--weak {
  background: #ff4d4f;
}

.password-strength__fill--medium {
  background: #faad14;
}

.password-strength__fill--strong {
  background: #52c41a;
}

.password-strength__text {
  display: block;
  font-size: 0.75rem;
  margin-top: 0.25rem;
  color: rgba(255, 255, 255, 0.7);
}

.password-feedback {
  font-size: 0.7rem;
  color: rgba(255, 255, 255, 0.5);
}

/* 按钮禁用状态 */
.form__btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  pointer-events: none;
}

/* 链接样式优化 */
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
</style>
