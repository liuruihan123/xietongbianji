package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.bo.CoFilePreviewBo;
import com.xmzs.coedit.domain.vo.CoFilePreviewVo;
import com.xmzs.coedit.domain.vo.CoFileThumbnailVo;

/**
 * 文件预览Service接口
 *
 * @author zuleed
 * @date 2025-11-05
 */
public interface ICoFilePreviewService {

    /**
     * 生成文件预览配置
     *
     * @param previewBo 预览参数
     * @param userId 用户ID
     * @return 预览配置
     */
    CoFilePreviewVo generatePreviewConfig(CoFilePreviewBo previewBo, Long userId);

    /**
     * 获取文件缩略图
     *
     * @param fileId 文件ID
     * @param width 宽度
     * @param height 高度
     * @return 缩略图信息
     */
    CoFileThumbnailVo getThumbnail(Long fileId, Integer width, Integer height);

    /**
     * 生成文件缩略图
     *
     * @param fileId 文件ID
     * @param width 宽度
     * @param height 高度
     * @return 缩略图信息
     */
    CoFileThumbnailVo generateThumbnail(Long fileId, Integer width, Integer height);

    /**
     * 检查文件是否支持预览
     *
     * @param fileType 文件类型
     * @return 是否支持
     */
    Boolean isSupportPreview(String fileType);

    /**
     * 清理过期缩略图
     *
     * @return 清理数量
     */
    Integer cleanExpiredThumbnails();

    /**
     * 删除文件缩略图
     *
     * @param fileId 文件ID
     * @return 是否成功
     */
    Boolean deleteThumbnail(Long fileId);

    /**
     * 批量删除文件缩略图
     *
     * @param fileIds 文件ID列表
     * @return 是否成功
     */
    Boolean batchDeleteThumbnails(Long[] fileIds);

    /**
     * 更新缩略图访问统计
     *
     * @param thumbnailId 缩略图ID
     */
    void updateThumbnailAccess(Long thumbnailId);
}

