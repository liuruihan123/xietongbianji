export interface LawListVO {
  /**
   *
   */
  id: string | number;

  /**
   * 法规目录
   */
  library: string;

  /**
   * Gid
   */
  Gid: string | number;

  /**
   * 标题
   */
  Title: string;

  /**
   * 发布部门
   */
  IssueDepartment: string;

  /**
   * 批准部门
   */
  RatifyDepartment: string;

  /**
   * 发文字号
   */
  DocumentNO: string;

  /**
   * 批准日期
   */
  RatifyDate: string;

  /**
   * 发布日期
   */
  IssueDate: string;

  /**
   * 实施日期
   */
  ImplementDate: string;

  /**
   * 时效性
   */
  TimelinessDic: string;

  /**
   * 失效依据
   */
  InvalidBasis: string | number;

  /**
   * 效力级别
   */
  EffectivenessDic: string;

  /**
   * 法规类别
   */
  Category: string;

  /**
   * 关键词
   */
  Keywords: string;

  /**
   * 专题分类
   */
  TopicType: string;

  /**
   * 正文
   */
  AllText: string;
}

export interface LawListForm extends BaseEntity {
  /**
   *
   */
  id?: string | number;

  /**
   * 法规目录
   */
  library?: string;

  /**
   * Gid
   */
  Gid?: string | number;

  /**
   * 标题
   */
  Title?: string;

  /**
   * 发布部门
   */
  IssueDepartment?: string;

  /**
   * 批准部门
   */
  RatifyDepartment?: string;

  /**
   * 发文字号
   */
  DocumentNO?: string;

  /**
   * 批准日期
   */
  RatifyDate?: string;

  /**
   * 发布日期
   */
  IssueDate?: string;

  /**
   * 实施日期
   */
  ImplementDate?: string;

  /**
   * 时效性
   */
  TimelinessDic?: string;

  /**
   * 失效依据
   */
  InvalidBasis?: string | number;

  /**
   * 效力级别
   */
  EffectivenessDic?: string;

  /**
   * 法规类别
   */
  Category?: string;

  /**
   * 关键词
   */
  Keywords?: string;

  /**
   * 专题分类
   */
  TopicType?: string;

  /**
   * 正文
   */
  AllText?: string;
}

export interface LawListQuery extends PageQuery {
  /**
   * 法规目录
   */
  library?: string;

  /**
   * Gid
   */
  Gid?: string | number;

  /**
   * 标题
   */
  Title?: string;

  /**
   * 发布部门
   */
  IssueDepartment?: string;

  /**
   * 批准部门
   */
  RatifyDepartment?: string;

  /**
   * 发文字号
   */
  DocumentNO?: string;

  /**
   * 批准日期
   */
  RatifyDate?: string;

  /**
   * 发布日期
   */
  IssueDate?: string;

  /**
   * 实施日期
   */
  ImplementDate?: string;

  /**
   * 时效性
   */
  TimelinessDic?: string;

  /**
   * 失效依据
   */
  InvalidBasis?: string | number;

  /**
   * 效力级别
   */
  EffectivenessDic?: string;

  /**
   * 法规类别
   */
  Category?: string;

  /**
   * 关键词
   */
  Keywords?: string;

  /**
   * 专题分类
   */
  TopicType?: string;

  /**
   * 正文
   */
  AllText?: string;

  params?: Record<string, any>;
}
