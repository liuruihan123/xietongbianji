import { computed } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores";
import type { ComputedRef } from "vue";
import type {
  LoginForm,
  EmailLoginForm,
  RegisterForm,
  ResetPasswordForm,
  ForgotPasswordForm,
  UserInfo,
} from "@/types/auth";

export interface UseAuthReturn {
  // 状态
  isAuthenticated: ComputedRef<boolean>;
  isLoggedIn: ComputedRef<boolean>;
  userInfo: ComputedRef<UserInfo | null>;
  token: ComputedRef<string | null>;
  session: ComputedRef<any>;
  loading: ComputedRef<boolean>;

  // 方法
  login: (form: LoginForm) => Promise<any>;
  emailLogin: (form: EmailLoginForm) => Promise<any>;
  register: (form: RegisterForm) => Promise<any>;
  resetPassword: (form: ResetPasswordForm) => Promise<any>;
  forgotPassword: (form: ForgotPasswordForm) => Promise<any>;
  sendEmailCode: (userName: string) => Promise<any>;
  logout: () => Promise<void>;
  checkUserNameUnique: (userName: string) => Promise<boolean>;
  fetchUserInfo: () => Promise<UserInfo>;
  validateUserStatus: () => Promise<boolean>;
  initializeAuth: () => Promise<void>;
  getSession: () => any;
}

/**
 * 认证相关的组合式函数
 * 封装了所有认证相关的逻辑
 */
export function useAuth(): UseAuthReturn {
  const authStore = useAuthStore();
  const router = useRouter();

  // 状态 - 从 store 中获取
  const isAuthenticated = computed(() => authStore.isAuthenticated);
  const isLoggedIn = computed(() => authStore.isAuthenticated);
  const userInfo = computed(() => authStore.userInfo);
  const token = computed(() => authStore.token);
  const session = computed(() => authStore.session);
  const loading = computed(() => authStore.loading);

  /**
   * 登录
   */
  const login = async (form: LoginForm) => {
    try {
      const result = await authStore.login(form);
      // 登录成功后重定向到首页或指定页面
      const redirect = router.currentRoute.value.query.redirect as string;
      await router.push(redirect || "/");
      return result;
    } catch (error) {
      throw error;
    }
  };

  /**
   * 注册
   */
  const register = async (form: RegisterForm) => {
    try {
      const result = await authStore.register(form);
      // 注册成功后跳转到登录页
      await router.push("/signin");
      return result;
    } catch (error) {
      throw error;
    }
  };

  /**
   * 邮箱验证码登录
   */
  const emailLogin = async (form: EmailLoginForm) => {
    try {
      const result = await authStore.emailLogin(form);
      // 登录成功后重定向到首页或指定页面
      const redirect = router.currentRoute.value.query.redirect as string;
      await router.push(redirect || "/");
      return result;
    } catch (error) {
      throw error;
    }
  };

  /**
   * 重置密码
   */
  const resetPassword = async (form: ResetPasswordForm) => {
    return await authStore.resetPassword(form);
  };

  /**
   * 发送邮箱验证码
   */
  const sendEmailCode = async (userName: string) => {
    return await authStore.sendEmailCode(userName);
  };

  /**
   * 退出登录
   */
  const logout = async () => {
    await authStore.logout();
    // 退出后重定向到登录页
    await router.push("/signin");
  };

  /**
   * 检查用户名是否唯一
   */
  const checkUserNameUnique = async (userName: string): Promise<boolean> => {
    return await authStore.checkUserNameUnique(userName);
  };

  /**
   * 获取用户信息
   */
  const fetchUserInfo = async (): Promise<UserInfo> => {
    return await authStore.fetchUserInfo() as UserInfo;
  };

  

  /**
   * 验证用户状态
   */
  const validateUserStatus = async (): Promise<boolean> => {
    return await authStore.validateUserStatus();
  };

  /**
   * 初始化认证状态
   */
  const initializeAuth = async (): Promise<void> => {
    return await authStore.initializeAuth();
  };

  /**
   * 忘记密码
   */
  const forgotPassword = async (form: ForgotPasswordForm) => {
    return await authStore.resetPassword({
      userName: form.email,
      password: '',
      confirmPassword: '',
      verificationCode: ''
    });
  };

  /**
   * 获取会话信息
   */
  const getSession = () => {
    return authStore.session;
  };

  return {
    // 状态
    isAuthenticated,
    isLoggedIn,
    userInfo,
    token,
    session,
    loading,

    // 方法
    login,
    emailLogin,
    register,
    resetPassword,
    forgotPassword,
    sendEmailCode,
    logout,
    checkUserNameUnique,
    fetchUserInfo,
    validateUserStatus,
    initializeAuth,
    getSession,
  };
}
