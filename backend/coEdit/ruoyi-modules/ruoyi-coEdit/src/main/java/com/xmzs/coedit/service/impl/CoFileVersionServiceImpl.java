package com.xmzs.coedit.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.coedit.domain.CoFile;
import com.xmzs.coedit.domain.CoFileVersion;
import com.xmzs.coedit.domain.CoWorkspace;
import com.xmzs.coedit.domain.bo.CoFileVersionBo;
import com.xmzs.coedit.domain.vo.CoFileVersionVo;
import com.xmzs.coedit.mapper.CoFileMapper;
import com.xmzs.coedit.mapper.CoFileVersionMapper;
import com.xmzs.coedit.mapper.CoWorkspaceMapper;
import com.xmzs.coedit.service.ICoFileVersionService;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.system.domain.SysUser;
import com.xmzs.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件版本Service业务层处理
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CoFileVersionServiceImpl implements ICoFileVersionService {

    private final CoFileVersionMapper baseMapper;
    private final CoFileMapper fileMapper;
    private final CoWorkspaceMapper workspaceMapper;
    private final SysUserMapper sysUserMapper;
    private final com.xmzs.coedit.service.DocumentService documentService;

    @Value("${file.upload-path:./upload}")
    private String uploadPath;

    /**
     * 创建文件版本
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CoFileVersionVo createVersion(CoFileVersionBo bo, Long userId, String userName) {
        // 获取文件信息
        CoFile file = fileMapper.selectById(bo.getFileId());
        if (file == null) {
            throw new RuntimeException("文件不存在");
        }

        // 检查文件是否存在
        File sourceFile = new File(file.getFilePath());
        if (!sourceFile.exists()) {
            throw new RuntimeException("源文件不存在");
        }

        // 获取下一个版本号
        Integer nextVersionNumber = getNextVersionNumber(bo.getFileId());

        // 创建版本存储目录
        String versionDir = uploadPath + "/versions/" + bo.getFileId() + "/" + nextVersionNumber;
        FileUtil.mkdir(versionDir);

        // 复制文件到版本目录
        String versionFilePath = versionDir + "/" + file.getFileName();
        try {
            FileUtil.copy(sourceFile, new File(versionFilePath), true);

            // 【验证】确保文件被正确复制
            File copiedFile = new File(versionFilePath);
            if (!copiedFile.exists()) {
                log.error("文件复制验证失败：目标文件不存在: {}", versionFilePath);
                throw new RuntimeException("创建版本失败：文件未被正确复制");
            }

            long copiedSize = copiedFile.length();
            long sourceSize = sourceFile.length();
            if (copiedSize != sourceSize) {
                log.error("文件复制验证失败：源文件大小={}, 目标文件大小={}", sourceSize, copiedSize);
                throw new RuntimeException("创建版本失败：文件未被完整复制");
            }

            log.info("版本文件复制成功: versionFilePath={}, size={} bytes", versionFilePath, copiedSize);
        } catch (Exception e) {
            log.error("复制文件失败", e);
            throw new RuntimeException("创建版本失败：文件复制错误");
        }

        // 计算文件哈希值（计算源文件的哈希，作为版本的标识）
        String sourceFileHash = DigestUtil.md5Hex(sourceFile);
        String versionFileHash = DigestUtil.md5Hex(new File(versionFilePath));

        // 验证哈希值一致性
        if (!sourceFileHash.equals(versionFileHash)) {
            log.error("文件哈希值验证失败：源文件={}, 版本文件={}", sourceFileHash, versionFileHash);
            throw new RuntimeException("创建版本失败：文件完整性验证失败");
        }

        String fileHash = versionFileHash;
        log.info("文件哈希值验证成功: hash={}", fileHash);

        // 将当前版本设为非当前
        baseMapper.update(null,
            Wrappers.lambdaUpdate(CoFileVersion.class)
                .eq(CoFileVersion::getFileId, bo.getFileId())
                .eq(CoFileVersion::getIsCurrent, 1)
                .set(CoFileVersion::getIsCurrent, 0)
        );

        // 获取工作空间的创建者昵称（域名昵称）
        String createdName = getWorkspaceOwnerNickname(file.getWorkspaceId());
        if (StrUtil.isBlank(createdName)) {
            createdName = userName; // 如果获取失败，使用用户名作为备用
        }

        // 创建版本记录
        CoFileVersion version = new CoFileVersion();
        version.setFileId(bo.getFileId());
        version.setVersionNumber(nextVersionNumber);
        version.setFileName(file.getFileName());
        version.setFileSize(file.getFileSize());
        version.setFilePath(versionFilePath);
        version.setFileHash(fileHash);
        version.setVersionDesc(bo.getVersionDesc());
        version.setChangeType(bo.getChangeType() != null ? bo.getChangeType() : 1);
        version.setChangeDescription(bo.getChangeDescription());
        version.setIsCurrent(1);
        version.setIsMajor(bo.getIsMajor() != null ? bo.getIsMajor() : 0);
        version.setCreatedBy(userId);
        version.setCreatedName(createdName);  // 使用工作空间创建者的昵称
        version.setCreateTime(new Date());

        baseMapper.insert(version);

        log.info("创建文件版本: fileId={}, versionNumber={}, userId={}, createdName={}",
                 bo.getFileId(), nextVersionNumber, userId, createdName);

        // 转换为VO并返回
        CoFileVersionVo vo = MapstructUtils.convert(version, CoFileVersionVo.class);
        enrichVersionVo(vo);
        return vo;
    }

    /**
     * 根据文件ID查询版本列表
     */
    @Override
    public TableDataInfo<CoFileVersionVo> getVersionListByFileId(Long fileId, PageQuery pageQuery) {
        LambdaQueryWrapper<CoFileVersion> wrapper = Wrappers.lambdaQuery(CoFileVersion.class)
            .eq(CoFileVersion::getFileId, fileId)
            .orderByDesc(CoFileVersion::getVersionNumber);

        Page<CoFileVersionVo> page = baseMapper.selectVoPage(pageQuery.build(), wrapper);

        // 丰富VO信息
        page.getRecords().forEach(this::enrichVersionVo);

        return TableDataInfo.build(page);
    }

    /**
     * 根据版本ID获取版本详情
     */
    @Override
    public CoFileVersionVo getVersionById(Long versionId) {
        CoFileVersionVo vo = baseMapper.selectVoById(versionId);
        if (vo != null) {
            enrichVersionVo(vo);
        }
        return vo;
    }

    /**
     * 版本对比
     */
    @Override
    public Map<String, Object> compareVersions(Long versionId1, Long versionId2) {
        Map<String, Object> result = new HashMap<>();

        CoFileVersion version1 = baseMapper.selectById(versionId1);
        CoFileVersion version2 = baseMapper.selectById(versionId2);

        if (version1 == null || version2 == null) {
            throw new RuntimeException("版本不存在");
        }

        if (!version1.getFileId().equals(version2.getFileId())) {
            throw new RuntimeException("不能对比不同文件的版本");
        }

        // 基本信息对比
        result.put("version1", MapstructUtils.convert(version1, CoFileVersionVo.class));
        result.put("version2", MapstructUtils.convert(version2, CoFileVersionVo.class));

        // 文件大小对比
        result.put("sizeDiff", version2.getFileSize() - version1.getFileSize());

        // 哈希值对比
        result.put("isSameContent", version1.getFileHash().equals(version2.getFileHash()));

        // 时间差
        long timeDiff = version2.getCreateTime().getTime() - version1.getCreateTime().getTime();
        result.put("timeDiffSeconds", timeDiff / 1000);

        // 版本号差
        result.put("versionDiff", version2.getVersionNumber() - version1.getVersionNumber());

        log.info("版本对比: versionId1={}, versionId2={}", versionId1, versionId2);

        return result;
    }

    /**
     * 版本回滚
     *
     * 【重要】该方法实现版本回滚功能，需要处理OnlyOffice编辑器的缓存问题
     * 当文件内容改变时，必须更新documentKey才能强制编辑器刷新缓存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean rollbackToVersion(Long versionId, Long userId, String userName) {
        log.info("========== [版本回滚] 开始调试 ==========");
        log.info("[回滚入口] versionId={}, userId={}, userName={}", versionId, userId, userName);

        // 获取要回滚到的版本
        CoFileVersion targetVersion = baseMapper.selectById(versionId);
        if (targetVersion == null) {
            log.error("[回滚失败] 版本不存在: versionId={}", versionId);
            throw new RuntimeException("版本不存在");
        }
        log.info("[目标版本信息] id={}, fileId={}, versionNumber={}, fileName={}, filePath={}, fileSize={}, fileHash={}",
                 targetVersion.getId(), targetVersion.getFileId(), targetVersion.getVersionNumber(),
                 targetVersion.getFileName(), targetVersion.getFilePath(), targetVersion.getFileSize(),
                 targetVersion.getFileHash());

        // 获取文件信息
        CoFile file = fileMapper.selectById(targetVersion.getFileId());
        if (file == null) {
            log.error("[回滚失败] 文件不存在: fileId={}", targetVersion.getFileId());
            throw new RuntimeException("文件不存在");
        }
        log.info("[当前文件信息] id={}, fileName={}, filePath={}, fileSize={}, documentKey={}",
                 file.getId(), file.getFileName(), file.getFilePath(), file.getFileSize(), file.getDocumentKey());

        // 检查版本文件是否存在
        File versionFile = new File(targetVersion.getFilePath());
        log.info("[版本文件检查] absolutePath={}, exists={}, isFile={}, length={}",
                 versionFile.getAbsolutePath(), versionFile.exists(), versionFile.isFile(), versionFile.length());
        if (!versionFile.exists()) {
            log.error("[回滚失败] 版本文件不存在: filePath={}", targetVersion.getFilePath());
            throw new RuntimeException("版本文件不存在");
        }

        // 计算版本文件的哈希值
        String versionFileHash = DigestUtil.md5Hex(versionFile);
        log.info("[版本文件哈希] hash={}", versionFileHash);

        // 【关键】复制版本文件到当前文件位置
        File currentFile = new File(file.getFilePath());
        log.info("[当前文件检查(复制前)] absolutePath={}, exists={}, isFile={}, length={}",
                 currentFile.getAbsolutePath(), currentFile.exists(), currentFile.isFile(),
                 currentFile.exists() ? currentFile.length() : 0);

        // 计算复制前当前文件的哈希值
        String beforeCopyHash = null;
        if (currentFile.exists()) {
            beforeCopyHash = DigestUtil.md5Hex(currentFile);
            log.info("[当前文件哈希(复制前)] hash={}", beforeCopyHash);
        }

        try {
            log.info("[开始复制] 源={} -> 目标={}", versionFile.getAbsolutePath(), currentFile.getAbsolutePath());
            FileUtil.copy(versionFile, currentFile, true);
            log.info("[复制完成] FileUtil.copy() 执行完毕");

            // 验证复制是否成功
            if (!currentFile.exists()) {
                log.error("[复制验证失败] 目标文件不存在");
                throw new RuntimeException("版本回滚失败：文件未被正确复制");
            }

            // 计算复制后当前文件的哈希值
            String afterCopyHash = DigestUtil.md5Hex(currentFile);
            log.info("[当前文件检查(复制后)] absolutePath={}, exists={}, isFile={}, length={}",
                     currentFile.getAbsolutePath(), currentFile.exists(), currentFile.isFile(), currentFile.length());
            log.info("[当前文件哈希(复制后)] hash={}", afterCopyHash);

            // 关键验证：比较哈希值
            boolean hashMatch = versionFileHash.equals(afterCopyHash);
            log.info("[哈希比对] 版本文件={}, 复制后当前文件={}, 匹配={}", versionFileHash, afterCopyHash, hashMatch);

            if (!hashMatch) {
                log.error("[回滚失败] 文件内容验证失败：版本文件哈希与复制后哈希不匹配");
                throw new RuntimeException("版本回滚失败：文件内容未正确复制");
            }

            // 检查是否真的发生了变化
            if (beforeCopyHash != null && beforeCopyHash.equals(afterCopyHash)) {
                log.warn("[警告] 复制前后文件哈希相同，文件内容可能未发生实际变化！beforeHash={}, afterHash={}",
                         beforeCopyHash, afterCopyHash);
            } else {
                log.info("[成功] 文件内容已变化: beforeHash={}, afterHash={}", beforeCopyHash, afterCopyHash);
            }

            log.info("[复制成功] 版本文件复制成功");
        } catch (Exception e) {
            log.error("[回滚失败] 文件复制异常: versionId={}, fileId={}, error={}",
                     versionId, targetVersion.getFileId(), e.getMessage(), e);
            throw new RuntimeException("版本回滚失败：文件复制错误 - " + e.getMessage());
        }

        // 【关键】更新文件信息（包括大小和修改时间）
        long newFileSize = currentFile.length();
        String oldDocumentKey = file.getDocumentKey();
        file.setFileSize(newFileSize);
        file.setUpdateTime(new Date());

        // 【关键】更新documentKey以强制OnlyOffice编辑器刷新缓存
        String newDocumentKey = java.util.UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
        file.setDocumentKey(newDocumentKey);
        log.info("[更新documentKey] 旧={}, 新={}", oldDocumentKey, newDocumentKey);

        int updateCount = fileMapper.updateById(file);
        log.info("[数据库更新] fileId={}, newSize={}, newDocumentKey={}, 更新行数={}",
                 targetVersion.getFileId(), newFileSize, newDocumentKey, updateCount);

        if (updateCount <= 0) {
            log.error("[回滚失败] 数据库更新失败：更新行数为0");
            throw new RuntimeException("版本回滚失败：更新文件信息失败");
        }

        // 【关键修复】同步更新 DocumentService 的内存缓存
        // 确保后续的编辑器配置请求能获取到正确的 documentKey
        String fileIdStr = String.valueOf(targetVersion.getFileId());
        documentService.setDocumentKey(fileIdStr, newDocumentKey);
        log.info("[缓存同步] 已更新 DocumentService 缓存: fileId={}, newDocumentKey={}", fileIdStr, newDocumentKey);

        // 【关键修复】更新版本标记：将所有版本的is_current设为0
        baseMapper.update(null,
            Wrappers.lambdaUpdate(CoFileVersion.class)
                .eq(CoFileVersion::getFileId, targetVersion.getFileId())
                .set(CoFileVersion::getIsCurrent, 0)
        );

        // 【关键修复】将目标版本的is_current设为1（标记为当前版本）
        baseMapper.update(null,
            Wrappers.lambdaUpdate(CoFileVersion.class)
                .eq(CoFileVersion::getId, versionId)
                .set(CoFileVersion::getIsCurrent, 1)
        );
        log.info("[版本标记更新] versionId={} 已设为当前版本", versionId);

        // 【最终验证】回滚完成后再次读取文件验证
        File verifyFile = new File(file.getFilePath());
        String verifyHash = DigestUtil.md5Hex(verifyFile);
        log.info("[最终验证] 回滚后文件: path={}, size={}, hash={}", verifyFile.getAbsolutePath(), verifyFile.length(), verifyHash);
        log.info("[最终验证] 与版本文件哈希比对: 版本={}, 当前={}, 匹配={}", versionFileHash, verifyHash, versionFileHash.equals(verifyHash));

        log.info("[回滚完成] versionId={}, fileId={}, targetVersionNumber={}, userId={}, newDocumentKey={}",
                 versionId, targetVersion.getFileId(), targetVersion.getVersionNumber(), userId, newDocumentKey);
        log.info("========== [版本回滚] 调试结束 ==========");

        return true;
    }

    /**
     * 删除指定版本
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteVersion(Long versionId) {
        CoFileVersion version = baseMapper.selectById(versionId);
        if (version == null) {
            throw new RuntimeException("版本不存在");
        }

        // 不能删除当前版本
        if (version.getIsCurrent() == 1) {
            throw new RuntimeException("不能删除当前版本");
        }

        // 删除版本文件
        File versionFile = new File(version.getFilePath());
        if (versionFile.exists()) {
            FileUtil.del(versionFile);
        }

        // 删除数据库记录
        int result = baseMapper.deleteById(versionId);

        log.info("删除版本: versionId={}, fileId={}", versionId, version.getFileId());

        return result > 0;
    }

    /**
     * 清理文件的旧版本
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer cleanOldVersions(Long fileId, Integer keepCount) {
        if (keepCount == null || keepCount < 1) {
            keepCount = 10; // 默认保留10个版本
        }

        // 查询所有版本（按版本号降序）
        List<CoFileVersion> versions = baseMapper.selectList(
            Wrappers.lambdaQuery(CoFileVersion.class)
                .eq(CoFileVersion::getFileId, fileId)
                .orderByDesc(CoFileVersion::getVersionNumber)
        );

        if (versions.size() <= keepCount) {
            return 0; // 版本数量未超过保留数量
        }

        // 获取要删除的版本（保留最新的keepCount个）
        List<CoFileVersion> versionsToDelete = versions.subList(keepCount, versions.size());

        int deletedCount = 0;
        for (CoFileVersion version : versionsToDelete) {
            // 不删除当前版本
            if (version.getIsCurrent() == 1) {
                continue;
            }

            // 删除版本文件
            File versionFile = new File(version.getFilePath());
            if (versionFile.exists()) {
                FileUtil.del(versionFile);
            }

            // 删除数据库记录
            baseMapper.deleteById(version.getId());
            deletedCount++;
        }

        log.info("清理旧版本: fileId={}, keepCount={}, deletedCount={}",
                 fileId, keepCount, deletedCount);

        return deletedCount;
    }

    /**
     * 获取文件的当前版本
     */
    @Override
    public CoFileVersionVo getCurrentVersion(Long fileId) {
        CoFileVersion version = baseMapper.selectOne(
            Wrappers.lambdaQuery(CoFileVersion.class)
                .eq(CoFileVersion::getFileId, fileId)
                .eq(CoFileVersion::getIsCurrent, 1)
        );

        if (version == null) {
            return null;
        }

        CoFileVersionVo vo = MapstructUtils.convert(version, CoFileVersionVo.class);
        enrichVersionVo(vo);
        return vo;
    }

    /**
     * 获取文件的版本统计
     */
    @Override
    public Map<String, Object> getVersionStatistics(Long fileId) {
        Map<String, Object> statistics = new HashMap<>();

        // 总版本数
        Long totalCount = baseMapper.selectCount(
            Wrappers.lambdaQuery(CoFileVersion.class)
                .eq(CoFileVersion::getFileId, fileId)
        );
        statistics.put("totalCount", totalCount);

        // 主要版本数
        Long majorCount = baseMapper.selectCount(
            Wrappers.lambdaQuery(CoFileVersion.class)
                .eq(CoFileVersion::getFileId, fileId)
                .eq(CoFileVersion::getIsMajor, 1)
        );
        statistics.put("majorCount", majorCount);

        // 当前版本号
        CoFileVersion currentVersion = baseMapper.selectOne(
            Wrappers.lambdaQuery(CoFileVersion.class)
                .eq(CoFileVersion::getFileId, fileId)
                .eq(CoFileVersion::getIsCurrent, 1)
        );
        statistics.put("currentVersionNumber",
                      currentVersion != null ? currentVersion.getVersionNumber() : 0);

        // 最早版本时间
        CoFileVersion firstVersion = baseMapper.selectOne(
            Wrappers.lambdaQuery(CoFileVersion.class)
                .eq(CoFileVersion::getFileId, fileId)
                .orderByAsc(CoFileVersion::getVersionNumber)
                .last("LIMIT 1")
        );
        statistics.put("firstVersionTime",
                      firstVersion != null ? firstVersion.getCreateTime() : null);

        // 最新版本时间
        CoFileVersion latestVersion = baseMapper.selectOne(
            Wrappers.lambdaQuery(CoFileVersion.class)
                .eq(CoFileVersion::getFileId, fileId)
                .orderByDesc(CoFileVersion::getVersionNumber)
                .last("LIMIT 1")
        );
        statistics.put("latestVersionTime",
                      latestVersion != null ? latestVersion.getCreateTime() : null);

        return statistics;
    }

    /**
     * 获取下一个版本号
     */
    private Integer getNextVersionNumber(Long fileId) {
        CoFileVersion latestVersion = baseMapper.selectOne(
            Wrappers.lambdaQuery(CoFileVersion.class)
                .eq(CoFileVersion::getFileId, fileId)
                .orderByDesc(CoFileVersion::getVersionNumber)
                .last("LIMIT 1")
        );

        return latestVersion != null ? latestVersion.getVersionNumber() + 1 : 1;
    }

    /**
     * 丰富版本VO信息
     */
    private void enrichVersionVo(CoFileVersionVo vo) {
        // 格式化文件大小
        if (vo.getFileSize() != null) {
            vo.setFileSizeFormatted(formatFileSize(vo.getFileSize()));
        }

        // 设置变更类型描述
        if (vo.getChangeType() != null) {
            String[] changeTypes = {"创建", "修改", "重命名", "回滚"};
            if (vo.getChangeType() >= 0 && vo.getChangeType() < changeTypes.length) {
                vo.setChangeTypeDesc(changeTypes[vo.getChangeType()]);
            }
        }

        // 格式化编辑时长
        if (vo.getEditDuration() != null) {
            vo.setEditDurationFormatted(formatDuration(vo.getEditDuration()));
        }
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(Long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }

    /**
     * 格式化时长
     */
    private String formatDuration(Integer seconds) {
        if (seconds < 60) {
            return seconds + "秒";
        } else if (seconds < 3600) {
            return (seconds / 60) + "分" + (seconds % 60) + "秒";
        } else {
            int hours = seconds / 3600;
            int minutes = (seconds % 3600) / 60;
            int secs = seconds % 60;
            return hours + "小时" + minutes + "分" + secs + "秒";
        }
    }

    /**
     * 获取工作空间所有者的昵称
     *
     * @param workspaceId 工作空间ID
     * @return 所有者昵称
     */
    private String getWorkspaceOwnerNickname(Long workspaceId) {
        try {
            if (workspaceId == null) {
                return null;
            }

            // 获取工作空间信息
            CoWorkspace workspace = workspaceMapper.selectById(workspaceId);
            if (workspace == null || workspace.getOwnerId() == null) {
                return null;
            }

            // 获取工作空间所有者的用户信息
            SysUser user = sysUserMapper.selectById(workspace.getOwnerId());
            if (user == null) {
                return null;
            }

            // 优先返回昵称，如果昵称为空则返回用户名
            String nickName = user.getNickName();
            if (StrUtil.isBlank(nickName)) {
                return user.getUserName();
            }
            return nickName;
        } catch (Exception e) {
            log.warn("获取工作空间所有者昵称失败, workspaceId={}", workspaceId, e);
            return null;
        }
    }

    /**
     * 下载版本文件
     *
     * @param versionId 版本ID
     * @return 文件字节数组
     */
    @Override
    public byte[] downloadVersionFile(Long versionId) {
        CoFileVersion version = baseMapper.selectById(versionId);
        if (version == null) {
            log.error("版本不存在: versionId={}", versionId);
            throw new RuntimeException("版本不存在");
        }

        // 使用版本的filePath（重要：不要使用当前文件的路径）
        String versionFilePath = version.getFilePath();
        log.info("开始下载版本文件: versionId={}, fileId={}, filePath={}",
                 versionId, version.getFileId(), versionFilePath);

        // 检查版本文件是否存在
        File versionFile = new File(versionFilePath);
        if (!versionFile.exists()) {
            log.error("版本文件不存在: filePath={}, versionId={}", versionFilePath, versionId);
            throw new RuntimeException("版本文件不存在: " + versionFilePath);
        }

        // 验证文件大小
        long fileSize = versionFile.length();
        long dbFileSize = version.getFileSize();
        if (fileSize != dbFileSize) {
            log.warn("版本文件大小不匹配：物理文件={} bytes, 数据库记录={} bytes, versionId={}",
                     fileSize, dbFileSize, versionId);
            // 不抛出异常，使用物理文件的实际大小
        }

        // 读取文件内容
        byte[] fileBytes = FileUtil.readBytes(versionFile);

        log.info("下载版本文件成功: versionId={}, fileId={}, fileName={}, size={} bytes, hash={}",
                 versionId, version.getFileId(), version.getFileName(), fileBytes.length, version.getFileHash());

        return fileBytes;
    }

}

