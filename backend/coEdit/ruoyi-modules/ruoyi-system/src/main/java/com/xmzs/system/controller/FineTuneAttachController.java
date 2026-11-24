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
import com.xmzs.system.domain.vo.FineTuneAttachVo;
import com.xmzs.system.domain.bo.FineTuneAttachBo;
import com.xmzs.system.service.IFineTuneAttachService;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

/**
 * 微调附件
 *
 * @author zhuzi
 * @date 2024-07-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/tuneAttach")
public class FineTuneAttachController extends BaseController {

    private final IFineTuneAttachService fineTuneAttachService;

    /**
     * 查询微调附件列表
     */
    @SaCheckPermission("system:tuneAttach:list")
    @GetMapping("/list")
    public TableDataInfo<FineTuneAttachVo> list(FineTuneAttachBo bo, PageQuery pageQuery) {
        return fineTuneAttachService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出微调附件列表
     */
    @SaCheckPermission("system:tuneAttach:export")
    @Log(title = "微调附件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(FineTuneAttachBo bo, HttpServletResponse response) {
        List<FineTuneAttachVo> list = fineTuneAttachService.queryList(bo);
        ExcelUtil.exportExcel(list, "微调附件", FineTuneAttachVo.class, response);
    }

    /**
     * 获取微调附件详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:tuneAttach:query")
    @GetMapping("/{id}")
    public R<FineTuneAttachVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(fineTuneAttachService.queryById(id));
    }

    /**
     * 新增微调附件
     */
    @SaCheckPermission("system:tuneAttach:add")
    @Log(title = "微调附件", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody FineTuneAttachBo bo) {
        return toAjax(fineTuneAttachService.insertByBo(bo));
    }

    /**
     * 修改微调附件
     */
    @SaCheckPermission("system:tuneAttach:edit")
    @Log(title = "微调附件", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody FineTuneAttachBo bo) {
        return toAjax(fineTuneAttachService.updateByBo(bo));
    }

    /**
     * 删除微调附件
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:tuneAttach:remove")
    @Log(title = "微调附件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(fineTuneAttachService.deleteWithValidByIds(List.of(ids), true));
    }
}
