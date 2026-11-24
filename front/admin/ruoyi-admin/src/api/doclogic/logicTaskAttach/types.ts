export interface LogicTaskAttachVO {
  /**
   * 
   */
  id: string | number;

  /**
   * 逻辑任务
   */
  lid: string | number;

  /**
   * 文档
   */
  docId: string | number;

  /**
   * 文档地址
   */
  docUrl: string;

  /**
   * 文档名称
   */
  docName: string;

  /**
   * 文档类型
   */
  docType: string;

  /**
   * 文档内容
   */
  content: string;

  /**
   * 
   */
  remark: string;

}

export interface LogicTaskAttachForm extends BaseEntity {
  /**
   * 
   */
  id?: string | number;

  /**
   * 逻辑任务
   */
  lid?: string | number;

  /**
   * 文档
   */
  docId?: string | number;

  /**
   * 文档地址
   */
  docUrl?: string;

  /**
   * 文档名称
   */
  docName?: string;

  /**
   * 文档类型
   */
  docType?: string;

  /**
   * 文档内容
   */
  content?: string;

  /**
   * 
   */
  remark?: string;

}

export interface LogicTaskAttachQuery extends PageQuery {
  /**
   * 逻辑任务
   */
  lid?: string | number;

  /**
   * 文档
   */
  docId?: string | number;

  /**
   * 文档地址
   */
  docUrl?: string;

  /**
   * 文档名称
   */
  docName?: string;

  /**
   * 文档类型
   */
  docType?: string;

  /**
   * 
   */
  createTime?: string;

  /**
   * 
   */
  createBy?: string;

}
