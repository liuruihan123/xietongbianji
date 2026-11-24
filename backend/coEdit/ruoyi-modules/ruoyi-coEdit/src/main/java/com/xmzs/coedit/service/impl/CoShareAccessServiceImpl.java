package com.xmzs.coedit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.coedit.domain.CoFile;
import com.xmzs.coedit.domain.CoShareAccessRecord;
import com.xmzs.coedit.domain.CoShareLink;
import com.xmzs.coedit.domain.vo.CoShareAccessRecordVo;
import com.xmzs.coedit.mapper.CoFileMapper;
import com.xmzs.coedit.mapper.CoShareAccessRecordMapper;
import com.xmzs.coedit.mapper.CoShareLinkMapper;
import com.xmzs.coedit.service.ICoShareAccessService;
import com.xmzs.coedit.util.SharePermissionValidator;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 共享访问Service业务层处理
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CoShareAccessServiceImpl implements ICoShareAccessService {

    private final CoShareAccessRecordMapper baseMapper;
    private final CoShareLinkMapper shareLinkMapper;
    private final CoFileMapper fileMapper;

    /**
     * 记录访问行为
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CoShareAccessRecordVo recordAccess(String shareCode, Long visitorId, String visitorName,
                                              String action, String accessIp, String userAgent, String sessionId) {
        // 获取共享链接信息
        CoShareLink shareLink = shareLinkMapper.selectOne(
            Wrappers.lambdaQuery(CoShareLink.class)
                .eq(CoShareLink::getShareCode, shareCode)
        );

        if (shareLink == null) {
            log.warn("共享链接不存在: shareCode={}", shareCode);
            return null;
        }

        // 创建访问记录
        CoShareAccessRecord record = new CoShareAccessRecord();
        record.setShareLinkId(shareLink.getId());
        record.setShareCode(shareCode);
        record.setFileId(shareLink.getFileId());
        record.setVisitorId(visitorId);
        record.setVisitorName(visitorName);
        record.setVisitorType(visitorId != null ? 1 : 0); // 1-已登录 0-匿名
        record.setAccessIp(accessIp);
        record.setUserAgent(userAgent);
        record.setAccessTime(new Date());
        record.setAccessAction(SharePermissionValidator.getActionCode(action));
        record.setAccessResult(0); // 成功
        record.setSessionId(sessionId);

        // 保存访问记录
        baseMapper.insert(record);

        log.info("记录访问行为: shareCode={}, visitorId={}, action={}, ip={}",
                 shareCode, visitorId, action, accessIp);

        // 转换为VO并返回
        CoShareAccessRecordVo vo = MapstructUtils.convert(record, CoShareAccessRecordVo.class);
        enrichAccessRecordVo(vo);
        return vo;
    }

    /**
     * 记录访问失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CoShareAccessRecordVo recordAccessFailure(String shareCode, String action,
                                                     String accessIp, String userAgent, String failReason) {
        // 获取共享链接信息
        CoShareLink shareLink = shareLinkMapper.selectOne(
            Wrappers.lambdaQuery(CoShareLink.class)
                .eq(CoShareLink::getShareCode, shareCode)
        );

        // 创建访问记录
        CoShareAccessRecord record = new CoShareAccessRecord();
        if (shareLink != null) {
            record.setShareLinkId(shareLink.getId());
            record.setFileId(shareLink.getFileId());
        }
        record.setShareCode(shareCode);
        record.setVisitorType(0); // 匿名
        record.setAccessIp(accessIp);
        record.setUserAgent(userAgent);
        record.setAccessTime(new Date());
        record.setAccessAction(SharePermissionValidator.getActionCode(action));
        record.setAccessResult(1); // 失败
        record.setFailReason(failReason);

        // 保存访问记录
        baseMapper.insert(record);

        log.warn("记录访问失败: shareCode={}, action={}, ip={}, reason={}",
                 shareCode, action, accessIp, failReason);

        // 转换为VO并返回
        CoShareAccessRecordVo vo = MapstructUtils.convert(record, CoShareAccessRecordVo.class);
        enrichAccessRecordVo(vo);
        return vo;
    }

    /**
     * 根据共享码查询访问记录
     */
    @Override
    public TableDataInfo<CoShareAccessRecordVo> getAccessRecordsByShareCode(String shareCode, PageQuery pageQuery) {
        LambdaQueryWrapper<CoShareAccessRecord> wrapper = Wrappers.lambdaQuery(CoShareAccessRecord.class)
            .eq(CoShareAccessRecord::getShareCode, shareCode)
            .orderByDesc(CoShareAccessRecord::getAccessTime);

        Page<CoShareAccessRecordVo> page = baseMapper.selectVoPage(pageQuery.build(), wrapper);

        // 丰富VO信息
        page.getRecords().forEach(this::enrichAccessRecordVo);

        return TableDataInfo.build(page);
    }

    /**
     * 根据文件ID查询访问记录
     */
    @Override
    public TableDataInfo<CoShareAccessRecordVo> getAccessRecordsByFileId(Long fileId, PageQuery pageQuery) {
        LambdaQueryWrapper<CoShareAccessRecord> wrapper = Wrappers.lambdaQuery(CoShareAccessRecord.class)
            .eq(CoShareAccessRecord::getFileId, fileId)
            .orderByDesc(CoShareAccessRecord::getAccessTime);

        Page<CoShareAccessRecordVo> page = baseMapper.selectVoPage(pageQuery.build(), wrapper);

        // 丰富VO信息
        page.getRecords().forEach(this::enrichAccessRecordVo);

        return TableDataInfo.build(page);
    }

    /**
     * 根据访问者ID查询访问记录
     */
    @Override
    public TableDataInfo<CoShareAccessRecordVo> getAccessRecordsByVisitorId(Long visitorId, PageQuery pageQuery) {
        LambdaQueryWrapper<CoShareAccessRecord> wrapper = Wrappers.lambdaQuery(CoShareAccessRecord.class)
            .eq(CoShareAccessRecord::getVisitorId, visitorId)
            .orderByDesc(CoShareAccessRecord::getAccessTime);

        Page<CoShareAccessRecordVo> page = baseMapper.selectVoPage(pageQuery.build(), wrapper);

        // 丰富VO信息
        page.getRecords().forEach(this::enrichAccessRecordVo);

        return TableDataInfo.build(page);
    }

    /**
     * 获取访问统计
     */
    @Override
    public Map<String, Object> getAccessStatistics(String shareCode) {
        Map<String, Object> statistics = new HashMap<>();

        // 总访问次数
        Long totalCount = baseMapper.selectCount(
            Wrappers.lambdaQuery(CoShareAccessRecord.class)
                .eq(CoShareAccessRecord::getShareCode, shareCode)
        );
        statistics.put("totalCount", totalCount);

        // 成功访问次数
        Long successCount = baseMapper.selectCount(
            Wrappers.lambdaQuery(CoShareAccessRecord.class)
                .eq(CoShareAccessRecord::getShareCode, shareCode)
                .eq(CoShareAccessRecord::getAccessResult, 0)
        );
        statistics.put("successCount", successCount);

        // 失败访问次数
        Long failCount = baseMapper.selectCount(
            Wrappers.lambdaQuery(CoShareAccessRecord.class)
                .eq(CoShareAccessRecord::getShareCode, shareCode)
                .eq(CoShareAccessRecord::getAccessResult, 1)
        );
        statistics.put("failCount", failCount);

        // 匿名访问次数
        Long anonymousCount = baseMapper.selectCount(
            Wrappers.lambdaQuery(CoShareAccessRecord.class)
                .eq(CoShareAccessRecord::getShareCode, shareCode)
                .eq(CoShareAccessRecord::getVisitorType, 0)
        );
        statistics.put("anonymousCount", anonymousCount);

        // 已登录用户访问次数
        Long loginCount = baseMapper.selectCount(
            Wrappers.lambdaQuery(CoShareAccessRecord.class)
                .eq(CoShareAccessRecord::getShareCode, shareCode)
                .eq(CoShareAccessRecord::getVisitorType, 1)
        );
        statistics.put("loginCount", loginCount);

        // 按操作类型统计
        Long viewCount = baseMapper.selectCount(
            Wrappers.lambdaQuery(CoShareAccessRecord.class)
                .eq(CoShareAccessRecord::getShareCode, shareCode)
                .eq(CoShareAccessRecord::getAccessAction, 0)
        );
        statistics.put("viewCount", viewCount);

        Long editCount = baseMapper.selectCount(
            Wrappers.lambdaQuery(CoShareAccessRecord.class)
                .eq(CoShareAccessRecord::getShareCode, shareCode)
                .eq(CoShareAccessRecord::getAccessAction, 1)
        );
        statistics.put("editCount", editCount);

        Long downloadCount = baseMapper.selectCount(
            Wrappers.lambdaQuery(CoShareAccessRecord.class)
                .eq(CoShareAccessRecord::getShareCode, shareCode)
                .eq(CoShareAccessRecord::getAccessAction, 2)
        );
        statistics.put("downloadCount", downloadCount);

        return statistics;
    }

    /**
     * 获取文件访问统计
     */
    @Override
    public Map<String, Object> getFileAccessStatistics(Long fileId) {
        Map<String, Object> statistics = new HashMap<>();

        // 总访问次数
        Long totalCount = baseMapper.selectCount(
            Wrappers.lambdaQuery(CoShareAccessRecord.class)
                .eq(CoShareAccessRecord::getFileId, fileId)
        );
        statistics.put("totalCount", totalCount);

        // 独立访问者数量（已登录用户）
        List<CoShareAccessRecord> records = baseMapper.selectList(
            Wrappers.lambdaQuery(CoShareAccessRecord.class)
                .eq(CoShareAccessRecord::getFileId, fileId)
                .isNotNull(CoShareAccessRecord::getVisitorId)
                .groupBy(CoShareAccessRecord::getVisitorId)
        );
        statistics.put("uniqueVisitors", records.size());

        return statistics;
    }

    /**
     * 检查IP访问频率
     */
    @Override
    public Boolean checkAccessFrequency(String shareCode, String accessIp,
                                       Integer maxAccessPerHour, Integer maxAccessPerDay) {
        Date now = new Date();

        // 检查每小时访问次数
        if (maxAccessPerHour != null && maxAccessPerHour > 0) {
            Date oneHourAgo = new Date(now.getTime() - 60 * 60 * 1000);
            Long hourCount = baseMapper.selectCount(
                Wrappers.lambdaQuery(CoShareAccessRecord.class)
                    .eq(CoShareAccessRecord::getShareCode, shareCode)
                    .eq(CoShareAccessRecord::getAccessIp, accessIp)
                    .ge(CoShareAccessRecord::getAccessTime, oneHourAgo)
            );

            if (hourCount >= maxAccessPerHour) {
                log.warn("IP访问频率超限(小时): shareCode={}, ip={}, count={}, max={}",
                         shareCode, accessIp, hourCount, maxAccessPerHour);
                return false;
            }
        }

        // 检查每天访问次数
        if (maxAccessPerDay != null && maxAccessPerDay > 0) {
            Date oneDayAgo = new Date(now.getTime() - 24 * 60 * 60 * 1000);
            Long dayCount = baseMapper.selectCount(
                Wrappers.lambdaQuery(CoShareAccessRecord.class)
                    .eq(CoShareAccessRecord::getShareCode, shareCode)
                    .eq(CoShareAccessRecord::getAccessIp, accessIp)
                    .ge(CoShareAccessRecord::getAccessTime, oneDayAgo)
            );

            if (dayCount >= maxAccessPerDay) {
                log.warn("IP访问频率超限(天): shareCode={}, ip={}, count={}, max={}",
                         shareCode, accessIp, dayCount, maxAccessPerDay);
                return false;
            }
        }

        return true;
    }

    /**
     * 丰富访问记录VO信息
     */
    private void enrichAccessRecordVo(CoShareAccessRecordVo vo) {
        // 获取文件信息
        if (vo.getFileId() != null) {
            CoFile file = fileMapper.selectById(vo.getFileId());
            if (file != null) {
                vo.setFileName(file.getFileName());
            }
        }

        // 设置访问者类型描述
        if (vo.getVisitorType() != null) {
            vo.setVisitorTypeDesc(vo.getVisitorType() == 0 ? "匿名" : "已登录");
        }

        // 设置访问操作描述
        if (vo.getAccessAction() != null) {
            String actionString = SharePermissionValidator.getActionString(vo.getAccessAction());
            vo.setAccessActionDesc(SharePermissionValidator.getActionDescription(actionString));
        }

        // 设置访问结果描述
        if (vo.getAccessResult() != null) {
            vo.setAccessResultDesc(vo.getAccessResult() == 0 ? "成功" : "失败");
        }
    }

}

