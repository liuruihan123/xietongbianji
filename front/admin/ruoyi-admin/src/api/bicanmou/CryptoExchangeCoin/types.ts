export interface CryptoExchangeCoinVO {
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
   * 基础资产
   */
  baseAsset: string;

  /**
   * 计价资产
   */
  quoteAsset: string;

  /**
   * 交易对
   */
  tradingPair: string;

  /**
   * 交易状态
   */
  status: string;

  /**
   * 是否为新币
   */
  isNew: number;

  /**
   * 上线时间
   */
  listingTime: string;

  /**
   * 发现时间
   */
  discoveredTime: string;

}

export interface CryptoExchangeCoinForm extends BaseEntity {
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
   * 基础资产
   */
  baseAsset?: string;

  /**
   * 计价资产
   */
  quoteAsset?: string;

  /**
   * 交易对
   */
  tradingPair?: string;

  /**
   * 交易状态
   */
  status?: string;

  /**
   * 是否为新币
   */
  isNew?: number;

  /**
   * 上线时间
   */
  listingTime?: string;

  /**
   * 发现时间
   */
  discoveredTime?: string;

}

export interface CryptoExchangeCoinQuery extends PageQuery {
  /**
   * 交易所代码
   */
  exchangeCode?: string;

  /**
   * 币种符号
   */
  symbol?: string;

  /**
   * 基础资产
   */
  baseAsset?: string;

  /**
   * 计价资产
   */
  quoteAsset?: string;

  /**
   * 交易对
   */
  tradingPair?: string;

  /**
   * 交易状态
   */
  status?: string;

  /**
   * 是否为新币
   */
  isNew?: number;

  /**
   * 上线时间
   */
  listingTime?: string;

  /**
   * 发现时间
   */
  discoveredTime?: string;

}
