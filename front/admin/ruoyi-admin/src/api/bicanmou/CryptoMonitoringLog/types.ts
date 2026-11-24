export interface CryptoMonitoringLogVO {
  /**
   * 主键ID
   */
  id: string | number;

  /**
   * 交易所代码
   */
  exchangeCode: string;

  /**
   * 监控类型（API-API监控，SCRAPING-爬虫监控）
   */
  monitorType: string;

  /**
   * 检查状态（0-失败，1-成功）
   */
  checkStatus: number;

  /**
   * 响应时间（毫秒）
   */
  responseTimeMs: number;

  /**
   * 总币种数量
   */
  totalCoinsCount: number;

  /**
   * 新币数量
   */
  newCoinsCount: number;

  /**
   * 错误信息
   */
  errorMessage: string;

  /**
   * 错误代码
   */
  errorCode: string;

  /**
   * HTTP状态码
   */
  httpStatusCode: number;

  /**
   * 请求URL
   */
  requestUrl: string;

  /**
   * 请求头信息
   */
  requestHeaders: string;

  /**
   * 响应头信息
   */
  responseHeaders: string;

  /**
   * 响应体大小（字节）
   */
  responseSizeBytes: number;

  /**
   * 重试次数
   */
  retryCount: number;

  /**
   * 检查开始时间
   */
  checkStartTime: string;

  /**
   * 检查结束时间
   */
  checkEndTime: string;

  /**
   * 状态（0-未使用，1-使用中）
   */
  status: number;

  /**
   * 备注
   */
  remark: string;

}

export interface CryptoMonitoringLogForm extends BaseEntity {
  /**
   * 主键ID
   */
  id?: string | number;

  /**
   * 交易所代码
   */
  exchangeCode?: string;

  /**
   * 监控类型（API-API监控，SCRAPING-爬虫监控）
   */
  monitorType?: string;

  /**
   * 检查状态（0-失败，1-成功）
   */
  checkStatus?: number;

  /**
   * 响应时间（毫秒）
   */
  responseTimeMs?: number;

  /**
   * 总币种数量
   */
  totalCoinsCount?: number;

  /**
   * 新币数量
   */
  newCoinsCount?: number;

  /**
   * 错误信息
   */
  errorMessage?: string;

  /**
   * 错误代码
   */
  errorCode?: string;

  /**
   * HTTP状态码
   */
  httpStatusCode?: number;

  /**
   * 请求URL
   */
  requestUrl?: string;

  /**
   * 请求头信息
   */
  requestHeaders?: string;

  /**
   * 响应头信息
   */
  responseHeaders?: string;

  /**
   * 响应体大小（字节）
   */
  responseSizeBytes?: number;

  /**
   * 重试次数
   */
  retryCount?: number;

  /**
   * 检查开始时间
   */
  checkStartTime?: string;

  /**
   * 检查结束时间
   */
  checkEndTime?: string;

  /**
   * 状态（0-未使用，1-使用中）
   */
  status?: number;

  /**
   * 备注
   */
  remark?: string;

}

export interface CryptoMonitoringLogQuery extends PageQuery {
  /**
   * 交易所代码
   */
  exchangeCode?: string;

  /**
   * 监控类型（API-API监控，SCRAPING-爬虫监控）
   */
  monitorType?: string;

  /**
   * 检查状态（0-失败，1-成功）
   */
  checkStatus?: number;

  /**
   * 响应时间（毫秒）
   */
  responseTimeMs?: number;

  /**
   * 总币种数量
   */
  totalCoinsCount?: number;

  /**
   * 新币数量
   */
  newCoinsCount?: number;

  /**
   * 错误信息
   */
  errorMessage?: string;

  /**
   * 错误代码
   */
  errorCode?: string;

  /**
   * HTTP状态码
   */
  httpStatusCode?: number;

  /**
   * 请求URL
   */
  requestUrl?: string;

  /**
   * 请求头信息
   */
  requestHeaders?: string;

  /**
   * 响应头信息
   */
  responseHeaders?: string;

  /**
   * 响应体大小（字节）
   */
  responseSizeBytes?: number;

  /**
   * 重试次数
   */
  retryCount?: number;

  /**
   * 检查开始时间
   */
  checkStartTime?: string;

  /**
   * 检查结束时间
   */
  checkEndTime?: string;

  /**
   * 状态（0-未使用，1-使用中）
   */
  status?: number;

}
