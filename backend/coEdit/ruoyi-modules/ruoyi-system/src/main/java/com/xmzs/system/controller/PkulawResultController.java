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
import com.xmzs.system.domain.vo.PkulawResultVo;
import com.xmzs.system.domain.bo.PkulawResultBo;
import com.xmzs.system.service.IPkulawResultService;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

/**
 * 判案结果
 *
 * @author zuleed
 * @date 2025-05-10
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/lawresult")
public class PkulawResultController extends BaseController {

    private final IPkulawResultService pkulawResultService;

    /**
     * 查询判案结果列表
     */
    @SaCheckPermission("system:lawresult:list")
    @GetMapping("/list")
    public TableDataInfo<PkulawResultVo> list(PkulawResultBo bo, PageQuery pageQuery) {
        return pkulawResultService.queryPageList(bo, pageQuery);
    }


//
    /**
     * 新增判案结果
     */
    @PostMapping("/start")
    public R<Void> start() {
        return toAjax(pkulawResultService.handleLawTest());
    }

    /**
     * 导出判案结果列表
     */
    @SaCheckPermission("system:lawresult:export")
    @Log(title = "判案结果", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PkulawResultBo bo, HttpServletResponse response) {
        List<PkulawResultVo> list = pkulawResultService.queryList(bo);
        ExcelUtil.exportExcel(list, "判案结果", PkulawResultVo.class, response);
    }

    /**
     * 获取判案结果详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:lawresult:query")
    @GetMapping("/{id}")
    public R<PkulawResultVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(pkulawResultService.queryById(id));
    }

    /**
     * 新增判案结果
     */
    @SaCheckPermission("system:lawresult:add")
    @Log(title = "判案结果", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PkulawResultBo bo) {
        return toAjax(pkulawResultService.insertByBo(bo));
    }

    /**
     * 修改判案结果
     */
    @SaCheckPermission("system:lawresult:edit")
    @Log(title = "判案结果", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PkulawResultBo bo) {
        return toAjax(pkulawResultService.updateByBo(bo));
    }

    /**
     * 删除判案结果
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:lawresult:remove")
    @Log(title = "判案结果", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(pkulawResultService.deleteWithValidByIds(List.of(ids), true));
    }
}
