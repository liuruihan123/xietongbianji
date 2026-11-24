export interface CryptoMonitorLogVO {
  /**
   * 主键ID
   */
  id: string | number;

  /**
   * 交易所代码
   */
  exchangeCode: string;

  /**
   * 检查时间
   */
  checkTime: string;

  /**
   * 检查状态
   */
  checkStatus: number;

  /**
   * 发现新币数量
   */
  newCoinsCount: number;

  /**
   * 总币种数量
   */
  totalCoinsCount: number;

  /**
   * 响应时间（毫秒）
   */
  responseTimeMs: number;

  /**
   * 错误信息
   */
  errorMessage: string;

}

export interface CryptoMonitorLogForm extends BaseEntity {
  /**
   * 主键ID
   */
  id?: string | number;

  /**
   * 交易所代码
   */
  exchangeCode?: string;

  /**
   * 检查时间
   */
  checkTime?: string;

  /**
   * 检查状态
   */
  checkStatus?: number;

  /**
   * 发现新币数量
   */
  newCoinsCount?: number;

  /**
   * 总币种数量
   */
  totalCoinsCount?: number;

  /**
   * 响应时间（毫秒）
   */
  responseTimeMs?: number;

  /**
   * 错误信息
   */
  errorMessage?: string;

}

export interface CryptoMonitorLogQuery extends PageQuery {
  /**
   * 交易所代码
   */
  exchangeCode?: string;

  /**
   * 检查时间
   */
  checkTime?: string;

  /**
   * 检查状态
   */
  checkStatus?: number;

  /**
   * 发现新币数量
   */
  newCoinsCount?: number;

  /**
   * 总币种数量
   */
  totalCoinsCount?: number;

  /**
   * 响应时间（毫秒）
   */
  responseTimeMs?: number;

  /**
   * 错误信息
   */
  errorMessage?: string;

}
