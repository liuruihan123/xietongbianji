package com.xmzs.system.controller;

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
import com.xmzs.system.domain.vo.PkulawApyVo;
import com.xmzs.system.domain.bo.PkulawApyBo;
import com.xmzs.system.service.IPkulawApyService;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

/**
 * 案例库
 *
 * @author zuleed
 * @date 2025-05-10
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/apy")
public class PkulawApyController extends BaseController {

    private final IPkulawApyService pkulawApyService;

    /**
     * 查询案例库列表
     */
    @SaCheckPermission("system:apy:list")
    @GetMapping("/list")
    public TableDataInfo<PkulawApyVo> list(PkulawApyBo bo, PageQuery pageQuery) {
        return pkulawApyService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出案例库列表
     */
    @SaCheckPermission("system:apy:export")
    @Log(title = "案例库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PkulawApyBo bo, HttpServletResponse response) {
        List<PkulawApyVo> list = pkulawApyService.queryList(bo);
        ExcelUtil.exportExcel(list, "案例库", PkulawApyVo.class, response);
    }

    /**
     * 获取案例库详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:apy:query")
    @GetMapping("/{id}")
    public R<PkulawApyVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(pkulawApyService.queryById(id));
    }

    /**
     * 新增案例库
     */
    @SaCheckPermission("system:apy:add")
    @Log(title = "案例库", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PkulawApyBo bo) {
        return toAjax(pkulawApyService.insertByBo(bo));
    }

    /**
     * 修改案例库
     */
    @SaCheckPermission("system:apy:edit")
    @Log(title = "案例库", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PkulawApyBo bo) {
        return toAjax(pkulawApyService.updateByBo(bo));
    }

    /**
     * 删除案例库
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:apy:remove")
    @Log(title = "案例库", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(pkulawApyService.deleteWithValidByIds(List.of(ids), true));
    }
}
