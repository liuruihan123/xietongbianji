import { defineStore } from "pinia";
import { computed, ref } from "vue";
import { store } from "@/stores/helper";
import * as authApi from "@/api/auth";
import {
  getToken,
  setToken,
  removeToken,
  getUserInfo,
  setUserInfo,
  clearAuthStorage,
} from "./helper";
import type {
  LoginForm,
  EmailLoginForm,
  RegisterForm,
  ResetPasswordForm,
  UserInfo,
} from "@/types/auth";

// 简单的消息提示函数，替代ElMessage
function showMessage(message: string, type: "success" | "error" = "success") {
  console.log(`[${type.toUpperCase()}] ${message}`);

  // 创建简单的消息提示
  const messageDiv = document.createElement("div");
  messageDiv.className = `toast toast--${type}`;
  messageDiv.textContent = message;
  messageDiv.style.cssText = `
    position: fixed;
    top: 20px;
    right: 20px;
    padding: 12px 20px;
    border-radius: 6px;
    color: white;
    font-weight: 500;
    z-index: 9999;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    max-width: 300px;
    word-wrap: break-word;
    animation: slideInRight 0.3s ease-out;
    ${type === "success" ? "background: #52c41a;" : "background: #ff4d4f;"}
  `;

  // 添加动画样式
  if (!document.querySelector('#toast-styles')) {
    const style = document.createElement('style');
    style.id = 'toast-styles';
    style.textContent = `
      @keyframes slideInRight {
        from {
          transform: translateX(100%);
          opacity: 0;
        }
        to {
          transform: translateX(0);
          opacity: 1;
        }
      }
      @keyframes slideOutRight {
        from {
          transform: translateX(0);
          opacity: 1;
        }
        to {
          transform: translateX(100%);
          opacity: 0;
        }
      }
    `;
    document.head.appendChild(style);
  }

  document.body.appendChild(messageDiv);

  // 3秒后移除
  setTimeout(() => {
    if (document.body.contains(messageDiv)) {
      messageDiv.style.animation = 'slideOutRight 0.3s ease-in';
      setTimeout(() => {
        if (document.body.contains(messageDiv)) {
          document.body.removeChild(messageDiv);
        }
      }, 300);
    }
  }, 3000);
}

interface SessionResponse {
  auth: boolean
  model?: string
}

export interface AuthState {
  token: string | undefined
  session: SessionResponse | null
  userInfo: UserInfo | null
}

