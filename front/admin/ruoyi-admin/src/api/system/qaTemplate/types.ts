export interface QaTemplateVO {
  /**
   * 编号
   */
  id: string | number;

  /**
   * 模版名称
   */
  name: string;

  /**
   * 模版描述
   */
  temDescription: string;

  /**
   * 角色类型（system/user）
   */
  roleType: string;

  /**
   * 模版内容
   */
  templateContent: string;

  /**
   * 备注
   */
  remark: string;

  /**
   * 状态（1有效、2无效）
   */
  status: string;
}

export interface QaTemplateForm extends BaseEntity {
  /**
   * 编号
   */
  id?: string | number;

  /**
   * 模版名称
   */
  name?: string;

  /**
   * 模版描述
   */
  temDescription?: string;

  /**
   * 角色类型（system/user）
   */
  roleType: string;

  /**
   * 模版内容
   */
  templateContent?: string;

  /**
   * 备注
   */
  remark?: string;

  /**
   * 状态（1有效、2无效）
   */
  status?: string;
}

export interface QaTemplateQuery extends PageQuery {
  /**
   * 模版名称
   */
  name?: string;

  /**
   * 模版描述
   */
  temDescription?: string;

  /**
   * 角色类型（system/user）
   */
  roleType: string;

  /**
   * 模版内容
   */
  templateContent?: string;

  /**
   * 状态（1有效、2无效）
   */
  status?: string;
}
