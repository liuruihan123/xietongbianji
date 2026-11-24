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
import com.xmzs.system.domain.vo.PkulawLawVo;
import com.xmzs.system.domain.bo.PkulawLawBo;
import com.xmzs.system.service.IPkulawLawService;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

/**
 * 法律条文
 *
 * @author zuleed
 * @date 2025-05-10
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/lawList")
public class PkulawLawController extends BaseController {

    private final IPkulawLawService pkulawLawService;

    /**
     * 查询法律条文列表
     */
    @SaCheckPermission("system:lawList:list")
    @GetMapping("/list")
    public TableDataInfo<PkulawLawVo> list(PkulawLawBo bo, PageQuery pageQuery) {
        return pkulawLawService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出法律条文列表
     */
    @SaCheckPermission("system:lawList:export")
    @Log(title = "法律条文", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PkulawLawBo bo, HttpServletResponse response) {
        List<PkulawLawVo> list = pkulawLawService.queryList(bo);
        ExcelUtil.exportExcel(list, "法律条文", PkulawLawVo.class, response);
    }

    /**
     * 获取法律条文详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:lawList:query")
    @GetMapping("/{id}")
    public R<PkulawLawVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(pkulawLawService.queryById(id));
    }

    /**
     * 新增法律条文
     */
    @SaCheckPermission("system:lawList:add")
    @Log(title = "法律条文", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PkulawLawBo bo) {
        return toAjax(pkulawLawService.insertByBo(bo));
    }

    /**
     * 修改法律条文
     */
    @SaCheckPermission("system:lawList:edit")
    @Log(title = "法律条文", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PkulawLawBo bo) {
        return toAjax(pkulawLawService.updateByBo(bo));
    }

    /**
     * 删除法律条文
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:lawList:remove")
    @Log(title = "法律条文", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(pkulawLawService.deleteWithValidByIds(List.of(ids), true));
    }
}