export const useAuthStore = defineStore("auth", () => {
  // 状态
  const token = ref<string | null>(getToken());
  const userInfo = ref<UserInfo | null>(getUserInfo());
  const session = ref<SessionResponse | null>(null);
  const loading = ref(false);

  // 计算属性
  const isAuthenticated = computed(() => !!token.value && !!userInfo.value);
  const hasSession = computed(() => !!session.value?.auth);

  /**
   * 密码登录
   */
  const login = async (loginData: LoginForm) => {
    loading.value = true;
    try {
      const response = await authApi.login(loginData);
      console.log(response)
      if (response.code === 200) {
        // 保存token和用户信息
        token.value = response.data.token;
        userInfo.value = response.data.userInfo as UserInfo;
        setToken(response.data.token);
        console.log("登录后的用户信息",response.data)
        setUserInfo(response.data.userInfo);

        showMessage("登录成功", "success");
        return Promise.resolve(response.data);
      } else {
        showMessage(response.msg || "登录失败", "error");
        return Promise.reject(new Error(response.msg));
      }
    } catch (error: any) {
      console.error("登录失败:", error);
      showMessage(error.message || "登录失败", "error");
      return Promise.reject(error);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 邮箱验证码登录
   */
  const emailLogin = async (emailForm: EmailLoginForm) => {
    loading.value = true;
    try {
      const response = await authApi.emailLogin(emailForm);
      if (response.code === 200) {
        token.value = response.data.token;
        userInfo.value = response.data.user as UserInfo;
        setToken(response.data.token);
        console.log("登录后的用户信息2",response.data)
        setUserInfo(response.data.user);

        showMessage("登录成功", "success");
        return Promise.resolve(response.data);
      } else {
        showMessage(response.msg || "登录失败", "error");
        return Promise.reject(new Error(response.msg));
      }
    } catch (error: any) {
      console.error("邮箱登录失败:", error);
      showMessage(error.message || "登录失败", "error");
      return Promise.reject(error);
    } finally {
      loading.value = false;
    }
  };

  /**
   * SSO 单点登录
   * @param ssoToken 外部系统传递的 SSO token
   */
  const ssoLogin = async (ssoToken: string) => {
    loading.value = true;
    try {
      const response = await authApi.ssoLogin(ssoToken);
      if (response.code === 200) {
        // 保存 token 和用户信息
        token.value = response.data.token;
        userInfo.value = response.data.userInfo as UserInfo;
        setToken(response.data.token);
        console.log("SSO 登录后的用户信息", response.data);
        setUserInfo(response.data.userInfo);

        showMessage("登录成功", "success");
        return Promise.resolve(response.data);
      } else {
        showMessage(response.msg || "SSO 登录失败", "error");
        return Promise.reject(new Error(response.msg));
      }
    } catch (error: any) {
      console.error("SSO 登录失败:", error);
      showMessage(error.message || "SSO 登录失败", "error");
      return Promise.reject(error);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 注册
   */
  const register = async (registerData: RegisterForm) => {
    loading.value = true;
    try {
      const response = await authApi.register(registerData);
      if (response.code === 200) {
        showMessage("注册成功，请登录", "success");
        return Promise.resolve(response.data);
      } else {
        showMessage(response.msg || "注册失败", "error");
        return Promise.reject(new Error(response.msg));
      }
    } catch (error: any) {
      console.error("注册失败:", error);
      showMessage(error.message || "注册失败", "error");
      return Promise.reject(error);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 发送邮箱验证码
   */
  const sendEmailCode = async (userName: string) => {
    try {
      const response = await authApi.sendEmailCode(userName);
      if (response.code === 200) {
        showMessage("验证码已发送，请查收邮箱", "success");
        return Promise.resolve(response.data);
      } else {
        showMessage(response.msg || "发送验证码失败", "error");
        return Promise.reject(new Error(response.msg));
      }
    } catch (error: any) {
      console.error("发送验证码失败:", error);
      showMessage(error.message || "发送验证码失败", "error");
      return Promise.reject(error);
    }
  };



  /**
   * 重置密码
   */
  const resetPassword = async (resetData: ResetPasswordForm) => {
    loading.value = true;
    try {
      const response = await authApi.resetPassword(resetData);
      if (response.code === 200) {
        showMessage("密码重置成功", "success");
        return Promise.resolve(response.data);
      } else {
        showMessage(response.msg || "密码重置失败", "error");
        return Promise.reject(new Error(response.msg));
      }
    } catch (error: any) {
      console.error("重置密码失败:", error);
      showMessage(error.message || "密码重置失败", "error");
      return Promise.reject(error);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 检查用户名是否唯一
   */
  const checkUserNameUnique = async (userName: string) => {
    try {
      const response = await authApi.checkUserNameUnique(userName);
      return response.data;
    } catch (error: any) {
      console.error("检查用户名失败:", error);
      return true; // 默认假设用户名可用
    }
  };

  /**
   * 获取用户信息
   */
  const fetchUserInfo = async (): Promise<UserInfo> => {
    try {
      const response = await authApi.getUserInfo();
      console.log("response=============",response)
      if (response.code === 200) {
        const userData = response.data.userInfo || response.data;
        userInfo.value = userData as UserInfo;
        console.log("登录后的用户信息3",response.data)
        setUserInfo(userData as UserInfo);
        return userData as UserInfo;
      } else {
        return Promise.reject(new Error(response.msg));
      }
    } catch (error: any) {
      console.error("获取用户信息失败3:", error);
      return Promise.reject(error);
    }
  };

  /**
   * 退出登录
   */
  const logout = async () => {
    try {
      // 调用登出API
      await authApi.logout();
    } catch (error) {
      console.error("退出登录API失败:", error);
    } finally {
      // 无论API是否成功，都清除本地状态
      clearUserState();
      showMessage("已退出登录", "success");
    }
  };

  /**
   * 清理用户状态（用于认证失败时）
   */
  const clearUserState = () => {
    token.value = null;
    userInfo.value = null;
    session.value = null;
    clearAuthStorage();

    // 清理其他可能的缓存
    try {
      // 清理 localStorage 中的其他用户相关数据
      const keysToRemove = ['USER_INFO', 'USER_PREFERENCES', 'CHAT_HISTORY'];
      keysToRemove.forEach(key => {
        localStorage.removeItem(key);
      });

      // 清理 sessionStorage
      sessionStorage.clear();
    } catch (error) {
      console.error('Clear cache failed:', error);
    }
  };

  /**
   * 验证用户登录状态
   */
  const validateUserStatus = async () => {
    if (!token.value) {
      return false;
    }

    try {
      // 使用 getUserInfo 接口验证用户状态
      await fetchUserInfo();
      return true;
    } catch (error: any) {
      console.warn('User validation failed:', error.message);

      // 只有在明确的认证错误时才清理状态
      const errorMsg = error.message || '';
      const isAuthError = errorMsg.includes('401') ||
                         errorMsg.includes('403') ||
                         errorMsg.includes('登录已过期') ||
                         errorMsg.includes('权限不足') ||
                         errorMsg.includes('无效的会话');

      if (isAuthError) {
        console.warn('Authentication error detected, clearing user state');
        clearUserState();
        return false;
      }

      // 对于其他错误（如网络错误、服务器错误等），不清理状态
      console.warn('Non-auth error during validation, keeping user state');
      return true; // 返回true让用户继续访问，让页面自己处理错误
    }
  };

  /**
   * 设置用户信息
   */
  const setUserInfoData = (userData: UserInfo) => {
    userInfo.value = userData;
    console.log("设置用户信息4",userData)
    setUserInfo(userData);
  };

  /**
   * 初始化认证状态
   * 在应用启动时调用，验证本地token是否有效
   */
  const initializeAuth = async () => {
    if (token.value) {
      try {
        await fetchUserInfo();
      } catch (error) {
        // 如果获取用户信息失败，清除本地状态
        console.warn("初始化认证失败，清除本地状态:", error);
        clearUserState();
      }
    }
  };

  return {
    // 状态
    token,
    userInfo,
    session,
    loading,
    // 计算属性
    isAuthenticated,
    hasSession,
    // 方法
    login,
    emailLogin,
    ssoLogin,
    register,
    resetPassword,
    sendEmailCode,
    checkUserNameUnique,
    fetchUserInfo,
    logout,
    clearUserState,
    validateUserStatus,
    setUserInfo: setUserInfoData,
    initializeAuth,
  };
});

export function useAuthStoreWithout() {
  return useAuthStore(store);
}
