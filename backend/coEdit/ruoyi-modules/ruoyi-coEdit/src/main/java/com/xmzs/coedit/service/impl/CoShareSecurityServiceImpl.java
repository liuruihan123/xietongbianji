package com.xmzs.coedit.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xmzs.coedit.domain.CoShareLink;
import com.xmzs.coedit.domain.CoShareSecurity;
import com.xmzs.coedit.domain.bo.CoShareSecurityBo;
import com.xmzs.coedit.mapper.CoShareLinkMapper;
import com.xmzs.coedit.mapper.CoShareSecurityMapper;
import com.xmzs.coedit.service.ICoShareAccessService;
import com.xmzs.coedit.service.ICoShareSecurityService;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 共享安全控制Service业务层处理
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CoShareSecurityServiceImpl implements ICoShareSecurityService {

    private final CoShareSecurityMapper baseMapper;
    private final CoShareLinkMapper shareLinkMapper;
    private final ICoShareAccessService accessService;

    /**
     * 创建安全配置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CoShareSecurity createSecurityConfig(CoShareSecurityBo bo) {
        // 检查共享链接是否存在
        CoShareLink shareLink = shareLinkMapper.selectById(bo.getShareLinkId());
        if (shareLink == null) {
            throw new RuntimeException("共享链接不存在");
        }

        // 检查是否已存在安全配置
        CoShareSecurity existing = baseMapper.selectOne(
            Wrappers.lambdaQuery(CoShareSecurity.class)
                .eq(CoShareSecurity::getShareLinkId, bo.getShareLinkId())
        );

        if (existing != null) {
            throw new RuntimeException("安全配置已存在，请使用更新接口");
        }

        // 创建安全配置
        CoShareSecurity security = MapstructUtils.convert(bo, CoShareSecurity.class);

        // 转换IP列表为JSON
        if (bo.getIpWhitelistArray() != null && !bo.getIpWhitelistArray().isEmpty()) {
            security.setIpWhitelist(JSONUtil.toJsonStr(bo.getIpWhitelistArray()));
        }
        if (bo.getIpBlacklistArray() != null && !bo.getIpBlacklistArray().isEmpty()) {
            security.setIpBlacklist(JSONUtil.toJsonStr(bo.getIpBlacklistArray()));
        }

        // 默认启用
        if (security.getIsEnabled() == null) {
            security.setIsEnabled(1);
        }

        baseMapper.insert(security);

        log.info("创建安全配置: shareLinkId={}", bo.getShareLinkId());
        return security;
    }

    /**
     * 根据共享链接ID获取安全配置
     */
    @Override
    public CoShareSecurity getSecurityConfigByShareLinkId(Long shareLinkId) {
        return baseMapper.selectOne(
            Wrappers.lambdaQuery(CoShareSecurity.class)
                .eq(CoShareSecurity::getShareLinkId, shareLinkId)
        );
    }

    /**
     * 更新安全配置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateSecurityConfig(CoShareSecurityBo bo) {
        CoShareSecurity security = baseMapper.selectOne(
            Wrappers.lambdaQuery(CoShareSecurity.class)
                .eq(CoShareSecurity::getShareLinkId, bo.getShareLinkId())
        );

        if (security == null) {
            throw new RuntimeException("安全配置不存在");
        }

        // 更新字段
        if (bo.getIpWhitelistArray() != null) {
            security.setIpWhitelist(
                bo.getIpWhitelistArray().isEmpty() ? null : JSONUtil.toJsonStr(bo.getIpWhitelistArray())
            );
        }

        if (bo.getIpBlacklistArray() != null) {
            security.setIpBlacklist(
                bo.getIpBlacklistArray().isEmpty() ? null : JSONUtil.toJsonStr(bo.getIpBlacklistArray())
            );
        }

        if (bo.getMaxAccessPerHour() != null) {
            security.setMaxAccessPerHour(bo.getMaxAccessPerHour());
        }

        if (bo.getMaxAccessPerDay() != null) {
            security.setMaxAccessPerDay(bo.getMaxAccessPerDay());
        }

        if (bo.getIsEnabled() != null) {
            security.setIsEnabled(bo.getIsEnabled());
        }

        int result = baseMapper.updateById(security);
        log.info("更新安全配置: shareLinkId={}, result={}", bo.getShareLinkId(), result);

        return result > 0;
    }

    /**
     * 删除安全配置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteSecurityConfig(Long shareLinkId) {
        int result = baseMapper.delete(
            Wrappers.lambdaQuery(CoShareSecurity.class)
                .eq(CoShareSecurity::getShareLinkId, shareLinkId)
        );

        log.info("删除安全配置: shareLinkId={}, result={}", shareLinkId, result);
        return result > 0;
    }

    /**
     * 检查IP是否被允许访问
     */
    @Override
    public Boolean checkIpAllowed(Long shareLinkId, String accessIp) {
        CoShareSecurity security = getSecurityConfigByShareLinkId(shareLinkId);

        // 如果没有配置或未启用，默认允许
        if (security == null || security.getIsEnabled() == 0) {
            return true;
        }

        // 检查黑名单
        if (StringUtils.isNotBlank(security.getIpBlacklist())) {
            List<String> blacklist = JSONUtil.toList(security.getIpBlacklist(), String.class);
            if (blacklist.contains(accessIp)) {
                log.warn("IP在黑名单中: shareLinkId={}, ip={}", shareLinkId, accessIp);
                return false;
            }
        }

        // 检查白名单（如果设置了白名单，只允许白名单IP访问）
        if (StringUtils.isNotBlank(security.getIpWhitelist())) {
            List<String> whitelist = JSONUtil.toList(security.getIpWhitelist(), String.class);
            if (!whitelist.contains(accessIp)) {
                log.warn("IP不在白名单中: shareLinkId={}, ip={}", shareLinkId, accessIp);
                return false;
            }
        }

        return true;
    }

    /**
     * 检查访问是否被限制
     */
    @Override
    public Boolean checkAccessRestricted(String shareCode, String accessIp) {
        // 获取共享链接
        CoShareLink shareLink = shareLinkMapper.selectOne(
            Wrappers.lambdaQuery(CoShareLink.class)
                .eq(CoShareLink::getShareCode, shareCode)
        );

        if (shareLink == null) {
            return true; // 链接不存在，视为受限
        }

        // 检查IP是否被允许
        if (!checkIpAllowed(shareLink.getId(), accessIp)) {
            return true; // IP被限制
        }

        // 获取安全配置
        CoShareSecurity security = getSecurityConfigByShareLinkId(shareLink.getId());

        // 如果没有配置或未启用，不受限
        if (security == null || security.getIsEnabled() == 0) {
            return false;
        }

        // 检查访问频率
        Boolean frequencyOk = accessService.checkAccessFrequency(
            shareCode, accessIp,
            security.getMaxAccessPerHour(),
            security.getMaxAccessPerDay()
        );

        if (!frequencyOk) {
            log.warn("访问频率受限: shareCode={}, ip={}", shareCode, accessIp);
            return true; // 访问频率受限
        }

        return false; // 不受限
    }

}

