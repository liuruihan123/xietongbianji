package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.vo.CoShareAccessRecordVo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

import java.util.Map;

/**
 * 共享访问Service接口
 *
 * @author zuleed
 * @date 2025-11-04
 */
public interface ICoShareAccessService {

    /**
     * 记录访问行为
     *
     * @param shareCode 共享码
     * @param visitorId 访问者ID（可为空）
     * @param visitorName 访问者名称（可为空）
     * @param action 访问操作
     * @param accessIp 访问IP
     * @param userAgent 用户代理
     * @param sessionId 会话ID
     * @return 访问记录
     */
    CoShareAccessRecordVo recordAccess(String shareCode, Long visitorId, String visitorName,
                                       String action, String accessIp, String userAgent, String sessionId);

    /**
     * 记录访问失败
     *
     * @param shareCode 共享码
     * @param action 访问操作
     * @param accessIp 访问IP
     * @param userAgent 用户代理
     * @param failReason 失败原因
     * @return 访问记录
     */
    CoShareAccessRecordVo recordAccessFailure(String shareCode, String action,
                                              String accessIp, String userAgent, String failReason);

    /**
     * 根据共享码查询访问记录
     *
     * @param shareCode 共享码
     * @param pageQuery 分页参数
     * @return 访问记录列表
     */
    TableDataInfo<CoShareAccessRecordVo> getAccessRecordsByShareCode(String shareCode, PageQuery pageQuery);

    /**
     * 根据文件ID查询访问记录
     *
     * @param fileId 文件ID
     * @param pageQuery 分页参数
     * @return 访问记录列表
     */
    TableDataInfo<CoShareAccessRecordVo> getAccessRecordsByFileId(Long fileId, PageQuery pageQuery);

    /**
     * 根据访问者ID查询访问记录
     *
     * @param visitorId 访问者ID
     * @param pageQuery 分页参数
     * @return 访问记录列表
     */
    TableDataInfo<CoShareAccessRecordVo> getAccessRecordsByVisitorId(Long visitorId, PageQuery pageQuery);

    /**
     * 获取访问统计
     *
     * @param shareCode 共享码
     * @return 统计数据
     */
    Map<String, Object> getAccessStatistics(String shareCode);

    /**
     * 获取文件访问统计
     *
     * @param fileId 文件ID
     * @return 统计数据
     */
    Map<String, Object> getFileAccessStatistics(Long fileId);

    /**
     * 检查IP访问频率
     *
     * @param shareCode 共享码
     * @param accessIp 访问IP
     * @param maxAccessPerHour 每小时最大访问次数
     * @param maxAccessPerDay 每天最大访问次数
     * @return 是否超过限制
     */
    Boolean checkAccessFrequency(String shareCode, String accessIp,
                                 Integer maxAccessPerHour, Integer maxAccessPerDay);

}

