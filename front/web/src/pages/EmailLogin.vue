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

        <!-- 登录方式切换 -->
        <div class="login-type-switcher">
          <button
            type="button"
            class="login-type-btn"
            :class="{ 'login-type-btn--active': loginType === 'email' }"
            @click="switchLoginType('email')"
          >
            <i class="ti ti-mail"></i>
            邮箱验证码登录
          </button>
          <button
            type="button"
            class="login-type-btn"
            :class="{ 'login-type-btn--active': loginType === 'password' }"
            @click="switchLoginType('password')"
          >
            <i class="ti ti-lock"></i>
            账号密码登录
          </button>
        </div>

        <div class="form__title">
          <h2>{{ loginType === 'email' ? '邮箱验证码登录' : '账号密码登录' }}</h2>
          <p>{{ loginType === 'email' ? '输入您的邮箱地址，我们将发送验证码给您。' : '使用您的账号和密码登录系统。' }}</p>
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

        <!-- 邮箱验证码登录表单 -->
        <template v-if="loginType === 'email'">
          <div class="form__group">
            <input
              type="email"
              class="form__input"
              placeholder="邮箱地址"
              v-model="emailLoginForm.email"
              :class="{ 'form__input--error': validator.getFieldError('email') }"
              @blur="validateField('email')"
            />
          </div>

          <div class="form__group">
            <div class="form__input-group">
              <input
                type="text"
                class="form__input form__input--code"
                placeholder="邮箱验证码"
                v-model="emailLoginForm.emailCode"
                :class="{
                  'form__input--error': validator.getFieldError('emailCode'),
                }"
                @blur="validateField('emailCode')"
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
        </template>

        <!-- 账号密码登录表单 -->
        <template v-else>
          <div class="form__group">
            <input
              type="text"
              class="form__input"
              placeholder="用户名/邮箱/手机号"
              v-model="passwordLoginForm.userName"
              :class="{ 'form__input--error': validator.getFieldError('userName') }"
              @blur="validateField('userName')"
            />
          </div>

          <div class="form__group">
            <input
              type="password"
              class="form__input"
              placeholder="密码"
              v-model="passwordLoginForm.password"
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
          </div>
        </template>

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
          <router-link :to="routeUtils.getLocalizedPath('/forgot-password')">
            忘记密码？
          </router-link>
        </span>

        <!-- design elements -->
        <span class="block-icon block-icon--purple">
          <i class="ti ti-mail"></i>
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

    <!-- 滑动验证码组件 -->
    <SlideCaptcha
      v-model:visible="captcha.state.visible"
      @success="handleCaptchaSuccess"
      @fail="captcha.onCaptchaFail"
      @close="handleCaptchaClose"
    />
  </div>
  <!-- end page wrap -->
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from "vue";
import { routeUtils } from "@/router";
import LanguageSwitcher from "@/components/common/LanguageSwitcher.vue";
import SlideCaptcha from "@/components/common/SlideCaptcha.vue";
import { useAuth } from "@/hooks/useAuth";
import { useAuthStore } from "@/stores";
import { useFormValidator } from "@/hooks/useAuthValidator";
import { useCaptcha } from "@/hooks/useCaptcha";
import { useRememberPassword } from "@/hooks/useRememberPassword";
import type { EmailLoginForm, LoginForm } from "@/types/auth";

const { emailLogin, sendEmailCode, login } = useAuth();
const authStore = useAuthStore();
const validator = useFormValidator();
const captcha = useCaptcha(3); // 最多允许3次验证失败
const rememberPasswordHook = useRememberPassword();

// 登录方式: 'email' 或 'password'
const loginType = ref<'email' | 'password'>('email');

// 存储待执行的操作
const pendingAction = ref<'login' | 'sendCode' | null>(null);

const sendingCode = ref(false);
const countdown = ref(0);

const emailLoginForm = reactive<EmailLoginForm>({
  email: "",
  emailCode: "",
});

const passwordLoginForm = reactive<LoginForm>({
  userName: "",
  password: "",
});

