import { get, post } from "@/utils/request";
import type {
  LoginForm,
  EmailLoginForm,
  RegisterForm,
  ResetPasswordForm,
  LoginResponse,
  LoginResponse2,
  UserInfo,
  ApiResponse,
} from "@/types/auth";

/**
 * 用户账号密码登录
 */
export function login(params: LoginForm): Promise<ApiResponse<LoginResponse2>> {
  return post({
    url: "/auth/login",
    data: params,
  });
}

/**
 * 邮箱验证码登录
 */
export function emailLogin(params: EmailLoginForm): Promise<ApiResponse<LoginResponse>> {
  return post({
    url: "/auth/emailLogin",
    data: params,
  });
}

/**
 * 用户注册
 */
export function register(params: RegisterForm): Promise<ApiResponse<any>> {
  return post({
    url: "/auth/register",
    data: {
      userName: params.userName,
      password: params.password,
      code: params.verificationCode
    },
  });
}

/**
 * 发送手机验证码
 */
export function sendPhoneCode(phonenumber: string): Promise<ApiResponse<any>> {
  return get({
    url: "/resource/sms/code",
    data: { phonenumber },
  });
}

/**
 * 发送邮箱验证码
 */
export function sendEmailCode(userName: string): Promise<ApiResponse<any>> {
  return post({
    url: "/resource/email/code",
    data: { userName },
  });
}

/**
 * 重置密码
 */
export function resetPassword(params: ResetPasswordForm): Promise<ApiResponse<any>> {
  return post({
    url: "/reset/password",
    data: {
      userName: params.userName,
      password: params.password,
      code: params.verificationCode
    },
  });
}

/**
 * 检查用户名是否唯一
 */
export function checkUserNameUnique(userName: string): Promise<ApiResponse<boolean>> {
  return post({
    url: "/auth/checkUserNameUnique",
    data: { userName },
  });
}

/**
 * 获取用户信息
 */
export function getUserInfo(): Promise<ApiResponse<{ userInfo: UserInfo }>> {
  return get({
    url: "/system/user/getInfo",
  });
}

/**
 * 退出登录
 */
export function logout(): Promise<ApiResponse<any>> {
  return post({
    url: "/auth/logout",
  });
}

/**
 * 领取新用户会员
 */
export function grantNewUserVip(userId: number): Promise<ApiResponse<any>> {
  return post({
    url: `/system/user/grantNewUserVip/${userId}`,
  });
}

/**
 * SSO 单点登录
 * @param token 外部系统传递的 SSO token
 */
export function ssoLogin(token: string): Promise<ApiResponse<LoginResponse2>> {
  return get({
    url: "/auth/sso/login",
    data: { token },
  });
}
