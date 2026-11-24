export interface CryptoExchangeVO {
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
   * 交易所中文名称
   */
  exchangeNameCn: string;

  /**
   * API基础URL
   */
  apiBaseUrl: string;

  /**
   * API端点
   */
  apiEndpoint: string;

  /**
   * 每分钟请求限制
   */
  rateLimitPerMinute: number;

  /**
   * 每秒请求限制
   */
  rateLimitPerSecond: number;

  /**
   * 是否需要认证
   */
  needAuth: number;

  /**
   * API密钥
   */
  apiKey: string;

  /**
   * API密钥
   */
  apiSecret: string;

  /**
   * 交易所等级
   */
  tier: number;

  /**
   * 状态
   */
  status: number;

  /**
   * 是否启用监控
   */
  monitorEnabled: number;

  /**
   * 最后检查时间
   */
  lastCheckTime: string;

  /**
   * 检查间隔（秒）
   */
  checkIntervalSeconds: number;

  /**
   * 备注
   */
  remark: string;

}

export interface CryptoExchangeForm extends BaseEntity {
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
   * 交易所中文名称
   */
  exchangeNameCn?: string;

  /**
   * API基础URL
   */
  apiBaseUrl?: string;

  /**
   * API端点
   */
  apiEndpoint?: string;

  /**
   * 每分钟请求限制
   */
  rateLimitPerMinute?: number;

  /**
   * 每秒请求限制
   */
  rateLimitPerSecond?: number;

  /**
   * 是否需要认证
   */
  needAuth?: number;

  /**
   * API密钥
   */
  apiKey?: string;

  /**
   * API密钥
   */
  apiSecret?: string;

  /**
   * 交易所等级
   */
  tier?: number;

  /**
   * 状态
   */
  status?: number;

  /**
   * 是否启用监控
   */
  monitorEnabled?: number;

  /**
   * 最后检查时间
   */
  lastCheckTime?: string;

  /**
   * 检查间隔（秒）
   */
  checkIntervalSeconds?: number;

  /**
   * 备注
   */
  remark?: string;

}

export interface CryptoExchangeQuery extends PageQuery {
  /**
   * 交易所代码
   */
  exchangeCode?: string;

  /**
   * 交易所名称
   */
  exchangeName?: string;

  /**
   * 交易所中文名称
   */
  exchangeNameCn?: string;

  /**
   * API基础URL
   */
  apiBaseUrl?: string;

  /**
   * API端点
   */
  apiEndpoint?: string;

  /**
   * 每分钟请求限制
   */
  rateLimitPerMinute?: number;

  /**
   * 每秒请求限制
   */
  rateLimitPerSecond?: number;

  /**
   * 是否需要认证
   */
  needAuth?: number;

  /**
   * API密钥
   */
  apiKey?: string;

  /**
   * API密钥
   */
  apiSecret?: string;

  /**
   * 交易所等级
   */
  tier?: number;

  /**
   * 状态
   */
  status?: number;

  /**
   * 是否启用监控
   */
  monitorEnabled?: number;

  /**
   * 最后检查时间
   */
  lastCheckTime?: string;

  /**
   * 检查间隔（秒）
   */
  checkIntervalSeconds?: number;

  /**
   * 参数
   */
  params?: Record<string, any>;

}
