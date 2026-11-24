export interface PromptsStoreVO {
  /**
   * id
   */
  id: string | number;

  /**
   * 提示词名称
   */
  name: string;

  /**
   * 模型id
   */
  modelId: string | number;

  /**
   * 角色设定
   */
  systemRole: string;

  /**
   * 用户要求
   */
  userPrompts: string;

  /**
   * 模型类型 (1: 付费模型, 2: 免费模型)
   */
  talkCount: number;

  /**
   * 随机性
   */
  temperature: number;

  /**
   * 核采样
   */
  topP: number;

  /**
   * 单次回复限制
   */
  maxTokens: number;

  /**
   * 话题新鲜度
   */
  presencePenalty: number;

  /**
   * 频率惩罚度
   */
  frequencyPenalty: number;

  /**
   * 状态（0正常 1停用）
   */
  status: string;

  /**
   * 备注
   */
  remark: string;

}

export interface PromptsStoreForm extends BaseEntity {
  /**
   * id
   */
  id?: string | number;

  /**
   * 提示词名称
   */
  name?: string;

  /**
   * 模型id
   */
  modelId?: string | number;

  /**
   * 角色设定
   */
  systemRole?: string;

  /**
   * 用户要求
   */
  userPrompts?: string;

  /**
   * 模型类型 (1: 付费模型, 2: 免费模型)
   */
  talkCount?: number;

  /**
   * 随机性
   */
  temperature?: number;

  /**
   * 核采样
   */
  topP?: number;

  /**
   * 单次回复限制
   */
  maxTokens?: number;

  /**
   * 话题新鲜度
   */
  presencePenalty?: number;

  /**
   * 频率惩罚度
   */
  frequencyPenalty?: number;

  /**
   * 状态（0正常 1停用）
   */
  status?: string;

  /**
   * 备注
   */
  remark?: string;

}

export interface PromptsStoreQuery extends PageQuery {
  /**
   * 提示词名称
   */
  name?: string;

  /**
   * 模型id
   */
  modelId?: string | number;

  /**
   * 角色设定
   */
  systemRole?: string;

  /**
   * 用户要求
   */
  userPrompts?: string;

  /**
   * 模型类型 (1: 付费模型, 2: 免费模型)
   */
  talkCount?: number;

  /**
   * 随机性
   */
  temperature?: number;

  /**
   * 核采样
   */
  topP?: number;

  /**
   * 单次回复限制
   */
  maxTokens?: number;

  /**
   * 话题新鲜度
   */
  presencePenalty?: number;

  /**
   * 频率惩罚度
   */
  frequencyPenalty?: number;

  /**
   * 状态（0正常 1停用）
   */
  status?: string;

}
