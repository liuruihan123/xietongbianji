export interface CryptoSubscriptionUserVO {
  /**
   * 主键ID
   */
  id: string | number;

  /**
   * 用户ID
   */
  userId: string | number;

  /**
   * 用户名
   */
  username: string;

  /**
   * 手机号码
   */
  phoneNumber: string;

  /**
   * 邮箱地址
   */
  email: string;

  /**
   * 订阅的交易所（逗号分隔）
   */
  subscribedExchanges: string;

  /**
   * 通知方式（SMS-短信，VOICE-语音，EMAIL-邮件，ALL-全部）
   */
  notificationMethod: string;

  /**
   * 是否启用通知
   */
  notificationEnabled: number;

  /**
   * 最小市值过滤（USD）
   */
  minMarketCapFilter: number;

  /**
   * 是否排除稳定币
   */
  excludeStablecoins: number;

  /**
   * 是否排除测试币
   */
  excludeTestCoins: number;

  /**
   * 订阅开始时间
   */
  subscriptionStartTime: string;

  /**
   * 订阅结束时间
   */
  subscriptionEndTime: string;

  /**
   * 订阅状态（0-未激活，1-激活，2-暂停，3-过期）
   */
  subscriptionStatus: number;

  /**
   * 最后通知时间
   */
  lastNotificationTime: string;

  /**
   * 通知次数统计
   */
  notificationCount: number;

  /**
   * 状态（0-禁用，1-启用）
   */
  status: number;

  /**
   * 备注
   */
  remark: string;

}

export interface CryptoSubscriptionUserForm extends BaseEntity {
  /**
   * 主键ID
   */
  id?: string | number;

  /**
   * 用户ID
   */
  userId?: string | number;

  /**
   * 用户名
   */
  username?: string;

  /**
   * 手机号码
   */
  phoneNumber?: string;

  /**
   * 邮箱地址
   */
  email?: string;

  /**
   * 订阅的交易所（逗号分隔）
   */
  subscribedExchanges?: string;

  /**
   * 通知方式（SMS-短信，VOICE-语音，EMAIL-邮件，ALL-全部）
   */
  notificationMethod?: string;

  /**
   * 是否启用通知
   */
  notificationEnabled?: number;

  /**
   * 最小市值过滤（USD）
   */
  minMarketCapFilter?: number;

  /**
   * 是否排除稳定币
   */
  excludeStablecoins?: number;

  /**
   * 是否排除测试币
   */
  excludeTestCoins?: number;

  /**
   * 订阅开始时间
   */
  subscriptionStartTime?: string;

  /**
   * 订阅结束时间
   */
  subscriptionEndTime?: string;

  /**
   * 订阅状态（0-未激活，1-激活，2-暂停，3-过期）
   */
  subscriptionStatus?: number;

  /**
   * 最后通知时间
   */
  lastNotificationTime?: string;

  /**
   * 通知次数统计
   */
  notificationCount?: number;

  /**
   * 状态（0-禁用，1-启用）
   */
  status?: number;

  /**
   * 备注
   */
  remark?: string;

}

export interface CryptoSubscriptionUserQuery extends PageQuery {
  /**
   * 用户ID
   */
  userId?: string | number;

  /**
   * 用户名
   */
  username?: string;

  /**
   * 手机号码
   */
  phoneNumber?: string;

  /**
   * 邮箱地址
   */
  email?: string;

  /**
   * 订阅的交易所（逗号分隔）
   */
  subscribedExchanges?: string;

  /**
   * 通知方式（SMS-短信，VOICE-语音，EMAIL-邮件，ALL-全部）
   */
  notificationMethod?: string;

  /**
   * 是否启用通知
   */
  notificationEnabled?: number;

  /**
   * 最小市值过滤（USD）
   */
  minMarketCapFilter?: number;

  /**
   * 是否排除稳定币
   */
  excludeStablecoins?: number;

  /**
   * 是否排除测试币
   */
  excludeTestCoins?: number;

  /**
   * 订阅开始时间
   */
  subscriptionStartTime?: string;

  /**
   * 订阅结束时间
   */
  subscriptionEndTime?: string;

  /**
   * 订阅状态（0-未激活，1-激活，2-暂停，3-过期）
   */
  subscriptionStatus?: number;

  /**
   * 最后通知时间
   */
  lastNotificationTime?: string;

  /**
   * 通知次数统计
   */
  notificationCount?: number;

  /**
   * 状态（0-禁用，1-启用）
   */
  status?: number;

}
