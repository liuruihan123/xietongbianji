export interface CryptoNotificationRecordVO {
  /**
   * 主键ID
   */
  id: string | number;

  /**
   * 事件ID
   */
  eventId: string | number;

  /**
   * 用户ID
   */
  userId: string | number;

  /**
   * 手机号码
   */
  phoneNumber: string;

  /**
   * 通知类型（SMS-短信，VOICE-语音，EMAIL-邮件）
   */
  notificationType: string;

  /**
   * 通知内容
   */
  notificationContent: string;

  /**
   * 通知状态（0-待发送，1-发送成功，2-发送失败）
   */
  notificationStatus: number;

  /**
   * 发送时间
   */
  sendTime: string;

  /**
   * 响应消息
   */
  responseMessage: string;

  /**
   * 服务提供商
   */
  provider: string | number;

  /**
   * 服务商消息ID
   */
  providerMessageId: string | number;

  /**
   * 费用金额
   */
  costAmount: number;

  /**
   * 重试次数
   */
  retryCount: number;

  /**
   * 状态（0-禁用，1-启用）
   */
  status: number;

  /**
   * 备注
   */
  remark: string;

}

export interface CryptoNotificationRecordForm extends BaseEntity {
  /**
   * 主键ID
   */
  id?: string | number;

  /**
   * 事件ID
   */
  eventId?: string | number;

  /**
   * 用户ID
   */
  userId?: string | number;

  /**
   * 手机号码
   */
  phoneNumber?: string;

  /**
   * 通知类型（SMS-短信，VOICE-语音，EMAIL-邮件）
   */
  notificationType?: string;

  /**
   * 通知内容
   */
  notificationContent?: string;

  /**
   * 通知状态（0-待发送，1-发送成功，2-发送失败）
   */
  notificationStatus?: number;

  /**
   * 发送时间
   */
  sendTime?: string;

  /**
   * 响应消息
   */
  responseMessage?: string;

  /**
   * 服务提供商
   */
  provider?: string | number;

  /**
   * 服务商消息ID
   */
  providerMessageId?: string | number;

  /**
   * 费用金额
   */
  costAmount?: number;

  /**
   * 重试次数
   */
  retryCount?: number;

  /**
   * 状态（0-禁用，1-启用）
   */
  status?: number;

  /**
   * 备注
   */
  remark?: string;

}

export interface CryptoNotificationRecordQuery extends PageQuery {
  /**
   * 事件ID
   */
  eventId?: string | number;

  /**
   * 用户ID
   */
  userId?: string | number;

  /**
   * 手机号码
   */
  phoneNumber?: string;

  /**
   * 通知类型（SMS-短信，VOICE-语音，EMAIL-邮件）
   */
  notificationType?: string;

  /**
   * 通知内容
   */
  notificationContent?: string;

  /**
   * 通知状态（0-待发送，1-发送成功，2-发送失败）
   */
  notificationStatus?: number;

  /**
   * 发送时间
   */
  sendTime?: string;

  /**
   * 响应消息
   */
  responseMessage?: string;

  /**
   * 服务提供商
   */
  provider?: string | number;

  /**
   * 服务商消息ID
   */
  providerMessageId?: string | number;

  /**
   * 费用金额
   */
  costAmount?: number;

  /**
   * 重试次数
   */
  retryCount?: number;

  /**
   * 状态（0-禁用，1-启用）
   */
  status?: number;

}
