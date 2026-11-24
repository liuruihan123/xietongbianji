export interface CryptoNotificationLogVO {
  /**
   * 主键ID
   */
  id: string | number;

  /**
   * 交易所代码
   */
  exchangeCode: string;

  /**
   * 币种符号
   */
  symbol: string;

  /**
   * 交易对
   */
  tradingPair: string;

  /**
   * 通知类型
   */
  notificationType: string;

  /**
   * 通知状态
   */
  notificationStatus: number;

  /**
   * 通知内容
   */
  notificationContent: string;

  /**
   * 通知时间
   */
  notificationTime: string;

}

export interface CryptoNotificationLogForm extends BaseEntity {
  /**
   * 主键ID
   */
  id?: string | number;

  /**
   * 交易所代码
   */
  exchangeCode?: string;

  /**
   * 币种符号
   */
  symbol?: string;

  /**
   * 交易对
   */
  tradingPair?: string;

  /**
   * 通知类型
   */
  notificationType?: string;

  /**
   * 通知状态
   */
  notificationStatus?: number;

  /**
   * 通知内容
   */
  notificationContent?: string;

  /**
   * 通知时间
   */
  notificationTime?: string;

}

export interface CryptoNotificationLogQuery extends PageQuery {
  /**
   * 交易所代码
   */
  exchangeCode?: string;

  /**
   * 币种符号
   */
  symbol?: string;

  /**
   * 交易对
   */
  tradingPair?: string;

  /**
   * 通知类型
   */
  notificationType?: string;

  /**
   * 通知状态
   */
  notificationStatus?: number;

  /**
   * 通知内容
   */
  notificationContent?: string;

  /**
   * 通知时间
   */
  notificationTime?: string;

}
