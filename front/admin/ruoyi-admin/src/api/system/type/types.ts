export interface TypeVO {
  id: number | string; // 添加这一行
  /**
   * 名称
   */
  name: string;

  /**
   * 状态（0正常 1停用）
   */
  status: string;

  /**
   * 备注
   */
  remark: string;
}

export interface TypeForm extends BaseEntity {
  id?: number | string;
  /**
   * 名称
   */
  name?: string;

  /**
   * 状态（0正常 1停用）
   */
  status?: string;

  /**
   * 备注
   */
  remark?: string;
}

export interface TypeQuery extends PageQuery {
  /**
   * 名称
   */
  name?: string;

  /**
   * 状态（0正常 1停用）
   */
  status?: string;
}
