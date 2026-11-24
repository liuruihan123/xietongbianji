export interface QaCaseVO {
  /**
   * 编号
   */
  id: string | number;

  /**
   * 标题
   */
  title: string;

  /**
   * 案例内容
   */
  caseContent: string;

  /**
   * 状态（0正常 1停用）
   */
  status: string;

  /**
   * 备注
   */
  remark: string;

  /**
   * 来源文件地址
   */
  sourceUrl: string;

  /**
   * 来源文件名称
   */
  sourceName: string;

  /**
   * 来源文件ID
   */
  sourceId: string;
}

export interface QaCaseForm extends BaseEntity {
  /**
   * 编号
   */
  id?: string | number;

  /**
   * 标题
   */
  title?: string;

  /**
   * 案例内容
   */
  caseContent?: string;

  /**
   * 状态（0正常 1停用）
   */
  status?: string;

  /**
   * 备注
   */
  remark?: string;

  /**
   * 来源文件地址
   */
  sourceUrl?: string;

  /**
   * 来源文件名称
   */
  sourceName?: string;

  /**
   * 来源文件ID
   */
  sourceId?: string;
}

export interface QaCaseQuery extends PageQuery {
  /**
   * 标题
   */
  title?: string;

  /**
   * 状态（0正常 1停用）
   */
  status?: string;

  /**
   * 来源文件名称
   */
  sourceName?: string;

  /**
   * 来源文件ID
   */
  sourceId?: string;
}

export interface qaCaseVO {
  /**
   * 值
   */
  value: string;

  /**
   * 标签
   */
  label: string;

  /**
   * 子节点
   */
  children: qaCaseVO[];
}
