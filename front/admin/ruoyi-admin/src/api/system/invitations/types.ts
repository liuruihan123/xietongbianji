export interface InvitationsVO {
  /**
   * 编号
   */
  id: string | number;

  /**
   * 邀请者的用户 ID
   */
  inviterId: string | number;

  /**
   * 邀请者的用户账号
   */
  inviterUserName: string;

  /**
   * 被邀请者的用户 ID
   */
  inviteeId: string | number;

  /**
   * 被邀请者的用户账号
   */
  inviteeUserName: string;

  /**
   * 邀请码
   */
  inviterCode: string;

  /**
   * 奖励天数
   */
  rewardDays: number;

  /**
   * 邀请状态（1邀请成功、2待处理、3邀请失败）
   */
  status: string;

  /**
   * 备注
   */
  remark: string;

}

export interface InvitationsForm extends BaseEntity {
  /**
   * 编号
   */
  id?: string | number;

  /**
   * 邀请者的用户 ID
   */
  inviterId?: string | number;

  /**
   * 邀请者的用户账号
   */
  inviterUserName?: string;

  /**
   * 被邀请者的用户 ID
   */
  inviteeId?: string | number;

  /**
   * 被邀请者的用户账号
   */
  inviteeUserName?: string;

  /**
   * 邀请码
   */
  inviterCode?: string;

  /**
   * 奖励天数
   */
  rewardDays?: number;

  /**
   * 邀请状态（1邀请成功、2待处理、3邀请失败）
   */
  status?: string;

  /**
   * 备注
   */
  remark?: string;

}

export interface InvitationsQuery extends PageQuery {
  /**
   * 邀请者的用户 ID
   */
  inviterId?: string | number;

  /**
   * 邀请者的用户账号
   */
  inviterUserName?: string;

  /**
   * 被邀请者的用户 ID
   */
  inviteeId?: string | number;

  /**
   * 被邀请者的用户账号
   */
  inviteeUserName?: string;

  /**
   * 邀请码
   */
  inviterCode?: string;

  /**
   * 奖励天数
   */
  rewardDays?: number;

  /**
   * 邀请状态（1邀请成功、2待处理、3邀请失败）
   */
  status?: string;

}
