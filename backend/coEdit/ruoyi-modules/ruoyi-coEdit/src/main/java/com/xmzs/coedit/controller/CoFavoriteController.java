package com.xmzs.coedit.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xmzs.coedit.domain.bo.CoFavoriteBo;
import com.xmzs.coedit.domain.vo.CoFavoriteVo;
import com.xmzs.coedit.service.ICoFavoriteService;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.common.satoken.utils.LoginHelper;
import com.xmzs.common.web.core.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏管理
 *
 * @author zuleed
 * @date 2025-11-08
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coedit/favorite")
@Tag(name = "收藏管理", description = "收藏管理接口")
public class CoFavoriteController extends BaseController {

    private final ICoFavoriteService favoriteService;

    /**
     * 查询收藏列表
     */
    @Operation(summary = "查询收藏列表")
    @GetMapping("/list")
    public TableDataInfo<CoFavoriteVo> list(CoFavoriteBo bo, PageQuery pageQuery) {
        // 设置当前用户ID
        bo.setUserId(LoginHelper.getUserId());
        return favoriteService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取收藏详细信息
     */
    @Operation(summary = "获取收藏详细信息")
    @GetMapping("/{id}")
    public R<CoFavoriteVo> getInfo(@Parameter(description = "收藏ID") @PathVariable Long id) {
        return R.ok(favoriteService.queryById(id));
    }

    /**
     * 添加收藏
     */
    @Operation(summary = "添加收藏")
    @Log(title = "收藏管理", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody CoFavoriteBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        return toAjax(favoriteService.addFavorite(bo));
    }

    /**
     * 修改收藏
     */
    @Operation(summary = "修改收藏")
    @Log(title = "收藏管理", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody CoFavoriteBo bo) {
        return toAjax(favoriteService.updateFavorite(bo));
    }

    /**
     * 删除收藏
     */
    @Operation(summary = "删除收藏")
    @Log(title = "收藏管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@Parameter(description = "收藏ID") @PathVariable Long id) {
        return toAjax(favoriteService.removeFavorite(id));
    }

    /**
     * 批量删除收藏
     */
    @Operation(summary = "批量删除收藏")
    @Log(title = "收藏管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public R<Void> removeBatch(@RequestBody List<Long> ids) {
        return toAjax(favoriteService.batchRemoveFavorite(ids));
    }

    /**
     * 检查是否已收藏
     */
    @Operation(summary = "检查是否已收藏")
    @GetMapping("/check")
    public R<Boolean> checkFavorite(
        @Parameter(description = "目标类型:1-工作空间 2-文件夹 3-文件") @RequestParam Integer targetType,
        @Parameter(description = "目标ID") @RequestParam Long targetId) {

        Long userId = LoginHelper.getUserId();
        Boolean isFavorited = favoriteService.isFavorited(userId, targetType, targetId);
        return R.ok(isFavorited);
    }

    /**
     * 根据目标获取收藏信息
     */
    @Operation(summary = "根据目标获取收藏信息")
    @GetMapping("/target")
    public R<CoFavoriteVo> getFavoriteByTarget(
        @Parameter(description = "目标类型:1-工作空间 2-文件夹 3-文件") @RequestParam Integer targetType,
        @Parameter(description = "目标ID") @RequestParam Long targetId) {

        Long userId = LoginHelper.getUserId();
        CoFavoriteVo vo = favoriteService.getFavoriteByTarget(userId, targetType, targetId);
        return R.ok(vo);
    }

    /**
     * 批量添加收藏
     */
    @Operation(summary = "批量添加收藏")
    @Log(title = "收藏管理", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public R<Void> addBatch(@Validated(AddGroup.class) @RequestBody List<CoFavoriteBo> boList) {
        return toAjax(favoriteService.batchAddFavorite(boList));
    }

    /**
     * 检查并更新收藏有效性
     */
    @Operation(summary = "检查并更新收藏有效性")
    @PostMapping("/check-validity/{id}")
    public R<Boolean> checkValidity(@Parameter(description = "收藏ID") @PathVariable Long id) {
        Boolean isValid = favoriteService.checkAndUpdateValidity(id);
        return R.ok(isValid);
    }

    /**
     * 批量检查用户的所有收藏有效性
     */
    @Operation(summary = "批量检查用户的所有收藏有效性")
    @PostMapping("/check-validity/batch")
    public R<Integer> batchCheckValidity() {
        Long userId = LoginHelper.getUserId();
        Integer invalidCount = favoriteService.batchCheckValidity(userId);
        return R.ok(invalidCount);
    }

    /**
     * 获取失效的收藏列表
     */
    @Operation(summary = "获取失效的收藏列表")
    @GetMapping("/invalid")
    public R<List<CoFavoriteVo>> getInvalidFavorites() {
        Long userId = LoginHelper.getUserId();
        List<CoFavoriteVo> list = favoriteService.getInvalidFavorites(userId);
        return R.ok(list);
    }

    /**
     * 根据文件ID查询是否被收藏
     */
    @Operation(summary = "根据文件ID查询是否被收藏")
    @GetMapping("/file/{fileId}")
    public R<Boolean> isFileFavorited(@Parameter(description = "文件ID") @PathVariable Long fileId) {
        Boolean isFavorited = favoriteService.isFileFavorited(fileId);
        return R.ok(isFavorited);
    }

}