// 是否可以发送验证码
const canSendCode = computed(() => {
  return (
    emailLoginForm.email.trim() !== "" &&
    !validator.getFieldError("email") &&
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
  if (loginType.value === 'email') {
    return (
      emailLoginForm.email.trim() !== "" &&
      emailLoginForm.emailCode.trim() !== "" &&
      !validator.hasErrors.value
    );
  } else {
    return (
      passwordLoginForm.userName.trim() !== "" &&
      passwordLoginForm.password.trim() !== "" &&
      !validator.hasErrors.value
    );
  }
});

// 单个字段验证
const validateField = (field: string) => {
  switch (field) {
    case "email":
      const emailError = validator.validateEmail(emailLoginForm.email);
      if (emailError) {
        validator.setFieldError("email", emailError);
      } else {
        validator.clearFieldError("email");
      }
      break;
    case "emailCode":
      const codeError = validator.validateVerificationCode(emailLoginForm.emailCode);
      if (codeError) {
        validator.setFieldError("emailCode", codeError);
      } else {
        validator.clearFieldError("emailCode");
      }
      break;
    case "userName":
      const userNameError = validator.validateRequired(passwordLoginForm.userName, "用户名");
      if (userNameError) {
        validator.setFieldError("userName", userNameError);
      } else {
        validator.clearFieldError("userName");
      }
      break;
    case "password":
      const passwordError = validator.validatePassword(passwordLoginForm.password);
      if (passwordError) {
        validator.setFieldError("password", passwordError);
      } else {
        validator.clearFieldError("password");
      }
      break;
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

const onSubmit = async () => {
  // 验证表单
  if (loginType.value === 'email') {
    validateField('email');
    validateField('emailCode');
  } else {
    validateField('userName');
    validateField('password');
  }

  if (!isFormValid.value) {
    return;
  }

  // 检查是否需要验证码验证
  if (captcha.needsCaptcha()) {
    pendingAction.value = 'login';
    captcha.showCaptcha();
    return;
  }

  // 如果已经验证过，直接执行
  await executeLogin();
};

// 验证码成功处理
const handleCaptchaSuccess = async () => {
  captcha.onCaptchaSuccess();

  // 根据待执行的操作执行相应的逻辑
  if (pendingAction.value === 'sendCode') {
    await executeSendCode();
  } else if (pendingAction.value === 'login') {
    await executeLogin();
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
    await sendEmailCode(emailLoginForm.email);

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

// 切换登录方式
const switchLoginType = (type: 'email' | 'password') => {
  loginType.value = type;
  // 清空所有表单
  emailLoginForm.email = '';
  emailLoginForm.emailCode = '';
  passwordLoginForm.userName = '';
  passwordLoginForm.password = '';
  // 清空所有错误
  validator.clearAllErrors();
  // 重置验证码状态
  captcha.resetCaptcha();
};

// 实际执行邮箱登录
const executeEmailLogin = async () => {
  try {
    await emailLogin(emailLoginForm);
    // 登录成功后，useAuth会自动处理重定向
    captcha.resetCaptcha(); // 登录成功后重置验证码状态
  } catch (error: any) {
    console.error("Email login failed:", error);
    // 错误消息已经在store中处理了
    // 登录失败后重置验证码，下次登录需要重新验证
    captcha.resetCaptcha();
  }
};

// 实际执行账号密码登录
const executePasswordLogin = async () => {
  try {
    await login(passwordLoginForm);

    // 登录成功后，根据记住密码选项保存凭据
    rememberPasswordHook.saveCredentials(
      passwordLoginForm.userName,
      passwordLoginForm.password,
      rememberPasswordHook.rememberPassword.value
    );

    // 重置验证码状态
    captcha.resetCaptcha();

    // 登录成功后，useAuth会自动处理重定向
  } catch (error: any) {
    console.error("Password login failed:", error);
    // 重置验证码状态，允许重新尝试
    captcha.resetCaptcha();
    // 错误消息已经在store中处理了
  }
};

// 实际执行登录(根据登录方式)
const executeLogin = async () => {
  if (loginType.value === 'email') {
    await executeEmailLogin();
  } else {
    await executePasswordLogin();
  }
};

// 组件挂载时自动填充保存的凭据
onMounted(() => {
  rememberPasswordHook.autoFillForm(passwordLoginForm);
});
</script>

<style scoped>
.language-switcher-container {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 10;
}

/* 登录方式切换按钮 */
.login-type-switcher {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 2rem;
  background: rgba(255, 255, 255, 0.05);
  padding: 0.25rem;
  border-radius: 8px;
}

.login-type-btn {
  flex: 1;
  padding: 0.75rem;
  background: transparent;
  color: rgba(255, 255, 255, 0.7);
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.login-type-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.9);
}

.login-type-btn--active {
  background: var(--main-color, #00d4ff);
  color: #000;
  font-weight: 600;
}

.login-type-btn--active:hover {
  background: var(--main-color-hover, #00b8e6);
}

.form__title {
  text-align: center;
  margin-bottom: 2rem;
}

.form__title h2 {
  margin: 0 0 0.5rem 0;
  color: #eee;
}

.form__title p {
  margin: 0;
  color: #898383;
  font-size: 0.9rem;
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

/* 验证码输入框组 */
.form__input-group {
  display: flex;
  gap: 10px;
}

.form__input--code {
  flex: 1;
}

.form__code-btn {
  padding: 0 16px;
  background: var(--main-color, #52297f);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.875rem;
  white-space: nowrap;
  transition: background-color 0.3s ease;
}

.form__code-btn:hover:not(:disabled) {
  background: #7b52a7;
}

.form__code-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

/* 按钮禁用状态 */
.form__btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  pointer-events: none;
}

/* 链接样式优化 */
.form__text--center a {
  color: var(--main-color, #52297f);
  text-decoration: none;
  transition: color 0.3s ease;
}

.form__text--center a:hover {
  color: #00b8e6;
  text-decoration: underline;
}

/* 记住密码复选框样式 */
.form__group--checkbox {
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

.form__checkbox:hover .form__checkbox-mark {
  border-color: rgba(0, 212, 255, 0.6);
  background: rgba(0, 212, 255, 0.1);
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-type-btn {
    font-size: 0.8rem;
    padding: 0.6rem;
    gap: 0.3rem;
  }

  .login-type-btn i {
    font-size: 1rem;
  }
}
</style>
