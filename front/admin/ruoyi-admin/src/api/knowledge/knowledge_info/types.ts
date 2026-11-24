import { TypeVO } from '@/api/system/type/types';

export interface Knowledge_infoVO {
  id?: string | number;
  /**
   * 知识库ID
   */
  kid: string | number;
  /**
   * 知识分类ID
   */
  typeId?: string | number;

  knowledgeType: TypeVO;

  /**
   * 知识库名称
   */
  kname: string;

  /**
   * 描述
   */
  description: string;
}
export interface KnowledgeDelReq {
  kid: string; // 附件id
}
export interface Knowledge_infoForm extends BaseEntity {
  /**
   * 知识库ID
   */
  kid?: string | number;
  /**
   * 知识分类ID
   */
  typeId?: string | number;
  /**
   * 知识库名称
   */
  kname?: string;

  /**
   * 描述
   */
  description?: string;
  id?: number | string;
}

export interface Knowledge_infoQuery extends PageQuery {
  /**
   * 知识库名称
   */
  kname?: string;
  /**
   * 知识分类ID
   */
  typeId?: string | number;
  /**
   * 描述
   */
  description?: string;

  /**
   * 创建时间
   */
  createTime?: string;
  params?: Record<string, any>;
}
