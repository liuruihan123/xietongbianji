export interface ConversationStartersVO {
  /**
   * id
   */
  id: string | number;

  /**
   * 中文标题
   */
  titleCn: string;

  /**
   * 英文标题
   */
  titleEn: string;

  /**
   * 启用知识库（0启动知识库 1不启用知识库）
   */
  useKnowledge: string;

  /**
   * 知识库ID(多选)
   */
  kidIds: string | number;

  /**
   * 业务类型（0AI面试 1面试例子 2公司背景 3面试要点）
   */
  busType: number;

  /**
   * 关联标签
   */
  tags: string;

  /**
   * 备注
   */
  remark: string;

  /**
   * 状态（0正常 1停用）
   */
  status: string;

  /**
   * 创建者
   */
  createdBy: string;

  /**
   * 缺省字段1
   */
  val1: string;

  /**
   * 缺省字段1
   */
  val2: string;

  /**
   * 案例库ID(多选)
   */
  knowledgeAttachIds?: string | string[];
}

export interface ConversationStartersForm extends BaseEntity {
  /**
   * id
   */
  id?: string | number;

  /**
   * 中文标题
   */
  titleCn?: string;

  /**
   * 英文标题
   */
  titleEn?: string;

  /**
   * 启用知识库（0启动知识库 1不启用知识库）
   */
  useKnowledge?: string;

  /**
   * 知识库ID(多选)
   */
  kidIds?: string | number;

  /**
   * 启用案例库（0启动案例库 1不启用案例库）
   */
  useKnowledgeAttach?: string;

  /**
   * 是否固定在开头(0不固定 1固定)
   */
  templateFixed?: string;

  /**
   * 案例库ID(多选)
   */
  knowledgeAttachIds?: string | number;

  /**
   * 问答模版ID
   */
  templateId?: number;

  /**
   * 业务类型（0AI面试 1面试例子 2公司背景 3面试要点）
   */
  busType?: number;

  /**
   * 关联标签
   */
  tags?: string;

  /**
   * 备注
   */
  remark?: string;

  /**
   * 状态（0正常 1停用）
   */
  status?: string;

  /**
   * 创建者
   */
  createdBy?: string;

  /**
   * 缺省字段1
   */
  val1?: string;

  /**
   * 缺省字段1
   */
  val2?: string;
}

export interface ConversationStartersQuery extends PageQuery {
  /**
   * 启用知识库（0启动知识库 1不启用知识库）
   */
  useKnowledge?: string;

  /**
   * 知识库ID(多选)
   */
  kidIds?: string | number;

  /**
   * 业务类型（0AI面试 1面试例子 2公司背景 3面试要点）
   */
  busType?: number;

  /**
   * 关联标签
   */
  tags?: string;

  /**
   * 状态（0正常 1停用）
   */
  status?: string;

  /**
   * 创建者
   */
  createdBy?: string;

  /**
   * 缺省字段1
   */
  val1?: string;

  /**
   * 缺省字段1
   */
  val2?: string;

  /**
   * 启用案例库（0启动案例库 1不启用案例库）
   */
  useKnowledgeAttach?: string;

  /**
   * 是否固定在开头(0不固定 1固定)
   */
  templateFixed?: string;

  /**
   * 案例库ID(多选)
   */
  knowledgeAttachIds?: string | number;
}
