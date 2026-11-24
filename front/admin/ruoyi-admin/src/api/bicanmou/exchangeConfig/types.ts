export interface ExchangeConfigVO {
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
   * API密钥
   */
  apiKey: string;

  /**
   * API密钥密码
   */
  apiSecret: string;

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
   * 交易所梯队（1-第一梯队，2-第二梯队，3-第三梯队）
   */
  tier: number;

  /**
   * 检查间隔（秒）
   */
  checkIntervalSeconds: number;

  /**
   * 是否启用
   */
  enabled: number;

  /**
   * 状态（0-禁用，1-启用）
   */
  status: number;

  /**
   * 备注
   */
  remark: string;

}

export interface ExchangeConfigForm extends BaseEntity {
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
   * API密钥
   */
  apiKey?: string;

  /**
   * API密钥密码
   */
  apiSecret?: string;

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
   * 交易所梯队（1-第一梯队，2-第二梯队，3-第三梯队）
   */
  tier?: number;

  /**
   * 检查间隔（秒）
   */
  checkIntervalSeconds?: number;

  /**
   * 是否启用
   */
  enabled?: number;

  /**
   * 状态（0-禁用，1-启用）
   */
  status?: number;

  /**
   * 备注
   */
  remark?: string;

}

export interface ExchangeConfigQuery extends PageQuery {
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
   * API密钥
   */
  apiKey?: string;

  /**
   * API密钥密码
   */
  apiSecret?: string;

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
   * 交易所梯队（1-第一梯队，2-第二梯队，3-第三梯队）
   */
  tier?: number;

  /**
   * 检查间隔（秒）
   */
  checkIntervalSeconds?: number;

  /**
   * 是否启用
   */
  enabled?: number;

  /**
   * 状态（0-禁用，1-启用）
   */
  status?: number;

  /**
   * 参数
   */
  params?: Record<string, any>;

}
