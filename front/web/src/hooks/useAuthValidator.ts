import { ref, computed } from "vue";
import type { Ref } from "vue";
import type { PasswordStrength, FormValidationErrors } from "@/types/auth";

/**
 * 邮箱验证正则
 */
const EMAIL_REGEX = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

/**
 * 手机号验证正则（中国大陆）
 */
const PHONE_REGEX = /^1[3-9]\d{9}$/;

/**
 * 密码强度检测
 */
export function usePasswordStrength() {
  const calculatePasswordStrength = (password: string): PasswordStrength => {
    if (!password) {
      return {
        score: 0,
        level: "weak",
        percentage: 0,
        feedback: ["请输入密码"],
      };
    }

    let score = 0;
    const feedback: string[] = [];

    // 长度检查
    if (password.length >= 8) {
      score += 1;
    } else {
      feedback.push("密码长度至少8位");
    }

    // 包含小写字母
    if (/[a-z]/.test(password)) {
      score += 1;
    } else {
      feedback.push("包含小写字母");
    }

    // 包含大写字母
    if (/[A-Z]/.test(password)) {
      score += 1;
    } else {
      feedback.push("包含大写字母");
    }

    // 包含数字
    if (/\d/.test(password)) {
      score += 1;
    } else {
      feedback.push("包含数字");
    }

    // 包含特殊字符
    if (/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) {
      score += 1;
    } else {
      feedback.push("包含特殊字符");
    }

    let level: "weak" | "fair" | "good" | "medium" | "strong" | "very-strong";
    let percentage: number;
    let color: string;

    if (score <= 1) {
      level = "weak";
      percentage = 20;
      color = "#ff4d4f";
    } else if (score <= 2) {
      level = "fair";
      percentage = 40;
      color = "#ff7a45";
    } else if (score <= 3) {
      level = "good";
      percentage = 60;
      color = "#ffa940";
    } else if (score <= 4) {
      level = "strong";
      percentage = 80;
      color = "#52c41a";
    } else {
      level = "very-strong";
      percentage = 100;
      color = "#389e0d";
    }

    return {
      score,
      level,
      percentage,
      color,
      feedback: feedback.slice(0, 3), // 只显示前3个建议
    };
  };

  return {
    calculatePasswordStrength,
  };
}

/**
 * 表单验证组合函数
 */
export function useFormValidator() {
  const errors = ref<FormValidationErrors>({});

  /**
   * 验证必填字段
   */
  const validateRequired = (value: string, fieldName = "此字段"): string | undefined => {
    if (!value || value.trim() === "") {
      return `请输入${fieldName}`;
    }
    return undefined;
  };

  /**
   * 验证邮箱
   */
  const validateEmail = (email: string): string | undefined => {
    if (!email) {
      return "请输入邮箱地址";
    }
    if (!EMAIL_REGEX.test(email)) {
      return "请输入有效的邮箱地址";
    }
    return undefined;
  };

  /**
   * 验证手机号
   */
  const validatePhone = (phone: string): string | undefined => {
    if (!phone) {
      return "请输入手机号码";
    }
    if (!PHONE_REGEX.test(phone)) {
      return "请输入正确的手机号码格式";
    }
    return undefined;
  };

  /**
   * 验证密码
   */
  const validatePassword = (
    password: string,
    minLength = 6
  ): string | undefined => {
    if (!password) {
      return "请输入密码";
    }
    if (password.length < minLength) {
      return `密码长度至少${minLength}位`;
    }
    return undefined;
  };

  /**
   * 验证确认密码
   */
  const validateConfirmPassword = (
    password: string,
    confirmPassword: string
  ): string | undefined => {
    if (!confirmPassword) {
      return "请确认密码";
    }
    if (password !== confirmPassword) {
      return "两次输入的密码不一致";
    }
    return undefined;
  };

  /**
   * 验证验证码
   */
  const validateVerificationCode = (code: string): string | undefined => {
    if (!code) {
      return "请输入验证码";
    }
    if (code.length !== 6) {
      return "验证码为6位数字";
    }
    if (!/^\d{6}$/.test(code)) {
      return "验证码只能包含数字";
    }
    return undefined;
  };

  /**
   * 设置字段错误
   */
  const setFieldError = (field: string, error: string | undefined) => {
    errors.value[field] = error;
  };

  /**
   * 清除字段错误
   */
  const clearFieldError = (field: string) => {
    delete errors.value[field];
  };

  /**
   * 清除所有错误
   */
  const clearAllErrors = () => {
    errors.value = {};
  };

  /**
   * 获取字段错误
   */
  const getFieldError = (field: string): string | undefined => {
    return errors.value[field];
  };

  /**
   * 检查是否有错误
   */
  const hasErrors = computed(() => {
    return Object.keys(errors.value).some((key) => errors.value[key]);
  });

  /**
   * 验证登录表单
   */
  const validateLoginForm = (form: { userName: string; password: string }) => {
    clearAllErrors();

    const userNameError = validateRequired(form.userName, "用户名");
    const passwordError = validatePassword(form.password);

    if (userNameError) setFieldError("userName", userNameError);
    if (passwordError) setFieldError("password", passwordError);

    return !userNameError && !passwordError;
  };

  /**
   * 验证注册表单
   */
  const validateRegisterForm = (form: {
    userName: string;
    password: string;
    confirmPassword: string;
    verificationCode: string;
  }) => {
    clearAllErrors();

    const emailError = validateEmail(form.userName);
    const passwordError = validatePassword(form.password, 8);
    const confirmPasswordError = validateConfirmPassword(
      form.password,
      form.confirmPassword
    );
    const codeError = validateVerificationCode(form.verificationCode);

    if (emailError) setFieldError("userName", emailError);
    if (passwordError) setFieldError("password", passwordError);
    if (confirmPasswordError)
      setFieldError("confirmPassword", confirmPasswordError);
    if (codeError) setFieldError("verificationCode", codeError);

    return !emailError && !passwordError && !confirmPasswordError && !codeError;
  };

  /**
   * 验证忘记密码表单
   */
  const validateForgotPasswordForm = (form: { email: string }) => {
    clearAllErrors();

    const emailError = validateEmail(form.email);
    if (emailError) setFieldError("email", emailError);

    return !emailError;
  };

  return {
    errors,
    hasErrors,
    validateRequired,
    validateEmail,
    validatePhone,
    validatePassword,
    validateConfirmPassword,
    validateVerificationCode,
    setFieldError,
    clearFieldError,
    clearAllErrors,
    getFieldError,
    validateLoginForm,
    validateRegisterForm,
    validateForgotPasswordForm,
  };
}

/**
 * 验证规则接口
 */
interface ValidationRule {
  required?: boolean
  message?: string
  trigger?: string
  type?: string
  min?: number
  validator?: (value: string) => boolean | Promise<boolean>
}

/**
 * 获取登录表单验证规则
 */
export function useLoginRules() {
  const getLoginRules = (): { userName: ValidationRule[], password: ValidationRule[] } => ({
    userName: [
      { required: true, message: '请输入用户名', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, message: '密码长度至少6位', trigger: 'blur' }
    ]
  })

  return { getLoginRules }
}

/**
 * 认证验证器组合函数（兼容性导出）
 */
export function useAuthValidator() {
  const formValidator = useFormValidator()
  const passwordStrength = usePasswordStrength()
  const loginRules = useLoginRules()

  return {
    ...formValidator,
    ...passwordStrength,
    ...loginRules,
    checkPasswordStrength: passwordStrength.calculatePasswordStrength
  }
}
