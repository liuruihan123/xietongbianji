package com.xmzs.coedit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.coedit.domain.CoEditorPerformance;
import com.xmzs.coedit.domain.bo.CoEditorPerformanceBo;
import com.xmzs.coedit.domain.vo.CoEditorPerformanceVo;
import com.xmzs.coedit.mapper.CoEditorPerformanceMapper;
import com.xmzs.coedit.service.ICoEditorPerformanceService;
import com.xmzs.coedit.websocket.WebSocketConnectionManager;
import com.xmzs.common.core.exception.ServiceException;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 编辑器性能监控Service实现
 *
 * @author zuleed
 * @date 2025-11-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CoEditorPerformanceServiceImpl implements ICoEditorPerformanceService {

    private final CoEditorPerformanceMapper performanceMapper;
    private final WebSocketConnectionManager connectionManager;

    @Override
    public TableDataInfo<CoEditorPerformanceVo> queryPageList(CoEditorPerformanceBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<CoEditorPerformance> wrapper = buildQueryWrapper(bo);
        wrapper.orderByDesc(CoEditorPerformance::getConnectTime);

        Page<CoEditorPerformanceVo> page = performanceMapper.selectVoPage(pageQuery.build(), wrapper);
        page.getRecords().forEach(this::enrichVo);
        return TableDataInfo.build(page);
    }

    @Override
    public CoEditorPerformanceVo queryById(Long id) {
        CoEditorPerformance performance = performanceMapper.selectById(id);
        if (performance == null) {
            throw new ServiceException("性能监控记录不存在");
        }
        CoEditorPerformanceVo vo = BeanUtil.copyProperties(performance, CoEditorPerformanceVo.class);
        enrichVo(vo);
        return vo;
    }

    @Override
    public Map<String, Object> getFilePerformanceStats(Long fileId) {
        LambdaQueryWrapper<CoEditorPerformance> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CoEditorPerformance::getFileId, fileId);

        List<CoEditorPerformance> performances = performanceMapper.selectList(wrapper);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSessions", performances.size());
        stats.put("totalUsers", performances.stream().map(CoEditorPerformance::getUserId).distinct().count());
        stats.put("totalMessages", performances.stream().mapToInt(p -> p.getMessagesSent() + p.getMessagesReceived()).sum());
        stats.put("averageLatency", performances.stream().filter(p -> p.getAverageLatency() != null)
                .mapToLong(CoEditorPerformance::getAverageLatency).average().orElse(0));
        stats.put("totalErrors", performances.stream().mapToInt(CoEditorPerformance::getErrorCount).sum());
        stats.put("totalReconnects", performances.stream().mapToInt(CoEditorPerformance::getReconnectCount).sum());
        stats.put("averageDuration", performances.stream().filter(p -> p.getConnectionDuration() != null)
                .mapToLong(CoEditorPerformance::getConnectionDuration).average().orElse(0));

        return stats;
    }

    @Override
    public Map<String, Object> getUserPerformanceStats(Long userId) {
        LambdaQueryWrapper<CoEditorPerformance> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CoEditorPerformance::getUserId, userId);

        List<CoEditorPerformance> performances = performanceMapper.selectList(wrapper);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSessions", performances.size());
        stats.put("totalFiles", performances.stream().map(CoEditorPerformance::getFileId).distinct().count());
        stats.put("totalMessages", performances.stream().mapToInt(p -> p.getMessagesSent() + p.getMessagesReceived()).sum());
        stats.put("averageLatency", performances.stream().filter(p -> p.getAverageLatency() != null)
                .mapToLong(CoEditorPerformance::getAverageLatency).average().orElse(0));
        stats.put("totalErrors", performances.stream().mapToInt(CoEditorPerformance::getErrorCount).sum());
        stats.put("totalReconnects", performances.stream().mapToInt(CoEditorPerformance::getReconnectCount).sum());
        stats.put("totalDuration", performances.stream().filter(p -> p.getConnectionDuration() != null)
                .mapToLong(CoEditorPerformance::getConnectionDuration).sum());

        return stats;
    }

    @Override
    public Map<String, Object> getRealtimeStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 活跃连接数
        stats.put("activeConnections", connectionManager.getActiveConnectionCount());
        
        // 在线用户数
        stats.put("onlineUsers", connectionManager.getOnlineUserCount());
        
        // 获取所有活跃连接的详细信息
        Map<String, WebSocketConnectionManager.ConnectionInfo> connections = connectionManager.getAllConnections();
        
        // 总消息数
        int totalMessages = connections.values().stream()
                .mapToInt(info -> info.getMessagesSentValue() + info.getMessagesReceivedValue())
                .sum();
        stats.put("totalMessages", totalMessages);
        
        // 平均延迟
        double avgLatency = connections.values().stream()
                .mapToLong(WebSocketConnectionManager.ConnectionInfo::getAverageLatency)
                .average()
                .orElse(0);
        stats.put("averageLatency", avgLatency);
        
        // 总错误数
        int totalErrors = connections.values().stream()
                .mapToInt(WebSocketConnectionManager.ConnectionInfo::getErrorCountValue)
                .sum();
        stats.put("totalErrors", totalErrors);
        
        // 设备类型分布
        Map<String, Long> deviceDistribution = connections.values().stream()
                .filter(info -> info.getDeviceType() != null)
                .collect(Collectors.groupingBy(
                        WebSocketConnectionManager.ConnectionInfo::getDeviceType,
                        Collectors.counting()
                ));
        stats.put("deviceDistribution", deviceDistribution);
        
        // 网络类型分布
        Map<String, Long> networkDistribution = connections.values().stream()
                .filter(info -> info.getNetworkType() != null)
                .collect(Collectors.groupingBy(
                        WebSocketConnectionManager.ConnectionInfo::getNetworkType,
                        Collectors.counting()
                ));
        stats.put("networkDistribution", networkDistribution);
        
        return stats;
    }

    @Override
    public Map<String, Object> getDeviceTypeStats() {
        List<CoEditorPerformance> performances = performanceMapper.selectList(null);
        
        Map<String, Long> deviceCounts = performances.stream()
                .filter(p -> p.getDeviceType() != null)
                .collect(Collectors.groupingBy(
                        CoEditorPerformance::getDeviceType,
                        Collectors.counting()
                ));
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("distribution", deviceCounts);
        stats.put("total", performances.size());
        
        return stats;
    }

    @Override
    public Map<String, Object> getNetworkTypeStats() {
        List<CoEditorPerformance> performances = performanceMapper.selectList(null);
        
        Map<String, Long> networkCounts = performances.stream()
                .filter(p -> p.getNetworkType() != null)
                .collect(Collectors.groupingBy(
                        CoEditorPerformance::getNetworkType,
                        Collectors.counting()
                ));
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("distribution", networkCounts);
        stats.put("total", performances.size());
        
        return stats;
    }

    @Override
    public Map<String, Object> getLatencyDistribution() {
        List<CoEditorPerformance> performances = performanceMapper.selectList(null);
        
        Map<String, Long> distribution = new HashMap<>();
        distribution.put("0-50ms", 0L);
        distribution.put("50-100ms", 0L);
        distribution.put("100-200ms", 0L);
        distribution.put("200-500ms", 0L);
        distribution.put("500ms+", 0L);
        
        for (CoEditorPerformance p : performances) {
            if (p.getAverageLatency() == null) continue;
            
            long latency = p.getAverageLatency();
            if (latency < 50) {
                distribution.put("0-50ms", distribution.get("0-50ms") + 1);
            } else if (latency < 100) {
                distribution.put("50-100ms", distribution.get("50-100ms") + 1);
            } else if (latency < 200) {
                distribution.put("100-200ms", distribution.get("100-200ms") + 1);
            } else if (latency < 500) {
                distribution.put("200-500ms", distribution.get("200-500ms") + 1);
            } else {
                distribution.put("500ms+", distribution.get("500ms+") + 1);
            }
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("distribution", distribution);
        stats.put("total", performances.size());
        
        return stats;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteById(Long id) {
        return performanceMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteBatchByIds(Long[] ids) {
        return performanceMapper.deleteBatchIds(Arrays.asList(ids)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer cleanExpiredData(Integer days) {
        Date expireDate = new Date(System.currentTimeMillis() - days * 24 * 60 * 60 * 1000L);
        
        LambdaQueryWrapper<CoEditorPerformance> wrapper = Wrappers.lambdaQuery();
        wrapper.lt(CoEditorPerformance::getConnectTime, expireDate);
        
        return performanceMapper.delete(wrapper);
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<CoEditorPerformance> buildQueryWrapper(CoEditorPerformanceBo bo) {
        LambdaQueryWrapper<CoEditorPerformance> wrapper = Wrappers.lambdaQuery();
        
        wrapper.eq(bo.getFileId() != null, CoEditorPerformance::getFileId, bo.getFileId());
        wrapper.eq(bo.getUserId() != null, CoEditorPerformance::getUserId, bo.getUserId());
        wrapper.eq(bo.getSessionId() != null, CoEditorPerformance::getSessionId, bo.getSessionId());
        wrapper.eq(bo.getDeviceType() != null, CoEditorPerformance::getDeviceType, bo.getDeviceType());
        wrapper.eq(bo.getBrowserType() != null, CoEditorPerformance::getBrowserType, bo.getBrowserType());
        wrapper.eq(bo.getNetworkType() != null, CoEditorPerformance::getNetworkType, bo.getNetworkType());
        wrapper.ge(bo.getStartTime() != null, CoEditorPerformance::getConnectTime, bo.getStartTime());
        wrapper.le(bo.getEndTime() != null, CoEditorPerformance::getDisconnectTime, bo.getEndTime());
        
        return wrapper;
    }

    /**
     * 丰富Vo对象
     */
    private void enrichVo(CoEditorPerformanceVo vo) {
        // 连接时长描述
        if (vo.getConnectionDuration() != null) {
            long seconds = vo.getConnectionDuration() / 1000;
            if (seconds < 60) {
                vo.setDurationDesc(seconds + "秒");
            } else if (seconds < 3600) {
                long minutes = seconds / 60;
                long remainSeconds = seconds % 60;
                vo.setDurationDesc(minutes + "分" + remainSeconds + "秒");
            } else {
                long hours = seconds / 3600;
                long minutes = (seconds % 3600) / 60;
                vo.setDurationDesc(hours + "小时" + minutes + "分");
            }
        }
        
        // 总消息数
        vo.setTotalMessages((vo.getMessagesSent() != null ? vo.getMessagesSent() : 0) + 
                           (vo.getMessagesReceived() != null ? vo.getMessagesReceived() : 0));
        
        // 断开状态描述
        vo.setDisconnectDesc(vo.getNormalDisconnect() != null && vo.getNormalDisconnect() == 1 ? "正常" : "异常");
    }
}

