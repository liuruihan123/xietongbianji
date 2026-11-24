export interface CryptoNewListingEventVO {
  /**
   * 主键ID
   */
  id: string | number;

  /**
   * 交易所代码
   */
  exchangeCode: string;

  /**
   * 交易所名称
   */
  exchangeName: string;

  /**
   * 币种符号
   */
  symbol: string;

  /**
   * 交易对
   */
  tradingPair: string;

  /**
   * 基础资产
   */
  baseAsset: string;

  /**
   * 计价资产
   */
  quoteAsset: string;

  /**
   * 交易状态
   */
  tradingStatus: string;

  /**
   * 上市价格
   */
  listingPrice: number;

  /**
   * 市值（USD）
   */
  marketCapUsd: number;

  /**
   * 24小时交易量
   */
  volume24h: number;

  /**
   * 发现时间
   */
  discoveredTime: string;

  /**
   * 上市时间
   */
  listingTime: string;

  /**
   * 通知状态（0-未通知，1-已通知，2-通知失败）
   */
  notificationStatus: number;

  /**
   * 通知时间
   */
  notificationTime: string;

  /**
   * 通知用户数量
   */
  notificationUserCount: number;

  /**
   * 事件类型（NEW_LISTING-新币上市，DELISTING-下架）
   */
  eventType: string;

  /**
   * 事件来源（API-API检测，SCRAPING-爬虫检测，MANUAL-手动添加）
   */
  eventSource: string;

  /**
   * 原始数据（JSON格式）
   */
  rawData: string;

  /**
   * 状态（0-禁用，1-启用）
   */
  status: number;

  /**
   * 备注
   */
  remark: string;

}

export interface CryptoNewListingEventForm extends BaseEntity {
  /**
   * 主键ID
   */
  id?: string | number;

  /**
   * 交易所代码
   */
  exchangeCode?: string;

  /**
   * 交易所名称
   */
  exchangeName?: string;

  /**
   * 币种符号
   */
  symbol?: string;

  /**
   * 交易对
   */
  tradingPair?: string;

  /**
   * 基础资产
   */
  baseAsset?: string;

  /**
   * 计价资产
   */
  quoteAsset?: string;

  /**
   * 交易状态
   */
  tradingStatus?: string;

  /**
   * 上市价格
   */
  listingPrice?: number;

  /**
   * 市值（USD）
   */
  marketCapUsd?: number;

  /**
   * 24小时交易量
   */
  volume24h?: number;

  /**
   * 发现时间
   */
  discoveredTime?: string;

  /**
   * 上市时间
   */
  listingTime?: string;

  /**
   * 通知状态（0-未通知，1-已通知，2-通知失败）
   */
  notificationStatus?: number;

  /**
   * 通知时间
   */
  notificationTime?: string;

  /**
   * 通知用户数量
   */
  notificationUserCount?: number;

  /**
   * 事件类型（NEW_LISTING-新币上市，DELISTING-下架）
   */
  eventType?: string;

  /**
   * 事件来源（API-API检测，SCRAPING-爬虫检测，MANUAL-手动添加）
   */
  eventSource?: string;

  /**
   * 原始数据（JSON格式）
   */
  rawData?: string;

  /**
   * 状态（0-禁用，1-启用）
   */
  status?: number;

  /**
   * 备注
   */
  remark?: string;

}

export interface CryptoNewListingEventQuery extends PageQuery {
  /**
   * 交易所代码
   */
  exchangeCode?: string;

  /**
   * 交易所名称
   */
  exchangeName?: string;

  /**
   * 币种符号
   */
  symbol?: string;

  /**
   * 交易对
   */
  tradingPair?: string;

  /**
   * 基础资产
   */
  baseAsset?: string;

  /**
   * 计价资产
   */
  quoteAsset?: string;

  /**
   * 交易状态
   */
  tradingStatus?: string;

  /**
   * 上市价格
   */
  listingPrice?: number;

  /**
   * 市值（USD）
   */
  marketCapUsd?: number;

  /**
   * 24小时交易量
   */
  volume24h?: number;

  /**
   * 发现时间
   */
  discoveredTime?: string;

  /**
   * 上市时间
   */
  listingTime?: string;

  /**
   * 通知状态（0-未通知，1-已通知，2-通知失败）
   */
  notificationStatus?: number;

  /**
   * 通知时间
   */
  notificationTime?: string;

  /**
   * 通知用户数量
   */
  notificationUserCount?: number;

  /**
   * 事件类型（NEW_LISTING-新币上市，DELISTING-下架）
   */
  eventType?: string;

  /**
   * 事件来源（API-API检测，SCRAPING-爬虫检测，MANUAL-手动添加）
   */
  eventSource?: string;

  /**
   * 原始数据（JSON格式）
   */
  rawData?: string;

  /**
   * 状态（0-禁用，1-启用）
   */
  status?: number;

}
