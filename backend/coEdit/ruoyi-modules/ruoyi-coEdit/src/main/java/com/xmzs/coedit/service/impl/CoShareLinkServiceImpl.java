package com.xmzs.coedit.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.coedit.domain.CoFile;
import com.xmzs.coedit.domain.CoShareLink;
import com.xmzs.coedit.domain.bo.CoShareLinkBo;
import com.xmzs.coedit.domain.vo.CoShareLinkVo;
import com.xmzs.coedit.mapper.CoFileMapper;
import com.xmzs.coedit.mapper.CoShareLinkMapper;
import com.xmzs.coedit.service.ICoShareLinkService;
import com.xmzs.coedit.utils.FileUtil;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 共享链接Service业务层处理
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CoShareLinkServiceImpl implements ICoShareLinkService {

    private final CoShareLinkMapper baseMapper;
    private final CoFileMapper fileMapper;

    /**
     * 前端地址配置
     */
    @Value("${onlyoffice.frontend.url}")
    private String frontendUrl;

    /**
     * 创建共享链接
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CoShareLinkVo createShareLink(CoShareLinkBo bo, Long userId, String userName) {
        // 检查文件是否存在
        CoFile file = fileMapper.selectById(bo.getFileId());
        if (file == null) {
            throw new RuntimeException("文件不存在");
        }

        // 生成唯一的共享码
        String shareCode = generateShareCode();
        while (baseMapper.selectCount(
            Wrappers.lambdaQuery(CoShareLink.class)
                .eq(CoShareLink::getShareCode, shareCode)
        ) > 0) {
            shareCode = generateShareCode();
        }

        // 创建共享链接对象
        CoShareLink shareLink = MapstructUtils.convert(bo, CoShareLink.class);
        shareLink.setShareCode(shareCode);
        shareLink.setCreatorId(userId);
        shareLink.setCreatorName(userName);

        // 设置共享类型，默认为公开
        if (shareLink.getShareType() == null) {
            shareLink.setShareType(0);
        }

        // 如果设置了密码，直接存储明文
        if (StringUtils.isNotBlank(bo.getSharePassword())) {
            shareLink.setShareType(1); // 密码保护
            shareLink.setSharePassword(bo.getSharePassword()); // 直接存储明文
        }

        // 设置权限类型，默认为只读
        if (shareLink.getPermissionType() == null) {
            shareLink.setPermissionType(0);
        }

        // 设置细粒度权限，如果前端传入了权限字段则使用，否则使用默认值1（允许）
        if (shareLink.getAllowDownload() == null) {
            shareLink.setAllowDownload(1);
        }
        if (shareLink.getAllowPrint() == null) {
            shareLink.setAllowPrint(1);
        }
        if (shareLink.getAllowCopy() == null) {
            shareLink.setAllowCopy(1);
        }
        if (shareLink.getAllowEdit() == null) {
            shareLink.setAllowEdit(1);
        }
        if (shareLink.getAllowComment() == null) {
            shareLink.setAllowComment(1);
        }

        log.debug("创建共享链接权限配置: allowDownload={}, allowPrint={}, allowCopy={}, allowEdit={}, allowComment={}",
            shareLink.getAllowDownload(), shareLink.getAllowPrint(), shareLink.getAllowCopy(),
            shareLink.getAllowEdit(), shareLink.getAllowComment());

        // 计算过期时间
        if (bo.getValidDays() != null && bo.getValidDays() > 0) {
            Date expireTime = new Date(System.currentTimeMillis() + bo.getValidDays() * 24 * 60 * 60 * 1000L);
            shareLink.setExpireTime(expireTime);
        } else if (bo.getExpireTime() != null) {
            shareLink.setExpireTime(bo.getExpireTime());
        }

        // 生成完整的分享URL并保存到数据库
        String shareUrl = generateShareUrl(shareCode);
        shareLink.setShareUrl(shareUrl);

        // 设置访问次数
        shareLink.setAccessCount(0);
        shareLink.setStatus(0); // 正常状态

        // 保存到数据库
        baseMapper.insert(shareLink);

        log.info("创建共享链接成功: fileId={}, shareCode={}, shareUrl={}, creatorId={}",
            bo.getFileId(), shareCode, shareUrl, userId);

        // 转换为VO并返回
        CoShareLinkVo vo = MapstructUtils.convert(shareLink, CoShareLinkVo.class);
        enrichShareLinkVo(vo, file);

        return vo;
    }

    /**
     * 根据共享码获取共享链接
     */
    @Override
    public CoShareLinkVo getShareLinkByCode(String shareCode) {
        CoShareLink shareLink = baseMapper.selectOne(
            Wrappers.lambdaQuery(CoShareLink.class)
                .eq(CoShareLink::getShareCode, shareCode)
        );

        if (shareLink == null) {
            return null;
        }

        CoShareLinkVo vo = MapstructUtils.convert(shareLink, CoShareLinkVo.class);

        // 获取文件信息
        CoFile file = fileMapper.selectById(shareLink.getFileId());
        enrichShareLinkVo(vo, file);

        return vo;
    }

    /**
     * 根据ID获取共享链接
     */
    @Override
    public CoShareLinkVo getShareLinkById(Long id) {
        CoShareLink shareLink = baseMapper.selectById(id);
        if (shareLink == null) {
            return null;
        }

        CoShareLinkVo vo = MapstructUtils.convert(shareLink, CoShareLinkVo.class);

        // 获取文件信息
        CoFile file = fileMapper.selectById(shareLink.getFileId());
        enrichShareLinkVo(vo, file);

        return vo;
    }

    /**
     * 根据文件ID获取共享链接列表
     */
    @Override
    public TableDataInfo<CoShareLinkVo> getShareLinksByFileId(Long fileId, PageQuery pageQuery) {
        LambdaQueryWrapper<CoShareLink> wrapper = Wrappers.lambdaQuery(CoShareLink.class)
            .eq(CoShareLink::getFileId, fileId)
            .orderByDesc(CoShareLink::getCreateTime);

        Page<CoShareLinkVo> page = baseMapper.selectVoPage(pageQuery.build(), wrapper);

        // 获取文件信息
        CoFile file = fileMapper.selectById(fileId);

        // 丰富VO信息
        page.getRecords().forEach(vo -> enrichShareLinkVo(vo, file));

        return TableDataInfo.build(page);
    }

    /**
     * 根据创建者ID获取共享链接列表
     */
    @Override
    public TableDataInfo<CoShareLinkVo> getShareLinksByCreatorId(Long creatorId, PageQuery pageQuery) {
        LambdaQueryWrapper<CoShareLink> wrapper = Wrappers.lambdaQuery(CoShareLink.class)
            .eq(CoShareLink::getCreatorId, creatorId)
            .orderByDesc(CoShareLink::getCreateTime);

        Page<CoShareLinkVo> page = baseMapper.selectVoPage(pageQuery.build(), wrapper);

        // 丰富VO信息
        page.getRecords().forEach(vo -> {
            CoFile file = fileMapper.selectById(vo.getFileId());
            enrichShareLinkVo(vo, file);
        });

        return TableDataInfo.build(page);
    }

    /**
     * 更新共享链接
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateShareLink(CoShareLinkBo bo) {
        CoShareLink shareLink = baseMapper.selectById(bo.getId());
        if (shareLink == null) {
            throw new RuntimeException("共享链接不存在");
        }

        // 更新字段
        if (bo.getShareType() != null) {
            shareLink.setShareType(bo.getShareType());
        }

        // 如果设置了新密码，直接存储明文
        if (StringUtils.isNotBlank(bo.getSharePassword())) {
            shareLink.setShareType(1); // 密码保护
            shareLink.setSharePassword(bo.getSharePassword()); // 直接存储明文
        }

        if (bo.getPermissionType() != null) {
            shareLink.setPermissionType(bo.getPermissionType());
        }

        // 更新过期时间
        if (bo.getValidDays() != null && bo.getValidDays() > 0) {
            Date expireTime = new Date(System.currentTimeMillis() + bo.getValidDays() * 24 * 60 * 60 * 1000L);
            shareLink.setExpireTime(expireTime);
        } else if (bo.getExpireTime() != null) {
            shareLink.setExpireTime(bo.getExpireTime());
        }

        if (bo.getMaxAccessCount() != null) {
            shareLink.setMaxAccessCount(bo.getMaxAccessCount());
        }

        // 更新细粒度权限
        if (bo.getAllowDownload() != null) {
            shareLink.setAllowDownload(bo.getAllowDownload());
        }
        if (bo.getAllowPrint() != null) {
            shareLink.setAllowPrint(bo.getAllowPrint());
        }
        if (bo.getAllowCopy() != null) {
            shareLink.setAllowCopy(bo.getAllowCopy());
        }
        if (bo.getAllowEdit() != null) {
            shareLink.setAllowEdit(bo.getAllowEdit());
        }
        if (bo.getAllowComment() != null) {
            shareLink.setAllowComment(bo.getAllowComment());
        }

        log.debug("更新共享链接权限配置: allowDownload={}, allowPrint={}, allowCopy={}, allowEdit={}, allowComment={}",
            shareLink.getAllowDownload(), shareLink.getAllowPrint(), shareLink.getAllowCopy(),
            shareLink.getAllowEdit(), shareLink.getAllowComment());

        int result = baseMapper.updateById(shareLink);
        log.info("更新共享链接: id={}, result={}", bo.getId(), result);

        return result > 0;
    }

    /**
     * 禁用共享链接
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean disableShareLink(Long id) {
        CoShareLink shareLink = baseMapper.selectById(id);
        if (shareLink == null) {
            return false;
        }

        shareLink.setStatus(1); // 已禁用
        int result = baseMapper.updateById(shareLink);

        log.info("禁用共享链接: id={}, result={}", id, result);
        return result > 0;
    }

    /**
     * 删除共享链接
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteShareLink(Long id) {
        int result = baseMapper.deleteById(id);
        log.info("删除共享链接: id={}, result={}", id, result);
        return result > 0;
    }

    /**
     * 批量删除共享链接
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteShareLinks(List<Long> ids) {
        int result = baseMapper.deleteBatchIds(ids);
        log.info("批量删除共享链接: count={}, result={}", ids.size(), result);
        return result > 0;
    }

    /**
     * 增加访问次数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean incrementAccessCount(String shareCode) {
        CoShareLink shareLink = baseMapper.selectOne(
            Wrappers.lambdaQuery(CoShareLink.class)
                .eq(CoShareLink::getShareCode, shareCode)
        );

        if (shareLink == null) {
            return false;
        }

        // 增加访问次数
        shareLink.setAccessCount(shareLink.getAccessCount() + 1);

        // 检查是否达到最大访问次数
        if (shareLink.getMaxAccessCount() != null
            && shareLink.getAccessCount() >= shareLink.getMaxAccessCount()) {
            shareLink.setStatus(2); // 已过期
            log.info("共享链接达到最大访问次数: shareCode={}, maxCount={}", shareCode, shareLink.getMaxAccessCount());
        }

        int result = baseMapper.updateById(shareLink);
        return result > 0;
    }

    /**
     * 检查共享链接是否有效
     */
    @Override
    public Boolean checkShareLinkValid(String shareCode, String password) {
        CoShareLink shareLink = baseMapper.selectOne(
            Wrappers.lambdaQuery(CoShareLink.class)
                .eq(CoShareLink::getShareCode, shareCode)
        );

        if (shareLink == null) {
            log.warn("共享链接不存在: shareCode={}", shareCode);
            return false;
        }

        // 检查状态
        if (shareLink.getStatus() != 0) {
            log.warn("共享链接状态异常: shareCode={}, status={}", shareCode, shareLink.getStatus());
            return false;
        }

        // 检查是否过期
        if (shareLink.getExpireTime() != null && shareLink.getExpireTime().before(new Date())) {
            log.warn("共享链接已过期: shareCode={}, expireTime={}", shareCode, shareLink.getExpireTime());
            return false;
        }

        // 检查访问次数
        if (shareLink.getMaxAccessCount() != null
            && shareLink.getAccessCount() >= shareLink.getMaxAccessCount()) {
            log.warn("共享链接已达到最大访问次数: shareCode={}, maxCount={}", shareCode, shareLink.getMaxAccessCount());
            return false;
        }

        // 检查密码
        if (shareLink.getShareType() == 1) {
            if (StringUtils.isBlank(password)) {
                log.warn("共享链接需要密码: shareCode={}", shareCode);
                return false;
            }
            // 直接比较明文密码
            if (!password.equals(shareLink.getSharePassword())) {
                log.warn("共享链接密码错误: shareCode={}", shareCode);
                return false;
            }
        }

        return true;
    }

    /**
     * 清理过期链接
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer cleanExpiredLinks() {
        Date now = new Date();

        // 查询过期的正常链接
        List<CoShareLink> expiredLinks = baseMapper.selectList(
            Wrappers.lambdaQuery(CoShareLink.class)
                .eq(CoShareLink::getStatus, 0) // 正常状态
                .lt(CoShareLink::getExpireTime, now) // 已过期
        );

        if (expiredLinks.isEmpty()) {
            return 0;
        }

        // 批量更新状态为已过期
        for (CoShareLink link : expiredLinks) {
            link.setStatus(2);
            baseMapper.updateById(link);
        }

        log.info("清理过期共享链接: count={}", expiredLinks.size());
        return expiredLinks.size();
    }

    /**
     * 生成共享码
     */
    @Override
    public String generateShareCode() {
        // 生成8位随机字符串（包含大小写字母和数字）
        return RandomUtil.randomString(8);
    }

    /**
     * 丰富共享链接VO信息
     */
    private void enrichShareLinkVo(CoShareLinkVo vo, CoFile file) {
        if (file != null) {
            vo.setFileName(file.getFileName());

            // 设置文件大小
            if (file.getFileSize() != null) {
                vo.setFileSize(file.getFileSize());
                vo.setFileSizeFormatted(FileUtil.formatFileSize(file.getFileSize()));
            }
        }

        // shareUrl 已经在创建时生成并保存到数据库,这里不需要重新生成
        // 直接使用数据库中的值即可

        // 设置共享类型描述
        if (vo.getShareType() != null) {
            vo.setShareTypeDesc(vo.getShareType() == 0 ? "公开" : "密码保护");
        }

        // 设置是否有密码
        vo.setHasPassword(vo.getShareType() != null && vo.getShareType() == 1);

        // 设置权限类型描述
        if (vo.getPermissionType() != null) {
            switch (vo.getPermissionType()) {
                case 0:
                    vo.setPermissionTypeDesc("只读");
                    break;
                case 1:
                    vo.setPermissionTypeDesc("可编辑");
                    break;
                case 2:
                    vo.setPermissionTypeDesc("可下载");
                    break;
                case 3:
                    vo.setPermissionTypeDesc("完全权限");
                    break;
            }
        }

        // 设置是否永久有效
        vo.setIsPermanent(vo.getExpireTime() == null);

        // 设置状态描述
        if (vo.getStatus() != null) {
            switch (vo.getStatus()) {
                case 0:
                    vo.setStatusDesc("正常");
                    break;
                case 1:
                    vo.setStatusDesc("已禁用");
                    break;
                case 2:
                    vo.setStatusDesc("已过期");
                    break;
            }
        }

        // 设置是否有效
        boolean isValid = vo.getStatus() != null && vo.getStatus() == 0;
        if (isValid && vo.getExpireTime() != null) {
            isValid = vo.getExpireTime().after(new Date());
        }
        if (isValid && vo.getMaxAccessCount() != null) {
            isValid = vo.getAccessCount() < vo.getMaxAccessCount();
        }
        vo.setIsValid(isValid);
    }

    /**
     * 生成完整的分享URL
     * 使用配置文件中的前端地址: onlyoffice.frontend.url
     * 注意: 前端路由使用 /s/:code 格式,不是 /share/:code
     */
    private String generateShareUrl(String shareCode) {
        return frontendUrl + "/s/" + shareCode;
    }

}

