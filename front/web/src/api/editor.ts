/**
 * 编辑器相关API接口
 */

import { get, post } from '@/utils/request'
import type { OnlyOfficeConfig, DocumentVersion, SaveCallbackData } from '@/types/editor'

/**
 * 获取编辑器配置
 * @param fileId 文件ID (支持number和string类型)
 * @param mode 编辑模式
 */
export function getEditorConfig(fileId: number | string, mode: 'edit' | 'view' = 'edit') {
  return get<OnlyOfficeConfig>({
    url: `/coedit/editor/config/${fileId}`,
    data: { mode },
  })
}

/**
 * 保存文档回调
 * @param data 保存回调数据
 */
export function saveDocumentCallback(data: SaveCallbackData) {
  return post<{ error: number }>({
    url: '/coedit/editor/callback',
    data,
  })
}

/**
 * 获取文档版本列表
 * @param fileId 文件ID
 */
export function getDocumentVersions(fileId: number | string) {
  return get<{ rows: DocumentVersion[]; total: number }>({
    url: `/coedit/editor/versions/${fileId}`,
  })
}

/**
 * 创建文档版本
 * @param fileId 文件ID
 * @param remark 版本备注
 */
export function createDocumentVersion(fileId: number | string, remark?: string) {
  return post<DocumentVersion>({
    url: '/coedit/editor/version',
    data: { fileId, remark },
  })
}

/**
 * 恢复文档版本
 * @param versionId 版本ID
 */
export function restoreDocumentVersion(versionId: number | string) {
  return post<void>({
    url: `/coedit/editor/version/${versionId}/restore`,
    data: {},
  })
}

/**
 * 删除文档版本
 * @param versionId 版本ID
 */
export function deleteDocumentVersion(versionId: number | string) {
  return post<void>({
    url: `/coedit/editor/version/${versionId}/delete`,
    data: {},
  })
}

/**
 * 强制保存文档
 * @param fileId 文件ID
 */
export function forceSaveDocument(fileId: number | string) {
  return post<void>({
    url: `/coedit/editor/force-save/${fileId}`,
    data: {},
  })
}

/**
 * 获取文档编辑历史
 * @param fileId 文件ID
 */
export function getDocumentHistory(fileId: number | string) {
  return get<any[]>({
    url: `/coedit/editor/history/${fileId}`,
  })
}

/**
 * 检查文档是否正在编辑
 * @param fileId 文件ID
 */
export function checkDocumentEditing(fileId: number | string) {
  return get<{ editing: boolean; users: string[] }>({
    url: `/coedit/editor/check-editing/${fileId}`,
  })
}

/**
 * 获取文档编辑权限
 * @param fileId 文件ID
 */
export function getDocumentPermission(fileId: number | string) {
  return get<{
    canEdit: boolean
    canDownload: boolean
    canPrint: boolean
    canReview: boolean
    canComment: boolean
  }>({
    url: `/coedit/editor/permission/${fileId}`,
  })
}

