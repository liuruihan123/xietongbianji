import { ModelNameVO } from '@/api/model/types';

export interface ModelsettingVO {
  /**
   * id
   */
  id: string | number;

  /**
   * 知识库ID
   */
  kid: string | number;

  /**
   * 启用知识库（0启动知识库 1不启用知识库）
   */
  useKnowledge: string;

  /**
   * 多少字符以上开启知识库，0为不限制
   */
  shortNoOpen: number;

  /**
   * 用户问题二次包装模版
   */
  standardTemplateId: bigint;

  /**
   * 上下文数量结束后的操作
   */
  afterTalkCount: string;

  /**
   * 语音速度
   */
  ttsSpeed: number;

  /**
   * 业务类型（0AI面试 1面试例子 2公司背景 3面试要点）
   */
  busType: number;

  /**
   * 模型id
   */
  modelId: string | number;

  /**
   * 名称
   */
  name: string;

  /**
   * 角色设定
   */
  systemRole: string;

  /**
   * 模型类型 (1: 付费模型, 2: 免费模型)
   */
  talkCount: number;

  /**
   * 语音人物
   */
  ttsVoice: string;

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

  sysModel: ModelNameVO;
}

export interface ModelsettingForm extends BaseEntity {
  /**
   * id
   */
  id?: string | number;

  /**
   * 知识库ID
   */
  kid?: string | number | string[];

  /**
   * 启用知识库（0启动知识库 1不启用知识库）
   */
  useKnowledge: string;

  /**
   * 多少字符以上开启知识库，0为不限制
   */
  shortNoOpen?: number;

  /**
   * 用户问题二次包装模版
   */
  standardTemplateId?: bigint;

  /**
   * 上下文数量结束后的操作
   */
  afterTalkCount?: string;

  /**
   * 语音速度
   */
  ttsSpeed?: number;

  /**
   * 业务类型（0AI面试 1面试例子 2公司背景 3面试要点）
   */
  busType: number;

  /**
   * 模型id
   */
  modelId?: string | number;

  /**
   * 名称
   */
  name?: string;

  /**
   * 角色设定
   */
  systemRole?: string;

  /**
   * 模型类型 (1: 付费模型, 2: 免费模型)
   */
  talkCount?: number;

  /**
   * 语音人物
   */
  ttsVoice?: string;

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

export interface ModelsettingQuery extends PageQuery {
  /**
   * 知识库ID
   */
  kid?: string | number;

  /**
   * 启用知识库（0启动知识库 1不启用知识库）
   */
  useKnowledge?: string;

  /**
   * 多少字符以上开启知识库，0为不限制
   */
  shortNoOpen?: number;

  /**
   * 用户问题二次包装模版
   */
  standardTemplateId?: bigint;

  /**
   * 上下文数量结束后的操作
   */
  afterTalkCount?: string;

  /**
   * 语音速度
   */
  ttsSpeed?: number;

  /**
   * 业务类型（0AI面试 1面试例子 2公司背景 3面试要点）
   */
  busType?: number;

  /**
   * 模型id
   */
  modelId?: string | number;

  /**
   * 名称
   */
  name?: string;

  /**
   * 角色设定
   */
  systemRole?: string;

  /**
   * 模型类型 (1: 付费模型, 2: 免费模型)
   */
  talkCount?: number;

  /**
   * 语音人物
   */
  ttsVoice?: string;

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
