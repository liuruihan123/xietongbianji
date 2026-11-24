export interface ContactusVO {
  /**
   * 编号
   */
  id: string | number;

  /**
   * 渠道信息
   */
  channel: string;

  /**
   * 用户留言
   */
  userMessage: string;

  /**
   * 创建人账号
   */
  createdUserName: string;

  /**
   * 创建人
   */
  createdBy: string;

}

export interface ContactusForm extends BaseEntity {
  /**
   * 编号
   */
  id?: string | number;

  /**
   * 渠道信息
   */
  channel?: string;

  /**
   * 用户留言
   */
  userMessage?: string;

  /**
   * 创建人账号
   */
  createdUserName?: string;

  /**
   * 创建人
   */
  createdBy?: string;

}

export interface ContactusQuery extends PageQuery {
  /**
   * 用户留言
   */
  userMessage?: string;

  /**
   * 创建时间
   */
  createTime?: string;

  /**
   * 创建人账号
   */
  createdUserName?: string;

  /**
   * 创建人
   */
  createdBy?: string;

}
