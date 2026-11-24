export interface PackagePlanVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 套餐名称
   */
  name: string;

  /**
   * 套餐价格（元）
   */
  price: number;

  /**
   * 套餐国外价格（元）
   */
  enPrice?: number;

  /**
   * 有效时间
   */
  duration: number;

  /**
   * 积分数量
   */
  pgPoints: number;

  /**
   * 计划详情
   */
  planDetail: string;

  /**
   * 备注
   */
  remark: string;
}

export interface PackagePlanForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 套餐名称
   */
  name?: string;

  enName?: string;

  /**
   * 套餐价格（元）
   */
  price?: number;

  /**
   * 套餐国外价格（元）
   */
  enPrice?: number;

  /**
   * 有效时间
   */
  duration?: number | string;
  pgPoints?: number | string;

  /**
   * 计划详情
   */
  planDetail?: string;

  /**
   * 备注
   */
  remark?: string;

  /**
   * 国外计划详情
   */
  enPlanDetail?: string;
}

export interface PackagePlanQuery extends PageQuery {
  /**
   * 套餐名称
   */
  name?: string;
}
