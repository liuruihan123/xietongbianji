/**
 * 系统设置模块类型定义
 */

/**
 * 性别枚举
 */
export enum Gender {
  /** 未知 */
  UNKNOWN = 0,
  /** 男 */
  MALE = 1,
  /** 女 */
  FEMALE = 2,
}

/**
 * 日志状态枚举
 */
export enum LogStatus {
  /** 成功 */
  SUCCESS = 'SUCCESS',
  /** 失败 */
  FAILED = 'FAILED',
}

/**
 * 布局模式枚举
 */
export enum LayoutMode {
  /** 侧边菜单 */
  SIDE = 'side',
  /** 顶部菜单 */
  TOP = 'top',
  /** 混合菜单 */
  MIX = 'mix',
}

/**
 * 用户个人信息
 */
export interface UserProfile {
  /** 用户ID */
  id: number
  /** 用户名 */
  username: string
  /** 昵称 */
  nickname: string
  /** 头像URL */
  avatar: string
  /** 邮箱 */
  email: string
  /** 手机号 */
  phone: string
  /** 性别 */
  gender: Gender
  /** 生日 */
  birthday: string
  /** 个性签名 */
  signature: string
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime: string
}

/**
 * 更新个人信息参数
 */
export interface UpdateProfileParams {
  /** 昵称 */
  nickname?: string
  /** 邮箱 */
  email?: string
  /** 手机号 */
  phone?: string
  /** 性别 */
  gender?: Gender
  /** 生日 */
  birthday?: string
  /** 个性签名 */
  signature?: string
}

/**
 * 修改密码参数
 */
export interface UpdatePasswordParams {
  /** 旧密码 */
  oldPassword: string
  /** 新密码 */
  newPassword: string
  /** 确认密码 */
  confirmPassword: string
}

/**
 * 更新头像参数
 */
export interface UpdateAvatarParams {
  /** 头像URL */
  avatar: string
}

/**
 * 系统配置信息
 */
export interface SystemConfig {
  /** 系统名称 */
  systemName: string
  /** 系统Logo */
  logo: string
  /** 网站图标 */
  favicon: string
  /** 版权信息 */
  copyright: string
  /** ICP备案号 */
  icp: string
  /** 主题色 */
  primaryColor: string
  /** 布局模式 */
  layout: LayoutMode
  /** 固定头部 */
  fixedHeader: boolean
  /** 固定侧边栏 */
  fixedSidebar: boolean
  /** 启用注册 */
  enableRegister: boolean
  /** 启用验证码 */
  enableCaptcha: boolean
  /** 启用水印 */
  enableWatermark: boolean
}

/**
 * 更新系统配置参数
 */
export interface UpdateSystemConfigParams extends Partial<SystemConfig> {}

/**
 * 操作日志
 */
export interface OperationLog {
  /** 日志ID */
  id: number
  /** 用户名 */
  username: string
  /** 模块 */
  module: string
  /** 操作 */
  operation: string
  /** 请求方法 */
  method: string
  /** 请求参数 */
  params: string
  /** 返回结果 */
  result: string
  /** IP地址 */
  ip: string
  /** 地理位置 */
  location: string
  /** 浏览器 */
  browser: string
  /** 操作系统 */
  os: string
  /** 状态 */
  status: LogStatus
  /** 错误信息 */
  errorMsg: string
  /** 操作时间 */
  createTime: string
}

/**
 * 操作日志查询参数
 */
export interface OperationLogListParams {
  /** 页码 */
  page: number
  /** 每页数量 */
  pageSize: number
  /** 用户名 */
  username?: string
  /** 模块 */
  module?: string
  /** 状态 */
  status?: LogStatus | ''
  /** 开始时间 */
  startTime?: string
  /** 结束时间 */
  endTime?: string
}

/**
 * 性别选项
 */
export const GENDER_OPTIONS = [
  { label: '未知', value: Gender.UNKNOWN },
  { label: '男', value: Gender.MALE },
  { label: '女', value: Gender.FEMALE },
]

/**
 * 日志状态选项
 */
export const LOG_STATUS_OPTIONS = [
  { label: '全部', value: '' },
  { label: '成功', value: LogStatus.SUCCESS },
  { label: '失败', value: LogStatus.FAILED },
]

/**
 * 布局模式选项
 */
export const LAYOUT_MODE_OPTIONS = [
  { label: '侧边菜单', value: LayoutMode.SIDE },
  { label: '顶部菜单', value: LayoutMode.TOP },
  { label: '混合菜单', value: LayoutMode.MIX },
]

/**
 * 获取性别文本
 */
export function getGenderText(gender: Gender): string {
  const option = GENDER_OPTIONS.find((opt) => opt.value === gender)
  return option?.label || '未知'
}

/**
 * 获取日志状态文本
 */
export function getLogStatusText(status: LogStatus): string {
  switch (status) {
    case LogStatus.SUCCESS:
      return '成功'
    case LogStatus.FAILED:
      return '失败'
    default:
      return '未知'
  }
}

/**
 * 获取日志状态颜色
 */
export function getLogStatusColor(status: LogStatus): string {
  switch (status) {
    case LogStatus.SUCCESS:
      return 'success'
    case LogStatus.FAILED:
      return 'error'
    default:
      return 'default'
  }
}

/**
 * 获取布局模式文本
 */
export function getLayoutModeText(layout: LayoutMode): string {
  const option = LAYOUT_MODE_OPTIONS.find((opt) => opt.value === layout)
  return option?.label || '侧边菜单'
}

