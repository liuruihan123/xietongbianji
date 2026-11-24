package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.CoEditHistory;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

/**
 * 编辑历史Service接口
 *
 * @author zuleed
 * @date 2025-11-04
 */
public interface ICoEditHistoryService {

    /**
     * 记录编辑历史
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @param userName 用户名称
     * @param sessionKey 会话Key
     * @param operationType 操作类型
     * @param contentBefore 修改前内容
     * @param contentAfter 修改后内容
     * @param positionData 位置数据
     */
    void recordHistory(Long fileId, Long userId, String userName, String sessionKey,
                      String operationType, String contentBefore, String contentAfter, String positionData);

    /**
     * 查询文件的编辑历史
     *
     * @param fileId 文件ID
     * @param pageQuery 分页参数
     * @return 历史列表
     */
    TableDataInfo<CoEditHistory> queryFileHistory(Long fileId, PageQuery pageQuery);

    /**
     * 查询用户的编辑历史
     *
     * @param userId 用户ID
     * @param pageQuery 分页参数
     * @return 历史列表
     */
    TableDataInfo<CoEditHistory> queryUserHistory(Long userId, PageQuery pageQuery);

}

