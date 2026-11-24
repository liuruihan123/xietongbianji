export interface TaskLogVO {
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
   * 文档片段
   */
  fid: string | number;

  /**
   * 错误级别
   */
  lvTag: number;

  /**
   * 分析内容
   */
  logContent: string;

  /**
   * 
   */
  remark: string;

}

export interface TaskLogForm extends BaseEntity {
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
   * 文档片段
   */
  fid?: string | number;

  /**
   * 错误级别
   */
  lvTag?: number;

  /**
   * 分析内容
   */
  logContent?: string;

  /**
   * 
   */
  remark?: string;

}

export interface TaskLogQuery extends PageQuery {
  /**
   * 逻辑任务
   */
  lid?: string | number;

  /**
   * 文档
   */
  docId?: string | number;

  /**
   * 文档片段
   */
  fid?: string | number;

  /**
   * 错误级别
   */
  lvTag?: number;

  /**
   * 分析内容
   */
  logContent?: string;

  /**
   *
   */
  createTime?: string;

  /**
   *
   */
  createBy?: string;

  /**
   * 参数
   */
  params?: Record<string, any>;

}
