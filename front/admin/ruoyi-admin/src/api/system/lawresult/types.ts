export interface LawresultVO {
  /**
   * 编号
   */
  id: string | number;

  /**
   * 处罚文书编号
   */
  Gid: string | number;

  /**
   * 时间
   */
  PunishmentDate: string;

  /**
   * 地点
   */
  LawRegional: string;

  /**
   * 处罚类别
   */
  PunishmentType: string;

  /**
   * 引用的法律
   */
  AccordingLaw: string;

  /**
   * 1具体情节
   */
  reasonText: string;

  /**
   * A 预测的结果
   */
  aijudgment: string;

  /**
   * 真实判决结果
   */
  realjudgment: string;
}

export interface LawresultForm extends BaseEntity {
  /**
   * 编号
   */
  id?: string | number;

  /**
   * 处罚文书编号
   */
  Gid?: string | number;

  /**
   * 时间
   */
  PunishmentDate?: string;

  /**
   * 地点
   */
  LawRegional?: string;

  /**
   * 处罚类别
   */
  PunishmentType?: string;

  /**
   * 引用的法律
   */
  AccordingLaw?: string;

  /**
   * 1具体情节
   */
  reasonText?: string;

  /**
   * A 预测的结果
   */
  aijudgment?: string;

  /**
   * 真实判决结果
   */
  realjudgment?: string;
}

export interface LawresultQuery extends PageQuery {
  /**
   * 处罚文书编号
   */
  Gid?: string | number;

  /**
   * 时间
   */
  PunishmentDate?: string;

  /**
   * 地点
   */
  LawRegional?: string;

  /**
   * 处罚类别
   */
  PunishmentType?: string;

  /**
   * 引用的法律
   */
  AccordingLaw?: string;

  /**
   * 1具体情节
   */
  reasonText?: string;

  /**
   * A 预测的结果
   */
  aijudgment?: string;

  /**
   * 真实判决结果
   */
  realjudgment?: string;

  /**
   * 额外参数
   */
  params?: Record<string, any>;
}
