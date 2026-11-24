package com.xmzs.system.controller.system;

import java.util.List;

import com.xmzs.common.excel.utils.ExcelUtil;
import com.xmzs.common.idempotent.annotation.RepeatSubmit;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.xmzs.common.log.annotation.Log;
import com.xmzs.common.web.core.BaseController;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.core.domain.R;
import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.log.enums.BusinessType;
import com.xmzs.system.domain.vo.SysPackagePlanVo;
import com.xmzs.system.domain.bo.SysPackagePlanBo;
import com.xmzs.system.service.ISysPackagePlanService;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

/**
 * 套餐管理
 *
 * @author zhuzi
 * @date 2024-06-09
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/packagePlan")
public class SysPackagePlanController extends BaseController {

    private final ISysPackagePlanService sysPackagePlanService;

    /**
     * 查询套餐管理列表
     */
    @SaCheckPermission("system:packagePlan:list")
    @GetMapping("/list")
    public TableDataInfo<SysPackagePlanVo> list(SysPackagePlanBo bo, PageQuery pageQuery) {
        return sysPackagePlanService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出套餐管理列表
     */
    @SaCheckPermission("system:packagePlan:export")
    @Log(title = "套餐管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysPackagePlanBo bo, HttpServletResponse response) {
        List<SysPackagePlanVo> list = sysPackagePlanService.queryList(bo);
        ExcelUtil.exportExcel(list, "套餐管理", SysPackagePlanVo.class, response);
    }

    /**
     * 获取套餐管理详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:packagePlan:query")
    @GetMapping("/{id}")
    public R<SysPackagePlanVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(sysPackagePlanService.queryById(id));
    }

    /**
     * 新增套餐管理
     */
    @SaCheckPermission("system:packagePlan:add")
    @Log(title = "套餐管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysPackagePlanBo bo) {
        return toAjax(sysPackagePlanService.insertByBo(bo));
    }

    /**
     * 修改套餐管理
     */
    @SaCheckPermission("system:packagePlan:edit")
    @Log(title = "套餐管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysPackagePlanBo bo) {
        return toAjax(sysPackagePlanService.updateByBo(bo));
    }

    /**
     * 删除套餐管理
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:packagePlan:remove")
    @Log(title = "套餐管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(sysPackagePlanService.deleteWithValidByIds(List.of(ids), true));
    }
}
