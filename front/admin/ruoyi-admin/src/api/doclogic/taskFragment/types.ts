export interface TaskFragmentVO {
  /**
   * 主键ID
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
   * 片段所属页码
   */
  fPage: string;

  /**
   * 片段索引下标
   */
  idx: string | number;

  /**
   * 片段文档内容
   */
  content: string;

}

export interface TaskFragmentForm extends BaseEntity {
  /**
   * 主键ID
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
   * 片段所属页码
   */
  fPage?: string;

  /**
   * 片段索引下标
   */
  idx?: string | number;

  /**
   * 片段文档内容
   */
  content?: string;

}

export interface TaskFragmentQuery extends PageQuery {
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
   * 片段所属页码
   */
  fPage?: string;

  /**
   * 片段索引下标
   */
  idx?: string | number;

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
