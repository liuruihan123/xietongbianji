export interface TuneInfoVO {
  /**
   * 编码
   */
  id: string | number;

  /**
   * 微调ID
   */
  ftId: string | number;

  /**
   * 作业id
   */
  ftJobId: string | number;

  /**
   * 用户ID
   */
  uid: string | number;

  /**
   * 微调名
   */
  ftName: string;

  /**
   * 微调的模型名称
   */
  ftModelName: string;

  /**
   * 基础模型
   */
  baseModel: string;

  /**
   * 训练状态（0未使用 1使用中 99训练失败）
   */
  status: string;

  /**
   * 使用状态（0未使用 1使用中）
   */
  useStatus: string;

}

export interface TuneInfoForm extends BaseEntity {
  /**
   * 编码
   */
  id?: string | number;

  /**
   * 微调ID
   */
  ftId?: string | number;

  /**
   * 作业id
   */
  ftJobId?: string | number;

  /**
   * 用户ID
   */
  uid?: string | number;

  /**
   * 微调名
   */
  ftName?: string;

  /**
   * 微调的模型名称
   */
  ftModelName?: string;

  /**
   * 基础模型
   */
  baseModel?: string;

  /**
   * 训练状态（0未使用 1使用中 99训练失败）
   */
  status?: string;

  /**
   * 使用状态（0未使用 1使用中）
   */
  useStatus?: string;

}

export interface TuneInfoQuery extends PageQuery {
  /**
   * 微调ID
   */
  ftId?: string | number;

  /**
   * 作业id
   */
  ftJobId?: string | number;

  /**
   * 用户ID
   */
  uid?: string | number;

  /**
   * 微调名
   */
  ftName?: string;

  /**
   * 微调的模型名称
   */
  ftModelName?: string;

  /**
   * 基础模型
   */
  baseModel?: string;

  /**
   * 训练状态（0未使用 1使用中 99训练失败）
   */
  status?: string;

  /**
   * 使用状态（0未使用 1使用中）
   */
  useStatus?: string;

}
