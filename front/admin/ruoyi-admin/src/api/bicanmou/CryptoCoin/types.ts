export interface CryptoCoinVO {
  /**
   * 主键ID
   */
  id: string | number;

  /**
   * 币种符号
   */
  symbol: string;

  /**
   * 币种名称
   */
  name: string;

  /**
   * 币种全称
   */
  fullName: string;

  /**
   * 首次发现的交易所
   */
  firstDiscoveredExchange: string;

  /**
   * 首次发现时间
   */
  firstDiscoveredTime: string;

  /**
   * 支持的交易所总数
   */
  totalExchanges: number;

  /**
   * 状态
   */
  status: number;

}

export interface CryptoCoinForm extends BaseEntity {
  /**
   * 主键ID
   */
  id?: string | number;

  /**
   * 币种符号
   */
  symbol?: string;

  /**
   * 币种名称
   */
  name?: string;

  /**
   * 币种全称
   */
  fullName?: string;

  /**
   * 首次发现的交易所
   */
  firstDiscoveredExchange?: string;

  /**
   * 首次发现时间
   */
  firstDiscoveredTime?: string;

  /**
   * 支持的交易所总数
   */
  totalExchanges?: number;

  /**
   * 状态
   */
  status?: number;

}

export interface CryptoCoinQuery extends PageQuery {
  /**
   * 币种符号
   */
  symbol?: string;

  /**
   * 币种名称
   */
  name?: string;

  /**
   * 币种全称
   */
  fullName?: string;

  /**
   * 首次发现的交易所
   */
  firstDiscoveredExchange?: string;

  /**
   * 首次发现时间
   */
  firstDiscoveredTime?: string;

  /**
   * 支持的交易所总数
   */
  totalExchanges?: number;

  /**
   * 状态
   */
  status?: number;

}
