package com.xmzs.coedit.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xmzs.coedit.constant.WorkspaceConstants;
import com.xmzs.coedit.domain.CoAccessRecord;
import com.xmzs.coedit.domain.CoFile;
import com.xmzs.coedit.domain.CoFolder;
import com.xmzs.coedit.domain.CoWorkspace;
import com.xmzs.coedit.domain.CoWorkspaceMember;
import com.xmzs.coedit.domain.vo.CoFileVo;
import com.xmzs.coedit.domain.vo.CoFolderVo;
import com.xmzs.coedit.domain.vo.CoWorkspaceVo;
import com.xmzs.coedit.domain.vo.FileTreeNodeVo;
import com.xmzs.coedit.domain.vo.FileTreeResponseVo;
import com.xmzs.coedit.mapper.CoAccessRecordMapper;
import com.xmzs.coedit.mapper.CoFileMapper;
import com.xmzs.coedit.mapper.CoFolderMapper;
import com.xmzs.coedit.mapper.CoWorkspaceMapper;
import com.xmzs.coedit.mapper.CoWorkspaceMemberMapper;
import com.xmzs.coedit.service.ICoFavoriteService;
import com.xmzs.coedit.service.ICoFileTreeService;
import com.xmzs.coedit.service.ICoWorkspaceMemberService;
import com.xmzs.common.core.exception.ServiceException;
import com.xmzs.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件树Service业务层处理
 *
 * @author zuleed
 * @date 2025-11-08
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CoFileTreeServiceImpl implements ICoFileTreeService {

    private final CoWorkspaceMapper workspaceMapper;
    private final CoFolderMapper folderMapper;
    private final CoFileMapper fileMapper;
    private final CoAccessRecordMapper accessRecordMapper;
    private final ICoFavoriteService favoriteService;
    private final ICoWorkspaceMemberService workspaceMemberService;
    private final CoWorkspaceMemberMapper workspaceMemberMapper;
    private final SysUserMapper sysUserMapper;

    /**
     * 获取工作空间的文件树(支持懒加载)
     *
     * @param workspaceId 工作空间ID
     * @param userId 用户ID
     * @param parentId 父文件夹ID,为null时返回根级文件夹和文件
     * @return 当前层级的文件夹和文件列表
     */
    @Override
    public List<FileTreeNodeVo> getFileTree(Long workspaceId, Long userId, Long parentId) {
        log.info("获取工作空间文件树(懒加载): workspaceId={}, userId={}, parentId={}",
                 workspaceId, userId, parentId);

        // 0. 验证工作空间访问权限
        validateWorkspaceAccess(workspaceId, userId);

        // 0.1 查询工作空间信息（用于设置 workspaceName）
        CoWorkspace workspace = workspaceMapper.selectById(workspaceId);
        String workspaceName = workspace != null ? workspace.getWorkspaceName() : null;

        List<FileTreeNodeVo> result = new ArrayList<>();

        // 1. 查询当前层级的文件夹(按创建时间倒序)
        LambdaQueryWrapper<CoFolder> folderWrapper = Wrappers.lambdaQuery(CoFolder.class)
            .eq(CoFolder::getWorkspaceId, workspaceId)
            .eq(CoFolder::getStatus, 1)  // 1-正常状态
            .orderByDesc(CoFolder::getCreateTime);

        if (parentId == null) {
            // 查询根级文件夹(parentId为null或0)
            folderWrapper.and(w -> w.isNull(CoFolder::getParentId).or().eq(CoFolder::getParentId, 0));
        } else {
            // 查询指定父文件夹下的子文件夹
            folderWrapper.eq(CoFolder::getParentId, parentId);
        }

        List<CoFolderVo> folders = folderMapper.selectVoList(folderWrapper);

        // 2. 查询当前层级的文件(按创建时间倒序)
        LambdaQueryWrapper<CoFile> fileWrapper = Wrappers.lambdaQuery(CoFile.class)
            .eq(CoFile::getWorkspaceId, workspaceId)
            .eq(CoFile::getStatus, 1)  // 1-正常状态
            .orderByDesc(CoFile::getCreateTime);

        if (parentId == null) {
            // 查询根级文件(folderId为null或0)
            fileWrapper.and(w -> w.isNull(CoFile::getFolderId).or().eq(CoFile::getFolderId, 0));
        } else {
            // 查询指定文件夹下的文件
            fileWrapper.eq(CoFile::getFolderId, parentId);
        }

        List<CoFileVo> files = fileMapper.selectVoList(fileWrapper);

        // 2.1 批量查询文件所有者的昵称
        if (!files.isEmpty()) {
            enrichFilesWithOwnerNickName(files);
        }

        // 3. 创建文件夹节点并设置统计信息和工作空间信息
        for (CoFolderVo folder : folders) {
            FileTreeNodeVo node = createFolderNode(folder, userId);
            setFolderStatistics(node, folder.getId());

            // 设置工作空间信息
            node.setWorkspaceId(workspaceId);
            node.setWorkspaceName(workspaceName);

            result.add(node);
        }

        // 4. 创建文件节点并设置工作空间信息
        for (CoFileVo file : files) {
            FileTreeNodeVo node = createFileNode(file, userId);

            // 设置工作空间信息
            node.setWorkspaceId(workspaceId);
            node.setWorkspaceName(workspaceName);

            result.add(node);
        }

        log.info("文件树构建完成(懒加载): workspaceId={}, parentId={}, 节点数={}",
                 workspaceId, parentId, result.size());
        return result;
    }

    /**
     * 获取文件夹下的文件树
     */
    @Override
    public List<FileTreeNodeVo> getFolderTree(Long folderId, Long userId, Boolean includeSubfolders) {
        log.info("获取文件夹文件树: folderId={}, userId={}, includeSubfolders={}",
                 folderId, userId, includeSubfolders);

        List<FileTreeNodeVo> nodes = new ArrayList<>();

        // 查询文件夹下的文件(按创建时间倒序)
        List<CoFileVo> files = fileMapper.selectVoList(
            Wrappers.lambdaQuery(CoFile.class)
                .eq(CoFile::getFolderId, folderId)
                .eq(CoFile::getStatus, 1)  // 1-正常状态
                .orderByDesc(CoFile::getCreateTime)
        );

        // 批量查询文件所有者的昵称
        if (!files.isEmpty()) {
            enrichFilesWithOwnerNickName(files);
        }

        for (CoFileVo file : files) {
            FileTreeNodeVo fileNode = createFileNode(file, userId);
            nodes.add(fileNode);
        }

        // 如果需要包含子文件夹
        if (includeSubfolders != null && includeSubfolders) {
            List<CoFolderVo> subfolders = folderMapper.selectVoList(
                Wrappers.lambdaQuery(CoFolder.class)
                    .eq(CoFolder::getParentId, folderId)
                    .eq(CoFolder::getStatus, 1)  // 1-正常状态
                    .orderByDesc(CoFolder::getCreateTime)
            );

            for (CoFolderVo folder : subfolders) {
                FileTreeNodeVo folderNode = createFolderNode(folder, userId);
                // 递归获取子文件夹的内容
                List<FileTreeNodeVo> children = getFolderTree(folder.getId(), userId, true);
                folderNode.getChildren().addAll(children);
                calculateStatistics(folderNode);
                nodes.add(folderNode);
            }
        }

        return nodes;
    }

    /**
     * 获取用户的所有文件树(跨工作空间)
     * 返回以工作空间为根节点的树形结构
     * 注意:此方法返回完整树形结构,不使用懒加载
     */
    @Override
    public List<FileTreeNodeVo> getUserFileTree(Long userId) {
        log.info("获取用户所有文件树: userId={}", userId);

        // 查询用户创建的所有工作空间(按创建时间倒序)
        List<CoWorkspaceVo> workspaces = workspaceMapper.selectVoList(
            Wrappers.lambdaQuery(CoWorkspace.class)
                .eq(CoWorkspace::getCreateBy, userId.toString())
                .orderByDesc(CoWorkspace::getCreateTime)
        );

        List<FileTreeNodeVo> result = new ArrayList<>();

        // 为每个工作空间构建节点
        for (CoWorkspaceVo workspace : workspaces) {
            FileTreeNodeVo workspaceNode = createWorkspaceNode(workspace, userId);

            // 获取该工作空间下的根级文件树(懒加载模式,只获取第一层)
            List<FileTreeNodeVo> children = getFileTree(workspace.getId(), userId, null);
            workspaceNode.getChildren().addAll(children);

            // 计算统计信息(只统计第一层)
            calculateStatistics(workspaceNode);

            result.add(workspaceNode);
        }

        log.info("用户文件树构建完成: userId={}, 工作空间数={}", userId, workspaces.size());
        return result;
    }

    /**
     * 创建工作空间节点
     */
    private FileTreeNodeVo createWorkspaceNode(CoWorkspaceVo workspace, Long userId) {
        FileTreeNodeVo node = new FileTreeNodeVo();
        node.setNodeType("workspace");
        node.setId(workspace.getId());
        node.setName(workspace.getWorkspaceName());
        node.setParentId(null);
        node.setPath("/" + workspace.getWorkspaceName());
        node.setLevel(0);
        node.setCreateTime(workspace.getCreateTime());
        node.setUpdateTime(workspace.getUpdateTime());
        node.setWorkspaceInfo(workspace);
        node.setChildren(new ArrayList<>());

        // 检查是否已收藏
        if (userId != null) {
            Boolean isFavorited = favoriteService.isFavorited(userId, 3, workspace.getId());
            node.setIsFavorited(isFavorited);
        }

        return node;
    }

    /**
     * 创建文件夹节点
     */
    private FileTreeNodeVo createFolderNode(CoFolderVo folder, Long userId) {
        FileTreeNodeVo node = new FileTreeNodeVo();
        node.setNodeType("folder");
        node.setId(folder.getId());
        node.setName(folder.getFolderName());
        node.setParentId(folder.getParentId());
        node.setPath(folder.getFolderPath());
//        node.setLevel(folder.getLevel());
        node.setCreateTime(folder.getCreateTime());
        node.setUpdateTime(folder.getUpdateTime());
        node.setFolderInfo(folder);
        node.setChildren(new ArrayList<>());

        // 检查是否已收藏
        if (userId != null) {
            Boolean isFavorited = favoriteService.isFavorited(userId, 2, folder.getId());
            node.setIsFavorited(isFavorited);
        }

        return node;
    }

    /**
     * 创建文件节点
     */
    private FileTreeNodeVo createFileNode(CoFileVo file, Long userId) {
        FileTreeNodeVo node = new FileTreeNodeVo();
        node.setNodeType("file");
        node.setId(file.getId());
        node.setName(file.getFileName());
        node.setParentId(file.getFolderId());
        node.setPath(file.getFilePath());
        node.setCreateTime(file.getCreateTime());
        node.setUpdateTime(file.getUpdateTime());

        // 构建文件的层级路径
        String locationPath = buildLocationPath(file);
        file.setLocationPath(locationPath);

        node.setFileInfo(file);

        // 检查是否已收藏
        if (userId != null) {
            Boolean isFavorited = favoriteService.isFavorited(userId, 1, file.getId());
            node.setIsFavorited(isFavorited);

            // 获取最后访问时间(查询访问记录表,targetType=3表示文件)
            CoAccessRecord lastAccess = accessRecordMapper.selectOne(
                Wrappers.lambdaQuery(CoAccessRecord.class)
                    .eq(CoAccessRecord::getTargetType, 3)
                    .eq(CoAccessRecord::getTargetId, file.getId())
                    .eq(CoAccessRecord::getUserId, userId)
                    .orderByDesc(CoAccessRecord::getAccessTime)
                    .last("LIMIT 1")
            );
            if (lastAccess != null) {
                node.setLastAccessTime(lastAccess.getAccessTime());
            }
        }

        return node;
    }

    /**
     * 设置文件夹统计信息(懒加载模式)
     * 只统计直接子节点数量,不递归统计
     *
     * @param node 文件夹节点
     * @param folderId 文件夹ID
     */
    private void setFolderStatistics(FileTreeNodeVo node, Long folderId) {
        // 统计直接子文件夹数量
        Long subFolderCount = folderMapper.selectCount(
            Wrappers.lambdaQuery(CoFolder.class)
                .eq(CoFolder::getParentId, folderId)
                .eq(CoFolder::getStatus, 1)  // 1-正常状态
        );

        // 统计直接子文件数量
        Long subFileCount = fileMapper.selectCount(
            Wrappers.lambdaQuery(CoFile.class)
                .eq(CoFile::getFolderId, folderId)
                .eq(CoFile::getStatus, 1)  // 1-正常状态
        );

        node.setFolderCount(subFolderCount.intValue());
        node.setFileCount(subFileCount.intValue());
        node.setHasChildren((subFolderCount + subFileCount) > 0);
        // 懒加载模式不计算总文件数(性能考虑)
        node.setTotalFileCount(null);
    }

    /**
     * 计算统计信息(递归)
     * 支持workspace和folder节点
     * 用于非懒加载模式(如getUserFileTree)
     */
    private void calculateStatistics(FileTreeNodeVo node) {
        if (!"folder".equals(node.getNodeType()) && !"workspace".equals(node.getNodeType())) {
            return;
        }

        int fileCount = 0;
        int folderCount = 0;
        int totalFileCount = 0;

        for (FileTreeNodeVo child : node.getChildren()) {
            if ("file".equals(child.getNodeType())) {
                fileCount++;
                totalFileCount++;
            } else if ("folder".equals(child.getNodeType())) {
                folderCount++;
                // 递归计算子文件夹
                calculateStatistics(child);
                totalFileCount += child.getTotalFileCount() != null ? child.getTotalFileCount() : 0;
            }
        }

        node.setFileCount(fileCount);
        node.setFolderCount(folderCount);
        node.setTotalFileCount(totalFileCount);
    }

    /**
     * 构建文件的层级路径
     * @param file 文件对象
     * @return 层级路径字符串，格式：工作空间名称/文件夹1/文件夹2
     */
    private String buildLocationPath(CoFileVo file) {
        if (file == null) {
            return "";
        }

        List<String> pathParts = new ArrayList<>();

        try {
            // 1. 获取工作空间名称
            if (file.getWorkspaceId() != null) {
                CoWorkspace workspace = workspaceMapper.selectById(file.getWorkspaceId());
                if (workspace != null && workspace.getWorkspaceName() != null) {
                    pathParts.add(workspace.getWorkspaceName());
                }
            }

            // 2. 递归获取文件夹路径
            if (file.getFolderId() != null && file.getFolderId() > 0) {
                List<String> folderPath = buildFolderPath(file.getFolderId());
                pathParts.addAll(folderPath);
            }

            // 3. 组合路径
            if (pathParts.isEmpty()) {
                return "根目录";
            }
            return String.join("/", pathParts);

        } catch (Exception e) {
            log.error("构建文件路径失败: fileId={}, workspaceId={}, folderId={}",
                    file.getId(), file.getWorkspaceId(), file.getFolderId(), e);
            return "未知位置";
        }
    }

    /**
     * 递归构建文件夹路径
     * @param folderId 文件夹ID
     * @return 文件夹路径列表（从根到当前文件夹）
     */
    private List<String> buildFolderPath(Long folderId) {
        List<String> path = new ArrayList<>();

        if (folderId == null || folderId <= 0) {
            return path;
        }

        try {
            CoFolder folder = folderMapper.selectById(folderId);
            if (folder == null) {
                return path;
            }

            // 递归获取父文件夹路径
            if (folder.getParentId() != null && folder.getParentId() > 0) {
                path.addAll(buildFolderPath(folder.getParentId()));
            }

            // 添加当前文件夹名称
            if (folder.getFolderName() != null) {
                path.add(folder.getFolderName());
            }

        } catch (Exception e) {
            log.error("构建文件夹路径失败: folderId={}", folderId, e);
        }

        return path;
    }

    /**
     * 验证用户对工作空间的访问权限
     * 用户可以访问工作空间，当且仅当满足以下任一条件：
     * 1. 用户是该工作空间的所有者
     * 2. 用户是该工作空间的成员
     * 3. 工作空间是部门空间，且用户与该工作空间属于同一部门
     *
     * @param workspaceId 工作空间ID
     * @param userId 用户ID
     * @throws ServiceException 如果用户没有访问权限
     */
    private void validateWorkspaceAccess(Long workspaceId, Long userId) {
        if (workspaceId == null || userId == null) {
            log.error("工作空间ID或用户ID为空: workspaceId={}, userId={}", workspaceId, userId);
            throw new ServiceException("参数错误：工作空间ID和用户ID不能为空");
        }

        try {
            // 1. 查询工作空间信息
            CoWorkspace workspace = workspaceMapper.selectById(workspaceId);
            if (workspace == null) {
                log.warn("工作空间不存在: workspaceId={}", workspaceId);
                throw new ServiceException("工作空间不存在");
            }

            // 2. 检查是否是工作空间所有者
            if (ObjectUtil.equal(workspace.getOwnerId(), userId)) {
                log.debug("用户 {} 是工作空间 {} 的所有者，权限验证通过", userId, workspaceId);
                return;
            }

            // 3. 检查是否是工作空间成员
            Boolean isMember = workspaceMemberService.isMember(workspaceId, userId);
            if (Boolean.TRUE.equals(isMember)) {
                log.debug("用户 {} 是工作空间 {} 的成员，权限验证通过", userId, workspaceId);
                return;
            }

            // 4. 检查是否是部门空间且用户同部门
            if (workspace.getWorkspaceType() != null && workspace.getWorkspaceType() == 2
                && workspace.getDeptId() != null) {
                try {
                    Long userDeptId = com.xmzs.common.satoken.utils.LoginHelper.getDeptId();
                    if (ObjectUtil.equal(workspace.getDeptId(), userDeptId)) {
                        log.info("用户 {} 与部门工作空间 {} 属于同一部门，权限验证通过", userId, workspaceId);
                        return;
                    }
                } catch (Exception e) {
                    log.warn("获取用户部门ID失败，无法验证部门空间权限", e);
                }
            }

            // 5. 以上条件都不满足，拒绝访问
            log.warn("用户 {} 无权访问工作空间 {}: 既不是所有者也不是成员，也不是同部门部门空间", userId, workspaceId);
            throw new ServiceException("您没有权限访问此工作空间");

        } catch (ServiceException e) {
            // 重新抛出业务异常
            throw e;
        } catch (Exception e) {
            log.error("验证工作空间访问权限时发生错误: workspaceId={}, userId={}", workspaceId, userId, e);
            throw new ServiceException("权限验证失败：" + e.getMessage());
        }
    }

    /**
     * 获取文件树响应数据
     * 当 workspaceId 为 null 时，返回用户有权限访问的所有工作空间及其一级文件树（支持分页和筛选）
     * 当 workspaceId 有值时，返回该工作空间的文件树
     *
     * @param workspaceId 工作空间ID，为null时返回所有工作空间
     * @param userId 用户ID
     * @param parentId 父文件夹ID，为null时返回根级
     * @param workspaceType 工作空间类型: 1-个人空间 2-部门空间 3-团队空间, null或0时忽略此条件
     * @param ownershipType 所有权类型: 1-我创建的 2-我加入的, null或0时返回全部
     * @param pageNum 页码，默认1，仅当workspaceId为null时生效
     * @param pageSize 每页大小，默认10，仅当workspaceId为null时生效
     * @return 文件树响应数据
     */
    @Override
    public FileTreeResponseVo getFileTreeResponse(Long workspaceId, Long userId, Long parentId,
                                                   Integer workspaceType, Integer ownershipType,
                                                   Integer pageNum, Integer pageSize) {
        log.info("获取文件树响应数据: workspaceId={}, userId={}, parentId={}, workspaceType={}, ownershipType={}, pageNum={}, pageSize={}",
                 workspaceId, userId, parentId, workspaceType, ownershipType, pageNum, pageSize);

        if (workspaceId == null) {
            // 返回用户有权限访问的所有工作空间及其一级文件树（支持分页和筛选）
            return getAllWorkspacesFileTree(userId, workspaceType, ownershipType, pageNum, pageSize);
        } else {
            // 返回指定工作空间的文件树
            List<FileTreeNodeVo> fileTree = getFileTree(workspaceId, userId, parentId);
            return FileTreeResponseVo.ofFileTree(fileTree);
        }
    }

    /**
     * 获取用户有权限访问的所有工作空间及其一级文件树（支持分页和筛选）
     *
     * @param userId 用户ID
     * @param workspaceType 工作空间类型筛选
     * @param ownershipType 所有权类型筛选
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 文件树响应数据
     */
    private FileTreeResponseVo getAllWorkspacesFileTree(Long userId, Integer workspaceType,
                                                        Integer ownershipType, Integer pageNum, Integer pageSize) {
        log.info("获取用户 {} 有权限访问的所有工作空间及其一级文件树（分页）: workspaceType={}, ownershipType={}, pageNum={}, pageSize={}",
                 userId, workspaceType, ownershipType, pageNum, pageSize);

        // 1. 查询用户有权限访问的所有工作空间（带筛选）
        List<CoWorkspaceVo> workspaces = getUserAccessibleWorkspaces(userId, workspaceType, ownershipType);

        if (workspaces.isEmpty()) {
            log.info("用户 {} 没有可访问的工作空间", userId);
            return FileTreeResponseVo.ofPage(new ArrayList<>(), new ArrayList<>(), 0L, pageNum, pageSize);
        }

        // 2. 提取所有工作空间ID
        List<Long> workspaceIds = workspaces.stream()
            .map(CoWorkspaceVo::getId)
            .collect(Collectors.toList());

        log.info("用户 {} 可访问的工作空间ID列表: {}", userId, workspaceIds);

        // 3. 查询所有工作空间下的一级文件夹和文件（合并后分页）
        List<FileTreeNodeVo> allFileTree = new ArrayList<>();

        // 3.1 查询所有工作空间下的一级文件夹
        LambdaQueryWrapper<CoFolder> folderWrapper = Wrappers.lambdaQuery(CoFolder.class)
            .in(CoFolder::getWorkspaceId, workspaceIds)
            .eq(CoFolder::getStatus, 1)  // 1-正常状态
            .and(w -> w.isNull(CoFolder::getParentId).or().eq(CoFolder::getParentId, 0))
            .orderByDesc(CoFolder::getCreateTime);

        List<CoFolderVo> folders = folderMapper.selectVoList(folderWrapper);

        // 3.2 查询所有工作空间下的一级文件
        LambdaQueryWrapper<CoFile> fileWrapper = Wrappers.lambdaQuery(CoFile.class)
            .in(CoFile::getWorkspaceId, workspaceIds)
            .eq(CoFile::getStatus, 1)  // 1-正常状态
            .and(w -> w.isNull(CoFile::getFolderId).or().eq(CoFile::getFolderId, 0))
            .orderByDesc(CoFile::getCreateTime);

        List<CoFileVo> files = fileMapper.selectVoList(fileWrapper);

        // 3.3 批量查询文件所有者的昵称
        if (!files.isEmpty()) {
            enrichFilesWithOwnerNickName(files);
        }

        // 4. 创建工作空间ID到名称的映射
        Map<Long, String> workspaceNameMap = workspaces.stream()
            .collect(Collectors.toMap(CoWorkspaceVo::getId, CoWorkspaceVo::getWorkspaceName));

        // 5. 创建文件夹节点并设置工作空间信息
        for (CoFolderVo folder : folders) {
            FileTreeNodeVo node = createFolderNode(folder, userId);
            setFolderStatistics(node, folder.getId());

            // 设置工作空间信息
            node.setWorkspaceId(folder.getWorkspaceId());
            node.setWorkspaceName(workspaceNameMap.get(folder.getWorkspaceId()));

            allFileTree.add(node);
        }

        // 6. 创建文件节点并设置工作空间信息
        for (CoFileVo file : files) {
            FileTreeNodeVo node = createFileNode(file, userId);

            // 设置工作空间信息
            node.setWorkspaceId(file.getWorkspaceId());
            node.setWorkspaceName(workspaceNameMap.get(file.getWorkspaceId()));

            allFileTree.add(node);
        }

        // 7. 手动分页
        long total = allFileTree.size();
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allFileTree.size());

        List<FileTreeNodeVo> pagedFileTree;
        if (startIndex >= allFileTree.size()) {
            pagedFileTree = new ArrayList<>();
        } else {
            pagedFileTree = allFileTree.subList(startIndex, endIndex);
        }

        log.info("用户 {} 的文件树包含 {} 个工作空间, 总共 {} 个节点, 当前页 {} 个节点",
                 userId, workspaces.size(), total, pagedFileTree.size());

        return FileTreeResponseVo.ofPage(workspaces, pagedFileTree, total, pageNum, pageSize);
    }

    /**
     * 查询用户有权限访问的所有工作空间（支持筛选）
     * 包括：
     * 1. 用户作为所有者的工作空间
     * 2. 用户作为成员的工作空间
     * 3. 同部门的部门工作空间（workspaceType=2）
     *
     * @param userId 用户ID
     * @param workspaceType 工作空间类型筛选: 1-个人空间 2-部门空间 3-团队空间, null或0时忽略此条件
     * @param ownershipType 所有权类型筛选: 1-我创建的 2-我加入的, null或0时返回全部
     * @return 工作空间列表
     */
    private List<CoWorkspaceVo> getUserAccessibleWorkspaces(Long userId, Integer workspaceType, Integer ownershipType) {
        log.debug("查询用户 {} 有权限访问的所有工作空间: workspaceType={}, ownershipType={}", userId, workspaceType, ownershipType);

        List<CoWorkspaceVo> ownedWorkspaces = new ArrayList<>();
        List<CoWorkspaceVo> memberWorkspaces = new ArrayList<>();
        List<CoWorkspaceVo> deptWorkspaces = new ArrayList<>();

        // 1. 根据 ownershipType 决定查询哪些工作空间
        boolean queryOwned = (ownershipType == null || ownershipType == 0 || ownershipType == 1);
        boolean queryMember = (ownershipType == null || ownershipType == 0 || ownershipType == 2);
        // 部门空间总是查询（如果用户有部门的话）
        boolean queryDept = true;

        // 获取用户的部门ID
        Long userDeptId = null;
        try {
            userDeptId = com.xmzs.common.satoken.utils.LoginHelper.getDeptId();
            log.info("用户 {} 的部门ID: {}", userId, userDeptId);
        } catch (Exception e) {
            log.warn("获取用户部门ID失败", e);
        }

        // 2. 查询用户作为所有者的工作空间
        if (queryOwned) {
            LambdaQueryWrapper<CoWorkspace> ownerWrapper = Wrappers.lambdaQuery(CoWorkspace.class)
                .eq(CoWorkspace::getOwnerId, userId)
                .eq(CoWorkspace::getStatus, WorkspaceConstants.WorkspaceStatus.NORMAL);

            // 添加工作空间类型筛选
            if (workspaceType != null && workspaceType > 0) {
                ownerWrapper.eq(CoWorkspace::getWorkspaceType, workspaceType);
            }

            ownerWrapper.orderByDesc(CoWorkspace::getIsDefault)
                .orderByAsc(CoWorkspace::getSortOrder)
                .orderByDesc(CoWorkspace::getCreateTime);

            ownedWorkspaces = workspaceMapper.selectVoList(ownerWrapper);
            // 设置 ownershipType 字段
            ownedWorkspaces.forEach(ws -> ws.setOwnershipType(1)); // 1-我创建的
            log.debug("用户 {} 作为所有者的工作空间数量: {}", userId, ownedWorkspaces.size());
        }

        // 3. 查询用户作为成员的工作空间
        if (queryMember) {
            // 3.1 查询用户作为成员的工作空间ID列表
            LambdaQueryWrapper<CoWorkspaceMember> memberWrapper = Wrappers.lambdaQuery(CoWorkspaceMember.class)
                .eq(CoWorkspaceMember::getUserId, userId)
                .eq(CoWorkspaceMember::getStatus, WorkspaceConstants.MemberStatus.NORMAL);

            List<CoWorkspaceMember> memberships = workspaceMemberMapper.selectList(memberWrapper);

            if (!memberships.isEmpty()) {
                List<Long> memberWorkspaceIds = memberships.stream()
                    .map(CoWorkspaceMember::getWorkspaceId)
                    .collect(Collectors.toList());

                log.debug("用户 {} 作为成员的工作空间ID列表: {}", userId, memberWorkspaceIds);

                // 3.2 查询用户作为成员的工作空间详情
                LambdaQueryWrapper<CoWorkspace> memberWorkspaceWrapper = Wrappers.lambdaQuery(CoWorkspace.class)
                    .in(CoWorkspace::getId, memberWorkspaceIds)
                    .eq(CoWorkspace::getStatus, WorkspaceConstants.WorkspaceStatus.NORMAL);

                // 添加工作空间类型筛选
                if (workspaceType != null && workspaceType > 0) {
                    memberWorkspaceWrapper.eq(CoWorkspace::getWorkspaceType, workspaceType);
                }

                memberWorkspaceWrapper.orderByDesc(CoWorkspace::getIsDefault)
                    .orderByAsc(CoWorkspace::getSortOrder)
                    .orderByDesc(CoWorkspace::getCreateTime);

                memberWorkspaces = workspaceMapper.selectVoList(memberWorkspaceWrapper);
                // 设置 ownershipType 字段
                memberWorkspaces.forEach(ws -> ws.setOwnershipType(2)); // 2-我加入的
                log.debug("用户 {} 作为成员的工作空间数量: {}", userId, memberWorkspaces.size());
            } else {
                log.debug("用户 {} 不是任何工作空间的成员", userId);
            }
        }

        // 4. 查询同部门的部门工作空间
        if (queryDept && userDeptId != null) {
            LambdaQueryWrapper<CoWorkspace> deptWrapper = Wrappers.lambdaQuery(CoWorkspace.class)
                .eq(CoWorkspace::getWorkspaceType, 2)  // 2-部门空间
                .eq(CoWorkspace::getDeptId, userDeptId)
                .eq(CoWorkspace::getStatus, WorkspaceConstants.WorkspaceStatus.NORMAL);

            // 添加工作空间类型筛选
            if (workspaceType != null && workspaceType > 0) {
                deptWrapper.eq(CoWorkspace::getWorkspaceType, workspaceType);
            }

            deptWrapper.orderByDesc(CoWorkspace::getIsDefault)
                .orderByAsc(CoWorkspace::getSortOrder)
                .orderByDesc(CoWorkspace::getCreateTime);

            deptWorkspaces = workspaceMapper.selectVoList(deptWrapper);
            // 设置 ownershipType 字段为部门空间
            deptWorkspaces.forEach(ws -> ws.setOwnershipType(3)); // 3-部门空间
            log.info("用户 {} 的同部门部门工作空间数量: {}", userId, deptWorkspaces.size());
        }

        // 5. 合并并去重（使用 LinkedHashSet 保持顺序）
        Set<Long> workspaceIdSet = new LinkedHashSet<>();
        List<CoWorkspaceVo> allWorkspaces = new ArrayList<>();

        // 先添加所有者的工作空间（优先级更高）
        for (CoWorkspaceVo workspace : ownedWorkspaces) {
            if (workspaceIdSet.add(workspace.getId())) {
                allWorkspaces.add(workspace);
            }
        }

        // 再添加成员的工作空间
        for (CoWorkspaceVo workspace : memberWorkspaces) {
            if (workspaceIdSet.add(workspace.getId())) {
                allWorkspaces.add(workspace);
            }
        }

        // 最后添加部门空间
        for (CoWorkspaceVo workspace : deptWorkspaces) {
            if (workspaceIdSet.add(workspace.getId())) {
                allWorkspaces.add(workspace);
            }
        }

        log.info("用户 {} 总共可访问 {} 个工作空间（包含部门空间）", userId, allWorkspaces.size());

        // 6. 为所有工作空间填充统计信息（memberCount 和 contentCount）
        if (!allWorkspaces.isEmpty()) {
            fillWorkspaceStatistics(allWorkspaces);
        }

        return allWorkspaces;
    }

    /**
     * 为工作空间列表填充统计信息（成员数、内容数、所有者昵称）
     * 使用批量查询避免N+1问题
     *
     * @param workspaces 工作空间列表
     */
    private void fillWorkspaceStatistics(List<CoWorkspaceVo> workspaces) {
        if (workspaces == null || workspaces.isEmpty()) {
            return;
        }

        // 1. 收集所有工作空间ID和所有者ID
        List<Long> workspaceIds = workspaces.stream()
            .map(CoWorkspaceVo::getId)
            .collect(Collectors.toList());

        List<Long> ownerIds = workspaces.stream()
            .map(CoWorkspaceVo::getOwnerId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

        log.debug("开始为 {} 个工作空间填充统计信息", workspaceIds.size());

        // 2. 批量统计成员数（包括所有者，所以需要+1）
        Map<Long, Long> memberCountMap = new HashMap<>();
        for (Long workspaceId : workspaceIds) {
            Long count = workspaceMemberMapper.selectCount(
                Wrappers.lambdaQuery(CoWorkspaceMember.class)
                    .eq(CoWorkspaceMember::getWorkspaceId, workspaceId)
                    .eq(CoWorkspaceMember::getStatus, WorkspaceConstants.MemberStatus.NORMAL)
            );
            // 成员数 = 普通成员数 + 1（所有者）
            memberCountMap.put(workspaceId, count + 1);
        }

        // 3. 批量统计文件夹数（所有层级）
        Map<Long, Long> folderCountMap = new HashMap<>();
        for (Long workspaceId : workspaceIds) {
            Long count = folderMapper.selectCount(
                Wrappers.lambdaQuery(CoFolder.class)
                    .eq(CoFolder::getWorkspaceId, workspaceId)
                    .eq(CoFolder::getStatus, WorkspaceConstants.FolderStatus.NORMAL)
            );
            folderCountMap.put(workspaceId, count);
        }

        // 4. 批量统计文件数（所有层级）
        Map<Long, Long> fileCountMap = new HashMap<>();
        for (Long workspaceId : workspaceIds) {
            Long count = fileMapper.selectCount(
                Wrappers.lambdaQuery(CoFile.class)
                    .eq(CoFile::getWorkspaceId, workspaceId)
                    .eq(CoFile::getStatus, WorkspaceConstants.FileStatus.NORMAL)
            );
            fileCountMap.put(workspaceId, count);
        }

        // 5. 批量查询所有者昵称
        // 注意：直接使用 selectList 避免跨模块 MapStruct 转换问题
        Map<Long, String> ownerNickNameMap = new HashMap<>();
        if (!ownerIds.isEmpty()) {
            List<com.xmzs.system.domain.SysUser> users = sysUserMapper.selectList(
                Wrappers.lambdaQuery(com.xmzs.system.domain.SysUser.class)
                    .in(com.xmzs.system.domain.SysUser::getUserId, ownerIds)
                    .select(com.xmzs.system.domain.SysUser::getUserId, com.xmzs.system.domain.SysUser::getNickName)
            );
            for (com.xmzs.system.domain.SysUser user : users) {
                ownerNickNameMap.put(user.getUserId(), user.getNickName());
            }
            log.debug("查询到 {} 个所有者的昵称信息", ownerNickNameMap.size());
        }

        // 6. 填充统计信息到工作空间对象
        for (CoWorkspaceVo workspace : workspaces) {
            Long workspaceId = workspace.getId();
            Long ownerId = workspace.getOwnerId();

            // 设置成员数
            Long memberCount = memberCountMap.getOrDefault(workspaceId, 1L); // 至少有1个所有者
            workspace.setMemberCount(memberCount.intValue());

            // 设置内容数（文件夹数 + 文件数）
            Long folderCount = folderCountMap.getOrDefault(workspaceId, 0L);
            Long fileCount = fileCountMap.getOrDefault(workspaceId, 0L);
            Long contentCount = folderCount + fileCount;
            workspace.setContentCount(contentCount.intValue());

            // 设置所有者昵称
            if (ownerId != null) {
                String ownerNickName = ownerNickNameMap.get(ownerId);
                workspace.setOwnerNickName(ownerNickName);
            }

            log.debug("工作空间 {} 统计信息: 成员数={}, 文件夹数={}, 文件数={}, 内容总数={}, 所有者昵称={}",
                     workspaceId, memberCount, folderCount, fileCount, contentCount, workspace.getOwnerNickName());
        }

        log.debug("完成为 {} 个工作空间填充统计信息", workspaces.size());
    }

    /**
     * 批量为文件列表填充所有者昵称
     * 使用批量查询避免N+1问题
     *
     * @param files 文件列表
     */
    private void enrichFilesWithOwnerNickName(List<CoFileVo> files) {
        if (files == null || files.isEmpty()) {
            return;
        }

        // 1. 收集所有文件的所有者ID
        List<Long> ownerIds = files.stream()
            .map(CoFileVo::getOwnerId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

        if (ownerIds.isEmpty()) {
            return;
        }

        log.debug("开始为 {} 个文件批量查询所有者昵称，涉及 {} 个所有者", files.size(), ownerIds.size());

        // 2. 批量查询所有者昵称
        // 注意：直接使用 selectList 避免跨模块 MapStruct 转换问题
        Map<Long, String> ownerNickNameMap = new HashMap<>();
        try {
            List<com.xmzs.system.domain.SysUser> users = sysUserMapper.selectList(
                Wrappers.lambdaQuery(com.xmzs.system.domain.SysUser.class)
                    .in(com.xmzs.system.domain.SysUser::getUserId, ownerIds)
                    .select(com.xmzs.system.domain.SysUser::getUserId, com.xmzs.system.domain.SysUser::getNickName)
            );
            for (com.xmzs.system.domain.SysUser user : users) {
                ownerNickNameMap.put(user.getUserId(), user.getNickName());
            }
            log.debug("查询到 {} 个所有者的昵称信息", ownerNickNameMap.size());
        } catch (Exception e) {
            log.error("批量查询文件所有者昵称失败", e);
            return;
        }

        // 3. 填充所有者昵称到文件对象
        for (CoFileVo file : files) {
            if (file.getOwnerId() != null) {
                String ownerNickName = ownerNickNameMap.get(file.getOwnerId());
                file.setOwnerNickName(ownerNickName);
            }
        }

        log.debug("完成为 {} 个文件填充所有者昵称", files.size());
    }

}

