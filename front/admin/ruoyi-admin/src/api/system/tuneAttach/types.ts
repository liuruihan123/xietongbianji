export interface TuneAttachVO {
  /**
   * 编码
   */
  id: string | number;

  /**
   * 生成提示词id
   */
  promptsStoreId: string;

  /**
   * 微调id
   */
  ftId: string;

  /**
   * 微调文件id
   */
  ftFileId: string | number;
  ftJobId: string | number;

  /**
   * 上传文件id
   */
  fileId: string | number;

  /**
   * 文件状态（0未转化 1已转化 99转化失败)
   */
  status: string;

  /**
   * 文件内容
   */
  fileContent: string;

  /**
   * 微调内容
   */
  ftContent: string;

  /**
   * 错误消息
   */
  messages: string;
}

export interface TuneAttachForm extends BaseEntity {
  /**
   * 编码
   */
  id?: string | number;

  /**
   * 生成提示词id
   */
  promptsStoreId?: string;
  /**
   * 微调id
   */
  ftId?: string | number;

  /**
   * 微调文件id
   */
  ftFileId?: string | number;
  ftJobId?: string | number;

  /**
   * 上传文件id
   */
  fileId?: string | number;

  /**
   * 文件状态（0未转化 1已转化 99转化失败)
   */
  status?: string;

  /**
   * 文件内容
   */
  fileContent?: string;

  /**
   * 微调内容
   */
  ftContent?: string;

  /**
   * 错误消息
   */
  messages?: string;
}

export interface TuneAttachQuery extends PageQuery {
  /**
   * 生成提示词id
   */
  promptsStoreId?: string;
  /**
   * 微调id
   */
  ftId?: string | number;

  /**
   * 微调文件id
   */
  ftFileId?: string | number;
  ftJobId?: string | number;

  /**
   * 上传文件id
   */
  fileId?: string | number;

  /**
   * 文件状态（0未转化 1已转化 99转化失败)
   */
  status?: string;

  /**
   * 文件内容
   */
  fileContent?: string;

  /**
   * 微调内容
   */
  ftContent?: string;

  /**
   * 错误消息
   */
  messages?: string;
}
