export interface AnalysisResultsVO {
  /**
   * 编号
   */
  id: string | number;

  /**
   * 逻辑任务ID
   */
  lid: string | number;

  /**
   * 文档id
   */
  docId: string | number;

  /**
   * 错误级别(1.严重 2.一般 3.轻微 4.可忽略)
   */
  lvTag: string;

  /**
   * 最终分析内容
   */
  resultContent: string;

  /**
   * 备注
   */
  remark: string;

}

export interface AnalysisResultsForm extends BaseEntity {
  /**
   * 编号
   */
  id?: string | number;

  /**
   * 逻辑任务ID
   */
  lid?: string | number;

  /**
   * 文档id
   */
  docId?: string | number;

  /**
   * 错误级别(1.严重 2.一般 3.轻微 4.可忽略)
   */
  lvTag?: string;

  /**
   * 最终分析内容
   */
  resultContent?: string;

  /**
   * 备注
   */
  remark?: string;

}

export interface AnalysisResultsQuery extends PageQuery {
  /**
   * 逻辑任务ID
   */
  lid?: string | number;

  /**
   * 文档id
   */
  docId?: string | number;

  /**
   * 错误级别(1.严重 2.一般 3.轻微 4.可忽略)
   */
  lvTag?: string;

  /**
   * 最终分析内容
   */
  resultContent?: string;

  /**
   * 创建时间
   */
  createTime?: string;

  /**
   * 参数
   */
  params?: Record<string, any>;

}
