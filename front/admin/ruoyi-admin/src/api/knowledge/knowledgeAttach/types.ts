export interface KnowledgeAttachVO {
  /**
   * 文档名称
   */
  docName: string;

  /**
   * 文档内容
   */
  content: string;

  docId: string;

  docType: string;

  kid: string;

  id: string;
}

export interface KnowledgeAttachForm extends BaseEntity {
  /**
   * 文档名称
   */
  docName?: string;

  /**
   * 文档内容
   */
  content?: string;
}

export interface KnowledgeAttachQuery extends PageQuery {
  /**
   * 知识库ID
   */
  kid?: string | number;

  /**
   * 文档名称
   */
  docName?: string;

  /**
   * 文档内容
   */
  content?: string;

  /**
   * 创建人
   */
  createBy?: string;
}
