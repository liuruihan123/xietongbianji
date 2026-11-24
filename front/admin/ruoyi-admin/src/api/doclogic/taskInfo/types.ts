export interface TaskInfoVO {
  /**
   * 
   */
  id: string | number;

  /**
   * 逻辑任务
   */
  lid: string | number;

  /**
   * 用户
   */
  uid: string | number;

  /**
   * 逻辑任务名称
   */
  lname: string;

  /**
   * 总字数
   */
  totalWords: number;

  /**
   * 片段字数
   */
  segmentSize: number;

  /**
   * 描述
   */
  description: string;

  /**
   * 任务类型id
   */
  typeId: string | number;

  /**
   * 菜单状态
   */
  status: string;

  /**
   * 状态说明
   */
  taskInfo: string;

}

export interface TaskInfoForm extends BaseEntity {
  /**
   * 
   */
  id?: string | number;

  /**
   * 逻辑任务
   */
  lid?: string | number;

  /**
   * 用户
   */
  uid?: string | number;

  /**
   * 逻辑任务名称
   */
  lname?: string;

  /**
   * 总字数
   */
  totalWords?: number;

  /**
   * 片段字数
   */
  segmentSize?: number;

  /**
   * 描述
   */
  description?: string;

  /**
   * 任务类型id
   */
  typeId?: string | number;

  /**
   * 菜单状态
   */
  status?: string;

  /**
   * 状态说明
   */
  taskInfo?: string;

}

export interface TaskInfoQuery extends PageQuery {
  /**
   * 逻辑任务
   */
  lid?: string | number;

  /**
   * 用户
   */
  uid?: string | number;

  /**
   * 逻辑任务名称
   */
  lname?: string;

  /**
   * 总字数
   */
  totalWords?: number;

  /**
   * 片段字数
   */
  segmentSize?: number;

  /**
   * 描述
   */
  description?: string;

  /**
   * 任务类型id
   */
  typeId?: string | number;

  /**
   * 菜单状态
   */
  status?: string;

  /**
   * 状态说明
   */
  taskInfo?: string;

}
