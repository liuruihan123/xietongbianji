package com.xmzs.coedit.service;

import com.xmzs.coedit.domain.bo.CoEditorPerformanceBo;
import com.xmzs.coedit.domain.vo.CoEditorPerformanceVo;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

import java.util.Map;

/**
 * 编辑器性能监控Service接口
 *
 * @author zuleed
 * @date 2025-11-05
 */
public interface ICoEditorPerformanceService {

    /**
     * 查询性能监控列表
     *
     * @param bo 查询条件
     * @param pageQuery 分页参数
     * @return 性能监控列表
     */
    TableDataInfo<CoEditorPerformanceVo> queryPageList(CoEditorPerformanceBo bo, PageQuery pageQuery);

    /**
     * 获取性能监控详情
     *
     * @param id 监控ID
     * @return 性能监控详情
     */
    CoEditorPerformanceVo queryById(Long id);

    /**
     * 获取文件的性能统计
     *
     * @param fileId 文件ID
     * @return 性能统计
     */
    Map<String, Object> getFilePerformanceStats(Long fileId);

    /**
     * 获取用户的性能统计
     *
     * @param userId 用户ID
     * @return 性能统计
     */
    Map<String, Object> getUserPerformanceStats(Long userId);

    /**
     * 获取实时连接统计
     *
     * @return 实时连接统计
     */
    Map<String, Object> getRealtimeStats();

    /**
     * 获取设备类型统计
     *
     * @return 设备类型统计
     */
    Map<String, Object> getDeviceTypeStats();

    /**
     * 获取网络类型统计
     *
     * @return 网络类型统计
     */
    Map<String, Object> getNetworkTypeStats();

    /**
     * 获取延迟分布统计
     *
     * @return 延迟分布统计
     */
    Map<String, Object> getLatencyDistribution();

    /**
     * 删除性能监控记录
     *
     * @param id 监控ID
     * @return 是否成功
     */
    Boolean deleteById(Long id);

    /**
     * 批量删除性能监控记录
     *
     * @param ids 监控ID列表
     * @return 是否成功
     */
    Boolean deleteBatchByIds(Long[] ids);

    /**
     * 清理过期的性能数据
     *
     * @param days 保留天数
     * @return 清理数量
     */
    Integer cleanExpiredData(Integer days);
}

