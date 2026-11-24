package com.xmzs.coedit.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xmzs.coedit.domain.CoEditHistory;
import com.xmzs.coedit.domain.CoEditLog;
import com.xmzs.coedit.service.ICoEditHistoryService;
import com.xmzs.coedit.service.ICoEditLockService;
import com.xmzs.coedit.service.ICoEditLogService;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.web.core.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 编辑冲突处理控制器
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coedit/conflict")
@Tag(name = "编辑冲突处理", description = "编辑冲突处理接口")
public class CoEditConflictController extends BaseController {

    private final ICoEditLockService lockService;
    private final ICoEditLogService logService;
    private final ICoEditHistoryService historyService;

    /**
     * 获取文档锁
     */
    // @SaCheckPermission("coedit:conflict:lock")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "获取文档锁", businessType = BusinessType.INSERT)
    @PostMapping("/lock/acquire")
    @Operation(summary = "获取文档锁")
    public R<Map<String, Object>> acquireLock(
            @Parameter(description = "文件ID", required = true) @RequestParam Long fileId,
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "用户名称", required = true) @RequestParam String userName,
            @Parameter(description = "锁类型：0-读锁 1-写锁") @RequestParam(defaultValue = "1") Integer lockType,
            @Parameter(description = "过期时间（分钟）") @RequestParam(defaultValue = "30") Integer expireMinutes) {

        String lockKey = lockService.acquireLock(fileId, userId, userName, lockType, expireMinutes);

        Map<String, Object> result = new HashMap<>();
        if (lockKey != null) {
            result.put("success", true);
            result.put("lockKey", lockKey);
            result.put("message", "成功获取锁");

            // 记录加锁日志
            logService.logLock(fileId, userId, userName, lockKey);
        } else {
            result.put("success", false);
            result.put("message", "文件已被其他用户锁定");
            String holder = lockService.getLockHolder(fileId);
            result.put("holder", holder);
        }

        return R.ok(result);
    }

    /**
     * 释放锁
     */
    // @SaCheckPermission("coedit:conflict:lock")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "释放锁", businessType = BusinessType.UPDATE)
    @PutMapping("/lock/release/{lockKey}")
    @Operation(summary = "释放锁")
    public R<Void> releaseLock(
            @Parameter(description = "锁Key", required = true) @PathVariable String lockKey,
            @Parameter(description = "文件ID") @RequestParam(required = false) Long fileId,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "用户名称") @RequestParam(required = false) String userName) {

        Boolean success = lockService.releaseLock(lockKey);

        if (success && fileId != null && userId != null && userName != null) {
            // 记录解锁日志
            logService.logUnlock(fileId, userId, userName, lockKey);
        }

        return success ? R.ok() : R.fail("释放锁失败");
    }

    /**
     * 检查文件是否被锁定
     */
    // @SaCheckPermission("coedit:conflict:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/lock/check/{fileId}")
    @Operation(summary = "检查文件是否被锁定")
    public R<Map<String, Object>> checkLock(
            @Parameter(description = "文件ID", required = true) @PathVariable Long fileId) {

        Boolean isLocked = lockService.isLocked(fileId);
        String holder = lockService.getLockHolder(fileId);

        Map<String, Object> result = new HashMap<>();
        result.put("isLocked", isLocked);
        result.put("holder", holder);

        return R.ok(result);
    }

    /**
     * 续期锁
     */
    // @SaCheckPermission("coedit:conflict:lock")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "续期锁", businessType = BusinessType.UPDATE)
    @PutMapping("/lock/renew/{lockKey}")
    @Operation(summary = "续期锁")
    public R<Void> renewLock(
            @Parameter(description = "锁Key", required = true) @PathVariable String lockKey,
            @Parameter(description = "延长时间（分钟）") @RequestParam(defaultValue = "30") Integer extendMinutes) {

        Boolean success = lockService.renewLock(lockKey, extendMinutes);
        return success ? R.ok() : R.fail("续期锁失败");
    }

    /**
     * 强制释放锁（管理员功能）
     */
    // @SaCheckPermission("coedit:conflict:admin")  // 协同编辑模块基础功能,允许所有登录用户访问
    @Log(title = "强制释放锁", businessType = BusinessType.UPDATE)
    @PutMapping("/lock/force-release/{fileId}")
    @Operation(summary = "强制释放锁")
    public R<Void> forceReleaseLock(
            @Parameter(description = "文件ID", required = true) @PathVariable Long fileId) {

        Boolean success = lockService.forceReleaseLock(fileId);
        return success ? R.ok() : R.fail("强制释放锁失败");
    }

    /**
     * 查询文件的编辑日志
     */
    // @SaCheckPermission("coedit:conflict:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/log/file/{fileId}")
    @Operation(summary = "查询文件的编辑日志")
    public TableDataInfo<CoEditLog> queryFileLog(
            @Parameter(description = "文件ID", required = true) @PathVariable Long fileId,
            PageQuery pageQuery) {

        return logService.queryFileLog(fileId, pageQuery);
    }

    /**
     * 查询用户的编辑日志
     */
    // @SaCheckPermission("coedit:conflict:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/log/user/{userId}")
    @Operation(summary = "查询用户的编辑日志")
    public TableDataInfo<CoEditLog> queryUserLog(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            PageQuery pageQuery) {

        return logService.queryUserLog(userId, pageQuery);
    }

    /**
     * 查询会话的编辑日志
     */
    // @SaCheckPermission("coedit:conflict:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/log/session/{sessionKey}")
    @Operation(summary = "查询会话的编辑日志")
    public R<List<CoEditLog>> querySessionLog(
            @Parameter(description = "会话Key", required = true) @PathVariable String sessionKey) {

        List<CoEditLog> logs = logService.querySessionLog(sessionKey);
        return R.ok(logs);
    }

    /**
     * 查询文件的编辑历史
     */
    // @SaCheckPermission("coedit:conflict:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/history/file/{fileId}")
    @Operation(summary = "查询文件的编辑历史")
    public TableDataInfo<CoEditHistory> queryFileHistory(
            @Parameter(description = "文件ID", required = true) @PathVariable Long fileId,
            PageQuery pageQuery) {

        return historyService.queryFileHistory(fileId, pageQuery);
    }

    /**
     * 查询用户的编辑历史
     */
    // @SaCheckPermission("coedit:conflict:query")  // 协同编辑模块基础功能,允许所有登录用户访问
    @GetMapping("/history/user/{userId}")
    @Operation(summary = "查询用户的编辑历史")
    public TableDataInfo<CoEditHistory> queryUserHistory(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            PageQuery pageQuery) {

        return historyService.queryUserHistory(userId, pageQuery);
    }

}

