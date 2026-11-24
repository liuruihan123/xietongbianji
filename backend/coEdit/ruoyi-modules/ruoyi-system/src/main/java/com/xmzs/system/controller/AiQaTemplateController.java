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
import com.xmzs.system.domain.vo.AiQaTemplateVo;
import com.xmzs.system.domain.bo.AiQaTemplateBo;
import com.xmzs.system.service.IAiQaTemplateService;
import com.xmzs.common.mybatis.core.page.TableDataInfo;

/**
 * 问答模版
 *
 * @author zuleed
 * @date 2024-12-19
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/qaTemplate")
public class AiQaTemplateController extends BaseController {

    private final IAiQaTemplateService aiQaTemplateService;

    /**
     * 查询问答模版列表
     */
    @SaCheckPermission("system:qaTemplate:list")
    @GetMapping("/list")
    public TableDataInfo<AiQaTemplateVo> list(AiQaTemplateBo bo, PageQuery pageQuery) {
        return aiQaTemplateService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出问答模版列表
     */
    @SaCheckPermission("system:qaTemplate:export")
    @Log(title = "问答模版", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AiQaTemplateBo bo, HttpServletResponse response) {
        List<AiQaTemplateVo> list = aiQaTemplateService.queryList(bo);
        ExcelUtil.exportExcel(list, "问答模版", AiQaTemplateVo.class, response);
    }

    /**
     * 获取问答模版详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:qaTemplate:query")
    @GetMapping("/{id}")
    public R<AiQaTemplateVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(aiQaTemplateService.queryById(id));
    }

    /**
     * 新增问答模版
     */
    @SaCheckPermission("system:qaTemplate:add")
    @Log(title = "问答模版", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AiQaTemplateBo bo) {
        return toAjax(aiQaTemplateService.insertByBo(bo));
    }

    /**
     * 修改问答模版
     */
    @SaCheckPermission("system:qaTemplate:edit")
    @Log(title = "问答模版", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AiQaTemplateBo bo) {
        return toAjax(aiQaTemplateService.updateByBo(bo));
    }

    /**
     * 删除问答模版
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:qaTemplate:remove")
    @Log(title = "问答模版", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(aiQaTemplateService.deleteWithValidByIds(List.of(ids), true));
    }
}
