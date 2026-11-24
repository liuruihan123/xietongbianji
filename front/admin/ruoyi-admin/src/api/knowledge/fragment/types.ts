export interface FragmentVO {
  /**
   * 编码
   */
  id: string | number;

  /**
   * 片段索引下标
   */
  idx: string | number;

  /**
   * 文档内容
   */
  content: string;

}

export interface FragmentForm extends BaseEntity {
  /**
   * 编码
   */
  id?: string | number;
  /**
   * 知识库ID
   */
  kid?: string | number;
  /**
   * 片段索引下标
   */
  idx?: string | number;

  /**
   * 文档ID
   */
  docId?: string | number;
  /**
   * 文档内容
   */
  content?: string;

}

export interface FragmentQuery extends PageQuery {
  /**
   * 知识库ID
   */
  kid?: string | number;

  /**
   * 文档ID
   */
  docId?: string | number;

  /**
   * 片段索引下标
   */
  idx?: string | number;

  /**
   * 文档内容
   */
  content?: string;

}
