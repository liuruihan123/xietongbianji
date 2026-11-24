// 认证相关类型定义

// 基础登录表单
export interface LoginForm {
  userName: string; // 用户名（邮箱）
  password: string; // 密码
}

// 邮箱验证码登录
export interface EmailLoginForm {
  email: string; // 邮箱地址
  emailCode: string; // 邮箱验证码
}

// 注册表单
export interface RegisterForm {
  userName: string; // 用户名（邮箱）
  password: string; // 密码
  confirmPassword: string; // 确认密码
  verificationCode: string; // 验证码
}

// 重置密码表单
export interface ResetPasswordForm {
  userName: string; // 用户名（邮箱）
  password: string; // 新密码
  confirmPassword: string; // 确认新密码
  verificationCode: string; // 验证码
}

// 忘记密码表单（兼容性）
export interface ForgotPasswordForm {
  email: string; // 邮箱地址
}

// 用户信息
export interface UserInfo {
  id?: number; // 用户ID（兼容性）
  userId: number; // 用户ID
  userName: string; // 用户名
  nickName: string; // 昵称
  avatar: string; // 头像
  email?: string; // 邮箱
  phonenumber?: string; // 手机号
  points?: number; // 账户余额
  userGrade?: string; // 用户等级
  vipExpiryDate?: string | number; // VIP过期时间
  loginDate?: string; // 上次登录时间
}

// 登录响应数据
export interface LoginResponse {
  token: string; // 登录token
  user: UserInfo; // 用户信息
}

// 登录响应数据
export interface LoginResponse2 {
  token: string; // 登录token
  userInfo: UserInfo; // 用户信息
}

// 注册响应数据
export interface RegisterResponse {
  success: boolean; // 注册是否成功
  message: string; // 消息
}

// API 响应基础结构
export interface ApiResponse<T = any> {
  code: number; // 状态码
  msg: string; // 消息
  data?: T; // 数据
  rows?: any; // 列表数据（兼容后端直接返回rows的情况）
  total?: number; // 总数（兼容后端直接返回total的情况）
  [key: string]: any; // 允许其他属性
}

// 验证码发送响应
export interface SendCodeResponse {
  success: boolean; // 是否发送成功
  message: string; // 消息
  ttl?: number; // 验证码有效期（秒）
}

// 用户名检查响应
export interface CheckUsernameResponse {
  unique: boolean; // 用户名是否唯一
  message?: string; // 消息
}

// 表单验证错误
export interface FormValidationErrors {
  [key: string]: string | undefined;
}

// 密码强度等级
export interface PasswordStrength {
  score: number; // 强度分数 0-5
  level: "weak" | "fair" | "good" | "medium" | "strong" | "very-strong"; // 强度等级
  percentage: number; // 强度百分比
  feedback: string[]; // 改进建议
  color?: string; // 颜色
}

// 认证状态
export interface AuthState {
  token: string | null; // 认证令牌
  isAuthenticated: boolean; // 是否已认证
  userInfo: UserInfo | null; // 用户信息
  loading: boolean; // 是否加载中
}

// Google 用户信息
export interface GoogleUserInfo {
  id: string;
  sub: string;
  email: string;
  email_verified: boolean;
  name: string;
  picture?: string;
  given_name?: string;
  family_name?: string;
  locale?: string;
}
